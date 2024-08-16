#SPDX-FileCopyrightText: 2017-2024 Enedis
#SPDX-License-Identifier: Apache-2.0

FROM eclipse-temurin:17-jre
VOLUME /config
VOLUME /.chutney
ARG JAR_PATH=packaging/local-dev/target
COPY $JAR_PATH/chutney-local-dev-*.jar app.jar
LABEL org.opencontainers.image.source https://github.com/chutney-testing/chutney
EXPOSE 8443
CMD java -jar /app.jar
