package com.sunwayworld.emc.module.bp.sol.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.sol.bean.EmcSolBean;
import com.sunwayworld.emc.module.bp.sol.bean.EmcSolLineBean;
import com.sunwayworld.emc.module.bp.sol.bean.EmcSolRecordBean;
import com.sunwayworld.emc.module.bp.sol.dao.EmcSolDao;
import com.sunwayworld.emc.module.bp.sol.service.EmcSolLineService;
import com.sunwayworld.emc.module.bp.sol.service.EmcSolRecordService;
import com.sunwayworld.emc.module.bp.sol.service.EmcSolService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;

@Repository
@GikamBean
public class EmcSolServiceImpl implements EmcSolService {

    @Autowired
    private EmcSolDao solDao;

    @Autowired
    private EmcSolLineService solLineService;

    @Autowired
    private EmcSolRecordService solRecordService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcSolDao getDao() {
        return solDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcSolBean sol = jsonWrapper.parseUnique(EmcSolBean.class);
        sol.setId(ApplicationContextHelper.getNextIdentity());
        sol.setSolNo(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        sol.setRevisionRule("EMC,4");
        getDao().insert(sol);
        return sol.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertRecord(Long id, RestJsonWrapperBean wrapper) {
        EmcSolRecordBean solRecord = wrapper.parseUnique(EmcSolRecordBean.class);
        solRecord.setId(ApplicationContextHelper.getNextIdentity());
        solRecord.setSolId(id);
        solRecord.setUnitName("mL");
        solRecordService.getDao().insert(solRecord);
        return solRecord.getId();
    }

    @Override
    public Page<EmcSolRecordBean> selectRecordPaginationById(Long id, RestJsonWrapperBean wrapper) {
        return solRecordService.selectPaginationByFilter(SearchFilter.instance().match("SOLID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteRecord(RestJsonWrapperBean wrapper) {
        solRecordService.delete(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void calucatePrepareConcent(RestJsonWrapperBean wrapper) {
        // 计算标准溶液配制后浓度
        List<EmcSolBean> emcSolList = getEmcSolList(wrapper);
        emcSolList.forEach(e -> {
            String originalConcent = e.getOriginalConcent();
            String transferVolume = e.getTransferVolume();
            String constantVolume = e.getConstantVolume();
            if (!ObjectUtils.isEmpty(originalConcent) && !ObjectUtils.isEmpty(transferVolume) && !ObjectUtils.isEmpty(constantVolume)) {
                Double preparedConcent = ArithUtils.div(ArithUtils.mul(originalConcent, transferVolume) + "", constantVolume);
                e.setPreparedConcent(preparedConcent + "");
            }
        });
        getDao().update(emcSolList, "PREPAREDCONCENT");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void calucateCalibrateConcent(RestJsonWrapperBean wrapper) {
        EmcSolLineBean solLine = wrapper.parseUnique(EmcSolLineBean.class);
        List<EmcSolLineBean> solLineList = solLineService.getDao().selectListByOneColumnValue(solLine.getSolId(), "SOLID");
        boolean hasRequiredValue = solLineList.stream().anyMatch(s -> !ObjectUtils.isEmpty(s.getCalibrateSolConcent()) && !ObjectUtils.isEmpty(s.getCalibrateSolVolume()));
        if (!hasRequiredValue) {
            return;
        }

        EmcSolBean sol = getDao().selectById(solLine.getSolId());
        List<EmcSolRecordBean> solRecordList = solRecordService.getDao().selectListByOneColumnValue(solLine.getSolId(), "SOLID");

        EmcSolBean updateSol = new EmcSolBean();
        updateSol.setId(solLine.getSolId());
        solLineList.stream().filter(line -> !ObjectUtils.isEmpty(line.getCalibrateSolConcent()) && !ObjectUtils.isEmpty(line.getCalibrateSolVolume())).findFirst().ifPresent(line -> {
            List<EmcSolRecordBean> currentRecordList = solRecordList.stream().filter(rec -> ObjectUtils.equals(line.getSolId(), rec.getSolId())).collect(Collectors.toList());
            currentRecordList.stream().mapToDouble(record -> NumberUtils.parseDouble(record.getConsumeValue())).average().ifPresent(avg -> {
                double mulValue = ArithUtils.mul(line.getCalibrateSolConcent(), line.getCalibrateSolVolume());
                if (avg != 0) {
                    updateSol.setCalibrateSolConcent(line.getCalibrateSolConcent());
                    double divResult = ArithUtils.div(mulValue, avg);
                    String revisionRule = Optional.ofNullable(sol.getRevisionRule()).orElse("EMC,4");
                    String revisedResult = NumberUtils.rounding(divResult + "", revisionRule);
                    updateSol.setCalibratedConcent(revisedResult);
                }
            });
        });

        getDao().update(updateSol, "CALIBRATEDCONCENT", "CALIBRATESOLCONCENT");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copy(RestJsonWrapperBean wrapper) {
        List<EmcSolBean> solList = wrapper.parse(EmcSolBean.class);
        List<Long> solIdList = solList.stream().map(EmcSolBean::getId).collect(Collectors.toList());
        List<EmcSolBean> copiedSolList = getDao().selectListByOneColumnValues(solIdList, "ID");
        if (copiedSolList.isEmpty()) {
            return;
        }

        List<String> newSolNoList = ApplicationContextHelper.getNextSequenceList(getDao().getTable(), copiedSolList.size());
        List<EmcSolLineBean> solLineList = solLineService.getDao().selectListByOneColumnValues(solIdList, "SOLID");

        List<EmcSolBean> newSolList = new ArrayList<>(copiedSolList.size());
        List<EmcSolLineBean> newSolLineList = new ArrayList<>(solLineList.size());

        IntStream.range(0, copiedSolList.size()).forEach(index -> {
            EmcSolBean copiedSol = copiedSolList.get(index);
            EmcSolBean newSol = new EmcSolBean();
            BeanUtils.copyProperties(copiedSol, newSol, EmcConstant.CREATED_COLUMNS);
            newSol.setId(ApplicationContextHelper.getNextIdentity());
            newSol.setSolNo(newSolNoList.get(index));
            newSol.setStatus(Constant.NO);
            newSolList.add(newSol);

            solLineList.stream().filter(line -> ObjectUtils.equals(copiedSol.getId(), line.getSolId())).forEach(line -> {
                EmcSolLineBean newSolLine = new EmcSolLineBean();
                BeanUtils.copyProperties(line, newSolLine, EmcConstant.CREATED_COLUMNS);
                newSolLine.setSolId(newSol.getId());
                newSolLine.setId(ApplicationContextHelper.getNextIdentity());
                newSolLineList.add(newSolLine);
            });
        });

        getDao().insert(newSolList);
        solLineService.getDao().insert(newSolLineList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void publish(RestJsonWrapperBean wrapper) {
        List<EmcSolBean> solList = wrapper.parse(EmcSolBean.class);
        if (!solList.isEmpty()) {
            solList.forEach(e-> e.setStatus(Constant.YES));
            getDao().update(solList, "STATUS");
        }
    }

    @Override
    public Page<EmcSolLineBean> selectSolLinePagination(Long id, RestJsonWrapperBean wrapper) {
        return solLineService.selectPaginationByFilter(SearchFilter.instance().match("SOLID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Long insertSolLine(RestJsonWrapperBean wrapper) {
        return solLineService.insert(wrapper);
    }

    @Override
    public void deleteSolLine(RestJsonWrapperBean wrapper) {
        solLineService.delete(wrapper);
    }

    @Override
    public EmcSolLineBean selectSolLineDetail(Long id) {
        return solLineService.selectById(id);
    }

    private List<EmcSolBean> getEmcSolList(RestJsonWrapperBean wrapper) {
        List<EmcSolBean> solList = wrapper.parse(EmcSolBean.class);
        return selectListByIds(solList.stream().map(EmcSolBean::getId).collect(Collectors.toList()));
    }

}
