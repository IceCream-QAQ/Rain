plugins {
    kotlin("jvm")
}

dependencies {
    api("ch.qos.logback:logback-classic:1.3.15")
    api(project(":rain-test"))
}