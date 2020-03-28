package pl.sparkbit.commons.mybatis.handlers;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

@SuppressWarnings("unused")
public class DurationTypeHandler extends BaseTypeHandler<Duration> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Duration duration, JdbcType jdbcType)
            throws SQLException {
        ps.setLong(i, duration.toMillis());
    }

    @Override
    public Duration getNullableResult(ResultSet rs, String columnName) throws SQLException {
        long duration = rs.getLong(columnName);
        return rs.wasNull() ? null : Duration.ofMillis(duration);
    }

    @Override
    public Duration getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        long duration = rs.getLong(columnIndex);
        return rs.wasNull() ? null : Duration.ofMillis(duration);
    }

    @Override
    public Duration getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        long duration = cs.getLong(columnIndex);
        return cs.wasNull() ? null : Duration.ofMillis(duration);
    }
}
