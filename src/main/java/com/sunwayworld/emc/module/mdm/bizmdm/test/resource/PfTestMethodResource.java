package com.sunwayworld.emc.module.mdm.bizmdm.test.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcRelatedTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodDefaultQcBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;

/**
 * @author zhengxy@sunwayworld.com 2021年05月28日
 */
@RequestMapping("/secure/emc/module/mdm/bizmdm/tests")
public interface PfTestMethodResource extends EmcTestMethodResource {

    @RequestMapping(value = "/tests/action/validate-unique", method = RequestMethod.POST)
    RestValidationResultBean validateTestUnique(RestJsonWrapperBean wrapper);

    Page<EmcTestMethodDefaultQcBean> selectDefaultQcPagination(Long id, RestJsonWrapperBean wrapper);

    void insertDefaultQc(Long id, RestJsonWrapperBean wrapper);

    void deleteDefaultQc(Long id, RestJsonWrapperBean wrapper);

    Page<EmcRelatedTestMethodBean> selectRelatedTestMethodPagination(Long id, RestJsonWrapperBean wrapper);

    void insertRelatedTestMethod(Long id, RestJsonWrapperBean wrapper);

    void deleteRelatedTestMethod(Long id, RestJsonWrapperBean wrapper);

    void batchInsertRelatedTestMethod(RestJsonWrapperBean wrapper);

    void copyByCategory(RestJsonWrapperBean wrapper);

    void copyDefaultQcTo(RestJsonWrapperBean wrapper);

    void syncTestId(RestJsonWrapperBean wrapper);
}
