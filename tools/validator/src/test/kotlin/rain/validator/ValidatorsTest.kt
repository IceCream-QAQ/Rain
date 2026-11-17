package rain.validator

import rain.classloader.SpawnClassLoader
import rain.validator.obj.FieldReader
import rain.validator.validators.Min

class GeneratedValidationValues {
    @field:Min(10, canEqual = true, message = "too small")
    val value = 9
}

class PrivateValidationValues {
    @field:Min(10, canEqual = true, message = "too small")
    private val value = 9
}

fun main(){
    val clazz = GeneratedValidationValues::class.java
    val field = clazz.getDeclaredField("value")
    val classloader = SpawnClassLoader(Thread.currentThread().contextClassLoader)

    val reader = FieldReader.fieldReaderOf(clazz, field, classloader)
    println(reader.read(GeneratedValidationValues()))
    println(reader)
}