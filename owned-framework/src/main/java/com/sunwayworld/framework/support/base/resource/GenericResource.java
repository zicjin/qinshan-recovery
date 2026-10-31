package com.sunwayworld.framework.support.base.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
/** Recorded source contract. Unimplemented operations fail explicitly. */
public interface GenericResource<S extends com.sunwayworld.framework.support.base.service.GenericService<T, ID>, T extends com.sunwayworld.framework.support.domain.Persistable<ID>, ID extends java.io.Serializable> {
    public abstract S getService();
    @PostMapping("/queries")
    public default com.sunwayworld.framework.data.page.Page<T> selectPagination(com.sunwayworld.framework.restful.data.RestJsonWrapperBean p0) { return getService().selectPagination(p0); }
    @PostMapping("/queries/raw")
    public default com.sunwayworld.framework.data.page.Page<T> selectRawPagination(com.sunwayworld.framework.restful.data.RestJsonWrapperBean p0) { return getService().selectRawPagination(p0); }
    @GetMapping("/{id}")
    public default T selectDetail(@PathVariable("id") ID p0) { return getService().selectDetail(p0); }
    public default java.lang.String selectColumnById(ID p0, java.lang.String p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericResource.selectColumnById is not implemented"); }
    public default com.sunwayworld.framework.support.base.bean.CoreServiceStatusDTO selectStatus(ID p0, java.lang.String p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericResource.selectStatus is not implemented"); }
    @DeleteMapping("")
    public default void delete(com.sunwayworld.framework.restful.data.RestJsonWrapperBean p0) { throw new UnsupportedOperationException("Owned read-only boundary: GenericResource.delete is not implemented"); }
    @PostMapping("")
    public default ID insert(com.sunwayworld.framework.restful.data.RestJsonWrapperBean p0) { throw new UnsupportedOperationException("Owned read-only boundary: GenericResource.insert is not implemented"); }
    @PutMapping("/instant")
    public default void instantSave(com.sunwayworld.framework.restful.data.RestJsonWrapperBean p0) { throw new UnsupportedOperationException("Owned read-only boundary: GenericResource.instantSave is not implemented"); }
    @PutMapping("/{id}")
    public default void save(@PathVariable("id") ID p0, com.sunwayworld.framework.restful.data.RestJsonWrapperBean p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericResource.save is not implemented"); }
    public default com.sunwayworld.framework.data.page.Page<T> selectSearchablePagination(com.sunwayworld.framework.restful.data.RestJsonWrapperBean p0) { throw new UnsupportedOperationException("Owned read-only boundary: GenericResource.selectSearchablePagination is not implemented"); }
    public default com.sunwayworld.framework.data.page.Page<T> selectByRole(java.lang.Long p0, com.sunwayworld.framework.restful.data.RestJsonWrapperBean p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericResource.selectByRole is not implemented"); }
    public default com.sunwayworld.framework.restful.data.RestValidationResultBean validate(ID p0, com.sunwayworld.framework.restful.data.RestJsonWrapperBean p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericResource.validate is not implemented"); }
    public default com.sunwayworld.framework.restful.data.RestValidationResultBean validateUnique(ID p0, java.lang.String p1, java.lang.String p2) { throw new UnsupportedOperationException("Owned read-only boundary: GenericResource.validateUnique is not implemented"); }
    public default com.sunwayworld.framework.restful.data.RestValidationResultBean validateUnique(com.sunwayworld.framework.restful.data.RestJsonWrapperBean p0) { throw new UnsupportedOperationException("Owned read-only boundary: GenericResource.validateUnique is not implemented"); }
    public default void swap(java.lang.String p0, ID p1, ID p2) { throw new UnsupportedOperationException("Owned read-only boundary: GenericResource.swap is not implemented"); }
    public default java.lang.String getGikamAttrsMybatisMapper() { throw new UnsupportedOperationException("Owned read-only boundary: GenericResource.getGikamAttrsMybatisMapper is not implemented"); }
    public default com.sunwayworld.framework.data.page.Page<com.sunwayworld.framework.support.base.bean.CoreControllerRequestEntryDTO> getGikamAttrsReqEntryPagination() { throw new UnsupportedOperationException("Owned read-only boundary: GenericResource.getGikamAttrsReqEntryPagination is not implemented"); }
    public default com.alibaba.fastjson.JSONObject print(ID p0) { throw new UnsupportedOperationException("Owned read-only boundary: GenericResource.print is not implemented"); }
}
