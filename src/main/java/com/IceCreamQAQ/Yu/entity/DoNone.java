package com.IceCreamQAQ.Yu.entity;

public class DoNone extends Result {

    private String message;

    public DoNone() {

    }

    public DoNone(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
