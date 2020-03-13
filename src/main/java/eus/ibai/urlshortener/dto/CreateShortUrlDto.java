package eus.ibai.urlshortener.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class CreateShortUrlDto {

    @NotEmpty
    @URL
    @Size(min = 10, max = 1020)
    @ApiModelProperty(value = "URL to be shortened.", required = true, example = "https://www.google.com/search?client=firefox-b-d&q=TDD+Rocks%21%21")
    private String url;
}
