package com.example.uberprojectbookingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableJpaAuditing
@EnableDiscoveryClient
@EntityScan("com.example.uberprojectentityservice.models")
public class UberProjectBookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UberProjectBookingServiceApplication.class, args);
    }

}
