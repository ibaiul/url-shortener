package eus.ibai.urlshortener.controller;

import eus.ibai.urlshortener.dto.CreateShortUrlDto;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/urls")
public class ShortUrlController {

    @Autowired
    private ShortUrlService service;

    @ApiOperation(notes = "Get all URLs that were shortened", value = "Get all ShortUrls", nickname = "getShortUrls" )
    @GetMapping
    public List<ShortUrlDto> getShortUrls() {
        return service.getAll();
    }

    @ApiOperation(notes = "Get one shorted URL by Id", value = "Get ShortUrl by Id", nickname = "getShortUrl" )
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
            @ApiResponse(code = 404, message = "Provided Id does not exist or was deleted.")
    })
    @GetMapping("/{id}")
    public ShortUrlDto getShortUrl(@ApiParam(value = "Id of the resource to be retrieved.", required = true)
                                       @PathVariable UUID id) {
        return service.getById(id);
    }


    @ApiOperation(notes = "Create a shortened URL", value = "Create a ShortUrl", nickname = "create" )
    @ApiResponses({
            @ApiResponse(code = 201, message = "Check Location header to get the URI to the created resource."),
            @ApiResponse(code = 400, message = "Provided body is not valid.")
    })
    @PostMapping
    public ResponseEntity<Void> create(@ApiParam(value = "URL to be shortened.", required = true)
                                           @RequestBody @Valid CreateShortUrlDto dto) {
        UUID id = service.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @ApiOperation(notes = "Delete a shortened URL by Id.", value = "Delete a ShortUrl by Id", nickname = "delete" )
    @ApiResponses({
            @ApiResponse(code = 204, message = "Resource has been successfully deleted."),
            @ApiResponse(code = 404, message = "Provided Id does not exist or was already deleted.")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@ApiParam(value = "Id of the resource to be deleted.", required = true)
                           @PathVariable UUID id) {
        service.disable(id);
    }
}
