package com.IceCreamQAQ.YuQ.loader;

import com.IceCreamQAQ.YuQ.YuQLogger;
import com.IceCreamQAQ.YuQ.controller.ActionInvoker;
import com.IceCreamQAQ.YuQ.controller.ControllerInvoker;
import com.IceCreamQAQ.YuQ.controller.MethodInvoker;
import com.IceCreamQAQ.YuQ.annotation.*;
import com.IceCreamQAQ.YuQ.inject.YuQInject;
import com.IceCreamQAQ.YuQ.controller.route.RouteInvoker;
import com.IceCreamQAQ.YuQ.controller.route.Router;
import lombok.val;
import lombok.var;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ControllerLoader {

    @Config("project.package.controller")
    public String packageName;

    @Config("project.location")
    private String projectLocation;

    @Inject
    private YuQInject inject;

    @Inject
    private ReloadAble reloadAble;

    @Inject
    private YuQLogger cq;

    @Inject
    private ClassLoader classLoader;

    private Router groupRootRouter;
    private Router privateRootRouter;

    public void load(List<Class> classes) throws Exception {
        groupRootRouter = new Router(0);
        privateRootRouter = new Router(0);

        for (val clazz : classes) {
            val group = clazz.getAnnotation(GroupController.class);
            if (group != null) {
                cq.logInfo("YuQ Loader", "Group Controller " + clazz.getName() + " 正在载入。");
                controllerToRouter(clazz, groupRootRouter);
                cq.logInfo("YuQ Loader", "Group Controller " + clazz.getName() + " 载入完成。");
            }

            val priv = clazz.getAnnotation(PrivateController.class);
            if (priv != null) {
                cq.logInfo("YuQ Loader", "Private Controller " + clazz.getName() + " 正在载入。");
                controllerToRouter(clazz, privateRootRouter);
                cq.logInfo("YuQ Loader", "Private Controller " + clazz.getName() + " 载入完成。");
            }
        }

        inject.putInjectObj(RouteInvoker.class.getName(),"group",groupRootRouter);
        inject.putInjectObj(RouteInvoker.class.getName(),"priv",privateRootRouter);
    }

    public void controllerToRouter(Class controller, Router rootRouter) throws IllegalAccessException, ClassNotFoundException, InstantiationException, IOException {
        val instance = inject.spawnAndPut(controller, null);

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

        val paths = (Path[]) controller.getAnnotationsByType(Path.class);
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

        val controllerInvoker = inject.spawnInstance(ControllerInvoker.class);
        val methods = controller.getMethods();

        val befores = new ArrayList<MethodInvoker>();
        val actions = new ConcurrentHashMap<String, ActionInvoker>();
        for (val method : methods) {
            val before = method.getAnnotation(Before.class);
            if (before != null) {
                cq.logInfo("YuQ Loader", "Before " + method.getName() + " 正在载入。");

                val beforeInvoker = new MethodInvoker(instance, method, methodMap.get(method.getName()));
                befores.add(beforeInvoker);
                continue;
            }

            val action = method.getAnnotation(Action.class);
            if (action != null) {
                cq.logInfo("YuQ Loader", "Action " + method.getName() + " 正在载入。");

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


                val at = action.at();
                val re = action.re();
                val level = action.level();
                val intercept = action.intercept();

                val methodInvoker = new MethodInvoker(instance, method, methodMap.get(method.getName()));

                val actionInvoker = new ActionInvoker();
                actionInvoker.setInvoker(methodInvoker);
                actionInvoker.setAt(at);
                actionInvoker.setRe(re);
                actionInvoker.setIntercept(intercept);


                actionRootRouter.getRouters().put(path, controllerInvoker);
                actions.put(path, actionInvoker);
            }
        }

        cq.logInfo("YuQ Loader", "共有 " + befores.size() + " 个 Before 被载入。");
        cq.logInfo("YuQ Loader", "共有 " + actions.size() + " 个 Action 被载入。");

        controllerInvoker.befores = befores.toArray(new MethodInvoker[befores.size()]);
        controllerInvoker.actions = actions;
    }

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
