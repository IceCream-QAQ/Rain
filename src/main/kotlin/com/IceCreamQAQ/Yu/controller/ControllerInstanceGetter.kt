package com.IceCreamQAQ.Yu.controller

fun interface ControllerInstanceGetter {
    operator fun invoke(): Any
}