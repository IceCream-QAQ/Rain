plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":api"))
    testImplementation(project(":test-base"))
}