package com.sunwayworld.module.design.studio.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.database.dialect.ColumnMetadata;
import com.sunwayworld.framework.database.dialect.Dialect;
import com.sunwayworld.framework.database.dialect.ColumnMetadata.DataType;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.design.studio.bean.CoreStudioFieldDTO;
import com.sunwayworld.module.design.studio.bean.CoreStudioGridDTO;
import com.sunwayworld.module.design.studio.bean.CoreStudioPageBean;
import com.sunwayworld.module.design.studio.bean.CoreStudioPageDraftBean;
import com.sunwayworld.module.design.studio.bean.CoreStudioTableFieldBean;
import com.sunwayworld.module.design.studio.dao.CoreStudioPageDao;
import com.sunwayworld.module.design.studio.service.CoreStudioPageDraftService;
import com.sunwayworld.module.design.studio.service.CoreStudioPageService;
import com.sunwayworld.module.design.studio.service.CoreStudioTableFieldService;
import com.sunwayworld.module.design.studio.support.CoreStudioPageHelper;
import com.sunwayworld.module.sys.page.service.CorePageGridFieldConfigService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreStudioPageServiceImpl implements CoreStudioPageService {
   @Autowired
   private CoreStudioPageDao pageDao;
   @Autowired
   private CoreStudioPageDraftService pageDraftService;
   @Autowired
   private CoreStudioTableFieldService tableFieldService;
   @Autowired
   private GlobalMapper globalMapper;
   @Autowired
   private Dialect dialect;
   @Autowired
   private CorePageGridFieldConfigService pageGridFieldConfigService;

   public CoreStudioPageDao getDao() {
      return this.pageDao;
   }

   @Override
   public CoreStudioPageBean selectLatestByUrl(String url) {
      return this.selectFirstByFilter(SearchFilter.instance().match("URL", url).filter(MatchPattern.EQ), new Order[]{Order.desc("ID")});
   }

   @Override
   public CoreStudioPageDraftBean selectDraftByUrl(String url) {
      return this.pageDraftService.selectFirstByFilter(SearchFilter.instance().match("URL", url).filter(MatchPattern.EQ), new Order[]{Order.desc("ID")});
   }

   @Transactional
   @Override
   public Long saveDraft(RestJsonWrapperBean wrapper) {
      CoreStudioPageDraftBean pageDraft = wrapper.parseUnique(CoreStudioPageDraftBean.class);
      if (pageDraft.getId() == null) {
         GikamConcurrentLocker.blockRun("T_CORE_STUDIO_PAGE_DRAFT:SAVE:" + pageDraft.getUrl(), () -> {
            CoreStudioPageDraftBean selectedPageDraft = this.selectDraftByUrl(pageDraft.getUrl());
            if (selectedPageDraft == null) {
               pageDraft.setId(ApplicationContextHelper.getNextIdentity());
               this.pageDraftService.getDao().insert(pageDraft);
            } else {
               pageDraft.setId(selectedPageDraft.getId());
               this.pageDraftService.getDao().update(pageDraft, "JSON");
            }
         });
      } else {
         this.pageDraftService.getDao().update(pageDraft, "JSON");
      }

      return pageDraft.getId();
   }

   @Transactional
   @Override
   public void submitDraft(Long draftId) {
      CoreStudioPageDraftBean pageDraft = this.pageDraftService.selectById(draftId);
      CoreStudioPageBean insertPage = new CoreStudioPageBean();
      BeanUtils.copyProperties(pageDraft, insertPage);
      insertPage.setId(ApplicationContextHelper.getNextIdentity());
      CoreStudioPageBean lastPage = this.selectFirstByFilter(
         SearchFilter.instance().match("URL", pageDraft.getUrl()).filter(MatchPattern.EQ), new Order[]{Order.desc("VERSION")}
      );
      if (lastPage != null) {
         insertPage.setVersion(lastPage.getVersion() + 1L);
      }

      this.getDao().insert(insertPage);
      Set<String> evictCacheTableList = new HashSet<>();
      List<CoreStudioTableFieldBean> tableFieldList = new ArrayList<>();

      for (CoreStudioFieldDTO field : CoreStudioPageHelper.getFieldList(insertPage.getJson())) {
         if (field.isForm()) {
            CoreStudioTableFieldBean tableField = new CoreStudioTableFieldBean();
            tableField.setTableName(field.getTable());
            tableField.setField(field.getField());
            tableField.setConfig(field.getJson());
            tableFieldList.add(tableField);
         }

         if (!field.isDefault() && field.isAppendField()) {
            List<ColumnContext> columnContextList = DatabaseManager.getTableColumnContextList(field.getTable());
            ColumnMetadata metadata = this.parse(field);
            ColumnContext context = columnContextList.stream().filter(c -> c.getColumnName().equalsIgnoreCase(field.getField())).findAny().orElse(null);
            if (context == null) {
               evictCacheTableList.add(field.getTable());
               this.dialect.getAddColumnSqlList(field.getTable(), metadata).forEach(s -> this.globalMapper.update(s));
            } else if (DataType.STRING.equals(metadata.getDataType()) && metadata.getDataLength() > context.getPrecision()) {
               evictCacheTableList.add(field.getTable());
               this.globalMapper.update(this.dialect.getUpdateVarcharLengthSql(field.getTable(), field.getField(), metadata.getDataLength()));
            }
         }
      }

      this.pageDraftService.delete(pageDraft.getId());
      if (!tableFieldList.isEmpty()) {
         this.tableFieldService.reset(tableFieldList);
      }

      TransactionUtils.runAfterCommit(() -> evictCacheTableList.forEach(t -> DatabaseManager.evictCache(t)));
      List<CoreStudioGridDTO> lastPageGridList = null == lastPage ? CollectionUtils.emptyList() : CoreStudioPageHelper.getPageGridList(lastPage.getJson());
      List<CoreStudioGridDTO> pageGridList = CoreStudioPageHelper.getPageGridList(insertPage.getJson());
      TransactionUtils.runAfterCommit(() -> this.pageGridFieldConfigService.resetPageGridConfig(lastPageGridList, pageGridList));
   }

   @Cacheable(
      value = {"T_CORE_STUDIO_PAGE"},
      key = "#pageUrl",
      unless = "#result == null"
   )
   @Override
   public String selectJsonByPageUrl(String pageUrl) {
      CoreStudioPageBean page = this.selectFirstByFilter(SearchFilter.instance().match("URL", pageUrl).filter(MatchPattern.EQ), new Order[]{Order.desc("ID")});
      return page == null ? "" : page.getJson();
   }

   private ColumnMetadata parse(CoreStudioFieldDTO field) {
      ColumnMetadata metadata = ColumnMetadata.of(field.getField(), field.getFieldConfig().getType());
      metadata.setComments(field.getTitle());
      if (DataType.STRING.equals(metadata.getDataType())) {
         metadata.setDataLength(field.getFieldConfig().getLength());
      } else if (DataType.DOUBLE.equals(metadata.getDataType()) || DataType.FLOAT.equals(metadata.getDataType())) {
         metadata.setDataLength(field.getFieldConfig().getPrecision());
         metadata.setDataScale(field.getFieldConfig().getScale());
      }

      return metadata;
   }
}
