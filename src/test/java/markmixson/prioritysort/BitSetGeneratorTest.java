package markmixson.prioritysort;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.BitSet;
import java.util.stream.IntStream;

@SuppressWarnings("java:S5778")
class BitSetGeneratorTest {

    public BitSetGenerator getGenerator() {
        return generator;
    }

    private BitSetGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new BitSetGenerator();
    }

    @Test
    void testGeneratorValueBiggerThanLength() {
        final int[] ints = new int[]{100};
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                getGenerator().generate(ints, 99));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testGeneratorNullValues() {
        Assertions.assertThrows(Exception.class, () ->
                getGenerator().generate(null, 99));
    }

    @Test
    void testGeneratorLengthLessThanSizeOfValues() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                getGenerator().generate(new int[]{0, 1, 2}, 1));
    }

    @Test
    void testGeneratorLengthLessThanZero() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                getGenerator().generate(new int[]{}, -1));
    }

    @Test
    void testValueLessThanZero() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                getGenerator().generate(new int[]{-1}, 1));
    }

    @Test
    void testGeneratorEmptyBitSet() {
        Assertions.assertEquals(new BitSet(0), getGenerator().generate(new int[]{}, 0));
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
        Assertions.assertEquals(cardinality, result.cardinality());
    }
}