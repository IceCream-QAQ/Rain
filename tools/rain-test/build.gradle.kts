plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":application"))
    implementation("org.junit.platform:junit-platform-engine:1.10.1")
    implementation("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    implementation("org.junit.platform:junit-platform-launcher:1.10.2")

    testImplementation("ch.qos.logback:logback-classic:1.3.14")
}