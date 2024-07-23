FROM ghcr.io/chutney-testing/chutney/chutney-server

LABEL org.opencontainers.image.source https://github.com/chutney-testing/chutney

ARG CONF=.docker/demo
COPY $CONF/conf/* /.chutney/
COPY $CONF/spring-config/* /config/
COPY $CONF/schema/* /schema/
ENV SPRING_PROFILES_ACTIVE=mem-auth,db-sqlite-rw
ENV SPRING_CONFIG_LOCATION=file:/config/
ENV LOADER_PATH=/schema/*
