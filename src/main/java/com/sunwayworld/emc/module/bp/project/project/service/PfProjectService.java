package com.sunwayworld.emc.module.bp.project.project.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.bean.PfProjectTaskProcessDTO;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

/**
 * @author zhengxy@sunwayworld.com 2021年06月01日
 */
public interface PfProjectService extends EmcProjectService {

    void undoDelivery(RestJsonWrapperBean wrapper);

    void schemeEditSubmit(RestJsonWrapperBean wrapper);

    String checkMoney(RestJsonWrapperBean wrapper);

    String cancelCheck(RestJsonWrapperBean wrapper);

    String submitBeforeCheck(RestJsonWrapperBean wrapper);

	Page<PfProjectTaskProcessDTO> selectProjectTaskProcess(RestJsonWrapperBean wrapper);

	Page<PfProjectTaskProcessDTO> selectAssignProjectTaskProcess(String analysisOrgid,RestJsonWrapperBean wrapper);

    void sendReport(RestJsonWrapperBean wrapper);

    Page<CoreFileBean> queryReportFile(RestJsonWrapperBean wrapper);

    String sampleContractFile(RestJsonWrapperBean wrapper);

    void syncProjInfoAfterEdit(RestJsonWrapperBean wrapper);

    String haveWriteOff(RestJsonWrapperBean wrapper);

    void repairProjectData(RestJsonWrapperBean wrapper);

    void createReportSendInfo(EmcProjectBean project);

    void createProjectBpData(List<EmcProjectBean> projectList);

    void moveOrder(RestJsonWrapperBean wrapper);

    void moveOrderItemGroup(RestJsonWrapperBean wrapper);
}
