package com.sunwayworld.cloud.module.lcdp.resourcedocument.support;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpModulePageCompBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceHistoryBean;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpModulePageCompService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceHistoryService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceService;
import com.sunwayworld.cloud.module.lcdp.resource.support.LcdpScriptUtils;
import com.sunwayworld.cloud.module.lcdp.resourcedocument.bean.LcdpResourceDocumentBean;
import com.sunwayworld.cloud.module.lcdp.resourcelock.bean.LcdpResourceLockBean;
import com.sunwayworld.cloud.module.lcdp.resourcelock.service.LcdpResourceLockService;
import com.sunwayworld.cloud.module.lcdp.support.constant.LcdpConstant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneSearchManager {
   private static final Logger logger = LogManager.getLogger(LuceneSearchManager.class);
   private static LcdpResourceLockService lcdpResourceLockService = ApplicationContextHelper.getBean(LcdpResourceLockService.class);
   private static LcdpResourceService lcdpResourceService = ApplicationContextHelper.getBean(LcdpResourceService.class);
   private static LcdpResourceHistoryService lcdpResourceHistoryService = ApplicationContextHelper.getBean(LcdpResourceHistoryService.class);
   private static LcdpModulePageCompService lcdpModulePageCompService = ApplicationContextHelper.getBean(LcdpModulePageCompService.class);
   public static final int RESOURCE_DOCUMENT_DEFAULT_COUNT = 2000;
   private static String CURRENT_ENVIRONMENT_DATABASE = LcdpConstant.PROFILE_DB_LIST
      .stream()
      .filter(profile -> ApplicationContextHelper.isProfileActivated(profile))
      .findFirst()
      .get();

   public static Page<LcdpResourceDocumentBean> searchPagination(RestJsonWrapperBean wrapper) {
      List<LcdpResourceDocumentBean> returnList = searchList(wrapper);
      return new Pagination<>(returnList);
   }

   public static List<LcdpResourceDocumentBean> searchList(RestJsonWrapperBean wrapper) {
      String searchKey = wrapper.getParamValue("searchKey");
      if (StringUtils.isEmpty(searchKey)) {
         return CollectionUtils.emptyList();
      } else {
         String resourceCategory = wrapper.getParamValue("resourceCategory");
         IndexReader reader = null;

         try {
            Directory dir = FSDirectory.open(Paths.get(LuceneIndexManager.getIndexPath()));
            reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            List<LcdpResourceDocumentBean> resultList = new ArrayList<>();
            List<LcdpResourceDocumentBean> resourceDocList = selectResourceResult(searchKey, resourceCategory, searcher, "1");
            List<String> resourceIdList = resourceDocList.stream().map(ex -> LuceneIndexManager.getCompResourceId(ex.getId())).collect(Collectors.toList());
            if (!resourceIdList.isEmpty()) {
               SearchFilter filter = SearchFilter.instance().match("RESOURCEID", resourceIdList).filter(MatchPattern.OR);
               List<LcdpResourceLockBean> lockList = lcdpResourceLockService.selectListByFilter(filter, new Order[0]);
               List<LcdpResourceLockBean> resourceLockList = lockList.stream()
                  .filter(f -> LocalContextHelper.getLoginUserId().equals(f.getLockUserId()))
                  .collect(Collectors.toList());
               List<Long> idList = resourceIdList.stream().map(ex -> Long.valueOf(ex)).collect(Collectors.toList());
               List<LcdpResourceBean> resourceList = lcdpResourceService.selectListByIds(idList);

               for (LcdpResourceDocumentBean doc : resourceDocList) {
                  String resourceId = LuceneIndexManager.getCompResourceId(doc.getId());
                  LcdpResourceBean resource = resourceList.stream()
                     .filter(f -> ObjectUtils.equals(Long.valueOf(resourceId), f.getId()))
                     .findFirst()
                     .orElse(null);
                  if (resource != null && resource.getEffectVersion() != null) {
                     LcdpResourceLockBean lock = resourceLockList.stream()
                        .filter(f -> ObjectUtils.equals(resourceId, f.getResourceId()))
                        .findFirst()
                        .orElse(null);
                     if (lock == null && ObjectUtils.equals(resource.getEffectVersion(), doc.getVersion())) {
                        resultList.add(doc);
                     }
                  }
               }
            }

            List<LcdpResourceDocumentBean> historyDocList = selectResourceResult(searchKey, resourceCategory, searcher, "0");
            List<Long> historyIdList = historyDocList.stream()
               .map(ex -> Long.parseLong(LuceneIndexManager.getCompResourceId(ex.getId())))
               .collect(Collectors.toList());
            if (!historyIdList.isEmpty()) {
               SearchFilter filter = SearchFilter.instance().match("ID", historyIdList).filter(MatchPattern.OR);
               List<LcdpResourceHistoryBean> historyList = lcdpResourceHistoryService.selectListByFilter(filter, new Order[0]);
               List<LcdpResourceHistoryBean> historyResourceList = historyList.stream()
                  .filter(f -> "no".equals(f.getSubmitFlag()))
                  .collect(Collectors.toList());

               for (LcdpResourceDocumentBean docx : historyDocList) {
                  Long resourceId = Long.parseLong(LuceneIndexManager.getCompResourceId(docx.getId()));
                  LcdpResourceHistoryBean historyResource = historyResourceList.stream()
                     .filter(f -> ObjectUtils.equals(resourceId, f.getId()))
                     .findFirst()
                     .orElse(null);
                  if (historyResource != null) {
                     if ("comp".equals(docx.getResourceCategory())) {
                        String compId = LuceneIndexManager.getCompId(docx.getId());
                        LcdpModulePageCompBean comp = (LcdpModulePageCompBean)lcdpModulePageCompService.getDao().selectByIdIfPresent(compId);
                        if (comp != null) {
                           String content = comp.getConfig() + (StringUtils.isEmpty(comp.getConfigExtend()) ? "" : comp.getConfigExtend());
                           if (content.indexOf(searchKey) >= 0) {
                              resultList.add(docx);
                           }
                        }
                     } else {
                        String content = historyResource.getContent();
                        if (!StringUtils.isEmpty(content) && content.indexOf(searchKey) >= 0) {
                           resultList.add(docx);
                        }
                     }
                  }
               }
            }

            List<LcdpResourceDocumentBean> filterList = filterMapperDoc(resultList);
            return filterList.stream()
               .collect(
                  Collectors.collectingAndThen(
                     Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(LcdpResourceDocumentBean::getId))), ArrayList::new
                  )
               );
         } catch (Exception var29) {
            logger.error("===========>lucene searchPagination error:" + var29.getMessage());
         } finally {
            if (reader != null) {
               try {
                  reader.close();
               } catch (IOException var28) {
                  logger.error("===========>lucene IndexReader close error:" + var28.getMessage());
               }
            }
         }

         return CollectionUtils.emptyList();
      }
   }

   public static List<LcdpResourceDocumentBean> filterMapperDoc(List<LcdpResourceDocumentBean> list) {
      return LcdpScriptUtils.validateCurrentDBMybatisMapper()
         ? list.stream()
            .filter(
               e -> !StringUtils.equals("mapper", e.getResourceCategory())
                  || StringUtils.equals("mapper", e.getResourceCategory()) && StringUtils.containsIgnoreCase(e.getResourceName(), CURRENT_ENVIRONMENT_DATABASE)
            )
            .collect(Collectors.toList())
         : list;
   }

   private static LcdpResourceDocumentBean mergeResource(String splitId, IndexSearcher searcher) throws Exception {
      Query query = new TermQuery(new Term("id", splitId));
      TopDocs tds = searcher.search(query, 2000);
      if (tds.totalHits.value == 0L) {
         return null;
      } else {
         List<LcdpResourceDocumentBean> list = new ArrayList<>();

         for (ScoreDoc sdc : tds.scoreDocs) {
            Document doc = searcher.doc(sdc.doc);
            String id = doc.get("id");
            String name = doc.get("resourceName");
            String path = doc.get("resourcePath");
            String category = doc.get("resourceCategory");
            String content = doc.get("resourceContent");
            Long version = Long.parseLong(doc.get("version"));
            String type = doc.get("type");
            String createdById = doc.get("createdById");
            list.add(new LcdpResourceDocumentBean(id, name, path, category, content, version, type, createdById));
         }

         return list.stream().max(Comparator.comparing(LcdpResourceDocumentBean::getVersion)).get();
      }
   }

   private static List<LcdpResourceDocumentBean> selectResourceResult(String searchKey, String resourceCategory, IndexSearcher searcher, String flag) throws Exception {
      Analyzer analyzer = new StandardAnalyzer();
      Builder builder = new Builder();
      Query contentQuery = new WildcardQuery(new Term("ignorCaseContent", "*" + searchKey.toLowerCase() + "*"));
      builder.add(contentQuery, Occur.MUST);
      if (!StringUtils.isEmpty(resourceCategory)) {
         QueryParser categoryParser = new QueryParser("resourceCategory", analyzer);
         Query categoryQuery = categoryParser.parse(resourceCategory);
         if ("js".equals(resourceCategory)) {
            categoryQuery = categoryParser.parse("js view");
         }

         builder.add(categoryQuery, Occur.MUST);
      }

      QueryParser typeParser = new QueryParser("type", analyzer);
      Query typeQuery = typeParser.parse(flag);
      builder.add(typeQuery, Occur.MUST);
      if ("0".equals(flag)) {
         String loginUserId = LocalContextHelper.getLoginUserId();
         QueryParser createdByIdParser = new QueryParser("createdById", analyzer);
         Query createdByIdQuery = createdByIdParser.parse(loginUserId);
         builder.add(createdByIdQuery, Occur.MUST);
      }

      List<LcdpResourceDocumentBean> resultList = new ArrayList<>();
      List<LcdpResourceDocumentBean> splitResultList = new ArrayList<>();
      TopDocs docs = searcher.search(builder.build(), 2000);

      for (ScoreDoc scoreDoc : docs.scoreDocs) {
         Document doc = searcher.doc(scoreDoc.doc);
         String id = doc.get("id");
         String name = doc.get("resourceName");
         String path = doc.get("resourcePath");
         String category = doc.get("resourceCategory");
         String content = doc.get("resourceContent");
         Long version = Long.parseLong(doc.get("version"));
         String type = doc.get("type");
         String createdById = doc.get("createdById");
         if (id.indexOf("_") > 0) {
            splitResultList.add(new LcdpResourceDocumentBean(id, name, path, category, content, version, type, createdById));
         } else {
            resultList.add(new LcdpResourceDocumentBean(id, name, path, category, content, version, type, createdById));
         }
      }

      if (splitResultList.size() > 0) {
         for (String splitId : splitResultList.stream().map(e -> e.getId().split("_")[0]).distinct().collect(Collectors.toList())) {
            LcdpResourceDocumentBean resource = null;
            StringBuilder sb = new StringBuilder();
            int count = 0;

            do {
               LcdpResourceDocumentBean splitResource = mergeResource(splitId + "_" + count, searcher);
               sb.append(splitResource.getResourceContent());
               if (count == 0) {
                  resource = splitResource;
               }
            } while (mergeResource(splitId + "_" + ++count, searcher) != null);

            resource.setId(splitId);
            resource.setResourceContent(sb.toString());
            resultList.add(resource);
         }
      }

      return resultList;
   }
}
