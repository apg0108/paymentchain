package com.infraestructuredomain.configserver.properties;

import org.springframework.context.annotation.*;

@Configuration
@PropertySource("dev-application.properties")
@Profile("dev")
public class PropertiesSourceDev {
    
}
