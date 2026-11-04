/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.fileacqconfig.resource;

import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqConfigBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqParamBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqPreprocessBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqResultBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqResultParamBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/equipt/file-acq-configs"})
public interface CoreFileAcqConfigResource
extends GenericCloudResource<CoreFileAcqConfigBean, Long>,
GenericChoosableCloudResource<CoreFileAcqConfigBean, Long> {
    @RequestMapping(value={"/{id}/acq-params/queries"}, method={RequestMethod.POST})
    public Page<CoreFileAcqParamBean> selectAcqParamPaginationByFileAcqConfigId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/acq-params"}, method={RequestMethod.POST})
    public Long insertAcqParam(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/acq-params"}, method={RequestMethod.DELETE})
    public void deleteAcqParam(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/acq-preprocesses/queries"}, method={RequestMethod.POST})
    public Page<CoreFileAcqPreprocessBean> selectAcqPreprocessPaginationByFileAcqConfigId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/acq-preprocesses"}, method={RequestMethod.POST})
    public Long insertAcqPreprocess(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/acq-preprocesses"}, method={RequestMethod.DELETE})
    public void deleteAcqPreprocess(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/acq-result-params/queries"}, method={RequestMethod.POST})
    public Page<CoreFileAcqResultParamBean> selectAcqResultParamPaginationByFileAcqConfigId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/acq-result-params"}, method={RequestMethod.POST})
    public Long insertAcqResultParam(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/acq-result-params"}, method={RequestMethod.DELETE})
    public void deleteAcqResultParam(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/files/{fileId}/info/content"}, method={RequestMethod.GET})
    public String selectFileContentByFileId(@PathVariable Long var1, @PathVariable Long var2);

    @RequestMapping(value={"/{id}/action/preprocess"}, method={RequestMethod.PUT})
    public String preprocess(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/action/test"}, method={RequestMethod.POST})
    public Page<CoreFileAcqResultBean> test(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/action/copy"}, method={RequestMethod.PUT})
    public void copy(RestJsonWrapperBean var1);
}
