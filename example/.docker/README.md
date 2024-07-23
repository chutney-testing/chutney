### Develop docker image
* All Chutney objects should be in demo package.
* Develop with the docker compose.
* Finish by deleting the bdd manually and run a synchronization on a fresh bdd before commit.

### Build docker image

From project root folder, run:
```shell
docker build --tag ghcr.io/chutney-testing/chutney/chutney-demo:latest . -f ./.docker/demo/Dockerfile
```

### Push docker image to GitHub registry

Push will be done by GitHub actions during release workflow.

To push manually :
```shell
//login
docker login ghcr.io -u ${your_username} --password ${your_personal_github_token}
// push
docker push ghcr.io/chutney-testing/chutney/chutney-demo:latest
```

### Run demo server container using docker compose

```shell
docker-compose -f ./.docker/dev-docker-compose-demo.yml up -d
```

### Enjoy app

visit https://localhost

### Stop & remove docker compose services

**stop**

```shell
docker-compose -f ./.docker/dev-docker-compose-demo.yml stop
```

**remove**

```shell
docker-compose -f ./.docker/dev-docker-compose-demo.yml rm chutney-demo-server --force
```
