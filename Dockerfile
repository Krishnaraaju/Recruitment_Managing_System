FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Make the wrapper executable and build the project
RUN chmod +x ./mvnw
RUN ./mvnw install -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-jammy
VOLUME /tmp
WORKDIR /app
COPY --from=build /workspace/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
