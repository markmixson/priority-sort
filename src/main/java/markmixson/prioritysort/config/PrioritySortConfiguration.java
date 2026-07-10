package markmixson.prioritysort.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.support.AsyncConnectionPoolSupport;
import io.lettuce.core.support.AsyncPool;
import io.lettuce.core.support.BoundedPoolConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redis connection pool settings.
 */
@Configuration
public class PrioritySortConfiguration {
    /**
     * Making changes to this will probably break redis.
     */
    private static final int MAX_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 4;

    /**
     * Redis host name.
     */
    @SuppressWarnings("SpringElInspection")
    @Value("${spring.data.redis.host:localhost}")
    @NotNull
    private String redisHost;

    /**
     * Redis port.
     */
    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    /**
     * The Redis Connection Pool settings.
     *
     * @return a {@link AsyncPool} for connections.
     */
    @Bean
    public AsyncPool<StatefulRedisConnection<String, byte[]>> connectionPool() {
        final var uri = RedisURI.create(getRedisHost(), getRedisPort());
        @SuppressWarnings("resource") final var client = RedisClient.create(uri);
        final var codec = RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE);
        final var config = BoundedPoolConfig.builder()
                .maxTotal(MAX_THREAD_POOL_SIZE)
                .maxIdle(MAX_THREAD_POOL_SIZE)
                .minIdle(MAX_THREAD_POOL_SIZE)
                .build();
        return AsyncConnectionPoolSupport.createBoundedObjectPool(() -> client.connectAsync(codec, uri), config);
    }

    public @NotNull String getRedisHost() {
        return redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }
}
