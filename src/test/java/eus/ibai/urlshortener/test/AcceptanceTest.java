package eus.ibai.urlshortener.test;

import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@ActiveProfiles({"acceptance"})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AcceptanceTest {
}
