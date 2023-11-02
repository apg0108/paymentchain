package com.infraestructuredomain.configserver.properties;

import org.springframework.context.annotation.*;

@Configuration
@PropertySource("prod-application.properties")
@Profile("prod")
public class PropertiesDourceProd {
    
}
