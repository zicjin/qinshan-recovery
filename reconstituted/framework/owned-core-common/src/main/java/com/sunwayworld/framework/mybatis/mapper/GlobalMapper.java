/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.mapper;

import com.sunwayworld.framework.mybatis.mapper.GlobalBoundSql;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

@GikamBean
public interface GlobalMapper {
    public void update(@Param(value="sql") String var1);

    public void updateByBoundSql(GlobalBoundSql var1);

    public List<Map<String, Object>> selectListBySqlList(@Param(value="sqlList") List<String> var1);

    public List<Map<String, Object>> selectListBySqlListUnionAll(@Param(value="sqlList") List<String> var1);

    public List<Map<String, Object>> selectListBySqlListUnionAllAppendWhere(MapperParameter var1);

    public List<Map<String, Object>> selectList(@Param(value="sql") String var1);

    public Map<String, Object> selectOne(@Param(value="sql") String var1);

    public List<String> selectColumnList(@Param(value="tableName") String var1);

    public LocalDateTime selectDatabaseTime();

    public void callNextSequenceProcedure(Map<String, Object> var1);

    public LocalDateTime selectSubmittedTime(@Param(value="auditedTable") String var1, @Param(value="id") String var2);

    public String selectOneString(@Param(value="sql") String var1);

    public String selectFirstString(@Param(value="sql") String var1);
}
