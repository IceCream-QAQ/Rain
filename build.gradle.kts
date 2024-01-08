plugins {
    java
    kotlin("jvm") version "1.9.10"
    `java-library`
    `maven-publish`
}

val dependencyAsm by extra("9.3")
val dependencyFastJSON2 by extra("2.0.23")
val dependencySlf4j by extra("2.0.9")

group = "com.IceCreamQAQ.Rain"
version = "0.5.0"

subprojects {
    apply {
        plugin("java")
        plugin("java-library")
        plugin("maven-publish")
    }

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.icecreamqaq.com/repository/maven-public/")
    }

    dependencies {
        api(kotlin("stdlib"))
        api(kotlin("reflect"))
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    }
}