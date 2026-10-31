package com.sunwayworld.framework.support.base.dao;

/** Recorded source contract. Unimplemented operations fail explicitly. */
public interface GenericDao<T extends com.sunwayworld.framework.support.domain.Persistable<ID>, ID extends java.io.Serializable> {
    public static final int STATEMENT_MAX_BATCH_BIND_VALUES = 1000;
    public static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GenericDao.class);
    public default java.lang.String getTable() { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.getTable is not implemented"); }
    public abstract java.lang.Class<T> getType();
    public abstract com.sunwayworld.framework.database.context.EntityContext getEntityContext();
    public default com.sunwayworld.framework.support.base.mapper.GenericMapper<ID> getMapper() { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.getMapper is not implemented"); }
    public abstract void insert(T p0);
    public abstract void insert(java.util.List<T> p0);
    public abstract void fastInsert(java.util.List<T> p0);
    public abstract void delete(ID p0);
    public abstract void deleteByIdList(java.util.List<ID> p0);
    public abstract void deleteBy(java.util.List<T> p0, java.lang.String... p1);
    public default void deleteBy(T p0, java.lang.String... p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.deleteBy is not implemented"); }
    public default void updateToEmptyData(java.util.List<T> p0, java.lang.String... p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.updateToEmptyData is not implemented"); }
    public default void updateToEmptyData(java.util.List<T> p0, java.util.List<java.lang.String> p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.updateToEmptyData is not implemented"); }
    public abstract void updateToEmptyData(T p0, java.util.List<java.lang.String> p1);
    public abstract void update(T p0, java.util.List<java.lang.String> p1, java.lang.String... p2);
    public default void update(T p0, java.lang.String... p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.update is not implemented"); }
    public abstract void updateByIds(T p0, java.util.List<ID> p1, java.lang.String... p2);
    public default void update(java.util.List<T> p0, java.util.List<java.lang.String> p1, java.lang.String... p2) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.update is not implemented"); }
    public abstract void update(java.util.List<T> p0, java.lang.String... p1);
    public abstract void fastUpdate(java.util.List<T> p0, java.lang.String... p1);
    public default void update(T p0, T p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.update is not implemented"); }
    public abstract void updateById(T p0, ID p1, java.lang.String... p2);
    public abstract java.util.List<T> updateIfChanged(java.util.List<T> p0, java.util.List<java.lang.String> p1);
    public default T updateIfChanged(T p0, java.util.List<java.lang.String> p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.updateIfChanged is not implemented"); }
    public default java.util.List<T> updateIfChanged(java.util.List<T> p0) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.updateIfChanged is not implemented"); }
    public default T updateIfChanged(T p0) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.updateIfChanged is not implemented"); }
    public default void updateCreatedBy(T p0) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.updateCreatedBy is not implemented"); }
    public abstract void updateCreatedBy(java.util.List<T> p0);
    public abstract T selectByIdIfPresent(ID p0);
    public default T selectById(ID p0) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectById is not implemented"); }
    public abstract T selectOneIfPresent(T p0, java.lang.String... p1);
    public default T selectOne(T p0, java.lang.String... p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectOne is not implemented"); }
    public abstract <V> V selectColumnById(ID p0, java.lang.String p1, java.lang.Class<V> p2);
    public abstract <V> java.util.List<V> selectColumnList(T p0, java.util.List<java.lang.String> p1, java.lang.String p2, java.lang.Class<V> p3, com.sunwayworld.framework.database.sql.Order... p4);
    public default <V> java.util.List<V> selectColumnList(T p0, java.lang.String p1, java.lang.Class<V> p2, com.sunwayworld.framework.database.sql.Order... p3) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectColumnList is not implemented"); }
    public default <V> V selectColumnIfPresent(T p0, java.lang.String p1, java.lang.Class<V> p2) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectColumnIfPresent is not implemented"); }
    public default <V> V selectColumn(T p0, java.lang.String p1, java.lang.Class<V> p2) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectColumn is not implemented"); }
    public default java.util.List<ID> selectIdList(T p0, com.sunwayworld.framework.database.sql.Order... p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectIdList is not implemented"); }
    public default java.util.List<ID> selectIdList(T p0, java.util.List<java.lang.String> p1, com.sunwayworld.framework.database.sql.Order... p2) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectIdList is not implemented"); }
    public default ID selectIdIfPresent(T p0) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectIdIfPresent is not implemented"); }
    public default ID selectId(T p0) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectId is not implemented"); }
    public abstract java.util.List<T> selectAll(java.util.List<com.sunwayworld.framework.database.sql.Order> p0, java.lang.String... p1);
    public default java.util.List<T> selectAll(com.sunwayworld.framework.database.sql.Order... p0) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectAll is not implemented"); }
    public default java.util.List<T> selectAll(java.lang.String... p0) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectAll is not implemented"); }
    public default java.util.List<T> selectAll() { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectAll is not implemented"); }
    public abstract java.util.List<T> selectListByIds(java.util.List<ID> p0, java.util.List<java.lang.String> p1, com.sunwayworld.framework.database.sql.Order... p2);
    public default java.util.List<T> selectListByIds(java.util.List<ID> p0, com.sunwayworld.framework.database.sql.Order... p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectListByIds is not implemented"); }
    public abstract <V> java.util.List<V> selectColumnsByIds(java.util.List<ID> p0, java.lang.String p1, java.lang.Class<V> p2, com.sunwayworld.framework.database.sql.Order... p3);
    public abstract java.util.List<T> selectList(T p0, java.util.List<java.lang.String> p1, java.util.List<java.lang.String> p2, com.sunwayworld.framework.database.sql.Order... p3);
    public default java.util.List<T> selectList(T p0, java.util.List<java.lang.String> p1, com.sunwayworld.framework.database.sql.Order... p2) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectList is not implemented"); }
    public default java.util.List<T> selectList(T p0, com.sunwayworld.framework.database.sql.Order... p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectList is not implemented"); }
    public abstract java.util.List<T> selectList(java.util.List<T> p0, java.util.List<java.lang.String> p1, java.util.List<java.lang.String> p2, com.sunwayworld.framework.database.sql.Order... p3);
    public abstract <V> java.util.List<V> selectList(java.util.List<T> p0, java.util.List<java.lang.String> p1, java.lang.String p2, java.lang.Class<V> p3, com.sunwayworld.framework.database.sql.Order... p4);
    public default <V> java.util.List<V> selectList(java.util.List<T> p0, java.lang.String p1, java.lang.Class<V> p2, com.sunwayworld.framework.database.sql.Order... p3) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectList is not implemented"); }
    public abstract java.util.List<T> selectListByOneColumnValues(java.lang.String p0, java.util.List<T> p1, java.util.List<java.lang.String> p2, com.sunwayworld.framework.database.sql.Order... p3);
    public default <V> java.util.List<T> selectListByOneColumnValues(java.util.List<V> p0, java.lang.String p1, java.util.List<java.lang.String> p2, com.sunwayworld.framework.database.sql.Order... p3) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectListByOneColumnValues is not implemented"); }
    public default <V> java.util.List<T> selectListByOneColumnValues(java.util.List<V> p0, java.lang.String p1, com.sunwayworld.framework.database.sql.Order... p2) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectListByOneColumnValues is not implemented"); }
    public default <V> java.util.List<T> selectListByOneColumnValue(V p0, java.lang.String p1, java.util.List<java.lang.String> p2, com.sunwayworld.framework.database.sql.Order... p3) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectListByOneColumnValue is not implemented"); }
    public default <V> java.util.List<T> selectListByOneColumnValue(V p0, java.lang.String p1, com.sunwayworld.framework.database.sql.Order... p2) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectListByOneColumnValue is not implemented"); }
    public default java.util.List<ID> selectIdList(java.util.List<T> p0, java.util.List<java.lang.String> p1, com.sunwayworld.framework.database.sql.Order... p2) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectIdList is not implemented"); }
    public default java.util.List<ID> selectIdList(java.util.List<T> p0, com.sunwayworld.framework.database.sql.Order... p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectIdList is not implemented"); }
    public abstract T selectFirstIfPresent(T p0, java.util.List<java.lang.String> p1, java.util.List<java.lang.String> p2, com.sunwayworld.framework.database.sql.Order... p3);
    public default T selectFirst(T p0, java.util.List<java.lang.String> p1, java.util.List<java.lang.String> p2, com.sunwayworld.framework.database.sql.Order... p3) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectFirst is not implemented"); }
    public default T selectFirstIfPresent(T p0, java.util.List<java.lang.String> p1, com.sunwayworld.framework.database.sql.Order... p2) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectFirstIfPresent is not implemented"); }
    public default T selectFirst(T p0, java.util.List<java.lang.String> p1, com.sunwayworld.framework.database.sql.Order... p2) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectFirst is not implemented"); }
    public default T selectFirstIfPresent(T p0, com.sunwayworld.framework.database.sql.Order... p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectFirstIfPresent is not implemented"); }
    public default T selectFirst(T p0, com.sunwayworld.framework.database.sql.Order... p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectFirst is not implemented"); }
    public abstract java.util.List<T> selectUnionList(T p0, java.util.List<java.lang.String> p1, java.util.List<java.lang.String> p2, com.sunwayworld.framework.database.sql.Order... p3);
    public default java.util.List<T> selectUnionList(T p0, java.util.List<java.lang.String> p1, com.sunwayworld.framework.database.sql.Order... p2) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectUnionList is not implemented"); }
    public default java.util.List<T> selectUnionList(T p0, com.sunwayworld.framework.database.sql.Order... p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectUnionList is not implemented"); }
    public default T selectOneUnionIfPresent(T p0, java.util.List<java.lang.String> p1, java.util.List<java.lang.String> p2, com.sunwayworld.framework.database.sql.Order... p3) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectOneUnionIfPresent is not implemented"); }
    public default T selectOneUnion(T p0, java.util.List<java.lang.String> p1, java.util.List<java.lang.String> p2, com.sunwayworld.framework.database.sql.Order... p3) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectOneUnion is not implemented"); }
    public default T selectOneUnionIfPresent(T p0, java.util.List<java.lang.String> p1, com.sunwayworld.framework.database.sql.Order... p2) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectOneUnionIfPresent is not implemented"); }
    public default T selectOneUnion(T p0, java.util.List<java.lang.String> p1, com.sunwayworld.framework.database.sql.Order... p2) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectOneUnion is not implemented"); }
    public default T selectOneUnionIfPresent(T p0, com.sunwayworld.framework.database.sql.Order... p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectOneUnionIfPresent is not implemented"); }
    public default T selectOneUnion(T p0, com.sunwayworld.framework.database.sql.Order... p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectOneUnion is not implemented"); }
    public abstract int countBy(T p0, java.lang.String... p1);
    public default int count() { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.count is not implemented"); }
    public abstract int countBy(java.util.List<T> p0, java.lang.String... p1);
    public abstract <V> java.util.Map<V, java.lang.Long> groupCountByValueList(java.util.List<V> p0, java.lang.String p1);
    public abstract com.sunwayworld.framework.data.page.Page<T> selectPaginationByMybatis(java.lang.String p0, com.sunwayworld.framework.data.page.Pageable p1);
    public default java.util.Map<java.lang.String, java.lang.Object> selectDetail(ID p0) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectDetail is not implemented"); }
    public default java.util.List<java.util.Map<java.lang.String, java.lang.Object>> selectByCondition(com.sunwayworld.framework.mybatis.mapper.MapperParameter p0) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.selectByCondition is not implemented"); }
    public default void cacheEvict(T p0, T p1) { throw new UnsupportedOperationException("Owned read-only boundary: GenericDao.cacheEvict is not implemented"); }
}
