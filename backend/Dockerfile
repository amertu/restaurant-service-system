# Build stage: Create the JAR using Maven
FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

# Copy the pom.xml and source files for Maven build
COPY pom.xml .
COPY src ./src

# Run Maven clean install to build the JAR, skip tests for faster builds
RUN mvn clean install -DskipTests

# Runtime stage: Use OpenJDK to run the JAR
FROM openjdk:17-jre-slim

WORKDIR /app

# Copy the built JAR file from the build stage into the runtime image
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar /app/backend.jar

# Expose port 8080 for the backend service
EXPOSE 8080

# Start the application with the "generateData" profile to initialize the database
CMD ["java", "-jar", "/app/backend.jar"]
