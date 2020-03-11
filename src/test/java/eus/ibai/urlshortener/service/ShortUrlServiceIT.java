package eus.ibai.urlshortener.service;

import eus.ibai.urlshortener.dto.CreateShortUrlDto;
import eus.ibai.urlshortener.dto.ShortUrlDto;
import eus.ibai.urlshortener.entity.ShortUrl;
import eus.ibai.urlshortener.repository.ShortUrlRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

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
    void given_AValidCreateEntityDto_when_CreatingEntity_Then_EnsureEntityHasAllRequiredFields() {
        String url = "url";
        CreateShortUrlDto createDto = new CreateShortUrlDto(url);

        UUID id = service.create(createDto);

        assertThat(id, notNullValue());
        ShortUrl entity = repository.findById(id).orElseThrow();
        assertThat(entity, notNullValue());
        assertThat(entity.getId(), is(id));
        assertThat(entity.getUrl(), is(url));
        assertThat(entity.getKey(), notNullValue());
        assertThat(entity.isEnabled(), is(true));
        assertThat(entity.getCreatedOn(), notNullValue());
    }
}
