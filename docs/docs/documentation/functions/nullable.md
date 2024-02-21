!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/function/NullableFunction.java){:target="_blank"}"

This function helps you handle values which may be null.

!!! note "Object nullable(Object input)"

    See [Optional.ofNullable()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Optional.html#ofNullable(T)){:target="_blank"} for further details

    **Returns** :
    
    * The typed value or the String "null" in case the value was null.

    **Examples** :

    SpEL without : `${T(java.util.Optional).ofNullable(#mayBeNull).orElse("null")}`

    SpEL with    : `${#nullable(#mayBeNull)}`
