package com.sunwayworld.emc.module.bp.sol.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.sol.bean.EmcSolBean;
import com.sunwayworld.emc.module.bp.sol.bean.EmcSolLineBean;
import com.sunwayworld.emc.module.bp.sol.bean.EmcSolRecordBean;
import com.sunwayworld.emc.module.bp.sol.service.EmcSolService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/bp/sols")
public interface EmcSolResource extends GenericResource<EmcSolService, EmcSolBean, Long> {

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

    void calcResult(RestJsonWrapperBean wrapper);

    EmcSolLineBean selectSolLineDetail(Long id);
}
