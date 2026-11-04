/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.mapper;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.metadata.bean.CoreValidatorBean;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

@GikamBean
public interface CoreMetadataMapper {
    public List<CoreValidatorBean> selectEnabledValidatorByTable(String var1);

    public List<Map<String, Object>> selectMasterMap(@Param(value="table") String var1, @Param(value="referColumn") String var2, @Param(value="idList") List<? extends Serializable> var3);

    public <ID> List<String> selectIdByMaster(@Param(value="table") String var1, @Param(value="referColumn") String var2, @Param(value="masterId") ID var3);

    public <ID> List<String> selectIdByMasterIdList(@Param(value="table") String var1, @Param(value="referColumn") String var2, @Param(value="masterIdList") List<ID> var3);

    public <ID> void deleteByMaster(@Param(value="table") String var1, @Param(value="referColumn") String var2, @Param(value="masterId") ID var3);

    public <ID> void delete(@Param(value="table") String var1, @Param(value="id") ID var2);

    public <ID> long countMaster(@Param(value="table") String var1, @Param(value="masterTable") String var2, @Param(value="referColumn") String var3, @Param(value="id") ID var4);

    public List<Map<String, Object>> selectImpValidatorByTable(@Param(value="table") String var1);

    public List<Map<String, Object>> selectSubmitValidatorByTable(@Param(value="table") String var1);
}
