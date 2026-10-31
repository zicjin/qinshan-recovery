package com.sunwayworld.emc.module.bp.project.project.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import com.alibaba.fastjson.JSONObject;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

/**
 * @author luoc@sunwayworld.com
 * @date 2021-05-07
 */
@RequestMapping("/secure/emc/module/bp/project/projects")
public interface PfProjectResource extends EmcProjectResource{

    void reportAssignSubmit(RestJsonWrapperBean wrapper);

    void qcMtlSelect(RestJsonWrapperBean wrapper);

    void undoDelivery(RestJsonWrapperBean wrapper);

    void schemeEditSubmit(RestJsonWrapperBean wrapper);

    String checkMoney(RestJsonWrapperBean wrapper);

    String cancelCheck(RestJsonWrapperBean wrapper);

    String submitBeforeCheck(RestJsonWrapperBean wrapper);

    void sendReport(RestJsonWrapperBean wrapper);

    Page<CoreFileBean> queryReportFile(RestJsonWrapperBean wrapper);

    String sampleContractFile(RestJsonWrapperBean wrapper);

    void syncProjInfoAfterEdit(RestJsonWrapperBean wrapper);

    String haveWriteOff(RestJsonWrapperBean wrapper);

    void repairProjectData(RestJsonWrapperBean wrapper);

    void moveOrder(RestJsonWrapperBean wrapper);

    void moveOrderItemGroup(RestJsonWrapperBean wrapper);

    String submitRemind(RestJsonWrapperBean wrapper);

    JSONObject exportProjectToExcel(RestJsonWrapperBean wrapper);


}
