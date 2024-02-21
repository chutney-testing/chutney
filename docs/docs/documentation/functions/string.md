!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/function/StringFunction.java){:target="_blank"}"

This function helps you modify strings.

!!! note "String stringReplace(String input, String regularExpression, String replacement)"

    Replaces by another value all occurrences of a given pattern found in a string.

    **Parameters** :

    * `input` : The primary string
    * `regularExpression` : The regular expression to match to
    * `replacement` : The replacement string

    **Returns** : The derived string

    **Examples** :

    SpEL with    : `${#stringReplace("Hello", "l+", "r")}`
