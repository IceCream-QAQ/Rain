plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":test-base"))
    implementation(project(":application"))
    implementation(project(":event"))
    implementation(project(":job"))
}