package com.sunwayworld.emc.module.bp.result.run.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderTaskResultService;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunRemindBean;
import com.sunwayworld.emc.module.bp.result.run.dao.EmcRunRemindDao;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunRemindService;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

@Repository
@GikamBean
public class EmcRunRemindServiceImpl implements EmcRunRemindService {

    @Autowired
    private EmcRunService runService;

    @Autowired
    private PfOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcRunRemindDao emcRunRemindDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcRunRemindDao getDao() {
        return emcRunRemindDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcRunRemindBean emcRunRemind = jsonWrapper.parseUnique(EmcRunRemindBean.class);
        emcRunRemind.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcRunRemind);
        return emcRunRemind.getId();
    }

    @Override
    public Page<EmcRunRemindBean> selectRunRemind(String runId, RestJsonWrapperBean wrapper) {
        EmcRunBean run = runService.selectByIdIfPresent(runId);
        List<EmcRunRemindBean> remindList = getDao().selectListByOneColumnValue("run", "checkType");

        for (EmcRunRemindBean remind : remindList) {
            Object propertyValue = BeanUtils.getPropertyValue(run, remind.getFieldName());

            if (ObjectUtils.isEmpty(propertyValue)) {
                remind.setCheckResult("异常");
            } else {
                remind.setValue(propertyValue.toString());
                remind.setAbnormalExplain(null);
            }
        }

        return new Pagination<>(remindList);
    }

    @Override
    public Page<EmcRunRemindBean> selectResultRemind(String runId, RestJsonWrapperBean wrapper) {

        List<EmcRunRemindBean> remindList = getDao().selectListByOneColumnValue("result", "checkType", Order.asc("qcCategoryCode"));
        String qcCategoryCode = remindList.stream().map(r -> "'" + r.getQcCategoryCode() + "'").distinct().collect(Collectors.joining(","));

        MapperParameter mapperParameter = wrapper.extractMapFilter();
        mapperParameter.put("runId", runId);
        mapperParameter.put("qcCategoryCode", qcCategoryCode);
        List<EmcOrderTaskResultBean> resultList = orderTaskResultService.selectResultRemind(mapperParameter);

        List<EmcRunRemindBean> abnormalRemindList = new ArrayList<>();

        for (EmcRunRemindBean remind : remindList) {
            resultList.stream().filter(r -> StringUtils.equals(remind.getQcCategoryCode(), r.getQcCategoryCode())).forEach(result -> {

                Object propertyValue = BeanUtils.getPropertyValue(result, remind.getFieldName());

                if (ObjectUtils.isEmpty(propertyValue)) {
                    EmcRunRemindBean abnormalRemind = new EmcRunRemindBean();
                    abnormalRemind.setRemind(result.getQcName() + "-" + remind.getRemind());
                    abnormalRemind.setValue(result.getExt$Item("labDisplayLabelNumber") + "  " + result.getExt$Item("TESTNAME"));
                    abnormalRemind.setCheckResult("异常");
                    abnormalRemind.setAbnormalExplain(remind.getAbnormalExplain());
                    abnormalRemindList.add(abnormalRemind);
                }
            });
        }

        return new Pagination<>(abnormalRemindList);
    }
}
