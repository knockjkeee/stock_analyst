package org.rostovpavel.webservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(scanBasePackages = {"org.rostovpavel.base.*","org.rostovpavel.webservice.*"})
@EnableJpaRepositories("org.rostovpavel.base.*")
@EntityScan(basePackages = {"org.rostovpavel.base.*"})
public class WebServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebServiceApplication.class, args);
    }
}
