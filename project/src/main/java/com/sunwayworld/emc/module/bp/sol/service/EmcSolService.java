package com.sunwayworld.emc.module.bp.sol.service;

import com.sunwayworld.emc.module.bp.sol.bean.EmcSolBean;
import com.sunwayworld.emc.module.bp.sol.bean.EmcSolLineBean;
import com.sunwayworld.emc.module.bp.sol.bean.EmcSolRecordBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcSolService extends GenericService<EmcSolBean, Long> {

    Long insertRecord(Long id, RestJsonWrapperBean wrapper);

    Page<EmcSolRecordBean> selectRecordPaginationById(Long id, RestJsonWrapperBean wrapper);

    void deleteRecord(RestJsonWrapperBean wrapper);

    void calucatePrepareConcent(RestJsonWrapperBean wrapper);

    void calucateCalibrateConcent(RestJsonWrapperBean wrapper);

    void copy(RestJsonWrapperBean wrapper);

    void publish(RestJsonWrapperBean wrapper);

    Page<EmcSolLineBean> selectSolLinePagination(Long id, RestJsonWrapperBean wrapper);

    Long insertSolLine(RestJsonWrapperBean wrapper);

    void deleteSolLine(RestJsonWrapperBean wrapper);

    EmcSolLineBean selectSolLineDetail(Long id);

}
