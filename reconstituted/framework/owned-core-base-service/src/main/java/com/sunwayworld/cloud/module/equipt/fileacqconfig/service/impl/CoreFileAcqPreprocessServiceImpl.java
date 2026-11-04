package com.sunwayworld.cloud.module.equipt.fileacqconfig.service.impl;

import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqPreprocessBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.persistent.dao.CoreFileAcqPreprocessDao;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.service.CoreFileAcqPreprocessService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreFileAcqPreprocessServiceImpl implements CoreFileAcqPreprocessService {
   @Autowired
   private CoreFileAcqPreprocessDao coreFileAcqPreprocessDao;

   public CoreFileAcqPreprocessDao getDao() {
      return this.coreFileAcqPreprocessDao;
   }

   @Override
   public String preprocess(Long id, String fileContent) {
      for (CoreFileAcqPreprocessBean acqPreprocess : this.selectListByFilter(
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
