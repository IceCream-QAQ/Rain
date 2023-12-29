package rain.classloader.enchant

import kotlin.reflect.KClass

annotation class EnchantBy(
    val value: KClass<out Enchanter>
)