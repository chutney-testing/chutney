??? info "Browse implementations"

    - [GET](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/http/HttpGetAction.java){:target="_blank"}
    - [POST](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/http/HttpPostAction.java){:target="_blank"}
    - [PUT](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/http/HttpPutAction.java){:target="_blank"}
    - [DELETE](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/http/HttpDeleteAction.java){:target="_blank"}
    - [PATCH](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/http/HttpPatchAction.java){:target="_blank"}
    - [Start mock server](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/http/HttpsServerStartAction.java){:target="_blank"}
    - [Listened routes](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/http/HttpsListenerAction.java){:target="_blank"}

!!! note "How to request a secured service"

    For all HTTP actions, authentication or secured connexion to a service is configured by setting properties on the **target**.

    * Basic Authentication:
        * To use a basic authorization header, set properties `username` and `password` on the target.

    * SSL:
        * To use an SSL context, set a `keystore` or `truststore` one the target.  
        Properties `keystore`, `keystorePassword`, `keyPassword`, `truststore` and `truststorePassword` could be alternatively used.

    * Proxy, 2 solutions: 
        * You can set a system property **http.proxyHost** or **https.proxyHost**, the HTTP client will use this as the default route planner.
        * You can set a target property `proxy`
            * Default port value is 3128.
            * Target property `proxy` override system property if set


```json title="Http target example"
{
    "name": "ghibli_movies_http_service",
    "url": "https://my.http.service:443/",
    "properties": {
        "username": "myUsername",
        "user": "myUsername", // (1)
        "userPassword": "myPassword",
        "password": "myPassword", // (2)
        "trustStore": "/home/APP/security/mytruststore.jks",
        "trustStorePassword": "myTrustStorePassword",
        "keyStore": "/home/APP/security/mykeyStore.jks",
        "keyStorePassword": "mykeyStorePassword",
        "keyPassword": "myKeyStoreKeyPassword",
        "proxy": "https://myproxy:3128"
    }
}
```

1. `username` or `user` to define user for basic authentification 
2. `userPassword` or `password` to define password for basic authentification

# Get
!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/http/HttpGetAction.java){:target="_blank"}"

=== "Inputs"

    | Required | Name      | Type                                                             | Default   |
    |:--------:|:----------|:-----------------------------------------------------------------|:---------:|
    |    *     | `target`  | String                                                           |           |
    |    *     | `uri`     | String                                                           |           |
    |          | `headers` | Map<String, String\>                                             |           |
    |          | `timeout` | [Duration](/documentation/actions/other/#duration-type) (String) | "2000 ms" |

=== "Outputs"

    |      Name | Type                                                                                                                                        |
    |:---------:|:--------------------------------------------------------------------------------------------------------------------------------------------|
    |  `status` | int                                                                                                                                         |
    |  `body`   | String                                                                                                                                      |
    | `headers` | [HttpHeaders](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/HttpHeaders.html){:target="_blank"} |

### Example

=== "Kotlin"
    ``` kotlin
    HttpGetAction(
        target = "HTTP_TARGET",
        uri = "https://github.com/search?q=chutney",
        headers = mapOf(
          "Content-Type" to "application/json"
        ),
        timeout = "1 sec"
    )
    ```

# Post
!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/http/HttpPostAction.java){:target="_blank"}"

=== "Inputs"

    | Required | Name      | Type                                                             |  Default   |
    |:--------:|:----------|:-----------------------------------------------------------------|:----------:|
    |    *     | `target`  | String                                                           |            |
    |    *     | `uri`     | String                                                           |            |
    |          | `body`    | String                                                           | `{}`       |
    |          | `headers` | Map<String, String\>                                             |            |
    |          | `timeout` | [Duration](/documentation/actions/other/#duration-type) (String) | "2000 ms"  |

=== "Outputs"

    |      Name | Type                                                                                                                                        |
    |----------:|:--------------------------------------------------------------------------------------------------------------------------------------------|
    |  `status` | int                                                                                                                                         |
    |    `body` | String                                                                                                                                      |
    | `headers` | [HttpHeaders](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/HttpHeaders.html){:target="_blank"} |

### Example

=== "Kotlin"
    ``` kotlin
    HttpPostAction(
        target = "HTTP_TARGET",
        uri = "https://github.com/search?q=chutney",
        body = "some content",
        headers = mapOf(
          "Content-Type" to "application/json"
        ),
    )
    ```

# Put
!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/http/HttpPutAction.java){:target="_blank"}"

=== "Inputs"

    | Required | Name      | Type                                                             |  Default   |
    |:--------:|:----------|:-----------------------------------------------------------------|:----------:|
    |    *     | `target`  | String                                                           |            |
    |    *     | `uri`     | String                                                           |            |
    |          | `body`    | String                                                           | `{}`       |
    |          | `headers` | Map<String, String\>                                             |            |
    |          | `timeout` | [Duration](/documentation/actions/other/#duration-type) (String) | "2000 ms"  |

=== "Outputs"

    |      Name | Type                                                                                                                                        |
    |----------:|:--------------------------------------------------------------------------------------------------------------------------------------------|
    |  `status` | int                                                                                                                                         |
    |    `body` | String                                                                                                                                      |
    | `headers` | [HttpHeaders](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/HttpHeaders.html){:target="_blank"} |

### Example

=== "Kotlin"
    ``` kotlin
    HttpPutAction(
        target = "HTTP_TARGET",
        uri = "https://github.com/search?q=chutney",
        body = "some content",
        headers = mapOf(
          "Content-Type" to "application/json"
        ),
    )
    ```

# Delete
!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/http/HttpDeleteAction.java){:target="_blank"}"

