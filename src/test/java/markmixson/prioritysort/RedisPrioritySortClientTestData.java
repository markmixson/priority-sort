package markmixson.prioritysort;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;

public class RedisPrioritySortClientTestData {
    public static final String FIXED_DATE = "2023-05-09T01:12:25Z";
    public static final Clock CLOCK = Clock.fixed(Instant.parse(FIXED_DATE), ZoneId.of("UTC"));
    public static final int BITSET_LENGTH = 64;
    public static final BitSetGenerator GENERATOR = new BitSetGenerator();
    public static final Random RANDOM = new Random();

    /**
     * All Fibonacci numbers below 64.
     */
    public static final RuleMatchResults FIRST = new RuleMatchResults(
            GENERATOR.generate(new int[]{0, 1, 2, 3, 5, 8, 13, 21, 34, 55}, BITSET_LENGTH),
            ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
            1356357L);

    /**
     * Minus 55.
     */
    public static final RuleMatchResults SECOND = new RuleMatchResults(
            GENERATOR.generate(new int[]{0, 1, 2, 3, 5, 8, 13, 21, 34}, BITSET_LENGTH),
            ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
            5367367L);

    /**
     * Minus 8.
     */
    public static final RuleMatchResults THIRD = new RuleMatchResults(
            GENERATOR.generate(new int[]{0, 1, 2, 3, 5, 13, 21, 34, 55}, BITSET_LENGTH),
            ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
            4356356L);

    /**
     * Minus 0.
     */
    public static final RuleMatchResults FOURTH = new RuleMatchResults(
            GENERATOR.generate(new int[]{1, 2, 3, 5, 8, 13, 21, 34, 55}, BITSET_LENGTH),
            ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
            -12314L);

    /**
     * Minus 0 and 10 seconds later.
     */
    public static final RuleMatchResults FIFTH = new RuleMatchResults(
            GENERATOR.generate(new int[]{1, 2, 3, 5, 8, 13, 21, 34, 55}, BITSET_LENGTH),
            ZonedDateTime.ofInstant(CLOCK.instant().plus(Duration.ofSeconds(10)), CLOCK.getZone()),
            33573573567356356L);

    /**
     * Minus 0 and 55.
     */
    public static final RuleMatchResults SIXTH = new RuleMatchResults(
            GENERATOR.generate(new int[]{1, 2, 3, 5, 8, 13, 21, 34}, BITSET_LENGTH),
            ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
            0L);

    /**
     * Updated first to have the lowest results
     */
    public static final RuleMatchResults UPDATED_FIRST = new RuleMatchResults(
            GENERATOR.generate(new int[]{0}, BITSET_LENGTH),
            ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
            1356357L);

    /**
     * 1 match
     */
    public static final RuleMatchResults ONE_MATCH = new RuleMatchResults(
            GENERATOR.generate(new int[]{0}, 1),
            ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
            1L);

    /**
     * 0 matches
     */
    public static final RuleMatchResults ZERO_MATCHES = new RuleMatchResults(
            GENERATOR.generate(new int[]{}, 1),
            ZonedDateTime.ofInstant(CLOCK.instant().minus(Duration.ofSeconds(30)), CLOCK.getZone()),
            0L);

    /**
     * 0 matches but later.
     */
    public static final RuleMatchResults ZERO_MATCHES_EARLIER = new RuleMatchResults(
            GENERATOR.generate(new int[]{}, 1),
            ZonedDateTime.ofInstant(CLOCK.instant().minus(Duration.ofDays(30)), CLOCK.getZone()),
            -1L);

    /**
     * 0 matches no length.
     */
    public static final RuleMatchResults ZERO_MATCHES_NO_LENGTH = new RuleMatchResults(
            GENERATOR.generate(new int[]{}, 0),
            ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
            -99999L);

    /**
     * 1 match, 1 didn't
     */
    public static final RuleMatchResults ONE_MATCH_ONE_DID_NOT = new RuleMatchResults(
            GENERATOR.generate(new int[]{1}, 2),
            ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
            113413532L);

    /**
     * 2 matches
     */
    public static final RuleMatchResults TWO_MATCHES = new RuleMatchResults(
            GENERATOR.generate(new int[]{0, 1}, 2),
            ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
            1234234L);

    /**
     * 7 out of 8 matches
     */
    public static final RuleMatchResults SEVEN_OUT_OF_EIGHT_MATCHES = new RuleMatchResults(
            GENERATOR.generate(new int[]{0, 1, 2, 3, 4, 5, 6}, 8),
            ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
            -113413532L);

    /**
     * 8 matches
     */
    public static final RuleMatchResults EIGHT_MATCHES = new RuleMatchResults(
            GENERATOR.generate(new int[]{0, 1, 2, 3, 4, 5, 6, 7}, 8),
            ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
            -1234234L);

    /**
     * List of match results in expected order.
     */
    public static final List<RuleMatchResults> RULE_MATCH_RESULTS = List.of(FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH);
}
