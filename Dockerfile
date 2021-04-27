FROM openjdk:8-jre

WORKDIR /app

COPY server/build/libs/servermanager-server-*.jar server.jar