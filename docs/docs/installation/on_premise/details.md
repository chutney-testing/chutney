# Database

[Liquibase](https://www.liquibase.org/){:target="_blank"} is used to manage Chutney RDBMS schema.  
You can find corresponding changelog [here](https://github.com/chutney-testing/chutney/blob/master/server/src/main/resources/changelog/db.changelog-master.xml){:target="_blank"}.

!!! note
    Chutney has been tested with H2 and PostgreSQL databases.

To configure your datasource, use the property `spring.datasource`

=== "H2 (memory)"
    ``` yaml
    spring:
        datasource:
            url: jdbc:h2:mem:dbName
    ```
    !!! note
        You can find an example in maven module [local-dev](https://github.com/chutney-testing/chutney/tree/master/packaging/local-dev){:target="_blank"}, which uses an embedded H2 with filesystem persistence.

=== "PostgreSQL (SSL two way)"
    ``` yaml
    spring:
        datasource:
            url: jdbc:postgresql://host:port/dbName?ssl=true&sslmode=verify-ca&sslfactory=org.postgresql.ssl.DefaultJavaSSLFactory&currentSchema=mySchema
            username: user
    ```

# Logs

Chutney depends on [SLF4J](https://www.slf4j.org/){:target="_blank"} API logging library.

At runtime, the Chutney server use the [Logback](https://logback.qos.ch/){:target="_blank"} SLF4J implementation and bridges all legacy APIs (JCL, LOG4J and JUL).

!!! warning
Since the server bridges all legacy APIs, you must be careful to not include any of the following libraries :

    * jcl-over-slf4j
    * log4j-over-slf4j and slf4j-reload4j
    * jul-to-slf4j

    Read [Bridging legacy APIs](https://logback.qos.ch/manual/configuration.html){:target="_blank"} for further details.

A [Logback configuration](https://logback.qos.ch/manual/configuration.html){:target="_blank"} must be package in the packaging project, in classpath root.

??? note "Logback configuration examples"

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

# Server (TLS/SSL)

Chutney server enforces the use of secure calls on any incoming requests.

    !!! note "Server HTTPS configuration"
    ``` yaml
    server:
        port: 443
        ssl:
            keystore: # keystore path
            key-store-password: # keystore password
            key-password: # key password
            trust-store: # truststore path
            trust-store-password: # truststore password
    ```

Chutney Server provides `undertow-https-redirect` Spring profile to redirect unsecured request to the right secured port.

??? note "Using `undertow-https-redirect` Spring profile"

    * Activate the profile

    ``` yaml
    spring:
        profiles:
            active:
              - undertow-https-redirect
    ```

    * Configure the HTTP listener

    ``` yaml
    server:
        http:
            port: 80 # (1)
            interface: 0.0.0.0 # (2)
    ```

    1. HTTP port to use
    2. Interface to bind to

