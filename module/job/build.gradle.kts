plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":function"))
    api(project(":api"))
    testImplementation(project(":test-base"))
}