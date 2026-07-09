package markmixson.prioritysort;

import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Query client for priority sorting using redis sorted sets and hash sets.
 */
public interface PrioritySortQueryClient {

    /**
     * Gets the top priority id from the index.
     *
     * @param keySuffix the suffix to use on the redis key.
     * @return a {@link Mono} representing the id with the top priority.
     */
    @NotNull
    Mono<Long> getTopPriority(@NotNull String keySuffix);

    /**
     * Gets the top N priorities ids from the index in order.
     * A negative count returns all ids in priority order.
     *
     * @param keySuffix the suffix to use on the redis key.
     * @param count     the number of priorities to get.
     * @return a {@link Flux} representing the top N priority ids.
     */
    @NotNull
    Flux<Long> getTopPriorities(@NotNull String keySuffix, long count);

    /**
     * Gets the top priority id from the index.
     *
     * @param keySuffix the suffix to use on the redis key.
     * @return a {@link Mono} representing the id with the top priority.
     */
    @NotNull
    Mono<RuleMatchResults> getTopPriorityRuleMatchResult(@NotNull String keySuffix);

    /**
     * Gets the top N priorities ids from the index in order.
     * A negative count returns all priorities in order.
     *
     * @param keySuffix the suffix to use on the redis key.
     * @param count     the number of priorities to get.
     * @return a {@link Flux} representing the top N priority ids.
     */
    @NotNull
    Flux<RuleMatchResults> getTopPriorityRuleMatchResults(@NotNull String keySuffix, long count);

    /**
     * Gets the overall count of elements in the index.
     *
     * @param keySuffix the suffix to use on the redis key.
     * @return a {@link Mono} representing the count of priorities in the index.
     */
    @NotNull
    Mono<Long> getIndexCount(@NotNull String keySuffix);

    /**
     * Gets {@link RuleMatchResults} based on the given id.
     *
     * @param keySuffix the suffix to use on the redis key.
     * @param id        the id to look up.
     * @return the results.
     */
    @NotNull
    Mono<RuleMatchResults> getRuleMatchResults(@NotNull String keySuffix, long id);
}
