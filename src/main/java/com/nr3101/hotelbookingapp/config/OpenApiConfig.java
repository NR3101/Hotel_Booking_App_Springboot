package com.nr3101.hotelbookingapp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Hotel Booking API",
                version = "1.0",
                description = "REST API for the Hotel Booking Application — manage hotels, rooms, inventory, bookings, and users.",
                contact = @Contact(name = "NR3101")
        ),
        servers = @Server(url = "/api/v1", description = "Default Server")
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT access token. Obtain via /auth/login endpoint.",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
