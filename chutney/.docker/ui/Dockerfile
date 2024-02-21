FROM nginx:1.17.3-alpine
ARG WEBSITE=ui/dist
ARG CONF=.docker/ui
COPY $WEBSITE/chutney /usr/share/nginx/html
COPY $CONF/nginx/nginx.conf  /etc/nginx/conf.d/default.conf
COPY $CONF/certif  /certif
LABEL org.opencontainers.image.source https://github.com/chutney-testing/chutney
EXPOSE 80 443
