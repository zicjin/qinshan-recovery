package com.sunwayworld.emc.module.mdm.bizmdm.capacity.service;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodDefaultQcBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2021年06月01日
 */
public interface PfTestCapacityService extends EmcTestCapacityService {
    Page<EmcTestMethodDefaultQcBean> selectDefaultQcPagination(Long id, RestJsonWrapperBean wrapper);

    void copyAllToOtherOrg(RestJsonWrapperBean wrapper);

    void copyInfoTo(RestJsonWrapperBean wrapper);

    Page<EmcCapacityItemBean> selectCapacityItemPagination(RestJsonWrapperBean wrapper);

    void syncItem(RestJsonWrapperBean wrapper);

    void refetchItem(RestJsonWrapperBean wrapper);

    void copyQcTo(RestJsonWrapperBean wrapper);

    Long refetchItemByTest(RestJsonWrapperBean wrapper);
}
