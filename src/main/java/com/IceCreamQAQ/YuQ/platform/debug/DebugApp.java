package com.IceCreamQAQ.YuQ.platform.debug;

import com.IceCreamQAQ.YuQ.App;
import com.IceCreamQAQ.YuQ.AppLogger;
import com.IceCreamQAQ.YuQ.YuQ;
import com.IceCreamQAQ.YuQ.loader.ReloadAble;

public class DebugApp extends App {

    public DebugApp() throws Exception {
        super(null, new DebugYuQ(), new DebugLogger());

        start();
    }
}
