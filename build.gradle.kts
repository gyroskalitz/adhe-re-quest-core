plugins {
    kotlin("jvm") version "2.2.0"
    `maven-publish`
    `java-library`
}

group = "io.github.gyroskalitz"
version = System.getenv("RELEASE_VERSION")?:"1.0.0"

// 配置发布内容
publishing {
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])

            groupId = project.group.toString()
            artifactId = "adhe-re-quest-core"
            version = project.version.toString()
            pom {
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("gyroskalitz")
                        name.set("Gyro Skalitz")
                    }
                }
            }
        }
    }

    // 发布到（GitHub Packages）
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/gyroskalitz/adhe-re-quest-core")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

java {
    withSourcesJar()
}