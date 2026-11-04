package com.sunwayworld.module.design.studio.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.database.dialect.ColumnMetadata;
import com.sunwayworld.framework.database.dialect.Dialect;
import com.sunwayworld.framework.database.dialect.ColumnMetadata.DataType;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.design.studio.bean.CoreStudioBlockBean;
import com.sunwayworld.module.design.studio.bean.CoreStudioBlockConfigDTO;
import com.sunwayworld.module.design.studio.bean.CoreStudioBlockFormDTO;
import com.sunwayworld.module.design.studio.bean.CoreStudioBlockGridDTO;
import com.sunwayworld.module.design.studio.bean.CoreStudioBlockLineBean;
import com.sunwayworld.module.design.studio.dao.CoreStudioBlockDao;
import com.sunwayworld.module.design.studio.service.CoreStudioBlockLineService;
import com.sunwayworld.module.design.studio.service.CoreStudioBlockService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreStudioBlockServiceImpl implements CoreStudioBlockService {
   @Autowired
   private CoreStudioBlockDao studioBlockDao;
   @Lazy
   @Autowired
   private CoreStudioBlockLineService studioBlockLineService;
   @Autowired
   private GlobalMapper globalMapper;
   @Autowired
   private Dialect dialect;

   public CoreStudioBlockDao getDao() {
      return this.studioBlockDao;
   }

   @Override
   public Page<CoreStudioBlockBean> selectPaginationByPageUrl(RestJsonWrapperBean wrapper) {
      String url = Objects.requireNonNull(wrapper.getParamValue("url"));
      CoreStudioBlockBean filter = new CoreStudioBlockBean();
      filter.setUrl(url);
      return new Pagination<>(this.getDao().selectList(filter, new Order[]{Order.asc("ID")}));
   }

   @Override
   public Page<CoreStudioBlockLineBean> selectLinePaginationByMasterId(Long id, RestJsonWrapperBean wrapper) {
      return this.studioBlockLineService.selectPaginationByFilter(SearchFilter.instance().match("MASTERID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @Override
   public Long save(RestJsonWrapperBean wrapper) {
      CoreStudioBlockBean block = wrapper.parse(CoreStudioBlockBean.class, 0).get(0);
      CoreStudioBlockBean blockFilter = new CoreStudioBlockBean();
      blockFilter.setUrl(block.getUrl());
      blockFilter.setBlockId(block.getBlockId());
      CoreStudioBlockBean selectedBlock = this.getDao().selectFirstIfPresent(blockFilter, new Order[0]);
      if (selectedBlock == null) {
         block.setId(ApplicationContextHelper.getNextIdentity());
         this.getDao().insert(block);
      } else {
         block.setId(selectedBlock.getId());
         CoreStudioBlockBean proxy = BeanUtils.getPropertyListenerProxy(selectedBlock);
         BeanUtils.copyProperties(block, proxy);
         this.getDao().update(proxy, new String[0]);
      }

      List<CoreStudioBlockLineBean> blockLineList = wrapper.parse(CoreStudioBlockLineBean.class, 1);
      List<ColumnContext> columnContextList = DatabaseManager.getTableColumnContextList(block.getTableName());
      List<CoreStudioBlockLineBean> selectedBlockLineList = this.studioBlockLineService
         .selectListByFilter(SearchFilter.instance().match("MASTERID", block.getId()).filter(MatchPattern.EQ), new Order[0]);
      List<CoreStudioBlockLineBean> insertLineList = new ArrayList<>();
      List<CoreStudioBlockLineBean> updateOrderNoLineList = new ArrayList<>();
      List<CoreStudioBlockLineBean> updateJsonLineList = new ArrayList<>();
      List<CoreStudioBlockLineBean> deleteLineList = new ArrayList<>();

      for (CoreStudioBlockLineBean blockLine : blockLineList) {
         CoreStudioBlockLineBean selectedBlockLine = selectedBlockLineList.stream()
            .filter(l -> StringUtils.equalsIgnoreCase(l.getColumnName(), blockLine.getColumnName()))
            .findAny()
            .orElse(null);
         if (selectedBlockLine == null) {
            insertLineList.add(blockLine);
         } else {
            blockLine.setId(selectedBlockLine.getId());
            if (!selectedBlockLine.getOrderNo().equals(blockLine.getOrderNo())) {
               updateOrderNoLineList.add(blockLine);
            }

            if (!selectedBlockLine.getJson().equals(blockLine.getJson())) {
               updateJsonLineList.add(blockLine);
            }
         }
      }

      for (CoreStudioBlockLineBean selectedBlockLine : selectedBlockLineList) {
         if (blockLineList.stream().noneMatch(l -> StringUtils.equalsIgnoreCase(l.getColumnName(), selectedBlockLine.getColumnName()))) {
            deleteLineList.add(selectedBlockLine);
         }
      }

      if (!updateOrderNoLineList.isEmpty()) {
         this.studioBlockLineService.getDao().update(updateOrderNoLineList, "ORDERNO");
      }

      if (!updateJsonLineList.isEmpty()) {
         this.studioBlockLineService.getDao().update(updateJsonLineList, "JSON");
      }

      if (!insertLineList.isEmpty()) {
         insertLineList.forEach(
            l -> {
               l.setId(ApplicationContextHelper.getNextIdentity());
               l.setMasterId(block.getId());
               DataType dataType = this.getDataType(l.getType());
               if (dataType != null
                  && StringUtils.startsWithIgnoreCase(l.getColumnName(), "EXT_")
                  && columnContextList.stream().noneMatch(c -> StringUtils.equalsIgnoreCase(c.getColumnName(), l.getColumnName()))) {
                  ColumnMetadata metadata = ColumnMetadata.of(l.getColumnName(), dataType);
                  metadata.setComments(JSONObject.parseObject(l.getJson()).getString("title"));
                  this.dialect.getAddColumnSqlList(block.getTableName(), metadata).forEach(s -> this.globalMapper.update(s));
               }
            }
         );
         this.studioBlockLineService.getDao().insert(insertLineList);
      }

      if (!deleteLineList.isEmpty()) {
         deleteLineList.stream()
            .filter(l -> columnContextList.stream().anyMatch(c -> StringUtils.equalsIgnoreCase(c.getColumnName(), l.getColumnName())))
            .forEach(l -> this.globalMapper.update(this.dialect.getDropColumnSql(block.getTableName(), l.getColumnName())));
         this.studioBlockLineService.getDao().deleteBy(deleteLineList);
      }

      DatabaseManager.evictCache(block.getTableName());
      return block.getId();
   }

   @Override
   public CoreStudioBlockConfigDTO getStudioBlockConfigByPageUrl(String url) {
      CoreStudioBlockConfigDTO config = new CoreStudioBlockConfigDTO();
      CoreStudioBlockBean blockFilter = new CoreStudioBlockBean();
      blockFilter.setUrl(url);
      List<CoreStudioBlockBean> studioBlockList = this.getDao().selectList(blockFilter, new Order[0]);
      if (studioBlockList.isEmpty()) {
         return config;
      } else {
         List<CoreStudioBlockLineBean> blockLineList = this.studioBlockLineService
            .getDao()
            .selectListByOneColumnValues(
               studioBlockList.stream().map(CoreStudioBlockBean::getId).collect(Collectors.toList()), "MASTERID", Order.asc("ORDERNO")
            );
         Map<String, CoreStudioBlockFormDTO> form = new HashMap<>();
         Map<String, CoreStudioBlockGridDTO> grid = new HashMap<>();

         for (CoreStudioBlockBean studioBlock : studioBlockList) {
            if ("grid".equals(studioBlock.getCategory())) {
               CoreStudioBlockGridDTO blockGrid = new CoreStudioBlockGridDTO();
               blockGrid.setVisible("1".equals(studioBlock.getVisible()));
               blockGrid.setColumns(
                  blockLineList.stream()
                     .filter(l -> l.getMasterId().equals(studioBlock.getId()) && !StringUtils.isBlank(l.getJson()))
                     .map(l -> JSONObject.parseObject(l.getJson()))
                     .toArray(JSONObject[]::new)
               );
               grid.put(studioBlock.getBlockId(), blockGrid);
            } else {
               if (!"form".equals(studioBlock.getCategory())) {
                  throw new UnexpectedException(ObjectUtils.toShortString(studioBlock));
               }

               CoreStudioBlockFormDTO blockForm = new CoreStudioBlockFormDTO();
               blockForm.setFormColumnQty(studioBlock.getFormColumnQty());
               blockForm.setVisible("1".equals(studioBlock.getVisible()));
               blockForm.setColumns(
                  blockLineList.stream()
                     .filter(l -> l.getMasterId().equals(studioBlock.getId()) && !StringUtils.isBlank(l.getJson()))
                     .map(l -> JSONObject.parseObject(l.getJson()))
                     .toArray(JSONObject[]::new)
               );
               form.put(studioBlock.getBlockId(), blockForm);
            }
         }

         config.setForm(form);
         config.setGrid(grid);
         return config;
      }
   }

   private DataType getDataType(String blockLineType) {
      if ("string".equals(blockLineType)) {
         return DataType.STRING;
      } else if ("long".equals(blockLineType)) {
         return DataType.INTEGER;
      } else if ("double".equals(blockLineType)) {
         return DataType.DOUBLE;
      } else if ("localdate".equals(blockLineType)) {
         return DataType.DATE;
      } else {
         return "localdatetime".equals(blockLineType) ? DataType.DATETIME : null;
      }
   }
}
