package markmixson.prioritysort;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;
import java.util.stream.IntStream;

public class RuleMatchResultsTest {
    private static final int RULESET_SIZE = 203;
    private static final Random RANDOM = new Random();
    private static final String END_TIME = "2023-05-08T00:46:24.00Z";
    private static final Instant END_INSTANT = Instant.parse(END_TIME);
    private static final Clock CLOCK = Clock.fixed(END_INSTANT, ZoneId.of("UTC"));
    private static final int ITERATIONS = 100;

    public static RuleMatchResults getRandomRuleMatchResults(final int ruleSize) {
        final var matched = getRandomBitSet(ruleSize);
        final var epochSecond = RANDOM.nextLong(CLOCK.instant().getEpochSecond());
        final var date =
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneId.of("UTC"));
        return new RuleMatchResults(matched, date, RANDOM.nextLong());
    }

    @Test
    void testMatchesTooSmall() {
        final var testValue = getRandomRuleMatchResults(0);
        final var testBytes = Arrays.copyOfRange(testValue.toByteArray(), 0, (Byte.SIZE * 2) - 2);
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                RuleMatchResults.getRuleMatchResults(testBytes));
    }

    @Test
    void testMatchesMinimum() {
        runRandomTest(0);
    }

    @Test
    void testGetRuleMatchResults() {
        IntStream.range(0, ITERATIONS).forEach(ignored -> runRandomTest(RULESET_SIZE));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testBadGetRuleMatchResultsEmpty() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                RuleMatchResults.getRuleMatchResults(new byte[]{}));
        Assertions.assertThrows(NullPointerException.class, () ->
                RuleMatchResults.getRuleMatchResults(null));
    }

    private void runRandomTest(final int ruleSize) {
        final var testValue = getRandomRuleMatchResults(ruleSize);
        final var testBytes = testValue.toByteArray();
        final var result = RuleMatchResults.getRuleMatchResults(testBytes);
        Assertions.assertEquals(testValue, result);
    }

    private static BitSet getRandomBitSet(final int ruleSize) {
        final var matched = new BitSet(ruleSize);
        IntStream.range(0, ruleSize)
                .filter(ignored -> RANDOM.nextBoolean())
                .forEach(matched::set);
        return matched;
    }
}
