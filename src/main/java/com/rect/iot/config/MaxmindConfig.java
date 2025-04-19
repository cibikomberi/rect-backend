package com.rect.iot.config;

import com.maxmind.geoip2.DatabaseReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class MaxmindConfig {

    @Value("${MAXMIND_DB_PATH}")
    private Resource geoLiteDatabase;

    @Bean
    DatabaseReader databaseReader() throws IOException {
        try (InputStream inputStream = geoLiteDatabase.getInputStream()) {
            System.out.println("Loading GeoIP database from classpath: " + geoLiteDatabase);
            return new DatabaseReader.Builder(inputStream).build();
        } catch (IOException e) {
            System.err.println("Error loading GeoIP database: " + e.getMessage());
            throw e;
        }
    }
}
