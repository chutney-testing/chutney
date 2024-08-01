## Develop docker image
* Use the demo java package to declare all required Chutney objects and synchronization functions.
* Develop with the docker compose (cf. next paragraph).
* Finish by deleting the bdd manually and run a synchronization on a fresh bdd before commit.

## Demo server container using docker compose

```shell
// Run
docker-compose -f ./.docker/dev-docker-compose-demo.yml up -d
// Stop
docker-compose -f ./.docker/dev-docker-compose-demo.yml stop
// Force
docker-compose -f ./.docker/dev-docker-compose-demo.yml rm chutney-demo-server --force
```

The Chutney web interface should be visible at https://localhost.

> **Notes** :
>
> In order to have successful scenarios' executions, it could be required to set a proxy on targets which use some
> public endpoints.

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