=== "Inputs"

    | Required | Name      | Type                                                             |  Default   |
    |:--------:|:----------|:-----------------------------------------------------------------|:----------:|
    |    *     | `target`  | String                                                           |            |
    |    *     | `uri`     | String                                                           |            |
    |          | `headers` | Map<String, String\>                                             |            |
    |          | `timeout` | [Duration](/documentation/actions/other/#duration-type) (String) | "2000 ms"  |

=== "Outputs"

    |      Name | Type                                                                                                                                        |
    |----------:|:--------------------------------------------------------------------------------------------------------------------------------------------|
    |  `status` | int                                                                                                                                         |
    |    `body` | String                                                                                                                                      |
    | `headers` | [HttpHeaders](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/HttpHeaders.html){:target="_blank"} |


### Example

=== "Kotlin"
    ``` kotlin
    HttpDeleteAction(
        target = "HTTP_TARGET",
        uri = "https://github.com/search?q=chutney",
        headers = mapOf(
          "Content-Type" to "application/json"
        ),
    )
    ```

# Patch
!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/http/HttpPatchAction.java){:target="_blank"}"

=== "Inputs"

    | Required | Name      | Type                                                             |  Default   |
    |:--------:|:----------|:-----------------------------------------------------------------|:----------:|
    |    *     | `target`  | String                                                           |            |
    |    *     | `uri`     | String                                                           |            |
    |          | `body`    | String                                                           | `{}`       |
    |          | `headers` | Map<String, String\>                                             |            |
    |          | `timeout` | [Duration](/documentation/actions/other/#duration-type) (String) | "2000 ms"  |

=== "Outputs"

    |      Name | Type                                                                                                                                        |
    |----------:|:--------------------------------------------------------------------------------------------------------------------------------------------|
    |  `status` | int                                                                                                                                         |
    |    `body` | String                                                                                                                                      |
    | `headers` | [HttpHeaders](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/HttpHeaders.html){:target="_blank"} |

### Example

=== "Kotlin"
    ``` kotlin
    HttpPatchAction(
        target = "HTTP_TARGET",
        uri = "https://github.com/search?q=chutney",
        body = "some content",
        headers = mapOf(
          "Content-Type" to "application/json"
        ),
    )
    ```
# Mocking an HTTP Server

## Start
!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/http/HttpsServerStartAction.java){:target="_blank"}"

=== "Inputs"

    | Required | Name                  | Type   | Default |
    |:--------:|:----------------------|:-------|:-------:|
    |          | `port`                | String | "8443"  |
    |          | `truststore-path`     | String |         |
    |          | `truststore-password` | String |         |
    |          | `keystore-path`       | String |         |
    |          | `keystore-password`   | String |         |

=== "Outputs"

    |         Name | Type                                                                                                                                                   |
    |-------------:|:-------------------------------------------------------------------------------------------------------------------------------------------------------|
    | httpsServer  | [WireMockServer](https://github.com/wiremock/wiremock/blob/master/src/main/java/com/github/tomakehurst/wiremock/WireMockServer.java){:target="_blank"} |

### Example

=== "Kotlin"
    ``` kotlin
    HttpsServerStartAction(
        port = "8443",
        trustStorePath = "/tmp/trustore.jks",
        trustStorePassword = "password",
        keyStorePath = "/user/admin/keystore",
        keyStorePassword = "keystorepassword",
        keyPassword = "passwordkey"
    )
    ```

!!! note
    This action automatically registers a teardown to stop the server at the end of the scenario.

## Listened routes
!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/http/HttpsListenerAction.java){:target="_blank"}"

This actions allows you to check which requests have been received by a wiremock server.  
Available verbs are : `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `OPTIONS`, `HEAD`, `TRACE`, `ANY`

!!! warning
    If this action succeeds, requests will be removed from the Wiremock server.

=== "Inputs"

    | Required | Name                     | Type                                                                                                                                                      | Default |
    |:--------:|:-------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------|:-------:|
    |    *     | `https-server`           | [WireMockServer](https://www.javadoc.io/doc/com.github.tomakehurst/wiremock/latest/com/github/tomakehurst/wiremock/WireMockServer.html){:target="_blank"} |         |
    |    *     | `uri`                    | String ([regex](https://wiremock.org/docs/request-matching/#:~:text=strategy%20in%20detail.-,URL%20matching,-URLs%20can%20be){:target=_blank})            |         |
    |    *     | `verb`                   | String                                                                                                                                                    |         |
    |          | `expected-message-count` | String                                                                                                                                                    |    1    |

=== "Outputs"

    |      Name | Type                                                                                                                                                                        |
    |----------:|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
    | requests  | List<[LoggedRequest](https://www.javadoc.io/doc/com.github.tomakehurst/wiremock/latest/com/github/tomakehurst/wiremock/verification/LoggedRequest.html){:target="_blank"}\> |

### Example

=== "Kotlin"
    ``` kotlin
    HttpsListenerAction(
        httpServerVarName = "\${#httpsServer}",
        uri = "https://github.com/search?q=chutney",
        verb = "GET",
        expectedMessageCount = 1,
    )
    ```
