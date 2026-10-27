# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

RUN apt-get update \
    && apt-get install -y --no-install-recommends ca-certificates ca-certificates-java \
    && update-ca-certificates \
    && rm -rf /var/lib/apt/lists/*

COPY pom.xml ./
COPY usm-agent/pom.xml ./usm-agent/pom.xml
COPY usm-agent/src ./usm-agent/src
COPY usm-invnetory/pom.xml ./usm-invnetory/pom.xml
COPY usm-invnetory/src ./usm-invnetory/src

RUN mvn -q -Dmaven.wagon.http.ssl.insecure=true -pl usm-agent -am -DskipTests dependency:go-offline
RUN mvn -q -Dmaven.wagon.http.ssl.insecure=true -pl usm-agent -am -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/usm-agent/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
