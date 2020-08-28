package pl.sparkbit.commons.test.db;

import org.springframework.beans.factory.annotation.Value;

@SuppressWarnings("unused")
public class MySQLMapperTestConfigBase extends MapperTestConfigBase {

    private static final String TEST_DB_MYSQL_VERSION = TEST_DB_PREFIX + "mysql.version";
    private static final String TEST_DB_MYSQL_CONFIG_DIR = TEST_DB_PREFIX + "mysql.config-dir";

    @Value("${" + TEST_DB_MYSQL_VERSION + ":5.7}")
    private String mysqlVersion;

    @Value("${" + TEST_DB_MYSQL_CONFIG_DIR + ":#{null}}")
    private String mysqlConfigDir;

    @Override
    protected String getJdbcURL() {
        String jdbcUrl = "jdbc:tc:mysql:" + mysqlVersion + ":///dbname?TC_TMPFS=/var/lib/mysql:rw";
        if (mysqlConfigDir != null) {
            jdbcUrl += "&TC_MY_CNF=" + mysqlConfigDir;
        }
        return jdbcUrl;
    }
}
