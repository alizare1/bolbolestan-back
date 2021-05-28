# FROM maven:3.5.2-jdk-8-alpine as build
FROM maven:3.8.1-openjdk-11 as build
WORKDIR /tmp/
COPY pom.xml /tmp
COPY src /tmp/src
RUN mvn -Dhttps.protocols=TLSv1.2 package

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /tmp/target/*.war /app/app.war

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.war"]
