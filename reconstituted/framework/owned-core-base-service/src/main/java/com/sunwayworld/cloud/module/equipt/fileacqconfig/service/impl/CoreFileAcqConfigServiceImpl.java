package com.sunwayworld.cloud.module.equipt.fileacqconfig.service.impl;

import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqConfigBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqParamBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqPreprocessBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqResultBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqResultParamBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.dao.CoreFileAcqConfigDao;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.service.CoreFileAcqConfigService;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.service.CoreFileAcqParamService;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.service.CoreFileAcqPreprocessService;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.service.CoreFileAcqResultParamService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreFileAcqConfigServiceImpl implements CoreFileAcqConfigService {
   @Autowired
   private CoreFileAcqConfigDao coreFileAcqConfigDao;
   @Autowired
   private CoreFileAcqParamService coreFileAcqParamService;
   @Autowired
   private CoreFileAcqPreprocessService coreFileAcqPreprocessService;
   @Autowired
   private CoreFileAcqResultParamService coreFileAcqResultParamService;
   @Autowired
   private CoreFileService coreFileService;

   public CoreFileAcqConfigDao getDao() {
      return this.coreFileAcqConfigDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CoreFileAcqConfigBean fileAcqConfig = jsonWrapper.parseUnique(CoreFileAcqConfigBean.class);
      fileAcqConfig.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(fileAcqConfig);
      return fileAcqConfig.getId();
   }

   @Override
   public Page<CoreFileAcqParamBean> selectAcqParamPaginationByFileAcqConfigId(Long id, RestJsonWrapperBean wrapper) {
      return this.coreFileAcqParamService.selectPaginationByFilter(SearchFilter.instance().match("FILEACQCONFIGID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public Long insertAcqParam(Long id, RestJsonWrapperBean wrapper) {
      List<CoreFileAcqParamBean> coreFileAcqParamList = wrapper.parse(CoreFileAcqParamBean.class);
      coreFileAcqParamList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setFileAcqConfigId(id);
      });
      this.coreFileAcqParamService.getDao().insert(coreFileAcqParamList);
      return coreFileAcqParamList.get(0).getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Override
   public void deleteAcqParam(Long id, RestJsonWrapperBean wrapper) {
      this.coreFileAcqParamService.delete(wrapper);
   }

   @Override
   public Page<CoreFileAcqPreprocessBean> selectAcqPreprocessPaginationByFileAcqConfigId(Long id, RestJsonWrapperBean wrapper) {
      return this.coreFileAcqPreprocessService.selectPaginationByFilter(SearchFilter.instance().match("FILEACQCONFIGID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public Long insertAcqPreprocess(Long id, RestJsonWrapperBean wrapper) {
      List<CoreFileAcqPreprocessBean> fileAcqPreprocessList = wrapper.parse(CoreFileAcqPreprocessBean.class);
      fileAcqPreprocessList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setFileAcqConfigId(id);
      });
      this.coreFileAcqPreprocessService.getDao().insert(fileAcqPreprocessList);
      return fileAcqPreprocessList.get(0).getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Override
   public void deleteAcqPreprocess(Long id, RestJsonWrapperBean wrapper) {
      this.coreFileAcqPreprocessService.delete(wrapper);
   }

   @Override
   public Page<CoreFileAcqResultParamBean> selectAcqResultParamPaginationByFileAcqConfigId(Long id, RestJsonWrapperBean wrapper) {
      return this.coreFileAcqResultParamService.selectPaginationByFilter(SearchFilter.instance().match("FILEACQCONFIGID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public Long insertAcqResultParam(Long id, RestJsonWrapperBean wrapper) {
      List<CoreFileAcqResultParamBean> fileAcqResultParamList = wrapper.parse(CoreFileAcqResultParamBean.class);
      fileAcqResultParamList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setFileAcqConfigId(id);
      });
      this.coreFileAcqResultParamService.getDao().insert(fileAcqResultParamList);
      return fileAcqResultParamList.get(0).getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Override
   public void deleteAcqResultParam(Long id, RestJsonWrapperBean wrapper) {
      this.coreFileAcqResultParamService.delete(wrapper);
   }

   @Override
   public String selectFileContentByFileId(Long id, Long fileId) {
      CoreFileBean coreFile = this.coreFileService.selectDetail(fileId);
      File file = CoreFileUtils.getLocalPath(coreFile).toFile();
      String fileContent = FileUtils.extractTextFromFile(file);
      return this.format(fileContent, id);
   }

   @Override
   public String preprocess(Long id) {
      String preprocessedFileContent = this.preprocessFile(id);
      return this.format(preprocessedFileContent, id);
   }

   @Override
   public String preprocessFile(Long id) {
      CoreFileBean coreFile = this.coreFileService.selectFileList(this.getDao().getTable(), id).get(0);
      File file = CoreFileUtils.getLocalPath(coreFile).toFile();
      String fileContent = FileUtils.extractTextFromFile(file);
      return this.coreFileAcqPreprocessService.preprocess(id, fileContent);
   }

   @Override
   public Page<CoreFileAcqResultBean> test(Long id, RestJsonWrapperBean wrapper) {
      CoreFileAcqConfigBean fileAcqConfig = this.selectDetail(id);
      if (StringUtils.isEmpty(fileAcqConfig.getRowSeparator())) {
         return new Pagination<>(CollectionUtils.emptyList());
      } else {
         List<CoreFileAcqResultParamBean> acqResultParamList = this.coreFileAcqResultParamService
            .selectListByFilter(SearchFilter.instance().match("FILEACQCONFIGID", id).filter(MatchPattern.EQ), new Order[0]);
         if (acqResultParamList.isEmpty()) {
            return new Pagination<>(CollectionUtils.emptyList());
         } else {
            String content = this.preprocessFile(id);
            List<CoreFileAcqResultBean> acqResultList = this.analyse(content, fileAcqConfig);
            return new Pagination<>(acqResultList);
         }
      }
   }

   @Override
   public List<CoreFileAcqResultBean> analyseFile(String content, CoreFileAcqConfigBean fileAcqConfig) {
      if (StringUtils.isEmpty(fileAcqConfig.getRowSeparator())) {
         return CollectionUtils.emptyList();
      } else {
         List<CoreFileAcqResultParamBean> acqResultParamList = this.coreFileAcqResultParamService
            .selectListByFilter(SearchFilter.instance().match("FILEACQCONFIGID", fileAcqConfig.getId()).filter(MatchPattern.EQ), new Order[0]);
         if (acqResultParamList.isEmpty()) {
            return CollectionUtils.emptyList();
         } else {
            String prepocessedContent = this.coreFileAcqPreprocessService.preprocess(fileAcqConfig.getId(), content);
            return this.analyse(prepocessedContent, fileAcqConfig);
         }
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void copy(RestJsonWrapperBean wrapper) {
      List<CoreFileAcqConfigBean> configList = wrapper.parse(CoreFileAcqConfigBean.class);
      List<CoreFileAcqConfigBean> copyConfigList = CollectionUtils.emptyList();
      List<CoreFileAcqParamBean> copyParamList = CollectionUtils.emptyList();
      List<CoreFileAcqPreprocessBean> copyPreprocessList = CollectionUtils.emptyList();
      List<CoreFileAcqResultParamBean> copyResultParamList = CollectionUtils.emptyList();
      List<Long> idList = configList.stream().map(e -> e.getId()).collect(Collectors.toList());
      List<CoreFileAcqParamBean> paramList = this.coreFileAcqParamService
         .selectListByFilter(SearchFilter.instance().match("FILEACQCONFIGID", idList).filter(MatchPattern.OR), new Order[0]);
      List<CoreFileAcqPreprocessBean> preprocessList = this.coreFileAcqPreprocessService
         .selectListByFilter(SearchFilter.instance().match("FILEACQCONFIGID", idList).filter(MatchPattern.OR), new Order[0]);
      List<CoreFileAcqResultParamBean> resultParamList = this.coreFileAcqResultParamService
         .selectListByFilter(SearchFilter.instance().match("FILEACQCONFIGID", idList).filter(MatchPattern.OR), new Order[0]);

      for (CoreFileAcqConfigBean coreFileAcqConfigBean : configList) {
         CoreFileAcqConfigBean copyConfigBean = new CoreFileAcqConfigBean();
         BeanUtils.copyProperties(coreFileAcqConfigBean, copyConfigBean);
         copyConfigBean.setId(ApplicationContextHelper.getNextIdentity());
         copyConfigList.add(copyConfigBean);
         if (!CollectionUtils.isEmpty(paramList)) {
            paramList.forEach(e -> {
               if (coreFileAcqConfigBean.getId().equals(e.getFileAcqConfigId())) {
                  CoreFileAcqParamBean copyParamBean = new CoreFileAcqParamBean();
                  BeanUtils.copyProperties(e, copyParamBean);
                  copyParamBean.setId(ApplicationContextHelper.getNextIdentity());
                  copyParamBean.setFileAcqConfigId(copyConfigBean.getId());
                  copyParamList.add(copyParamBean);
               }
            });
         }

         if (!CollectionUtils.isEmpty(preprocessList)) {
            preprocessList.forEach(e -> {
               if (coreFileAcqConfigBean.getId().equals(e.getFileAcqConfigId())) {
                  CoreFileAcqPreprocessBean copyPreprocessBean = new CoreFileAcqPreprocessBean();
                  BeanUtils.copyProperties(e, copyPreprocessBean);
                  copyPreprocessBean.setId(ApplicationContextHelper.getNextIdentity());
                  copyPreprocessBean.setFileAcqConfigId(copyConfigBean.getId());
                  copyPreprocessList.add(copyPreprocessBean);
               }
            });
         }

         if (!CollectionUtils.isEmpty(resultParamList)) {
            resultParamList.forEach(e -> {
               if (coreFileAcqConfigBean.getId().equals(e.getFileAcqConfigId())) {
                  CoreFileAcqResultParamBean copyResultParamBean = new CoreFileAcqResultParamBean();
                  BeanUtils.copyProperties(e, copyResultParamBean);
                  copyResultParamBean.setId(ApplicationContextHelper.getNextIdentity());
                  copyResultParamBean.setFileAcqConfigId(copyConfigBean.getId());
                  copyResultParamList.add(copyResultParamBean);
               }
            });
         }
      }

      this.coreFileAcqConfigDao.insert(copyConfigList);
      this.coreFileAcqParamService.getDao().insert(copyParamList);
      this.coreFileAcqPreprocessService.getDao().insert(copyPreprocessList);
      this.coreFileAcqResultParamService.getDao().insert(copyResultParamList);
   }

   private List<CoreFileAcqResultBean> analyse(String content, CoreFileAcqConfigBean fileAcqConfig) {
      List<String> paragraphList = new ArrayList<>();
      if ("multiple".equals(fileAcqConfig.getDataFormat())
         && !StringUtils.isEmpty(fileAcqConfig.getParagraphSeparator())
         && content.contains(fileAcqConfig.getParagraphSeparator())) {
         paragraphList = Arrays.asList(content.split(fileAcqConfig.getParagraphSeparator()));

         for (String paragraph : paragraphList) {
            paragraph = paragraph + fileAcqConfig.getParagraphSeparator();
         }
      } else {
         paragraphList.add("defaultStartFlag" + content + "defaultEndFlag");
      }

      List<CoreFileAcqParamBean> acqParamList = this.coreFileAcqParamService
         .selectListByFilter(SearchFilter.instance().match("FILEACQCONFIGID", fileAcqConfig.getId()).filter(MatchPattern.EQ), new Order[0]);
      List<CoreFileAcqParamBean> resultAcqParamList = acqParamList.stream().filter(ex -> "result".equals(ex.getField())).collect(Collectors.toList());
      List<CoreFileAcqParamBean> sampleNoAcqParamList = acqParamList.stream().filter(ex -> "sampleNo".equals(ex.getField())).collect(Collectors.toList());
      List<CoreFileAcqParamBean> itemNameAcqParamList = acqParamList.stream().filter(ex -> "itemName".equals(ex.getField())).collect(Collectors.toList());
      List<CoreFileAcqResultParamBean> acqResultParamList = this.coreFileAcqResultParamService
         .selectListByFilter(SearchFilter.instance().match("FILEACQCONFIGID", fileAcqConfig.getId()).filter(MatchPattern.EQ), new Order[0]);
      List<CoreFileAcqResultBean> acqResultList = new ArrayList<>();

      for (String paragraph : paragraphList) {
         for (CoreFileAcqParamBean resultAcqParam : resultAcqParamList) {
            String beginSymbol = resultAcqParam.getBeginSymbol();
            String endSymbol = resultAcqParam.getEndSymbol();
            if (StringUtils.contains(paragraph, beginSymbol)
               && StringUtils.contains(paragraph, endSymbol)
               && paragraph.indexOf(beginSymbol) < paragraph.indexOf(endSymbol)) {
               String sampleNo = null;
               if ("form".equals(fileAcqConfig.getSampleNoFormat()) && !sampleNoAcqParamList.isEmpty()) {
                  CoreFileAcqParamBean sampleNoAcqParam = sampleNoAcqParamList.get(0);
                  String sampleNoBeginSymbol = sampleNoAcqParam.getBeginSymbol();
                  String sampleNoEndSymbol = sampleNoAcqParam.getEndSymbol();
                  if (!StringUtils.isEmpty(sampleNoBeginSymbol)
                     && !StringUtils.isEmpty(sampleNoEndSymbol)
                     && StringUtils.contains(paragraph, sampleNoBeginSymbol)
                     && StringUtils.contains(paragraph, sampleNoEndSymbol)
                     && paragraph.indexOf(sampleNoBeginSymbol) < paragraph.indexOf(sampleNoEndSymbol)) {
                     sampleNo = paragraph.substring(paragraph.indexOf(sampleNoBeginSymbol) + sampleNoBeginSymbol.length(), paragraph.indexOf(sampleNoEndSymbol));
                     sampleNo = StringUtils.trim(sampleNo).replaceAll("/r/n", "");
                  }
               }

               String interceptItemName = null;
               if ("intercept".equals(fileAcqConfig.getItemNameSource()) && !itemNameAcqParamList.isEmpty()) {
                  CoreFileAcqParamBean itemNameAcqParam = itemNameAcqParamList.get(0);
                  String itemNameBeginSymbol = itemNameAcqParam.getBeginSymbol();
                  String itemNameEndSymbol = itemNameAcqParam.getEndSymbol();
                  if (!StringUtils.isEmpty(itemNameBeginSymbol)
                     && !StringUtils.isEmpty(itemNameEndSymbol)
                     && StringUtils.contains(paragraph, itemNameBeginSymbol)
                     && StringUtils.contains(paragraph, itemNameEndSymbol)
                     && paragraph.indexOf(itemNameBeginSymbol) < paragraph.indexOf(itemNameEndSymbol)) {
                     interceptItemName = paragraph.substring(
                        paragraph.indexOf(itemNameBeginSymbol) + itemNameBeginSymbol.length(), paragraph.indexOf(itemNameEndSymbol)
                     );
                     interceptItemName = StringUtils.trim(interceptItemName).replaceAll("/r/n", "");
                  }
               }

               String resultContent = paragraph.substring(paragraph.indexOf(beginSymbol), paragraph.indexOf(endSymbol));
               BufferedReader reader = new BufferedReader(new StringReader(resultContent));
               String resultRow = null;

               try {
                  while ((resultRow = reader.readLine()) != null) {
                     if (!StringUtils.isEmpty(resultRow)) {
                        String[] resultCell = resultRow.split(fileAcqConfig.getRowSeparator());

                        for (CoreFileAcqResultParamBean acqResultParam : acqResultParamList) {
                           Long resultColNo = acqResultParam.getResultColNo();
                           Long itemColNo = acqResultParam.getItemColNo();
                           Long sampleNoColNo = acqResultParam.getSampleNoColNo();
                           Long repeatNoColNo = acqResultParam.getRepeatNoColNo();
                           if (!StringUtils.isEmpty(sampleNo)) {
                              if (resultColNo != null && resultColNo < resultCell.length) {
                                 String result = resultCell[NumberUtils.parseInt(resultColNo)];
                                 if (NumberUtils.isNumber(result)) {
                                    CoreFileAcqResultBean acqResult = new CoreFileAcqResultBean();
                                    acqResult.setAcqResult(result);
                                    if ("input".equals(fileAcqConfig.getItemNameSource())) {
                                       acqResult.setItemName(acqResultParam.getItemName());
                                    } else if ("intercept".equals(fileAcqConfig.getItemNameSource())) {
                                       acqResult.setItemName(interceptItemName);
                                    } else {
                                       if (itemColNo == null || itemColNo >= resultCell.length) {
                                          continue;
                                       }

                                       acqResult.setItemName(resultCell[NumberUtils.parseInt(itemColNo)]);
                                    }

                                    acqResult.setRepeatNo(this.getRepeatNo(repeatNoColNo, resultCell));
                                    acqResult.setSampleNo(sampleNo);
                                    acqResultList.add(acqResult);
                                 }
                              }
                           } else if (resultColNo != null && sampleNoColNo != null && resultColNo < resultCell.length && sampleNoColNo < resultCell.length) {
                              String result = resultCell[NumberUtils.parseInt(resultColNo)];
                              if (NumberUtils.isNumber(result)) {
                                 CoreFileAcqResultBean acqResult = new CoreFileAcqResultBean();
                                 acqResult.setAcqResult(result);
                                 if ("input".equals(fileAcqConfig.getItemNameSource())) {
                                    acqResult.setItemName(acqResultParam.getItemName());
                                 } else if ("intercept".equals(fileAcqConfig.getItemNameSource())) {
                                    acqResult.setItemName(interceptItemName);
                                 } else {
                                    if (itemColNo == null || itemColNo >= resultCell.length) {
                                       continue;
                                    }

                                    acqResult.setItemName(resultCell[NumberUtils.parseInt(itemColNo)]);
                                 }

                                 acqResult.setRepeatNo(this.getRepeatNo(repeatNoColNo, resultCell));
                                 acqResult.setSampleNo(resultCell[NumberUtils.parseInt(sampleNoColNo)]);
                                 acqResultList.add(acqResult);
                              }
                           }
                        }
                     }
                  }
               } catch (IOException var37) {
                  throw new UnexpectedException(var37);
               } finally {
                  if (reader != null) {
                     try {
                        reader.close();
                     } catch (IOException var36) {
                     }
                  }
               }
            }
         }
      }

      return acqResultList;
   }

   private Long getRepeatNo(Long repeatNoColNo, String[] resultCell) {
      long repeatNo = 1L;
      if (repeatNoColNo != null && repeatNoColNo < resultCell.length) {
         String repeatNoStr = resultCell[NumberUtils.parseInt(repeatNoColNo)];
         if (NumberUtils.isNumber(repeatNoStr)) {
            repeatNo = Long.parseLong(repeatNoStr);
         }
      }

      return repeatNo;
   }

   private String format(String fileContent, long id) {
      if (StringUtils.isEmpty(fileContent)) {
         return fileContent;
      } else {
         Set<String> keywordSet = new HashSet<>();

         for (CoreFileAcqParamBean acqParam : this.coreFileAcqParamService
            .selectListByFilter(SearchFilter.instance().match("FILEACQCONFIGID", id).filter(MatchPattern.EQ), new Order[0])) {
            if (!StringUtils.isEmpty(acqParam.getBeginSymbol())) {
               keywordSet.add(acqParam.getBeginSymbol());
            }

            if (!StringUtils.isEmpty(acqParam.getEndSymbol())) {
               keywordSet.add(acqParam.getEndSymbol());
            }
         }

         keywordSet.add("defaultStartFlag");
         keywordSet.add("defaultEndFlag");
         List<CoreFileAcqPreprocessBean> acqPreprocessList = this.coreFileAcqPreprocessService
            .selectListByFilter(SearchFilter.instance().match("FILEACQCONFIGID", id).filter(MatchPattern.EQ), new Order[0]);
         keywordSet.addAll(
            acqPreprocessList.stream().filter(e -> !StringUtils.isEmpty(e.getKeyword())).map(e -> e.getKeyword()).distinct().collect(Collectors.toList())
         );
         keywordSet.addAll(
            acqPreprocessList.stream().filter(e -> !StringUtils.isEmpty(e.getReplaceStr())).map(e -> e.getReplaceStr()).distinct().collect(Collectors.toList())
         );
         String content = fileContent.replaceAll("\\r{0,}\\n", "<br/>");
         content = "defaultStartFlag<br/>" + content + "<br/>defaultEndFlag";

         for (String keyword : keywordSet) {
            keyword = keyword.replaceAll("\\r{0,}\\n", "<br/>");
            content = content.replace(keyword, "<font color='red'>" + keyword + "</font>");
         }

         return content;
      }
   }
}
