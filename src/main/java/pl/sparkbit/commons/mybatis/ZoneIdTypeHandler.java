package pl.sparkbit.commons.mybatis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;

@SuppressWarnings("unused")
public class ZoneIdTypeHandler extends BaseTypeHandler<ZoneId> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ZoneId dateTimeZone, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, dateTimeZone.getId());
    }

    @Override
    public ZoneId getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String timeZoneId = rs.getString(columnName);
        return tryParseTimeZone(timeZoneId);
    }

    @Override
    public ZoneId getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String timeZoneId = rs.getString(columnIndex);
        return tryParseTimeZone(timeZoneId);
    }

    @Override
    public ZoneId getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String timeZoneId = cs.getString(columnIndex);
        return tryParseTimeZone(timeZoneId);
    }

    private ZoneId tryParseTimeZone(String timeZoneId) {
        if (timeZoneId == null) {
            return null;
        }
        try {
            return ZoneId.of(timeZoneId);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
