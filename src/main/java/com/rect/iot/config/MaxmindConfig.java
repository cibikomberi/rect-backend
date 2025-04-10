package com.rect.iot.config;

import com.maxmind.geoip2.DatabaseReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

@Configuration
public class MaxmindConfig {

    @Value("${maxmind.database.path}")
    private Resource geoLiteDatabase;

    @Bean
    public DatabaseReader databaseReader() throws IOException {
        try {
            // Get the current working directory
            String currentDir = new File(".").getAbsolutePath();
            System.out.println("📂 Current Working Directory: " + currentDir);
            System.out.println("GeoIP Database Location: " + geoLiteDatabase);
            return new DatabaseReader.Builder(geoLiteDatabase.getFile()).build();
        } catch (IOException e) {
            System.err.println("Error loading GeoIP database: " + e.getMessage());
            throw e;
        }
    }
}
