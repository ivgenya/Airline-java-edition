package ru.vlsu.airline.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ru.vlsu.airline")
@EnableJpaRepositories(basePackages = "ru.vlsu.airline.repositories")
@EnableTransactionManagement
@EnableScheduling
public class AppConfig {

}