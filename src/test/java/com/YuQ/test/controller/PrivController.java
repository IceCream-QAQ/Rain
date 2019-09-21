package com.YuQ.test.controller;

import com.IceCreamQAQ.YuQ.annotation.Action;
import com.IceCreamQAQ.YuQ.annotation.PrivateController;

@PrivateController
public class PrivController {

    @Action("菜单")
    public String menu(){
        return "Priv OK!";
    }

}
