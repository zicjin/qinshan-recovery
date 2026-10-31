package com.sunwayworld.emc.module.mdm.basemdm.cert.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-08
 */

public interface EmcCertDao extends GenericDao<EmcCertBean, Long> {

    List<Map<String, Object>> selectOverdueCert(MapperParameter parameter);

    List<Map<String, Object>> selectOverdueQual(MapperParameter parameter);
}