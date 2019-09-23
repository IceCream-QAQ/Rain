package com.IceCreamQAQ.YuQ.platform.JCQ;

import com.IceCreamQAQ.YuQ.App;
import com.IceCreamQAQ.YuQ.loader.ReloadAble;
import com.sobte.cqp.jcq.entity.CoolQ;

public class JCQApp extends App {



    public JCQApp(ReloadAble reloadAble, CoolQ cq) throws Exception {
        super(reloadAble, new JCQOperater(cq));

        inject.putInjectObj(CoolQ.class.getName(),"",cq);

        start();
    }
}
