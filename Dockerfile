FROM gradle:8.7-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN chmod +x gradlew && ./gradlew clean bootJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/build/libs/restaurapp.jar app.jar
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
