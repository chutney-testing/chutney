/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests.edition

import com.chutneytesting.acceptance.common.createScenario
import com.chutneytesting.kotlin.dsl.*

val `Request testcase edition` = Scenario(title = "Request testcase edition") {
  Given("A start date") {
    ContextPutAction(
        entries = mapOf(
            "startDate" to "now().toInstant()".spEL,
            "isoFormatter" to "isoDateFormatter('instant')".spEL
        )
    )
  }
  And("An existing testcase") {
    createScenario("testcaseId")
  }
  And("paloma requests an edition on an existing testcase") {
    HttpPostAction(
        target = "CHUTNEY_LOCAL_NO_USER",
        uri = "/api/v1/editions/testcases/${'$'}{#testcaseId}",
        body = null,
        headers = mapOf(
            "Authorization" to "Basic ${'$'}{T(java.util.Base64).getEncoder().encodeToString((\"paloma:paloma\").getBytes())}"
        ),
        validations = mapOf(
            statusValidation(200)
        )
    )
  }
  And("robert requests an edition on the same testcase") {
    HttpPostAction(
        target = "CHUTNEY_LOCAL_NO_USER",
        uri = "/api/v1/editions/testcases/${'$'}{#testcaseId}",
        body = null,
        headers = mapOf(
            "Content-Type" to "application/json;charset=UTF-8",
            "Authorization" to "Basic ${'$'}{T(java.util.Base64).getEncoder().encodeToString((\"robert:robert\").getBytes())}"
        ),
        validations = mapOf(
            statusValidation(200)
        )
    )
  }
  When("admin consult it") {
    HttpGetAction(
        target = "CHUTNEY_LOCAL_NO_USER",
        uri = "/api/v1/editions/testcases/${'$'}{#testcaseId}",
        headers = mapOf(
            "Content-Type" to "application/json;charset=UTF-8",
            "Authorization" to "Basic ${'$'}{T(java.util.Base64).getEncoder().encodeToString((\"admin:admin\").getBytes())}"
        ),
        validations = mapOf(
            statusValidation(200)
        ),
        outputs = mapOf(
            "currentEditions" to "body".spEL()
        )
    )
  }
  Then("paloma and robert are seen as current editors") {
    Step("Check paloma's edition") {
      JsonAssertAction(
          document = "jsonSerialize(#json(#currentEditions, \"${'$'}[?(@.editionUser=='paloma')]\").get(0))".spEL(),
          expected = mapOf(
              "$.testCaseId" to "testcaseId".spEL,
              "$.testCaseVersion" to "1",
              "$.editionStartDate" to "${'$'}isAfterDate:${'$'}{#isoFormatter.format(#startDate)}",
          )
      )
    }
    Step("Check robert's edition") {
      JsonAssertAction(
          document = "jsonSerialize(#json(#currentEditions, \"${'$'}[?(@.editionUser=='robert')]\").get(0))".spEL(),
          expected = mapOf(
              "$.testCaseId" to "testcaseId".spEL,
              "$.testCaseVersion" to "1",
              "$.editionStartDate" to "${'$'}isAfterDate:${'$'}{#isoFormatter.format(#startDate)}",
          )
      )
    }
  }
}

val `Request for a second time testcase edition` = Scenario(title = "Request for a second time testcase edition") {
  Given("A start date") {
    ContextPutAction(
        entries = mapOf(
            "startDate" to "now().toInstant()".spEL,
            "isoFormatter" to "isoDateFormatter('instant')".spEL
        )
    )
  }
  And("An existing testcase") {
    createScenario("testcaseId")
  }
  And("paloma requests an edition on an existing testcase") {
    HttpPostAction(
        target = "CHUTNEY_LOCAL_NO_USER",
        uri = "/api/v1/editions/testcases/${'$'}{#testcaseId}",
        body = null,
        headers = mapOf(
            "Authorization" to "Basic ${'$'}{T(java.util.Base64).getEncoder().encodeToString((\"paloma:paloma\").getBytes())}"
        ),
        validations = mapOf(
            statusValidation(200)
        ),
        outputs = mapOf(
            "firstEdition" to "body".spEL()
        )
    )
  }
  When ("paloma requests an edition on the same testcase") {
    HttpPostAction(
        target = "CHUTNEY_LOCAL_NO_USER",
        uri = "/api/v1/editions/testcases/${'$'}{#testcaseId}",
        body = null,
        headers = mapOf(
            "Authorization" to "Basic ${'$'}{T(java.util.Base64).getEncoder().encodeToString((\"paloma:paloma\").getBytes())}"
        ),
        validations = mapOf(
            statusValidation(200)
        ),
        outputs = mapOf(
            "secondEdition" to "body".spEL()
        )
    )
  }
  Then("The edition received is the first one") {
    JsonCompareAction(
        document1 = "firstEdition".spEL,
        document2 = "secondEdition".spEL
    )
  }
}

