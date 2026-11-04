package com.sunwayworld.cloud.module.equipt.pdfacqconfig.service.impl;

import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqPreprocessBean;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqResultDTO;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.persistent.dao.CorePdfAcqPreprocessDao;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.service.CorePdfAcqPreprocessService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.ListUtils;

@Repository
@GikamBean
public class CorePdfAcqPreprocessServiceImpl implements CorePdfAcqPreprocessService {
   @Autowired
   private CorePdfAcqPreprocessDao corePdfAcqPreprocessDao;

   public CorePdfAcqPreprocessDao getDao() {
      return this.corePdfAcqPreprocessDao;
   }

   @Override
   public String preprocess(Long id, String fileContent) {
      for (CorePdfAcqPreprocessBean acqPreprocess : this.selectListByFilter(
         SearchFilter.instance().match("FILEACQCONFIGID", id).filter(MatchPattern.EQ), new Order[0]
      )) {
         String keyword = acqPreprocess.getKeyword();
         if (!StringUtils.isEmpty(keyword)) {
            if ("remove".equals(acqPreprocess.getOperation())) {
               fileContent = StringUtils.replace(fileContent, keyword, "");
            } else if ("removeRow".equals(acqPreprocess.getOperation())) {
               if (fileContent.contains(keyword)) {
                  fileContent = deleteRow(fileContent, keyword);
               }
            } else if ("replace".equals(acqPreprocess.getOperation())) {
               String replaceStr = acqPreprocess.getReplaceStr();
               fileContent = StringUtils.replace(fileContent, keyword, Optional.ofNullable(replaceStr).orElse(""));
            }
         }
      }

      return fileContent;
   }

   @Override
   public List<CorePdfAcqResultDTO> resultPreprocess(Long id, List<CorePdfAcqResultDTO> pdfAcqResultList) {
      if (ListUtils.isEmpty(pdfAcqResultList)) {
         return pdfAcqResultList;
      } else {
         for (CorePdfAcqPreprocessBean acqPreprocess : this.selectListByFilter(
            SearchFilter.instance().match("PDFACQCONFIGID", id).filter(MatchPattern.EQ), new Order[0]
         )) {
            String keyword = acqPreprocess.getKeyword();
            if (!StringUtils.isEmpty(keyword)) {
               if ("remove".equals(acqPreprocess.getOperation())) {
                  this.resultPreprocessRemove(keyword, pdfAcqResultList);
               } else if ("removeRow".equals(acqPreprocess.getOperation())) {
                  pdfAcqResultList = this.resultPreprocessOperationRemove(keyword, pdfAcqResultList);
               } else if ("replace".equals(acqPreprocess.getOperation())) {
                  this.resultPreprocessReplace(acqPreprocess, pdfAcqResultList);
               }
            }
         }

         return pdfAcqResultList;
      }
   }

   private List<CorePdfAcqResultDTO> resultPreprocessOperationRemove(String keyword, List<CorePdfAcqResultDTO> pdfAcqResultList) {
      List<CorePdfAcqResultDTO> newPdfAcqResultList = new ArrayList<>();
      pdfAcqResultList.forEach(
         corePdfAcqResultDTO -> {
            if (!StringUtils.isEmpty(corePdfAcqResultDTO.getResult())
               && !corePdfAcqResultDTO.getResult().contains(keyword)
               && !StringUtils.isEmpty(corePdfAcqResultDTO.getSampleId())
               && !corePdfAcqResultDTO.getSampleId().contains(keyword)
               && !StringUtils.isEmpty(corePdfAcqResultDTO.getItemName())
               && !corePdfAcqResultDTO.getItemName().contains(keyword)) {
               newPdfAcqResultList.add(corePdfAcqResultDTO);
            }
         }
      );
      return newPdfAcqResultList;
   }

   private void resultPreprocessReplace(CorePdfAcqPreprocessBean acqPreprocess, List<CorePdfAcqResultDTO> pdfAcqResultList) {
      String keyword = acqPreprocess.getKeyword();
      if (!StringUtils.isEmpty(keyword)) {
         pdfAcqResultList.forEach(corePdfAcqResultDTO -> {
            if (!StringUtils.isEmpty(corePdfAcqResultDTO.getResult())) {
               String replaceStr = acqPreprocess.getReplaceStr();
               corePdfAcqResultDTO.setResult(StringUtils.replace(corePdfAcqResultDTO.getResult(), keyword, Optional.ofNullable(replaceStr).orElse("")));
            }

            if (!StringUtils.isEmpty(corePdfAcqResultDTO.getSampleId())) {
               String replaceStr = acqPreprocess.getReplaceStr();
               corePdfAcqResultDTO.setSampleId(StringUtils.replace(corePdfAcqResultDTO.getSampleId(), keyword, Optional.ofNullable(replaceStr).orElse("")));
            }

            if (!StringUtils.isEmpty(corePdfAcqResultDTO.getItemName())) {
               String replaceStr = acqPreprocess.getReplaceStr();
               corePdfAcqResultDTO.setItemName(StringUtils.replace(corePdfAcqResultDTO.getItemName(), keyword, Optional.ofNullable(replaceStr).orElse("")));
            }
         });
      }
   }

   private void resultPreprocessRemove(String keyword, List<CorePdfAcqResultDTO> pdfAcqResultList) {
      pdfAcqResultList.forEach(corePdfAcqResultDTO -> {
         if (!StringUtils.isEmpty(corePdfAcqResultDTO.getResult())) {
            corePdfAcqResultDTO.setResult(StringUtils.replace(corePdfAcqResultDTO.getResult(), keyword, ""));
         }

         if (!StringUtils.isEmpty(corePdfAcqResultDTO.getSampleId())) {
            corePdfAcqResultDTO.setSampleId(StringUtils.replace(corePdfAcqResultDTO.getSampleId(), keyword, ""));
         }

         if (!StringUtils.isEmpty(corePdfAcqResultDTO.getItemName())) {
            corePdfAcqResultDTO.setItemName(StringUtils.replace(corePdfAcqResultDTO.getItemName(), keyword, ""));
         }
      });
   }

   private static String deleteRow(String content, String keyword) {
      String[] splits = content.split("(\\r|\\n)");
      StringBuilder sb = new StringBuilder();

      for (String value : splits) {
         if (!value.contains(keyword)) {
            if (sb.length() > 0) {
               sb.append("\n");
            }

            sb.append(value);
         }
      }

      return sb.toString();
   }
}
