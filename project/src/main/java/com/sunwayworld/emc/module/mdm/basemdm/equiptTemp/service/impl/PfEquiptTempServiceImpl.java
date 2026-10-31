package com.sunwayworld.emc.module.mdm.basemdm.equiptTemp.service.impl;




import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.equiptTemp.bean.PfEquiptTempBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptTemp.dao.PfEquiptTempDao;
import com.sunwayworld.emc.module.mdm.basemdm.equiptTemp.service.PfEquiptTempService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;

/**
 * @author licm@sunwayworld.com 2022年01月10日
 */
@Repository
@GikamBean
public class PfEquiptTempServiceImpl implements PfEquiptTempService {
    @Autowired
    private PfEquiptTempDao pfEquiptTempDao;

    
    @Override
    @SuppressWarnings("unchecked")
    public PfEquiptTempDao getDao() {
        return pfEquiptTempDao;
    }
    

	//根据传的开始日期和结束日期来新增新的环境记录数据
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void batchInsertTemp(RestJsonWrapperBean wrapper)  {
    	PfEquiptTempBean equiptTempBean = wrapper.parseUnique(PfEquiptTempBean.class);
    	LocalDateTime beginOrgDate = equiptTempBean.getStartDate();
    	LocalDateTime endOrgDate = equiptTempBean.getEndDate();
    	Date beginDate = Date.from(beginOrgDate.atZone( ZoneId.systemDefault()).toInstant());
    	Date endDate = Date.from(endOrgDate.atZone( ZoneId.systemDefault()).toInstant());

        CoreUserBean user = LocalContextHelper.getLoginUser();
        try {
            List<Date> lists = dateSplit(beginDate, endDate);
            Collections.reverse(lists);
            if (!lists.isEmpty()) {
                for (Date date : lists) {
                	PfEquiptTempBean copyTempBean = new PfEquiptTempBean();
                	PfEquiptTempBean copyTempNewBean = new PfEquiptTempBean();
                	copyTempBean.setId(ApplicationContextHelper.getNextIdentity());
                	copyTempBean.setEquiptId(equiptTempBean.getEquiptId());
                	copyTempBean.setRecordUserId(user.getId());
                	copyTempBean.setRecordUserName(user.getUserName());
                	copyTempBean.setStartDate(equiptTempBean.getStartDate());
                	copyTempBean.setEndDate(equiptTempBean.getEndDate());
                	copyTempBean.setOrgId(LocalContextHelper.getLoginUser().getExt$Item("parentorgid"));
                	LocalDateTime localDateTime = date.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
                	copyTempBean.setRecordDate(localDateTime);
                	copyTempBean.setCreatedById(LocalContextHelper.getLoginUserId());
                	copyTempBean.setCreatedByName(LocalContextHelper.getLoginUserName());
                	copyTempBean.setCreatedTime(LocalDateTime.now());
                	copyTempBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
                	copyTempBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
                	BeanUtils.copyProperties(copyTempBean,copyTempNewBean);
                	copyTempBean.setSection("morning");
                	copyTempNewBean.setSection("afternoon");
                	copyTempNewBean.setId(ApplicationContextHelper.getNextIdentity());
                    getDao().insert(copyTempBean);
                    getDao().insert(copyTempNewBean);
                }
            }
        } catch (Exception e) {
        }              
    }

    
    private static List<Date> dateSplit(Date startDate, Date endDate)
            throws Exception {
        if (!startDate.before(endDate))
            throw new Exception("开始时间应该在结束时间之后");
        Long spi = endDate.getTime() - startDate.getTime();
        Long step = spi / (24 * 60 * 60 * 1000);// 相隔天数
     
        List<Date> dateList = new ArrayList<Date>();
        dateList.add(endDate);
        for (int i = 1; i <= step; i++) {
            dateList.add(new Date(dateList.get(i - 1).getTime()
                    - (24 * 60 * 60 * 1000)));// 比上一天减一
        }
        return dateList;
    }
    
    
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
    	Long existStatus = 0L;
        CoreUserBean user = LocalContextHelper.getLoginUser();
        PfEquiptTempBean equiptTempBean = wrapper.parseUnique(PfEquiptTempBean.class);
        equiptTempBean.setId(ApplicationContextHelper.getNextIdentity());
        equiptTempBean.setRecordUserId(user.getId());
        equiptTempBean.setRecordUserName(user.getUserName());
        //防止相同日期新增多条环境记录
        Long equiptId = equiptTempBean.getEquiptId();
    	LocalDateTime recordDate = equiptTempBean.getRecordDate();
    	String secition = equiptTempBean.getSection();
    	List<PfEquiptTempBean> existedTempList = getDao().selectListByOneColumnValue(equiptId, "equiptId");
    	List<PfEquiptTempBean> existedDateList = existedTempList.stream().filter(e -> e.getRecordDate().isEqual(recordDate)).collect(Collectors.toList());
    	List<PfEquiptTempBean> existedList = existedDateList.stream().filter(e -> StringUtils.equals(e.getSection(), secition)).collect(Collectors.toList());
    	
    	if(!existedList.isEmpty()){
    		existStatus = 1L;
    	}else{
            getDao().insert(equiptTempBean);
    	}
    	return existStatus;
    }
    
    
}
    
    