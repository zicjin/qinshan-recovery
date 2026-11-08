package com.sunwayworld.emc.module.mdm.equipt.equipt.service.impl;
import com.sunwayworld.framework.database.sql.Order;




import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptMaintainRepairBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptMaintainRepairDTO;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptPerCheckDTO;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptPlanDTO;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptUserDTO;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.PfEquiptLedgerDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptPerCheckPlanQueryDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPerCheckPlanService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptMaintainRepairService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.PfEquiptService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.impl.EmcEquiptServiceImpl;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;



@Repository
@GikamBean
public class PfEquiptServiceImpl extends EmcEquiptServiceImpl implements PfEquiptService {
	
    @Autowired
    private EmcEquiptMaintainRepairService equiptMaintainRepairService;

    @Autowired
    private PfEquiptLedgerDao equiptLedgerDao;

    @Autowired
    private EmcEquiptPerCheckPlanService perCheckPlanService;

    @Autowired
    private EmcEquiptPerCheckPlanQueryDao perCheckPlanQueryDao;

    @Override
    @Transactional(readOnly = true)
    public Page<EmcEquiptPerCheckPlanBean> selectGlobalPerCheckPlans(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        CoreUserBean user = LocalContextHelper.getLoginUser();
        // Independent predicates always use the actual session, never client
        // scope flags or a supplied principal in the JSON wrapper.
        parameter.put("globalPlanParentOrgId", user == null ? null : user.getExt$Item("parentOrgId"));
        parameter.put("globalPlanLoginUserId", user == null ? null : user.getId());
        parameter.setTableName("T_EMC_EQUIPT_PER_CHECK_PLAN");
        return perCheckPlanService.selectPagination(
                () -> perCheckPlanQueryDao.selectGlobalPerCheckPlans(parameter),
                wrapper.extractPageRowBounds());
    }

    
    @Override
    @Transactional(readOnly = true)
    public Page<PfEquiptPlanDTO> selectEquiptPlan(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = ledgerParameter(wrapper, "T_EMC_EQUIPT_PLAN");
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return MybatisPageHelper.get(rowBounds, () -> equiptLedgerDao.selectLedgerPlan(parameter));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PfEquiptPerCheckDTO> selectEquiptPerCheck(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = ledgerParameter(wrapper, "T_EMC_EQUIPT_PER_CHECK_PLAN");
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return MybatisPageHelper.get(rowBounds, () -> equiptLedgerDao.selectLedgerPerCheck(parameter));
    }

    @Override
    public Page<PfEquiptMaintainRepairDTO> selectEquiptMaintainRepair(RestJsonWrapperBean wrapper) {

    	 List<EmcEquiptBean> equptOrgList =  selectListByFilter(SearchFilter.instance().match("orgId", LocalContextHelper.getLoginUser().getExt$Item("parentOrgId")).filter(MatchPattern.SEQ));
         List<Long> equptOrgIdList = equptOrgList.stream().map(EmcEquiptBean :: getId).distinct().collect(Collectors.toList());;
                  
        List<PfEquiptMaintainRepairDTO> list = new ArrayList<>();
        //List<EmcEquiptMaintainRepairBean> equiptMaintainRepairList =  equiptMaintainRepairService.selectListByFilter(SearchFilter.instance().match("equiptId",equptOrgIdList).filter(MatchPattern.OR));
        Page<EmcEquiptMaintainRepairBean> EmcEquiptMaintainRepairPage  =  equiptMaintainRepairService.selectPaginationByFilter(SearchFilter.instance().match("equiptId",equptOrgIdList).filter(MatchPattern.OR),wrapper);

        List<EmcEquiptMaintainRepairBean> equiptMaintainRepairList  = EmcEquiptMaintainRepairPage.getRows();
        
        if(equiptMaintainRepairList.isEmpty()){
            return new Pagination<>(list);
        }
        List<Long> equiptIdList = equiptMaintainRepairList.stream().map(EmcEquiptMaintainRepairBean :: getEquiptId).distinct().collect(Collectors.toList());

        List<EmcEquiptBean> equiptList = getDao().selectListByIds(equiptIdList);
    	

        equiptMaintainRepairList.forEach(epc ->{
        	PfEquiptMaintainRepairDTO pfEquiptMaintainRepairDTO = new PfEquiptMaintainRepairDTO();
        	List<EmcEquiptBean> matchEquiptList = equiptList.stream().filter(e ->ObjectUtils.equals(epc.getEquiptId(),e.getId())).collect(Collectors.toList());
        	if(!matchEquiptList.isEmpty()){
        		pfEquiptMaintainRepairDTO.setEquiptNo(matchEquiptList.get(0).getEquiptId());
            	if(epc.getMaintainRepairDate()!=null){
            		pfEquiptMaintainRepairDTO.setMaintainRepairDate(epc.getMaintainRepairDate());
            	}
            	pfEquiptMaintainRepairDTO.setMaintainRepairConcent(epc.getMaintainRepairConcent());
            	pfEquiptMaintainRepairDTO.setMaintainRepairSign(epc.getMaintainRepairSign());
            	pfEquiptMaintainRepairDTO.setMaintainrepairunit(epc.getMaintainrepairunit());
            	pfEquiptMaintainRepairDTO.setEquiptFailure(epc.getEquiptFailure());
 
            	
            	list.add(pfEquiptMaintainRepairDTO);	
        	}


        });
        return new Pagination<>(EmcEquiptMaintainRepairPage,list);

    }

    @Override
    @Transactional(readOnly = true)
    public Page<PfEquiptUserDTO> selectEquiptUser(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = ledgerParameter(wrapper, "T_EMC_EQUIPT");
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return MybatisPageHelper.get(rowBounds, () -> equiptLedgerDao.selectLedgerUser(parameter));
    }

    private MapperParameter ledgerParameter(RestJsonWrapperBean wrapper, String tableName) {
        MapperParameter parameter = wrapper.extractMapFilter();
        // This trusted scope must override client parameters, including a supplied
        // ledgerParentOrgId. SQL applies it independently of all client filters.
        parameter.put("ledgerParentOrgId", LocalContextHelper.getLoginUser().getExt$Item("parentOrgId"));
        parameter.setTableName(tableName);
        return parameter;
    }

    @Override
    @Transactional
    public void updateStatusBySourceDate() {
        List<EmcEquiptBean> emcEquiptBeanList = this.selectListByFilter(SearchFilter.instance().match("SOURCEDATE", (Object)LocalDate.now()).filter(MatchPattern.DL), new Order[0]);
        if (!emcEquiptBeanList.isEmpty()) {
            for (EmcEquiptBean e : emcEquiptBeanList) {
                e.setStatus("notUse");
            }
            this.getDao().update(emcEquiptBeanList, new String[]{"STATUS"});
        }
    }
}
