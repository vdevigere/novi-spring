package org.novi.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "org.novi")
@EntityScan(basePackages = {"org.novi"})
@EnableJpaRepositories(basePackages = {"org.novi"})
@PropertySource(value = {"application.properties", "novi-core-application.properties"})
public class NoviApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoviApplication.class, args);
    }

}
