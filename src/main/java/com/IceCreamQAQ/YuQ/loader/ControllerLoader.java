package com.IceCreamQAQ.YuQ.loader;


import com.IceCreamQAQ.YuQ.AppLogger;
import com.IceCreamQAQ.YuQ.annotation.*;
import com.IceCreamQAQ.YuQ.controller.*;
import com.IceCreamQAQ.YuQ.inject.YuQInject;
import com.IceCreamQAQ.YuQ.controller.route.RouteInvoker;
import com.IceCreamQAQ.YuQ.controller.route.Router;
import lombok.val;
import lombok.var;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ControllerLoader implements Loader {

    @Config("project.package.controller")
    public String packageName;

    @Config("project.location")
    private String projectLocation;

    @Inject
    private AppLogger logger;

    @Inject
    private ClassLoader classLoader;

    private Router groupRootRouter;
    private Router privateRootRouter;



//    public void load_old(List<Class> classes) throws Exception {
//
//        for (val clazz : classes) {
//            val group = clazz.getAnnotation(GroupController.class);
//            if (group != null) {
//                logger.logInfo("YuQ Loader", "Group Controller " + clazz.getName() + " 正在载入。");
//                controllerToRouter(clazz, groupRootRouter);
//                logger.logInfo("YuQ Loader", "Group Controller " + clazz.getName() + " 载入完成。");
//            }
//
//            val priv = clazz.getAnnotation(PrivateController.class);
//            if (priv != null) {
//                logger.logInfo("YuQ Loader", "Private Controller " + clazz.getName() + " 正在载入。");
//                controllerToRouter(clazz, privateRootRouter);
//                logger.logInfo("YuQ Loader", "Private Controller " + clazz.getName() + " 载入完成。");
//            }
//        }
//
//        inject.putInjectObj(RouteInvoker.class.getName(), "group", groupRootRouter);
//        inject.putInjectObj(RouteInvoker.class.getName(), "priv", privateRootRouter);
//    }

//    public void controllerToRouter(Class controller, Router rootRouter) throws IllegalAccessException, InstantiationException, IOException, NoSuchMethodException, InvocationTargetException {
//        val instance = inject.spawnAndPut(controller, null);
//        controllerToRouter(instance, rootRouter);
//    }

    public void controllerToRouter(Object instance, Router rootRouter) throws Exception {
        val controller = instance.getClass();

        val fileName = controller.getName().replace(".", "/") + ".class";
        val in = controller.getClassLoader().getResourceAsStream(fileName);
        val cr = new ClassReader(in);
        val node = new ClassNode();
        cr.accept(node, 0);

        val methodMap = new HashMap<String, MethodNode>();

        val cvMethods = (List<MethodNode>) node.methods;


        for (val method : cvMethods) {
            methodMap.put(method.name, method);
        }

        val paths = controller.getAnnotationsByType(Path.class);
        Router controllerRouter;
        if (paths.length == 0)
            controllerRouter = rootRouter;
        else if (paths.length == 1) {
            val pathString = paths[0].value();
            controllerRouter = getRouterByPathString(rootRouter, pathString, 0);
        } else {
            controllerRouter = rootRouter;
            for (Path path : paths) {
                controllerRouter = getRouter(controllerRouter, path.value());
            }
        }

//        val controllerInvoker = inject.spawnInstance(ControllerInvoker.class);
        val methods = controller.getMethods();

        val befores = new ArrayList<MethodInvoker>();
//        val actions = new ConcurrentHashMap<String, ActionInvoker>();
        for (val method : methods) {
            val before = method.getAnnotation(Before.class);
            if (before != null) {
                logger.logInfo("YuQ Loader", "Before " + method.getName() + " 正在载入。");

//                val beforeInvoker = new ReflectMethodInvoker(instance, method, methodMap.get(method.getName()));
                val beforeInvoker = createMethodInvoker(instance, method, methodMap.get(method.getName()));
                befores.add(beforeInvoker);
            }
        }

        val before = befores.toArray(new MethodInvoker[befores.size()]);;

        for (Method method : methods) {
            val action = method.getAnnotation(Action.class);
            if (action != null) {
                logger.logInfo("YuQ Loader", "Action " + method.getName() + " 正在载入。");

                var path = action.value();

                Router actionRootRouter;
                if (path.contains("/")) {
                    if ("/".equals(path.substring(0, 1))) {
                        path = path.substring(1);
                        actionRootRouter = rootRouter;
                    } else {
                        actionRootRouter = controllerRouter;
                    }
                    actionRootRouter = getRouterByPathString(actionRootRouter, path, 1);
                } else {
                    actionRootRouter = controllerRouter;
                }

//                val methodInvoker = new ReflectMethodInvoker(instance, method, methodMap.get(method.getName()));
                val methodInvoker = createMethodInvoker(instance, method, methodMap.get(method.getName()));

                val actionInvoker = createActionInvoker(method);
                actionInvoker.setInvoker(methodInvoker);
                actionInvoker.setBefores(before);


                actionRootRouter.getRouters().put(path, actionInvoker);
//                actions.put(path, actionInvoker);
            }
        }

        logger.logInfo("YuQ Loader", "共有 " + befores.size() + " 个 Before 被载入。");
//        logger.logInfo("YuQ Loader", "共有 " + actions.size() + " 个 Action 被载入。");

//        controllerInvoker.befores = befores.toArray(new MethodInvoker[befores.size()]);
//        controllerInvoker.actions = actions;
    }

    protected abstract MethodInvoker createMethodInvoker(Object object, Method method, MethodNode methodNode) throws Exception;
    protected abstract ActionInvoker createActionInvoker(Method method);

    private Router getRouter(Router router, String name) {
        var nextRouter = router.getRouters().get(name);
        if (!(nextRouter instanceof Router)) {
            var level = router.getLevel() + 1;
            nextRouter = new Router(level);
            router.getRouters().put(name, nextRouter);
        }
        return (Router) nextRouter;
    }

    private Router getRouterByPathString(Router router, String pathString, Integer lessLevel) {
//        if (pathString.substring(0, 1).equals("/")) router = groupRootRouter;

        val paths = pathString.split("/");
        var finishRouter = router;

        val length = paths.length - lessLevel;
        for (int i = 0; i < length; i++) {
            val path = paths[i];
            finishRouter = getRouter(finishRouter, path);
        }
        return finishRouter;
    }


}
