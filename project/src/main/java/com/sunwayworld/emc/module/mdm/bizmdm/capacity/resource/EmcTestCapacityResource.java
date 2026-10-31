package com.sunwayworld.emc.module.mdm.bizmdm.capacity.resource;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcTestCapacityBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcTestCapacityService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/secure/emc/module/mdm/bizmdm/test/test-capacitys")
public interface EmcTestCapacityResource extends GenericResource<EmcTestCapacityService, EmcTestCapacityBean, Long> {

    Long batchInsert(RestJsonWrapperBean jsonWrapper);

    Page<EmcCapacityItemGroupBean> selectItemGroupPagationByTestCapacity(Long id, RestJsonWrapperBean wrapper);

    Long batchInsertItemGroup(Long id,RestJsonWrapperBean jsonWrapper);

    Page<EmcCapacityItemGroupBean> selectGroupItemChoosablePagination(RestJsonWrapperBean wrapper);

    void deleteItemGroup(Long id, RestJsonWrapperBean wrapper);

}
