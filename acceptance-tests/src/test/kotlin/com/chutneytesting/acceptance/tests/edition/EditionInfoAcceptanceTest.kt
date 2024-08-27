/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests.edition

import com.chutneytesting.kotlin.dsl.*

val readTestCaseMetadataScenario = Scenario(title = "Consult new testcase metadata") {
  Given("A start date") {
    ContextPutAction(
        entries = mapOf(
            "startDate" to "now().toInstant()".spEL,
            "isoFormatter" to "isoDateFormatter('instant')".spEL
        )
    )
  }
  And("robert has created a testcase with metadata") {
    HttpPostAction(
        target = "CHUTNEY_LOCAL_NO_USER",
        uri = "/api/scenario/v2",
        body = """
                {
                    "title": "titre",
                    "description": "a testcase",
                    "tags": [ "first", "second" ],
                    "author": "notCreator",
                    "creationDate": "2020-01-01T12:00:03Z",
                    "updateDate": "2020-02-02T12:00:03Z",
                    "version": 111,
                    "scenario":{
                        "when":{},
                        "thens":[]
                    }
                }
                """,
        headers = mapOf(
            "Content-Type" to "application/json;charset=UTF-8",
            "Authorization" to "Basic ${'$'}{T(java.util.Base64).getEncoder().encodeToString((\"robert:robert\").getBytes())}"
        ),
        validations = mapOf(
            statusValidation(200)
        ),
        outputs = mapOf(
            "testcaseId" to "body".spEL()
        )
    )
  }
  When("admin consult it") {
    HttpGetAction(
        target = "CHUTNEY_LOCAL_NO_USER",
        uri = "/api/scenario/v2/${'$'}{#testcaseId}",
        headers = mapOf(
            "Content-Type" to "application/json;charset=UTF-8",
            "Authorization" to "Basic ${'$'}{T(java.util.Base64).getEncoder().encodeToString((\"admin:admin\").getBytes())}"
        ),
        validations = mapOf(
            statusValidation(200)
        ),
        outputs = mapOf(
            "scenario" to "body".spEL()
        )
    )
  }
  Then("Check testcase metadata") {
    JsonAssertAction(
        document = "body".spEL(),
        expected = mapOf(
            "$.title" to "titre",
            "$.description" to "a testcase",
            "$.tags" to "json('[\"FIRST\",\"SECOND\"]', '$')".spEL,
            "$.creationDate" to "${'$'}isEqualDate:2020-01-01T12:00:03Z",
            "$.author" to "robert",
            "$.updateDate" to "${'$'}isAfterDate:${'$'}{#isoFormatter.format(#startDate)}",
            "$.version" to "111"

        )
    )
  }
}

