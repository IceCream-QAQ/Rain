plugins {
    kotlin("jvm")
}

val dependencyAsm: String by project

dependencies {
    api(project(":hook"))
    implementation("org.ow2.asm:asm-commons:$dependencyAsm")
    api("javax.inject:javax.inject:1")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
}

tasks.test {
    useJUnitPlatform {
        includeEngines("junit-jupiter")
    }
}
