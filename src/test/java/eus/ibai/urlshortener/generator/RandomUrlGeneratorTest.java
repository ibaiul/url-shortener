package eus.ibai.urlshortener.generator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class RandomUrlGeneratorTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 7, 10})
    void given_ALength_when_GeneratingRandomUrl_Then_ReturnRandomStringWithSameLength(int expectedLength) {
        RandomUrlGenerator generator = new AlphanumericUrlGenerator();

        String result = generator.generate(expectedLength);

        assertThat(result.length(), is(expectedLength));
    }
}
