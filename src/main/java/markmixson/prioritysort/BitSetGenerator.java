package markmixson.prioritysort;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.BitSet;

/**
 * Gets a {@link BitSet} that can be used inside {@link RuleMatchResults}.
 */
public class BitSetGenerator {

    /**
     * Sets a default cache size.
     */
    private static final int CACHE_SIZE = 1_000;

    /**
     * {@link BitSet} cache used when requesting same cardinality repeatedly.
     */
    private final LoadingCache<Integer, BitSet> bitSetCache = CacheBuilder.newBuilder()
            .maximumSize(CACHE_SIZE)
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
            .build(CacheLoader.from(bits -> {
                        final var bitSet = new BitSet(bits);
                        bitSet.flip(0, bits);
                        return bitSet;
                    }
            ));

    /**
     * How to generate a correct {@link BitSet}.
     * <p>
     * 1. Length must be larger than the largest value in values.<p>
     * 2. The BitSet size must be the next integer larger than length but divisible by 8.<p>
     * 3. All bits must be set initially.<p>
     * 4. Flip each bit for each given rule.
     *
     * @param values values to flip (lowest value is highest priority rule).
     * @param length the requested length.
     * @return the bitset
     */
    public @NotNull BitSet generate(final int @NotNull [] values, final int length) {
        Preconditions.checkArgument(values.length <= length);
        Preconditions.checkArgument(Arrays.stream(values).noneMatch(value -> value < 0
                || value > length - 1));
        return length == 0
                ? new BitSet(0)
                : generateFromTrueBits(values, length);
    }

    private @NotNull BitSet generateFromTrueBits(final int @NotNull [] values, final int length) {
        final int trueBits = length % Byte.SIZE == 0
                ? length
                : length + Byte.SIZE - length % Byte.SIZE;
        final var bitSet = (BitSet) getBitSetCache().getUnchecked(trueBits).clone();
        Arrays.stream(values).forEach(bitSet::flip);
        return bitSet;
    }

    private @NotNull LoadingCache<Integer, BitSet> getBitSetCache() {
        return bitSetCache;
    }
}
