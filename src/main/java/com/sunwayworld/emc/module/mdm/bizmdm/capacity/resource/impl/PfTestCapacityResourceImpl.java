package com.sunwayworld.emc.module.mdm.bizmdm.capacity.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.resource.PfTestCapacityResource;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.PfTestCapacityService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodDefaultQcBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("监测能力库")
@RestController
@GikamBean
public class PfTestCapacityResourceImpl extends EmcTestCapacityResourceImpl implements PfTestCapacityResource {

    @Autowired
    private PfTestCapacityService testCapacityService;

    @Override
    @Log(value = "通过测试方法查询默认QC", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/default-qcs/queries", method = RequestMethod.POST)
    public Page<EmcTestMethodDefaultQcBean> selectDefaultQcPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return testCapacityService.selectDefaultQcPagination(id, wrapper);
    }

    @Override
    @Log(value = "根据公司复制监测能力库数据", type = LogType.INSERT)
    @RequestMapping(value = "/copy-all-to-org", method = RequestMethod.POST)
    public void copyAllToOtherOrg(RestJsonWrapperBean wrapper) {
        testCapacityService.copyAllToOtherOrg(wrapper);
    }

    @Override
    @Log(value = "复制监测能力测试信息到其他测试", type = LogType.UPDATE)
    @RequestMapping(value = "/action/copy-info-to", method = RequestMethod.POST)
    public void copyInfoTo(RestJsonWrapperBean wrapper) {
        testCapacityService.copyInfoTo(wrapper);
    }

    @Override
    @Log(value = "复制监测能力的默认qc到其他测试", type = LogType.UPDATE)
    @RequestMapping(value = "/action/copy-qc-to", method = RequestMethod.POST)
    public void copyQcTo(RestJsonWrapperBean wrapper) {
        testCapacityService.copyQcTo(wrapper);
    }

    @Override
    @Log(value = "查询监测能力分析项列表", type = LogType.SELECT)
    @RequestMapping(value = "/capacity-items/queries", method = RequestMethod.POST)
    public Page<EmcCapacityItemBean> selectCapacityItemPagination(RestJsonWrapperBean wrapper) {
        return testCapacityService.selectCapacityItemPagination(wrapper);
    }

    @Override
    @Log(value = "同步分析项", type = LogType.SELECT)
    @RequestMapping(value = "/action/sync-item", method = RequestMethod.POST)
    public void syncItem(RestJsonWrapperBean wrapper) {
        testCapacityService.syncItem(wrapper);
    }

    @Override
    @Log(value = "重新获取分析项", type = LogType.SELECT)
    @RequestMapping(value = "/action/refetch-item", method = RequestMethod.POST)
    public void refetchItem(RestJsonWrapperBean wrapper) {
        testCapacityService.refetchItem(wrapper);
    }
}
