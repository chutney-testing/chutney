# Changelog

## [2.9.0.3](https://github.com/chutney-testing/chutney/tree/2.9.0.3)
### 🚀 Implemented enhancements:
* Scenario execute with parameters by @rbenyoussef in https://github.com/chutney-testing/chutney/pull/121
### 🐛 Fixed bugs:
* Campaign edition - JIRA filter not present by @boddissattva in https://github.com/chutney-testing/chutney/pull/119
* Break long word in admin bdd search, fix overflow ng-multi… by @DelaunayAlex in https://github.com/chutney-testing/chutney/pull/120
* Use 24h formats for moment calendar by @boddissattva in https://github.com/chutney-testing/chutney/pull/134
* Purge empty campaigns' executions by @boddissattva in https://github.com/chutney-testing/chutney/pull/122
* Does not allow empty campaign execution by @boddissattva in https://github.com/chutney-testing/chutney/pull/133
* Responsive dataset select in campaign edition by @DelaunayAlex in https://github.com/chutney-testing/chutney/pull/139
* Snapshot report's inputs/outputs in engine after each step execution by @DelaunayAlex in https://github.com/chutney-testing/chutney/pull/136
* Fix CONTRIBUTING.md path by @GeVa2072 in https://github.com/chutney-testing/chutney/pull/138
* Fix tag selection on scenarios list by @rbenyoussef in https://github.com/chutney-testing/chutney/pull/137
### 👒 Dependencies:
* Bump org.wiremock:wiremock-standalone from 3.6.0 to 3.8.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/125
* Bump org.codehaus.mojo:build-helper-maven-plugin from 3.5.0 to 3.6.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/126
* Bump sshd.version from 2.12.0 to 2.13.1 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/128
* Bump org.springframework.boot:spring-boot-maven-plugin from 3.2.4 to 3.3.1 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/129
* Bump commons-io:commons-io from 2.16.0 to 2.16.1 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/130
* Bump org.apache.maven.plugins:maven-clean-plugin from 3.3.2 to 3.4.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/131
* Bump org.apache.maven.plugins:maven-surefire-plugin from 3.2.5 to 3.3.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/132
* Bump jqwik.version from 1.8.5 to 1.9.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/127

**Full Changelog**: https://github.com/chutney-testing/chutney/compare/2.9.0.2...2.9.0.3

## [2.9.0.2](https://github.com/chutney-testing/chutney/tree/2.9.0.2)

### 🐛 Fixed bugs:
* Content search only on activated scenario by @rbenyoussef in https://github.com/chutney-testing/chutney/pull/114
* Fix campaign edition filters width by @rbenyoussef in https://github.com/chutney-testing/chutney/pull/115
* Update campaigns when renaming environment by @DelaunayAlex in https://github.com/chutney-testing/chutney/pull/87
* Fix campaign execution status by @boddissattva in https://github.com/chutney-testing/chutney/pull/116
* Delete dataset module HTML title by @boddissattva in https://github.com/chutney-testing/chutney/pull/118
* Use the id instead of the name for scenario dataset in cam… by @DelaunayAlex in https://github.com/chutney-testing/chutney/pull/117

**Full Changelog**: https://github.com/chutney-testing/chutney/compare/2.9.0.1...2.9.0.2

## [2.9.0.1](https://github.com/chutney-testing/chutney/tree/2.9.0.1)

### 🐛 Fixed bugs:
* Scenarios list refresh must be unsubscribed by @boddissattva in https://github.com/chutney-testing/chutney/pull/113

**Full Changelog**: https://github.com/chutney-testing/chutney/compare/2.9.0...2.9.0.1

## [2.9.0](https://github.com/chutney-testing/chutney/tree/2.9.0)

