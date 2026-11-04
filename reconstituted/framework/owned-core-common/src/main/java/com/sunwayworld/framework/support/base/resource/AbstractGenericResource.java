/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.base.resource;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.bean.CoreControllerRequestEntryDTO;
import com.sunwayworld.framework.support.base.bean.CoreServiceStatusDTO;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.validator.permission.annotation.ValidatePermissionWith;
import com.sunwayworld.framework.validator.permission.support.DefaultAvailablePermissionValidator;
import com.sunwayworld.framework.validator.permission.support.DefaultUpdatablePermissionValidator;
import java.io.Serializable;
import java.util.Objects;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface AbstractGenericResource<S extends GenericService<T, ID>, T extends Persistable<ID>, ID extends Serializable>
extends GenericCloudResource<T, ID> {
    public S getService();

    @Override
    @Log(value="\u901a\u7528\u67e5\u8be2", type=LogType.SELECT)
    default public Pagination<T> selectPagination(RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectPagination(wrapper);
    }

    @Override
    @Log(value="\u7f16\u8f91\u9875\u67e5\u8be2", type=LogType.SELECT)
    default public Pagination<T> selectRawPagination(RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectRawPagination(wrapper);
    }

    @Override
    @Log(value="\u8be6\u7ec6\u4fe1\u606f\u67e5\u8be2", type=LogType.SELECT)
    @ValidatePermissionWith(value=DefaultAvailablePermissionValidator.class, table="#this.getService().getDao().getTable()")
    default public T selectDetail(@PathVariable ID id) {
        return this.getService().selectDetail(id);
    }

    @Override
    @Log(value="\u5355\u4e2a\u5b57\u6bb5\u67e5\u8be2", type=LogType.SELECT)
    @ValidatePermissionWith(value=DefaultAvailablePermissionValidator.class, table="#this.getService().getDao().getTable()")
    default public String selectColumnById(@PathVariable ID id, @PathVariable String column) {
        return this.getService().selectColumnById(id, column);
    }

    @Override
    @Log(value="\u83b7\u53d6\u72b6\u6001\u4fe1\u606f", type=LogType.SELECT)
    default public CoreServiceStatusDTO selectStatus(@PathVariable(required=false) ID id, @RequestParam(required=false) String currentStatusCode) {
        return this.getService().selectStatus(id, currentStatusCode);
    }

    @Override
    default public CoreServiceStatusDTO selectStatus(@RequestParam(required=false) String currentStatusCode) {
        return this.getService().selectStatus(null, currentStatusCode);
    }

    @Override
    @Log(value="\u5220\u9664", type=LogType.DELETE)
    @ValidatePermissionWith(value=DefaultUpdatablePermissionValidator.class, table="#this.getService().getDao().getTable()")
    default public void delete(RestJsonWrapperBean wrapper) {
        this.getService().delete(wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e", type=LogType.INSERT)
    default public ID insert(RestJsonWrapperBean wrapper) {
        return this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u5b9e\u65f6\u4fee\u6539", type=LogType.UPDATE)
    default public void instantSave(RestJsonWrapperBean wrapper) {
        this.getService().instantSave(wrapper);
    }

    @Override
    @Log(value="\u4fee\u6539", type=LogType.UPDATE)
    @ValidatePermissionWith(value=DefaultUpdatablePermissionValidator.class, table="#this.getService().getDao().getTable()", category="normal")
    default public void save(@PathVariable ID id, RestJsonWrapperBean wrapper) {
        this.getService().save(id, wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u9875\u67e5\u8be2", type=LogType.SELECT)
    default public Pagination<T> selectSearchablePagination(RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectSearchablePagination(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u89d2\u8272\u4e0b\u7684\u6570\u636e", type=LogType.SELECT)
    default public Pagination<T> selectByRole(@PathVariable Long roleId, RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectByRole(roleId, wrapper);
    }

    @Override
    @Log(value="\u9a8c\u8bc1\u6570\u636e\u51c6\u786e\u6027", type=LogType.VALIDATE)
    default public RestValidationResultBean validate(@PathVariable ID id, RestJsonWrapperBean wrapper) {
        return this.getService().validate(id, wrapper);
    }

    @Override
    @Log(value="\u9a8c\u8bc1\u6570\u636e\u552f\u4e00\u6027", type=LogType.VALIDATE)
    default public RestValidationResultBean validateUnique(@PathVariable(required=false) ID id) {
        return this.getService().validateUnique(id, null, null);
    }

    @Override
    @Log(value="\u9a8c\u8bc1\u6570\u636e\u552f\u4e00\u6027", type=LogType.VALIDATE)
    default public RestValidationResultBean validateUnique(@PathVariable(required=false) ID id, @PathVariable(required=false) String columnName, @PathVariable(required=false) String columnValue) {
        return this.getService().validateUnique(id, columnName, columnValue);
    }

    @Override
    @Log(value="\u9a8c\u8bc1\u6570\u636e\u552f\u4e00\u6027", type=LogType.VALIDATE)
    default public RestValidationResultBean validateUnique(@PathVariable(required=false) String columnName, @PathVariable(required=false) String columnValue) {
        return this.getService().validateUnique(null, columnName, columnValue);
    }

    @Override
    @Log(value="\u9a8c\u8bc1\u6570\u636e\u552f\u4e00\u6027", type=LogType.VALIDATE)
    @RequestMapping(value={"/action/validate-unique"}, method={RequestMethod.POST})
    default public RestValidationResultBean validateUnique(RestJsonWrapperBean wrapper) {
        return this.getService().validateUnique(wrapper);
    }

    @Override
    @Log(value="\u4e92\u6362\u5bf9\u5e94\u7684\u503c", type=LogType.UPDATE)
    default public void swap(@PathVariable(required=false) String column, @PathVariable ID firstId, @PathVariable ID secondId) {
        this.getService().swap(Objects.toString(column, "ORDERNO"), firstId, secondId);
    }

    @Override
    @Log(value="\u4e92\u6362\u5bf9\u5e94\u7684\u503c", type=LogType.UPDATE)
    default public void swap(@PathVariable ID firstId, @PathVariable ID secondId) {
        this.getService().swap(Objects.toString(null, "ORDERNO"), firstId, secondId);
    }

    @Override
    @Log(value="\u67e5\u8be2\u5f53\u524d\u4e1a\u52a1\u5bf9\u5e94\u7684Mapper\u7684\u5185\u5bb9", type=LogType.SELECT)
    default public String getGikamAttrsMybatisMapper() {
        return this.getService().getGikamAttrsMybatisMapper();
    }

    @Override
    @Log(value="\u67e5\u8be2\u5f53\u524d\u4e1a\u52a1\u4e0b\u529f\u80fd", type=LogType.SELECT)
    default public Pagination<CoreControllerRequestEntryDTO> getGikamAttrsReqEntryPagination() {
        return (Pagination)this.getService().getGikamAttrsReqEntryPagination();
    }

    @Override
    @Log(value="\u6253\u5370\u8be6\u60c5", type=LogType.PRINT)
    @ValidatePermissionWith(value=DefaultAvailablePermissionValidator.class, table="#this.getService().getDao().getTable()", category="normal")
    default public JSONObject print(@PathVariable ID id) {
        return this.getService().print(id);
    }

    @Override
    @Log(value="\u83b7\u53d6\u5f53\u524d\u4e1a\u52a1\u5355\u636e\u5bf9\u5e94\u7684\u6d41\u7a0b\u5b9a\u4e49ID", type=LogType.SELECT)
    default public Long selectProcId(@PathVariable ID id) {
        return this.getService().selectProcId(id);
    }
}
