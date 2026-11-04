package com.sunwayworld.cloud.module.item.samplescheme.service.impl;

import com.sunwayworld.cloud.module.item.samplescheme.bean.CoreSampleQtyCodeBean;
import com.sunwayworld.cloud.module.item.samplescheme.persistent.dao.CoreSampleQtyCodeDao;
import com.sunwayworld.cloud.module.item.samplescheme.service.CoreSampleQtyCodeService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreSampleQtyCodeServiceImpl implements CoreSampleQtyCodeService {
   @Autowired
   private CoreSampleQtyCodeDao coreSampleQtyCodeDao;

   public CoreSampleQtyCodeDao getDao() {
      return this.coreSampleQtyCodeDao;
   }

   @Override
   public String selectSampleQtyCode(Long batchQty, String inspection) {
      List<CoreSampleQtyCodeBean> sampleQtyCodeList = this.selectListByFilter(
         SearchFilter.instance().match("INSPECTION", inspection).filter(MatchPattern.EQ), new Order[]{Order.asc("BATCHQTYLOWERLIMIT")}
      );
      String sampleQtyCode = null;

      for (CoreSampleQtyCodeBean sampleQtyCodeBean : sampleQtyCodeList) {
         Long batchQtyLowerLimit = sampleQtyCodeBean.getBatchQtyLowerLimit();
         Long batchQtyUpperLimit = sampleQtyCodeBean.getBatchQtyUpperLimit();
         if (batchQtyUpperLimit == null) {
            sampleQtyCode = sampleQtyCodeBean.getSampleQtyCode();
            break;
         }

         if (batchQty <= batchQtyUpperLimit && batchQty >= batchQtyLowerLimit) {
            sampleQtyCode = sampleQtyCodeBean.getSampleQtyCode();
            break;
         }
      }

      return sampleQtyCode;
   }
}
