#FROM maven:latest as builder
#COPY pom.xml /Users/amtz/gepg/logs/
#WORKDIR /Users/amtz/gepg/logs/
#
#
#RUN mvn clean install
#

# For Java 8, try this
# FROM openjdk:8-jdk-alpine

# For Java 11, try this
FROM adoptopenjdk/openjdk11:alpine-jre


LABEL service="trab-backend"
LABEL version="V-0.0.1"
LABEL description="Trab Project - Revenue Appeal System"


EXPOSE 9094
WORKDIR /opt/app/

ARG JAR_FILE=target/gepg_trab-0.0.1-SNAPSHOT.jar

# cp spring-boot-web.jar /opt/app/app.jar
COPY ${JAR_FILE} app.jar

# java -jar /opt/app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]


