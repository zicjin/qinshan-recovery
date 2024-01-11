package com.sunwayworld.emc.module.bp.order.orderretain.mapper;

import com.sunwayworld.emc.module.bp.order.orderretain.bean.vo.EmcOrderRetainOverdueBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

import java.util.List;

/**
* @author zhengxy@sunwayworld.com 2019年10月17日
*/
@GikamBean
public interface EmcOrderRetainMapper extends GenericMapper<Long> {
    List<EmcOrderRetainOverdueBean> selectOverdueOrderRetain();
}