import java.time.Instant
import java.time.format.DateTimeFormatter

plugins {
    java
    `maven-publish`
    signing
}

val group = "com.chutneytesting"
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

    api("com.chutneytesting:engine:${project.extra["chutneyTestingVersion"]}")
    implementation("com.chutneytesting:environment:${project.extra["chutneyTestingVersion"]}")
    runtimeOnly("com.chutneytesting:action-impl:${project.extra["chutneyTestingVersion"]}")

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

publishing {
    publications {
        create<MavenPublication>(publicationName) {
            groupId = group
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
            pom {
                name.set("Chutney Kotlin DSL")
                description.set("Generates Chutney scenarios using Kotlin.")
                inceptionYear.set("2020")
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
                }
            }
        }
    }
    repositories {
        maven {
            name = "OSSRH"

            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots"

            val ossrhUsername =
                System.getenv("OSSRH_USERNAME") // Use token ; https://s01.oss.sonatype.org/#profile;User%20Token
            val ossrhPassword = System.getenv("OSSRH_PASSWORD") // Use token

            url = uri(releasesRepoUrl)
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}

signing {
    //useGpgCmd()

    // Format: "0x12345678" ; gpg --list-keys --keyid-format 0xSHORT
    val signingKeyId: String? = System.getenv("CHUTNEY_GPG_KEY_ID")

    // gpg -a --export-secret-subkeys KEY_ID
    val signingKey: String? = System.getenv("CHUTNEY_GPG_KEY")
    val signingPassword: String? = System.getenv("CHUTNEY_GPG_PASSPHRASE")
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications[publicationName])
}
