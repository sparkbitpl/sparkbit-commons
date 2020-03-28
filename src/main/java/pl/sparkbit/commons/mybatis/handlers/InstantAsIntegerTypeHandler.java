package pl.sparkbit.commons.mybatis.handlers;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

@SuppressWarnings("unused")
public class InstantAsIntegerTypeHandler extends BaseTypeHandler<Instant> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int parameterIndex, Instant instant, JdbcType jdbcType)
            throws SQLException {
        ps.setLong(parameterIndex, instant.toEpochMilli());
    }

    @Override
    public Instant getNullableResult(ResultSet rs, String columnName) throws SQLException {
        long instant = rs.getLong(columnName);
        return rs.wasNull() ? null : Instant.ofEpochMilli(instant);
    }

    @Override
    public Instant getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        long instant = rs.getLong(columnIndex);
        return rs.wasNull() ? null : Instant.ofEpochMilli(instant);
    }

    @Override
    public Instant getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        long instant = cs.getLong(columnIndex);
        return cs.wasNull() ? null : Instant.ofEpochMilli(instant);
    }
}
