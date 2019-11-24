package com.IceCreamQAQ.YuQ.entity;

import lombok.Getter;
import lombok.ToString;
import lombok.val;
import org.meowy.cqp.jcq.entity.Anonymous;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class Message extends YuQThrowable{
    //来自 Runtime 的 Message 原文。
    private String msg;
    //经过 YuQ 转译过，方便的可供识别的内容。
    private String[] texts;

    protected Message(){

    }
}
