/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.pdfacqconfig.resource;

import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqConfigBean;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqPreprocessBean;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqResultDTO;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfPageDTO;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableCloudResource;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/equipt/pdf-acq-configs"})
public interface CorePdfAcqConfigResource
extends GenericCloudResource<CorePdfAcqConfigBean, Long>,
GenericChoosableCloudResource<CorePdfAcqConfigBean, Long>,
GenericSelectableCloudResource<CorePdfAcqConfigBean, Long> {
    @RequestMapping(value={"/{id}/lines"}, method={RequestMethod.POST})
    public Long insertLine(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/lines"}, method={RequestMethod.DELETE})
    public void deleteLine(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/lines/queries"}, method={RequestMethod.POST})
    public Page<CorePdfPageDTO> selectLinePaginationById(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/acq-preprocesses/queries"}, method={RequestMethod.POST})
    public Page<CorePdfAcqPreprocessBean> selectAcqPreprocessPaginationByFileAcqConfigId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/files/{fileId}/action/test"}, method={RequestMethod.POST})
    public List<CorePdfAcqResultDTO> test(@PathVariable Long var1, @PathVariable Long var2);

    @RequestMapping(value={"/{id}/acq-preprocesses"}, method={RequestMethod.POST})
    public Long insertAcqPreprocess(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/acq-preprocesses"}, method={RequestMethod.DELETE})
    public void deleteAcqPreprocess(@PathVariable Long var1, RestJsonWrapperBean var2);
}
