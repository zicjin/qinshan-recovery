/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.fileacqconfig.service;

import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqConfigBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqParamBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqPreprocessBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqResultBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqResultParamBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import java.util.List;

public interface CoreFileAcqConfigService
extends GenericService<CoreFileAcqConfigBean, Long>,
GenericChoosableService<CoreFileAcqConfigBean, Long> {
    public Page<CoreFileAcqParamBean> selectAcqParamPaginationByFileAcqConfigId(Long var1, RestJsonWrapperBean var2);

    public Long insertAcqParam(Long var1, RestJsonWrapperBean var2);

    public void deleteAcqParam(Long var1, RestJsonWrapperBean var2);

    public Page<CoreFileAcqPreprocessBean> selectAcqPreprocessPaginationByFileAcqConfigId(Long var1, RestJsonWrapperBean var2);

    public Long insertAcqPreprocess(Long var1, RestJsonWrapperBean var2);

    public void deleteAcqPreprocess(Long var1, RestJsonWrapperBean var2);

    public Page<CoreFileAcqResultParamBean> selectAcqResultParamPaginationByFileAcqConfigId(Long var1, RestJsonWrapperBean var2);

    public Long insertAcqResultParam(Long var1, RestJsonWrapperBean var2);

    public void deleteAcqResultParam(Long var1, RestJsonWrapperBean var2);

    public String selectFileContentByFileId(Long var1, Long var2);

    public String preprocess(Long var1);

    public Page<CoreFileAcqResultBean> test(Long var1, RestJsonWrapperBean var2);

    public String preprocessFile(Long var1);

    public List<CoreFileAcqResultBean> analyseFile(String var1, CoreFileAcqConfigBean var2);

    public void copy(RestJsonWrapperBean var1);
}
