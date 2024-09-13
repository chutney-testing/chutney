/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
  // Java support
  java
  // Kotlin support
  kotlin("jvm") version "2.0.20"
  // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
  id("org.jetbrains.intellij.platform") version "2.0.1"
}

configurations.all {
  exclude("org.sl4j")
  resolutionStrategy {
    failOnVersionConflict()
  }
}

configurations.runtimeOnly {
  shouldResolveConsistentlyWith(configurations.implementation.get())
}

private val chutneyVersion = providers.gradleProperty("chutneyVersion").get()

group = providers.gradleProperty("pluginGroup")
version = chutneyVersion

repositories {
  mavenLocal()
  mavenCentral()
  intellijPlatform {
    defaultRepositories()
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
  jvmToolchain(17)
}

dependencies {
  intellijPlatform {
    // Build against the least version supported
    intellijIdeaUltimate("2023.1")

    // \o/ Conflict here when building !! => Use Spring bundled plugin transitive dependencies
    //bundledPlugin("com.intellij.java")
    //bundledPlugin("org.jetbrains.plugins.yaml")
    bundledPlugin("com.intellij.spring")
    bundledPlugin("org.jetbrains.kotlin")

    pluginVerifier()
    instrumentationTools()
  }

  implementation(enforcedPlatform("com.chutneytesting:chutney-parent:$chutneyVersion"))
  implementation("com.chutneytesting", "chutney-kotlin-dsl", chutneyVersion) {
    isTransitive = false
  }
  // Runtime for kotlin-dsl dependency (server info && Http client)
  runtimeOnly("com.fasterxml.jackson.module", "jackson-module-kotlin")
  runtimeOnly("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310")
  runtimeOnly("com.fasterxml.jackson.module", "jackson-module-paranamer")
  runtimeOnly("org.apache.httpcomponents.client5", "httpclient5") {
    exclude("org.slf4j")
  }
  runtimeOnly("org.apache.httpcomponents.core5", "httpcore5")
  implementation("com.google.guava", "guava")
  implementation("org.hjson", "hjson")
  implementation("org.apache.commons", "commons-text")
  implementation("com.fasterxml.jackson.core", "jackson-core")
  implementation("com.fasterxml.jackson.core", "jackson-databind")
  implementation("com.fasterxml.jackson.core", "jackson-annotations")
  implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml")
  implementation("org.jetbrains:annotations") {
    version { strictly("24.0.0") }
  }

  // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-script-util
  implementation("org.jetbrains.kotlin:kotlin-script-util:1.8.22")

  implementation("me.andrz.jackson", "jackson-json-reference-core", "0.3.2") {
    // exclude("org.sl4j") does not exclude
    isTransitive = false // this excludes "org.sl4j"
  }
  runtimeOnly("com.chutneytesting", "local-api-unsecure", chutneyVersion, ext = "jar") {
    isTransitive = false
  }
}

// see https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-extension.html
intellijPlatform {
  buildSearchableOptions = false

  pluginConfiguration {
    version = chutneyVersion

    ideaVersion {
      sinceBuild = providers.gradleProperty("pluginSinceBuild")
      untilBuild = providers.gradleProperty("pluginSinceUntil")
    }
  }

  publishing {
    token = System.getenv("PUBLISH_TOKEN")
    channels = listOf(chutneyVersion.split('-').getOrElse(1) { "default" }.split('.').first())
  }

  // https://github.com/JetBrains/intellij-platform-gradle-plugin/issues/1719
  pluginVerification {
    // Need to set this option because can't download android dependencies
    // because don't know how to set proxy configuration for this
    //freeArgs.add("-offline")
    ides {
      // Check against the last Ultimate version
      ide(IntelliJPlatformType.IntellijIdeaUltimate, "2024.2.1")
      // \o/ Can't use recommended, got dependency conflict !!
      // recommended()
    }
  }
}
