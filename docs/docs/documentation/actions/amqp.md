??? info "Browse implementations"

    - [Basic publish](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/amqp/AmqpBasicPublishAction.java){:target="_blank"}
    - [Basic consume](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/amqp/AmqpBasicConsumeAction.java){:target="_blank"}
    - [Basic get](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/amqp/AmqpBasicGetAction.java){:target="_blank"}
    - [Clean queues](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/amqp/AmqpCleanQueuesAction.java){:target="_blank"}
    - [Create and bind temporary queue](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/amqp/AmqpCreateBoundTemporaryQueueAction.java){:target="_blank"}
    - [Unbind queue](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/amqp/AmqpUnbindQueueAction.java){:target="_blank"}
    - [Delete queue](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/amqp/AmqpDeleteQueueAction.java){:target="_blank"}


!!! important "Target Configuration"
    For all actions, a AMQP target should be defined and have a `name` and a valid `url`.

```json title="Rabbitmq target"
{
    "name":"RABBITMQ_TARGET",
    "url":"amqp://localhost:5672",
    "properties": [
        {
            "key":"password",
            "value":"admin"
        },
        {
            "key":"username",
            "value":"admin"
        }
    ]
}
```
# Basic publish
!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/amqp/AmqpBasicPublishAction.java){:target="_blank"}"

Use this action to publish a message to an exchange.

=== "Inputs"

    | Required | Name            | Type                     |   Description   |
    |:--------:|:----------------|:-------------------------|:----------------|
    |    *     | `exchange-name` | String                   | The exchange to publish the message to. Must exist                  |
    |          | `routing-key`   | String                   | The routing key. See this [example](https://www.rabbitmq.com/tutorials/tutorial-five-python.html#:~:text=complex%20topic%20exchange.-,Topic%20exchange,-Messages%20sent%20to){:target=_blank} for more details.                                                     |
    |          | `headers`       | Map<String, Object\>     | Message headers                                                     |
    |          | `properties`    | Map<String, String\>     | Other message's [properties](https://rabbitmq.github.io/rabbitmq-java-client/api/current/com/rabbitmq/client/AMQP.BasicProperties.html){:target=_blank}. Actually only content_type property is handled. |
    |    *     | `payload`       | String                   |  Message content                                                    |

=== "Outputs"

    |      Name | Type   | Description     |
    |----------:|:-------|:----------------|
    | `payload` | String | Message content |
    | `headers` | String | Message headers |

### Example

=== "Kotlin"
    
    ``` kotlin
        AmqpBasicPublishAction(
            target = "RABBITMQ_TARGET",
            exchangeName = "my.exchange",
            routingKey = "children.fiction",
            headers = mapOf(
                "season" to "1",
            ),
            properties = mapOf(
                "content_type" to "application/json",
            ),
            payload = """
                    {
                    "title": "Castle in the Sky",
                    "director": "Hayao Miyazaki",
                    "rating": 78,
                    "category": "fiction"
                    }
                """.trimIndent(),
        )
    ```

# Basic consume
!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/amqp/AmqpBasicConsumeAction.java){:target="_blank"}"

Use this action to consume messages from a queue.

!!! warning "Parallel consume"
    * Only **one** queue consumer can be started at a given time.
    * To start a queue consumer, Chutney:
        * check if an other consumer was started:
            * if true, it re check the queue availability every `500 ms` without exceeding `timeout` duration. At every iteration, the remaining timeout is reduced by `500 ms`. 
            * else, it mark the queue as locked and start the current consumer.
        * consume messages without exceeding the remaining timeout ( = timeout - n * 500ms) or the `nb-messages`.
        * stop consumer and unlock the queue.
    
    :material-lightbulb-on: Don't use long `timeout`. In parallel execution, it makes execution slower and it can fails other executions. Prefer a retry strategy with short timeOut to allow different execution to access to the queue. For example : instead of 5 min timeout at action level, prefer adding a RetryTimeOutStrategy("5 min" (timeout), "1 s" (delay))) to the step
    

