FROM eclipse-temurin
WORKDIR /app

# Copy the jar file
COPY target/*.jar app.jar

# Create resources directory and copy MMDB file
COPY src/main/resources/GeoLite2-City.mmdb /app/resources/GeoLite2-City.mmdb

EXPOSE 8080

# Set classpath to include resources directory
CMD ["java", "-cp", "/app/resources:/app/app.jar", "-jar", "app.jar"]