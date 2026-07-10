package markmixson.prioritysort;

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.BitSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("java:S5778")
class BitSetGeneratorTest {
    private BitSetGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new BitSetGenerator();
    }

    private BitSetGenerator getGenerator() {
        return generator;
    }

    @Test
    void testGeneratorEmptyBitSet() {
        assertEquals(new BitSet(0), getGenerator().generate(new int[]{}, 0));
    }

    static Stream<Arguments> exceptionGeneratorValues() {
        return Stream.of(
                Arguments.of(IllegalArgumentException.class, new int[]{100}, 99),
                Arguments.of(Exception.class, null, 99),
                Arguments.of(IllegalArgumentException.class, new int[]{0, 1, 2}, 1),
                Arguments.of(IllegalArgumentException.class, new int[]{}, -1),
                Arguments.of(IllegalArgumentException.class, new int[]{-1}, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("exceptionGeneratorValues")
    @SuppressWarnings("ConstantConditions")
    void testGeneratorException(
            final Class<? extends Exception> exception, final int @Nullable [] values, final int length) {
        assertThrows(exception, () -> getGenerator().generate(values, length));
    }

    static Stream<Arguments> generatorChangeLengths() {
        return Stream.of(
                Arguments.of(IntStream.range(0, 8).toArray(), 8, 0),
                Arguments.of(IntStream.range(0, 7).toArray(), 8, 1),
                Arguments.of(IntStream.range(1, 7).toArray(), 8, 2),
                Arguments.of(IntStream.range(1, 7).toArray(), 16, 10),
                Arguments.of(IntStream.range(1, 7).toArray(), 12, 10)
        );
    }

    @ParameterizedTest
    @MethodSource("generatorChangeLengths")
    void testGenerator(final int[] values, final int length, final int cardinality) {
        final var result = getGenerator().generate(values, length);
        assertEquals(cardinality, result.cardinality());
    }
}
