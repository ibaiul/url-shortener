package eus.ibai.urlshortener.config;


import eus.ibai.urlshortener.test.AT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import javax.annotation.PostConstruct;

import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.containsString;

@AutoConfigureMetrics
class MetricsAT extends AT {

    private static final String BASE_URI_FORMAT = "http://localhost:%s";

    @LocalServerPort
    private int port;

    private String baseUrl;

    @PostConstruct
    void init() {
        baseUrl = format(BASE_URI_FORMAT, port);
    }

    @Test
    @DisplayName("Can scrape metrics")
    void given_BasicAuthentication_When_ReachingTheMetricsEndpoint_Then_ApplicationMetricsAreReturned() {
        given().when()
                .redirects().follow(false)
                .auth().basic("user", "pass")
                .get(baseUrl + "/actuator/prometheus")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(containsString("system_cpu_usage{appName=\"urlshortener\",domain=\"default\",namespace=\"default\",node=\"default\",pod="));
    }
}
