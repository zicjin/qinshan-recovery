package com.sunwayworld.emc.module.mdm.equipt.equipt.resource;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptDCUConfBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestItemBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptUseRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/mdm/equipt/equipts")
public interface EmcEquiptResource extends GenericResource<EmcEquiptService, EmcEquiptBean, Long>, GenericChoosableResource<EmcEquiptService, EmcEquiptBean, Long> {

    Long insertTestMethod(Long id, RestJsonWrapperBean wrapper);

    Page<EmcEquiptTestMethodBean> selectEquiptTestMethodRawPagination(Long id, RestJsonWrapperBean wrapper);

    void deleteTestMethod(Long id, RestJsonWrapperBean wrapper);

    Long insertTestItem(Long id, RestJsonWrapperBean wrapper);

    Page<EmcEquiptTestItemBean> selectEquiptTestItemRawPagination(Long id, RestJsonWrapperBean wrapper);

    void deleteTestItem(Long id, RestJsonWrapperBean wrapper);

    EmcEquiptDCUConfBean selectSearchDCUConfDetailByEquipt(Long id, RestJsonWrapperBean wrapper);

    List<EmcEquiptMaintBean> selectMaintSelectableList(Long id, RestJsonWrapperBean wrapper);

    Long insertPlan(RestJsonWrapperBean wrapper);

    Long insertRecord(RestJsonWrapperBean wrapper);

    List<EmcEquiptMaintBean> selectRecordMaintSelectableList(Long id);

    void complete(RestJsonWrapperBean wrapper);

    void perCheckRecordcomplete(RestJsonWrapperBean wrapper);

    Page<EmcEquiptPlanBean> selectEquiptPlanRawPagination(Long id, RestJsonWrapperBean wrapper);

    Page<EmcEquiptRecordBean> selectEquiptRecordRawPagination(Long id, RestJsonWrapperBean wrapper);

    void deletePlan(RestJsonWrapperBean wrapper);

    void deleteRecord(RestJsonWrapperBean wrapper);

    void testMethodCopyTo(RestJsonWrapperBean wrapper);

    Page<EmcEquiptUseRecordBean> selectEquiptUseRecordRawPagination(Long id, RestJsonWrapperBean wrapper);

    void insertUseRecord(RestJsonWrapperBean wrapper);

    void updateUseRecord(RestJsonWrapperBean wrapper);

    void deleteUseRecord(RestJsonWrapperBean wrapper);

    Page<EmcEquiptUseRecordBean> selectEquiptUseRecordPagination(RestJsonWrapperBean wrapper);

    RestValidationResultBean validateDcuConfig(Long id);

    Long selectMainWaringDaysById(Long id);

    Page<EmcEquiptBean> selectCaliChooseRawPagination(RestJsonWrapperBean wrapper);

    Page<EmcEquiptBean> selectVerifyChooseRawPagination(RestJsonWrapperBean wrapper);

    Page<EmcEquiptBean> selectPerCheckChooseRawPagination(RestJsonWrapperBean wrapper);

    Long insertPerCheckPlan(RestJsonWrapperBean wrapper);

    void deletePerCheckPlan(RestJsonWrapperBean wrapper);

    Page<EmcEquiptPerCheckPlanBean> selectPerCheckPagination(Long id, RestJsonWrapperBean wrapper);

    Long insertPerCheckRecord(RestJsonWrapperBean wrapper);

    void deletePerCheckRecord(RestJsonWrapperBean wrapper);

    Page<EmcEquiptPerCheckRecordBean> selectPerCheckRecordPagination(Long id, RestJsonWrapperBean wrapper);

    List<EmcEquiptMaintBean> selectPerCheckRecordMaintSelectableList(Long id);
}
