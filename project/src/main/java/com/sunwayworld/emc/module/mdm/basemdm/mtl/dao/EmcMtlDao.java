package com.sunwayworld.emc.module.mdm.basemdm.mtl.dao;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;
/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月19日 下午6:05:42
 */
public interface EmcMtlDao extends GenericDao<EmcMtlBean, String> {

    void updateStockQty(String id);
}
