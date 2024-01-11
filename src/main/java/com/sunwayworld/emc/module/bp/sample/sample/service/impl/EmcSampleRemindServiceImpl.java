package com.sunwayworld.emc.module.bp.sample.sample.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleRemindBean;
import com.sunwayworld.emc.module.bp.sample.sample.dao.EmcSampleRemindDao;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleRemindService;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

@Repository
@GikamBean
public class EmcSampleRemindServiceImpl implements EmcSampleRemindService {

    @Autowired
    private EmcSampleService sampleService;

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcSampleRemindDao emcSampleRemindDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcSampleRemindDao getDao() {
        return emcSampleRemindDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcSampleRemindBean emcSampleRemind = jsonWrapper.parseUnique(EmcSampleRemindBean.class);
        emcSampleRemind.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcSampleRemind);
        return emcSampleRemind.getId();
    }

    @Override
    public Page<EmcSampleRemindBean> selectRemindBySample(RestJsonWrapperBean wrapper) {
        Long sampleId = NumberUtils.parseLong(wrapper.getFilterValue("sampleId"));
        EmcSampleBean sample = sampleService.getDao().selectById(sampleId);
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValue(sampleId, "sampleId");

        List<EmcSampleRemindBean> remindList = selectAll();

        for (EmcSampleRemindBean r : remindList) {
            switch (r.getRemind()) {
                case "采样单模板": {
                    r.setExt$Item("matter", "有要求");
                    r.setExt$Item("result", "√");
                    if (ObjectUtils.isEmpty(sample.getReportConfigLineId())) {
                        r.setExt$Item("result", "异常");
                        r.setExt$Item("explain", "未选择");
                    }
                    break;
                }
                case "采样开始时间": {
                    r.setExt$Item("matter", "有要求");
                    r.setExt$Item("result", "√");
                    if (orderList.stream().anyMatch(o -> o.getSampleBeginDate() == null || StringUtils.isEmpty(o.getSampleBeginTime()))) {
                        r.setExt$Item("result", "异常");
                        r.setExt$Item("explain", "部分未选择");
                    }
                    break;
                }
                default: {
                    r.setExt$Item("matter", "无要求");
                    r.setExt$Item("result", "√");
                    r.setExt$Item("explain", "");
                }
            }
        }

        remindList = remindList.stream().sorted((o1, o2) -> o2.getExt$Item("result").compareTo(o1.getExt$Item("result"))).collect(Collectors.toList());
        return new Pagination<>(remindList);
    }

}
