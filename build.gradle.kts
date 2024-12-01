import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    kotlin("jvm") version "2.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
    id("com.github.ben-manes.versions") version "0.51.0"
}

group = "us.aldwin.test"
version = "0.0.1-alpha3"

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
