package markmixson.prioritysort;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.Range;

import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Gets a {@link BitSet} that can be used inside {@link RuleMatchResults}.
 */
@NoArgsConstructor
public class BitSetGenerator {

    private static final int CACHE_SIZE = 1_000;

    /**
     * {@link BitSet} cache used when requesting same cardinality repeatedly.
     */
    @Getter(AccessLevel.PRIVATE)
    final private LoadingCache<Integer, BitSet> bitSetCache = CacheBuilder.newBuilder()
            .maximumSize(CACHE_SIZE)
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build(CacheLoader.from(bits -> {
                final var bitSet = new BitSet(bits);
                bitSet.flip(0, bits);
                return bitSet;
            }));

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
    @SneakyThrows(ExecutionException.class)
    public BitSet generate(final int @NonNull [] values, final int length) {
        Preconditions.checkArgument(values.length <= length);
        if (length == 0) {
            return new BitSet(0);
        }
        final var range = Range.between(0, length - 1);
        Preconditions.checkArgument(Arrays.stream(values).allMatch(range::contains));
        final var trueBits = getLengthRoundedUpToNearestByte(length);
        final var bitSet = (BitSet) getBitSetCache().get(trueBits).clone();
        Arrays.stream(values).forEach(bitSet::flip);
        return bitSet;
    }

    private int getLengthRoundedUpToNearestByte(final int length) {
        final int mod = length % Byte.SIZE;
        if (mod == 0) {
            return length;
        } else {
            return length + Byte.SIZE - mod;
        }
    }
}
