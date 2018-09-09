package pl.sparkbit.commons.mybatis.handlers;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

@SuppressWarnings("unused")
public class LocaleTypeHandler extends BaseTypeHandler<Locale> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Locale locale, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, locale.toLanguageTag());
    }

    @Override
    public Locale getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String languageTag = rs.getString(columnName);
        return rs.wasNull() ? null : Locale.forLanguageTag(languageTag);
    }

    @Override
    public Locale getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String languageTag = rs.getString(columnIndex);
        return rs.wasNull() ? null : Locale.forLanguageTag(languageTag);
    }

    @Override
    public Locale getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String languageTag = cs.getString(columnIndex);
        return cs.wasNull() ? null : Locale.forLanguageTag(languageTag);
    }
}
