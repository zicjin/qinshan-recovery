package com.sunwayworld.emc.module.mdm.basemdm.subcontractor.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.bean.EmcSubcontractRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.dao.EmcSubcontractRecordDao;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.service.EmcSubcontractRecordService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.utils.ObjectUtils;

/**
 * @author magh@sunwayworld.com
 * @date 2019-11-05
 */

@Repository
public class EmcSubcontractRecordServiceImpl implements EmcSubcontractRecordService {
    @Autowired
    private EmcSubcontractRecordDao emcScItemDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcSubcontractRecordDao getDao() {
        return emcScItemDao;
    }

    public void insert(List<EmcOrderTaskBean> orderTaskList) {
        List<Long> subcontractorIdList = orderTaskList.stream().map(e -> e.getSubcontractorId()).distinct().collect(Collectors.toList());
        List<EmcSubcontractRecordBean> insertSubcontractRecordList = new ArrayList<>();
        subcontractorIdList.forEach(id -> {
            List<EmcOrderTaskBean> subcontractorOrderTaskList = orderTaskList.stream().filter(e -> ObjectUtils.equals(id, e.getSubcontractorId())).collect(Collectors.toList());
            subcontractorOrderTaskList.forEach(o -> {
                EmcSubcontractRecordBean insertSubcontractRecordBean = new EmcSubcontractRecordBean();
                insertSubcontractRecordBean.setId(ApplicationContextHelper.getNextIdentity());
                insertSubcontractRecordBean.setSubcontractorId(id);
                insertSubcontractRecordBean.setOrderTaskId(o.getId());

                insertSubcontractRecordList.add(insertSubcontractRecordBean);
            });
        });

        getDao().insert(insertSubcontractRecordList);
    }

}
