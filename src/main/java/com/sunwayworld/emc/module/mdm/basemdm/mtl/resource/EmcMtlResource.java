package com.sunwayworld.emc.module.mdm.basemdm.mtl.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockConcentBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockLogBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月19日 下午6:22:19
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/mtls")
public interface EmcMtlResource extends GenericResource<EmcMtlService, EmcMtlBean, String>,
                                        GenericChoosableResource<EmcMtlService, EmcMtlBean, String> {

    Long insertStock(String id, RestJsonWrapperBean wrapper);

    Page<EmcStockBean> selectStockPagationByMtl(String id, RestJsonWrapperBean wrapper);

    Page<EmcStockLogBean> selectStockLogPagationByStock(Long stockId, RestJsonWrapperBean wrapper);

    void deleteStock(String id, RestJsonWrapperBean wrapper);

    Long stockinStock(Long id, RestJsonWrapperBean wrapper);

    void inventoryStock(String id, RestJsonWrapperBean wrapper);

    void abandonStock(String id, RestJsonWrapperBean wrapper);

    void recieveStock(Long id, RestJsonWrapperBean wrapper);

    Page<EmcMtlMaintainBean> selectMaintainPagationByMtl(String id, RestJsonWrapperBean wrapper);

    void insertMaintain(String id, RestJsonWrapperBean wrapper);

    void deleteMaintain(String id, RestJsonWrapperBean wrapper);

    Page<EmcMtlMaintainItemBean> selectMaintainItemPagationByMaintain(Long id, RestJsonWrapperBean wrapper);

    void insertMaintainItem(Long id, RestJsonWrapperBean wrapper);

    void deleteMaintainItem(RestJsonWrapperBean wrapper);

    Page<EmcStockConcentBean> selectConcentPagationByStock(Long id, RestJsonWrapperBean wrapper);

    Page<EmcStockBean> selectDetailChoosablePagination(RestJsonWrapperBean wrapper);

    RestValidationResultBean validateStockUnique(Long id, String columnName, String columnValue);

    Long insertGeneralStock(String id, RestJsonWrapperBean wrapper);

    void changeStockConcent(RestJsonWrapperBean wrapper);

    Page<EmcStockBean> selectQcStdStockPagination(RestJsonWrapperBean wrapper);
}
