<!--
  ~ SPDX-FileCopyrightText: 2017-2024 Enedis
  ~
  ~ SPDX-License-Identifier: Apache-2.0
  ~
  -->

# Changelog

## [3.0.0](https://github.com/chutney-testing/chutney/tree/3.0.0)
### 🚀 Implemented enhancements:
* Zip scenario execution report in db & index it for search using lucene by @KarimGl in https://github.com/chutney-testing/chutney/pull/208
* Schedule campaign: Overload environment and dataset by @nbrouand in https://github.com/chutney-testing/chutney/pull/191
* SSH action proxy jump by @boddissattva in https://github.com/chutney-testing/chutney/pull/219
* Sql action can read blob type by @rbenyoussef in https://github.com/chutney-testing/chutney/pull/220
### 🔧 Technical enhancements:
* Fix dsl javadoc generation by @KarimGl in https://github.com/chutney-testing/chutney/pull/224

**Full Changelog**: https://github.com/chutney-testing/chutney/compare/2.9.0.7...3.0.0

## [2.9.0.7](https://github.com/chutney-testing/chutney/tree/2.9.0.7)
### 🚀 Implemented enhancements:
* Add SpEL support to RetryWithTimeOutStrategy by @Redouane-E in https://github.com/chutney-testing/chutney/pull/203
* Show dataset usage in dataset page by @DelaunayAlex in https://github.com/chutney-testing/chutney/pull/204
### 🐛 Fixed bugs:
* Check test plan only when the campaign is linked with jira issue by @DelaunayAlex in https://github.com/chutney-testing/chutney/pull/218
* Preserve input type within for strategy by @rbenyoussef in https://github.com/chutney-testing/chutney/pull/207
### 🔧 Technical enhancements:
* Remove oracle and ibm example dependency by @nbrouand in https://github.com/chutney-testing/chutney/pull/206
* Fix build in a wsl+docker dev env by @boddissattva in https://github.com/chutney-testing/chutney/pull/205
### 👒 Dependencies:
* Bump jqwik.version from 1.9.0 to 1.9.1 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/201
* Bump org.testcontainers:testcontainers-bom from 1.20.1 to 1.20.2 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/200
* Bump com.hierynomus:sshj from 0.38.0 to 0.39.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/197
* Bump com.github.eirslett:frontend-maven-plugin from 1.15.0 to 1.15.1 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/195
* Bump org.codehaus.mojo:exec-maven-plugin from 3.2.0 to 3.4.1 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/193
* Bump org.apache.maven.plugins:maven-javadoc-plugin from 3.10.0 to 3.10.1 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/192
* Bump org.liquibase:liquibase-core from 4.29.1 to 4.29.2 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/194
* Bump mapstruct.version from 1.6.0 to 1.6.2 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/196
* Bump org.apache.maven.plugins:maven-surefire-plugin from 3.3.0 to 3.5.1 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/210
* Bump commons-io:commons-io from 2.16.1 to 2.17.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/213
* Bump sshd.version from 2.13.2 to 2.14.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/214
* Bump org.springframework.boot:spring-boot-maven-plugin from 3.3.3 to 3.3.5 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/217

**Full Changelog**: https://github.com/chutney-testing/chutney/compare/2.9.0.6...2.9.0.7

## [2.9.0.6](https://github.com/chutney-testing/chutney/tree/2.9.0.6)
### 🚀 Implemented enhancements:
* Request scenarios' executions to stop when stopping campaign execution by @boddissattva in https://github.com/chutney-testing/chutney/pull/185
* Can set the key in Kafka producer, can read the key in Kafka consumer by @DelaunayAlex in https://github.com/chutney-testing/chutney/pull/190
### 🐛 Fixed bugs:
* Add idea report missing attributes by @KarimGl in https://github.com/chutney-testing/chutney/pull/187
* Fix selenium screen-shot action by @rbenyoussef in https://github.com/chutney-testing/chutney/pull/202
### 🔧 Technical enhancements:
* Remove glacio, add kotlin dsl acceptance test by @nbrouand in https://github.com/chutney-testing/chutney/pull/168
* Dsl as maven project. Add parent maven project by @KarimGl in https://github.com/chutney-testing/chutney/pull/186
* Fix .gitignore by @boddissattva in https://github.com/chutney-testing/chutney/pull/189

