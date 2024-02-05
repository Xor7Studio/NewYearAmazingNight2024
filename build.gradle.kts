@file:Suppress("SpellCheckingInspection")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.0.0"
    application
}

group = "cn.xor7"
version = "1.0.0"

repositories {
    maven("https://repo.leavesmc.top/snapshots")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://www.jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://maven.aliyun.com/repository/public/")
    mavenCentral()
}

@Suppress("VulnerableLibrariesLocal")
dependencies {
    compileOnly("top.leavesmc.leaves:leaves-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.github.602723113:ParticleLib:1.5.1")
    compileOnly("dev.jorel:commandapi-bukkit-core:9.3.0")
    compileOnly("dev.jorel:commandapi-bukkit-kotlin:9.3.0")
    // 暂不需要
    // implementation("net.jodah:expiringmap:0.5.11")
    implementation("cn.zhxu:okhttps:4.0.1")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("io.ktor:ktor-server-cors:2.3.7")
    implementation("fr.mrmicky:fastboard:2.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation(kotlin("stdlib-jdk8"))
}

application {
    mainClass.set("cn.xor7.MainKt")
}

kotlin {
    jvmToolchain(17)
}

tasks.processResources {
    filesMatching("paper-plugin.yml") {
        expand(
            mapOf(
                "version" to version,
            )
        )
    }
}

tasks.withType<ShadowJar> {
    minimize()
    archiveFileName.set("NewYearAmazingNight2024-$version.jar")
    relocate("fr.mrmicky.fastboard", "cn.xor7.fastboard")
}