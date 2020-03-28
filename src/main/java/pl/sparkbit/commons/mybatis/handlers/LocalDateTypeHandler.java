package pl.sparkbit.commons.mybatis.handlers;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;

@SuppressWarnings("unused")
public class LocalDateTypeHandler extends BaseTypeHandler<LocalDate> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int parameterIndex, LocalDate localDate, JdbcType jdbcType)
            throws SQLException {
        // This class should not be required as LocalDateTypeHandler is present in MyBatis
        // Unfortunately third parameter needed because of https://github.com/mybatis/mybatis-3/issues/1537
        // More description: https://bugs.mysql.com/bug.php?id=91112
        ps.setDate(parameterIndex, Date.valueOf(localDate), Calendar.getInstance());
    }

    @Override
    public LocalDate getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Date date = rs.getDate(columnName, Calendar.getInstance());
        return rs.wasNull() ? null : date.toLocalDate();
    }

    @Override
    public LocalDate getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Date date = rs.getDate(columnIndex, Calendar.getInstance());
        return rs.wasNull() ? null : date.toLocalDate();
    }

    @Override
    public LocalDate getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Date date = cs.getDate(columnIndex, Calendar.getInstance());
        return cs.wasNull() ? null : date.toLocalDate();
    }
}
