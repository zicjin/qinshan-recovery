package com.sunwayworld.emc.module.mdm.bizmdm.method.mapper;

import com.sunwayworld.framework.support.base.mapper.GenericMapper;

/**
 * @author yuanhh@sunwayworld.com 2019年9月10日
 */

public interface EmcMethodMapper extends GenericMapper<Long> {

    Long selectReportConfigLineIdById(Long id);
}
