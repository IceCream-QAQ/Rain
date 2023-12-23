plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":function"))
    api("org.yaml:snakeyaml:2.0")
    api("javax.inject:javax.inject:1")
    testImplementation(project(":test-base"))
}