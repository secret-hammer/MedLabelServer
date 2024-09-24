FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app

COPY ./pom.xml .
COPY ./src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "sleep 10 && java -jar app.jar"]