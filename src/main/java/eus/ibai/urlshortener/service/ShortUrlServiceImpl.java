package eus.ibai.urlshortener.service;

import eus.ibai.urlshortener.dto.CreateShortUrlDto;
import eus.ibai.urlshortener.dto.ShortUrlDto;
import eus.ibai.urlshortener.entity.ShortUrl;
import eus.ibai.urlshortener.exception.EntityNotFoundException;
import eus.ibai.urlshortener.generator.RandomUrlGenerator;
import eus.ibai.urlshortener.repository.ShortUrlRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ShortUrlServiceImpl implements ShortUrlService {

    private static final int KEY_LENGTH = 4;

    private final ShortUrlRepository repository;

    private final RandomUrlGenerator generator;

    @Override
    public ShortUrlDto getById(UUID id) {
        return repository.findByIdIfEnabled(id)
                .map(ShortUrlDto::new)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<ShortUrlDto> getAll() {
        return repository.findAllEnabled().stream()
                .map(ShortUrlDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public UUID create(CreateShortUrlDto createDto) {
        ShortUrl entity = ShortUrl.builder()
                .key(generator.generate(KEY_LENGTH))
                .url(createDto.getUrl())
                .enabled(true).build();
        entity = repository.save(entity);
        return entity.getId();
    }

    @Override
    public ShortUrlDto getByKey(String key) {
        return repository.findByKey(key)
                .filter(ShortUrl::isEnabled)
                .map(ShortUrlDto::new)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public void disable(UUID id) {
        int disabled = repository.disableById(id);
        if (disabled == 0) {
            throw new EntityNotFoundException("Entity does not exist or it is already disabled.");
        }
    }
}
