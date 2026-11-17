package rain.validator

class ValidateFailException(
    val methodFullName: String,
    val paraName: String,
    val result: ValidateResult,
) : RuntimeException("方法 $methodFullName 的参数 $paraName 验证失败！${result.fullMessage}", null, false, false) {

    constructor(
        className: String,
        methodName: String,
        paraName: String,
        result: ValidateResult,
    ) : this("$className.$methodName", paraName, result)

    override val message: String
        get() = "方法 $methodFullName 的参数 $paraName 验证失败！${result.fullMessage}"

}
