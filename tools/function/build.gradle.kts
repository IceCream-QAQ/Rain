plugins {
    kotlin("jvm")
}

val dependencyAsm: String by project
val dependencyFastJSON2: String by project
val dependencySlf4j: String by project

dependencies {
    api("org.ow2.asm:asm-commons:$dependencyAsm")
    api("com.alibaba.fastjson2:fastjson2:$dependencyFastJSON2")
    api("org.slf4j:slf4j-api:$dependencySlf4j")
}