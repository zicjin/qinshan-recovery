package com.sunwayworld.emc.module.mdm.bizmdm.test.service;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcRelatedTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodDefaultQcBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2021年06月17日
 */
public interface PfTestMethodService extends EmcTestMethodService {

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

    void beforeHandleCopyTo(RestJsonWrapperBean wrapper);

    void scriptFileCopyTo(RestJsonWrapperBean wrapper);

    void delQcIndex(RestJsonWrapperBean wrapper);

    void delRevision(RestJsonWrapperBean wrapper);
}
