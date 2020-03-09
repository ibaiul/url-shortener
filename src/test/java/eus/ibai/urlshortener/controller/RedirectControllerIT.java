package eus.ibai.urlshortener.controller;


import eus.ibai.urlshortener.dto.ShortUrlDto;
import eus.ibai.urlshortener.exception.EntityNotFoundException;
import eus.ibai.urlshortener.service.ShortUrlService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedirectControllerIT {

    private static final String BASE_URI = "http://localhost:%s/go";

    private String uri;

    @LocalServerPort
    private int port;

    @MockBean
    private ShortUrlService service;

    @PostConstruct
    void init() {
        uri = String.format(BASE_URI, port);
    }

    @Test
    void given_AnExistingAndEnabledKey_when_RequestRedirect_Then_RedirectToUrl() {
        String key = "AAA";
        String redirectUrl = "https://www.ibai.eus";
        ShortUrlDto dto = new ShortUrlDto(UUID.randomUUID(), key, redirectUrl, new Date());
        when(service.getByKey(key)).thenReturn(dto);
        String url = uri + '/' + key;

        given().when()
                .redirects().follow(false)
                .get(url).then()
                .assertThat()
                .statusCode(HttpStatus.MOVED_PERMANENTLY.value())
                .header(HttpHeaders.LOCATION, redirectUrl);
    }

    @Test
    void given_NonExistingOrDisabledKey_when_RequestRedirect_Then_NotFound() {
        String key = "AAA";
        when(service.getByKey(key)).thenThrow(new EntityNotFoundException());
        String url = uri + '/' + key;

        given().when()
                .redirects().follow(false)
                .get(url).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
