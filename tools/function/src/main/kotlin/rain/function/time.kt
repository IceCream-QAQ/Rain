package rain.function

val currentTimeMillis: Long
    inline get() = System.currentTimeMillis()

val currentTimeSeconds: Int
    inline get() = (currentTimeMillis / 1000).toInt()