### 🚀 Implemented enhancements:
* Replay scenario execution by @KarimGl in https://github.com/chutney-testing/chutney/pull/86
* Set tag from campaign to scenario execution by @DelaunayAlex in https://github.com/chutney-testing/chutney/pull/85
* Update tag design  in table for campaign executions by @DelaunayAlex in https://github.com/chutney-testing/chutney/pull/90
* Select dataset for each scenario in campaign by @DelaunayAlex in https://github.com/chutney-testing/chutney/pull/84
* Show dataset content in execution report by @KarimGl in https://github.com/chutney-testing/chutney/pull/88
* Order environment by name on target edition by @nbrouand in https://github.com/chutney-testing/chutney/pull/110
* Externalize jira link edition for scenarios by @rbenyoussef in https://github.com/chutney-testing/chutney/pull/109
### 🐛 Fixed bugs:
* Update scenario list every 3 seconds when running by @DelaunayAlex in https://github.com/chutney-testing/chutney/pull/49
* Remove ng-scrollbar by @nbrouand in https://github.com/chutney-testing/chutney/pull/89
* Allow no proxy configuration for JIRA configuration by @boddissattva in https://github.com/chutney-testing/chutney/pull/105
* Resolve api/name error message conflict on envs list by @KarimGl in https://github.com/chutney-testing/chutney/pull/106
* Cursor pointer on report step title hover by @KarimGl in https://github.com/chutney-testing/chutney/pull/107
* Fix scenarios list scroll by @KarimGl in https://github.com/chutney-testing/chutney/pull/108
### 🔧 Technical enhancements:
* Take into account documentation label in Changelog template by @boddissattva in https://github.com/chutney-testing/chutney/pull/83
* Set dynamic ports for servers' start test by @boddissattva in https://github.com/chutney-testing/chutney/pull/103
* Fix old table deletion by @KarimGl in https://github.com/chutney-testing/chutney/pull/91
* Change scenarios default dataset id from "null" to null by @KarimGl in https://github.com/chutney-testing/chutney/pull/92
### 👒 Dependencies:
* Ng17 by @KarimGl in https://github.com/chutney-testing/chutney/pull/81
* Bump org.wiremock:wiremock-standalone from 3.3.1 to 3.6.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/99
* Bump org.apache.maven.plugins:maven-jar-plugin from 3.3.0 to 3.4.1 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/94
* Bump org.apache.maven.plugins:maven-install-plugin from 3.1.0 to 3.1.2 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/93
* Bump org.apache.maven.plugins:maven-enforcer-plugin from 3.4.1 to 3.5.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/101
* Bump jqwik.version from 1.8.4 to 1.8.5 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/98
* Bump org.testcontainers:testcontainers-bom from 1.19.6 to 1.19.8 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/95

**Full Changelog**: https://github.com/chutney-testing/chutney/compare/2.8.0.1...2.9.0

## [2.8.0.1](https://github.com/chutney-testing/chutney/tree/2.8.0.1)

## What's Changed
### 🚀 Implemented enhancements:
* Enable offset reset before consuming from kafka by @KarimGl in https://github.com/chutney-testing/chutney/pull/38
* Scenario execution inherits scenario and dataset tags by @boddissattva in https://github.com/chutney-testing/chutney/pull/46
* Iterate over dataset constants when datatable is empty by @KarimGl in https://github.com/chutney-testing/chutney/pull/48
* Execute campaign on different environments in parallel by @KarimGl in https://github.com/chutney-testing/chutney/pull/63
* Free system port function by @boddissattva in https://github.com/chutney-testing/chutney/pull/64
* Kotlin DSL with scenario tags, dataset and campaign synchronization by @boddissattva in https://github.com/chutney-testing/chutney/pull/61
* Purge - Keep executions newer than 24 hours by @KarimGl in https://github.com/chutney-testing/chutney/pull/62
* Time rules spEL functions by @boddissattva in https://github.com/chutney-testing/chutney/pull/68
* Campaign with scenario-dataset pairs by @boddissattva in https://github.com/chutney-testing/chutney/pull/67
### 🐛 Fixed bugs:
* Fix confirm-dialog component title by @KarimGl in https://github.com/chutney-testing/chutney/pull/50
### 🔧 Technical enhancements:
* Log kafka consume errors by @KarimGl in https://github.com/chutney-testing/chutney/pull/44
* Audit api call (not GET) by @nbrouand in https://github.com/chutney-testing/chutney/pull/45
* Delete dataset version property by @boddissattva in https://github.com/chutney-testing/chutney/pull/65
* Use dynamic port for jms tests by @boddissattva in https://github.com/chutney-testing/chutney/pull/82
### 📖 Documentation
* Fix broken links to code and img by @rbenyoussef in https://github.com/chutney-testing/chutney/pull/47
* Change README by @DelaunayAlex in https://github.com/chutney-testing/chutney/pull/66
### 👒 Dependencies:
* Bump net.jqwik:jqwik from 1.8.1 to 1.8.4 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/51
* Bump org.apache.maven.plugins:maven-compiler-plugin from 3.12.1 to 3.13.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/54
* Bump org.apache.maven.plugins:maven-gpg-plugin from 1.6 to 3.2.2 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/55
* Bump qpid-broker.version from 9.1.0 to 9.2.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/57
* Bump org.apache.maven.plugins:maven-surefire-plugin from 3.2.3 to 3.2.5 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/58
* Bump commons-io:commons-io from 2.15.1 to 2.16.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/60
* Bump org.apache.cxf:cxf-xjc-plugin from 4.0.0 to 4.0.1 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/53
* Bump org.springframework.boot:spring-boot-maven-plugin from 3.1.2 to 3.2.4 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/56
* Bump org.apache.maven.plugins:maven-deploy-plugin from 3.0.0 to 3.1.2 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/80
* Bump commons-logging:commons-logging from 1.3.0 to 1.3.1 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/79
* Bump org.apache.maven.plugins:maven-gpg-plugin from 3.2.2 to 3.2.4 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/71
* Bump org.liquibase:liquibase-core from 4.25.1 to 4.27.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/72
* Bump org.apache.activemq.tooling:activemq-junit from 5.18.3 to 6.1.2 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/76

