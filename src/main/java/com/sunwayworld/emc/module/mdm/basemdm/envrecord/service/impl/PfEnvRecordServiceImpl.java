package com.sunwayworld.emc.module.mdm.basemdm.envrecord.service.impl;


import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.envrecord.bean.EmcEnvRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.envrecord.service.PfEnvRecordService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;


@Repository
@GikamBean
public class PfEnvRecordServiceImpl extends EmcEnvRecordServiceImpl implements PfEnvRecordService  {


	
	//根据传的开始日期和结束日期来新增新的环境记录数据
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void batchInsertEnv(RestJsonWrapperBean wrapper)  {
        EmcEnvRecordBean envRecordBean = wrapper.parseUnique(EmcEnvRecordBean.class);
        String beginOrgDate = envRecordBean.getExt$Item("startDate");
        String endOrgDate = envRecordBean.getExt$Item("endDate");
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        CoreUserBean user = LocalContextHelper.getLoginUser();
        
        try {
            Date beginDate = sf.parse(beginOrgDate);
            Date endDate = sf.parse(endOrgDate);
            List<Date> lists = dateSplit(beginDate, endDate);
            Collections.reverse(lists);
            if (!lists.isEmpty()) {
                for (Date date : lists) {
                	EmcEnvRecordBean copyEnvBean = new EmcEnvRecordBean();
                	EmcEnvRecordBean copyEnvNewBean = new EmcEnvRecordBean();
                	copyEnvBean.setId(ApplicationContextHelper.getNextSequence(getDao().getTable()));
                	copyEnvBean.setRecordUserId(user.getId());
                	copyEnvBean.setRecordUserName(user.getUserName());
                	copyEnvBean.setStandardHumidity(envRecordBean.getStandardHumidity());
                	copyEnvBean.setStandardTemperature(envRecordBean.getStandardTemperature());
                	copyEnvBean.setThermometerNo(envRecordBean.getThermometerNo());
                	copyEnvBean.setRoom(envRecordBean.getRoom());
                	copyEnvBean.setExt$Item("roomid",envRecordBean.getExt$Item("roomid") );
                	copyEnvBean.setExt$Item("startdate",envRecordBean.getExt$Item("startdate") );
                	copyEnvBean.setExt$Item("enddate",envRecordBean.getExt$Item("enddate") );
                	copyEnvBean.setExt$Item("section","morning");
                	copyEnvBean.setOrgId(LocalContextHelper.getLoginUser().getExt$Item("parentorgid"));
                	LocalDateTime localDateTime = date.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
                	copyEnvBean.setMonitorDate(localDateTime);
                	BeanUtils.copyProperties(copyEnvBean,copyEnvNewBean);
                	copyEnvNewBean.setId(ApplicationContextHelper.getNextSequence(getDao().getTable()));
                	copyEnvNewBean.setExt$Item("section","afternoon");
                    getDao().insert(copyEnvBean);
                    getDao().insert(copyEnvNewBean);
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
    public String insert(RestJsonWrapperBean wrapper) {
    	String isExistUserName = "";
        CoreUserBean user = LocalContextHelper.getLoginUser();
        EmcEnvRecordBean envRecordBean = wrapper.parseUnique(EmcEnvRecordBean.class);
        envRecordBean.setId(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        envRecordBean.setRecordUserId(user.getId());
        envRecordBean.setRecordUserName(user.getUserName());
        //防止相同日期新增多条环境记录
        String room = envRecordBean.getRoom();
    	LocalDateTime monitorDate = envRecordBean.getMonitorDate();
    	String secition = envRecordBean.getExt$Item("SECTION");
    	List<EmcEnvRecordBean> existedRoomList = getDao().selectListByOneColumnValue(room, "ROOM");
    	List<EmcEnvRecordBean> existedDateList = existedRoomList.stream().filter(e -> e.getMonitorDate().isEqual(monitorDate)).collect(Collectors.toList());
    	List<EmcEnvRecordBean> existedList = existedDateList.stream().filter(e -> StringUtils.equals(e.getExt$Item("SECTION"), secition)).collect(Collectors.toList());
    	
    	if(!existedList.isEmpty()){
        	List<String> recordNameList=  existedList.stream().map(e ->e.getRecordUserName()).distinct().collect(Collectors.toList());
        	if(recordNameList.size()>=1){
        		isExistUserName = String.join(",", recordNameList);
        	}
    	}else{
            getDao().insert(envRecordBean);
    	}
    	return isExistUserName;
    }

}