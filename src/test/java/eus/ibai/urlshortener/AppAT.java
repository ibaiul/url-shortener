package eus.ibai.urlshortener;


import eus.ibai.urlshortener.dto.CreateShortUrlDto;
import eus.ibai.urlshortener.dto.ShortUrlDto;
import eus.ibai.urlshortener.test.AT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.annotation.PostConstruct;
import java.util.UUID;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.given;

class AppAT extends AT {

    private static final String EXAMPLE_URL = "https://www.google.com/search?client=firefox-b-d&q=TDD+Rocks%21%21";
    private static final String BASE_URL = "http://localhost:%s";
    private static final String CRUD_BASE_URL = BASE_URL + "/urls";
    private static final String REDIRECT_BASE_URL = BASE_URL + "/go";

    private String crudUrl;
    private String redirectUrl;

    @LocalServerPort
    private int port;

    @PostConstruct
    void init() {
        crudUrl = String.format(CRUD_BASE_URL, port);
        redirectUrl = String.format(REDIRECT_BASE_URL, port);
    }

    @Test
    @DisplayName("A user shortens a URL and she can use it to get to the original long URL")
    void given_AUserHasALongUrlAndCreatesAShortenedUrl_When_SheUsesTheShortenedUrl_Then_SheIsRedirectedToTheLongUrl() {
        String shortKey = createShortUrl(EXAMPLE_URL).getKey();

        goToShortUrl(shortKey, EXAMPLE_URL);
    }

    @Test
    @DisplayName("A user shortens a URL and after deleting it she cannot use it to access the long URL anymore")
    void given_AUserHasALongUrlAndCreatesAShortenedUrl_When_SheDeletesTheShortenedUrl_Then_ItCannotBeUsedAnymore() {
        ShortUrlDto response = createShortUrl(EXAMPLE_URL);
        String shortKey = response.getKey();
        UUID id = response.getId();

        deleteShortUrl(id);

        ensureShortUrlDoesNotExist(shortKey);
    }

    ShortUrlDto createShortUrl(String longUrl) {
        CreateShortUrlDto createDto = new CreateShortUrlDto(longUrl);
        String location = given().contentType("application/json")
                .body(createDto)
                .when()
                .post(crudUrl)
                .getHeader(HttpHeaders.LOCATION);

        return given().contentType("application/json")
                .when()
                .get(location).then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ShortUrlDto.class);
    }

    void goToShortUrl(String key, String expectedUrl) {
        given().when()
                .redirects().follow(false)
                .get(redirectUrl + '/' + key).then()
                .assertThat()
                .statusCode(HttpStatus.MOVED_PERMANENTLY.value())
                .header(HttpHeaders.LOCATION, expectedUrl);
    }

    void deleteShortUrl(UUID id) {
        delete(crudUrl + '/' + id).then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    void ensureShortUrlDoesNotExist(String key) {
        given().when()
                .redirects().follow(false)
                .get(redirectUrl + '/' + key).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
