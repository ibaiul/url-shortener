package eus.ibai.urlshortener.generator;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class AlphanumericUrlGeneratorTest {

    private final RandomUrlGenerator generator = new AlphanumericUrlGenerator();

    @Test
    void given_ALength_when_GeneratingRandomUrl_Then_ReturnRandomAlphanumericStringWithSameLength() {
        int length = 4;
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]{" + length + "}");

        String result = generator.generate(length);
        Matcher matcher = pattern.matcher(result);

        assertThat(matcher.matches(), is(true));
    }

    @Test
    void given_ALength_when_GeneratingTwoUrl_Then_UrlsAreDifferent() {
        int length = 4;

        String result = generator.generate(length);
        String result2 = generator.generate(length);

        assertThat(result, is(not(result2)));
    }
}
