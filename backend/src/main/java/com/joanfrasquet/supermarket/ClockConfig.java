package com.joanfrasquet.supermarket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfig {

    // A Clock bean lets tests fix the current time instead of depending on the real one.
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
