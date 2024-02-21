When using Chutney to test your applications, you may need proprietary drivers, clients or use an obscure protocol not implemented by Chutney.

**In order to do this, you have to make your own custom package.**

For example, if you use JMS with Oracle WebLogic, you have to package Chutney with the Oracle WebLogic client as a runtime dependency.  
Another use case is when you need an Action for something we don't provide, we are open to requests but if it's proprietary and cannot be open, then you have to implement your own Action and package Chutney with it.

Moreover,  
If you intend to use a shared Chutney server, you may need to integrate to an external database or authentication system.  
In order to do this, some configurations require to be done with Spring, so you have to make your own Chutney package.

This page will guide you on how to :

- Use Chutney with proprietary drivers or clients
- Use Chutney with an external database and authentication system
- Configure logs, SSL/TLS, sessions, metrics, etc.

!!! important "Quick technical insight"

    * Chutney server is a [Spring Boot](https://docs.spring.io/spring-boot/docs/2.7.x/reference/html/index.html){:target="_blank"} application running with [Undertow](https://undertow.io/) and based on a standard Spring stack (mvc, webflux, security){:target="_blank"}
    * Chutney UI is an [Angular](https://angular.io/){:target="_blank"} web application
    * Chutney is packaged as a [Spring Boot executable jar](https://docs.spring.io/spring-boot/docs/2.7.x/reference/html/executable-jar.html#appendix.executable-jar){:target="_blank"}
    * Chutney follows Angular and Spring Boot lastest versions and corresponding dependencies

# Configuration

In addition to java dependencies,
you may have to provide your own configuration for your database, authentication system, user roles and permissions, logs etc.

Configuration is done by setting [Spring Boot](https://docs.spring.io/spring-boot/docs/2.7.x/reference/html/application-properties.html#appendix.application-properties) or [Chutney](#chutney-specifics) properties.

In order to do this, you have to edit the default Spring Boot configuration file `application.yml`.

!!! warning "Handling secrets"
    How to handle secrets in configuration files varies a lot and depends on your CI/CD so this documentation does not cover this topic.  
    One example, if you use [Ansible](https://docs.ansible.com/ansible/latest/index.html), you can package a subset of configuration files, select and filter them during deployment, so they will be included in the runtime classpath of the application.
