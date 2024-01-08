plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":function"))
    api("javax.inject:javax.inject:1")
    testImplementation(project(":test-base"))
}