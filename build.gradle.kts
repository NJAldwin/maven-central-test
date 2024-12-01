import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    kotlin("jvm") version "2.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
    id("com.github.ben-manes.versions") version "0.51.0"
    id("org.jreleaser") version "1.15.0"
}

group = "us.aldwin.test"
// SHOULD MATCH GIT TAG!
// TODO @NJA: investigate a plugin for this
version = "0.0.1-alpha4"

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
        jvmToolchain(17)
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
        version.set(project.version.toString())
        authors.set(listOf("Nick Aldwin"))
        license.set("MIT")
    }

    release {
        github {
            repoOwner.set("NJAldwin")
            name.set("maven-central-test")
            branch.set("master")
            // skip tag because we're running release on tag creation
            skipTag.set(true)
            prerelease {
                enabled.set(true)
                // match semver `x.y.z-something`
                pattern.set("""\d+\.\d+\.\d+-.+""")
            }
            releaseNotes {
                enabled.set(true)
            }
        }
    }

    distributions {
        subprojects.filter { it.plugins.hasPlugin("java") }.forEach { subproject ->
            create(subproject.name) {
                project {
                    description.set(subproject.description)
                }
                artifact {
                    path.set(subproject.tasks.named<Jar>("jar").get().archiveFile.get().asFile)
                }
                artifact {
                    path.set(subproject.tasks.named<Jar>("sourcesJar").get().archiveFile.get().asFile)
                }
                artifact {
                    path.set(subproject.tasks.named<Jar>("javadocJar").get().archiveFile.get().asFile)
                }
            }
        }
    }
}
