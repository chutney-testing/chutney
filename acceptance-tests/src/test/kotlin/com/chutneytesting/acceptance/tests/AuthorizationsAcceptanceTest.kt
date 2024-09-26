/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests

import com.chutneytesting.acceptance.common.jsonHeader
import com.chutneytesting.kotlin.dsl.*

val `Declare a new role with its authorizations` = Scenario(title = "Declare a new role with its authorizations") {
  Given("A new role") {
    ContextPutAction(
        entries = mapOf(
            "newRole" to "jsonPath('{\"name\": \"NEW_ROLE\", \"rights\":[\"SCENARIO_READ\", \"CAMPAIGN_READ\", \"SCENARIO_WRITE\"]}', \"$\")".spEL,
        )
    )
  }
  When("Add it to current roles and authorizations") {
    Step("Ask for current roles") {
      HttpGetAction(
          target = "CHUTNEY_LOCAL",
          uri = "/api/v1/authorizations",
          validations = mapOf(statusValidation(200)),
          outputs = mapOf("currentAuthorizations" to "body".spEL())
      )
    }
    Step("Add the new role") {
      HttpPostAction(
          target = "CHUTNEY_LOCAL",
          uri = "/api/v1/authorizations",
          headers = jsonHeader(),
          body = """
          {
              "roles": ${'$'}{#jsonPath(#currentAuthorizations, '$.roles').appendElement(#newRole)},
              "authorizations": ${'$'}{#jsonPath(#currentAuthorizations, '$.authorizations')}
          }  
          """.trimIndent(),
          validations = mapOf(statusValidation(200)),
      )
    }

  }
  Then("It must be read back") {

    Step("Ask for current roles and authorizations") {
      HttpGetAction(
          target = "CHUTNEY_LOCAL",
          uri = "/api/v1/authorizations",
          validations = mapOf(statusValidation(200)),
          outputs = mapOf("readAuthorizations" to "body".spEL())
      )
    }
    Step("Validate the new role existence") {
      JsonAssertAction(
          document = "readAuthorizations".spEL(),
          expected = mapOf(
              "$.roles[?(@.name=='NEW_ROLE')].rights" to "${'$'}value:${'$'}{#jsonPath(#newRole, \"${'$'}.rights\")}",
          )
      )
    }
  }
}

val `Add and remove user to-from an existing role` = Scenario(title = "Add and remove user to/from an existing role") {
  Given("A role not given to user") {
    Step("Variable definition") {
      ContextPutAction(
          entries = mapOf(
              "userName" to "user",
              "roleNameWithNoUser" to "NO_USER_ROLE",
          ),
          outputs = mapOf("roleUserAuthorizations" to "jsonPath('{\"name\": \"'+#roleNameWithNoUser+'\", \"users\":[\"'+#userName+'\"]}', \"$\")".spEL())
      )
    }
    Step("for current roles and authorizations") {
      HttpGetAction(
          target = "CHUTNEY_LOCAL",
          uri = "/api/v1/authorizations",
          validations = mapOf(statusValidation(200)),
          outputs = mapOf("currentAuthorizations" to "body".spEL())
      )
    }
    Step("Validate the role") {
      JsonAssertAction(
          document = "currentAuthorizations".spEL(),
          expected = mapOf(
              "$.roles[?(@.name=='${'$'}{#roleNameWithNoUser}')]" to "${'$'}isNotNull",
              "\$.authorizations[?(@.name=='${'$'}{#roleNameWithNoUser}')].users" to "${'$'}isEmpty"
          ),
          outputs = mapOf("roleAuthorizations" to "jsonPath(#currentAuthorizations, \"\$.roles[?(@.name=='\"+#roleNameWithNoUser+\"')].rights[*]\")".spEL())
      )
    }

  }
  When("Add user to role") {
    HttpPostAction(
        target = "CHUTNEY_LOCAL",
        uri = "/api/v1/authorizations",
        headers = jsonHeader(),
        body = """
          {
              "roles": ${'$'}{#jsonPath(#currentAuthorizations, '$.roles')},
              "authorizations": ${'$'}{#jsonPath(#currentAuthorizations, "$.authorizations[?(@.name!='"+#roleNameWithNoUser+"')]").appendElement(#roleUserAuthorizations)}
          }
          """.trimIndent(),
        validations = mapOf(statusValidation(200)),
    )
  }
  Then("It must be read back") {
    Step("Ask for current roles and authorizations") {
      HttpGetAction(
          target = "CHUTNEY_LOCAL",
          uri = "/api/v1/authorizations",
          validations = mapOf(statusValidation(200)),
          outputs = mapOf("readAuthorizations" to "body".spEL())
      )
    }
    Step("Validate the user existence") {
      JsonAssertAction(
          document = "readAuthorizations".spEL(),
          expected = mapOf(
              "$.authorizations[?(@.name=='${'$'}{#roleNameWithNoUser}')].users[0]" to "${'$'}value:${'$'}{#userName}",
          )
      )
    }
  }
  And("Check user authority") {
    Step("Get user") {
      HttpGetAction(
          target = "CHUTNEY_LOCAL_NO_USER",
          uri = "/api/v1/user",
          headers = jsonHeader() + mapOf(
              "Authorization" to "Basic ${'$'}{T(java.util.Base64).getEncoder().encodeToString((\"user:user\").getBytes())}"
          ),
          validations = mapOf(statusValidation(200)),
      )
    }
    Step("Check user authorizations") {
      JsonAssertAction(
          document = "body".spEL(),
          expected = mapOf(
              "$.authorizations" to "${'$'}{#roleAuthorizations}",
          )
      )
    }
  }
  And("Remove user from role") {
    HttpPostAction(
        target = "CHUTNEY_LOCAL",
        uri = "/api/v1/authorizations",
        headers = jsonHeader(),
        body = "currentAuthorizations".spEL,
        validations = mapOf(statusValidation(200)),
    )
  }
  And("Check user authority") {
    Step("Get user") {
      HttpGetAction(
          target = "CHUTNEY_LOCAL",
          uri = "/api/v1/user",
          validations = mapOf(statusValidation(200)),
      )
    }
    Step("Assert the role was removed") {
      JsonAssertAction(
          document = "body".spEL(),
          expected = mapOf(
              "\$.authorizations[?(@.name=='${'$'}{#roleNameWithNoUser}')].users[0]" to "${'$'}isEmpty",
          )
      )
    }
  }
}
