package ru.itmo.rest.megamarket.spring.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class YandexBackendPart2SpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(YandexBackendPart2SpringApplication.class, args);
    }

}
