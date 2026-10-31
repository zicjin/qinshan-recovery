package com.sunwayworld.emc.module.mdm.bizmdm.capacity.resource.impl;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.resource.EmcTestCapacityResource;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcTestCapacityService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.validator.EmcItemGroupDeleteValidator;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule("监测能力库")
@RestController
@GikamBean
public class EmcTestCapacityResourceImpl implements EmcTestCapacityResource {

    @Autowired
    private EmcTestCapacityService emcTestCapacityService;

    @Autowired
    private EmcCapacityItemGroupService capacityItemGroupService;

    @Override
    public EmcTestCapacityService getService() {
        return emcTestCapacityService;
    }

    @Log(value = "新增监测能力库", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Log(value = "批量新增监测能力库", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/batchInsert", method = RequestMethod.POST)
    public Long batchInsert(RestJsonWrapperBean jsonWrapper) {
        return getService().batchInsert(jsonWrapper);
    }

    @Log(value = "通过监测能力查询分析项组", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/item-groups/queries", method = RequestMethod.POST)
    public Page<EmcCapacityItemGroupBean> selectItemGroupPagationByTestCapacity(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return capacityItemGroupService.selectPaginationByFilter(SearchFilter.instance().match("TESTCAPACITYID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Log(value = "批量新增分析项组", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/item-groups/insert", method = RequestMethod.POST)
    public Long batchInsertItemGroup(@PathVariable Long id,RestJsonWrapperBean jsonWrapper) {
        return capacityItemGroupService.batchInsert(id,jsonWrapper);
    }

    @Log(value = "组分析项选择页查询", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/item-groups/queries/choosable", method = RequestMethod.POST)
    public Page<EmcCapacityItemGroupBean> selectGroupItemChoosablePagination(RestJsonWrapperBean wrapper) {
        return capacityItemGroupService.selectChoosablePagination(wrapper);
    }

    @Override
    @Log(value = "删除分析项组", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/item-groups", method = RequestMethod.DELETE)
    @ValidateDataWith(EmcItemGroupDeleteValidator.class)
    public void deleteItemGroup(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        capacityItemGroupService.delete(wrapper);
    }
}
