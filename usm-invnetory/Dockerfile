# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

RUN apt-get update \
    && apt-get install -y --no-install-recommends ca-certificates ca-certificates-java \
    && update-ca-certificates \
    && rm -rf /var/lib/apt/lists/*

COPY pom.xml ./
COPY usm-invnetory/pom.xml ./usm-invnetory/pom.xml
COPY usm-invnetory/src ./usm-invnetory/src
COPY usm-agent/pom.xml ./usm-agent/pom.xml
COPY usm-agent/src ./usm-agent/src

RUN mvn -q -Dmaven.wagon.http.ssl.insecure=true -pl usm-invnetory -am -DskipTests dependency:go-offline
RUN mvn -q -Dmaven.wagon.http.ssl.insecure=true -pl usm-invnetory -am -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/usm-invnetory/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
