/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.matrix.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericDownloadableService;
import com.sunwayworld.framework.support.matrix.bean.MatrixHeaderBean;
import com.sunwayworld.framework.support.matrix.bean.MatrixRowMap;
import java.util.List;
import java.util.Map;

public interface GenericMatrixService
extends GenericDownloadableService {
    public List<MatrixHeaderBean> selectMatrixHeaderList();

    public Page<MatrixRowMap> selectMatrixRowPagination(RestJsonWrapperBean var1);

    public void save(List<Map<String, Object>> var1);
}
