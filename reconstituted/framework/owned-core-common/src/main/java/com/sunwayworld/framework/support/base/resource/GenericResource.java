/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.base.resource;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.bean.CoreControllerRequestEntryDTO;
import com.sunwayworld.framework.support.base.bean.CoreServiceStatusDTO;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.validator.permission.annotation.ValidatePermissionWith;
import com.sunwayworld.framework.validator.permission.support.DefaultAvailablePermissionValidator;
import com.sunwayworld.framework.validator.permission.support.DefaultUpdatablePermissionValidator;
import java.io.Serializable;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface GenericResource<S extends GenericService<T, ID>, T extends Persistable<ID>, ID extends Serializable> {
    public S getService();

    @Log(value="\u901a\u7528\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries"}, method={RequestMethod.POST})
    default public Page<T> selectPagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectPagination(wrapper);
    }

    @Log(value="\u7f16\u8f91\u9875\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries/raw"}, method={RequestMethod.POST})
    default public Page<T> selectRawPagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectRawPagination(wrapper);
    }

    @Log(value="\u8be6\u7ec6\u4fe1\u606f\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/{id}"}, method={RequestMethod.GET})
    @ValidatePermissionWith(value=DefaultAvailablePermissionValidator.class, table="#this.getService().getDao().getTable()")
    default public T selectDetail(@PathVariable ID id) {
        return this.getService().selectDetail(id);
    }

    @Log(value="\u5355\u4e2a\u5b57\u6bb5\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/columns/{column}"}, method={RequestMethod.GET})
    @ValidatePermissionWith(value=DefaultAvailablePermissionValidator.class, table="#this.getService().getDao().getTable()")
    default public String selectColumnById(@PathVariable ID id, @PathVariable String column) {
        return this.getService().selectColumnById(id, column);
    }

    @Log(value="\u83b7\u53d6\u72b6\u6001\u4fe1\u606f", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/info/status", "/info/status"}, method={RequestMethod.GET})
    default public CoreServiceStatusDTO selectStatus(@PathVariable(required=false) ID id, @RequestParam(required=false) String currentStatusCode) {
        return this.getService().selectStatus(id, currentStatusCode);
    }

    @Log(value="\u5220\u9664", type=LogType.DELETE)
    @RequestMapping(method={RequestMethod.DELETE})
    @ValidatePermissionWith(value=DefaultUpdatablePermissionValidator.class, table="#this.getService().getDao().getTable()")
    default public void delete(RestJsonWrapperBean wrapper) {
        this.getService().delete(wrapper);
    }

    @Log(value="\u65b0\u589e", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    default public ID insert(RestJsonWrapperBean wrapper) {
        return this.getService().insert(wrapper);
    }

    @Log(value="\u5b9e\u65f6\u4fee\u6539", type=LogType.UPDATE)
    @RequestMapping(value={"/instant"}, method={RequestMethod.PUT})
    @ResponseStatus(value=HttpStatus.OK)
    default public void instantSave(RestJsonWrapperBean wrapper) {
        this.getService().instantSave(wrapper);
    }

    @Log(value="\u4fee\u6539", type=LogType.UPDATE)
    @RequestMapping(value={"/{id}"}, method={RequestMethod.PUT})
    @ResponseStatus(value=HttpStatus.OK)
    @ValidatePermissionWith(value=DefaultUpdatablePermissionValidator.class, table="#this.getService().getDao().getTable()", category="normal")
    default public void save(@PathVariable ID id, RestJsonWrapperBean wrapper) {
        this.getService().save(id, wrapper);
    }

    @Log(value="\u67e5\u8be2\u9875\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries/searchable"}, method={RequestMethod.POST})
    default public Page<T> selectSearchablePagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectSearchablePagination(wrapper);
    }

    @Log(value="\u67e5\u8be2\u89d2\u8272\u4e0b\u7684\u6570\u636e", type=LogType.SELECT)
    @RequestMapping(value={"/queries/roles/{roleId}"}, method={RequestMethod.POST})
    default public Page<T> selectByRole(@PathVariable Long roleId, RestJsonWrapperBean wrapper) {
        return this.getService().selectByRole(roleId, wrapper);
    }

    @Log(value="\u9a8c\u8bc1\u6570\u636e\u51c6\u786e\u6027", type=LogType.VALIDATE)
    @RequestMapping(value={"/{id}/action/validate"}, method={RequestMethod.POST})
    default public RestValidationResultBean validate(@PathVariable ID id, RestJsonWrapperBean wrapper) {
        return this.getService().validate(id, wrapper);
    }

    @Log(value="\u9a8c\u8bc1\u6570\u636e\u552f\u4e00\u6027", type=LogType.VALIDATE)
    @RequestMapping(value={"/{id}/action/validate-unique", "/{id}/action/validate-unique/{columnName}/{columnValue}", "/action/validate-unique/{columnName}/{columnValue}"}, method={RequestMethod.POST})
    default public RestValidationResultBean validateUnique(@PathVariable(required=false) ID id, @PathVariable(required=false) String columnName, @PathVariable(required=false) String columnValue) {
        return this.getService().validateUnique(id, columnName, columnValue);
    }

    @Log(value="\u9a8c\u8bc1\u6570\u636e\u552f\u4e00\u6027", type=LogType.VALIDATE)
    @RequestMapping(value={"/action/validate-unique"}, method={RequestMethod.POST})
    default public RestValidationResultBean validateUnique(RestJsonWrapperBean wrapper) {
        return this.getService().validateUnique(wrapper);
    }

    @Log(value="\u4e92\u6362\u5bf9\u5e94\u7684\u503c", type=LogType.UPDATE)
    @RequestMapping(value={"/action/swap/{column}/{firstId}/{secondId}", "/action/swap/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    default public void swap(@PathVariable(required=false) String column, @PathVariable ID firstId, @PathVariable ID secondId) {
        this.getService().swap(Objects.toString(column, "ORDERNO"), firstId, secondId);
    }

    @Log(value="\u67e5\u8be2\u5f53\u524d\u4e1a\u52a1\u5bf9\u5e94\u7684Mapper\u7684\u5185\u5bb9", type=LogType.SELECT)
    @RequestMapping(value={"/gikam-attrs/mybatis-mapper"}, method={RequestMethod.GET})
    default public String getGikamAttrsMybatisMapper() {
        return this.getService().getGikamAttrsMybatisMapper();
    }

    @Log(value="\u67e5\u8be2\u5f53\u524d\u4e1a\u52a1\u4e0b\u529f\u80fd", type=LogType.SELECT)
    @RequestMapping(value={"/gikam-attrs/reqentries/queries"}, method={RequestMethod.POST})
    default public Page<CoreControllerRequestEntryDTO> getGikamAttrsReqEntryPagination() {
        return this.getService().getGikamAttrsReqEntryPagination();
    }

    @Log(value="\u6253\u5370\u8be6\u60c5", type=LogType.PRINT)
    @RequestMapping(value={"/{id}/action/print"}, method={RequestMethod.GET})
    @ValidatePermissionWith(value=DefaultAvailablePermissionValidator.class, table="#this.getService().getDao().getTable()", category="normal")
    default public JSONObject print(@PathVariable ID id) {
        return this.getService().print(id);
    }
}
