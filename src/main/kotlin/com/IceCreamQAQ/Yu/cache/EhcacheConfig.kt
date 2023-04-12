package com.IceCreamQAQ.Yu.cache

data class EhcacheConfig(
    var size: Long = 10000,
    var ttl: Long = -1,
    var tti: Long = -1
)