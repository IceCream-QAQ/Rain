package com.IceCreamQAQ.Yu.entity;

public class DoNone extends RuntimeException {

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
