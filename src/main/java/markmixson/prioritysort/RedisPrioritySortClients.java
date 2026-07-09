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


    public RedisPrioritySortClients(PrioritySortMutationClient mutation, PrioritySortQueryClient query) {
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
