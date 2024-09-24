/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests.actions

import com.chutneytesting.acceptance.common.*
import com.chutneytesting.kotlin.dsl.*

val `Sql query success` = Scenario(title = "Sql query success") {
  Given("A sql database target") {
    createSqlTarget("SQL_ENV_OK")
  }
  And("This scenario with sql task is saved") {
    createScenario("scenarioId",
        """
        {
            "givens":[
                {
                    "sentence":"Create users table",
                    "implementation":{
                        "task":"{\n type: sql \n target: test_sql \n inputs: {\n statements: [\n CREATE TABLE users ( id INTEGER PRIMARY KEY, name VARCHAR(30), email VARCHAR(50) ) \n] \n}\n}"
                    }
                }
            ],
            "when":{
                "sentence":"Insert users",
                "implementation":{
                    "task":"{\n type: sql \n target: test_sql \n inputs: {\n statements: [\n INSERT INTO users VALUES (1, 'laitue', 'laitue@fake.com') \n INSERT INTO users VALUES (2, 'carotte', 'kakarot@fake.db') \n] \n} \n}"
                }
            },
            "thens":[
                {
                    "sentence":"Select all users",
                    "implementation":{
                        "task":"{\n type: sql \n target: test_sql \n inputs: {\n statements: [\n SELECT * FROM users \n] \n} \n}"
                    }
                }
            ]
        }  
        """.trimIndent()
    )
  }
  When("The scenario is executed") {
    executeScenario("scenarioId".spEL,"SQL_ENV_OK")
  }
  Then("the report status is SUCCESS") {
    checkScenarioReportSuccess()
  }
  And("The report contains record results"){
    CompareAction(
        mode = "equals",
        actual = "json(#report, \"$.report.steps[-1:].stepOutputs.recordResult\").toString()".spEL,
        expected = "[[{\"affectedRows\":-1,\"headers\":[\"ID\",\"NAME\",\"EMAIL\"],\"rows\":[[1,\"laitue\",\"laitue@fake.com\"],[2,\"carotte\",\"kakarot@fake.db\"]],\"columns\":[{\"name\":\"ID\",\"index\":0},{\"name\":\"NAME\",\"index\":1},{\"name\":\"EMAIL\",\"index\":2}],\"records\":[{\"cells\":[{\"column\":{\"name\":\"ID\",\"index\":0},\"value\":1},{\"column\":{\"name\":\"NAME\",\"index\":1},\"value\":\"laitue\"},{\"column\":{\"name\":\"EMAIL\",\"index\":2},\"value\":\"laitue@fake.com\"}]},{\"cells\":[{\"column\":{\"name\":\"ID\",\"index\":0},\"value\":2},{\"column\":{\"name\":\"NAME\",\"index\":1},\"value\":\"carotte\"},{\"column\":{\"name\":\"EMAIL\",\"index\":2},\"value\":\"kakarot@fake.db\"}]}]}]]"
    )
  }
  And("the report contains firstRow output"){
    CompareAction(
        mode = "equals",
        actual = "json(#report, \"$.report.steps[-1:].stepOutputs.firstRow\").toString()".spEL,
        expected = "[{\"cells\":[{\"column\":{\"name\":\"ID\",\"index\":0},\"value\":1},{\"column\":{\"name\":\"NAME\",\"index\":1},\"value\":\"laitue\"},{\"column\":{\"name\":\"EMAIL\",\"index\":2},\"value\":\"laitue@fake.com\"}]}]"
    )
  }
  And("the report contains rows output"){
    CompareAction(
        mode = "equals",
        actual = "json(#report, \"\$.report.steps[-1:].stepOutputs.rows\").toString()".spEL,
        expected = "[{\"rows\":[{\"cells\":[{\"column\":{\"name\":\"ID\",\"index\":0},\"value\":1},{\"column\":{\"name\":\"NAME\",\"index\":1},\"value\":\"laitue\"},{\"column\":{\"name\":\"EMAIL\",\"index\":2},\"value\":\"laitue@fake.com\"}]},{\"cells\":[{\"column\":{\"name\":\"ID\",\"index\":0},\"value\":2},{\"column\":{\"name\":\"NAME\",\"index\":1},\"value\":\"carotte\"},{\"column\":{\"name\":\"EMAIL\",\"index\":2},\"value\":\"kakarot@fake.db\"}]}]}]"
    )
  }
  And("the report contains affectedRows output"){
    CompareAction(
        mode = "equals",
        actual = "json(#report, \"\$.report.steps[-1:].stepOutputs.affectedRows\").toString()".spEL,
        expected = "[-1]"
    )
  }
}

val `Sql query wrong table` = Scenario(title = "Sql query success") {
  Given("A sql database target") {
    createSqlTarget("SQL_ENV_KO")
  }
  And("This scenario with sql task is saved") {
    createScenario("scenarioId",
        """
         {
         "when":{
              "sentence":"select unknown table",
              "implementation":{
                  "task":"{\n type: sql \n target: test_sql \n inputs: {\n statements: [\n SELECT * FROM unknownTable \n] \n} \n}"
              }
         },
         "thens":[]
         }
        """.trimIndent()
    )
  }
  When("The scenario is executed") {
    executeScenario("scenarioId".spEL,"SQL_ENV_KO")
  }
  Then("the report status is FAILURE") {
    checkScenarioReportFailure()
  }
}

private fun ChutneyStepBuilder.createSqlTarget(environmentName: String) {
  createEnvironment(environmentName,
      """
        [
            {
                "name": "test_sql",
                "url": "tcp://localhost:12345",
                "properties": [
                    { "key": "jdbcUrl", "value": "jdbc:h2:mem:fake-test;DB_CLOSE_DELAY=-1" },
                    { "key": "username", "value": "sa" },
                    { "key": "password", "value": "" }
                ]
            }
        ]
      """.trimIndent()
      )
}
