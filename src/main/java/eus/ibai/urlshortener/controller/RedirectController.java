package eus.ibai.urlshortener.controller;

import eus.ibai.urlshortener.dto.ShortUrlDto;
import eus.ibai.urlshortener.service.ShortUrlService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/go")
public class RedirectController {

    @Autowired
    private ShortUrlService service;

    @ApiOperation(notes = "Redirects to the URL that was shortened", value = "Redirect by key", nickname = "redirectByKey" )
    @ApiResponses({
            @ApiResponse(code = 301, message = "The key provided exists and the redirection works. Location header contains the URL to be redirected to."),
            @ApiResponse(code = 404, message = "Provided key does not exist or was already deleted.")
    })
    @GetMapping("/{key}")
    @ResponseStatus(HttpStatus.MOVED_PERMANENTLY)
    public ResponseEntity<Void> getShortUrl(@ApiParam(value = "Short key bind to the url that was shortened.", required = true)
                                                @PathVariable String key) {
        ShortUrlDto dto = service.getByKey(key);

        URI location = URI.create(dto.getUrl());
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).location(location).build();
    }
}
