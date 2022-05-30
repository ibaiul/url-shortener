package eus.ibai.urlshortener.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ManagementAccess managementAccess;

    @Autowired
    private SwaggerAccess swaggerAccess;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(managementAccess.getUsername()).password(passwordEncoder().encode(managementAccess.getPassword())).roles(managementAccess.getRole())
                .and()
                .withUser(swaggerAccess.getUsername()).password(passwordEncoder().encode(swaggerAccess.getPassword())).roles(swaggerAccess.getRole());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/actuator/health/liveness").permitAll()
                .antMatchers("/actuator/health/readiness").permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(managementAccess.getRole())
                .antMatchers("/swagger*").hasRole(swaggerAccess.getRole())
                .anyRequest().permitAll()
                .and()
                .httpBasic();
    }
}
