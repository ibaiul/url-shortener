package eus.ibai.urlshortener.controller;

import eus.ibai.urlshortener.dto.CreateShortUrlDto;
import eus.ibai.urlshortener.dto.ShortUrlDto;
import eus.ibai.urlshortener.service.ShortUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/urls")
public class ShortUrlController {

    @Autowired
    private ShortUrlService service;

    @GetMapping
    public List<ShortUrlDto> getShortUrls() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ShortUrlDto getShortUrl(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShortUrlDto create(@RequestBody @Valid CreateShortUrlDto dto) {
        return service.create(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.disable(id);
    }
}
