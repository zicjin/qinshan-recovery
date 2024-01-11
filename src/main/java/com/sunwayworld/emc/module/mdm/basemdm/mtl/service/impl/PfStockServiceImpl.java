package com.sunwayworld.emc.module.mdm.basemdm.mtl.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;

@Repository
@GikamBean
public class PfStockServiceImpl extends EmcStockServiceImpl implements PfStockService {

    @Autowired
    private EmcStockLogService stockLogService;

    @Autowired
    private PfMtlService mtlService;

    @Autowired
    @Lazy
    private EmcMtlMaintainService mtlMaintainService;

    @Autowired
    private EmcStockConcentService stockConcentService;

	@Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(String mtlId, RestJsonWrapperBean wrapper) {
        EmcStockLogBean emcStockLog = wrapper.parseUnique(EmcStockLogBean.class);
        //库存
        EmcStockBean emcStock = wrapper.parseUnique(EmcStockBean.class);
        //根据试剂类别(RS/QC/RW)生成不同的序号
        String reagentCategory = emcStock.getExt$Item("REAGENTCATEGORY");
		String tagetHead = null;
		String tagetNo = null;
		String  batchTargetNo = null;
		Calendar calendar = Calendar.getInstance();
		String year = Integer.toString(calendar.get(Calendar.YEAR)).substring(2);
        if(reagentCategory != null && reagentCategory !=""){
        	if(reagentCategory.equals("QC")){
        		//ZK-QC22-0001
        		tagetHead ="ZK-QC";
        		tagetNo = tagetHead +year;
        		//找到对应试剂类别下的最大编号
/*                List<EmcStockBean> stockQCList = getDao().selectListByOneColumnValue(reagentCategory,"REAGENTCATEGORY");
*/
                List<EmcStockBean> stockQCList = selectListByFilter(SearchFilter.instance().match("REAGENTCATEGORY", reagentCategory).filter(MatchPattern.SEQ)
                	       .match("batchNo", tagetHead).filter(MatchPattern.SB));

                if(stockQCList.isEmpty()){
                     batchTargetNo = tagetNo +"-"+"0001";
                }else{
                    stockQCList.sort(Comparator.comparing(EmcStockBean::getBatchNo).reversed());
                    String batchNo = stockQCList.get(0).getBatchNo();//此类别下的最大ID,例ZK-QC23-1000
                    int batchNoNumber=Integer.parseInt(batchNo.substring(8));
                    int batchNoTargetNumber= batchNoNumber+1;

                    batchTargetNo = tagetNo +"-"+String.format("%0"+4+"d", batchNoTargetNumber);
                }

        	}else if(reagentCategory.equals("RS")){
        		//ZKRS220001
        		tagetHead ="ZK-RS";
        		tagetNo = tagetHead +year;
        		//找到对应试剂类别下的最大编号
                List<EmcStockBean> stockQCList = selectListByFilter(SearchFilter.instance().match("REAGENTCATEGORY", reagentCategory).filter(MatchPattern.SEQ)
             	       .match("batchNo", tagetHead).filter(MatchPattern.SB));
                if(stockQCList.isEmpty()){
                    batchTargetNo = tagetNo +"-"+"0001";
                }else{
                    stockQCList.sort(Comparator.comparing(EmcStockBean::getBatchNo).reversed());
                    String batchNo = stockQCList.get(0).getBatchNo();//此类别下的最大ID,例ZK-RS-23-1000
                    int batchNoNumber=Integer.parseInt(batchNo.substring(8));
                    int batchNoTargetNumber= batchNoNumber+1;
                    batchTargetNo = tagetNo+"-" +String.format("%0"+4+"d", batchNoTargetNumber);
                }
        	}else{
        		//ZKRSW220001
        		tagetHead ="ZK-RW";
        		tagetNo = tagetHead +year;
        		//找到对应试剂类别下的最大编号
                List<EmcStockBean> stockQCList = selectListByFilter(SearchFilter.instance().match("REAGENTCATEGORY", reagentCategory).filter(MatchPattern.SEQ)
                		 .match("batchNo", tagetHead).filter(MatchPattern.SB));
                if(stockQCList.isEmpty()){
                    batchTargetNo = tagetNo +"-"+"0001";
                }else{
                    stockQCList.sort(Comparator.comparing(EmcStockBean::getBatchNo).reversed());
                    String batchNo = stockQCList.get(0).getBatchNo();//此类别下的最大ID,例ZKRW231000
                    int batchNoNumber=Integer.parseInt(batchNo.substring(8));
                    int batchNoTargetNumber= batchNoNumber+1;
                    batchTargetNo = tagetNo +"-"+String.format("%0"+4+"d", batchNoTargetNumber);
                }
        	}

        }
        List<String> batchNoList = new ArrayList<>();
        batchNoList.add(batchTargetNo);

        List<EmcMtlMaintainBean> maintainList = mtlMaintainService.getDao().selectListByOneColumnValue(mtlId,"MTLID");

        int qty = Optional.ofNullable(emcStockLog.getQty()).orElse(0D).intValue();

        List<EmcStockBean> newStockList = new ArrayList<>(qty);
        List<EmcStockLogBean> newStockLogList = new ArrayList<>(qty);
        List<EmcStockConcentBean> newStockConcentList = new ArrayList<>();

        IntStream.range(0, qty).forEach(i -> {
            EmcStockBean newStock = new EmcStockBean();
            BeanUtils.copyProperties(emcStock, newStock);


            int batchNoNumber=Integer.parseInt(batchNoList.get(0).substring(8));
            String batchTargetNoHead = batchNoList.get(0).substring(0,8);//ZK-RS-23-1000
            int batchNoTargetNumber= batchNoNumber+i;
            String targetNo = batchTargetNoHead+String.format("%0"+4+"d", batchNoTargetNumber);

            newStock.setStockQty(1D);
            newStock.setId(ApplicationContextHelper.getNextIdentity());
            newStock.setMtlId(mtlId);
            newStock.setStatus(Constant.NO);
            newStock.setBatchNo(targetNo);
            newStockList.add(newStock);

            EmcStockLogBean newStockLog = new EmcStockLogBean();
            BeanUtils.copyProperties(emcStockLog, newStockLog);
            newStockLog.setQty(1D);
            newStockLog.setId(ApplicationContextHelper.getNextIdentity());
            newStockLog.setStockId(newStock.getId());
            newStockLog.setLogType(LOG_TYPE_ADD);
            newStockLogList.add(newStockLog);
            for(int a =0;a<maintainList.size();a++){
            	 EmcStockConcentBean newStockConcent = new EmcStockConcentBean();
                 newStockConcent.setId(ApplicationContextHelper.getNextIdentity()+a);
                 newStockConcent.setStockId(newStock.getId());
                 newStockConcent.setCategory(maintainList.get(a).getCategory());
                 newStockConcent.setMtlMaintainId(maintainList.get(a).getId());
                 newStockConcentList.add(newStockConcent);
            }

        });

        getDao().insert(newStockList);

        //历史记录
        stockLogService.getDao().insert(newStockLogList);

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
        //添加浓度
        stockConcentService.getDao().insert(newStockConcentList);

        return emcStock.getId();
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