## [2.9.0.5](https://github.com/chutney-testing/chutney/tree/2.9.0.5)

### 🚀 Implemented enhancements:
* Supervise demo instance with prometheus and grafana by @KarimGl in https://github.com/chutney-testing/chutney/pull/167
### 🐛 Fixed bugs:
* Fix/dataset jira by @DelaunayAlex in https://github.com/chutney-testing/chutney/pull/184

**Full Changelog**: https://github.com/chutney-testing/chutney/compare/2.9.0.4...2.9.0.5

## [2.9.0.4](https://github.com/chutney-testing/chutney/tree/2.9.0.4)
### 🚀 Implemented enhancements:
* Create example/demo module by @boddissattva in https://github.com/chutney-testing/chutney/pull/140
* Add button with modal that allow the user to execute a campaign with specified env and dataset by @DelaunayAlex in https://github.com/chutney-testing/chutney/pull/141
* Allow delete scenario executions by @rbenyoussef in https://github.com/chutney-testing/chutney/pull/169
* Feature/create dataset modal exec by @DelaunayAlex in https://github.com/chutney-testing/chutney/pull/150
* Show dataset used in pdf report by @rbenyoussef in https://github.com/chutney-testing/chutney/pull/152
### 🐛 Fixed bugs:
* Use jira id mapped to dataset_scenario pair when available by @rbenyoussef in https://github.com/chutney-testing/chutney/pull/153
* Fix target import by @KarimGl in https://github.com/chutney-testing/chutney/pull/165
* Fix selenium driver init actions by @rbenyoussef in https://github.com/chutney-testing/chutney/pull/182
### 🔧 Technical enhancements:
* Use codeql v3 by @KarimGl in https://github.com/chutney-testing/chutney/pull/164 and https://github.com/chutney-testing/chutney/pull/166
* Add JUnit 5 test dependencies to example project by @boddissattva in https://github.com/chutney-testing/chutney/pull/181
* Support idea 242 by @boddissattva in https://github.com/chutney-testing/chutney/pull/183
* REUSE workflow + add missing license by @KarimGl in https://github.com/chutney-testing/chutney/pull/151
### 👒 Dependencies:
* Bump org.liquibase:liquibase-core from 4.27.0 to 4.29.1 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/142
* Bump org.apache.commons:commons-text from 1.10.0 to 1.12.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/143
* Bump org.apache.maven.plugins:maven-surefire-plugin from 3.3.0 to 3.3.1 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/144
* Bump org.testcontainers:testcontainers-bom from 1.19.8 to 1.20.1 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/149
* Bump org.apache.maven.plugins:maven-javadoc-plugin from 3.6.3 to 3.8.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/156
* Bump org.apache.maven.plugins:maven-gpg-plugin from 3.2.4 to 3.2.5 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/158
* Bump mapstruct.version from 1.5.5.Final to 1.6.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/159
* Bump org.springframework.boot:spring-boot-maven-plugin from 3.3.1 to 3.3.2 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/160
* Bump org.apache.maven.plugins:maven-deploy-plugin from 3.1.2 to 3.1.3 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/179
* Bump org.apache.maven.plugins:maven-javadoc-plugin from 3.8.0 to 3.10.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/178
* Bump org.wiremock:wiremock-standalone from 3.8.0 to 3.9.1 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/177
* Bump org.apache.maven.plugins:maven-surefire-plugin from 3.3.1 to 3.5.0 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/176
* Bump org.springframework.boot:spring-boot-maven-plugin from 3.3.2 to 3.3.3 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/174
* Bump org.apache.maven.plugins:maven-source-plugin from 3.3.0 to 3.3.1 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/173
* Bump sshd.version from 2.13.1 to 2.13.2 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/172
* Bump commons-logging:commons-logging from 1.3.1 to 1.3.4 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/171
* Bump org.apache.maven.plugins:maven-install-plugin from 3.1.2 to 3.1.3 in /chutney by @dependabot in https://github.com/chutney-testing/chutney/pull/170


**Full Changelog**: https://github.com/chutney-testing/chutney/compare/2.9.0.3...2.9.0.4

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
