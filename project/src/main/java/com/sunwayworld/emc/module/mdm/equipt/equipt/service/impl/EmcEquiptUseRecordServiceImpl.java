package com.sunwayworld.emc.module.mdm.equipt.equipt.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptUseRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptUseRecordDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptUseRecordService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ServletUtils;

@Repository
@GikamBean
public class EmcEquiptUseRecordServiceImpl implements EmcEquiptUseRecordService {

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcSampleService emcSampleService;

    @Autowired
    private EmcProjectService projectService;

    @Autowired
    private EmcEquiptUseRecordDao equiptUseRecordDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquiptUseRecordDao getDao() {
        return equiptUseRecordDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        List<EmcEquiptUseRecordBean> useRecordList = jsonWrapper.parse(EmcEquiptUseRecordBean.class);
        useRecordList.forEach(record -> {
            record.setId(ApplicationContextHelper.getNextIdentity());
            record.setBeforeUseStatus("正常");
            record.setAfterUseStatus("正常");
        });
        getDao().insert(useRecordList);
        return 0L;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateUseRecord(RestJsonWrapperBean wrapper) {
        List<EmcEquiptUseRecordBean> equiptUseRecordList = wrapper.parse(EmcEquiptUseRecordBean.class);

        Long projId = equiptUseRecordList.get(0).getProjId();
        Long sampleId = equiptUseRecordList.get(0).getSampleId();
        String projNo = projectService.selectById(projId).getProjNo();
        String sampleNo = emcSampleService.selectById(sampleId).getSampleNo();

        List<Long> orderIdList = equiptUseRecordList.stream().filter(r -> NumberUtils.isNumber(r.getExt$Item("orderid"))).mapToLong(r -> NumberUtils.parseLong(r.getExt$Item("orderid"))).boxed().collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByIds(orderIdList);

        MapperParameter parameter = new MapperParameter();
        parameter.setFilter(equiptUseRecordList, "SAMPLEID", "TESTID");

        List<EmcEquiptUseRecordBean> equiptUseRecordExistsList = selectList(parameter);
        List<EmcEquiptUseRecordBean> equiptUseRecordUpdateList = new ArrayList<>();

        List<EmcEquiptUseRecordBean> equiptUseRecordInsertList = equiptUseRecordList.stream().filter(x -> {
            return equiptUseRecordExistsList.isEmpty() || equiptUseRecordExistsList.stream().allMatch(y -> {
                boolean updateFlag = ObjectUtils.equals(String.valueOf(x.getSampleId()) + String.valueOf(x.getTestId()), String.valueOf(y.getSampleId()) + String.valueOf(y.getTestId()));
                if (updateFlag) {
                    x.setId(y.getId());
                    setEquipmentUseTime(x, orderList);
                    equiptUseRecordUpdateList.add(x);
                }
                return !updateFlag;
            });
        }).collect(Collectors.toList());

        equiptUseRecordInsertList.forEach(z -> {
            z.setId(ApplicationContextHelper.getNextIdentity());
            z.setProjNo(projNo);
            z.setSampleNo(sampleNo);
            setEquipmentUseTime(z, orderList);
        });

        getDao().insert(equiptUseRecordInsertList);
        updateIfChanged(equiptUseRecordUpdateList);

    }

    @Override
    public Page<EmcEquiptUseRecordBean> selectRawPaginationByEquiptIdOrSampleId(Long id, RestJsonWrapperBean wrapper) {
        String selectKey = ServletUtils.getCurrentRequestParamMap().get("selectKey");
        return selectPaginationByFilter(SearchFilter.instance().match(selectKey, id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Page<EmcEquiptUseRecordBean> selectEquiptUseRecordPagination(RestJsonWrapperBean wrapper) {
        return selectRawPagination(wrapper);
    }

    private void setEquipmentUseTime(EmcEquiptUseRecordBean useRecord, List<EmcOrderBean> orderList) {
        orderList.stream().filter(o -> ObjectUtils.equals(o.getId(), NumberUtils.parseLong(useRecord.getExt$Item("orderid")))).findFirst().ifPresent(order -> {
            String startTime = Optional.ofNullable(DateTimeUtils.formatLocalDate(order.getSampleBeginDate())).orElse("") + " " + Optional.ofNullable(order.getSampleBeginTime()).orElse("");
            String endTime = Optional.ofNullable(DateTimeUtils.formatLocalDate(order.getSampleCompleteDate())).orElse("") + " " + Optional.ofNullable(order.getSampleCompleteTime()).orElse("");
            useRecord.setStartTime(startTime);
            useRecord.setEndTime(endTime);
        });
    }

}
