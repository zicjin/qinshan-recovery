package com.sunwayworld.emc.module.bp.result.run.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/bp/result/runs")
public interface EmcRunResource extends GenericResource<EmcRunService, EmcRunBean, String>, GenericAuditableResource<EmcRunService, EmcRunBean, String> {
    Long addOrderTask(String id, RestJsonWrapperBean wrapper);

    Long deleteOrderTask(RestJsonWrapperBean wrapper);

    Long init(RestJsonWrapperBean wrapper);

    Long updatedefaultresults(RestJsonWrapperBean wrapper);

    Long sampleTestInitialize(RestJsonWrapperBean wrapper);

    Long addQc(RestJsonWrapperBean wrapper);

    Long delQc(RestJsonWrapperBean wrapper);

    Page<EmcEquiptBean> selectEquiptPagination(String testMethodId);

    Page<EmcEquiptBean> selectEquiptPagination(String testMethodId, RestJsonWrapperBean wrapper);

    Long runInput(RestJsonWrapperBean wrapper);

    Page<EmcItemBean> selectEditItem(Long id,RestJsonWrapperBean wrapper);

    Long editItem(RestJsonWrapperBean wrapper);

    void syncTmplInfo(RestJsonWrapperBean wrapper);

    void syncSubTmplInfo(RestJsonWrapperBean wrapper);

    void removeFomula(RestJsonWrapperBean wrapper);

    void modifyUnit(RestJsonWrapperBean wrapper);

    void modifyLimit(RestJsonWrapperBean wrapper);

    Page<EmcRunBean> selectAllPagination(RestJsonWrapperBean wrapper);
    
    Page<EmcRunBean> selectRunResult(RestJsonWrapperBean wrapper);

    void methodEdit(RestJsonWrapperBean wrapper);

    void triggerFormlua(RestJsonWrapperBean wrapper);

    void equiptCollect(String id, RestJsonWrapperBean wrapper);

    Page<EmcEquiptBean> selectEditEquiptPagination(String testMethodId, String equiptId);

    void editEquipt(RestJsonWrapperBean wrapper);

    void recomposeRunPdf(RestJsonWrapperBean wrapper);

    String correlationValidate(RestJsonWrapperBean wrapper);

    void submitAnalysisProject(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectOrderTaskPagination(RestJsonWrapperBean wrapper);

    void copyBlankTo(RestJsonWrapperBean wrapper);

    void copyBlankSpkcTo(RestJsonWrapperBean wrapper);

    void batchInputResultRule(RestJsonWrapperBean wrapper);

    void recomposeProjectRunPdf(RestJsonWrapperBean wrapper);
    
    void modifyRevision(RestJsonWrapperBean wrapper);

	Page<EmcRunBean> analysisPretrial(RestJsonWrapperBean wrapper);

	void passPreApproval(RestJsonWrapperBean wrapper);

}
