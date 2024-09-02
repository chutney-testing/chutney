/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import org.jetbrains.changelog.markdownToHTML

fun properties(key: String) = project.findProperty(key).toString()

plugins {
  // Java support
  id("java")
  // Kotlin support
  id("org.jetbrains.kotlin.jvm") version "1.9.25"
  // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
  id("org.jetbrains.intellij.platform") version "2.0.1"
  id("org.jetbrains.intellij.platform.migration") version "2.0.1"
  // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
  id("org.jetbrains.changelog") version "2.2.1"
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

group = properties("pluginGroup")
version = properties("chutneyVersion")

// Set the JVM language level used to build the project.
kotlin {
  jvmToolchain(17)
}

repositories {
  mavenLocal()
  mavenCentral()
  intellijPlatform {
    defaultRepositories()
  }
}

dependencies {
  implementation(enforcedPlatform("com.chutneytesting:chutney-parent:${properties["chutneyVersion"]}"))
  implementation("com.chutneytesting", "chutney-kotlin-dsl", properties("chutneyVersion")) {
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
    isTransitive = false // this exclude "org.sl4j"
  }
  runtimeOnly("com.chutneytesting", "local-api-unsecure", properties("chutneyVersion"), ext = "jar") {
    isTransitive = false
  }

  intellijPlatform {
    create(properties("platformType"), properties("platformVersion"))
    bundledPlugins(properties("platformBundledPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
    instrumentationTools()
    pluginVerifier()
  }

}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellijPlatform {
  buildSearchableOptions = false
  pluginConfiguration {
    name = properties("pluginName")
    version = properties("chutneyVersion")
    description = File(projectDir, "README.md").readText().lines().run {
      val start = "<!-- Plugin description -->"
      val end = "<!-- Plugin description end -->"

      if (!containsAll(listOf(start, end))) {
        throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
      }
      subList(indexOf(start) + 1, indexOf(end))
    }.joinToString("\n").run { markdownToHTML(this) }
    changeNotes = provider { changelog.getLatest().toHTML() }
    ideaVersion {
      sinceBuild = providers.gradleProperty("pluginSinceBuild")
      untilBuild = providers.gradleProperty("pluginUntilBuild")
    }
  }

  pluginVerification {
    ides {
      recommended()
    }
  }

  publishing {
    token = System.getenv("PUBLISH_TOKEN")
    channels = listOf(properties("chutneyVersion").split('-').getOrElse(1) { "default" }.split('.').first())
  }
  tasks {
    wrapper {
      gradleVersion = properties("gradleVersion")
    }
    runIde {
      maxHeapSize = "2g"
    }
    publishPlugin {
      dependsOn(patchChangelog)
    }
  }
}
