### Build docker images

From project root folder, run:

**server**
```shell
docker build --tag ghcr.io/chutney-testing/chutney/chutney-server:latest . -f ./.docker/server/Dockerfile
```

**ui**
```shell
docker build --tag ghcr.io/chutney-testing/chutney/chutney-ui:latest . -f ./.docker/ui/Dockerfile
```

### Push docker image to github registry

Push will be done by github actions during release workflow.

To push manually :
```shell
//login
docker login ghcr.io -u ${your_username} --password ${your_personal_github_token}
// push
docker push ghcr.io/chutney-testing/chutney/chutney-server:latest
docker push ghcr.io/chutney-testing/chutney/chutney-ui:latest
```

### Run ui and server containers using docker compose

```shell
docker-compose -f ./.docker/docker-compose-local-dev.yml up -d
```

**Notes :**

* By default, server container will run with local-dev configuration(see packaging/local-dev module)
* It's possible to override default configuration by passing configuration folder as volume when running server container (see docker-compose-custom-config.yml file for more details)

### Enjoy app

visit https://localhost

### Stop & remove docker compose services

**stop**

```shell
docker-compose -f ./.docker/docker-compose-local-dev.yml stop
```

**remove**

```shell
docker-compose -f ./.docker/docker-compose-local-dev.yml rm server ui --force
```
