package com.sunwayworld.emc.module.mdm.basemdm.mtl.service;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.service.GenericService;
/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月20日 下午3:31:18
 */
public interface EmcStockService extends GenericService<EmcStockBean, Long> {

    Long insert(String mtlId, RestJsonWrapperBean wrapper);

    void storage(Long id, RestJsonWrapperBean wrapper);

    void inventory(String id, RestJsonWrapperBean wrapper);

    void abandon(String id, RestJsonWrapperBean wrapper);

    void recieve(Long id, RestJsonWrapperBean wrapper);

    void delete(String id, RestJsonWrapperBean wrapper);

    Page<EmcStockBean> selectStockLowLine(RestJsonWrapperBean wrapper);

    Page<EmcStockBean> selectStockOverdue(RestJsonWrapperBean wrapper);

    Page<EmcStockBean> selectDetailChoosablePagination(RestJsonWrapperBean wrapper);

    RestValidationResultBean validateStockUnique(Long id, String columnName, String columnValue);

    Long insertGeneralStock(String mtlId, RestJsonWrapperBean wrapper);

    Page<EmcStockBean> selectQcStdStockPagination(RestJsonWrapperBean wrapper);
}
