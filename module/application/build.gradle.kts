plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":di"))
    api(project(":classloader"))
    api(project(":hook"))
    testImplementation(project(":test-base"))
}