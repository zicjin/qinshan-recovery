package com.sunwayworld.emc.module.bp.result.pretreatment.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.result.pretreatment.bean.EmcExtractMethodBean;
import com.sunwayworld.emc.module.bp.result.pretreatment.bean.EmcPretreatmentBean;
import com.sunwayworld.emc.module.bp.result.pretreatment.bean.EmcPurifyMethodBean;
import com.sunwayworld.emc.module.bp.result.pretreatment.dao.EmcPretreatmentDao;
import com.sunwayworld.emc.module.bp.result.pretreatment.service.EmcExtractMethodService;
import com.sunwayworld.emc.module.bp.result.pretreatment.service.EmcPretreatmentService;
import com.sunwayworld.emc.module.bp.result.pretreatment.service.EmcPurifyMethodService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ObjectUtils;

/**
 * @author zhengxy@sunwayworld.com 2023年08月14日
 */
@Repository
@GikamBean
public class EmcPretreatmentServiceImpl implements EmcPretreatmentService {

    @Autowired
    private EmcPurifyMethodService purifyMethodService;

    @Autowired
    private EmcExtractMethodService extractMethodService;

    @Autowired
    @Lazy
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcPretreatmentDao pretreatmentDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcPretreatmentDao getDao() {
        return pretreatmentDao;
    }

    @Override
    public Page<EmcExtractMethodBean> queryExtractMethodByPagination(RestJsonWrapperBean wrapper) {
        return extractMethodService.selectPagination(wrapper);
    }

    @Override
    public void addExtractMethod(RestJsonWrapperBean wrapper) {
        extractMethodService.insert(wrapper);
    }

    @Override
    public void deleteExtractMethod(RestJsonWrapperBean wrapper) {
        extractMethodService.delete(wrapper);
    }

    @Override
    public Page<EmcPurifyMethodBean> queryPurifyMethodByPagination(RestJsonWrapperBean wrapper) {
        return purifyMethodService.selectPagination(wrapper);
    }

    @Override
    public void addPurifyMethod(RestJsonWrapperBean wrapper) {
        purifyMethodService.insert(wrapper);
    }

    @Override
    public void deletePurifyMethod(RestJsonWrapperBean wrapper) {
        purifyMethodService.delete(wrapper);
    }

    @Override
    public void complete(RestJsonWrapperBean wrapper) {
        List<EmcPretreatmentBean> pretreatmentList = wrapper.parse(EmcPretreatmentBean.class);
        pretreatmentList.forEach(p -> p.setStatus(Constant.YES));

        List<Long> orderTaskIdList = pretreatmentList.stream().map(EmcPretreatmentBean::getOrderTaskId).collect(Collectors.toList());

        EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();
        updateOrderTask.setExt$Item("pretreatmentStatus", Constant.YES);

        getDao().update(pretreatmentList, "status");
        orderTaskService.getDao().updateByIds(updateOrderTask, orderTaskIdList, "pretreatmentStatus");
    }

    @Override
    public void copyTo(RestJsonWrapperBean wrapper) {
        List<EmcPretreatmentBean> sourcePretreatmentList = wrapper.parse(EmcPretreatmentBean.class, 0);
        List<EmcPretreatmentBean> targetPretreatmentList = wrapper.parse(EmcPretreatmentBean.class, 1);

        EmcPretreatmentBean sourcePretreatment = sourcePretreatmentList.get(0);

        List<EmcExtractMethodBean> sourceExtractMethodList = extractMethodService.getDao().selectListByOneColumnValue(sourcePretreatment.getId(), "pretreatmentId");

        List<Long> targetPretreatmentIdList = targetPretreatmentList.stream().map(EmcPretreatmentBean::getId).collect(Collectors.toList());
        List<EmcExtractMethodBean> targetExtractMethodList = extractMethodService.getDao().selectListByOneColumnValues(targetPretreatmentIdList, "pretreatmentId");

        List<EmcExtractMethodBean> newExtractMethodList = new ArrayList<>();

        targetPretreatmentList.forEach(tp -> {
            tp.setPurifyStd(sourcePretreatment.getPurifyStd());
            tp.setPurifyStdAdditive(sourcePretreatment.getPurifyStdAdditive());
            tp.setPurifyStdUnit(sourcePretreatment.getPurifyStdUnit());
            tp.setPurifyStdTime(sourcePretreatment.getPurifyStdTime());
            tp.setPurifyStdUserId(sourcePretreatment.getPurifyStdUserId());
            tp.setPurifyStdUserName(sourcePretreatment.getPurifyStdUserName());
            tp.setPurifyMethod(sourcePretreatment.getPurifyMethod());
            tp.setOrderDivision(sourcePretreatment.getOrderDivision());
            tp.setInjectStd(sourcePretreatment.getInjectStd());
            tp.setInjectStdAdditive(sourcePretreatment.getInjectStdAdditive());
            tp.setInjectStdUnit(sourcePretreatment.getInjectStdUnit());
            tp.setInjectStdUserId(sourcePretreatment.getInjectStdUserId());
            tp.setInjectStdUserName(sourcePretreatment.getInjectStdUserName());
            tp.setInjectStdVolume(sourcePretreatment.getInjectStdVolume());
            tp.setRemark(sourcePretreatment.getRemark());

            List<EmcExtractMethodBean> curExtractMethodList = targetExtractMethodList.stream().filter(em -> ObjectUtils.equals(em.getPretreatmentId(), tp.getId())).collect(Collectors.toList());
            if (curExtractMethodList.isEmpty() && !sourceExtractMethodList.isEmpty()) {
                sourceExtractMethodList.forEach(sem -> {
                    EmcExtractMethodBean newExtractMethod = new EmcExtractMethodBean();
                    BeanUtils.copyProperties(sem, newExtractMethod, EmcConstant.CREATED_COLUMNS);
                    newExtractMethod.setId(ApplicationContextHelper.getNextIdentity());
                    newExtractMethod.setPretreatmentId(tp.getId());
                    newExtractMethodList.add(newExtractMethod);

                    tp.setExtractMethodId(newExtractMethod.getId() + "");
                });
            }
        });

        getDao().update(targetPretreatmentList, "purifyStd", "purifyStdAdditive", "purifyStdUnit",
                "purifyStdTime", "purifyStdUserId", "purifyStdUserName", "purifyMethod", "orderDivision", "injectStd",
                "injectStdAdditive", "injectStdUnit", "injectStdUserId", "injectStdUserName", "injectStdVolume", "extractMethodId");
        extractMethodService.getDao().insert(newExtractMethodList);
    }

}
