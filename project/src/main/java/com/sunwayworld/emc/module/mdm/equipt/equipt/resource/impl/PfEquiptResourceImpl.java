package com.sunwayworld.emc.module.mdm.equipt.equipt.resource.impl;



import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptMaintainRepairBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptMaintainRepairDTO;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptPerCheckDTO;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptPlanDTO;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptUserDTO;
import com.sunwayworld.emc.module.mdm.equipt.equipt.resource.PfEquiptResource;
import com.sunwayworld.emc.module.mdm.equipt.equipt.resource.impl.EmcEquipCertResourceImpl;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptMaintainRepairService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.PfEquiptService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@GikamBean
public class PfEquiptResourceImpl extends EmcEquipCertResourceImpl implements PfEquiptResource {

    @Autowired
    private PfEquiptService pfEquiptService;
    
    @Autowired
    private EmcEquiptMaintainRepairService equiptMaintainRepairService;

    @Override
    @Log(value = "查询仪器期间核查计划", type = LogType.SELECT)
    @RequestMapping(value = "/per-check-plans/queries", method = RequestMethod.POST)
    public Page<EmcEquiptPerCheckPlanBean> selectGlobalPerCheckPlans(RestJsonWrapperBean wrapper) {
        return pfEquiptService.selectGlobalPerCheckPlans(wrapper);
    }
    
    @Log(value = "仪器检定查询", type = LogType.SELECT)
    @RequestMapping(value = "/action/select-equipt-plan", method = RequestMethod.POST)
    public Page<PfEquiptPlanDTO> selectEquiptPlan(RestJsonWrapperBean wrapper) {
        return pfEquiptService.selectEquiptPlan(wrapper);
    }

    @Log(value = "仪器期间核查计划查询", type = LogType.SELECT)
    @RequestMapping(value = "/action/select-equipt-per-check-search", method = RequestMethod.POST)
    public Page<PfEquiptPerCheckDTO> selectEquiptPerCheck(RestJsonWrapperBean wrapper) {
        return pfEquiptService.selectEquiptPerCheck(wrapper);
    }
    
    
    @Override
    @Log(value = "查询维护维修记录", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/maintain/queries", method = RequestMethod.POST)
    public Page<EmcEquiptMaintainRepairBean> selectEquiptMaintainRepairPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return equiptMaintainRepairService.selectPaginationByFilter(SearchFilter.instance().match("equiptId",id).filter(MatchPattern.EQ),wrapper);
    }
    
    @Log(value = "仪器期维护计划查询", type = LogType.SELECT)
    @RequestMapping(value = "/action/select-equipt-maintain-repair-search", method = RequestMethod.POST)
    public Page<PfEquiptMaintainRepairDTO> selectEquiptMaintainRepair(RestJsonWrapperBean wrapper) {
        return pfEquiptService.selectEquiptMaintainRepair(wrapper);
    }
    
    @Log(value = "仪器设备使用授权人员清单", type = LogType.SELECT)
    @RequestMapping(value = "/action/select-equipt-user", method = RequestMethod.POST)
    public Page<PfEquiptUserDTO> selectEquiptUser(RestJsonWrapperBean wrapper) {
        return pfEquiptService.selectEquiptUser(wrapper);
    }
    
    
}
