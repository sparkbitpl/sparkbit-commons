package pl.sparkbit.commons.test.cassandra

import com.datastax.oss.driver.api.core.CqlSession
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
    protected lateinit var session: CqlSession

    @Value("\${$TEST_CASSANDRA_KEYSPACE}")
    private lateinit var keyspace: String

    @After
    fun cleanupAfterTest() {
        val tables = session.metadata.getKeyspace(keyspace).get().tables.values
        tables.forEach { t -> session.execute("TRUNCATE " + t.name) }
    }
}
