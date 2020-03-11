package eus.ibai.urlshortener.service;

import eus.ibai.urlshortener.dto.CreateShortUrlDto;
import eus.ibai.urlshortener.dto.ShortUrlDto;
import eus.ibai.urlshortener.entity.ShortUrl;
import eus.ibai.urlshortener.exception.EntityNotFoundException;
import eus.ibai.urlshortener.exception.ValidationException;
import eus.ibai.urlshortener.generator.RandomUrlGenerator;
import eus.ibai.urlshortener.repository.ShortUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShortUrlServiceTest {

    @Mock
    private RandomUrlGenerator generator;

    @Mock
    private ShortUrlRepository repository;

    private ShortUrlService service;

    @BeforeEach
    void beforeEach() {
        service = new ShortUrlServiceImpl(repository, generator);
    }

    @Test
    void given_AnExistingAndEnabledId_When_RetrievingById_Then_ReturnsDto() {
        ShortUrl entity = testEntity();
        UUID id = entity.getId();
        when(repository.findByIdIfEnabled(id)).thenReturn(Optional.of(entity));

        ShortUrlDto dto = service.getById(id);

        assertThat(dto, is(testDto(entity)));
    }

    @Test
    void given_ANonExistentOrDisabledId_When_RetrievingById_ThenThrowException() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdIfEnabled(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getById(id));
    }

    @Test
    void given_AnExistingAndEnabledKey_When_RetrievingByKey_Then_ReturnsDto() {
        ShortUrl entity = testEntity();
        String key = entity.getKey();
        when(repository.findByKey(key)).thenReturn(Optional.of(entity));

        ShortUrlDto dto = service.getByKey(key);

        assertThat(dto, is(testDto(entity)));
    }

    @Test
    void given_AnExistingAndDisabledKey_When_RetrievingByKey_Then_ThrowException() {
        ShortUrl entity = testEntity();
        entity.setEnabled(false);
        String key = entity.getKey();
        when(repository.findByKey(key)).thenReturn(Optional.of(entity));

        assertThrows(EntityNotFoundException.class, () -> service.getByKey(key));
    }

    @Test
    void given_NonExistentKey_When_RetrievingByKey_Then_ThrowException() {
        String key = "key";
        when(repository.findByKey(key)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getByKey(key));
    }

    @Test
    void when_RetrievingAll_Then_ReturnsAllDtos() {
        List<ShortUrl> entities = Arrays.asList(testEntity(), testEntity(), testEntity());
        ShortUrlDto[] expectedDtos = entities.stream().map(this::testDto).toArray(ShortUrlDto[]::new);
        when(repository.findAllEnabled()).thenReturn(entities);

        List<ShortUrlDto> dtos = service.getAll();

        assertThat(dtos, containsInAnyOrder(expectedDtos));
    }

    @Test
    void when_CreatingEntity_Then_ReturnIdOfCreatedEntity() {
        CreateShortUrlDto createDto = new CreateShortUrlDto("url");
        ShortUrl savedEntity = testEntity();
        when(repository.save(any())).thenReturn(savedEntity);
        when(generator.generate(anyInt())).thenReturn("key");

        UUID id = service.create(createDto);

        assertThat(id, is(savedEntity.getId()));
    }

    @Test
    void given_AUrlLongerThan1020_when_CreatingEntity_Then_ThrowValidationException() {
        String longUrl = IntStream.range(0, 500).mapToObj(Integer::toString).collect(Collectors.joining());
        CreateShortUrlDto createDto = new CreateShortUrlDto(longUrl);
        when(repository.save(any())).thenThrow(new DataIntegrityViolationException(""));

        assertThrows(ValidationException.class, () -> service.create(createDto));
    }

    @Test
    void given_AnExistingAndEnabledId_when_Disabling_Then_DontThrowException() {
        UUID id = UUID.randomUUID();
        when(repository.disableById(id)).thenReturn(1);

        assertDoesNotThrow(() -> service.disable(id));
    }

    @Test
    void given_AnExistingAndDisabledOrNonExistingId_when_Disabling_Then_ThrowException() {
        UUID id = UUID.randomUUID();
        when(repository.disableById(id)).thenReturn(0);

        assertThrows(EntityNotFoundException.class, () -> service.disable(id));
    }

    private ShortUrl testEntity() {
        return ShortUrl.builder().id(UUID.randomUUID()).key("key").url("url").enabled(true).createdOn(new Date()).build();
    }

    private ShortUrlDto testDto(ShortUrl entity) {
        return new ShortUrlDto(entity);
    }
}
