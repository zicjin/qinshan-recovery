package com.sunwayworld.emc.module.bp.order.orderretain.dao;

import java.util.List;

import com.sunwayworld.emc.module.bp.order.orderretain.bean.EmcOrderRetainBean;
import com.sunwayworld.emc.module.bp.order.orderretain.bean.vo.EmcOrderRetainOverdueBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
* @author zhengxy@sunwayworld.com 2019年10月17日
*/
public interface EmcOrderRetainDao extends GenericDao<EmcOrderRetainBean, Long> {
    List<EmcOrderRetainOverdueBean> selectOverdueOrderRetain();
}
