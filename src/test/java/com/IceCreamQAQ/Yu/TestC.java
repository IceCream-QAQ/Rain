package com.IceCreamQAQ.Yu;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class TestC {

    public void o(String a, @NotNull String b){

        new Thread(() -> {}).start();

        render(a,b);

    }

    void render(String... args){

    }

}
