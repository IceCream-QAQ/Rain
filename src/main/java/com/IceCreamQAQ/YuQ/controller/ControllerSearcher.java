package com.IceCreamQAQ.YuQ.controller;

import com.IceCreamQAQ.YuQ.DevModeLoader;
import com.IceCreamQAQ.YuQ.ReloadAble;
import com.IceCreamQAQ.YuQ.annotation.*;
import com.IceCreamQAQ.YuQ.inject.YuQInject;
import com.IceCreamQAQ.YuQ.route.Router;
import com.sobte.cqp.jcq.entity.CoolQ;
import lombok.val;
import lombok.var;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ControllerSearcher {

    @Config("project.package.controller")
    public String packageName;

    @Config("project.location")
    private String projectLocation;

    @Inject
    private YuQInject inject;

    @Inject
    private ReloadAble reloadAble;

    @Inject
    private CoolQ cq;

    private ClassLoader classLoader;

    private Router groupRootRouter;
    private Router privateRootRouter;

    public void search() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        val classes = getClasses(packageName);

        cq.logInfo("YuQ Loader", "共有 " + classes.size() + " 个 Controller 被扫描。");
        //val yuqc=classLoader.loadClass("com.icecreamqaq.qq.controller.group.SettingController");


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

//        val pathAnnotation =
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


    public Router[] makeRouter() throws IllegalAccessException, InstantiationException, ClassNotFoundException, IOException {
        groupRootRouter = new Router(0);
        privateRootRouter = new Router(0);

        ClassLoader classLoader;
        if (!projectLocation.equals("") && reloadAble != null) {
            cq.logInfo("YuQFramework Loader", "Dev Mode Load.");
            val location = new File(projectLocation);
            val url = location.toURI().toURL();
            classLoader = new DevModeLoader(new URL[]{url}, this.getClass().getClassLoader());


            val listenerAdaptor = new ListenerAdaptor();
            inject.injectObject(listenerAdaptor);

            long interval = TimeUnit.SECONDS.toMillis(2);
            IOFileFilter directories = FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), HiddenFileFilter.VISIBLE);
            IOFileFilter files = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), FileFilterUtils.suffixFileFilter(".txt"));
            IOFileFilter filter = FileFilterUtils.or(directories, files);
            FileAlterationObserver observer = new FileAlterationObserver(location, filter);
            observer.addListener(listenerAdaptor);
            FileAlterationMonitor fileAlterationMonitor = new FileAlterationMonitor(interval, observer);

            try {
                listenerAdaptor.f = fileAlterationMonitor;
                fileAlterationMonitor.start();
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            cq.logInfo("YuQFramework Loader", "Pro Mode Load.");
            classLoader = this.getClass().getClassLoader();
        }
        this.classLoader = classLoader;

        search();

        return new Router[]{privateRootRouter, groupRootRouter};
    }


    public List<Class<?>> getClasses(String packageName) throws MalformedURLException {
        val projectLocationUrlString = new File(projectLocation).toURI().toURL().toString();
        List<Class<?>> classes = new ArrayList<>();
        boolean recursive = true;
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = classLoader.getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                if (!projectLocation.equals(""))
                    if (!(url.toString().substring(0, projectLocationUrlString.length()).equals(projectLocationUrlString)))
                        continue;

                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    JarFile jar;
                    try {
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.charAt(0) == '/') {
                                name = name.substring(1);
                            }
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                if (idx != -1) {
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                if ((idx != -1) || recursive) {
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<Class<?>> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirfiles = dir.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    if (projectLocation.equals("")) {
                        classes.add(Class.forName(packageName + '.' + className));
                    } else {
                        val clazz = ((DevModeLoader) classLoader).findClass(packageName + '.' + className);
                        if (clazz.getClassLoader().equals(classLoader)) classes.add(clazz);
                    }

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class ListenerAdaptor extends FileAlterationListenerAdaptor {

        private FileAlterationMonitor f;

        @Inject
        private ReloadAble reloadAble;

        @Inject
        private CoolQ cq;

        @Override
        public void onDirectoryCreate(File directory) {
            reload();
        }

        @Override
        public void onDirectoryChange(File directory) {
            reload();
        }

        @Override
        public void onDirectoryDelete(File directory) {
            reload();
        }

        @Override
        public void onFileCreate(File file) {
            reload();
        }

        @Override
        public void onFileChange(File file) {
            reload();
        }

        @Override
        public void onFileDelete(File file) {
            reload();
        }

        public void reload() {
            try {
                f.stop();
                cq.logError("YuQ Framework", "准备重载。");
                reloadAble.reload();
            } catch (Exception ignored) {
            }
        }
    }
}
