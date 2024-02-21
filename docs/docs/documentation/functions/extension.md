When you need a custom function which is not provided by Chutney, you can implement it and load it to your chutney server.

# Implement your custom function
* Create a new java class.
* Declare a static method and implement it.
* Annotate it with `@SpelFunction`.

!!! warning
    Method overloading does not work with SpEL.

Example

``` java
    package my.custom.package;

    import com.chutneytesting.action.spi.SpelFunction;
    import org.apache.commons.lang3.StringUtils;

    public class MyCustomFunctions {

        @SpelFunction
        public static int stringSum(String a, String b) {
            int right = StringUtils.isNoneBlank(a) ? Integer.valueOf(a) : 0;
            int left = StringUtils.isNoneBlank(b) ? Integer.valueOf(b) : 0;
            return left + right;
        }
}
```

# Load it to Chutney
* create a `chutney.functions` in resources/META-INF/extension
* declare your custom class inside it:
    ```
    my.custom.package.MyCustomFunctions

    ```
  

* Restart Chutney server and all annotated methods with `@SpelFunction` are now loaded.
    </br> Check your server log, you will see something like: </br>
    `[main] DEBUG c.c.e.d.e.evaluation.SpelFunctions - Loading function: stringSum (MyCustomFunctions)`

# Use it
Call your custom function from your Kotlin scenario:

Example:

``` kotlin
    import com.chutneytesting.kotlin.dsl.AssertAction
    import com.chutneytesting.kotlin.dsl.Scenario

    val my_scenario = Scenario(title = "my scenario") {
        When("I test my string sum function") {
            AssertAction(
                asserts = listOf(
                    "\${#stringSum(\"1\", \"2\") == 3}",
                    "\${#stringSum(\"1\", null) == 1}",
                    "\${#stringSum(null, \"2\") == 2}",
                    "\${#stringSum(null, null) == 0}",
                ),
            )
        }
    }
```
