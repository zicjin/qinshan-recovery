/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.pdfacqconfig.service;

import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqPreprocessBean;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqResultDTO;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface CorePdfAcqPreprocessService
extends GenericService<CorePdfAcqPreprocessBean, Long> {
    public String preprocess(Long var1, String var2);

    public List<CorePdfAcqResultDTO> resultPreprocess(Long var1, List<CorePdfAcqResultDTO> var2);
}
