package eus.ibai.urlshortener.service;

import eus.ibai.urlshortener.dto.CreateShortUrlDto;
import eus.ibai.urlshortener.dto.ShortUrlDto;
import eus.ibai.urlshortener.entity.ShortUrl;
import eus.ibai.urlshortener.repository.ShortUrlRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ShortUrlServiceIT {

    @Autowired
    private ShortUrlRepository repository;

    @Autowired
    private ShortUrlService service;

    @Test
    void when_CreatingEntity_Then_ReturnDtoWithAllRequiredFieldsPopulated() {
        CreateShortUrlDto createDto = new CreateShortUrlDto("url");

        ShortUrlDto dto = service.create(createDto);

        assertThat(dto.getId(), notNullValue());
        assertThat(dto.getKey(), is(notNullValue()));
        assertThat(dto.getUrl(), is(createDto.getUrl()));
        ShortUrl entity = repository.findById(dto.getId()).orElseThrow();
        assertThat(entity.getId(), is(dto.getId()));
        assertThat(entity.getKey(), is(dto.getKey()));
        assertThat(entity.getUrl(), is(dto.getUrl()));
        assertThat(entity.isEnabled(), is(true));
        assertThat(entity.getCreatedOn(), is(notNullValue()));
    }
}
