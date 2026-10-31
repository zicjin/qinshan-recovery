package com.sunwayworld.emc.module.mdm.bizmdm.capacity.resource;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodDefaultQcBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

public interface PfTestCapacityResource extends EmcTestCapacityResource{
    Page<EmcTestMethodDefaultQcBean> selectDefaultQcPagination(Long id, RestJsonWrapperBean wrapper);

    void copyAllToOtherOrg(RestJsonWrapperBean wrapper);

    void copyInfoTo(RestJsonWrapperBean wrapper);

    Page<EmcCapacityItemBean> selectCapacityItemPagination(RestJsonWrapperBean wrapper);

    void syncItem(RestJsonWrapperBean wrapper);

    void refetchItem(RestJsonWrapperBean wrapper);

    void copyQcTo(RestJsonWrapperBean wrapper);
}