val readTestCaseAfterUpdateScenario = Scenario(title = "Consult testcase metadata after update") {
  Given("A start date") {
    ContextPutAction(
        entries = mapOf(
            "startDate" to "now().toInstant()".spEL,
            "isoFormatter" to "isoDateFormatter('instant')".spEL
        )
    )
  }
  And("robert has created a testcase with metadata") {
    HttpPostAction(
        target = "CHUTNEY_LOCAL_NO_USER",
        uri = "/api/scenario/v2",
        body = """
                {
                    "title": "titre",
                    "description": "a testcase",
                    "tags": [ "first", "second" ],
                    "author": "notCreator",
                    "creationDate": "2020-01-01T12:00:03Z",
                    "updateDate": "2020-02-02T12:00:03Z",
                    "scenario":{
                        "when":{},
                        "thens":[]
                    }
                }
                """,
        headers = mapOf(
            "Content-Type" to "application/json;charset=UTF-8",
            "Authorization" to "Basic ${'$'}{T(java.util.Base64).getEncoder().encodeToString((\"robert:robert\").getBytes())}"
        ),
        validations = mapOf(
            statusValidation(200)
        ),
        outputs = mapOf(
            "testcaseId" to "body".spEL()
        )
    )
  }
  And("Paloma has updated it with metadata") {
    HttpPatchAction(
        target = "CHUTNEY_LOCAL_NO_USER",
        uri = "/api/scenario/v2",
        body = """
                 {
                    "id": "${'$'}{#testcaseId}",
                    "title": "new Title",
                    "description": "new description",
                    "tags": [ "second", "third" ],
                    "author": "notEditor",
                    "creationDate": "2020-06-01T14:00:00Z",
                    "updateDate": "2001-01-01T00:00:00Z",
                    "version": 1,
                    "scenario":{
                        "when":{},
                        "thens":[]
                    }
                }
                """,
        headers = mapOf(
            "Content-Type" to "application/json;charset=UTF-8",
            "Authorization" to "Basic ${'$'}{T(java.util.Base64).getEncoder().encodeToString((\"paloma:paloma\").getBytes())}"
        ),
        validations = mapOf(
            statusValidation(200)
        ),
        outputs = mapOf(
            "testcaseId" to "body".spEL()
        )
    )
  }
  When("admin consult it") {
    HttpGetAction(
        target = "CHUTNEY_LOCAL_NO_USER",
        uri = "/api/scenario/v2/${'$'}{#testcaseId}",
        headers = mapOf(
            "Content-Type" to "application/json;charset=UTF-8",
            "Authorization" to "Basic ${'$'}{T(java.util.Base64).getEncoder().encodeToString((\"admin:admin\").getBytes())}"
        ),
        validations = mapOf(
            statusValidation(200)
        ),
        outputs = mapOf(
            "scenario" to "body".spEL()
        )
    )
  }
  Then("Check testcase metadata") {
    JsonAssertAction(
        document = "body".spEL(),
        expected = mapOf(
            "$.title" to "new Title",
            "$.description" to "new description",
            "$.tags" to "json('[\"SECOND\",\"THIRD\"]', '$')".spEL,
            "$.creationDate" to "${'$'}isEqualDate:2020-01-01T12:00:03Z",
            "$.author" to "paloma",
            "$.updateDate" to "${'$'}isAfterDate:${'$'}{#isoFormatter.format(#startDate)}",
            "$.version" to "2"
        )
    )
  }
}

val updateTestCaseWithBadVersionScenario = Scenario(title = "Update testcase with wrong version") {
  Given("robert has created a testcase with metadata") {
    HttpPostAction(
        target = "CHUTNEY_LOCAL_NO_USER",
        uri = "/api/scenario/v2",
        body = """
                {
                    "title": "titre",
                    "description": "a testcase",
                    "tags": [ "first", "second" ],
                    "author": "notCreator",
                    "creationDate": "2020-01-01T12:00:03Z",
                    "updateDate": "2020-02-02T12:00:03Z",
                    "version": 111,
                    "scenario":{
                        "when":{},
                        "thens":[]
                    }
                }
                """,
        headers = mapOf(
            "Content-Type" to "application/json;charset=UTF-8",
            "Authorization" to "Basic ${'$'}{T(java.util.Base64).getEncoder().encodeToString((\"robert:robert\").getBytes())}"
        ),
        validations = mapOf(
            statusValidation(200)
        ),
        outputs = mapOf(
            "testcaseId" to "body".spEL()
        )
    )
  }
  When("Paloma has updated it with wrong version") {
    HttpPatchAction(
        target = "CHUTNEY_LOCAL_NO_USER",
        uri = "/api/scenario/v2",
        body = """
                 {
                    "id": "${'$'}{#testcaseId}",
                    "title": "new Title",
                    "description": "new description",
                    "tags": [ "second", "third" ],
                    "author": "notEditor",
                    "creationDate": "2020-06-01T14:00:00Z",
                    "updateDate": "2001-01-01T00:00:00Z",
                    "version": 42,
                    "scenario":{
                        "when":{},
                        "thens":[]
                    }
                }
                """,
        headers = mapOf(
            "Content-Type" to "application/json;charset=UTF-8",
            "Authorization" to "Basic ${'$'}{T(java.util.Base64).getEncoder().encodeToString((\"paloma:paloma\").getBytes())}"
        ),
        validations = mapOf(
            statusValidation(404)
        ),
        outputs = mapOf(
            "testcaseId" to "body".spEL()
        )
    )
  }
  Then("message contains \"version [42] not found\"") {
    CompareAction(
        mode = "contains",
        actual = "json(#body, '$')".spEL,
        expected = "version [42] not found"
    )
  }
}