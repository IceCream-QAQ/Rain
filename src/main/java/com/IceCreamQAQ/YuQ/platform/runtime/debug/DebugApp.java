package com.IceCreamQAQ.YuQ.platform.runtime.debug;

import com.IceCreamQAQ.YuQ.App;

public class DebugApp extends App {

    public DebugApp() throws Exception {
        super(null, new DebugYuQ(), new DebugLogger());

        start();
    }

    public void platformLoad() {

    }
}
