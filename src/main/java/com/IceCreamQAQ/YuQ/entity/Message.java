package com.IceCreamQAQ.YuQ.entity;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Message extends Result {
    //来自 Runtime 的 Message 原文。
    private String msg;
    //经过 YuQ 转译过，方便的可供识别的内容。
    private String[] texts;

    protected Message(){

    }
}
