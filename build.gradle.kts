plugins {
    java
    kotlin("jvm") version "1.8.0"
    `java-library`
    `maven-publish`
}

group = "com.IceCreamQAQ"
version = "0.3.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.icecreamqaq.com/repository/maven-public/")
}

dependencies {
    api(kotlin("stdlib"))
    api(kotlin("reflect"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    api("com.squareup.okhttp3:okhttp:4.10.0")
    // Logback 1.4+ 最低支持 Java 11。
    api("ch.qos.logback:logback-classic:1.3.1")
    api("com.alibaba.fastjson2:fastjson2:2.0.23")
    // ECJ 当前版本为最后支持 Java8 的版本，应停留在本版本。
    api("org.eclipse.jdt:ecj:3.26.0")
    api("org.ehcache:ehcache:3.10.1")

    api("org.ow2.asm:asm-commons:9.3")

    api("javax.inject:javax.inject:1")

    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
}

java {
    withSourcesJar()
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

publishing {

    publications {
        create<MavenPublication>("Yu-Core") {
            groupId = group.toString()
            artifactId = name
            version = project.version.toString()

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
    }

    repositories {
        mavenLocal()
        maven {
            val snapshotsRepoUrl = "https://maven.icecreamqaq.com/repository/maven-snapshots/"
            val releasesRepoUrl = "https://maven.icecreamqaq.com/repository/maven-releases/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)


            credentials {

                val mvnInfo = readMavenUserInfo("IceCream")
                username = mvnInfo[0]
                password = mvnInfo[1]
            }
        }
    }

}
fun readMavenUserInfo(id: String) =
    fileOr(
        "mavenInfo.txt",
        "${System.getProperty("user.home")}/.m2/mvnInfo-$id.txt"
    )?.readText()?.split("|") ?: arrayListOf("", "")


fun File.check() = if (this.exists()) this else null
fun fileOr(vararg file: String): File? {
    for (s in file) {
        val f = file(s)
        if (f.exists()) return f
    }
    return null
}