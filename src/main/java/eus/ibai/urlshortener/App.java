package eus.ibai.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableJpaAuditing
public class App {
    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
    }
}
