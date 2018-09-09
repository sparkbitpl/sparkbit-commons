package pl.sparkbit.commons.mybatis.handlers;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@SuppressWarnings("unused")
public class UUIDTypeHandler extends BaseTypeHandler<UUID> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UUID uuid, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, uuid.toString());
    }

    @Override
    public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String uuid = rs.getString(columnName);
        return rs.wasNull() ? null : UUID.fromString(uuid);
    }

    @Override
    public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String uuid = rs.getString(columnIndex);
        return rs.wasNull() ? null : UUID.fromString(uuid);
    }

    @Override
    public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String uuid = cs.getString(columnIndex);
        return cs.wasNull() ? null : UUID.fromString(uuid);
    }
}
