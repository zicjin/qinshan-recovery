/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.type;

import com.sunwayworld.framework.utils.ConvertUtils;
import java.io.ByteArrayInputStream;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedJdbcTypes(value={JdbcType.BLOB, JdbcType.BINARY, JdbcType.LONGVARBINARY})
@MappedTypes(value={String.class})
public class BlobTypeHandler
extends BaseTypeHandler<String> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setBlob(i, ConvertUtils.convert(parameter, ByteArrayInputStream.class));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnLabel) throws SQLException {
        return ConvertUtils.convert(rs.getBlob(columnLabel), String.class);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return ConvertUtils.convert(rs.getBlob(columnIndex), String.class);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return ConvertUtils.convert(cs.getBlob(columnIndex), String.class);
    }
}
