package com.sunwayworld.framework.support.base.mapper;

/** Recorded source contract. Unimplemented operations fail explicitly. */
public interface GenericMapper<ID extends java.io.Serializable> {
    public abstract java.util.List<java.util.Map<java.lang.String, java.lang.Object>> selectByCondition(com.sunwayworld.framework.mybatis.mapper.MapperParameter p0);
}
