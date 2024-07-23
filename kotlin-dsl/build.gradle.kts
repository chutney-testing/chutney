import org.jetbrains.kotlin.gradle.dsl.JvmTarget

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    kotlin("jvm") version "1.9.25"
    java
    `maven-publish`
    signing
}

repositories {
    mavenLocal()
    mavenCentral()
}

val chutneyGroup = "com.chutneytesting"
val chutneyVersion = properties("chutneyVersion")
val githubUrl = "https://github.com/chutney-testing/${project.name}"
val publicationName = "chutneyKotlinDSL"

group = chutneyGroup
version = chutneyVersion

configurations {
    all {
        resolutionStrategy {
            failOnVersionConflict()
        }
    }
}

dependencies {

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    implementation(enforcedPlatform("com.chutneytesting:chutney-parent:${chutneyVersion}"))
    // Resolve conflicts from chutney-parent for runtime classpath
    runtimeOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")

    api("com.chutneytesting:engine")
    implementation("com.chutneytesting:environment")
    runtimeOnly("com.chutneytesting:action-impl")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.slf4j:slf4j-api")
    implementation("org.assertj:assertj-core")
    implementation("org.springframework:spring-core")
    implementation("org.apache.commons:commons-text")
    implementation("io.github.classgraph:classgraph:4.8.171")
    implementation("org.apache.httpcomponents.client5:httpclient5")
    implementation("io.reactivex.rxjava3:rxjava")

    testImplementation("org.skyscreamer:jsonassert")
    testImplementation("org.springframework:spring-expression")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.junit-pioneer:junit-pioneer:2.2.0")
    testImplementation("org.wiremock:wiremock-standalone")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")

    // JUnit5 engine dependencies
    implementation("org.junit.platform:junit-platform-engine")
    implementation("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.platform:junit-platform-testkit")
}

tasks {
    compileKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    test {
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

    javadoc {
        if (JavaVersion.current().isJava11Compatible) {
            (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
        }
    }

    withType<GenerateModuleMetadata> {
        suppressedValidationErrors.add("enforced-platform")
    }

    withType<PublishToMavenRepository>().configureEach {
        val predicate = provider {
            val publishToOssrh =
                repository == publishing.repositories["ossrh"] && project.findProperty("server-id") == "ossrh"
            val publishToGithub =
                repository == publishing.repositories["github"] && project.findProperty("server-id") == "github"
            publishToOssrh || publishToGithub
        }
        onlyIf("publish to ossrh or github") {
            predicate.get()
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()

    consistentResolution {
        useCompileClasspathVersions() // OR set in configurations using shouldResolveConsistentlyWith()
    }
}

publishing {
    publications {
        create<MavenPublication>(publicationName) {
            groupId = chutneyGroup
            artifactId = project.name
            version = chutneyVersion
            from(components["java"])
            pom {
                name.set("Chutney Kotlin DSL")
                description.set("Generates Chutney scenarios using Kotlin.")
                inceptionYear.set("2017")
                url.set(githubUrl)
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                scm {
                    url.set("${githubUrl}.git")
                    connection.set("scm:git:git@github.com:chutney-testing/${project.name}.git")
                    developerConnection.set("scm:git:git@github.com:chutney-testing/${project.name}.git")
                    tag.set(project.version.toString().takeUnless { it.endsWith("SNAPSHOT") })
                }
                issueManagement {
                    system.set("github")
                    url.set("${githubUrl}/issues")
                }
                ciManagement {
                    system.set("github-ci")
                    url.set("${githubUrl}/actions")
                }
                developers {
                    developer {
                        id.set("iguissouma ")
                        name.set("Issam Guissouma")
                    }
                    developer {
                        id.set("boddissattva")
                        name.set("Matthieu Gensollen")
                    }
                    developer {
                        id.set("bessonm")
                        name.set("Mael Besson")
                    }
                    developer {
                        id.set("nbrouand")
                        name.set("Nicolas Brouand")
                    }
                    developer {
                        id.set("KarimGl")
                        name.set("Karim Goubbaa")
                    }
                    developer {
                        id.set("DelaunayAlex")
                        name.set("Alexandre Delaunay")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "ossrh"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
        maven {
            name = "github"
            url = uri("https://maven.pkg.github.com/chutney-testing/chutney")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

signing {
    isRequired = false
    if (project.findProperty("server-id") == "ossrh") {
//useGpgCmd()
// Format: "0x12345678" ; gpg --list-keys --keyid-format 0xSHORT
        val signingKeyId: String? = System.getenv("CHUTNEY_GPG_KEY_ID")
// gpg -a --export-secret-subkeys KEY_ID
        val signingKey: String? = System.getenv("CHUTNEY_GPG_KEY")
        val signingPassword: String? = System.getenv("CHUTNEY_GPG_PASSPHRASE")
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        sign(publishing.publications[publicationName])
    }
}
