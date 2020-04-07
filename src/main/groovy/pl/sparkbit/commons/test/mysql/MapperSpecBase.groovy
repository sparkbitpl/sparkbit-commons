package pl.sparkbit.commons.test.mysql;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.Destination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import spock.lang.Specification;

import javax.sql.DataSource;

@SuppressWarnings(["SpringJavaAutowiredMembersInspection", "unused"])
@Transactional
abstract class MapperSpecBase extends Specification {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private PlatformTransactionManager transactionManager;

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate();

    protected void insertTestData(Operation... operations) {
        Destination ds = new TransactionAwareDataSourceDestination(this.dataSource, this.transactionManager);
        DbSetup dbSetup = new DbSetup(ds, Operations.sequenceOf(operations));
        dbSetup.launch();
    }

    protected static String quote(Object arg) {
        return "'" + arg + "'";
    }

    protected int countRowsInTableWhereColumnsEquals(String tableName, Object... colsAndValues) {
        Assert.isTrue(colsAndValues.length % 2 == 0, "There should be even number of columns and values");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < colsAndValues.length - 1; i += 2) {
            Object key = colsAndValues[i];
            Object value = colsAndValues[i + 1];
            if (value == null) {
                sb.append(key).append(" IS NULL ").append(" AND ");
            } else {
                sb.append(key).append("=").append(value).append(" AND ");
            }
        }

        return this.countRowsInTableWhere(tableName, sb.substring(0, sb.length() - " AND ".length()));
    }

    protected int countRowsInTableWhere(String tableName, String whereClause) {
        return JdbcTestUtils.countRowsInTableWhere(this.jdbcTemplate, tableName, whereClause);
    }

    @Autowired
    void setDataSource(DataSource dataSource) {
        this.jdbcTemplate.setDataSource(dataSource);
    }
}
