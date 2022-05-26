plugins {
    java
    kotlin("jvm") version "1.4.20"
    `java-library`
    `maven-publish`
}


group = "com.IceCreamQAQ"
version = "0.2.0.0-DEV16"

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

java{
    withSourcesJar()
}
//kotlin{
//    withSou
//}

tasks {
//    val sourcesJar by creating(Jar::class) {
//        archiveClassifier.set("sources")
//        from(sourceSets.main.get().allSource)
//    }


//    val javadocJar by creating(Jar::class) {
//        dependsOn.add(javadoc)
//        archiveClassifier.set("javadoc")
//        from(javadoc)
//    }

//    artifacts {
//        archives(sourcesJar)
////        archives(javadocJar)
//        archives(jar)
//    }
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
    file("${System.getProperty("user.home")}/.m2/mvnInfo-$id.txt").readText().split("|")