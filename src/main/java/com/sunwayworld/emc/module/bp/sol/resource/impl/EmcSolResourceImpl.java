package com.sunwayworld.emc.module.bp.sol.resource.impl;

import com.sunwayworld.emc.module.bp.sol.service.EmcSolRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.sol.bean.EmcSolLineBean;
import com.sunwayworld.emc.module.bp.sol.bean.EmcSolRecordBean;
import com.sunwayworld.emc.module.bp.sol.resource.EmcSolResource;
import com.sunwayworld.emc.module.bp.sol.service.EmcSolService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("标准溶液配制")
@RestController
@GikamBean
public class EmcSolResourceImpl implements EmcSolResource {
    @Autowired
    private EmcSolService solService;

    @Autowired
    private EmcSolRecordService solRecordService;

    @Override
    public EmcSolService getService() {
        return solService;
    }

    @Log(value = "新增标准溶液配制", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Log(value = "新增标定原始记录", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/records", method = RequestMethod.POST)
    public Long insertRecord(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().insertRecord(id, wrapper);
    }

    @Log(value = "查询标定原始记录", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/records/queries", method = RequestMethod.POST)
    public Page<EmcSolRecordBean> selectRecordPaginationById(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectRecordPaginationById(id, wrapper);
    }

    @Log(value = "删除标定原始记录", type = LogType.DELETE)
    @Override
    @RequestMapping(value = "/records", method = RequestMethod.DELETE)
    public void deleteRecord(RestJsonWrapperBean wrapper) {
        getService().deleteRecord(wrapper);
    }

    @Log(value = "计算标准溶液配制后浓度", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/action/calucate-prepare", method = RequestMethod.PUT)
    public void calucatePrepareConcent(RestJsonWrapperBean wrapper) {
        getService().calucatePrepareConcent(wrapper);
    }

    @Log(value = "计算标准溶液标定后浓度", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/action/calucate-calibrate", method = RequestMethod.PUT)
    public void calucateCalibrateConcent(RestJsonWrapperBean wrapper) {
        getService().calucateCalibrateConcent(wrapper);
    }

    @Log(value = "复制标定溶液记录", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/action/copy", method = RequestMethod.POST)
    public void copy(RestJsonWrapperBean wrapper) {
        getService().copy(wrapper);
    }

    @Log(value = "发布标定溶液记录", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/action/publish", method = RequestMethod.PUT)
    public void publish(RestJsonWrapperBean wrapper) {
        getService().publish(wrapper);
    }

    @Override
    @RequestMapping(value = "/{id}/lines/queries", method = RequestMethod.POST)
    public Page<EmcSolLineBean> selectSolLinePagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectSolLinePagination(id, wrapper);
    }

    @Override
    @Log(value = "新增标准溶液配制明细", type = LogType.INSERT)
    @RequestMapping(value = "/lines", method = RequestMethod.POST)
    public Long insertSolLine(RestJsonWrapperBean wrapper) {
        return getService().insertSolLine(wrapper);
    }

    @Override
    @Log(value = "删除标准溶液配制明细", type = LogType.DELETE)
    @RequestMapping(value = "/lines", method = RequestMethod.DELETE)
    public void deleteSolLine(RestJsonWrapperBean wrapper) {
        getService().deleteSolLine(wrapper);
    }

    @Override
    @Log(value = "标定溶液详细信息查询", type = LogType.SELECT)
    @RequestMapping(value = "/lines/{id}", method = RequestMethod.GET)
    public EmcSolLineBean selectSolLineDetail(@PathVariable Long id) {
        return getService().selectSolLineDetail(id);
    }

    @Log(value = "标定溶液计算", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/action/calc-result", method = RequestMethod.PUT)
    public void calcResult(RestJsonWrapperBean wrapper) {
        solRecordService.calcResult(wrapper);
    }
}
