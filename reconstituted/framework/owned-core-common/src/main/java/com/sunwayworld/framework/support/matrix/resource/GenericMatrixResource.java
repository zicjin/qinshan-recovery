/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.matrix.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.matrix.bean.MatrixHeaderBean;
import com.sunwayworld.framework.support.matrix.bean.MatrixRowMap;
import com.sunwayworld.framework.support.matrix.service.GenericMatrixService;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface GenericMatrixResource {
    public <S extends GenericMatrixService> S getMatrixService();

    @Log(value="\u77e9\u9635\u7684\u52a8\u6001\u8868\u5934\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries/matrix-headers"}, method={RequestMethod.POST})
    default public List<MatrixHeaderBean> selectMatrixHeaderList() {
        return this.getMatrixService().selectMatrixHeaderList();
    }

    @Log(value="\u77e9\u9635\u7684\u52a8\u6001\u884c\u6570\u636e\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries/matrix-rows"}, method={RequestMethod.POST})
    default public Page<MatrixRowMap> selectMatrixRowPagination(RestJsonWrapperBean wrapper) {
        return this.getMatrixService().selectMatrixRowPagination(wrapper);
    }
}
