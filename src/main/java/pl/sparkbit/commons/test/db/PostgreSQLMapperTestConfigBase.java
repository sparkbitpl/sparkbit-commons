package pl.sparkbit.commons.test.db;

import org.springframework.beans.factory.annotation.Value;

@SuppressWarnings("unused")
public class PostgreSQLMapperTestConfigBase extends MapperTestConfigBase {

    private static final String TEST_DB_POSTGRESQL_VERSION = TEST_DB_PREFIX + "postgresql.version";

    @Value("${" + TEST_DB_POSTGRESQL_VERSION + ":9.6.8}")
    private String postgresqlVersion;

    @Override
    protected String getJdbcURL() {
        return "jdbc:tc:postgresql:" + postgresqlVersion + ":///dbname";
    }
}
