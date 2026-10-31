package com.sunwayworld.emc.module.mdm.basemdm.mtl.service;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.PfMtlCheckDTO;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

public interface PfStockService extends EmcStockService {
    Long insert(String mtlId, RestJsonWrapperBean wrapper);

	Page<PfMtlCheckDTO> selectMtlCheck(RestJsonWrapperBean wrapper);

    void copy(RestJsonWrapperBean wrapper);
}
