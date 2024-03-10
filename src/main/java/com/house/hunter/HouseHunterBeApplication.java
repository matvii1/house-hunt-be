package com.house.hunter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class HouseHunterBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(HouseHunterBeApplication.class, args);
    }

}
