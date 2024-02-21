!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/function/JsonFunctions.java){:target="_blank"}"

Following functions help you handle JSON documents.

!!! important "All functions accept an input parameter `document` of any type"


# JsonPath

!!! note "Object jsonPath(Object document, String jsonPath)"

    Read the given JSON path from the given document.

    **Parameters** :

    * `document` : The document in which the path will be executed
    * `jsonPath` : The JSON path to read

    **Returns** : The read result

    **Examples** :

    SpEL : `${#jsonPath(#json, '$.key[0]')}`


# JsonSerialize

!!! note "String jsonSerialize(Object obj)"

    Serialize given object as a JSON string.

    **Parameters** :

    * `obj` : The object to serialize

    **Returns** : The result JSON string

    **Examples** :

    SpEL : `${#jsonSerialize(#anyObj)}`


# JsonSet

!!! note "String jsonSet(Object document, String path, String value)"

    Set an existing key value into a given JSON document.

    **Parameters** :

    * `document` : The document to update
    * `path` : The path in the document to update
    * `value` : The new value to set

    **Returns** : The result JSON string

    **Examples** :

    SpEL : `${#jsonSet(#json, '$.keyToSet', 'new value')}`


# JsonSetMany

!!! note "String jsonSetMany(Object document, Map<String, Object> map)"

    Set existing keys values into a given JSON document.

    **Parameters** :

    * `document` : The document to update
    * `map` : A map of paths in the document to update associated with the new values to set

    **Returns** : The result JSON string

    **Examples** :

    SpEL : `${#jsonSetMany(#json, {'$.path1': 'new value1', '$.path2': 'new value2'})}`


# JsonMerge

!!! note "String jsonMerge(Object documentA, Object documentB)"

    Merge a given JSON document into another.

    **Parameters** :

    * `documentA` : The document to update
    * `documentB` : The document to set

    **Returns** : The result JSON string

    **Examples** :

    SpEL : `${#jsonMerge(#jsonBase, #jsonToAdd)}`
