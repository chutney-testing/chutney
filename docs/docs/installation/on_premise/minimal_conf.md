!!! note "Packaging example"
    Maven module [local-dev](https://github.com/chutney-testing/chutney/tree/master/packaging/local-dev){:target="_blank"} shows one way of packaging Chutney.  
    Use it as an example to make your own package, custom to your needs.


# Maven configuration

Use [Spring Boot Build Tool Plugins](https://docs.spring.io/spring-boot/docs/2.7.x/reference/html/build-tool-plugins.html#build-tool-plugins){:target="_blank"} to package Chutney as an executable jar.

=== "maven"
    ``` xml
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>2.7.5</version>
        <configuration>
            <executable>true</executable>
            <layout>ZIP</layout>
            <mainClass>com.chutneytesting.ServerBootstrap</mainClass>
            <finalName>chutney-${project.artifactId}-${chutney.version}</finalName>
        </configuration>
        <executions>
            <execution>
                <goals>
                    <goal>repackage</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
    ```

Declare a BOM dependency on Chutney parent.

=== "maven"
    ``` xml
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.chutneytesting</groupId>
                <artifactId>chutney-parent</artifactId>
                <version>${chutney.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    ```

Add Chutney server and UI as dependencies.

=== "maven"
    ``` xml
    <dependency>
        <groupId>com.chutneytesting</groupId>
        <artifactId>server</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>com.chutneytesting</groupId>
        <artifactId>ui</artifactId>
        <scope>runtime</scope>
    </dependency>
    ```

Then, add dependency for your chosen database.

=== "maven"
    ``` xml
    <dependency> <!-- (1) -->
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
    </dependency>
    <dependency> <!-- (2) -->
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    ```

    1. If you want to use H2 as Chutney main database
    2. If you want to use PostgreSQL as Chutney main database

Also, you should add any dependencies you would need to run your scenarios.  
This may depend on the underlying Chutney actions you are using.

=== "maven"
    ``` xml
    <dependency> <!-- (1) -->
        <groupId>com.oracle</groupId>
        <artifactId>ojdbc6</artifactId>
        <version>x.x.x</version>
        <scope>runtime</scope>
    </dependency>
    <dependency> <!-- (2) -->
        <groupId>weblogic</groupId>
        <artifactId>wlthinclient</artifactId>
        <version>x.x.x</version>
        <scope>runtime</scope>
    </dependency>
    ```

    1. Example for using [SQL actions](/documentation/actions/sql) and query an Oracle database
    2. Example for using [JMS actions](/documentation/actions/jms) with a WebLogic server

Finally, add your own [Actions](/documentation/actions/) and [Functions](/documentation/functions/classpath) (see [extending Chutney](/documentation/actions/extension) for further details)

=== "maven"
    ``` xml
    <dependency>
        <groupId>com.my.company</groupId>
        <artifactId>chutney-extensions</artifactId>
        <version>x.x.x</version>
        <scope>runtime</scope>
    </dependency>
    ```

# Logback

A [Logback configuration](https://logback.qos.ch/manual/configuration.html){:target="_blank"} must be package in the packaging project, in classpath root.

!!! note "Logback configuration examples"

    === "Standard output"
        ``` xml
        <configuration>
            <appender name="stdout" class="ch.qos.logback.core.ConsoleAppender">
                <encoder>
                    <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
                </encoder>
            </appender>
    
            <root level="WARN">
                <appender-ref ref="stdout"/>
            </root>
        </configuration>
        ```

    === "Rolling file"
        ``` xml
        <configuration>
            <appender name="total" class="ch.qos.logback.core.rolling.RollingFileAppender"> 
                <file>total.log</file>
                <encoder>
                    <pattern>%d | %logger{16} | %level | %msg%n</pattern>
                </encoder>
                <rollingPolicy class="ch.qos.logback.core.rolling.FixedWindowRollingPolicy">
                    <fileNamePattern>total.%i.log</fileNamePattern>
                    <minIndex>1</minIndex>
                    <maxIndex>50</maxIndex>
                </rollingPolicy>
                <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
                    <maxFileSize>50MB</maxFileSize>
                </triggeringPolicy>
            </appender>
    
            <root level="WARN">
                <appender-ref ref="total"/>
            </root>
        </configuration>
        ```

# Application.yml

// TODO
