plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":function"))
    api(project(":classloader"))
    testImplementation(project(":test-base"))
}