package com.IceCreamQAQ.Yu;

import lombok.val;

public class Td {

    public static void fun(Object... sss){
        System.out.println(sss.length);
    }

    public static void main(String[] args) {
        val objs = new Object[]{"1","2","3"};
        fun(objs);
    }

}
