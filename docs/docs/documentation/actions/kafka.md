??? info "Browse implementations"

    - [Publish](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/kafka/KafkaBasicPublishAction.java){:target="_blank"}
    - [Consume](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/kafka/KafkaBasicConsumeAction.java){:target="_blank"}

!!! important "Target Configuration"
    For all actions, a target should be defined and have a `name` and a valid `url`.

=== "minimal config"
    ```json title="Kafka target example"
    {
      "name": "my_kafka_target"
      "url": "tcp://localhost:60962"
    }
    ```

=== "extended config"
    ```json title="Kafka target example"
    {
        "name":"KAFKA",
        "url":"tcp://kafka.server.fr:9095",
        "properties":[
            {
            "key":"ssl.keystore.location",
            "value":"/keystores/keys.jks"
            },
            {
            "key":"security.protocol",
            "value":"SSL"
            },
            {
            "key":"ssl.keystore.password",
            "value":"password"
            },
            {
            "key":"ssl.truststore.password",
            "value":"password"
            },
            {
            "key":"ssl.truststore.location",
            "value":"/truststores/trust.jks"
            },
            {
            "key":"auto.offset.reset",
            "value":"earliest"
            },
            {
            "key":"enable.auto.commit",
            "value":"true"
            }
        ]
    }
    ```

# Publish
!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/kafka/KafkaBasicPublishAction.java){:target="_blank"}"

Use this action to publish a message to a Kafka topic.

=== "Inputs"

    | Required | Name         | Type                             | Description                                                                                                                                                                                                                    |
    |:--------:|:-------------|:---------------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
    |    *     | `target`     | String                           | Kafka target name                                                                                                                                                                                                              |
    |    *     | `topic`      | String                           | Topic where the message will be published                                                                                                                                                                                      |
    |    *     | `headers`    | Map <String, String\>            | Headers to be sent with the request                                                                                                                                                                                            |
    |    *     | `payload`    | String                           | Message to be published                                                                                                                                                                                                        |
    |          | `properties` | Map <String, String\>            | [Configurations](https://github.com/apache/kafka/blob/trunk/clients/src/main/java/org/apache/kafka/clients/producer/ProducerConfig.java#:~:text=CONFIG%20%3D%20new,TRANSACTIONAL_ID_DOC){:target="_blank"} for Kafka producer. |

=== "Outputs"

    |    Name   | Type     | Description                           |
    |:----------|:---------|:--------------------------------------|
    | `payload` | String   | Sent message                          |
    | `headers` | String   | Headers to be sent with the request   |

### Example

=== "Kotlin"
``` kotlin
KafkaBasicPublishTask(
    target = "my_kafka_target",
    topic = "my.queue",
    headers = mapOf(
        "contentType" to "application/json",
        "season" to "1"
    ),
    payload = """
                {
                  "title": "Castle in the Sky",
                  "director": "Hayao Miyazaki",
                  "rating": 78
                }
               """.trimIndent()
)
```

# Consume
!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/kafka/KafkaBasicConsumeAction.java){:target="_blank"}"

Use this action to consume a message from a Kafka topic.

=== "Inputs"

    | Required | Name              | Type                                                                                                                                     | Default                        | Description                                                                                                                                                                                                                                                                           |
    |:--------:|:------------------|:-----------------------------------------------------------------------------------------------------------------------------------------|:-------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
    |    *     | `target`          | String                                                                                                                                   | Kafka target name              |                                                                                                                                                                                                                                                                                       |
    |    *     | `topic`           | String                                                                                                                                   |                                | Topic from where the message will be consumed                                                                                                                                                                                                                                         |
    |    *     | `group`           | String                                                                                                                                   |                                | Group id of the consumer                                                                                                                                                                                                                                                              |
    |          | `properties`      | Map <String, String\>                                                                                                                    |                                | [Configurations](https://github.com/apache/kafka/blob/trunk/clients/src/main/java/org/apache/kafka/clients/producer/ProducerConfig.java#:~:text=CONFIG%20%3D%20new,TRANSACTIONAL_ID_DOC){:target="_blank"} for Kafka producer.                                                        |
    |          | `header-selector` | String                                                                                                                                   |                                | Consume only messages whose **headers** match this selector. Selector must be a [json paths](https://github.com/json-path/JsonPath){:target="_blank"}. The root node is message's headers                                                                                             |
    |          | `selector`        | String                                                                                                                                   |                                | Consume only messages whose **headers** or **payload** match this selector. Selector must be [json paths](https://github.com/json-path/JsonPath){:target="_blank"} or [xml paths](https://www.w3schools.com/xml/xml_xpath.asp){:target="_blank"}. The root node is the whole message. |
    |          | `nb-messages`     | Integer                                                                                                                                  | 1                              | How many messages to be consumed                                                                                                                                                                                                                                                      |
    |          | `content-type`    | String                                                                                                                                   | `application/json`             | To be consumed message's content type                                                                                                                                                                                                                                                 |
    |          | `timeout`         | [Duration](/documentation/actions/other/#duration-type)                                                                                  | `60 sec`                       | Listening time on the topic                                                                                                                                                                                                                                                           |
    |          | `ackMode`         | [AckMode](https://docs.spring.io/spring-kafka/api/org/springframework/kafka/listener/ContainerProperties.AckMode.html){:target="_blank"} | target's ackMode, else `BATCH` | The offset commit behavior                                                                                                                                                                                                                                                            |

=== "Outputs"

    |    Name    | Type                                 | Description                |
    |:-----------|:-------------------------------------|:---------------------------|
    | `body`     | List<Map<String,Object\>\>           | Consumed messages          |
    | `payloads` | List<String\>                        | Consumed messages payloads |
    | `headers`  | List<String\>                        | Consumed messages headers  |

### Example

=== "Kotlin"
``` kotlin
KafkaBasicConsumeAction(
    target = "my_kafka_target",
    topic = "my.queue",
    group= "my.group",
    timeout= "10 sec",
    selector= "\$..[?(\$.payload.title==\"Castle in the Sky\")]",
    headerSelector= "\$..[?(\$.season=='1')]",
    contentType= "application/json"
)
```
