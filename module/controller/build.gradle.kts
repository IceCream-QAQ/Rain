plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":api"))
    compileOnly(project(":classloader"))
    testImplementation(project(":test-base"))
}