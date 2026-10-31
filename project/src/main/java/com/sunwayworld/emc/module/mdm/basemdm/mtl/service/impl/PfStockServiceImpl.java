package com.sunwayworld.emc.module.mdm.basemdm.mtl.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockConcentBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockLogBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.PfMtlCheckDTO;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlMaintainService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockConcentService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockLogService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.PfMtlService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.PfStockService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;

@Repository
@GikamBean
public class PfStockServiceImpl extends EmcStockServiceImpl implements PfStockService {

    @Autowired
    private EmcStockLogService stockLogService;

    @Autowired
    @Lazy
    private PfMtlService mtlService;

    @Autowired
    @Lazy
    private EmcMtlMaintainService mtlMaintainService;

    @Autowired
    private EmcStockConcentService stockConcentService;

	@Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(String mtlId, RestJsonWrapperBean wrapper) {
        EmcMtlBean emcMtl = mtlService.selectByIdIfPresent(mtlId);
        EmcStockLogBean emcStockLog = wrapper.parseUnique(EmcStockLogBean.class);
        EmcStockBean emcStock = wrapper.parseUnique(EmcStockBean.class);
        String batchNo = emcStock.getBatchNo();
        int num = 0;
        String tagetNo = null;
        if (batchNo == null) {
            String reagentCategory = emcStock.getExt$Item("REAGENTCATEGORY");
            if (emcMtl.getMtlNo() == null && reagentCategory != null) {
                emcMtl.setExt$Item("REAGENTCATEGORY", reagentCategory);
                this.mtlService.generateMtlNo(emcMtl);
                this.mtlService.getDao().update(emcMtl, new String[]{"MTLNO"});
            }
            if (emcMtl.getMtlNo() != null) {
                tagetNo = emcStock.getExt$Item("productionno") != null ? emcStock.getExt$Item("productionno") : emcMtl.getMtlNo() + "-" + DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "yyMM");
            }
            if (tagetNo != null) {
                List<EmcStockBean> stockQCList = this.selectListByFilter(SearchFilter.instance().match("REAGENTCATEGORY", reagentCategory).filter(MatchPattern.SEQ).match("batchNo", tagetNo).filter(MatchPattern.SB), new Order[0]);
                num = stockQCList.stream().filter(e -> NumberUtils.isNumber(e.getBatchNo().substring(e.getBatchNo().length() - 3))).map(e -> Integer.parseInt(e.getBatchNo().substring(e.getBatchNo().length() - 3))).max(Comparator.naturalOrder()).orElse(0);
            }
        }
        ArrayList<EmcStockBean> newStockList = new ArrayList<EmcStockBean>();
        ArrayList<EmcStockLogBean> newStockLogList = new ArrayList<EmcStockLogBean>();
        ArrayList<EmcStockConcentBean> newStockConcentList = new ArrayList<EmcStockConcentBean>();
        List maintainList = this.mtlMaintainService.getDao().selectListByOneColumnValue(mtlId, "MTLID", new Order[0]);
        for (double i = 0.0; i < emcStockLog.getQty(); i += 1.0) {
            EmcStockBean newStock = new EmcStockBean();
            BeanUtils.copyProperties(emcStock, newStock);
            ++num;
            if (emcStock.getBatchNo() == null) {
                batchNo = tagetNo + "-" + String.format("%03d", num);
            }
            newStock.setStockQty(Double.valueOf(1.0));
            newStock.setId(ApplicationContextHelper.getNextIdentity());
            newStock.setMtlId(mtlId);
            newStock.setStatus("0");
            newStock.setBatchNo(batchNo);
            newStockList.add(newStock);
            EmcStockLogBean newStockLog = new EmcStockLogBean();
            BeanUtils.copyProperties(emcStockLog, newStockLog);
            newStockLog.setQty(Double.valueOf(1.0));
            newStockLog.setId(ApplicationContextHelper.getNextIdentity());
            newStockLog.setStockId(newStock.getId());
            newStockLog.setLogType("\u6dfb\u52a0");
            newStockLogList.add(newStockLog);
            for (int a2 = 0; a2 < maintainList.size(); ++a2) {
                EmcStockConcentBean newStockConcent = new EmcStockConcentBean();
                newStockConcent.setId(Long.valueOf(ApplicationContextHelper.getNextIdentity() + (long)a2));
                newStockConcent.setStockId(newStock.getId());
                newStockConcent.setCategory(((EmcMtlMaintainBean)maintainList.get(a2)).getCategory());
                newStockConcent.setMtlMaintainId(((EmcMtlMaintainBean)maintainList.get(a2)).getId());
                newStockConcentList.add(newStockConcent);
            }
        }
        this.getDao().insert(newStockList);
        this.stockLogService.getDao().insert(newStockLogList);
        List<EmcStockBean> stockExistList = this.getDao().selectListByOneColumnValue(mtlId, "mtlId", new Order[0]);
        double mtlStockQty = stockExistList.stream().collect(Collectors.summarizingDouble(EmcStockBean::getStockQty)).getSum();
        EmcMtlBean mtlBean = (EmcMtlBean)this.mtlService.getDao().selectById(mtlId);
        mtlBean.setStockQty(Double.valueOf(mtlStockQty));
        this.mtlService.getDao().update(mtlBean, new String[]{"stockQty"});
        this.stockConcentService.getDao().insert(newStockConcentList);
        return ((EmcStockBean)newStockList.get(0)).getId();
    }

    @Override
    public Page<PfMtlCheckDTO> selectMtlCheck(RestJsonWrapperBean wrapper) {
        List<PfMtlCheckDTO> list = new ArrayList<>();

        List<EmcMtlBean> mtlAllList =  mtlService.selectListByFilter(SearchFilter.instance().match("orgId", LocalContextHelper.getLoginUser().getExt$Item("parentOrgId")).filter(MatchPattern.SEQ));

        List<String> mtlAllIdList =  mtlAllList.stream().map(EmcMtlBean :: getId).distinct().collect(Collectors.toList());

        Page<EmcStockBean> stockPage  =  selectPaginationByFilter(SearchFilter.instance().match("mtlId",mtlAllIdList).filter(MatchPattern.OR),wrapper);
        List<EmcStockBean> stockBeanList = stockPage.getRows();

        List<String> mtlIdList = stockBeanList.stream().map(EmcStockBean :: getMtlId).distinct().collect(Collectors.toList());
        List<EmcMtlBean> mtlList = mtlService.getDao().selectListByIds(mtlIdList);

        stockBeanList.forEach(s ->{
        	PfMtlCheckDTO pfMtlCheckDTO = new PfMtlCheckDTO();
            List<EmcMtlBean> mtlMatchList = mtlList.stream().filter(m ->StringUtils.equals(s.getMtlId(),m.getId())).collect(Collectors.toList());
            //判空
            if(!mtlMatchList.isEmpty()){
            	pfMtlCheckDTO.setMtlName(mtlMatchList.get(0).getMtlName());
            	pfMtlCheckDTO.setBatchNo(s.getBatchNo());
            	pfMtlCheckDTO.setIsPackagingDamage(s.getExt$Item("ispackagingdamage"));
            	pfMtlCheckDTO.setIsSolutionDeposition(s.getExt$Item("issolutiondeposition"));
            	pfMtlCheckDTO.setIsSuitTemperature(s.getExt$Item("issuittemperature"));
            	pfMtlCheckDTO.setIsTermofValidity(s.getExt$Item("istermofvalidity"));
            	pfMtlCheckDTO.setOther(s.getExt$Item("other"));
            	pfMtlCheckDTO.setCheckConclusion(s.getExt$Item("checkconclusion"));
            	list.add(pfMtlCheckDTO);
            }


        });

        return new Pagination<>(stockPage,list);
    }

    @Override
    @Transactional
    public void copy(RestJsonWrapperBean wrapper) {
        String uniqueNo = wrapper.getParamValue("uniqueNo");
        String expireDate = wrapper.getParamValue("expireDate");

        EmcStockBean copiedStock = wrapper.parseUnique(EmcStockBean.class);
        List<EmcStockConcentBean> copiedConcentList = stockConcentService.getDao().selectListByOneColumnValue(copiedStock.getId(), "stockId");

        EmcStockBean newStock = new EmcStockBean();
        BeanUtils.copyProperties(copiedStock, newStock, EmcConstant.CREATED_COLUMNS);
        newStock.setId(ApplicationContextHelper.getNextIdentity());
        newStock.setBatchNo(uniqueNo);
        newStock.setExpireDate(LocalDateTime.of(LocalDate.parse(expireDate), LocalTime.of(0, 0)));
        getDao().insert(newStock);

        List<EmcStockConcentBean> newConcentList = new ArrayList<>(copiedConcentList.size());
        copiedConcentList.forEach(concent -> {
            EmcStockConcentBean newConcent = new EmcStockConcentBean();
            BeanUtils.copyProperties(concent, newConcent, EmcConstant.CREATED_COLUMNS);
            newConcent.setId(ApplicationContextHelper.getNextIdentity());
            newConcent.setStockId(newStock.getId());
            newConcentList.add(newConcent);
        });
        stockConcentService.getDao().insert(newConcentList);

        EmcStockLogBean newStockLog = new EmcStockLogBean();
        newStockLog.setQty(1D);
        newStockLog.setId(ApplicationContextHelper.getNextIdentity());
        newStockLog.setStockId(newStock.getId());
        newStockLog.setLogType(LOG_TYPE_ADD);
        stockLogService.getDao().insert(newStockLog);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void delete(String mtlId, RestJsonWrapperBean wrapper) {
        PfStockService.super.delete(wrapper);
      //更新材料管理库存
/*        if(!StringUtils.isEmpty(mtlId)){
            mtlService.updateStockQty(mtlId);
        }*/

        //已存在的数量
        List<EmcStockBean> stockExistList = getDao().selectListByOneColumnValue(mtlId, "mtlId");
        int insertFinal = stockExistList.size();
        EmcMtlBean mtlBean = mtlService.getDao().selectById(mtlId);
        mtlBean.setStockQty(Double.valueOf(insertFinal));
        mtlService.getDao().update(mtlBean, "stockQty");
    }


}
