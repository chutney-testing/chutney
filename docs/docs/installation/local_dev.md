
You can run scenarios without installing a Chutney server. This fits well within a CI or for a developer local setup.

However, building software is most often a teamwork !  
Doing so, you will need to collaborate and share scenarios, track their executions 
and allow functional and business analyst to review and be involved in testing their product.

That's why we provide a server and web UI to help us do all these things.

You can find all code and configuration below in this [example project](https://github.com/chutney-testing/chutney-project-template){:target="_blank"}

# Start a server

!!! note "Docker"

    1. Checkout this [example project](https://github.com/chutney-testing/chutney-project-template)
    2. Start Chutney locally with `docker compose up&` ([Docker compose documentation](https://docs.docker.com/compose/)).

!!! note "Java"

    1. Download the latest release jar [chutney-local-dev-x.x.x.jar](https://github.com/chutney-testing/chutney/releases/latest).
    2. Start Chutney locally with `java -jar chutney-local-dev-x.x.x.jar`

!!! note "Intellij"

    1. Checkout [chutney](https://github.com/chutney-testing/chutney).
    2. Build the project using maven : `mvn compile [-DuseExternalNpm]`
    3. Start [Intellij run configuration](https://www.jetbrains.com/help/idea/run-debug-configuration.html) `start_local_server`

