package com.icecream.test;

public class TestJava extends TestA {

    {
        String a = "";
        System.out.println(a);
    }

    public TestJava(String f) {
        super(f);
        f.equals("");
    }

    public TestJava(String f, String c) {
        this(f);
        c.equals("f");
    }

}
