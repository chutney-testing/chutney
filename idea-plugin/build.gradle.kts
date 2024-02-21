import org.jetbrains.changelog.markdownToHTML

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.8.22"
    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "1.13.3"
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "1.3.1"
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
version = properties("pluginVersion")

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://repo1.maven.org/maven2")
    }
    ivy {
        url = uri("https://github.com/")
        patternLayout {
            artifact("/[organisation]/[module]/releases/download/[revision]/[artifact]-[revision].[ext]")
        }
        // This is required in Gradle 6.0+ as metadata file (ivy.xml) is mandatory.
        // Docs https://docs.gradle.org/6.2/userguide/declaring_repositories.html#sec:supported_metadata_sources
        metadataSources { artifact() }
    }
}

dependencies {
    implementation(enforcedPlatform("com.chutneytesting:chutney-parent:${properties["chutneyVersion"]}"))
    implementation("com.chutneytesting", "chutney-kotlin-dsl", "2.0.1")
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
    testImplementation("junit", "junit", "4.12")
    runtimeOnly("chutney-testing", "chutney-idea-server", properties("chutneyIdeaServerVersion"), ext = "jar") {
        isTransitive = false
    }

}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))
    downloadSources.set(properties("platformDownloadSources").toBoolean())
    updateSinceUntilBuild.set(true)

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))

    tasks{
        buildSearchableOptions {
            enabled = false
        }

        runIde {
            maxHeapSize = "2g"
        }
    }
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_17
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    version.set(properties("pluginVersion"))
    groups.set(emptyList())
}


tasks {
    // Set the compatibility versions to 17
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }


    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription.set(
            File(projectDir, "README.md").readText().lines().run {
                val start = "<!-- Plugin description -->"
                val end = "<!-- Plugin description end -->"

                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end))
            }.joinToString("\n").run { markdownToHTML(this) }
        )

        // Get the latest available change notes from the changelog file
        changeNotes.set(provider { changelog.getLatest().toHTML() })
    }

    runPluginVerifier {
        ideVersions.set(properties("pluginVerifierIdeVersions").split(',').map(String::trim).filter(String::isNotEmpty))
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token.set(System.getenv("PUBLISH_TOKEN"))
        // pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels.set(listOf(properties("pluginVersion").split('-').getOrElse(1) { "default" }.split('.').first()))
    }
}
