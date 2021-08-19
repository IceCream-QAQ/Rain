import okhttp3.OkHttpClient
import okhttp3.Request

plugins {
    java
    kotlin("jvm") version "1.4.20"
    `java-library`
    `maven-publish`
}

buildscript {

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.icecreamqaq.com/repository/maven-public/")
    }

    dependencies {
        classpath("com.squareup.okhttp3:okhttp:4.9.0")
    }
}
val baseVersion = "0.2.0.0"

fun makeVersion(): String {
    val branch = exec("git rev-parse --abbrev-ref HEAD")
    if (branch == "master") return baseVersion
    val buildVersion = getNextBuildVersion(group.toString(), name, baseVersion, branch, "")
    return "$baseVersion-$branch$buildVersion"
}

fun getNextBuildVersion(groupId: String, artifact: String, baseVersion: String, branch: String, opcode: String) =
    OkHttpClient()
        .newCall(
            Request.Builder()
                .url(
                    "http://127.0.0.1:5557/" +
                            "qptVersion/" +
                            "nextVersion?" +
                            "groupId=$groupId&" +
                            "artifact=$artifact&" +
                            "baseVersion=$baseVersion&" +
                            "branch=$branch&" +
                            "opcode=$opcode"
                ).build()
        ).execute()
        .body!!
        .string()


fun exec(cmd: String): String {
    val r = Runtime.getRuntime().exec(cmd)
    r.waitFor()
    return r.inputStream.reader().readText()
}

group = "com.IceCreamQAQ"
version = "0.2.0.0-DEV13"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.icecreamqaq.com/repository/maven-public/")
}

dependencies {
    api(kotlin("stdlib"))
    api(kotlin("reflect"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0")

    api("com.squareup.okhttp3:okhttp:4.9.0")
    api("ch.qos.logback:logback-classic:1.2.3")
    api("com.alibaba:fastjson:1.2.78")
    api("org.eclipse.jdt:ecj:3.26.0")
    api("net.sf.ehcache:ehcache:2.10.9.2")

    api("org.ow2.asm:asm-commons:9.2")

    api("javax.inject:javax.inject:1")

    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")
}

tasks {
    withType<JavaCompile> {

    }
}

publishing {

    publications {
        create<MavenPublication>("maven") {
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
    file("${System.getProperty("user.home")}/.m2/mvnInfo-$id.txt").readText().split("|")