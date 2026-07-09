package markmixson.prioritysort;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * Redis clients for priority sort.
 */
@Component
public class RedisPrioritySortClients {

    /**
     * Client for mutations.
     */
    @NotNull
    private final PrioritySortMutationClient mutation;

    /**
     * Client for queries.
     */
    @NotNull
    private final PrioritySortQueryClient query;

    /**
     * Main constructor.
     *
     * @param mutation mutation client
     * @param query    query client
     */
    public RedisPrioritySortClients(final @NotNull PrioritySortMutationClient mutation,
                                    final @NotNull PrioritySortQueryClient query) {
        this.mutation = mutation;
        this.query = query;
    }

    public @NotNull PrioritySortMutationClient getMutation() {
        return mutation;
    }

    public @NotNull PrioritySortQueryClient getQuery() {
        return query;
    }
}
