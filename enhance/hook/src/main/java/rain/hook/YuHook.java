package rain.hook;

import rain.classloader.IRainClassLoader;

public class YuHook {

    private static IHook hookInstance;

    static {
        if (!(YuHook.class.getClassLoader() instanceof IRainClassLoader)) hookInstance = new UnsupportedClassLoaderHook();
    }

    public static IHook findHook(){
        return hookInstance;
    }

}
