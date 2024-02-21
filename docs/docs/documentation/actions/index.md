# Actions

[^1]: [Here, you can see the code for all actions.](https://github.com/chutney-testing/chutney/tree/master/action-impl/src/main/java/com/chutneytesting/action)


Chutney provides a set of capabilities, or `Actions`, you can use in your scenarios.
They replace all the boilerplate code you would have to write and maintain for executing your scenarios.
You can see them as a set of small generic clients. [^1]

For example, instead of writing your own HTTP client for doing a POST request, you just have to use the [HttpPost](/documentation/actions/http/#post) action
and give it the minimum amount of information as inputs (i.e. targeted service, URI, body and headers).

All actions are structured the same way with **inputs**, **outputs**, **validations** and **teardown**.

!!! note "Extending Chutney actions"
    Actions are extensible and you can provide your own.  
    For further details, see [how to implement your own action](/documentation/actions/extension/) and then [how to package Chutney with it](/todo).

## Inputs

Inputs are the minimum information needed to run the action.  
For example, if you want to perform an HTTP GET request, you should give, at least, the targeted service and an URI.  
Obviously, you should be familiar with the technology behind each action you use, and we stick to the proper vocabulary (i.e. _body_ for HTTP, _payload_ for Kafka etc.)

!!! note
    Some input values are required and checked for correctness. While other values might not be required, or we provide a default value.

!!! note
    All actions performing a request on a remote service need to know the `Target`. While other action, like validating XML data, don't need a target.  
    Please, refer to [target configuration](/configuration/env/#target-configuration) for further details.

!!! note
    All actions must have a [Logger](https://github.com/chutney-testing/chutney/blob/master/action-spi/src/main/java/com/chutneytesting/action/spi/injectable/Logger.java){:target=_blank} class as input.
    At runtime a [DelagateLogger](https://github.com/chutney-testing/chutney/blob/master/engine/src/main/java/com/chutneytesting/engine/domain/execution/engine/parameterResolver/DelegateLogger.java){:target=_blank} is automatically injected by the execution engine.</br>
    This logger contains action's logs which be present in the execution report.

## Outputs

Outputs contain the data collected after performing an action, and only if it succeeded.
These data are set in the execution context and can be accessed and used later in another action.  
Each action provide a set of default outputs. But they are generic and may contain much more information than what you actually need.

!!! note
    The execution context holds outputs in a key/value map, where the key is a string and the value is typed.

!!! warning
    Since the execution context is a map, default outputs are overridden if you run the same action more than once in the scenario or if outputs have the same name (key).

!!! important
    We strongly recommend you to define your own outputs for setting relevant data in the execution context.

### How to use outputs

Let's say you are doing an HTTP GET request. By default, this action has 3 outputs: `status`, `body`, `headers`, but you want to capture a specific value from the response body.

In order to do so, you need to use an [expression](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions) and Chutney [functions](/documentation/functions/classpath/), so we recommend you to read about them for further details.

Let's see a simple example where you want to get a list of movie titles rated above 85/100.

```json title="Response body"
{
    "movies": [
        {
            "title": "Castle in the Sky",
            "director": "Hayao Miyazaki",
            "rating": 78
        },
        {
            "title": "Grave of the Fireflies",
            "director": "Isao Takahata",
            "rating": 94
        },
        {
            "title": "My Neighbor Totoro",
            "director": "Hayao Miyazaki",
            "rating": 86
        }
    ]
}
```

The best way to filter and extract the relevant data from a JSON document is to use a JSONPath expression.  
Here is the one for our example : `$.movies[?(@.rating > 85)].title`

In order to process it, you would need to write code using a JSONPath library and then tell Chutney to run your custom code.  
Chutney provides a way to run custom code during scenario execution using [Spring Expression](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions).

Here is a raw expression you could write : `${T(com.jayway.jsonpath.JsonPath).parse(#body).read("$.movies[?(@.rating > 85)].title")}`  
Fortunately, Chutney also provides [Functions](/todo) for common needs, which helps you write SpEL.  
In this case you can use the `json` function and the resulting SpEL would become : `${#jsonPath(#body, '$.movies[?(@.rating > 85)].title')}`

Now that you know what this cryptic expression is and does, let's see the full HTTP GET action with outputs :

=== "Kotlin"

    ``` kotlin
    HttpGetAction(
        target = "ghibli_movie_service",
        uri = "/all?offset=0&limit=3",
        outputs = mapOf(
            "bestMovies" to "jsonPath(#body, '$.movies[?(@.rating > 85)].title')".spEL()
        )
    )
    ```

=== "JSON"

    ``` json
    {
        "type": "http-get",
        "target": "ghibli_movie_service",
        "inputs": {
            "uri": "/all?offset=0&limit=3"
        },
        "outputs": {
            "bestMovies": "${#jsonPath(#body, '$.movies[?(@.rating > 85)].title')}"
        }
    }
    ```

After executing this action, the execution context will contain the following outputs :

| Key        | Type                                 |
|:-----------|:-------------------------------------|
| body       | String                               |
| status     | Integer                              |
| headers    | org.springframework.http.HttpHeaders |
| bestMovies | List<String>                         |

Your relevant data can be accessed from another SpEL using `#bestMovies` and since it is a List you can call methods on it, like so : `${#bestMovies.get(0)}`  
`#body`, `#status` and `#headers` are also available but are very likely to be overridden by a following step while you have full control over the use of the `#bestMovies` key.

## Validation

Validations are a list of checks you want to perform in order to validate a step.
By default, a step will _fail_ when an error occurs, but we cannot verify the semantic of the result.  
Asserting a step depends on your feature and requirements.

For example, if an HTTP GET request returns a status code 500, the step is _technically_ complete and succeed.  
But, you may want to fail the step if the status is different than 200.

Each validation has a name and evaluates to a boolean, using [expressions](/todo) and [functions](/todo). 

=== "Kotlin"

    ``` kotlin
    HttpGetAction(
        target = "ghibli_movie_service",
        uri = "/all?offset=0&limit=3",
        outputs = mapOf(
            "bestMovies" to "jsonPath(#body, '$.movies[?(@.rating > 85)].title')".spEL()
        ),
        validations = mapOf(
            "request_succeed" to "status == 200".spEL(),
            "found_2_movies" to "bestMovies.size() == 2".spEL()
        )
    )
    ```

=== "JSON"

    ``` json
    {
        "type": "http-get",
        "target": "ghibli_movie_service",
        "inputs": {
            "uri": "/all?offset=0&limit=3"
        },
        "outputs": {
            "bestMovies": "${#jsonPath(#body, '$.movies[?(@.rating > 85)].title')}"
        },
        "validations": {
            "request_succeed": "${#status == 200}",
            "found_2_movies": "${#bestMovies.size() == 2}"
        }
    }
    ```

## Teardown

Sometimes you may need to clean data or come back to a stable state after executing a scenario.  
Chutney provides a way to do it by _registering_ a `final action`.

!!! note
    Some actions will, by default, register a final action.  
    Most often, it is for closing resources. For example, when starting a mock SSH server, we automatically register an action to stop it at the end of the scenario.

    But we cannot provide more than that, since a teardown depends on _your_ specification and needs.

If you need to add your own final action to your scenario, it is not different than a regular action since it **is** just an action by itself !

However, we suggest you the following tips:

!!! important "Register your final action first !"
    Since a scenario execution stops at the first failure, if your final action is in a step _after_ the failure, it will never be registered nor run.  
    So you must register them before.

!!! important "Wrap your final action with its corresponding step !"
    Since you register your final actions before anything, you still don't want to run them all when it does not make sense.  
    To avoid that, the best practice is to wrap it in a step with the corresponding action it cleans.

**Example**

``` kotlin
Step("Insert data in a table") { // (1)
    Step("Final action : delete data at the end") { // (2)
        FinalAction(
            name = "Delete data",
            type = "sql",
            target = "my_database",
            inputs = mapOf(
                "statements" to listOf("DELETE FROM MY_TABLE WHERE id=1")
            )
        )
    }
    Step("Insert data in MY_TABLE") { // (3)
        SqlAction(
            target = "my_database",
            statements = listOf(
                "insert into MY_TABLE (ID, NAME) values(1, 'my_name')"
            )
        )
    }
}
```

1. This is a wrapper step
2. We declare our final action **first** !
3. We declare our real action after
