package com.IceCreamQAQ.YuQ.platform.runtime.JCQ;

import com.IceCreamQAQ.YuQ.App;
import com.IceCreamQAQ.YuQ.loader.ReloadAble;
import com.IceCreamQAQ.YuQ.platform.software.QQ.QQApp;
import lombok.val;
import org.meowy.cqp.jcq.entity.CoolQ;

import java.util.HashMap;
import java.util.Map;

public class JCQApp extends QQApp {


    public JCQApp(ReloadAble reloadAble, CoolQ cq,ClassLoader appClassloader) throws Exception {
        super(reloadAble, new JCYuQ(cq),new JcqLogger(cq),appClassloader,new HashMap<String,Object>(){{ put("cq",cq); }});
    }

    @Override
    public void platformLoad(Map<String, Object> paras) {

        val cq=paras.get("cq");
        inject.putInjectObj(CoolQ.class.getName(),"",cq);

    }
}
