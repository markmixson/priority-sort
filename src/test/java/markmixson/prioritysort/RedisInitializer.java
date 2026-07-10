package markmixson.prioritysort;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;

public class RedisInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    /**
     * Making changes to this will probably break redis.
     */
    public static final int PARALLEL_PROCESSES = Runtime.getRuntime().availableProcessors() * 2;
    private static final int CONTAINER_REDIS_PORT = 6379;
    private static final String IMAGE_NAME = "redis:7-alpine";
    private static final DockerImageName REDIS_IMAGE_NAME = DockerImageName.parse(IMAGE_NAME);

    @SuppressWarnings("resource")
    static GenericContainer<?> REDIS = new GenericContainer<>(REDIS_IMAGE_NAME)
            .withExposedPorts(CONTAINER_REDIS_PORT)
            .withCreateContainerCmdModifier(cmd -> {
                final var hostConfig = cmd.getHostConfig();
                if (hostConfig != null) {
                    hostConfig.withSysctls(Map.of(
                            "net.core.somaxconn", "8192",
                            "net.ipv4.tcp_max_syn_backlog", "8192")
                    );
                }
            })
            .waitingFor(Wait.forLogMessage(".*Ready to accept connections.*\\n", 1))
            .withEnv("REDIS_ARGS", String.format(
                    "--io-threads %d --io-threads-do-reads yes --save \"\" --appendonly no",
                    PARALLEL_PROCESSES * 2));

    @Override
    public void initialize(final @NotNull ConfigurableApplicationContext context) {
        final var binding = String.format("%d:%d", CONTAINER_REDIS_PORT, CONTAINER_REDIS_PORT);
        REDIS.setPortBindings(List.of(binding));
        REDIS.start();
    }
}
