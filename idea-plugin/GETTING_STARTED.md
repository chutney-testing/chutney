## Getting Started

### Build plugin

- Build and install Chutney (`./mvn clean install -DskipTests`)
- Build and install kotlin-dsl (`./gradlew clean builld -x test :chutney-kotlin-dsl:publishToMavenLocal`)
- Install local-api-insecure-jar (use install-local-api-unsecure-jar [run configuration](https://github.com/chutney-testing/chutney/blob/main/.idea/runConfigurations/install_local_api_unsecure_jar.xml))
- `./gradlew clean buildPlugin`