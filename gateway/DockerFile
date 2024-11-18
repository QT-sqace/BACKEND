FROM gradle:8.3-jdk17 AS build

WORKDIR /app

COPY build.gradle settings.gradle gradle.properties ./
RUN gradle dependencies --no-daemon

COPY src ./src
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8000

ENV SERVER_PORT=8000

ENTRYPOINT ["java", "-jar", "app.jar"]
