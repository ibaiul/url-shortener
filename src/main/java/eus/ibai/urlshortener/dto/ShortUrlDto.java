package eus.ibai.urlshortener.dto;

import eus.ibai.urlshortener.entity.ShortUrl;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ShortUrlDto {

    private UUID id;

    private String key;

    private String url;

    private Date createdOn;

    public ShortUrlDto(ShortUrl shortUrl) {
        this(shortUrl.getId(), shortUrl.getKey(), shortUrl.getUrl(), shortUrl.getCreatedOn());
    }
}
