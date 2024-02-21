??? info "Browse implementations"

    - [Count](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/mongo/MongoCountAction.java){:target="_blank"}
    - [Delete](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/mongo/MongoDeleteAction.java){:target="_blank"}
    - [Find](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/mongo/MongoFindAction.java){:target="_blank"}
    - [Insert](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/mongo/MongoInsertAction.java){:target="_blank"}
    - [List Collections](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/mongo/MongoListAction.java){:target="_blank"}
    - [Update](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/mongo/MongoUpdateAction.java){:target="_blank"}

!!! important "Target Configuration"
    For all actions, the target should have a property `databaseName`

    ```json title="Mongo target example"
    {
        "name": "mongo_target",
        "url": "mongo://my.mongo.base:27017",
        "properties": {
            "databaseName": "myDatabaseName",
            "username": "myUsername", // (1)
            "password": "myPassword" // (2)
        }
    }
    ```

    1. Valid properties are `username` or `user`. Set this for basic authentication
    2. Valid properties are `userPassword` or `password`. Set this for basic authentication

!!! note "Collection Example"
    ```json title="ghibli_movies"
    {
      "title": "Castle in the Sky",
      "director": "Hayao Miyazaki",
      "rating": 78
    }
    {
      "title": "Grave of the Fireflies",
      "director": "Isao Takahata",
      "rating": 94
    }
    {
      "title": "My Neighbor Totoro",
      "director": "Hayao Miyazaki",
      "rating": 86
    }
    ```

# Count
!!! info [Browse implementations](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/mongo/MongoCountAction.java){:target="_blank"}

=== "Inputs"

    | Required | Name         | Type    |  Default   |
    |:--------:|:-------------|:--------|:----------:|
    |    *     | `target`     | String  |            |
    |    *     | `collection` | String  |            |
    |    *     | `query`      | String  |            |

=== "Outputs"

    |    Name | Type   |
    |--------:|:-------|
    | `count` | long   |

### Example

=== "Kotlin"
``` kotlin
MongoCountAction(
    target = "mongo_target",
    collection = "ghibli_movies",
    query = "{ rating: { \$gt: 85 } }"
)
```

# Delete
!!! info [Browse implementations](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/mongo/MongoDeleteAction.java){:target="_blank"}

=== "Inputs"

    | Required | Name         | Type    |  Default   |
    |:--------:|:-------------|:--------|:----------:|
    |    *     | `target`     | String  |            |
    |    *     | `collection` | String  |            |
    |    *     | `query`      | String  |            |

=== "Outputs"

    |           Name | Type   |
    |---------------:|:-------|
    | `deletedCount` | long   |

### Example

=== "Kotlin"
``` kotlin
MongoDeleteAction(
    target = "mongo_target",
    collection = "ghibli_movies",
    query = "{ director: { \"Hayao Miyazaki\" } }"
)
```

# Find
!!! info [Browse implementations](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/mongo/MongoFindAction.java){:target="_blank"}

=== "Inputs"

    | Required | Name         | Type    | Default |
    |:--------:|:-------------|:--------|:-------:|
    |    *     | `target`     | String  |         |
    |    *     | `collection` | String  |         |
    |    *     | `query`      | String  |         |
    |          | `limit`      | Integer |   20    |

=== "Outputs"

    |        Name | Type          |
    |------------:|:--------------|
    | `documents` | List<String\> |

### Example

=== "Kotlin"
``` kotlin
MongoFindAction(
    target = "mongo_target",
    collection = "ghibli_movies",
    query = "{ director: { \"Hayao Miyazaki\" } }",
    limit = 42
)
```

# Insert
!!! info [Browse implementations](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/mongo/MongoInsertAction.java){:target="_blank"}

=== "Inputs"

    | Required | Name         | Type    | Default |
    |:--------:|:-------------|:--------|:-------:|
    |    *     | `target`     | String  |         |
    |    *     | `collection` | String  |         |
    |    *     | `document`   | String  |         |

*Insert action does not have output.*

### Example

=== "Kotlin"
``` kotlin
MongoInsertAction(
    target = "mongo_target",
    collection = "ghibli_movies",
    document = "{ title: \"Pom Poko\", director: \"Isao Takahata\", rating: 77 }"
)
```

# List Collections
!!! info [Browse implementations](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/mongo/MongoListAction.java){:target="_blank"}

=== "Inputs"

    | Required | Name     | Type   | Default |
    |:--------:|:---------|:-------|:-------:|
    |    *     | `target` | String |         |

=== "Outputs"

    |              Name | Type          |
    |------------------:|:--------------|
    | `collectionNames` | List<String\> |

### Example

=== "Kotlin"
``` kotlin
MongoListAction(
    target = "mongo_target"
)
```

# Update
!!! info [Browse implementations](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/mongo/MongoUpdateAction.java){:target="_blank"}

=== "Inputs"

    | Required | Name           | Type          | Default |
    |:--------:|:---------------|:--------------|:-------:|
    |    *     | `target`       | String        |         |
    |    *     | `collection`   | String        |         |
    |    *     | `filter`       | String        |         |
    |    *     | `update`       | String        |         |
    |          | `arrayFilters` | List<String\> |         |

    !!! note
        ArrayFilters are supported since MongoDB v3.5.12+ ([https://jira.mongodb.org/browse/SERVER-831](https://jira.mongodb.org/browse/SERVER-831){:target="_blank"})
=== "Outputs"

    |        Name     | Type |
    |----------------:|:-----|
    | `modifiedCount` | long |

### Example

=== "Kotlin"
``` kotlin
MongoUpdateAction(
    target = "mongo_target",
    collection = "ghibli_movies",
    filter = "{ director: { \"Hayao Miyazaki\" } }",
    update = "{ director: { \"Saburō Akitsu\" } }"
)
```
