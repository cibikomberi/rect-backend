# Step 1: Builder stage (for building the application)
FROM openjdk:21-slim AS builder

# Set the working directory
WORKDIR /app

# Copy the Maven files and source code
COPY . /app
#COPY src /app/src/

# Build the application
RUN ./mvnw clean package -DskipTests

# Step 2: Final runtime stage (with OpenJDK 21)
FROM openjdk:21-slim

# Install PlatformIO in the runtime image
RUN #apt-get update && apt-get install -y --no-install-recommends \
#    python3-pip \
#    && pip3 install platformio \
#    && apt-get remove -y python3-pip \
#    && apt-get autoremove -y \
#    && rm -rf /var/lib/apt/lists/*

# Create necessary directories (code and upload)
RUN mkdir -p /app/code /app/upload

# Set the working directory to /app
WORKDIR /app

# Copy the application JAR from the builder stage
COPY --from=builder /app/target/*.jar /app/app.jar

# Copy MaxMind DB from resources
COPY src/main/resources/GeoLite2-City.mmdb /app/resources/GeoLite2-City.mmdb

# Expose the port for your app
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]
