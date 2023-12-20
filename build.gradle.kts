import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.0.0"
    application
}

group = "cn.xor7"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        url = uri("https://repo.leavesmc.top/snapshots")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/public/")
    }
    mavenCentral()
}

dependencies {
    compileOnly("top.leavesmc.leaves:leaves-api:1.20.1-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<ShadowJar> {
    minimize()
    archiveFileName.set("NewYearAmazingNight2024-$version.jar")
}

application {
    mainClass.set("")
}