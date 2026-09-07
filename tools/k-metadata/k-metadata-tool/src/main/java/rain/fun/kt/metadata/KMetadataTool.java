package rain.fun.kt.metadata;

public class KMetadataTool {

    private static KHelper instance;
    public static KHelper instance() throws ClassNotFoundException {
        if (instance == null){
            Class<?> clazz = Class.forName("kotlin.reflect.jvm.internal.impl.km.ClassKind");
            try{
                clazz.getMethod("getFlags$kotlin_metadata");
                instance = new rain.fun.kt.metadata.v2_0_0.KHelperImpl();
            } catch (NoSuchMethodException e) {
                instance = new rain.fun.kt.metadata.v2_4_0.KHelperImpl();
            }
        }
        return instance;
    }

}
