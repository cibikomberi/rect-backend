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
        InputStream inputStream = geoLiteDatabase.getInputStream();
        return new DatabaseReader.Builder(inputStream).build();
    }
}
