??? info "Browse implementations"

    - [Sender](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/jms/JmsSenderAction.java){:target="_blank"}
    - [Listener](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/jms/JmsListenerAction.java){:target="_blank"}
    - [Clean queue](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/jms/JmsCleanQueueAction.java){:target="_blank"}

!!! note "Define a jms target"

    * Default `connectionFactoryName` is `ConnectionFactory`
    * To configure ssl, by default we add these properties in InitialContext : 
        * `connection.ConnectionFactory.keyStore` with `keyStore` property
        * `connection.ConnectionFactory.keyStorePassword` with `keyStorePassword` property
        * `connection.ConnectionFactory.keyStoreKeyPassword` with `keyPassword` property
        * `connection.ConnectionFactory.trustStore` with `trustStore` property
        * `connection.ConnectionFactory.trustStorePassword` with `trustStorePassword` property
    * All configuration beginning with java.naming.* are added to the context 
    * Other configuration:  
    In order to provide more configuration, you should prefix all other target properties with `jndi.`.  
    By example, if you want to add `com.specific.vendor.properties` key, the key should be `jndi.com.specific.vendor.properties`

```json title="Jms target example"
{
    "name": "JMS_TARGET",
    "url": "ssl://my.jms.server:61616",
    "properties": {
        "connectionFactoryName": "MyConnectionFactory"
        "java.naming.factory.initial": "org.apache.activemq.jndi.ActiveMQInitialContextFactory",
        
        "username": "myUsername", // (1)
        "password": "myPassword", // (2)
        "trustStore": "/home/APP/security/mytruststore.jks",
        "trustStorePassword": "myTrustStorePassword",
        "keyStore": "/home/APP/security/mykeyStore.jks",
        "keyStorePassword": "mykeyStorePassword",
        "keyPassword": "myKeyStoreKeyPassword",
    }
}
```

1. Valid properties are `username` or `user`. Set this for basic authentication
2. Valid properties are `userPassword` or `password`. Set this for basic authentication

# Jms Sender
!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/jms/JmsSenderAction.java){:target="_blank"}"

=== "Inputs"

    | Required | Name          | Type                              |  Default   |
    |:--------:|:--------------|:----------------------------------|:----------:|
    |    *     | `target`      | String                            |            |
    |    *     | `destination` | String                            |            |
    |    *     | `body`        | String                            |            |
    |          | `headers`     | Map<String, String\>              |            |

No output. Only a log in report if message was successfully sent


### Example

=== "Kotlin"
``` kotlin
JmsSenderAction(
    target = "JMS_TARGET",
    destination = "jms/domain/my/queue",
    body = "my text body"
    attributes = mapOf(
        "jms.MyProperty" to "some value"
    )
)
```

# Jms Listener
!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/jms/JmsListenerAction.java){:target="_blank"}"

*  **Only works on javax.jms.TextMessage**
* `selector` used as message filter in [createConsumer](https://docs.oracle.com/javaee/7/api/javax/jms/Session.html#createConsumer-javax.jms.Destination-java.lang.String-){:target="_blank"} or in [createBrowser](https://docs.oracle.com/javaee/7/api/javax/jms/Session.html#createBrowser-javax.jms.Queue-java.lang.String-){:target="_blank"}
* `bodySelector` verify in `browserMaxDepth` messages on the queue if it contains `bodySelector` characters

=== "Inputs"

    | Required | Name              | Type    |  Default   |
    |:--------:|:------------------|:--------|:----------:|
    |    *     | `target`          | String  |            |
    |    *     | `destination`     | String  |            |
    |          | `selector`        | String  |            |
    |          | `bodySelector`    | String  |            |
    |          | `browserMaxDepth` | Integer |            |
    |          | `timeOut`         | String  |   500 ms   |

=== "Outputs"

    |            Name | Type                |
    |----------------:|:--------------------|
    |   `textMessage` | String              |
    | `jmsProperties` | Map<String, Object> |

### Example

=== "Kotlin"
``` kotlin
JmsListenerAction(
    target = "JMS_TARGET",
    destination = "jms/domain/my/queue",
    selector = "type = 'boat' AND color = 'red'",
    bodySelector = "some value to search in message",
    browserMaxDepth = 100,
    timeOut = "1 s"
)
```

# Jms Clean Queue
!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/jms/JmsCleanQueueAction.java){:target="_blank"}"

*  **Only works on javax.jms.TextMessage**
* `selector` used as message filter in [createConsumer](https://docs.oracle.com/javaee/7/api/javax/jms/Session.html#createConsumer-javax.jms.Destination-java.lang.String-){:target="_blank"} or in [createBrowser](https://docs.oracle.com/javaee/7/api/javax/jms/Session.html#createBrowser-javax.jms.Queue-java.lang.String-){:target="_blank"}
* `bodySelector` verify in `browserMaxDepth` messages on the queue if it contains `bodySelector` characters **(only works on javax.jms.TextMessage)**

=== "Inputs"

    | Required | Name              | Type         |  Default   |
    |:--------:|:------------------|:-------------|:----------:|
    |    *     | `target`          | String       |            |
    |    *     | `destination`     | String       |            |
    |          | `selector`        | String       |            |
    |          | `bodySelector`    | String       |            |
    |          | `browserMaxDepth` | Integer      |            |
    |          | `timeOut`         | String       |   500 ms   |

No output. Only a log in report with number of messages removed

### Example

=== "Kotlin"
``` kotlin
JmsCleanQueueAction(
    target = "JMS_TARGET",
    destination = "jms/domain/my/queue",
    selector = "type = 'boat' AND color = 'red'",
    bodySelector = "some value to search in message",
    browserMaxDepth = 100,
    timeOut = "1 s"
)
```
