package pl.sparkbit.commons.test.cassandra

import com.datastax.driver.core.Session
import org.junit.After
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import pl.sparkbit.commons.test.cassandra.CassandraTestConfigBase.Companion.TEST_CASSANDRA_KEYSPACE

@RunWith(SpringRunner::class)
@SpringBootTest
@Suppress("unused")
open class CassandraDaoTestBase {

    @Autowired
    protected lateinit var session: Session

    @Value("\${$TEST_CASSANDRA_KEYSPACE}")
    private lateinit var keyspace: String

    @After
    fun cleanupAfterTest() {
        val tables = session.cluster.metadata.getKeyspace(keyspace).tables
        tables.forEach { t -> session.execute("TRUNCATE " + t.name) }
    }
}
