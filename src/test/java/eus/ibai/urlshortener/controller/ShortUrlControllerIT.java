package eus.ibai.urlshortener.controller;


import eus.ibai.urlshortener.dto.CreateShortUrlDto;
import eus.ibai.urlshortener.dto.ShortUrlDto;
import eus.ibai.urlshortener.exception.EntityNotFoundException;
import eus.ibai.urlshortener.service.ShortUrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShortUrlControllerIT {

    private static final String BASE_URI = "http://localhost:%s/urls";

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
    void given_ShortUrlId_when_RequestGetOne_Then_ReturnDto() {
        UUID id = UUID.randomUUID();
        ShortUrlDto dto = new ShortUrlDto(id, "AAA", "url", new Date());
        when(service.getById(id)).thenReturn(dto);
        String url = uri + '/' + id;

        get(url).then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(id.toString()))
                .body("key", equalTo(dto.getKey()))
                .body("url", equalTo(dto.getUrl()))
                .body("createdOn", notNullValue());

        ShortUrlDto result = get(url).then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ShortUrlDto.class);

        assertThat(result, is(dto));
    }

    @Test
    void given_NoExistentShortUrlId_when_RequestGetOne_Then_ReturnNotFound() {
        UUID id = UUID.randomUUID();
        when(service.getById(id)).thenThrow(new EntityNotFoundException());
        String url = uri + '/' + id;

        get(url).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void when_RequestGetAll_Then_ReturnAllDtos() {
        List<ShortUrlDto> dtos = Arrays.asList(
                new ShortUrlDto(UUID.randomUUID(), "AAA", "url1", new Date()),
                new ShortUrlDto(UUID.randomUUID(), "BBB", "url2", new Date()),
                new ShortUrlDto(UUID.randomUUID(), "CCC", "url3", new Date()));
        when(service.getAll()).thenReturn(dtos);

        ShortUrlDto[] result = get(uri).then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ShortUrlDto[].class);

        assertThat(Arrays.asList(result), containsInAnyOrder(dtos.toArray(new ShortUrlDto[0])));
    }

    @Test
    void given_ValidCreateShortUrlDto_when_RequestStoreOne_Then_ReturnResourcePathInLocationHeader() {
        CreateShortUrlDto createDto = new CreateShortUrlDto("https://www.ibai.eus");
        UUID id = UUID.randomUUID();
        when(service.create(createDto)).thenReturn(id);
        String expectedPath = uri + '/' + id;

        given().contentType("application/json")
                .body(createDto)
                .when()
                .post(uri).then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .header(HttpHeaders.LOCATION, expectedPath);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCreateShortUrlDtos")
    void given_InvalidCreateShortUrlDto_when_RequestStoreOne_Then_ReturnBadRequest(CreateShortUrlDto dto) {
        given().contentType("application/json")
                .body(dto)
                .when()
                .post(uri).then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void when_RequestReplaceOne_Then_ReturnMethodNotAllowed() {
        ShortUrlDto dto = new ShortUrlDto(UUID.randomUUID(), "key", "https://www.ibai.eus", new Date());

        given().contentType("application/json")
                .body(dto)
                .when()
                .put(uri).then()
                .assertThat()
                .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }

    @Test
    void given_ExistingAndEnabledId_when_DeleteOne_Then_ReturnOk() {
        UUID id = UUID.randomUUID();
        String url = uri + '/' + id;

        delete(url).then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void given_NoExistentOrDisabledId_when_DeleteOne_Then_ReturnNotFound() {
        UUID id = UUID.randomUUID();
        doThrow(new EntityNotFoundException()).when(service).disable(id);
        String url = uri + '/' + id;

        delete(url).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    private static Stream<Arguments> provideInvalidCreateShortUrlDtos() {
        String longUrl = "https://www.ibai.eus/go?param=" + IntStream.range(0, 500).mapToObj(Integer::toString).collect(Collectors.joining());
        return Stream.of(
                Arguments.of(new CreateShortUrlDto(null)),
                Arguments.of(new CreateShortUrlDto("")),
                Arguments.of(new CreateShortUrlDto("           ")),
                Arguments.of(new CreateShortUrlDto("http://aa")),
                Arguments.of(new CreateShortUrlDto("www.ibai.eus")),
                Arguments.of(new CreateShortUrlDto("servus://www.ibai.eus")),
                Arguments.of(new CreateShortUrlDto(longUrl)));
    }
}
