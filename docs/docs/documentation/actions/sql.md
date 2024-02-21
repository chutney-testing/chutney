!!! warning

    Due to legacy naming, there might be confusion with `Records`, `Rows`, attributes `Records.rows` and `Records.records` and method like `Records.getRows()`, `Records.rows()` etc.  
    **So read carefully this page.**

??? info "Browse implementations"

    - [Action](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/sql/SqlAction.java){:target="_blank"}
    - [Client](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/sql/core/SqlClient.java){:target="_blank"}

!!! note "Configuration"
    Most of the configuration is done on the target database.

    * Database URL:  
    You should configure your target with the property `jdbcUrl` to provide the JDBC URL of your database.
    
    * Authentication:  
    You can set target properties `username` and `password` if required.
    
    * Max fetch size:  
    You can set the maximum fetch size using target property `maxFetchSize` (default to 1000).
    
    * Other configuration:  
    In order to provide more configuration you should prefix all other target properties with `dataSource.`

    ```json title="Example"
    {
        "name": "ghibli_movies_database",
        "url": "tcp://myoracle.db.server:1531/",
        "properties": {
            "jdbcUrl": "jdbc:oracle:thin:@myoracle.db.server:1531/ghibli_movies_service",
            "username": "myUsername",
            "password": "myPassword",
            "maxFetchSize": "100",
            "dataSource.driverClassName": "oracle.jdbc.OracleDriver",
            "dataSource.maximumPoolSize": "5"
        }
    }
    ```

!!! note "Logging results"

    You can configure the maximum number of results to print in the execution report.

    - For a global project scope, set the property `chutney.actions.sql.max-logged-rows`.    
    - For a local step scope, use input value `nbLoggedRow`. This will override the value set by configuration.

=== "Inputs"

    | Required | Name          | Type          | Default | Note                                              |
    |:--------:|:--------------|:--------------|:-------:|:--------------------------------------------------|
    |    *     | `target`      | String        |         |                                                   |
    |    *     | `statements`  | List<String\> |         |                                                   |
    |          | `nbLoggedRow` | Integer       |   30    | Maximum number of rows to log in execution report |

=== "Outputs"

    This action `outputs` depends on wether you provided only one or many `statements` input.  
    See following sections for details about each case.

# Outputs for one statement

