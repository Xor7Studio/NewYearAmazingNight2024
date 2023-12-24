@file:Suppress("SpellCheckingInspection")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.0.0"
}

group = "cn.xor7"
version = "1.0-SNAPSHOT"

repositories {
    maven ("https://repo.leavesmc.top/snapshots")
    maven ("https://maven.aliyun.com/repository/public/")
    maven ("https://repo.aikar.co/content/groups/aikar/")
    maven ("https://www.jitpack.io")
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.javaParameters = true
}

@Suppress("VulnerableLibrariesLocal")
dependencies {
    compileOnly("top.leavesmc.leaves:leaves-api:1.20.1-R0.1-SNAPSHOT")
    implementation("com.github.602723113:ParticleLib:1.5.0")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<ShadowJar> {
    minimize()
    archiveFileName.set("NewYearAmazingNight2024-$version.jar")
    relocate("co.aikar.commands", "cn.xor7.acf")
    relocate("co.aikar.locales", "cn.xor7.locales")
}