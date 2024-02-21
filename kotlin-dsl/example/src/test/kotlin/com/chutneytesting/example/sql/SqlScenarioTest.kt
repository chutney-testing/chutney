package com.chutneytesting.example.sql

import com.chutneytesting.example.scenario.SQL_TARGET_NAME
import com.chutneytesting.example.scenario.sql_scenario
import com.chutneytesting.kotlin.dsl.ChutneyEnvironment
import com.chutneytesting.kotlin.dsl.Environment
import com.chutneytesting.kotlin.launcher.Launcher
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
class SqlScenarioTest {

    private var dbAddress: String = ""
    private var dbPort = 0
    private var environment: ChutneyEnvironment = ChutneyEnvironment("default value")

    @Container
    val postgresqlContainer = PostgreSQLContainer(DockerImageName.parse("postgres"))
        .withInitScript("sql/create_movies_table.sql")


    @BeforeEach
    fun setUp() {
        dbAddress = postgresqlContainer.host
        dbPort = postgresqlContainer.firstMappedPort
        environment = Environment(name = "local", description = "local environment") {
            Target {
                Name(SQL_TARGET_NAME)
                Url("tcp://$dbAddress:$dbPort")
                Properties(
                    "jdbcUrl" to postgresqlContainer.jdbcUrl,
                    "username" to postgresqlContainer.username,
                    "password" to postgresqlContainer.password
                )
            }
        }
    }

    @Test
    fun `insert and select movies`() {
        Launcher().run(sql_scenario, environment)
    }
}
