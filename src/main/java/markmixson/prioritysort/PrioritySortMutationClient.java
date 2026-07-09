package markmixson.prioritysort;

import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

/**
 * Mutation client for priority sorting using redis sorted sets and hash sets.
 */
public interface PrioritySortMutationClient {

    /**
     * Adds or updates a {@link RuleMatchResults} in the index.
     *
     * @param keySuffix the suffix to use on the redis key.
     * @param results   the results.
     * @return the number of updated or added items.
     */
    @NotNull
    Mono<Long> addOrUpdate(@NotNull String keySuffix, @NotNull RuleMatchResults results);

    /**
     * Removes a {@link RuleMatchResults} from the index.
     *
     * @param keySuffix the suffix to use on the redis key.
     * @param id        the id to remove.
     * @return a {@link Mono} representing the number of deleted items.
     */
    @NotNull
    Mono<Long> delete(@NotNull String keySuffix, long id);

    /**
     * Clears all data from the index and the hashset.
     *
     * @param keySuffix the suffix to use on the redis key.
     * @return a {@link Mono} representing the number elements deleted.
     */
    @NotNull
    Mono<Long> clear(@NotNull String keySuffix);
}
