package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptTestMethodDao;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/** Marker DAO for the source-owned inherited test-method service contract. */
public final class OwnedEquipmentTestMethodDao extends MybatisDaoSupport<EmcEquiptTestMethodBean, Long>
        implements EmcEquiptTestMethodDao {
    @Override public Class<EmcEquiptTestMethodBean> getType() { return EmcEquiptTestMethodBean.class; }
}
