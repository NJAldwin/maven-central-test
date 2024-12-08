dependencies {
    implementation(project(":maven-central-test"))

    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

description = "A second test project for Maven Central publishing"
