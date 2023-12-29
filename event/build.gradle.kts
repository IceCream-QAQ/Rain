plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":api"))
    api(project(":classloader"))
    testImplementation(project(":test-base"))
}