package com.sunwayworld.emc.module.mdm.equipt.equipt.service.impl;




import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptMaintainRepairBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptMaintainRepairDTO;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptPerCheckDTO;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptPlanDTO;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptUserDTO;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptMaintainRepairService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPerCheckPlanService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPlanService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.PfEquiptService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.impl.EmcEquiptServiceImpl;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import com.sunwayworld.framework.utils.StringUtils;;



@Repository
@GikamBean
public class PfEquiptServiceImpl extends EmcEquiptServiceImpl implements PfEquiptService {
	
    @Autowired
	@Lazy
    private EmcEquiptPlanService equiptPlanService;
    
    @Autowired
    private EmcEquiptPerCheckPlanService equiptPerCheckPlanService;

    @Autowired
    private CoreUserService userService;

    @Autowired
    private EmcEquiptMaintainRepairService equiptMaintainRepairService;

    
    @Override
    public Page<PfEquiptPlanDTO> selectEquiptPlan(RestJsonWrapperBean wrapper) {
    	//根据仪器表和仪器校准计划表组合台账
        List<PfEquiptPlanDTO> list = new ArrayList<>();
        List<EmcEquiptBean> equptOrgList =  selectListByFilter(SearchFilter.instance().match("orgId", LocalContextHelper.getLoginUser().getExt$Item("parentOrgId")).filter(MatchPattern.SEQ));
        List<Long> equptOrgIdList = equptOrgList.stream().map(EmcEquiptBean :: getId).distinct().collect(Collectors.toList());;
        //List<EmcEquiptPlanBean> equplanList =  equiptPlanService.selectListByFilter(SearchFilter.instance().match("equiptId",equptOrgIdList).filter(MatchPattern.OR));
        
        Page<EmcEquiptPlanBean> equiptPlanPage  =  equiptPlanService.selectPaginationByFilter(SearchFilter.instance().match("equiptId",equptOrgIdList).filter(MatchPattern.OR),wrapper);

        List<EmcEquiptPlanBean> equplanList = equiptPlanPage.getRows();
        
        List<Long> equiptIdList = equplanList.stream().map(EmcEquiptPlanBean :: getEquiptId).distinct().collect(Collectors.toList());
        List<EmcEquiptBean> equiptList = getDao().selectListByIds(equiptIdList);
    	List<String> keepUserIdBeforeList = equiptList.stream().map(EmcEquiptBean :: getKeepUserId).distinct().collect(Collectors.toList());
    	List<String> keepUserIdList = keepUserIdBeforeList.stream().filter(t ->!StringUtils.isEmpty(t)).collect(Collectors.toList());

    	List<CoreUserBean> userList = userService.getDao().selectListByIds(keepUserIdList);
        equplanList.forEach(ep ->{
        	PfEquiptPlanDTO pfEquiptPlanDTO = new PfEquiptPlanDTO();
        	List<EmcEquiptBean> matchEquiptList = equiptList.stream().filter(e ->ObjectUtils.equals(ep.getEquiptId(),e.getId())).collect(Collectors.toList());
        	if(!matchEquiptList.isEmpty()){
            	pfEquiptPlanDTO.setEquiptNo(matchEquiptList.get(0).getEquiptId());
            	pfEquiptPlanDTO.setEquiptName(matchEquiptList.get(0).getEquiptName());
            	pfEquiptPlanDTO.setSpecification(matchEquiptList.get(0).getSpecification());
            	pfEquiptPlanDTO.setVerifyOrg(ep.getVerifyOrg());
            	pfEquiptPlanDTO.setCycle(ep.getCycle());
            	if(ep.getExt$Item("maintlastdate")!=null &&ep.getExt$Item("maintlastdate")!=""){
                	pfEquiptPlanDTO.setMaintLastDate(LocalDate.parse(ep.getExt$Item("maintlastdate"), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            	}
            	if(ep.getExt$Item("maintplandate")!=null &&ep.getExt$Item("maintplandate")!=""){
                	pfEquiptPlanDTO.setMaintPlanDate(LocalDate.parse(ep.getExt$Item("maintplandate"), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            	}
            	pfEquiptPlanDTO.setRemark(ep.getRemark());
            	pfEquiptPlanDTO.setKeepUserId(matchEquiptList.get(0).getKeepUserId());
            	List<CoreUserBean> userNList = userList.stream().filter(u ->StringUtils.equals(matchEquiptList.get(0).getKeepUserId(),u.getId())).collect(Collectors.toList());
            	if(!userNList.isEmpty()){
                	pfEquiptPlanDTO.setKeepUserName(userNList.get(0).getUserName());
            	}

            	list.add(pfEquiptPlanDTO);	
        	}


        });
        return new Pagination<>(equiptPlanPage,list);

    }

    @Override
    public Page<PfEquiptPerCheckDTO> selectEquiptPerCheck(RestJsonWrapperBean wrapper) {
    	
        List<PfEquiptPerCheckDTO> list = new ArrayList<>();
        List<EmcEquiptBean> equptOrgList =  selectListByFilter(SearchFilter.instance().match("orgId", LocalContextHelper.getLoginUser().getExt$Item("parentOrgId")).filter(MatchPattern.SEQ));
        List<Long> equptOrgIdList = equptOrgList.stream().map(EmcEquiptBean :: getId).distinct().collect(Collectors.toList());;
        
        //List<EmcEquiptPerCheckPlanBean> equiptPerCheckList =  equiptPerCheckPlanService.selectListByFilter(SearchFilter.instance().match("equiptId",equptOrgIdList).filter(MatchPattern.OR));
        Page<EmcEquiptPerCheckPlanBean> EmcEquiptPerCheckPlanPage  =  equiptPerCheckPlanService.selectPaginationByFilter(SearchFilter.instance().match("equiptId",equptOrgIdList).filter(MatchPattern.OR),wrapper);

        List<EmcEquiptPerCheckPlanBean> equiptPerCheckList  = EmcEquiptPerCheckPlanPage.getRows();
        
        
        List<Long> equiptIdList = equiptPerCheckList.stream().map(EmcEquiptPerCheckPlanBean :: getEquiptId).distinct().collect(Collectors.toList());
        List<EmcEquiptBean> equiptList = getDao().selectListByIds(equiptIdList);
    	

        equiptPerCheckList.forEach(epc ->{
        	PfEquiptPerCheckDTO pfEquiptPerCheckDTO = new PfEquiptPerCheckDTO();
        	List<EmcEquiptBean> matchEquiptList = equiptList.stream().filter(e ->ObjectUtils.equals(epc.getEquiptId(),e.getId())).collect(Collectors.toList());
        	if(!matchEquiptList.isEmpty()){
        		pfEquiptPerCheckDTO.setEquiptNo(matchEquiptList.get(0).getEquiptId());
        		pfEquiptPerCheckDTO.setEquiptName(matchEquiptList.get(0).getEquiptName());
        		pfEquiptPerCheckDTO.setSpecification(matchEquiptList.get(0).getSpecification());
            	if(epc.getExt$Item("maintlastdate")!=null &&epc.getExt$Item("maintlastdate")!=""){
            		pfEquiptPerCheckDTO.setMaintLastDate(LocalDate.parse(epc.getExt$Item("maintlastdate"), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            	}
            	pfEquiptPerCheckDTO.setMaintDate(epc.getMaintDate());
            	pfEquiptPerCheckDTO.setCheckIndex(epc.getExt$Item("checkindex"));
            	pfEquiptPerCheckDTO.setCheckWay(epc.getCheckWay());
            	pfEquiptPerCheckDTO.setCheckReason(epc.getCheckReason());
            	pfEquiptPerCheckDTO.setCheckUserId(epc.getCheckUserId());
            	pfEquiptPerCheckDTO.setCheckUserName(epc.getCheckUserName());
            	pfEquiptPerCheckDTO.setRemark(epc.getRemark());
 
            	
            	list.add(pfEquiptPerCheckDTO);	
        	}


        });
        return new Pagination<>(EmcEquiptPerCheckPlanPage,list);

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
    public Page<PfEquiptUserDTO> selectEquiptUser(RestJsonWrapperBean wrapper) {
    	//根据仪器表和仪器校准计划表组合台账
        List<PfEquiptUserDTO> list = new ArrayList<>();
        //List<EmcEquiptBean> equList =  equiptService.selectListByFilter(SearchFilter.instance().match("orgId", LocalContextHelper.getLoginUser().getExt$Item("parentOrgId")).filter(MatchPattern.SEQ));
        Page<EmcEquiptBean> equiptPage  =  selectPaginationByFilter(SearchFilter.instance().match("orgId", LocalContextHelper.getLoginUser().getExt$Item("parentOrgId")).filter(MatchPattern.SEQ),wrapper);

        List<EmcEquiptBean> equList  = equiptPage.getRows();
        
        equList.forEach(ep ->{
        	PfEquiptUserDTO pfEquiptUserDTO = new PfEquiptUserDTO();
        	pfEquiptUserDTO.setEquiptNo(ep.getEquiptId());
        	pfEquiptUserDTO.setEquiptName(ep.getEquiptName());
        	pfEquiptUserDTO.setSpecification(ep.getSpecification());
        	//使用人员
        	pfEquiptUserDTO.setUsers(ep.getUsedUserName());
        	list.add(pfEquiptUserDTO);	

        });
        return new Pagination<>(equiptPage,list);

    }
    
}
