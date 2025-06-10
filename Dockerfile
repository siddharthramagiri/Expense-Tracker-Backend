# Use Java 21 compatible Maven image for the build stage
FROM maven:3.9.8-eclipse-temurin-21 AS build

# Set working directory inside the container
WORKDIR /app

# Copy only the pom.xml first (to leverage Docker cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage - Use Eclipse Temurin Java 21 JRE for consistency
FROM eclipse-temurin:21-jre AS runtime

# Working directory for the runtime container
WORKDIR /app

# Copy the jar file from the build stage and list target directory for debugging
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Command to run your application
ENTRYPOINT ["java", "-jar", "app.jar"]