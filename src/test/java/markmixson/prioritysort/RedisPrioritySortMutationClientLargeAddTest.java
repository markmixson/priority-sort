package markmixson.prioritysort;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.IntStream;

class RedisPrioritySortMutationClientLargeAddTest extends RedisPrioritySortClientTest {
    private static final int LARGE_RULE_COUNT = 203;
    private static final int LARGE_DATA_COUNT = 1_500_000;
    private static final String LARGE_ADD_SUFFIX = "largeadd";
    private static final int[] ALL_SELECTED = IntStream.range(0, LARGE_RULE_COUNT).toArray();
    private static final RuleMatchResults HIGHEST_POSSIBLE = new RuleMatchResults(
            RedisPrioritySortClientTestData.GENERATOR.generate(ALL_SELECTED, LARGE_RULE_COUNT),
            ZonedDateTime.ofInstant(RedisPrioritySortClientTestData.CLOCK.instant(),
                    RedisPrioritySortClientTestData.CLOCK.getZone()),
            Long.MAX_VALUE);
    private static final ZoneId UTC = ZoneId.of("UTC");

    @Test
    @EnabledIfEnvironmentVariable(named = "RUN_BIG_TESTS", matches = "true")
    void testLargeNumberOfAdds() {
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

    private void doLargeNumberOfAdds() {
        final var randomRuleResults = doTimed(() -> getRandomIds().parallelStream()
                .map(this::getRandomRuleMatchResults)
                .toList(), "getting random rule results");
        try (final var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            final var output = doTimed(() -> Flux.fromIterable(randomRuleResults)
                    .publishOn(Schedulers.fromExecutor(executor))
                    .flatMap(result ->
                            getClients().getMutation().addOrUpdate(LARGE_ADD_SUFFIX, result),
                            RedisInitializer.PARALLEL_PROCESSES)
                    .collectList()
                    .block(), "processing random rule results");
            Assertions.assertEquals(LARGE_DATA_COUNT, output.size());
        }
    }

    private static <T> List<T> doTimed(final @NotNull Supplier<List<T>> supplier, final @NotNull String name) {
        final var start = Instant.now();
        IO.println(("starting '%s'").formatted(name));
        final var output = supplier.get();
        final var end = Instant.now();
        IO.println("%s finished in %d s".formatted(name, end.getEpochSecond() - start.getEpochSecond()));
        return output;
    }

    private @NotNull List<Long> getRandomIds() {
        final var matches = new ArrayList<Long>();
        for (long i = 0; i < LARGE_DATA_COUNT; i++) {
            matches.add(i);
        }
        Collections.shuffle(matches, RedisPrioritySortClientTestData.RANDOM);
        return matches;
    }

    private @NotNull RuleMatchResults getRandomRuleMatchResults(final @NotNull Long id) {
        final var epochSecond = RedisPrioritySortClientTestData.RANDOM.nextLong(
                RedisPrioritySortClientTestData.CLOCK.instant().getEpochSecond());
        return new RuleMatchResults(RedisPrioritySortClientTestData.GENERATOR.generate(
                getRandomMatches(), LARGE_RULE_COUNT),
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), UTC),
                id);
    }

    private int @NotNull [] getRandomMatches() {
        final var matches = new ArrayList<Integer>();
        for (int i = 0; i < LARGE_RULE_COUNT; i++) {
            matches.add(i);
        }
        Collections.shuffle(matches, RedisPrioritySortClientTestData.RANDOM);
        randomizeMatches(matches);
        return getArray(matches);
    }

    private void randomizeMatches(final @NotNull List<Integer> matches) {
        for (int i = matches.size() - 1; i >= 0; i--) {
            if (!RedisPrioritySortClientTestData.RANDOM.nextBoolean()) {
                matches.remove(i);
            }
        }
    }

    private int @NotNull [] getArray(final List<Integer> matches) {
        final int[] array = new int[matches.size()];
        for (int i = 0; i < matches.size(); i++) {
            array[i] = matches.get(i);
        }
        return array;
    }
}
