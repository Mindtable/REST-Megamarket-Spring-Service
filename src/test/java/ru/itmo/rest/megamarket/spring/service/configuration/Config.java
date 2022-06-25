package ru.itmo.rest.megamarket.spring.service.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class Config {

    @Bean(name = "validationFailedJson")
    public String getValidationFailedJson() {
        return "{" +
                    "\"code\":  400," +
                    "\"message\": \"Validation failed\"" +
                    "}";
    }

    @Bean(name = "itemNotFoundJson")
    public String getItemNotFoundJson() {
        return "{" +
                    "\"code\":404," +
                    "\"message\":\"Item not found\"" +
                    "}";
    }
}
