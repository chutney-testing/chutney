# Spring Boot Server

Following section shows how to configure the [Spring Boot server](https://docs.spring.io/spring-boot/docs/2.7.x/reference/html/application-properties.html#appendix.application-properties.server){:target="_blank"}.

# Liquibase

// TODO

# Metrics

Since Chutney relies on Spring Boot [Actuator](#spring-boot-actuator) and [Micrometer](https://micrometer.io/){:target="_blank"} auto-configuration, it includes [Prometheus](https://micrometer.io/docs/registry/prometheus) by default.  
So you can find and use [default metrics](https://docs.spring.io/spring-boot/docs/2.7.x/reference/html/actuator.html#actuator.metrics.supported) : JVM, System, Datasource, Loggers, Executors and Spring MVC metrics.

Moreover, Chutney provides following metrics and corresponding Micrometer tags :

* `scenario_execution_count` counter (execution status, scenario id, scenario tags) is incremented after a scenario execution.
* `scenario_execution_timer` timer (execution status, scenario id, scenario tags) is recorded after a scenario execution.
* `scenario_in_campaign_gauge` gauge (campaign id, execution status) counts the scenario execution status after a campaign execution.
* `campaign_execution_count` counter (campaign id, campaign title, execution status) is incremented after a campaign execution..
* `campaign_execution_timer` timer (campaign id) is recorded after a campaign execution.

!!! important
    We won't document how to collect and manage your metrics outside Chutney.  
    Some hints could be :

    * Use the Actuator Prometheus endpoint to get the metrics with the appropriate format
    * Use push solution (Prometheus Pushgateway or custom)

# Authentication

!!! important
    Maven module [local-dev](https://github.com/chutney-testing/chutney/tree/master/packaging/local-dev) shows :

    * How to use in memory authentication and roles, see the `mem-auth` profile.
    * How to use a custom LDAP authentication (for example purpose, it uses an embedded LDAP server).

Chutney uses Spring Security for :

* Basic authentication
* Enforce authentication and check authorization on API (ex. admin rights Spring Boot [Actuator](#spring-boot-actuator) endpoints)
* Configuring in memory users and roles with a Spring profile [mem-auth](link) if needed


??? note "How to use in memory Spring profile `mem-auth`"
    * Activate the profile

    ``` yaml
    spring:
        profiles:
            active:
              - mem-auth
    ```

    * Declare users and roles

    ``` yaml
    chutney:
      security:
        users:
          -
            id: user
            name: user
            firstname: user
            lastname: user
            mail: user@user.com
            password: user-password-bcrypt
            roles: # (1)!
              - role-with-admin-in-it
    ```
    
    1. Optional, if the role include the characters 'admin', ignoring case, all permissions will be granted to that user.

!!! warning
    If you create a role name including characters 'admin' (ignoring case), all permissions will be granted to users with this role.

If you want to add another authentication mechanism, you should follow the [Spring security architecture](https://spring.io/guides/topicals/spring-security-architecture).

!!! important "Authentication requirements"
    The principal build by the authentication mechanism must be an instance of the Chutney [UserDto](https://github.com/chutney-testing/chutney/blob/master/server/src/main/java/com/chutneytesting/security/api/UserDto.java).

User roles and permissions are configured either with Web app form or by editing the file.

One could use the existing [AuthenticationService](https://github.com/chutney-testing/chutney/blob/master/server/src/main/java/com/chutneytesting/security/domain/AuthenticationService.java) Chutney Spring Bean to retrieve Chutney roles by user id and grant associated authorities. <!-- TODO : I don't understand what you mean, is it useful ? provide a real use case for showing why and how it could be done -->

!!! note "How to manage permissions"
    * A user can only have one role
    * Chutney permissions are defined in the [Authorization](https://github.com/chutney-testing/chutney/blob/master/server-core/src/main/java/com/chutneytesting/server/core/domain/security/Authorization.java) class.
    * The static `grantAuthoritiesFromUserRole` method of [UserDetailsServiceHelper](https://github.com/chutney-testing/chutney/blob/master/server/src/main/java/com/chutneytesting/security/infra/UserDetailsServiceHelper.java) class could be used to have the same authentication process than `mem-auth` profile,  
    i.e. if the user has a role name containing the characters 'admin', ignoring case, user will be given all authorities available, else he will be given the authorities associated by the role retrieved by the AuthenticationService.

# Compression

Spring Boot allows to configure compression on HTTP responses payloads.

Chutney Server stores scenarios executions reports and send them over the network, so it could be useful to use this configuration.

!!! note "Server compression configuration"
    ``` yaml
    server:
        compression:
            enabled: true
            mime-types: text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json # (1)
            min-response-size: 1024 # (2)
    ```
    
    1. The mime-types you want to compresse
    2. The minimum content length required for compression


# Session management

Spring Boot allows to configure session management.

!!! note "Server session configuration (with cookie)"
    ``` yaml
    server:
        servlet:
            session:
                timeout: 240m # (1)
                tracking-modes: cookie
            cookie:
                http-only: true # (2)
                secure: true # (3)
    ```

    1. The session timeout in minutes (example is 4 hours)
    2. Forbids Javascript to access the cookie
    3. Only for HTTPS requests

# Actuator

Spring Boot provides [production-ready features](https://docs.spring.io/spring-boot/docs/2.7.x/reference/html/actuator.html#actuator) with the Actuator module.
Since Chutney includes this module, you can also configure it.

!!! note "Actuator configuration examples"

    === "Total deactivation"
        ``` yaml
        management:
            server:
                port: -1
            endpoints:
                enabled-by-default: false
                web:
                    exposure:
                        exclude: "*"
                jmx:
                    exposure:
                        exclude: "*"
        ```

    === "Web activation simple example"
        ``` yaml
        management:
            endpoints:
                web:
                    exposure:
                        include: "*"
            endpoint:
                health:
                    show-details: always
        ```

!!! warning
    Chutney enforces `ADMIN_ACCESS` permissions on all default Actuator endpoints.


# Specifics values

Following table shows all properties you can set to configure Chutney.

| Name                                                    | Description                                                                                                       | Default value               |
|:--------------------------------------------------------|:------------------------------------------------------------------------------------------------------------------|:----------------------------|
| chutney.configuration-folder                            | Local directory path to data and configuration files                                                              | ~/.chutney/conf             |
| chutney.environment.configuration-folder                | Local directory path to environments data files                                                                   | ~/.chutney/conf/environment |
| chutney.jira.configuration-folder                       | Local directory path to jira data files                                                                           | ~/.chutney/conf/jira        |
| chutney.server.editions.ttl.value                       | Time to live value of unclosed scenario's editions                                                                | 6                           |
| chutney.server.editions.ttl.unit                        | Time to live time unit of unclosed scenario's editions                                                            | HOURS                       |
| chutney.server.execution.async.publisher.ttl            | Time to live in seconds of a finished observable scenario execution                                               | 5                           |
| chutney.server.execution.async.publisher.debounce       | Window time in milliseconds in which a running observable scenario execution ignores new associated engine report | 250                         |
| chutney.server.campaigns.executor.pool-size             | Pool size of campaigns' executor                                                                                  | 20                          |
| chutney.server.scheduled-campaigns.fixed-rate           | Fixed time period for scheduled campaigns execution checking                                                      | 60000                       |
| chutney.server.scheduled-campaigns.executor.pool-size   | Pool size of scheduled campaigns' executor                                                                        | 20                          |
| chutney.server.agent.name                               | Default name of local agent                                                                                       |                             |
| chutney.server.agent.hostname                           | Default hostname of local agent                                                                                   |                             |
| chutney.server.agent.network.connection-checker-timeout | Socket timeout in milliseconds for agent networking management actions                                            | 1000                        |
| chutney.engine.executor.pool-size                       | Pool size of scenarios' executor                                                                                  | 20                          |
| chutney.engine.delegation.user                          | Username of engine's delegation service HTTP client                                                               |                             |
| chutney.engine.delegation.password                      | Password of engine's delegation service HTTP client                                                               |                             |
| chutney.actions.sql.max-logged-rows                     | Max logged rows in report for SQL action                                                                          | 30                          |
| chutney.component.orient.path                           | Local directory path to component data                                                                            | ~/.chutney/orient           |
| chutney.component.orient.dBProperties.dbName            | Database name of component data                                                                                   | chutney_component_db        |
| chutney.component.orient.contextConfiguration           | Database configuration map of component data                                                                      |                             |

