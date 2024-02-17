package ru.vlsu.airline.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "ru.vlsu.airline")
@EnableJpaRepositories(basePackages = "ru.vlsu.airline.repositories")
public class AppConfig {

}