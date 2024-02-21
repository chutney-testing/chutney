??? info "Browse implementations"

    - [Counter](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/micrometer/MicrometerCounterAction.java){:target="_blank"}
    - [Gauge](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/micrometer/MicrometerGaugeAction.java){:target="_blank"}
    - [Timer](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/micrometer/MicrometerTimerAction.java){:target="_blank"}
    - [Timer Start](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/micrometer/MicrometerTimerStartAction.java){:target="_blank"}
    - [Timer Stop](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/micrometer/MicrometerTimerStopAction.java){:target="_blank"}
    - [Summary](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/micrometer/MicrometerSummaryAction.java){:target="_blank"}

!!! note
    Micrometer provides a static [global registry](https://micrometer.io/docs/concepts#_global_registry){:target=_blank}. This registry is used as default if no registry is given in action's inputs.
# Counter

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/micrometer/MicrometerCounterAction.java){:target="_blank"}"

Use this action to report
a [count](https://micrometer.io/docs/concepts#_counters:~:text=9.-,Counters,-Counters%20report%20a){:target=_blank}
metric.

=== "Inputs"

    | Required                 | Name         | Type                             |  Description                                             |
    |:------------------------:|:-------------|:---------------------------------|:---------------------------------------------------------|
    | if `counter` is null     | `name`       | String                           | Counter name.    |
    | if `name` is null        | `counter`    | [Counter](https://javadoc.io/doc/io.micrometer/micrometer-core/latest/io/micrometer/core/instrument/Counter.html){:target=_blank}             | Counter instance. |
    |                          | `description`| String                           | Counter  description                                 |
    |                          | `unit`       | String                           | Count [unit](https://github.com/micrometer-metrics/micrometer/blob/main/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/BaseUnits.java){:target=_blank}                                                 |
    |                          | `tags`       | List<String\>                    | key,value list representing tags. A tag is a Key/value pair representing a dimension of a meter used to classify and drill into measurements.                                  |
    |                          | `registry`   | [MeterRegistry](https://javadoc.io/doc/io.micrometer/micrometer-core/latest/io/micrometer/core/instrument/MeterRegistry.html){:target=_blank}             | Creates and manages your application's set of meters  |
    |           *              | `increment`  | Integer as String                | Positive number by which the counter will be incremented. |

=== "Outputs"

    |    Name               | Type                                                                                                                               | Description             |
    |:----------------------|:-----------------------------------------------------------------------------------------------------------------------------------|:------------------------|
    | `micrometerCounter`   | [Counter](https://javadoc.io/doc/io.micrometer/micrometer-core/latest/io/micrometer/core/instrument/Counter.html){:target=_blank}  | The incremented counter |

### Example

=== "Kotlin"

``` kotlin
MicrometerCounterAction(
    name = "products_likes",
    description = "products likes counter",
    tags = listOf(
        "product", "1",
        "liked_feature", "color"
    ),
    increment = "1"
)
```

# Gauge

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/micrometer/MicrometerGaugeAction.java){:target="_blank"}"

Use this action to report a [gauge](https://micrometer.io/docs/concepts#_gauges){:target=_blank} metric.

=== "Inputs"

    | Required                   | Name              | Type          |  Description                                               |
    |:--------------------------:|:------------------|:--------------|:-----------------------------------------------------------|
    |            *               | `name`            | String        | Gauge name.                                                |
    |                            | `description`     | String        | Gauge  description                                         |
    | if `gaugeFunction` is null | `gaugeObject`     | Object        | Gauge will return the current value of this object .       |
    | if `gaugeObject` is null   | `gaugeFunction`   | String        | Gauge function.                                            |
    |                            | `unit`            | String        | Count [unit](https://github.com/micrometer-metrics/micrometer/blob/main/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/BaseUnits.java){:target=_blank} |
    |                            | `tags`            | List<String\> | key,value list representing tags. A tag is a Key/value pair representing a dimension of a meter used to classify and drill into measurements.   |
    |                            | `registry`        | [MeterRegistry](https://javadoc.io/doc/io.micrometer/micrometer-core/latest/io/micrometer/core/instrument/MeterRegistry.html){:target=_blank} | Creates and manages your application's set of meters  |
    |                            | `strongReference` | Boolean       | Indicates that the gauge should maintain a strong reference on the object upon which its instantaneous value is determined. Default is False |

    !!! info
        1. GaugeObject is required and must be a Number, a Collection or a Map if no gaugeFunction supplied.
        2. If gaugeFunction is null and gaugeObject is:
            * a number: the gauge will return it's value.
            * a collection: the gauge will return it's size.
            * a Map: the gauge will return it's size.
        3. If gaugeObject and gaugeFunction are not null: the gauge call gaugeFunction of gaugeObject and return the result value.
        4. If gaugeObject is null, gaugeFunction must be a static function.
        5. GaugeFunction is required if gaugeObject is null.
        6. GaugeFunction shouldn't have parameters.
        7. GaugeFunction should return an `int`, `long`, `float`, `double` or any [Number](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Number.html){:target=_blank}'s child class.

=== "Outputs"

    |    Name                 | Type    | Description               |
    |:------------------------|:--------|:--------------------------|
    | `micrometerGaugeObject` | Object  | `gaugeObject` input       |

### Example

=== "gaugeObject as Map"
    ``` kotlin
    Given("I declare my claims collection") {
        ContextPutAction(
            entries = mapOf(
                "claims" to mapOf(
                    "1" to listOf("claim_1", "claim_2"),
                    "2" to listOf("claim_3", "claim_4")
                )
            )
        )
    }
    When("I request my gauge metric") {
        MicrometerGaugeAction(
            name = "claims_gauge",
            description = "claims gauge",
            gaugeObject = "\${#claims}",
        )
    }
    ```

=== "gaugeObject & gaugeFunction"
    ``` kotlin
    Given("I set the last claim dateTime") {
        ContextPutAction(
            entries = mapOf(
                "last_claim_at" to "\${T(java.time.LocalDateTime).now()}"
            )
        )
    }
    When("I request my last claim day metric") {
        MicrometerGaugeAction(
            name = "last_claim_date_time_gauge",
            description = "last claim date time gauge",
            gaugeObject = "\${#last_claim_at}",
            gaugeFunction = "getDayOfMonth",
        )
    }
    ```

=== "only gaugeFunction"
    Supposing that we have class with a static methode that return an int.

    ``` java
    public class MyClass {
        public static int getValue(){
            int result = 0;
            /*
            do some thing
            result = ...
            */
            return result;
        }
    }
    
    ```
    
    Then we can use this function as gaugeFunction:
    ``` kotlin
    When("I request the gauge metric") {
        MicrometerGaugeAction(
            name = "gauge name",
            description = "gauge description",
            gaugeFunction = "my_package.myClass.getValue",
        )
    }
    ```

# Timer

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/micrometer/MicrometerTimerAction.java){:target="_blank"}"

Use this action to report
a [timer](https://micrometer.io/docs/concepts#_gauges:~:text=11.-,Timers,-Timers%20are%20intended){:target=_blank}
metric.

=== "Inputs"

    | Required             | Name                           | Type                             |  Description                                             |
    |:--------------------:|:-------------------------------|:---------------------------------|:---------------------------------------------------------|
    | if `timer` is null   | `name`                         | String                           | Timer name.                                              |
    |                      | `description`                  | String                           | Timer  description                                       |
    |                      | `tags`                         | List<String\>                    | key,value list representing tags. A tag is a Key/value pair representing a dimension of a meter used to classify and drill into measurements   |
    |                      | `bufferLength`                 | Integer as String                | Distribution statistic buffer length |
    |                      | `expiry`                       | [Duration](https://www.chutney-testing.com/documentation/actions/other/#duration-type) | Distribution statistic expiry |
    |                      | `maxValue`                     | [Duration](https://www.chutney-testing.com/documentation/actions/other/#duration-type) | Timer max duration |
    |                      | `minValue`                     | [Duration](https://www.chutney-testing.com/documentation/actions/other/#duration-type) | Timer min duration |
    |                      | `percentilePrecision`          | Integer as String                | Percentile precision                                      |
    |                      | `publishPercentilesHistogram`  | Boolean                          | Publish percentile histogram or not                       |
    |                      | `percentiles`                  | String                           | Comma separated list of doublepercentiles doubles         |
    |                      | `sla`                          | String                           | Comma separated list of doublepercentiles doubles         |
    | if `name` is null    | `timer`                        | [Timer](https://micrometer.io/docs/concepts#_gauges:~:text=11.-,Timers,-Timers%20are%20intended){:target=_blank}  | Timer instance. |
    |                      | `registry`                     | [MeterRegistry](https://javadoc.io/doc/io.micrometer/micrometer-core/latest/io/micrometer/core/instrument/MeterRegistry.html){:target=_blank}   | Creates and manages your application's set of meters  |
    |                      | `timeunit`                     | [TimeUnit](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/TimeUnit.html){:target=_blank}  as String | Time unit                                                 |
    |                      | `record`                       | [Duration](https://www.chutney-testing.com/documentation/actions/other/#duration-type)       | The timer will be updated by the record duration |

=== "Outputs"

    |    Name               | Type                                                                                                                               | Description             |
    |:----------------------|:-----------------------------------------------------------------------------------------------------------------------------------|:------------------------|
    | `micrometerTimer`     | [Timer](https://www.javadoc.io/doc/io.micrometer/micrometer-core/latest/io/micrometer/core/instrument/Timer.html){:target=_blank}  | The timer               |

### Example

=== "Kotlin"

``` kotlin
MicrometerTimerAction(
    name = "my_timer",
    description = "my timer description",
    record = "3 s",
)
```

# Timer start

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/micrometer/MicrometerTimerStartAction.java){:target="_blank"}"

Use this action to start a [Timer.Sample](https://www.javadoc.io/doc/io.micrometer/micrometer-core/latest/io/micrometer/core/instrument/Timer.Sample.html){:target=_blank}.

=== "Inputs"

    | Required | Name                           | Type                             |  Description                                             |
    |:--------:|:-------------------------------|:---------------------------------|:---------------------------------------------------------|
    |          | `registry`                     | [MeterRegistry](https://javadoc.io/doc/io.micrometer/micrometer-core/latest/io/micrometer/core/instrument/MeterRegistry.html){:target=_blank}   | Creates and manages your application's set of meters  |

=== "Outputs"

    |    Name                 | Type                                                                                                                               | Description             |
    |:------------------------|:-----------------------------------------------------------------------------------------------------------------------------------|:------------------------|
    | `micrometerTimerSample` | [Timer.Sample](https://www.javadoc.io/doc/io.micrometer/micrometer-core/latest/io/micrometer/core/instrument/Timer.Sample.html){:target=_blank}  | The sample               |

### Example

=== "Kotlin"

``` kotlin
MicrometerTimerStartAction()
```

# Timer stop

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/micrometer/MicrometerTimerStopAction.java){:target="_blank"}"

Use this action to stop a [Timer.Sample](https://www.javadoc.io/doc/io.micrometer/micrometer-core/latest/io/micrometer/core/instrument/Timer.Sample.html){:target=_blank} and record its duration into timer.

=== "Inputs"

    | Required | Name                           | Type                             |  Description                                             |
    |:--------:|:-------------------------------|:---------------------------------|:---------------------------------------------------------|
    |     *    | `sample`                       | [Timer.Sample](https://www.javadoc.io/doc/io.micrometer/micrometer-core/latest/io/micrometer/core/instrument/Timer.Sample.html){:target=_blank}   | The previously started sample  |
    |     *    | `timer`                        | [MeterRegistry](https://www.javadoc.io/doc/io.micrometer/micrometer-core/latest/io/micrometer/core/instrument/Timer.Sample.html){:target=_blank}   | Sample's duration will be recorded into this timer  |
    |          | `timeunit`                     | [TimeUnit](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/TimeUnit.html){:target=_blank}  as String | Time unit. Default is SECONDS                                   |
=== "Outputs"

    |    Name                         | Type                                                                                                              | Description                  |
    |:--------------------------------|:------------------------------------------------------------------------------------------------------------------|:-----------------------------|
    | `micrometerTimerSampleDuration` | [Duration](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Duration.html){:target=_blank}  | The sample recorded duration |

### Example

=== "Kotlin"

``` kotlin
MicrometerTimerStopTask(
    sample = "\${#micrometerTimerSample}",
    timer =  "\${#micrometerTimer}"
)
```

# Summary

!!! info "[Browse Implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/micrometer/MicrometerSummaryAction.java){:target="_blank"}"

Use this action to report a [distribution summary](https://micrometer.io/docs/concepts#_distribution_summaries){:target=_blank} metric.

=== "Inputs"

    | Required                              | Name                           | Type                             |  Description                                             |
    |:-------------------------------------:|:-------------------------------|:---------------------------------|:---------------------------------------------------------|
    | if `distributionSummary` is null      | `name`                         | String                           | Distrubution summary name.                               |
    |                                       | `description`                  | String                           | Distrubution summary description                                       |
    |                                       | `tags`                         | List<String\>                    | Key,value list representing tags. A tag is a Key/value pair representing a dimension of a meter used to classify and drill into measurements   |
    |                                       | `bufferLength`                 | Integer as String                | Distribution statistic buffer length |
    |                                       | `expiry`                       | [Duration](https://www.chutney-testing.com/documentation/actions/other/#duration-type) | Distribution statistic expiry |
    |                                       | `maxValue`                     | [Duration](https://www.chutney-testing.com/documentation/actions/other/#duration-type) | Distrubution max duration |
    |                                       | `minValue`                     | [Duration](https://www.chutney-testing.com/documentation/actions/other/#duration-type) | Distrubution min duration |
    |                                       | `percentilePrecision`          | Integer as String                | Percentile precision                                      |
    |                                       | `publishPercentilesHistogram`  | Boolean                          | Publish percentile histogram or not                       |
    |                                       | `percentiles`                  | String                           | Comma separated list of doublepercentiles doubles         |
    |                                       | `sla`                          | String                           | Comma separated list of doublepercentiles doubles         |
    |                                       | `scale`                        | Double as String                 | Scale value         |
    |  if `name` is null                    | `distributionSummary`          | [DistributionSummary](https://github.com/micrometer-metrics/micrometer/blob/main/micrometer-core/src/main/java/io/micrometer/core/instrument/DistributionSummary.java){:target=_blank}  | Distribution instance.|
    |                                       | `registry`                     | [MeterRegistry](https://javadoc.io/doc/io.micrometer/micrometer-core/latest/io/micrometer/core/instrument/MeterRegistry.html){:target=_blank}   | Creates and manages your application's set of meters  |
    |                                       | `timeunit`                     | [TimeUnit](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/TimeUnit.html){:target=_blank}  as String | Time unit                                                 |
    |                                       | `record`                       | Double as String       | The distribution will be updated by the record value |

=== "Outputs"

    |    Name               | Type                                                                                                                               | Description             |
    |:----------------------|:-----------------------------------------------------------------------------------------------------------------------------------|:------------------------|
    | `micrometerSummary`   | [DistributionSummary](https://github.com/micrometer-metrics/micrometer/blob/main/micrometer-core/src/main/java/io/micrometer/core/instrument/DistributionSummary.java){:target=_blank}  | The distribution summary               |

### Example

=== "Kotlin"

``` kotlin
MicrometerSummaryAction(
    name = "response_size_summary",
    description = "response size summary",
    unit = "bytes",
)
```
