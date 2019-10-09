package com.YuQ.test.controller;

import com.IceCreamQAQ.YuQ.annotation.Action;
import com.IceCreamQAQ.YuQ.annotation.Before;
import com.IceCreamQAQ.YuQ.annotation.GroupController;
import com.IceCreamQAQ.YuQ.annotation.Inject;

@GroupController
public class TestController {

    @Before
    public void before(Long qq,@Inject(name = "group") Long group){

    }

    @Action(value = "菜单",intercept = true)
    public String menu(){
//        throw new RuntimeException();
        return "Group OK!";
    }

}
