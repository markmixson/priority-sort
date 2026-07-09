package markmixson.prioritysort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.BitSet;
import java.util.stream.IntStream;

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
    void testGeneratorValueBiggerThanLength() {
        assertThrows(IllegalArgumentException.class, () ->
                getGenerator().generate(new int[]{100}, 99));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testGeneratorNullValues() {
        assertThrows(Exception.class, () ->
                getGenerator().generate(null, 99));
    }

    @Test
    void testGeneratorLengthLessThanSizeOfValues() {
        assertThrows(IllegalArgumentException.class, () ->
                getGenerator().generate(new int[]{0, 1, 2}, 1));
    }

    @Test
    void testGeneratorLengthLessThanZero() {
        assertThrows(IllegalArgumentException.class, () ->
                getGenerator().generate(new int[]{}, -1));
    }

    @Test
    void testValueLessThanZero() {
        assertThrows(IllegalArgumentException.class, () ->
                getGenerator().generate(new int[]{-1}, 1));
    }

    @Test
    void testGeneratorEmptyBitSet() {
        assertEquals(new BitSet(0), getGenerator().generate(new int[]{}, 0));
    }

    @Test
    void testGeneratorChangeLengths() {
        testGenerator(IntStream.range(0, 8).toArray(), 8, 0);
        testGenerator(IntStream.range(0, 7).toArray(), 8, 1);
        testGenerator(IntStream.range(1, 7).toArray(), 8, 2);
        testGenerator(IntStream.range(1, 7).toArray(), 16, 10);
        testGenerator(IntStream.range(1, 7).toArray(), 12, 10);
    }

    private void testGenerator(final int[] values, final int length, final int cardinality) {
        final var result = getGenerator().generate(values, length);
        assertEquals(cardinality, result.cardinality());
    }
}
