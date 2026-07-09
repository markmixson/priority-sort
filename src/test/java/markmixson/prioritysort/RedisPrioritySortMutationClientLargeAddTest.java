package markmixson.prioritysort;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static markmixson.prioritysort.RedisPrioritySortClientTestData.CLOCK;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.GENERATOR;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.RANDOM;

class RedisPrioritySortMutationClientLargeAddTest extends RedisPrioritySortClientTest {

    /**
     * Increasing this value can cause problems with Redis.
     */
    private static final int PARALLEL_PROCESSES = Runtime.getRuntime().availableProcessors() / 2;
    private static final int LARGE_RULE_COUNT = 203;
    private static final int LARGE_DATA_COUNT = 1_500_000;
    private static final String LARGE_ADD_SUFFIX = "largeadd";
    private static final int[] ALL_SELECTED = IntStream.range(0, LARGE_RULE_COUNT).toArray();
    private static final RuleMatchResults HIGHEST_POSSIBLE = new RuleMatchResults(
            GENERATOR.generate(ALL_SELECTED, LARGE_RULE_COUNT),
            ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
            Long.MAX_VALUE);

    @Test
    @EnabledIfEnvironmentVariable(named = "RUN_BIG_TESTS", matches = "true")
    void testLargeNumberOfAdds() throws InterruptedException {
        StepVerifier.create(getClients().getMutation().addOrUpdate(LARGE_ADD_SUFFIX, HIGHEST_POSSIBLE))
                .expectNext(1L)
                .expectComplete()
                .verify();
        doLargeNumberOfAdds();
        StepVerifier.create(getClients().getQuery().getTopPriority(LARGE_ADD_SUFFIX))
                .expectNext(Long.MAX_VALUE)
                .expectComplete()
                .verify();
        StepVerifier.create(getClients().getQuery().getIndexCount(LARGE_ADD_SUFFIX))
                .expectNext((long) (LARGE_DATA_COUNT + 1))
                .expectComplete()
                .verify();
    }

    private void doLargeNumberOfAdds() throws InterruptedException {
        final var results = getRandomIds().stream()
                .<Callable<Void>>map(id -> () -> {
                    getClients().getMutation().addOrUpdate(LARGE_ADD_SUFFIX, getRandomRuleMatchResults(id)).block();
                    return null;
                })
                .toList();
        try (var executor = Executors.newWorkStealingPool(PARALLEL_PROCESSES)) {
            final var output = executor.invokeAll(results);
            Assertions.assertEquals(LARGE_DATA_COUNT, output.size());
        }
    }

    private RuleMatchResults getRandomRuleMatchResults(@NotNull final Long id) {
        final var epochSecond = RANDOM.nextLong(CLOCK.instant().getEpochSecond());
        return new RuleMatchResults(GENERATOR.generate(getRandomMatches(), LARGE_RULE_COUNT),
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneId.of("UTC")),
                id);
    }

    private int[] getRandomMatches() {
        final var matches = new ArrayList<Integer>();
        IntStream.range(0, LARGE_RULE_COUNT).forEach(matches::add);
        Collections.shuffle(matches, RANDOM);
        randomizeMatches(matches);
        return matches.stream()
                .mapToInt(x -> x)
                .toArray();
    }

    private void randomizeMatches(@NotNull final ArrayList<Integer> matches) {
        for (int i = matches.size() - 1; i >= 0; i--) {
            if (!RANDOM.nextBoolean()) {
                matches.remove(i);
            }
        }
    }

    private List<Long> getRandomIds() {
        final var matches = new ArrayList<Long>();
        LongStream.range(0, LARGE_DATA_COUNT).forEach(matches::add);
        Collections.shuffle(matches, RANDOM);
        return matches;
    }
}
