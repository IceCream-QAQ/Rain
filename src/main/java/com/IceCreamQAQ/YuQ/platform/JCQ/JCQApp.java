package com.IceCreamQAQ.YuQ.platform.JCQ;

import com.IceCreamQAQ.YuQ.App;
import com.IceCreamQAQ.YuQ.loader.ReloadAble;
import org.meowy.cqp.jcq.entity.CoolQ;

public class JCQApp extends App {



    public JCQApp(ReloadAble reloadAble, CoolQ cq,ClassLoader appClassloader) throws Exception {
        super(reloadAble, new JCYuQ(cq),new JcqLogger(cq),appClassloader);

        inject.putInjectObj(CoolQ.class.getName(),"",cq);

        start();
    }
}
