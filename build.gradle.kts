plugins {
    java
    kotlin("jvm") version "2.0.10"
    `java-library`
    `maven-publish`
}

val dependencyAsm by extra("9.3")
val dependencyFastJSON2 by extra("2.0.46")
val dependencySlf4j by extra("2.0.9")

group = "com.IceCreamQAQ.Rain"
version = "1.0.0-DEV1"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.icecreamqaq.com/repository/maven-public/")
}

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

    java {
        withSourcesJar()
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>(name) {
                groupId = "com.IceCreamQAQ.Rain"
                artifactId = name
                version = "1.0.0-DEV1"

                pom {
                    name.set("Rain Java Dev Framework")
                    description.set("Rain Java Dev Framework")
                    url.set("https://github.com/IceCream-Open/Rain")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("IceCream")
                            name.set("IceCream")
                            email.set("www@withdata.net")
                        }
                    }
                    scm {
                        connection.set("")
                    }
                }
                from(components["java"])
            }

            repositories {
                mavenLocal()
                maven {
                    val snapshotsRepoUrl = "https://maven.icecreamqaq.com/repository/maven-snapshots/"
                    val releasesRepoUrl = "https://maven.icecreamqaq.com/repository/maven-releases/"
                    url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)


                    credentials {
                        username = ""
                        password = ""
                    }
                }
            }
        }
    }

    tasks {
        test {
            useJUnitPlatform {
                excludeEngines("junit-jupiter")
            }
        }
    }
}