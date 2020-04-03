package pl.sparkbit.commons.test.cassandra;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

import static pl.sparkbit.commons.test.cassandra.CassandraTestConfigBase.TEST_CASSANDRA_KEYSPACE;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CassandraDaoTestBase {

    @Autowired
    private Session session;

    @Value("${" + TEST_CASSANDRA_KEYSPACE + "}")
    private String keyspace;

    @After
    public void cleanupAfterTest() {
        Collection<TableMetadata> tables = session.getCluster().getMetadata().getKeyspace(keyspace).getTables();
        tables.forEach(t -> session.execute("TRUNCATE " + t.getName()));
    }
}
