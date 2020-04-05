package pl.sparkbit.commons.test.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Arrays;

@SuppressWarnings("unused")
public class MapperTestConfigBase {

    private static final String TEST_DB_PREFIX = "mapper-test.db.";

    private static final String TEST_DB_HANDLER_PACKAGES = TEST_DB_PREFIX + "handler-packages";
    private static final String TEST_DB_SCHEMA_FILES = TEST_DB_PREFIX + "schema-files";
    private static final String TEST_DB_TYPE_ALIASES_PACKAGE = TEST_DB_PREFIX + "type-aliases-package";
    private static final String TEST_DB_MYSQL_VERSION = TEST_DB_PREFIX + "mysql.version";
    private static final String TEST_DB_MYSQL_CONFIG_DIR = TEST_DB_PREFIX + "mysql.config-dir";


    @Value("classpath*:mybatis/*-mapper.xml")
    private Resource[] mappers;

    @Value("${" + TEST_DB_SCHEMA_FILES + ":}")
    private String[] schemaFiles;

    @Value("${" + TEST_DB_HANDLER_PACKAGES + ":}")
    private String handlerPackages;

    @Value("${" + TEST_DB_TYPE_ALIASES_PACKAGE + ":}")
    private String typeAliasesPackage;

    @Value("${" + TEST_DB_MYSQL_VERSION + ":5.7}")
    private String mysqlVersion;

    @Value("${" + TEST_DB_MYSQL_CONFIG_DIR + ":#{null}}")
    private String mysqlConfigDir;

    @Bean
    public DataSource dataSourceSpied() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.testcontainers.jdbc.ContainerDatabaseDriver");
        String jdbcUrl = "jdbc:tc:mysql:" + mysqlVersion + "://localhost/dbname?TC_TMPFS=/var/lib/mysql:rw";
        if (mysqlConfigDir != null) {
            jdbcUrl += "&TC_MY_CNF=" + mysqlConfigDir;
        }
        config.setMaximumPoolSize(1);
        config.setJdbcUrl(jdbcUrl);

        return new HikariDataSource(config);
    }

    @Bean
    public DataSource dataSource(DataSource dataSourceSpied) {
        return new DataSourceSpy(dataSourceSpied);
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(databasePopulator());
        dataSourceInitializer.setDatabaseCleaner(connection -> {
        });
        return dataSourceInitializer;
    }

    private DatabasePopulator databasePopulator() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        Arrays.stream(schemaFiles).forEach(file -> populator.addScript(new ClassPathResource(file)));
        return populator;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(mappers);
        sessionFactory.setTypeAliasesPackage(typeAliasesPackage);
        sessionFactory.setTypeHandlersPackage("pl.sparkbit.commons.mybatis.handlers," + handlerPackages);
        return sessionFactory.getObject();
    }

    @Bean
    public PlatformTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
