package rain.api.loader

data class LoadItem(
    // 被加载类
    val clazz: Class<*>,
    /*** 触发加载的类
     * 一个类被加载，可能由于父类标记某注解/实现某接口标记的 LoadBy 触发。
     */
    val target: Class<*>,
    /*** 标记 LoadBy 注解的注解。
     * 若被加载类以某个标记了 LoadBy 注解的注解作为被扫描项作为加载项目的话，则为该注解。
     * 若直接标记 LoadBy 被加载则此项为空。
     */
    val annotation: Annotation?,
    val loadByAnnotation: Boolean = annotation != null
)