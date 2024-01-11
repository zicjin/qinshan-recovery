package com.sunwayworld.emc.module.bp.info.infoconfig.dao;

import java.util.List;

import com.sunwayworld.emc.module.bp.info.infoconfig.bean.EmcInfoConfigBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

public interface EmcInfoConfigDao extends GenericDao<EmcInfoConfigBean, Long> {

    List<EmcInfoConfigBean> selectTreeNodeList(MapperParameter parameter);
}
