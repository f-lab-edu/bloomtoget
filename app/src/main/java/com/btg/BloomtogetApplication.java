package com.btg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.btg.infrastructure.persistence")
@EnableJpaRepositories(basePackages = "com.btg.infrastructure.persistence")
public class BloomtogetApplication {

    public static void main(String[] args) {
        SpringApplication.run(BloomtogetApplication.class, args);
    }
}
