package eus.ibai.urlshortener;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AppIT {

    @Test
    void contextLoads() {
    }

    @Test
    void plainApp() {
        App.main(new String[]{
                "--spring.main.web-environment=false"
        });
    }
}
