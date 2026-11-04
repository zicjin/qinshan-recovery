/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filetype.service;

import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileCategoryBean;
import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileCategoryRoleAuthBean;
import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileTypeBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import java.util.List;

public interface CoreFileTypeService
extends GenericService<CoreFileTypeBean, Long>,
GenericChoosableService<CoreFileTypeBean, Long>,
GenericSelectableService<CoreFileTypeBean, Long>,
GenericActivatableService<CoreFileTypeBean, Long> {
    public Page<CoreFileCategoryBean> selectCategoryPaginationByFileTypeId(Long var1, RestJsonWrapperBean var2);

    public Long insertCategory(Long var1, RestJsonWrapperBean var2);

    public void deleteCategory(Long var1, RestJsonWrapperBean var2);

    public Page<CoreFileCategoryRoleAuthBean> selectCategoryRoleAuthPaginationByFileTypeId(Long var1, RestJsonWrapperBean var2);

    public Long insertCategoryRoleAuth(Long var1, RestJsonWrapperBean var2);

    public void deleteCategoryRoleAuth(Long var1, RestJsonWrapperBean var2);

    public List<CoreFileCategoryBean> copyCategoryDraftTemplate(Long var1, RestJsonWrapperBean var2);

    public void activateFileCategory(RestJsonWrapperBean var1);

    public void deactivateFileCategory(RestJsonWrapperBean var1);
}