When you provide only one statement in input, the following outputs and operations using [`Rows`](#rows) and [`Row`](#row) types are available.

=== "Outputs"

|           Name | Type            | Note                          |
|---------------:|:----------------|:------------------------------|
|         `rows` | [`Rows`](#rows) |                               |
|     `firstRow` | [`Row`](#row)   | is an alias for `rows.get(0)` |
| `affectedRows` | int             | for non `SELECT` statements   |

## Rows

!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/sql/core/Rows.java){:target="_blank"}"

One `Rows` instance contains results for **one statement**. Following attributes and methods are available in SpEL :

* `count()`: Returns the number of results from a **SELECT** statement.  
  -> `${#rows.count()}`

* `get(int index)`: Returns the [`row`](#row) found at given index (starts at 0) or an empty row if not found  
  -> `${#rows.get(42)}`

* `get(String header)`: Returns a list of values (`List<Object>`) for one column's name  
  -> `${#rows.get("TITLE")}`

* `valuesOf(String... header)`: Returns a list of values (`List<List<Object>>`) for one or many column's name  
  -> `${#rows.valuesOf("DIRECTOR", "TITLE")}`

* `asMap()`: Transforms the structure as a `List<Map<String, Object>>`  
  Where `Map<String, Object>` represents a row, `String` is a header and `Object` an actual value  
  -> `${#rows.asMap()}`

## Row

!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/sql/core/Row.java){:target="_blank"}"

A `Row` provides you access to a record values.

* `get(String header)`: Get the actual value (Object) by column's name  
  -> `${#firstRow.get("TITLE")}` or `${#rows.get(0).get("TITLE")}`

* `get(int index)`: Get the actual value (Object) by column's index  
  -> `${#firstRow.get(4)}` or `${#rows.get(0).get(4)}`

## Example

Here is an example based one the following table :

| ID  | TITLE                    |    YEAR    | RATING |         DIRECTOR |
|:---:|:-------------------------|:----------:|:------:|-----------------:|
|  1  | "Castle in the Sky"      | 1986-08-02 |   78   | "Hayao Miyazaki" |
|  2  | "Grave of the Fireflies" | 1988-04-16 |   94   |  "Isao Takahata" |
|  3  | "My Neighbor Totoro"     | 1988-04-16 |   86   | "Hayao Miyazaki" |

=== "Kotlin"
``` kotlin
SqlAction(
    target = "ghibli_movies_database",
    statements = listOf(
        "SELECT * FROM movies WHERE rating > 85" // (1)
    ),
    nbLoggedRow = 5, // (2)
    outputs = mapOf(
        "numberOfBest" to "rows.count()".spEL(), // (3)
        "bestMoviesTitles" to "rows.get(\"TITLE\")".spEL() // (4)
    )
)
```

1. `statements` has only one entry, so outputs `rows` and `firstRow` are available but `recordResult` is not
2. Will locally override configuration `chutney.actions.sql.max-logged-rows`
3. Expected result is 2
4. Expected result is ["Grave of the Fireflies", "My Neighbor Totoro"]


# Outputs for many statements

When you provide more than one statement in input, you get a [`Records`](#records) **for each statement**.

=== "Outputs"

    |          Name  | Type                       | Note                                                                                   |
    |---------------:|:---------------------------|:---------------------------------------------------------------------------------------|
    | `recordResult` | List<[Records](#records)\> | Each [`Records`](#records) in the list contains the resulting records of one statement |

## Records

!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/sql/core/Records.java){:target="_blank"}"

One `Records` instance contains results for **one statement** and provides methods to retrieve and search them.  
`Records` is different from [`Rows`](#rows) type, but you can convert it using the `rows()` method.  

Following attributes and methods are available in SpEL :

* `affectedRows`: Returns the number of affected rows. This is useful for **INSERT**, **UPDATE** or **DELETE** statements  
-> `${#recordResult.get(42).affectedRows}`

* `count()`: Returns the number of results from a **SELECT** statement.  
-> `${#recordResult.get(42).count()}`

* `headers`: Returns the list of columns names (List< String >)  
-> `${#recordResult.get(42).headers}`

* `rows()`: Converts this `Records` to the wrapper type [`Rows`](#rows). **This may be recommended for easier use**  
-> `${#recordResult.get(42).rows()}`

* `records`: The list of [`Row`](#row). **While useful, it is recommended to use `rows()` instead**  
  -> `${#recordResult.get(42).records}`

* `row(int index)`: Get the [`Row`](#row) at provided index.  
  -> `${#recordResult.get(42).row(1337)}` equivalent to `${#recordResult.get(42).records.get(1337)}`
* 
## Example

Here is an example based one the following table :

| ID  | TITLE                    |    YEAR    | RATING |         DIRECTOR |
|:---:|:-------------------------|:----------:|:------:|-----------------:|
|  1  | "Castle in the Sky"      | 1986-08-02 |   78   | "Hayao Miyazaki" |
|  2  | "Grave of the Fireflies" | 1988-04-16 |   94   |  "Isao Takahata" |
|  3  | "My Neighbor Totoro"     | 1988-04-16 |   86   | "Hayao Miyazaki" |

=== "Kotlin"
``` kotlin
SqlAction(
    target = "ghibli_movies_database",
    statements = listOf(
        "SELECT * FROM movies WHERE rating > 90", // (1)
        "SELECT * FROM movies WHERE rating < 90"
    ),
    nbLoggedRow = 5, // (2)
    outputs = mapOf(
        "numberOfBest" to "recordResult.get(0).rows.count()".spEL(), // (3)
        "bestMoviesTitles" to "recordResult.get(0).get(\"TITLE\")".spEL() // (4)
        "numberOfWorst" to "recordResult.get(1).count()".spEL(), // (5)
        "worstMoviesTitles" to "recordResult.get(1).get(\"TITLE\")".spEL() // (6)
    )
)
```

1. `statements` has two queries, so only the output `recordResult` is available
2. Will locally override configuration `chutney.actions.sql.max-logged-rows`
3. Get the result for the 1st query, expected output is 1
4. Expected output is ["Grave of the Fireflies"]
5. Get the result for the 2nd query, expected output is 2
6. Expected output is ["My Neighbor Totoro", "Castle in the Sky"]
