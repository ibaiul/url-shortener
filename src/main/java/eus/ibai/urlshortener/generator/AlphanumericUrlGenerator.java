package eus.ibai.urlshortener.generator;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class AlphanumericUrlGenerator implements RandomUrlGenerator {

    private static final int LEFT_LIMIT = 48; // numeral '0'

    private static final int RIGHT_LIMIT = 122; // letter 'z'

    @Override
    public String generate(int length) {
        Random random = ThreadLocalRandom.current();

        return random.ints(LEFT_LIMIT, RIGHT_LIMIT + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
