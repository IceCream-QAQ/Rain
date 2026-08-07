package test.rain;

import rain.classloader.enchant.MethodParaNamedEnchanter;

import javax.inject.Named;

@MethodParaNamedEnchanter.MethodParaNamed
public class MethodParaNamedClass {

    public void method1(String name, int age) {
        System.out.println("method1: name=" + name + ", age=" + age);
    }

    public void method2(@Named("name!") String name, int age) {
        System.out.println("method2: name=" + name + ", age=" + age);
    }

    public void method3(@Named("name!") String name, @Named("age!") int age) {
        System.out.println("method2: name=" + name + ", age=" + age);
    }

}
