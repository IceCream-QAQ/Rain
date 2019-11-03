package com.IceCreamQAQ.YuQ.loader;


import com.IceCreamQAQ.YuQ.AppLogger;
import com.IceCreamQAQ.YuQ.annotation.*;
import com.IceCreamQAQ.YuQ.inject.YuQInject;
import lombok.val;
import lombok.var;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AppLoader {

    @Config("project.package")
    private String projectPackage;
    @Config("project.location")
    private String projectLocation;

    @Inject(name = "appClassLoader")
    private ClassLoader classLoader;
    @Inject
    private YuQInject inject;
    @Inject
    private AppLogger logger;
    @Inject
    private ReloadAble reloadAble;

    public void load() {
        try {
            ClassLoader classLoader;
            if (!projectLocation.equals("") && reloadAble != null) {
                logger.logInfo("YuQ Loader", "Dev Mode Load.");
                val location = new File(projectLocation);
                val url = location.toURI().toURL();
                classLoader = new DevModeLoader(new URL[]{url}, this.classLoader);


                val listenerAdaptor = inject.spawnInstance(ListenerAdaptor.class);

                long interval = TimeUnit.SECONDS.toMillis(5);
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
                logger.logInfo("YuQ Loader", "Pro Mode Load.");
                classLoader = this.classLoader;
            }
            this.classLoader = classLoader;


            inject.putInjectObj(ClassLoader.class.getName(), "", classLoader);
//            val invokerClassLoader=new InvokerClassLoader(this.classLoader);
//            inject.putInjectObj(InvokerClassLoader.class.getName(),"",invokerClassLoader);

            val classes = getClasses(projectPackage);

            val loadItemsMap = new HashMap<Class<? extends Loader>,List<LoadItem>>();

//            val controllers = new ArrayList<Class>();
//
//            val eventListeners = new ArrayList<Class>();
//
//            for (Class<?> clazz : classes.values()) {
//
//                val groupController = clazz.getAnnotation(GroupController.class);
//                if (groupController != null) controllers.add(clazz);
//
//                val privController = clazz.getAnnotation(PrivateController.class);
//                if (privController != null) controllers.add(clazz);
//
//                val eventHandler = clazz.getAnnotation(EventHandler.class);
//                if (eventHandler != null) eventListeners.add(clazz);
//
//                val eventListener = clazz.getAnnotation(com.IceCreamQAQ.YuQ.annotation.EventListener.class);
//                if (eventListener != null) eventListeners.add(clazz);
//
//            }

            for (val clazz : classes.values()) {
                val annotationInstances = clazz.getAnnotations();

                val instance = inject.spawnAndPut(clazz);

                for (val annotationInstance : annotationInstances) {
                    val annotationClass = annotationInstance.getClass().getInterfaces()[0];
                    val loadBy = annotationClass.getAnnotation(LoadBy.class);
                    if (loadBy != null){
                        val loader = loadBy.value();
                        var loadItems = loadItemsMap.get(loader);
                        if (loadItems==null){
                            loadItems = new ArrayList<LoadItem>();
                            loadItemsMap.put(loader,loadItems);
                        }
                        val loadItem = new LoadItem();
                        loadItem.setAnnotation(annotationInstance);
                        loadItem.setType(clazz);
                        loadItem.setInstance(instance);
                        loadItems.add(loadItem);
                    }
                }
            }

            for (val loader : loadItemsMap.keySet()) {
                val loaderInstance = inject.spawnInstance(loader);
                loaderInstance.load(loadItemsMap.get(loader));
            }

//            val controllerLoader = inject.spawnInstance(ControllerLoader.class);
//            controllerLoader.load_old(controllers);
//
//            val eventHandlerLoader = inject.spawnInstance(EventListenerLoader.class);
//            eventHandlerLoader.load(eventListeners);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Map<String, Class> getClasses(String packageName) throws MalformedURLException {
        val classes = new HashMap<String, Class>();

        val projectLocationUrlString = new File(projectLocation).toURI().toURL().toString();
        //List<Class<?>> classes = new ArrayList<>();
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
                                            val clazz = Class.forName(packageName + '.' + className, true, classLoader);
                                            classes.putIfAbsent(clazz.getName(), clazz);
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

    public void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Map<String, Class> classes) {
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
                        val clazz = Class.forName(packageName + '.' + className);
                        classes.putIfAbsent(clazz.getName(), clazz);
                    } else {
                        val name = packageName + '.' + className;
                        if (classes.get(name) == null) {
                            val clazz = ((DevModeLoader) classLoader).findClass(name);
                            if (clazz.getClassLoader().equals(classLoader))
                                classes.put(clazz.getName(), clazz);
                        }
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
        private AppLogger logger;

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
                logger.logError("YuQ Framework", "准备重载。");
                reloadAble.reload();
            } catch (Exception ignored) {
            }
        }
    }

}
