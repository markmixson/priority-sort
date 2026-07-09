package markmixson.prioritysort;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Bytes;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.BitSet;

/**
 * Record representing rule match results.
 */
public record RuleMatchResults(
        @NotNull BitSet matched,
        @NotNull ZonedDateTime date,
        @NotNull Long id) {

    /**
     * Size of bytes for non-matched values.
     */
    private static final int NON_MATCHED_BYTES_SIZE = Long.BYTES * 2;

    /**
     * UTC timezone id.
     */
    private static final ZoneId UTC = ZoneId.of("UTC");

    /**
     * Given a {@link byte[]}, get a {@link RuleMatchResults} back.
     * The byte array must have at least 2 times the amount of bytes in a long.
     *
     * @param bytes the bytes to convert.
     * @return the {@link RuleMatchResults}.
     */
    public static RuleMatchResults getRuleMatchResults(final byte @NotNull [] bytes) {
        Preconditions.checkArgument(bytes.length >= NON_MATCHED_BYTES_SIZE);
        final var input = ByteBuffer.wrap(bytes);
        final int matchedSize = input.array().length - NON_MATCHED_BYTES_SIZE;
        final var matchedSlice = input.slice(0, matchedSize);
        final var dateSlice = input.slice(matchedSize, Long.BYTES);
        final var idSlice = input.slice(matchedSize + Long.BYTES, Long.BYTES);
        return new RuleMatchResults(
                BitSet.valueOf(matchedSlice),
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(dateSlice.getLong()), UTC),
                idSlice.getLong());
    }

    /**
     * Converts the record into a big-endian byte format.
     *
     * @return a {@link byte[]} representing the {@link RuleMatchResults}.
     */
    public byte[] toByteArray() {
        final var buffer = ByteBuffer.allocate(NON_MATCHED_BYTES_SIZE)
                .putLong(date().toEpochSecond())
                .putLong(id());
        return Bytes.concat(matched().toByteArray(), buffer.array());
    }
}
