plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":function"))
    testImplementation(project(":test-base"))
}