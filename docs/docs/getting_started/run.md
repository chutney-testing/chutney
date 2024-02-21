Now, in order to run your first scenario, you need to configure your project and build tool according to your preferences.

Chutney Kotlin DSL provides a JUnit 5 Runner with annotations.

It provides also a `Launcher` class you can use the way you want. You are free to run your scenarios _when_ and
_anywhere_ you want (be it in a `main` if you wish).


In the following sections :

* you will see how to use the JUnit 5 Runner and annotations.
* you will see how to use the `Launcher` in a standard test, attached to the `integration` phase and run using the maven failsafe plugin.


# Using Chutney JUnit5 Engine

When using the JUnit5 engine, we recommend you to use JSON files to [declare your environments and targets](/getting_started/write/#define-your-test-environment-alternative).

Create a Kotlin file (ex. `Junit5SearchFeat.kt`) with the following content :

``` kotlin title="SearchFeat.kt"
package com.chutneytesting.getstart

import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.junit.api.ChutneyTest

class Junit5SearchFeat {

    @ChutneyTest(environment = "ENVIRONMENT")
    fun testMethod(): ChutneyScenario {
        return search_scenario
    }
}
```


# Using Chutney Launcher

Under `src/test/kotlin` create a package (ex. `com.chutneytesting.getstart`) and create a Kotlin file (ex. `SearchFeat.kt`) with the following content :

``` kotlin title="SearchFeat.kt"
package com.chutneytesting.getstart

import com.chutneytesting.kotlin.launcher.Launcher
import org.junit.jupiter.api.Test

class SearchFeat {
    @Test
    fun `search on the world wide web`() {
        Launcher().run(search_scenario, environment)
    }
}
```

??? note "Configure your build tool"

    === "maven"

        ``` xml
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-failsafe-plugin</artifactId>
            <configuration>
                <includes>
                    <include>**/*Feat.*</include>
                </includes>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>integration-test</goal>
                        <goal>verify</goal>
                    </goals>
                </execution>
            </executions>
            <dependencies>
                <dependency>
                    <groupId>org.junit.jupiter</groupId>
                    <artifactId>junit-jupiter-engine</artifactId>
                    <version>5.8.2</version>
                </dependency>
            </dependencies>
        </plugin>
        ```

    === "gradle"

        ``` kotlin
        actions.test {
            filter {
                includeTestsMatching("*Feat")
            }
            useJUnitPlatform()
        }
        ```


# Run it !

Now you can simply run `mvn verify` or `./gradlew test`.

If you are using Maven, the console will output the resulting execution :

``` sh
[SUCCESS] scenario: "Search documents" on environment The World Wide Web # (1)
[SUCCESS] I visit a search engine [default]  # (2)
>> Validation [http 200] : OK # (3)
http-get { uri: "/"} # (4)
on { search_engine: https://www.google.fr } # (5)
[SUCCESS] I am on the front page [default]
success { }
```

1. Scenario succeed and was run on environment "The World Wide Web"
2. Step `I visit a search engine` succeed and was performed with the `default` strategy
3. Step validation on the HTTP status succeed
4. Information about which action was performed and with which parameters
5. Information about the target on which the action was performed


# Chutney Idea plugin
You can run scenarios using our [IntelliJ Idea plugin](/getting_started/intellij_plugin).
