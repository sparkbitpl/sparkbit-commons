package pl.sparkbit.commons.test;

import com.ninja_squad.dbsetup.destination.Destination;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.jdbc.datasource.ConnectionProxy;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * Based on https://gist.github.com/arey/6453086
 */
@EqualsAndHashCode
@ToString
public class TransactionAwareDataSourceDestination implements Destination {

    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;

    TransactionAwareDataSourceDestination(DataSource dataSource, PlatformTransactionManager transactionManager) {
        this.dataSource = new TransactionAwareDataSourceProxy(dataSource);
        this.transactionManager = transactionManager;
    }

    @Override
    public Connection getConnection() {
        return (Connection) Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(),
                new Class[]{ConnectionProxy.class}, new TransactionAwareInvocationHandler(dataSource));
    }

    private class TransactionAwareInvocationHandler extends DefaultTransactionDefinition implements InvocationHandler {

        private final DataSource targetDataSource;

        TransactionAwareInvocationHandler(DataSource targetDataSource) {
            this.targetDataSource = targetDataSource;
        }

        @SuppressWarnings("IllegalThrows")
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            switch (method.getName()) {
                case "commit": {
                    TransactionStatus status = transactionManager.getTransaction(this);
                    transactionManager.commit(status);
                    return null;
                }
                case "rollback": {
                    TransactionStatus status = transactionManager.getTransaction(this);
                    transactionManager.rollback(status);
                    return null;
                }
                default:
                    try {
                        Connection connection = targetDataSource.getConnection();
                        return method.invoke(connection, args);
                    } catch (InvocationTargetException ex) {
                        throw ex.getTargetException();
                    }
            }
        }
    }
}
