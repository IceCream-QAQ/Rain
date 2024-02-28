plugins {
    kotlin("jvm")
}

dependencies {
    api("ch.qos.logback:logback-classic:1.3.14")
    api(project(":rain-test"))
}