val `End testcase edition` = Scenario(title = "End testcase edition") {
  Given("A start date") {
    ContextPutAction(
        entries = mapOf(
            "startDate" to "now().toInstant()".spEL,
            "isoFormatter" to "isoDateFormatter('instant')".spEL
        )
    )
  }
  And("An existing testcase") {
    createScenario("testcaseId")
  }
  And("Paloma requests an edition on an existing testcase") {
    HttpPostAction(
        target = "CHUTNEY_LOCAL_NO_USER",
        uri = "/api/v1/editions/testcases/${'$'}{#testcaseId}",
        body = null,
        headers = mapOf(
            "Authorization" to "Basic ${'$'}{T(java.util.Base64).getEncoder().encodeToString((\"paloma:paloma\").getBytes())}"
        ),
        validations = mapOf(
            statusValidation(200)
        )
    )
  }
  When ("Paloma ends its edition") {
    HttpDeleteAction(
        target = "CHUTNEY_LOCAL_NO_USER",
        uri = "/api/v1/editions/testcases/${'$'}{#testcaseId}",
        headers = mapOf(
            "Authorization" to "Basic ${'$'}{T(java.util.Base64).getEncoder().encodeToString((\"paloma:paloma\").getBytes())}"
        ),
        validations = mapOf(
            statusValidation(200)
        )
    )
  }
  Then("Paloma cannot be seen as current editor") {
    Step("Consults the current editions of this testcase") {
      HttpGetAction(
          target = "CHUTNEY_LOCAL",
          uri = "/api/v1/editions/testcases/${'$'}{#testcaseId}",
          validations = mapOf(
              statusValidation(200)
          ),
          outputs = mapOf(
              "currentEditions" to "body".spEL()
          )
      )
    }
    Step("Check paloma's edition inexistence") {
      JsonAssertAction(
          document = "currentEditions".spEL,
          expected = mapOf(
              "$[?(@.editionUser=='paloma')]" to "${'$'}isNull",
          )
      )
    }
  }

}

val `Edition time to live` = Scenario(title = "Edition time to live") {
  Given("An existing testcase") {
    createScenario("testcaseId")
  }
  And("Paloma requests an edition on an existing testcase") {
    HttpPostAction(
        target = "CHUTNEY_LOCAL_NO_USER",
        uri = "/api/v1/editions/testcases/${'$'}{#testcaseId}",
        body = null,
        headers = mapOf(
            "Authorization" to "Basic ${'$'}{T(java.util.Base64).getEncoder().encodeToString((\"paloma:paloma\").getBytes())}"
        ),
        validations = mapOf(
            statusValidation(200)
        )
    )
  }
  When ("edition lasts beyond defined ttl of 2 seconds") {
    SleepAction("2500 ms")
  }
  Then("Paloma cannot be seen as current editor") {
    Step("Consults the current editions of this testcase") {
      HttpGetAction(
          target = "CHUTNEY_LOCAL",
          uri = "/api/v1/editions/testcases/${'$'}{#testcaseId}",
          validations = mapOf(
              statusValidation(200)
          ),
          outputs = mapOf(
              "currentEditions" to "body".spEL()
          )
      )
    }
    Step("Check paloma's edition inexistence") {
      JsonAssertAction(
          document = "currentEditions".spEL,
          expected = mapOf(
              "$[?(@.editionUser=='paloma')]" to "${'$'}isNull",
          )
      )
    }
  }
}