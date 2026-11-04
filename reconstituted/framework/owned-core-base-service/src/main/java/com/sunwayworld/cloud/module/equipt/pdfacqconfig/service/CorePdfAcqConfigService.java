/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.pdfacqconfig.service;

import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqConfigBean;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqPreprocessBean;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqResultDTO;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfPageDTO;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import java.util.List;

public interface CorePdfAcqConfigService
extends GenericService<CorePdfAcqConfigBean, Long>,
GenericChoosableService<CorePdfAcqConfigBean, Long>,
GenericSelectableService<CorePdfAcqConfigBean, Long> {
    public List<CorePdfAcqResultDTO> parse(Long var1, Long var2);

    public Long insertLine(Long var1, RestJsonWrapperBean var2);

    public void deleteLine(Long var1, RestJsonWrapperBean var2);

    public Page<CorePdfPageDTO> selectLinePaginationById(Long var1, RestJsonWrapperBean var2);

    public Page<CorePdfAcqPreprocessBean> selectAcqPreprocessPaginationByFileAcqConfigId(Long var1, RestJsonWrapperBean var2);

    public Long insertAcqPreprocess(Long var1, RestJsonWrapperBean var2);

    public void deleteAcqPreprocess(Long var1, RestJsonWrapperBean var2);
}
