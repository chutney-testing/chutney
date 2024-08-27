<!--
  ~ SPDX-FileCopyrightText: 2017-2024 Enedis
  ~
  ~ SPDX-License-Identifier: Apache-2.0
  ~
-->

## Develop the demo docker image

* Use the demo java package to declare all required Chutney objects and synchronization functions.
* Develop with the docker compose (cf. next paragraph).
* The database file commited should be a newly fresh database with synchronization done.

> **Notes** :
>
> * The demo has scenario which use public apis. In order to have successful scenarios' executions, it could be required to set
> a proxy on some demo targets.
> * If changes have been made to the compose file, it should be mirrored to the Dockerfile.

## Demo server container using docker compose

```shell
// from example project folder
// Run
docker-compose -f ./.docker/dev-docker-compose-demo.yml up -d
// Stop
docker-compose -f ./.docker/dev-docker-compose-demo.yml stop
// Remove
docker-compose -f ./.docker/dev-docker-compose-demo.yml rm
```
> **Notes**:
> * The Chutney web interface should be visible at http://localhost. Try to execute some scenarios and campaigns to generate some data that will be used in dashboards bellow.
> * The Grafana web interface should be visible at http://localhost:3000/dashboards. Some dashboards examples are already done by the team:
>   * Chutney [jvm](http://localhost:3000/d/cdw2ubi8bk1kwc/jvm-micrometer?orgId=1&refresh=30s&from=now-1h&to=now) and [scenarios executions](http://localhost:3000/d/edw39vvnep88wa/scenarios-executions?orgId=1&from=now-1h&to=now&refresh=30s).
>   * Prometheus [stats](http://localhost:3000/d/UDdpyzz7z/prometheus-2-0-stats?orgId=1&refresh=1m).

## Build docker image

From project root folder, run:
```shell
docker build --tag ghcr.io/chutney-testing/chutney/chutney-demo:latest . -f ./.docker/demo/Dockerfile
```

## Push docker image to GitHub registry

Push will be done by GitHub actions during release workflow.

To push manually :
```shell
//login
docker login ghcr.io -u ${your_username} --password ${your_personal_github_token}
// push
docker push ghcr.io/chutney-testing/chutney/chutney-demo:latest
```
