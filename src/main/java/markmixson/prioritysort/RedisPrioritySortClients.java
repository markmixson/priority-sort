package markmixson.prioritysort;

import org.springframework.stereotype.Component;

/**
 * Redis clients for priority sort.
 */
@Component
public class RedisPrioritySortClients {

    /**
     * Client for mutations.
     */
    private final PrioritySortMutationClient mutation;

    /**
     * Client for queries.
     */
    private final PrioritySortQueryClient query;

    /**
     * Main constructor.
     *
     * @param mutation mutation client
     * @param query query client
     */
    public RedisPrioritySortClients(final PrioritySortMutationClient mutation, final PrioritySortQueryClient query) {
        this.mutation = mutation;
        this.query = query;
    }

    public PrioritySortMutationClient getMutation() {
        return mutation;
    }

    public PrioritySortQueryClient getQuery() {
        return query;
    }
}
