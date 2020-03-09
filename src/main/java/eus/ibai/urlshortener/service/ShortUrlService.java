package eus.ibai.urlshortener.service;

import eus.ibai.urlshortener.dto.CreateShortUrlDto;
import eus.ibai.urlshortener.dto.ShortUrlDto;

import java.util.List;
import java.util.UUID;

public interface ShortUrlService {
    ShortUrlDto getById(UUID id);

    List<ShortUrlDto> getAll();

    ShortUrlDto create(CreateShortUrlDto createDto);

    ShortUrlDto getByKey(String key);

    void disable(UUID id);
}
