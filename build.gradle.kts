@file:Suppress("SpellCheckingInspection")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.0.0"
    application
}

group = "cn.xor7"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://maven.aliyun.com/repository/public/")
    mavenCentral()
    maven("https://repo.leavesmc.top/snapshots")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://www.jitpack.io")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.javaParameters = true
}

@Suppress("VulnerableLibrariesLocal")
dependencies {
    compileOnly("top.leavesmc.leaves:leaves-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.github.602723113:ParticleLib:1.5.1")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("io.ktor:ktor-server-cors:2.3.7")
    implementation("fr.mrmicky:fastboard:2.0.2")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation(kotlin("stdlib-jdk8"))
}

application {
    mainClass.set("cn.xor7.MainKt")
}

tasks.withType<ShadowJar> {
    minimize()
    archiveFileName.set("NewYearAmazingNight2024-$version.jar")
    relocate("co.aikar.commands", "cn.xor7.acf")
    relocate("co.aikar.locales", "cn.xor7.locales")
    relocate("fr.mrmicky.fastboard", "cn.xor7.fastboard")
}