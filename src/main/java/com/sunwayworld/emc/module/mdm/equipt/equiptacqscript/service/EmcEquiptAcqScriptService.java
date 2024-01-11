package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service;

import java.util.Map;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.bean.EmcEquiptAcqResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEquiptAcqScriptBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface EmcEquiptAcqScriptService extends GenericService<EmcEquiptAcqScriptBean, Long>, GenericChoosableService<EmcEquiptAcqScriptBean, Long> {

    void insertParam(Long id, RestJsonWrapperBean wrapper);

    void deleteParam(Long id, RestJsonWrapperBean wrapper);

    Long insertPreAnaly(Long id);

    void deletePreAnaly(Long id, RestJsonWrapperBean wrapper);

    Long insertResult(Long id);

    void deleteResult(Long id, RestJsonWrapperBean wrapper);

    Map<String, String> extractTextFromFileForTest(Long id, Long fileId);

    Map<String, String> preAnalyTest(Long id, RestJsonWrapperBean wrapper);

    Page<EmcEquiptAcqResultBean> analyResultTest(Long id, Long fileId);

    RestValidationResultBean validateFieldName(RestJsonWrapperBean wrapper);

    void copyResult(Long id, RestJsonWrapperBean wrapper);
}
