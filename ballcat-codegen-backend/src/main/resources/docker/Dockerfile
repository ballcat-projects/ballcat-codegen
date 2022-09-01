# syntax=docker/dockerfile:1
# https://docs.docker.com/language/java/build-images/
FROM eclipse-temurin:11-jdk-jammy

MAINTAINER <mobaijun8@163.com>mobaijun

VOLUME /usr/local/codegen

ADD ballcat-codegen.jar ballcat-codegen.jar

ENTRYPOINT ["java","-Xms256m","-Xmx512m","-XX:PermSize=128M","-XX:MaxPermSize=256M","-jar","ballcat-codegen.jar"]