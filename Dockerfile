#FROM azul/zulu-openjdk:17-latest
#VOLUME /tmp
#COPY build/libs/*.jar app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]

########################################

# Stage 1: Build the application with Gradle
#FROM gradle:7.6.4-jdk17 AS build
FROM gradle:7.6.4-jdk-focal AS build

    
# Set the working directory
WORKDIR /app

# Copy Gradle wrapper and configuration files
COPY gradle gradle
COPY gradlew build.gradle settings.gradle /app/

# Download dependencies
RUN ./gradlew build --stacktrace || return 0

# Copy the rest of the application source code
COPY src /app/src

# Build the application
RUN ./gradlew build

# Stage 2: Create the final runtime image
#FROM openjdk:17-jdk-slim
FROM azul/zulu-openjdk:17-latest
VOLUME /tmp

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/build/libs/*.jar /app/app.jar

# Expose the port the application runs on
# EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
