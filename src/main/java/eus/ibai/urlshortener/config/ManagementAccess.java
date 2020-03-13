package eus.ibai.urlshortener.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@Configuration
public class ManagementAccess {

    @Value("${management.security.user:metrics}")
    private String username;

    @Value("${management.security.pass:pass}")
    private String password;

    @Value("${management.security.role:metrics}")
    private String role;
}
