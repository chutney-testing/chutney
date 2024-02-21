import java.time.Instant
import java.time.format.DateTimeFormatter

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    java
    `maven-publish`
    signing
}

val group = "com.chutneytesting"
version = properties("chutneyVersion")
val timestamp: String = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
val githubUrl = "https://github.com/chutney-testing/${project.name}"
val publicationName = "chutneyKotlinDSL"

configurations {
    all {
        resolutionStrategy {
            failOnVersionConflict()
        }
    }
}

dependencies {

    api("com.chutneytesting:engine:${properties["chutneyVersion"]}")
    implementation("com.chutneytesting:environment:${properties["chutneyVersion"]}")
    runtimeOnly("com.chutneytesting:action-impl:${properties["chutneyVersion"]}")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.slf4j:slf4j-api")
    implementation("org.assertj:assertj-core")
    implementation("org.springframework:spring-core")
    implementation("org.apache.commons:commons-text")
    implementation("io.github.classgraph:classgraph:4.8.141")
    implementation("org.apache.httpcomponents.client5:httpclient5")
    implementation("io.reactivex.rxjava3:rxjava")

    testImplementation("org.skyscreamer:jsonassert")
    testImplementation("org.springframework:spring-expression")
    testImplementation(kotlin("scripting-jsr223"))
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.junit-pioneer:junit-pioneer:2.0.0")
    testImplementation("org.wiremock:wiremock-standalone")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")

    // JUnit5 engine dependencies
    implementation("org.junit.platform:junit-platform-engine")
    implementation("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.platform:junit-platform-testkit")
}

java {
    withJavadocJar()
    withSourcesJar()

    consistentResolution {
        useCompileClasspathVersions() // OR set in configurations using shouldResolveConsistentlyWith()
    }

}

tasks.javadoc {
    if (JavaVersion.current().isJava11Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

tasks.withType<GenerateModuleMetadata> {
    suppressedValidationErrors.add("enforced-platform")
}

tasks.withType<PublishToMavenRepository>().configureEach {
    val predicate = provider {
        val publishToOssrh = repository == publishing.repositories["ossrh"] && project.findProperty("server-id") == "ossrh"
        val publishToGithub = repository == publishing.repositories["github"] && project.findProperty("server-id") == "github"
        publishToOssrh || publishToGithub
    }
    onlyIf("publish to ossrh or github") {
        predicate.get()
    }
}

publishing {
    publications {
        create<MavenPublication>(publicationName) {
            groupId = group
            artifactId = project.name
            version = properties("chutneyVersion")
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
            url = uri("https://maven.pkg.github.com/chutney-testing/chutney-suite")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

signing {
    setRequired(  false )
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

