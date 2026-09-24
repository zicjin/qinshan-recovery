package com.sunwayworld.emc.module.bp.project.project.mapper;

import com.sunwayworld.emc.module.bp.project.project.mapper.EmcProjectMapper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.Map;

@GikamBean
public interface PfProjectMapper
extends EmcProjectMapper {
    public List<Map<String, Object>> selectByReportEdit(MapperParameter var1);

    public List<Map<String, Object>> selectCrmInvoiceProjListByCondition(MapperParameter var1);

    public List<Map<String, Object>> selectCrmPaymentProjListByCondition(MapperParameter var1);

    public List<Map<String, Object>> selectCrmProjListByCondition(MapperParameter var1);

    public List<Map<String, Object>> selectProjListByCondition(MapperParameter var1);
}
