package pl.sparkbit.commons.test;

import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Driver;
import java.util.Arrays;

import static pl.sparkbit.commons.Properties.*;

@SuppressWarnings("unused")
public class MapperTestConfigBase {

    @Value("classpath*:mybatis/*-mapper.xml")
    private Resource[] mappers;

    @Value("${" + TEST_DB_URL + "}")
    private String url;

    @Value("${" + TEST_DB_USERNAME + "}")
    private String username;

    @Value("${" + TEST_DB_PASSWORD + "}")
    private String password;

    @Value("${" + TEST_DB_DRIVER_CLASS_NAME + ":com.mysql.jdbc.Driver}")
    private String driverClassName;

    @Value("${" + TEST_DB_SCHEMA_FILES + ":sql/schema.sql}")
    private String[] schemaFiles;

    @Value("${" + TEST_DB_TYPE_ALIASES_PACKAGE + ":}")
    private String typeAliasesPackage;

    @Value("${" + TEST_DB_HANDLER_PACKAGES + "}")
    private String handlerPackages;

    @Bean
    public DataSource dataSourceSpied() throws Exception {
        Driver driver = (Driver) Class.forName(driverClassName).newInstance();
        return new SimpleDriverDataSource(driver, url, username, password);
    }

    @Bean
    public DataSource dataSource() throws Exception {
        return new DataSourceSpy(dataSourceSpied());
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer() throws Exception {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource());
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
