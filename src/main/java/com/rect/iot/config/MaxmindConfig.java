package com.rect.iot.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.maxmind.geoip2.DatabaseReader;

@Configuration
public class MaxmindConfig {

    @Value("${maxmind.database.path}")
    private Resource geoLiteDatabase;

    @Bean
    public DatabaseReader databaseReader() throws IOException {
        try (InputStream inputStream = geoLiteDatabase.getInputStream()) {
            System.out.println("✅ Loading GeoIP database from classpath: " + geoLiteDatabase);
            return new DatabaseReader.Builder(inputStream).build();
        } catch (IOException e) {
            System.err.println("❌ Error loading GeoIP database: " + e.getMessage());
            throw e;
        }
    }
}
