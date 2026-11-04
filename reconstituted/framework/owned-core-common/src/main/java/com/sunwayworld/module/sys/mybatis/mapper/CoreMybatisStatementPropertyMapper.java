/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.mybatis.mapper;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.mybatis.bean.CoreMybatisStatementPropertyBean;
import java.util.List;
import org.apache.ibatis.annotations.Param;

@GikamBean
public interface CoreMybatisStatementPropertyMapper {
    public List<CoreMybatisStatementPropertyBean> selectList(@Param(value="namespace") String var1, @Param(value="statementId") String var2);
}
