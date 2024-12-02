import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.jreleaser.model.Active

plugins {
    kotlin("jvm") version "1.9.25"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
    id("com.github.ben-manes.versions") version "0.51.0"
    id("org.jreleaser") version "1.15.0"
    id("maven-publish")
}

group = "us.aldwin.test"
// SHOULD MATCH GIT TAG!
// TODO @NJA: investigate a plugin for this
version = "1.0.0"

val ghUser = "NJAldwin"
val ghRepo = "maven-central-test"
val ghUrl = "https://github.com/$ghUser/$ghRepo"

val topDesc = "A test project for Maven Central publishing"

allprojects {
    repositories {
        mavenCentral()
    }
}

// no top-level jar
tasks.named("jar") {
    enabled = false
}

tasks.withType<DependencyUpdatesTask> {
    gradleReleaseChannel = GradleReleaseChannel.CURRENT.id

    rejectVersionIf {
        listOf("alpha", "beta", "rc", "cr", "m", "eap", "pr", "dev").any {
            candidate.version.contains(it, ignoreCase = true)
        }
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    kotlin {
        jvmToolchain(8)
        explicitApi()
    }

    tasks.withType<KotlinJvmCompile> {
        compilerOptions {
            allWarningsAsErrors.set(true)
        }
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    version = rootProject.version
    tasks.withType<Jar> {
        archiveBaseName.set(project.name)
        archiveVersion.set(project.version.toString())
    }

    ktlint {
        verbose.set(true)
        outputToConsole.set(true)
        coloredOutput.set(true)
        reporters {
            reporter(ReporterType.PLAIN)
            reporter(ReporterType.CHECKSTYLE)
            reporter(ReporterType.JSON)
            reporter(ReporterType.HTML)
        }
    }

    tasks.test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}

jreleaser {
    dryrun.set(System.getenv("CI").isNullOrBlank())

    project {
        name.set("maven-central-test")
        description.set(topDesc)
        version.set(rootProject.version.toString())
        authors.set(listOf("Nick Aldwin"))
        license.set("MIT")
        inceptionYear.set("2024")
        links {
            homepage = ghUrl
        }
    }

    release {
        github {
            repoOwner.set(ghUser)
            name.set(ghRepo)
            branch.set("master")

            // skip tag because we're running release on tag creation
            skipTag.set(true)
            prerelease {
                enabled.set(true)
                // match semver `x.y.z-something`
                pattern.set("""\d+\.\d+\.\d+-.+""")
            }
        }
    }

    signing {
        active.set(Active.ALWAYS)
        armored.set(true)
        verify.set(true)
    }

    deploy {
        maven {
            mavenCentral.create("sonatype") {
                active.set(Active.ALWAYS)
                url.set("https://central.sonatype.com/api/v1/publisher")
                subprojects.filter { it.plugins.hasPlugin("java") }.forEach { subproject ->
                    stagingRepositories.add("${subproject.layout.buildDirectory.get()}/staging-deploy")
                }
                applyMavenCentralRules.set(true)
                retryDelay.set(20)
                maxRetries.set(90)
            }
        }
    }

    distributions {
        subprojects.filter { it.plugins.hasPlugin("java") }.forEach { subproject ->
            create(subproject.name) {
                project {
                    description.set(
                        providers.provider {
                            subproject.description ?: topDesc
                        },
                    )
                }
                artifact {
                    path.set(subproject.tasks.named<Jar>("jar").get().archiveFile.get().asFile)
                }
                artifact {
                    path.set(subproject.tasks.named<Jar>("sourcesJar").get().archiveFile.get().asFile)
                    platform.set("java-sources")
                }
                artifact {
                    path.set(subproject.tasks.named<Jar>("javadocJar").get().archiveFile.get().asFile)
                    platform.set("java-docs")
                }
            }
        }
    }
}

subprojects {
    plugins.withId("java") {
        apply(plugin = "maven-publish")
        publishing {
            publications {
                create<MavenPublication>("maven") {
                    groupId = rootProject.group.toString()
                    artifactId = project.name
                    version = rootProject.version.toString()

                    from(components["java"])

                    pom {
                        name.set(project.name)
                        description.set(
                            providers.provider {
                                project.description ?: topDesc
                            },
                        )
                        url.set(rootProject.jreleaser.project.links.homepage)

                        inceptionYear.set(rootProject.jreleaser.project.inceptionYear.get())
                        licenses {
                            license {
                                name.set(rootProject.jreleaser.project.license.get())
                                url.set("https://opensource.org/licenses/${rootProject.jreleaser.project.license.get()}")
                            }
                        }
                        developers {
                            developer {
                                id.set(rootProject.jreleaser.release.github.repoOwner.get())
                                name.set(rootProject.jreleaser.project.authors.get().joinToString())
                            }
                        }
                        scm {
                            connection.set("scm:git:$ghUrl.git")
                            developerConnection.set("scm:git:ssh://github.com/$ghUser/$ghRepo.git")
                            url.set(ghUrl)
                        }
                    }
                }
            }

            repositories {
                maven {
                    url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
                }
            }
        }
    }
}
