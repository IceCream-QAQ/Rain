package com.IceCreamQAQ.Yu.validation

object JavaUtil {

    @JvmStatic
    fun getAnnotation(clazz: Class<*>, field: String, annotation: Class<out Annotation>) = clazz.getDeclaredField(field).getAnnotation(annotation)

    @JvmStatic
    fun getValidatorData(factory: ValidatorFactory, clazz: Class<*>, field: String, annotation: Class<out Annotation>): ValidateData {
        val ann = getAnnotation(clazz, field, annotation)
        val vb = ann::class.java.interfaces[0].getAnnotation(ValidateBy::class.java)
        return ValidateData(ann,factory[vb.value])
    }

}