**[Full Changelog]( https://github.com/chutney-testing/chutney/compare/2.8.0...2.8.0.1)**

## [2.8.0](https://github.com/chutney-testing/chutney/tree/2.8.0)

### 🚀 Implemented enhancements:
* Export campaign execution report as pdf by @rbenyoussef in https://github.com/chutney-testing/chutney/pull/36
### 🐛 Fixed bugs:
* Fix startNotify called already excpetion on json execution by @boddissattva in https://github.com/chutney-testing/chutney/pull/41
### 🔧 Technical enhancements:
* Throttle async scenario execution and SQLite routing r/w datasources example by @boddissattva in https://github.com/chutney-testing/chutney/pull/40
* Remove scroll on click by @nbrouand in https://github.com/chutney-testing/chutney/pull/39
* Regex does not work in github actions by @nbrouand in https://github.com/chutney-testing/chutney/pull/42

**[Full Changelog](https://github.com/chutney-testing/chutney/compare/2.7.0.1...2.8.0)**

## [2.7.0.1](https://github.com/chutney-testing/chutney/tree/2.7.0.1)

### 🚀 Implemented enhancements:
* Increase close area on executions tabs by @KarimGl in https://github.com/chutney-testing/chutney/pull/14
* Right menu -> middle menu by @nbrouand in https://github.com/chutney-testing/chutney/pull/18
### 🐛 Fixed bugs:
* Get campaign fetches executions from db by @KarimGl in https://github.com/chutney-testing/chutney/pull/32
* getLastExecutions returns the last running exec if available, otherwise the last exec by @KarimGl in https://github.com/chutney-testing/chutney/pull/33
* Remove write numbers as strings in reports by @KarimGl in https://github.com/chutney-testing/chutney/pull/11
### 🔧 Technical enhancements:
* log login and logout actions by @KarimGl in https://github.com/chutney-testing/chutney/pull/20
* Clean JIRA service log by @boddissattva in https://github.com/chutney-testing/chutney/pull/19
* Updating contributing for new release management by @nbrouand in https://github.com/chutney-testing/chutney/pull/31
* Chutney selenium action test - Use correct web driver output key by @boddissattva in https://github.com/chutney-testing/chutney/pull/35
* Allow build with TestContainers' test for kotlin-dsl on docker in WSL2 without desktop by @boddissattva in https://github.com/chutney-testing/chutney/pull/37
* Clean transitive dependencies from kotlin-dsl & server packaging jar name by @KarimGl in https://github.com/chutney-testing/chutney/pull/12
### 👒 Dependencies:
* Bump org.codehaus.mojo:exec-maven-plugin from 3.1.1 to 3.2.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/21
* Bump org.codehaus.mojo:build-helper-maven-plugin from 3.3.0 to 3.5.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/25
* Bump org.testcontainers:testcontainers-bom from 1.19.3 to 1.19.6 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/29

**[Full Changelog](https://github.com/chutney-testing/chutney/compare/Mono-repo...2.7.0.1)**

## Please check release note before 2.7.0.1 here :

- [chutney](https://github.com/chutney-testing/chutney-legacy/releases)
- [kotlin-dsl](https://github.com/chutney-testing/chutney-kotlin-dsl-legacy/releases)
- [documentation](https://github.com/chutney-testing/chutney-testing.github.io-legacy)
- [plugin](https://github.com/chutney-testing/chutney-idea-plugin-legacy/releases/)
