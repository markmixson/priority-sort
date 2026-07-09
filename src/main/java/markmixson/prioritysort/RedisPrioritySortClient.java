package markmixson.prioritysort;

import com.google.common.base.Preconditions;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.support.AsyncPool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * Base class for Redis priority sort clients.
 */
@SuppressWarnings("SpringElInspection")
public class RedisPrioritySortClient {
    /**
     * Name format for strings.
     */
    private static final String NAME_FORMAT = "%s.%s";

    /**
     * Prefix for indexes.
     */
    @Value("${priority-sort.index-name-prefix:prioritysort}")
    @Nullable
    private String indexNamePrefix;

    /**
     * Prefix for sets.
     */
    @Value("${priority-sort.set-name-prefix:prioritysort.content}")
    @Nullable
    private String setNamePrefix;

    /**
     * Connection pool.
     */
    @NotNull
    private final AsyncPool<StatefulRedisConnection<String, byte[]>> pool;

    /**
     * Main constructor
     *
     * @param pool connection pool
     */
    public RedisPrioritySortClient(final @NotNull AsyncPool<StatefulRedisConnection<String, byte[]>> pool) {
        this.pool = pool;
    }

    /**
     * Given a Redis command to run that has a single result, apply the command to a Redis connection pool.
     *
     * @param toRun the command to run
     * @param <T>   the type of the result
     * @return the result
     */
    @NotNull <T> Mono<T> runSingle(final @NotNull Function<RedisReactiveCommands<String, byte[]>,
            @NotNull Mono<T>> toRun) {
        return Mono.fromFuture(() -> getPool().acquire())
                .flatMap(connection -> toRun.apply(connection.reactive())
                        .doFinally(_ -> getPool().release(connection)));
    }

    /**
     * Given a Redis command to run that has possibly many results, apply the command to a Redis connection pool.
     *
     * @param toRun the command to run
     * @param <T>   the type of the result
     * @return the results
     */
    @NotNull <T> Flux<T> runMany(final @NotNull Function<RedisReactiveCommands<String, byte[]>,
            @NotNull Flux<T>> toRun) {
        return Mono.fromFuture(() -> getPool().acquire())
                .flatMapMany(connection -> toRun.apply(connection.reactive())
                        .doFinally(_ -> getPool().release(connection)));
    }

    /**
     * Gets expected index name format in Redis.
     *
     * @param suffix suffix to add to name.
     * @return the index name.
     */
    @NotNull
    String getIndexName(final @NotNull String suffix) {
        return getName(getIndexNamePrefix(), suffix);
    }

    /**
     * Gets expected set name format in Redis.
     *
     * @param suffix suffix to add to name.
     * @return the set name.
     */
    @NotNull
    String getSetName(final @NotNull String suffix) {
        return getName(getSetNamePrefix(), suffix);
    }

    private @NotNull String getName(final @Nullable String prefix, final @NotNull String suffix) {
        Preconditions.checkArgument(prefix != null && !prefix.isBlank());
        Preconditions.checkArgument(!suffix.isBlank());
        return String.format(NAME_FORMAT, prefix, suffix);
    }

    private @Nullable String getIndexNamePrefix() {
        return indexNamePrefix;
    }

    private @Nullable String getSetNamePrefix() {
        return setNamePrefix;
    }

    private @NotNull AsyncPool<StatefulRedisConnection<String, byte[]>> getPool() {
        return pool;
    }
}
