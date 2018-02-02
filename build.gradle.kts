import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    var kotlinVersion: String by extra
    kotlinVersion = "1.2.21"

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlinModule("gradle-plugin", kotlinVersion))
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.3")
    }
}

group = "sirgl"
version = "1.0-SNAPSHOT"

apply {
    plugin("kotlin")
}

val kotlinVersion: String by extra

repositories {
    mavenCentral()
    maven {
        setUrl("https://jitpack.io")
    }
}


apply {
    plugin("org.junit.platform.gradle.plugin")
}

dependencies {
    compile(kotlinModule("stdlib-jdk8", kotlinVersion))
    compile("com.github.norswap:whimsy:-SNAPSHOT")
    testCompile("org.junit.jupiter:junit-jupiter-api:5.0.3")
    testCompile("org.junit.jupiter:junit-jupiter-engine:5.0.3")
    testCompile("org.junit.platform:junit-platform-launcher:1.0.3")
    testCompile("org.junit.platform:junit-platform-runner:1.0.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}