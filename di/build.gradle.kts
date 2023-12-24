plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":function"))
    api(project(":api"))
    api("org.yaml:snakeyaml:2.0")
    testImplementation(project(":test-base"))
}