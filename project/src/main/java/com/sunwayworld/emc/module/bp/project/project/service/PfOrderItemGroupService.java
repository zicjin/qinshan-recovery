package com.sunwayworld.emc.module.bp.project.project.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

public interface PfOrderItemGroupService extends EmcOrderItemGroupService{

    void qcMtlSelect(RestJsonWrapperBean wrapper);
}
