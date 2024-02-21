dependencies {
    implementation(project(":chutney-kotlin-dsl"))
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.testcontainers:rabbitmq")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.wiremock.integrations.testcontainers:wiremock-testcontainers-module:1.0-alpha-13")
    testImplementation ("org.postgresql:postgresql")
    testImplementation("org.testcontainers:selenium")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.+")
}

task("chutneyGradleReportSite", JavaExec::class) {
    classpath(configurations.runtimeClasspath)

    mainClass.set("com.chutneytesting.kotlin.execution.report.SiteGeneratorMain")
    args("build/reports/chutney")
}

tasks {
    test {
        systemProperty("chutney.report.rootPath", "build/reports/chutney")
        systemProperty("chutney.engine.stepAsTest", false)
        systemProperty("chutney.log.color.enabled", false)

        finalizedBy("chutneyGradleReportSite")
    }
}
