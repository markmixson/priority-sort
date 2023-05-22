package markmixson.prioritysort;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.Range;

import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

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
            .build(CacheLoader.from(bits -> Stream.of(bits)
                    .map(BitSet::new)
                    .peek(bitSet -> bitSet.flip(0, bits))
                    .findFirst().get()
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
    public BitSet generate(final int @NonNull [] values, final int length) {
        return switch (values) {
            case int[] v when v.length > length -> throw new IllegalArgumentException();
            case int[] ignored when length == 0 -> new BitSet(0);
            case int[] v when !Arrays.stream(v).allMatch(Range.between(0, length - 1)::contains) ->
                    throw new IllegalArgumentException();
            default -> Stream.of(length % Byte.SIZE == 0 ? length : length + Byte.SIZE - length % Byte.SIZE)
                    .map(trueBits -> (BitSet) getBitSetCache().getUnchecked(trueBits).clone())
                    .peek(bitSet -> Arrays.stream(values).forEach(bitSet::flip))
                    .findFirst().get();
        };
    }
}
