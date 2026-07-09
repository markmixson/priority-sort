package markmixson.prioritysort;

import io.lettuce.core.Range;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.AsyncPool;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Client for making queries for priority sorts.
 */
@Component
public class RedisPrioritySortQueryClient
        extends RedisPrioritySortClient
        implements PrioritySortQueryClient {
    /**
     * Start index.
     */
    private static final int START = 0;
    /**
     * Single value.
     */
    private static final int SINGLE = 1;

    /**
     * Sets up client with connection pool.
     *
     * @param pool the connection pool.
     */
    public RedisPrioritySortQueryClient(final @NotNull AsyncPool<StatefulRedisConnection<String, byte[]>> pool) {
        super(pool);
    }

    @Override
    public @NotNull Flux<RuleMatchResults> getTopPriorityRuleMatchResults(
            final @NotNull String keySuffix, final long count) {
        return runMany(redis -> redis.zrange(getIndexName(keySuffix), START, count))
                .map(RuleMatchResults::getRuleMatchResults);
    }

    @Override
    public @NotNull Flux<Long> getTopPriorities(final @NotNull String keySuffix, final long count) {
        return getTopPriorityRuleMatchResults(keySuffix, count)
                .map(RuleMatchResults::id);
    }

    @Override
    public @NotNull Mono<Long> getTopPriority(final @NotNull String keySuffix) {
        return getTopPriorities(keySuffix, SINGLE)
                .next();
    }

    @Override
    public @NotNull Mono<RuleMatchResults> getTopPriorityRuleMatchResult(final @NotNull String keySuffix) {
        return getTopPriorityRuleMatchResults(keySuffix, SINGLE)
                .next();
    }

    @Override
    public @NotNull Mono<Long> getIndexCount(final @NotNull String keySuffix) {
        return runSingle(redis ->
                redis.zcount(getIndexName(keySuffix), Range.unbounded()));
    }

    @Override
    public @NotNull Mono<RuleMatchResults> getRuleMatchResults(final @NotNull String keySuffix, final long id) {
        return runSingle(redis ->
                redis.hget(getSetName(keySuffix), Long.toString(id)))
                .map(RuleMatchResults::getRuleMatchResults);
    }
}
