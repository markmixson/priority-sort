package markmixson.prioritysort;

import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.AsyncPool;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Client for making mutations for priority sorts.
 */
@Component
public class RedisPrioritySortMutationClient
        extends RedisPrioritySortClient
        implements PrioritySortMutationClient {

    /**
     * If previous data exists in set, delete the set and hash entries.
     * Then add the new record in the set and hash.
     */
    @Language("lua")
    private static final String ADD_UPDATE_LUA_SCRIPT = """
            local indexname, setname, id = unpack(KEYS)
            local previous = redis.call('hget', setname, id)
            if previous then
                redis.call('zrem', indexname, previous)
                redis.call('hdel', setname, id)
            end
            local data = unpack(ARGV)
            local output = redis.call('zadd', indexname, 0, data)
            redis.call('hset', setname, id, data)
            return output
            """;

    /**
     * Looks up the data from set to remove from hash, then removes entry from and hash and set.
     */
    @Language("lua")
    private static final String DEL_LUA_SCRIPT = """
            local indexname, setname, id = unpack(KEYS)
            local byteData = redis.call('hget', setname, id)
            local output = redis.call('zrem', indexname, byteData)
            redis.call('hdel', setname, id)
            return output
            """;

    /**
     * Sets up client with connection pool.
     *
     * @param pool the connection pool.
     */
    public RedisPrioritySortMutationClient(final @NotNull AsyncPool<StatefulRedisConnection<String, byte[]>> pool) {
        super(pool);
    }

    @Override
    public @NotNull Mono<Long> addOrUpdate(final @NotNull String keySuffix, final @NotNull RuleMatchResults results) {
        final var keys = new String[]{getIndexName(keySuffix), getSetName(keySuffix), results.id().toString()};
        final var values = new byte[][]{results.toByteArray()};
        return runMany(redis ->
                redis.<Long>eval(ADD_UPDATE_LUA_SCRIPT, ScriptOutputType.INTEGER, keys, values))
                .next();
    }

    @Override
    public @NotNull Mono<Long> delete(final @NotNull String keySuffix, final long id) {
        final var keys = new String[]{getIndexName(keySuffix), getSetName(keySuffix), Long.toString(id)};
        return runMany(redis ->
                redis.<Long>eval(DEL_LUA_SCRIPT, ScriptOutputType.INTEGER, keys))
                .next();
    }

    @Override
    public @NotNull Mono<Long> clear(final @NotNull String keySuffix) {
        return runSingle(redis ->
                redis.del(getIndexName(keySuffix), getSetName(keySuffix)));
    }
}
