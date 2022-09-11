package org.rostovpavel.webservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class BaseConfig {

    @Bean
    public ScheduledExecutorService getExecutor() {
        return Executors.newScheduledThreadPool(20);
    }
}
