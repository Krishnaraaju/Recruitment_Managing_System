# Stage 1: Build with Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
# Cache dependencies for faster builds
COPY pom.xml .
RUN mvn dependency:go-offline -B
# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Explicitly use the prod profile and set default port to 8080 or the PORT env variable provided by Render
ENV SERVER_PORT=8080
EXPOSE 8080

# The required environment variables like MYSQL_HOST, MYSQL_USER, JWT_SECRET etc 
# will be injected by Render's environment variable system.
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
