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
// Run
docker-compose -f ./.docker/dev-docker-compose-demo.yml up -d
// Stop
docker-compose -f ./.docker/dev-docker-compose-demo.yml stop
// Force
docker-compose -f ./.docker/dev-docker-compose-demo.yml rm server --force
```

The Chutney web interface should be visible at http://localhost.

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
