package com.IceCreamQAQ.Yu.validation

import java.lang.RuntimeException

class ValidateFailException(
        val className:String,
        val methodName:String,
        val paraName:String,
        val result: ValidateResult
):RuntimeException(){

    override val message: String
        get() = "方法 $className.$methodName 的参数 $paraName 验证失败！${result.fullMessage}"

}