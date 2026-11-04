package com.sunwayworld.framework.support.query.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.base.service.GenericDownloadableService;
import com.sunwayworld.framework.support.domain.BaseData;
import com.sunwayworld.framework.support.query.mapper.GenericQueryMapper;
import com.sunwayworld.framework.utils.CollectionUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface GenericQueryService<T extends BaseData> extends GenericDownloadableService {
   Class<T> getDataType();

   GenericQueryMapper getQueryMapper();

   default Page<T> selectQueryPagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      parameter.setRawQueries();
      parameter.setOrgAuthority();
      if (!wrapper.getAuthorityList().isEmpty()) {
         wrapper.getAuthorityList().forEach(a -> parameter.setAuthorityParameter(a));
      }

      Page<Map<String, Object>> page = MybatisPageHelper.get(rowBounds, () -> this.getQueryMapper().selectByCondition(parameter));
      if (page.getRows().isEmpty()) {
         return new Pagination<>(page, CollectionUtils.emptyList());
      } else {
         List<T> itemList = page.getRows()
            .stream()
            .map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, this.getDataType()))
            .collect(Collectors.toList());
         return new Pagination<>(page, itemList);
      }
   }
}
