package eus.ibai.urlshortener.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@Configuration
public class SwaggerAccess {

    @Value("${swagger.security.user:swagger}")
    private String username;

    @Value("${swagger.security.pass:pass}")
    private String password;

    @Value("${swagger.security.role:swagger}")
    private String role;
}
