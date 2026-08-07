package test.rain;

import org.junit.jupiter.api.Test;
import rain.classloader.AppClassloader;
import rain.classloader.IRainClassLoader;
import rain.classloader.transformer.ClassTransformer;

import javax.inject.Named;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class MethodParaNamedTest {

    @Test
    public void testMethodParaNamed() throws ClassNotFoundException {
        ClassLoader classLoader = MakeClassLoader.INSTANCE.classloader();
        Class<?> clazz = classLoader.loadClass("test.rain.MethodParaNamedClass");
        Arrays.stream(clazz.getMethods()).filter(it -> it.getName().startsWith("method")).forEach(method -> {
            System.out.println("Method: " + method.getName());
            Arrays.stream(method.getParameters()).forEach(parameter -> {
                Named namedAnnotation = parameter.getAnnotation(Named.class);
                if (namedAnnotation == null) throw new RuntimeException("Parameter " + parameter.getName() + " of method " + method.getName() + " is not annotated with @Named");
                System.out.println("Parameter: " + parameter.getName() + ", Named: " + parameter.getAnnotation(Named.class));
            });
        });
    }

}