=== "Inputs"

    | Required | Name          | Type        | Default    | Description                         |
    |:--------:|:--------------|:------------|:-----------|:------------------------------------|
    |    *     | `queue-name`  | string      |            | Queue name.                         |
    |          | `nb-messages` | integer     |   1        | How many messages to be consumed. Throw error if got messages number is less than nb-messages.   |
    |          | `selector`    | string      |            |                                     |
    |          | `timeout`     | [duration](/documentation/actions/other/#duration-type) | `"10 sec"` |   In how many time a consume connection must be established and messages must be read     |
    |          | `ack`         | boolean     |    true    | [Basic.ack](https://www.rabbitmq.com/confirms.html#acknowledgement-modes){:target=_blank} acknowledgements mode is used if true.    |

=== "Outputs"

    |       Name | Type   | Description      |
    |-----------:|:-------|:-----------------|
    | `body`     | String | response as Map  |
    | `payloads` | String | response paylods |
    | `headers`  | String | response headers |

### Example

=== "Consume with short timeout"
    ``` kotlin
    AmqpBasicConsumeAction(
        target = "RABBITMQ_TARGET",
        queueName = "my.queue",
        nbMessages = 1,
        selector = "\$..[?(\$.headers.season=='1')]",
        timeout = "5 sec",
        ack = true
    )
    ```

=== "Consume with long timeout"
    ``` kotlin
    Step("Long basic consume", RetryTimeOutStrategy("5 min", "1 s")) {
        AmqpBasicConsumeAction(
            target = "RABBITMQ_TARGET",
            queueName = "my.queue",
            nbMessages = 1,
            selector = "\$..[?(\$.headers.season=='1')]"
        )
    }
    ```

# Basic get
!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/amqp/AmqpBasicGetAction.java){:target="_blank"}"

Use this action to have a direct access to available messages in a queue.

=== "Inputs"

    | Required | Name         | Type   | Description |
    |:--------:|:-------------|:-------|:-----------:|
    |    *     | `queue-name` | String | Queue name. |

=== "Outputs"

    |      Name | Type                                   | Description      |
    |----------:|:---------------------------------------|:-----------------|
    | `message` | String                                 | [response](https://rabbitmq.github.io/rabbitmq-java-client/api/4.x.x/com/rabbitmq/client/GetResponse.html){:target=_blank} as Map  |
    | `body`    | String                                 | response body |
    | `headers` | String                                 | response headers |



### Example

=== "Kotlin"
    ``` kotlin
    AmqpBasicGetAction(
        target ="RABBITMQ_TARGET",
        queueName = "my.queue"
    )
    ```

# Clean queues
!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/amqp/AmqpCleanQueuesAction.java){:target="_blank"}"

Use this action to purges the content of the given queues.
For example, it can be used at the beginning of your scenario to ensure that used queues are empty.

!!! warning
    Be careful when cleaning a queue which is shared between many scenarios.

=== "Inputs"

    | Required | Name          | Type          |Description |
    |:--------:|:--------------|:--------------|:-----------|
    |     *    | `queue-names` | List<String\> |  to be burged queues names          |

=== "Output"
     No output

### Example

=== "Kotlin"
    ``` kotlin
    AmqpCleanQueuesAction(
        target = "RABBITMQ_TARGET",
        queueNames = listOf(
            "my.queue",
            "my.other.queue"
        )
    )
    ```


# Create and bind temporary queue
!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/amqp/AmqpCreateBoundTemporaryQueueAction.java){:target="_blank"}"

Use this action to create a temporary queue and bind it to an existing exchange using a routing key.

=== "Inputs"

    | Required | Name            | Type   |   Default    |   Description    |
    |:--------:|:----------------|:-------|:------------:|:-----------------|
    |    *     | `exchange-name` | String |              |  Exchange name   |
    |          | `routing-key`   | String | "queue-name" |  The routing key to use for the binding. See this [example](https://www.rabbitmq.com/tutorials/tutorial-five-python.html#:~:text=complex%20topic%20exchange.-,Topic%20exchange,-Messages%20sent%20to){:target=_blank} for more details.  |
    |    *     | `queue-name`    | String |              |  Queue name      |

=== "Outputs"

    |    Name     | Type   | Description        |
    |------------:|:-------|:-------------------|
    | `queueName` | String | Created queue name |

### Example

=== "Kotlin"
    ``` kotlin
    AmqpCreateBoundTemporaryQueueAction(
        target = "RABBITMQ_TARGET",
        exchangeName = "my.exchange",
        queueName = "my.queue",
        routingKey = "children.*"
    )
    ```

!!! info
    At the end of the scenario execution, the created binding and queue will be automatically deleted respectively by [amqp-unbind-queue](/documentation/actions/amqp/#unbind-queue) and [amqp-delete-queue](/documentation/actions/amqp/#delete-queue) final actions.


# Unbind queue
!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/amqp/AmqpUnbindQueueAction.java){:target="_blank"}"

Use this action to delete a binding between exchange and queue.

=== "Inputs"

    | Required | Name            | Type   |   Description    |
    |:--------:|:----------------|:-------|:-----------------|
    |    *     | `queue-name`    | String |  Queue name      |
    |          | `exchange-name` | String |  Exchange name   |
    |          | `routing-key`   | String |  The routing key used for the binding.  |

### Example

=== "Kotlin"
``` kotlin
AmqpUnbindQueueAction(
target = "RABBITMQ_TARGET",
queueName = "my.queue",
exchangeName = "my.exchange",
routingKey = "children.*"
)
```

# Delete queue
!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/amqp/AmqpDeleteQueueAction.java){:target="_blank"}"

Use this action to delete an existing queue without regard for whether it is in use or has messages on it.
=== "Inputs"

    | Required | Name       | Type   | Description |
    |:--------:|:-----------|:-------|:------------|
    |    *     | queue-name | String | Queue name  |

=== "Outputs"
    No output

### Example

=== "Kotlin"
    ``` kotlin
    AmqpDeleteQueueAction(
        target = "RABBITMQ_TARGET",
        queueName = "my.queue"
    )
    ```
