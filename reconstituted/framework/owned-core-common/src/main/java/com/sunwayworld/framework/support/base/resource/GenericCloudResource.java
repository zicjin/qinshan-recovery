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
import com.sunwayworld.framework.support.domain.Persistable;
import java.io.Serializable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface GenericCloudResource<T extends Persistable<ID>, ID extends Serializable> {
    @RequestMapping(value={"/queries"}, method={RequestMethod.POST})
    public Pagination<T> selectPagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/queries/raw"}, method={RequestMethod.POST})
    public Pagination<T> selectRawPagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}"}, method={RequestMethod.GET})
    public T selectDetail(@PathVariable ID var1);

    @RequestMapping(value={"/{id}/columns/{column}"}, method={RequestMethod.GET})
    public String selectColumnById(@PathVariable ID var1, @PathVariable String var2);

    @RequestMapping(value={"/{id}/info/status"}, method={RequestMethod.GET})
    public CoreServiceStatusDTO selectStatus(@PathVariable(required=false) ID var1, @RequestParam(required=false) String var2);

    @RequestMapping(value={"/info/status"}, method={RequestMethod.GET})
    public CoreServiceStatusDTO selectStatus(@RequestParam(required=false) String var1);

    @RequestMapping(method={RequestMethod.DELETE})
    public void delete(RestJsonWrapperBean var1);

    @RequestMapping(method={RequestMethod.POST})
    public ID insert(RestJsonWrapperBean var1);

    @RequestMapping(value={"/instant"}, method={RequestMethod.PUT})
    @ResponseStatus(value=HttpStatus.OK)
    public void instantSave(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}"}, method={RequestMethod.PUT})
    @ResponseStatus(value=HttpStatus.OK)
    public void save(@PathVariable ID var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/queries/searchable"}, method={RequestMethod.POST})
    public Pagination<T> selectSearchablePagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/queries/roles/{roleId}"}, method={RequestMethod.POST})
    public Pagination<T> selectByRole(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/action/validate"}, method={RequestMethod.POST})
    public RestValidationResultBean validate(@PathVariable ID var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/action/validate-unique"}, method={RequestMethod.POST})
    public RestValidationResultBean validateUnique(@PathVariable(required=false) ID var1);

    @RequestMapping(value={"/{id}/action/validate-unique/{columnName}/{columnValue}"}, method={RequestMethod.POST})
    public RestValidationResultBean validateUnique(@PathVariable(required=false) ID var1, @PathVariable(required=false) String var2, @PathVariable(required=false) String var3);

    @RequestMapping(value={"/action/validate-unique/{columnName}/{columnValue}"}, method={RequestMethod.POST})
    public RestValidationResultBean validateUnique(@PathVariable(required=false) String var1, @PathVariable(required=false) String var2);

    @Log(value="\u9a8c\u8bc1\u6570\u636e\u552f\u4e00\u6027", type=LogType.VALIDATE)
    @RequestMapping(value={"/action/validate-unique"}, method={RequestMethod.POST})
    public RestValidationResultBean validateUnique(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/swap/{column}/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    public void swap(@PathVariable(required=false) String var1, @PathVariable ID var2, @PathVariable ID var3);

    @RequestMapping(value={"/action/swap/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    public void swap(@PathVariable ID var1, @PathVariable ID var2);

    @RequestMapping(value={"/gikam-attrs/mybatis-mapper"}, method={RequestMethod.GET})
    public String getGikamAttrsMybatisMapper();

    @RequestMapping(value={"/gikam-attrs/reqentries/queries"}, method={RequestMethod.POST})
    public Pagination<CoreControllerRequestEntryDTO> getGikamAttrsReqEntryPagination();

    @RequestMapping(value={"/{id}/action/print"}, method={RequestMethod.GET})
    public JSONObject print(@PathVariable ID var1);

    @RequestMapping(value={"/{id}/info/bpmn-proc-id"}, method={RequestMethod.GET})
    public Long selectProcId(@PathVariable ID var1);
}
