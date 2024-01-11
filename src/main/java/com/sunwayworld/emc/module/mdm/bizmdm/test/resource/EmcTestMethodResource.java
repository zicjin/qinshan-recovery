package com.sunwayworld.emc.module.mdm.bizmdm.test.resource;

import java.util.List;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */
@RequestMapping("/secure/emc/module/mdm/bizmdm/tests")
public interface EmcTestMethodResource extends GenericResource<EmcTestMethodService, EmcTestMethodBean, Long>, GenericActivatableResource<EmcTestMethodService, EmcTestMethodBean, Long>,
        GenericChoosableResource<EmcTestMethodService, EmcTestMethodBean, Long>, GenericSelectableResource<EmcTestMethodService, EmcTestMethodBean, Long> {

    EmcItemBean selectItemDetail(Long itemId);

    Long insertItem(Long id, RestJsonWrapperBean wrapper);

    void deleteItem(Long id, RestJsonWrapperBean wrapper);

    Page<EmcItemBean> selectItemPagationByTestMethod(Long id, RestJsonWrapperBean wrapper);

    Long insertItemGroup(Long id, RestJsonWrapperBean wrapper);

    void deleteItemGroup(Long id, RestJsonWrapperBean wrapper);

    void copyItemGroup(Long id, RestJsonWrapperBean wrapper);

    Page<EmcItemGroupBean> selectItemGroupPagationByTestMethod(Long id, RestJsonWrapperBean wrapper);

    void insertEquipt(Long id, RestJsonWrapperBean wrapper);

    void deleteEquipt(Long id, RestJsonWrapperBean wrapper);

    public void setDefaultEquipt(Long id, RestJsonWrapperBean wrapper);

    Page<EmcEquiptTestMethodBean> selectEquiptPagationByTestMethod(Long id, RestJsonWrapperBean wrapper);

    Long insertItemQcIndex(Long itemId, RestJsonWrapperBean wrapper);

    void deleteItemQcIndex(Long itemId, RestJsonWrapperBean wrapper);

    Page<EmcItemQcIndexBean> selectQcIndexPagationByItem(Long itemId, RestJsonWrapperBean wrapper);

    Long insertItemRevision(Long itemId, RestJsonWrapperBean wrapper);

    void deleteItemRevision(Long itemId, RestJsonWrapperBean wrapper);

    Page<EmcItemRevisionBean> selectRevisionPagationByItem(Long itemId, RestJsonWrapperBean wrapper);

    Long insertOrderCategory(Long id, RestJsonWrapperBean wrapper);

    void deleteOrderCategory(Long id, RestJsonWrapperBean wrapper);

    Page<EmcTestMethodOrderCategoryBean> selectOrderCategoryPagationByTestMethod(Long id, RestJsonWrapperBean wrapper);

    Page<EmcItemBean> selectItemPagination(RestJsonWrapperBean wrapper);

    Page<EmcItemBean> selectItemChoosablePagination(RestJsonWrapperBean wrapper);

    Page<EmcItemGroupBean> selectItemGroupChoosablePagination(RestJsonWrapperBean wrapper);

    void insertGroupItem(Long itemGroupId, RestJsonWrapperBean wrapper);

    void deleteGroupItem(Long itemGroupId, RestJsonWrapperBean wrapper);

    Page<EmcGroupItemBean> selectGroupItemPagationByItemGroup(Long itemGroupId, RestJsonWrapperBean wrapper);

    void activateItems(RestJsonWrapperBean wrapper);

    void deactivateItems(RestJsonWrapperBean wrapper);

    void copyItem(RestJsonWrapperBean jsonWrapper);

    void copyItemTo(RestJsonWrapperBean jsonWrapper);

    Long insertItemResult(Long itemId, RestJsonWrapperBean wrapper);

    void deleteItemResult(Long itemId, RestJsonWrapperBean wrapper);

    Page<EmcItemResultBean> selectResultPagationByItem(Long itemId, RestJsonWrapperBean wrapper);

    void copy(RestJsonWrapperBean jsonWrapper);

    void move(RestJsonWrapperBean jsonWrapper);

    void copyEquiptTo(RestJsonWrapperBean jsonWrapper);

    Long insertTest(RestJsonWrapperBean wrapper);

    void deleteTest(RestJsonWrapperBean wrapper);

    Page<EmcTestBean> selectTestRawPagination(RestJsonWrapperBean wrapper);

    Page<EmcTestBean> selectTestChoosablePagination(RestJsonWrapperBean wrapper);

    Page<EmcGroupItemBean> selectGroupItemChoosablePagination(RestJsonWrapperBean wrapper);

    Page<EmcItemResultBean> selectItemResultChoosablePagination(RestJsonWrapperBean wrapper);

    List<EmcTestBean> selectTestSelectableList();

    List<EmcItemBean> selectTestItemSelectableList();

    RestValidationResultBean validateItemNameUnique(RestJsonWrapperBean wrapper, @PathVariable Long testMethodId);

    List<Long> itemGroupCheckUsed(RestJsonWrapperBean wrapper);

    List<Long> testCheckUsed(RestJsonWrapperBean wrapper);

    List<Long> testMethodCheckUsed(RestJsonWrapperBean wrapper);

    void itemQcIndexCopyTo(RestJsonWrapperBean wrapper);

    Long copyOrganicItemGroup(RestJsonWrapperBean wrapper);

    void editOrganicItemGroup(RestJsonWrapperBean wrapper);

    void copyOrganicFormulaTo(Long itemGroupId, RestJsonWrapperBean wrapper);

    void copyItemRevision(RestJsonWrapperBean wrapper);

    void copyItemRevisionTo(RestJsonWrapperBean wrapper);
    
    RestValidationResultBean validateUnique(Long id, String columnName, String columnValue);
    
    void geneItemGroup(RestJsonWrapperBean wrapper);
    
    void lock(RestJsonWrapperBean wrapper);
    
    void unlock(RestJsonWrapperBean wrapper);
    
    Page<EmcTestMethodLogBean> selectLogPagination(RestJsonWrapperBean wrapper);

    void copyFormulaTo(RestJsonWrapperBean wrapper);

    Long insertQcItem(RestJsonWrapperBean wrapper);

    void deleteQcItem(RestJsonWrapperBean wrapper);

    void batchUpdateQcIndex(RestJsonWrapperBean wrapper);

    Page<EmcQcItemBean> selectQcItemPagation(RestJsonWrapperBean wrapper);

    Long insertTestMethodBeforehandle(Long testMethodId, RestJsonWrapperBean wrapper);

    void deleteTestMethodBeforehandle(Long testMethodId, RestJsonWrapperBean wrapper);

    Page<EmcTestmethodBeforehandleBean> selectTestMethodBeforehandlePagationByTestMethod(Long testMethodId, RestJsonWrapperBean wrapper);

    Page<EmcTestmethodBeforehandleBean> selectTestMethodBeforehandleChoosablePagination(RestJsonWrapperBean wrapper);

}
