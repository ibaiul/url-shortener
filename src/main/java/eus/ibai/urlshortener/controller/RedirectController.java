package eus.ibai.urlshortener.controller;

import eus.ibai.urlshortener.dto.ShortUrlDto;
import eus.ibai.urlshortener.service.ShortUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;

@RestController
@RequestMapping("/go")
public class RedirectController {

    @Autowired
    private ShortUrlService service;

    @GetMapping("/{key}")
    public ResponseEntity<Void> getShortUrl(@PathVariable String key, HttpServletResponse response) {
        ShortUrlDto dto = service.getByKey(key);

        URI location = URI.create(dto.getUrl());
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).location(location).build();
    }
}
