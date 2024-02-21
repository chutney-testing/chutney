??? info "Browse implementations"

    - [Accounting](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/radius/RadiusAccountingAction.java){:target="_blank"}
    - [Authenticate](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/radius/RadiusAuthenticateAction.java){:target="_blank"}

!!! note "Define a radius target"

    For all radius actions, target must defined these three properties (See [RadiusClient](https://github.com/ctran/TinyRadius/blob/master/src/main/java/org/tinyradius/util/RadiusClient.java){:target="_blank"} for more details)

    * sharedSecret
    * authenticatePort
    * accountingPort

```json title="Radius target example"
{
    "name": "RADIUS_TARGET",
    "url": "udp://my.radius.service:1211/",
    "properties": {
        "sharedSecret": "a_secret",
        "authenticatePort": "1812",
        "accountingPort": "1813"
    }
}
```

# Radius accounting
!!! info "[Browse implementations](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/radius/RadiusAccountingAction.java){:target="_blank"}"

=== "Inputs"

    | Required  | Name             | Type                 | Default |                             Comment                                                      |
    |:---------:|:-----------------|:---------------------|:-------:|:----------------------------------------------------------------------------------------:|
    |     *     | `target`         | String               |         |                                                                                          |
    |     *     | `userName`       | String               |         |                                                                                          |
    |     *     | `accountingType` | Integer              |         | [between 1 and 15](https://www.rfc-editor.org/rfc/rfc2866#section-5.1){:target="_blank"} |
    |           | `attributes`     | Map<String, String\> |         |                                                                                          |

=== "Outputs"

    |             Name | Type                                                                                                                                    |
    |-----------------:|:----------------------------------------------------------------------------------------------------------------------------------------|
    | `radiusResponse` | [RadiusPacket](https://github.com/ctran/TinyRadius/blob/master/src/main/java/org/tinyradius/packet/RadiusPacket.java){:target="_blank"} |


### Example

=== "Kotlin"
    ``` kotlin
    RadiusAccountingAction(
        target = "RADIUS_TARGET",
        userName = "iotUsername",
        attributes = mapOf(
          "Framed-IP-Address" to "123.456.789.1"
          "Acct-Session-Id" to "123456"
        ),
        accountingType = "1"
    )
    ```

# Radius authenticate
!!! info "[Browse implementations](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/radius/RadiusAuthenticateAction.java){:target="_blank"}"

Protocols `mschapv2` and `eap` are not yet supported by our client. See [AccessRequest.encodeRequestAttributes(...)](https://github.com/ctran/TinyRadius/blob/master/src/main/java/org/tinyradius/packet/AccessRequest.java){:target="_blank"}

=== "Inputs"

    | Required | Name           | Type                 | Default   |   Comment       |
    |:--------:|:---------------|:---------------------|:---------:|:---------------:|
    |    *     | `target`       | String               |           |                 |
    |    *     | `userName`     | String               |           |                 |
    |    *     | `userPassword` | String               |           |                 |
    |          | `protocol`     | String               |   `pap`   | `pap` or `chap` |
    |          | `attributes`   | Map<String, String\> |           |                 |

=== "Outputs"

    |             Name | Type                                                                                                                                    |
    |-----------------:|:----------------------------------------------------------------------------------------------------------------------------------------|
    | `radiusResponse` | [RadiusPacket](https://github.com/ctran/TinyRadius/blob/master/src/main/java/org/tinyradius/packet/RadiusPacket.java){:target="_blank"} |

### Example

=== "Kotlin"
    ``` kotlin
    RadiusAuthenticateAction(
        target = "RADIUS_TARGET",
        userName = "https://github.com/search?q=chutney",
        userPassword = "some content",
        protocol = "",
        attributes = mapOf(
          "NAS-identifier" to "NAS_OPERATOR"
        )
    )
    ```
