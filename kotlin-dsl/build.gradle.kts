import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.ajoberstar.reckon") version "0.18.0"

    kotlin("jvm") version "1.9.21" apply false
}

reckon {
    setDefaultInferredScope("patch")
    stages("rc", "final")
    setScopeCalc(calcScopeFromProp().or(calcScopeFromCommitMessages()))
    setStageCalc(calcStageFromProp())
}

subprojects {
    extra["chutneyTestingVersion"] = "2.7.0"

    repositories {
        mavenLocal()
        mavenCentral()
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        val implementation by configurations
        val testImplementation by configurations
        val runtimeOnly by configurations
        val testRuntimeOnly by configurations

        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testImplementation("org.junit.jupiter:junit-jupiter-params")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

        implementation(enforcedPlatform("com.chutneytesting:chutney-parent:${project.extra["chutneyTestingVersion"]}"))
        // Resolve conflicts from chutney-parent for runtime classpath
        runtimeOnly("com.fasterxml.jackson.module:jackson-module-scala_2.13") // :2.15.3
        runtimeOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-csv") // :2.15.3
        runtimeOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml") // :2.15.3
        runtimeOnly("org.eclipse.jetty:jetty-client") // :12.0.5
        runtimeOnly("org.eclipse.jetty:jetty-security") // :12.0.5
        runtimeOnly("org.eclipse.jetty:jetty-xml") // :12.0.5
        runtimeOnly("org.eclipse.jetty.http2:http2-common:11.0.19")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "failed", "skipped")
            showStandardStreams = true
        }
        // Pass the proxy configuration to the gradle test executor
        systemProperty("http.proxyHost", System.getProperty("http.proxyHost"))
        systemProperty("http.proxyPort", System.getProperty("http.proxyPort"))
        systemProperty("http.nonProxyHosts", System.getProperty("http.nonProxyHosts"))
        systemProperty("https.proxyHost", System.getProperty("https.proxyHost"))
        systemProperty("https.proxyPort", System.getProperty("https.proxyPort"))
        systemProperty("https.nonProxyHosts", System.getProperty("https.nonProxyHosts"))
    }
}
