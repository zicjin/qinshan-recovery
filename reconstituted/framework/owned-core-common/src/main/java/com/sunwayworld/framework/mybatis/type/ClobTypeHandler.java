/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.type;

import com.sunwayworld.framework.utils.ConvertUtils;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedJdbcTypes(value={JdbcType.CLOB, JdbcType.NCLOB, JdbcType.LONGNVARCHAR, JdbcType.LONGVARCHAR})
@MappedTypes(value={String.class})
public class ClobTypeHandler
extends BaseTypeHandler<String> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setClob(i, ConvertUtils.convert(parameter, InputStreamReader.class));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnLabel) throws SQLException {
        return ConvertUtils.convert(rs.getClob(columnLabel), String.class);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return ConvertUtils.convert(rs.getClob(columnIndex), String.class);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return ConvertUtils.convert(cs.getClob(columnIndex), String.class);
    }
}
