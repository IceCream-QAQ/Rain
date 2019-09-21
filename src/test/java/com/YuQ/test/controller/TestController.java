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

    @Action("菜单")
    public String menu(){
//        throw new RuntimeException();
        return "Group OK!";
    }

}
