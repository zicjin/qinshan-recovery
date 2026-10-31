package com.sunwayworld.emc.module.mdm.equipt.equipt.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

@GikamBean
public interface EmcEquiptMapper extends GenericMapper<Long> {

    List<EmcEquiptBean> selectByConditionByTestMethodIdList(List<String> testMethodIds);

    List<Map<String, Object>> selectCaliChoose();

    List<Map<String, Object>> selectVerifyChoose();

    List<Map<String, Object>> selectPerCheckChoose();

    List<EmcEquiptBean> selectMaxEquiptIdByEquiptCategoryId(long equiptCategoryId);

}
