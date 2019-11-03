package com.YuQ.test.controller;

import com.IceCreamQAQ.YuQ.annotation.*;

@GroupController
public class TestGroupController {

    @Before
    public void before(Long qq, @Inject(name = "group") Long group) {
        System.out.println("这里是Before！");
        System.out.println("qq: " + qq);
        System.out.println("group: " + group);
    }

    @Action(value = "菜单", intercept = true)
    public String menu(@PathVar(value = 1,type = PathVar.Type.flag)Boolean flag) {
        System.out.println("这里是Action！");
        System.out.println("flag: "+flag);
//        throw new RuntimeException();
        return "Group OK!";
    }

}
