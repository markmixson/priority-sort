package markmixson.prioritysort;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static markmixson.prioritysort.RedisPrioritySortClientTestData.FIFTH;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.FIRST;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.FOURTH;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.SECOND;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.SIXTH;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.THIRD;

@SpringBootTest
@ContextConfiguration(initializers = RedisInitializer.class)
@SuppressWarnings("java:S2187")
class RedisPrioritySortClientTest {
    static final List<RuleMatchResults> RULE_MATCH_RESULTS_SCRAMBLED =
            List.of(THIRD, FOURTH, FIRST, FIFTH, SIXTH, SECOND);

    @Autowired
    @NotNull
    RedisPrioritySortClients clients;

    void doAddOrUpdateTestData(final @NotNull String suffix) {
        RULE_MATCH_RESULTS_SCRAMBLED.parallelStream()
                .forEach(result -> getClients().getMutation().addOrUpdate(suffix, result)
                        .block());
    }

    @NotNull
    public RedisPrioritySortClients getClients() {
        return clients;
    }
}
