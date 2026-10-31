package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.resource;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.bean.EmcEquiptAcqResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasParamBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasPreAnalyBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEquiptAcqScriptBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEquiptAcqScriptService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/mdm/equipt/equipt-acq-scripts")
public interface EmcEquiptAcqScriptResource
        extends GenericResource<EmcEquiptAcqScriptService, EmcEquiptAcqScriptBean, Long>, GenericChoosableResource<EmcEquiptAcqScriptService, EmcEquiptAcqScriptBean, Long> {

    @RequestMapping(value = "/{id}/params/queries", method = RequestMethod.POST)
    Page<EmcEasParamBean> selectSearchParamPaginationByScript(Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/params", method = RequestMethod.POST)
    void insertParam(Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/params", method = RequestMethod.DELETE)
    void deleteParam(Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/pre-analies/queries", method = RequestMethod.POST)
    Page<EmcEasPreAnalyBean> selectSearchPreAnalyPaginationByScript(Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/pre-analies", method = RequestMethod.POST)
    Long insertPreAnaly(Long id);

    @RequestMapping(value = "/{id}/pre-analies", method = RequestMethod.DELETE)
    void deletePreAnaly(Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/results/queries", method = RequestMethod.POST)
    Page<EmcEasResultBean> selectSearchResultPaginationByScript(Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/results", method = RequestMethod.POST)
    Long insertResult(Long id);

    @RequestMapping(value = "/{id}/results", method = RequestMethod.DELETE)
    void deleteResult(Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/files/queries/{fileId}", method = RequestMethod.POST)
    Map<String, String> extractTextFromFileForTest(Long id, Long fileId);

    @RequestMapping(value = "/{id}/action/pre-analy-test", method = RequestMethod.POST)
    Map<String, String> preAnalyTest(Long id, RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/action/analy-result-test/{fileId}", method = RequestMethod.POST)
    Page<EmcEquiptAcqResultBean> analyResultTest(Long id, Long fileId, RestJsonWrapperBean wrapper);

    RestValidationResultBean validateFieldName(RestJsonWrapperBean wrapper);

    @RequestMapping(value = "/{id}/results/action/copy", method = RequestMethod.POST)
    void copyResult(Long id, RestJsonWrapperBean wrapper);
}
