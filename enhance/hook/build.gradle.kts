plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":classloader"))
    testImplementation(project(":test-base"))
}