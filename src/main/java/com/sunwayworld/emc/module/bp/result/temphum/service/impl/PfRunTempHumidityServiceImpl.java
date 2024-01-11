package com.sunwayworld.emc.module.bp.result.temphum.service.impl;



import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.result.temphum.bean.PfRunTempHumidityBean;
import com.sunwayworld.emc.module.bp.result.temphum.dao.PfRunTempHumidityDao;
import com.sunwayworld.emc.module.bp.result.temphum.service.PfRunTempHumidityService;
import com.sunwayworld.emc.module.mdm.basemdm.envrecord.bean.EmcEnvRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.envrecord.service.EmcEnvRecordService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author licm@sunwayworld.com 2022年01月10日
 */
@Repository
@GikamBean
public class PfRunTempHumidityServiceImpl implements PfRunTempHumidityService {
    @Autowired
    private PfRunTempHumidityDao pfRunTempHumidityDao;

    @Autowired
    @Lazy
    private EmcEquiptService equiptService;
  
    @Autowired
    @Lazy
    private EmcEnvRecordService emcEnvRecordService;
 
    
    @Autowired
    @Lazy
    private EmcOrderTaskService orderTaskService;
    
    @Override
    @SuppressWarnings("unchecked")
    public PfRunTempHumidityDao getDao() {
        return pfRunTempHumidityDao;
    }
    

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void synTempHumidity(RestJsonWrapperBean wrapper) {
    	List<PfRunTempHumidityBean> tempHumidityList = wrapper.parse(PfRunTempHumidityBean.class);
    	List<PfRunTempHumidityBean> synTempHumidityList = new ArrayList<PfRunTempHumidityBean>();
    	List<Long> equiptIdList = tempHumidityList.stream().map(temp ->temp.getEquiptId()).distinct().collect(Collectors.toList());
    	List<EmcEquiptBean> equiptList = equiptService.selectListByIds(equiptIdList);
    	List<String> roomList = equiptList.stream().map(eq ->eq.getExt$Item("room")).collect(Collectors.toList());
    	List<EmcEnvRecordBean> allEnvRecordList = emcEnvRecordService.getDao().selectListByOneColumnValues(roomList, "room");
    	tempHumidityList.forEach(temp ->{
    		//根据仪器ID找到仪器表新增的房间字段        	
        	EmcEquiptBean equiptBean = equiptList.stream().filter(e ->ObjectUtils.equals(temp.getEquiptId(),e.getId())).collect(Collectors.toList()).get(0);
        	String room = equiptBean.getExt$Item("room");
        	if(room == "" || room ==null){
        		return ;
        	}
        	//根据仪器表的房间字段和日期字段找到环境维护的房间今天上午的温度,跨多个天温度需要拼接--begin
        	LocalDate beginOrgDate = temp.getBeginDate();
        	LocalDate endOrgDate = temp.getEndDate();
        	LocalDate beginOrgDateOne = temp.getBeginDateOne();
        	LocalDate endOrgDateOne = temp.getEndDateOne();
        	LocalDate beginOrgDateTwo = temp.getBeginDateTwo();
        	LocalDate endOrgDateTwo = temp.getEndDateTwo();
        	
        	List<LocalDate> localDateList = getsAllDatesInTheDateRange(beginOrgDate, endOrgDate);
           	List<EmcEnvRecordBean> envAllOrgList = allEnvRecordList.stream().filter(env ->StringUtils.equals(room,env.getRoom())).collect(Collectors.toList());

            //三个时间段,分别取得时间区间的温湿度
            if(beginOrgDate==null ||endOrgDate==null){
            	return;
            }
       	 	//区间1
           	List<EmcEnvRecordBean> envOrgList = envAllOrgList.stream().filter(env ->localDateList.contains(env.getMonitorDate().toLocalDate())).collect(Collectors.toList());
           	List<EmcEnvRecordBean> envList = envOrgList.stream()
       			  .filter(e -> StringUtils.equals("morning", e.getExt$Item("section"))).collect(Collectors.toList());
       	 	//区间2
        	if(beginOrgDateOne!=null && endOrgDateOne!=null){
                List<LocalDate> localDateListOne = getsAllDatesInTheDateRange(beginOrgDateOne, endOrgDateOne);
            	List<EmcEnvRecordBean> envOrgOneList = envAllOrgList.stream().filter(env ->localDateListOne.contains(env.getMonitorDate().toLocalDate())).collect(Collectors.toList());
            	List<EmcEnvRecordBean> envOneList = envOrgOneList.stream()
        			  .filter(e -> StringUtils.equals("morning", e.getExt$Item("section"))).collect(Collectors.toList());
            	envList.addAll(envOneList);
            }
            //区间3
            if(beginOrgDateTwo!=null && endOrgDateTwo!=null){
	            	//区间3
	            List<LocalDate> localDateListTwo = getsAllDatesInTheDateRange(beginOrgDateTwo, endOrgDateTwo);
	        	List<EmcEnvRecordBean> envOrgTwoList = envAllOrgList.stream().filter(env ->localDateListTwo.contains(env.getMonitorDate().toLocalDate())).collect(Collectors.toList());
	        	List<EmcEnvRecordBean> envListTwo = envOrgTwoList.stream()
	    			  .filter(e -> StringUtils.equals("morning", e.getExt$Item("section"))).collect(Collectors.toList());
	        	envList.addAll(envListTwo);
        }
        	//合并区间
        	if(envList.isEmpty()){
        		return;
        	}
        	if(envList.size()>1){
        		//用户未严格按照区间选择时间段需要根据去重
        		//用户未严格按照区间选择时间段需要根据去重
        		List<String> envId = envList.stream().filter(e -> e.getId() != null).map(e -> e.getId()).distinct().collect(Collectors.toList());
            	List<EmcEnvRecordBean> envOrgSelectList = envAllOrgList.stream().filter(env ->envId.contains(env.getId())).collect(Collectors.toList());
            	envOrgSelectList.sort(Comparator.comparing(EmcEnvRecordBean::getMonitorDate));
        		List<String> temperatures = envOrgSelectList.stream().map(e -> e.getTemperature()).collect(Collectors.toList());
        		String temperatureStr = String.join(",", temperatures);
        		List<String> humiditys = envOrgSelectList.stream().map(e -> e.getHumidity()).collect(Collectors.toList());
        		String humidityStr= String.join(",", humiditys);
        		PfRunTempHumidityBean updatePfRunTempHumidity = new PfRunTempHumidityBean();
            	updatePfRunTempHumidity.setId(temp.getId());
            	updatePfRunTempHumidity.setTemperature(temperatureStr);
            	updatePfRunTempHumidity.setHumidity(humidityStr);
            	synTempHumidityList.add(updatePfRunTempHumidity);
        	}else{
        		EmcEnvRecordBean envBean = envList.get(0);
            	String temperature = envBean.getTemperature();
            	String humidity = envBean.getHumidity();
              	//根据仪器表的房间字段和日期字段找到环境维护的房间今天上午的温度--end
        		PfRunTempHumidityBean updatePfRunTempHumidity = new PfRunTempHumidityBean();
            	updatePfRunTempHumidity.setId(temp.getId());
            	updatePfRunTempHumidity.setTemperature(temperature);
            	updatePfRunTempHumidity.setHumidity(humidity);
            	synTempHumidityList.add(updatePfRunTempHumidity);
        	}
        	

    	});
        getDao().update(synTempHumidityList,"temperature","humidity");

    	return;
    }
    
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void synAllTempHumidity(RestJsonWrapperBean wrapper) {
    	String runId = wrapper.getFilterValue("runId");
    	
    	List<PfRunTempHumidityBean> tempHumidityList = getDao().selectListByOneColumnValue(runId,"runId");
    	List<PfRunTempHumidityBean> synTempHumidityList = new ArrayList<PfRunTempHumidityBean>(); 
    	List<Long> equiptIdList = tempHumidityList.stream().map(temp ->temp.getEquiptId()).filter(temp ->!ObjectUtils.isEmpty(temp)).distinct().collect(Collectors.toList());

    	List<EmcEquiptBean> equiptList = equiptService.selectListByIds(equiptIdList);
    	List<String> roomList = equiptList.stream().map(eq ->eq.getExt$Item("room")).collect(Collectors.toList());
    	List<EmcEnvRecordBean> allEnvRecordList = emcEnvRecordService.getDao().selectListByOneColumnValues(roomList, "room");
    	tempHumidityList.forEach(temp ->{
    		//根据仪器ID找到仪器表新增的房间字段
    		if(temp.getEquiptId()==null){
    			return;
    		}
        	EmcEquiptBean equiptBean = equiptList.stream().filter(e ->ObjectUtils.equals(temp.getEquiptId(),e.getId())).collect(Collectors.toList()).get(0);
        	String room = equiptBean.getExt$Item("room");
        	if(room == "" || room ==null){
        		return ;
        	}
        	//根据仪器表的房间字段和日期字段找到环境维护的房间今天上午的温度,跨多个天温度需要拼接--begin
        	LocalDate beginOrgDate = temp.getBeginDate();
        	LocalDate endOrgDate = temp.getEndDate();
        	LocalDate beginOrgDateOne = temp.getBeginDateOne();
        	LocalDate endOrgDateOne = temp.getEndDateOne();
        	LocalDate beginOrgDateTwo = temp.getBeginDateTwo();
        	LocalDate endOrgDateTwo = temp.getEndDateTwo();
        	
            //三个时间段,分别取得时间区间的温湿度
            if(beginOrgDate==null ||endOrgDate==null){
            	return;
            }
            List<LocalDate> localDateList = getsAllDatesInTheDateRange(beginOrgDate, endOrgDate);
        	List<EmcEnvRecordBean> envAllOrgList = allEnvRecordList.stream().filter(env ->StringUtils.equals(room,env.getRoom())).collect(Collectors.toList());

       	 	//区间1
        	List<EmcEnvRecordBean> envOrgList = envAllOrgList.stream().filter(env ->localDateList.contains(env.getMonitorDate().toLocalDate())).collect(Collectors.toList());
        	List<EmcEnvRecordBean> envList = envOrgList.stream()
    			  .filter(e -> StringUtils.equals("morning", e.getExt$Item("section"))).collect(Collectors.toList());
       	 	//区间2
        	if(beginOrgDateOne!=null && endOrgDateOne!=null){
                List<LocalDate> localDateListOne = getsAllDatesInTheDateRange(beginOrgDateOne, endOrgDateOne);
            	List<EmcEnvRecordBean> envOrgOneList = envAllOrgList.stream().filter(env ->localDateListOne.contains(env.getMonitorDate().toLocalDate())).collect(Collectors.toList());
            	List<EmcEnvRecordBean> envOneList = envOrgOneList.stream()
        			  .filter(e -> StringUtils.equals("morning", e.getExt$Item("section"))).collect(Collectors.toList());
            	envList.addAll(envOneList);
            }
            //区间3
            if(beginOrgDateTwo!=null && endOrgDateTwo!=null){
                	//区间3
                List<LocalDate> localDateListTwo = getsAllDatesInTheDateRange(beginOrgDateTwo, endOrgDateTwo);
            	List<EmcEnvRecordBean> envOrgTwoList = envAllOrgList.stream().filter(env ->localDateListTwo.contains(env.getMonitorDate().toLocalDate())).collect(Collectors.toList());
            	List<EmcEnvRecordBean> envListTwo = envOrgTwoList.stream()
        			  .filter(e -> StringUtils.equals("morning", e.getExt$Item("section"))).collect(Collectors.toList());
            	envList.addAll(envListTwo);
            }
        	//合并区间
        	if(envList.isEmpty()){
        		return;
        	}
        	if(envList.size()>1){
        		//用户未严格按照区间选择时间段需要根据去重
        		List<String> envId = envList.stream().filter(e -> e.getId() != null).map(e -> e.getId()).distinct().collect(Collectors.toList());
            	List<EmcEnvRecordBean> envOrgSelectList = envAllOrgList.stream().filter(env ->envId.contains(env.getId())).collect(Collectors.toList());
            	envOrgSelectList.sort(Comparator.comparing(EmcEnvRecordBean::getMonitorDate));
        		List<String> temperatures = envOrgSelectList.stream().map(e -> e.getTemperature()).collect(Collectors.toList());
        		String temperatureStr = String.join(",", temperatures);
        		List<String> humiditys = envOrgSelectList.stream().map(e -> e.getHumidity()).collect(Collectors.toList());
        		String humidityStr= String.join(",", humiditys);
        		PfRunTempHumidityBean updatePfRunTempHumidity = new PfRunTempHumidityBean();
            	updatePfRunTempHumidity.setId(temp.getId());
            	updatePfRunTempHumidity.setTemperature(temperatureStr);
            	updatePfRunTempHumidity.setHumidity(humidityStr);
            	synTempHumidityList.add(updatePfRunTempHumidity);
        	}else{
        		EmcEnvRecordBean envBean = envList.get(0);
            	String temperature = envBean.getTemperature();
            	String humidity = envBean.getHumidity();
              	//根据仪器表的房间字段和日期字段找到环境维护的房间今天上午的温度--end
        		PfRunTempHumidityBean updatePfRunTempHumidity = new PfRunTempHumidityBean();
            	updatePfRunTempHumidity.setId(temp.getId());
            	updatePfRunTempHumidity.setTemperature(temperature);
            	updatePfRunTempHumidity.setHumidity(humidity);
            	synTempHumidityList.add(updatePfRunTempHumidity);
        	}
        	

    	});
        getDao().update(synTempHumidityList,"temperature","humidity");

    	return;
    }
    
    
    @Override
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void batchUpdateTempOrder(RestJsonWrapperBean wrapper) {
    	List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
    	//from表单填的字段温湿度，三个时间段
    	PfRunTempHumidityBean runTemp = wrapper.parseUnique(PfRunTempHumidityBean.class);
    	String temperature = runTemp.getTemperature(); 
    	String humidity = runTemp.getHumidity();
    	LocalDate beginDate = runTemp.getBeginDate();
    	LocalDate endDate = runTemp.getEndDate();
    	LocalDate beginDateOne = runTemp.getBeginDateOne();
    	LocalDate endDateOne = runTemp.getEndDateOne();
    	LocalDate beginDateTwo = runTemp.getBeginDateTwo();
    	LocalDate endDateTwo = runTemp.getEndDateTwo();
    	
    	String runId = orderTaskList.get(0).getRunId();//同一批下runid都一样
    	//找到根据温湿度表对应的数据
        List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
        
        List<EmcOrderTaskBean> toOrderTaskOrgList = orderTaskService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");
        List<EmcOrderTaskBean> toOrderTaskList = toOrderTaskOrgList.stream()
		  .filter(e -> StringUtils.equals(runId, e.getRunId())).collect(Collectors.toList());
        //温湿度表也是按照样品测试，用ordertaskid匹配
        List<Long> orderTaskIdList = toOrderTaskList.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
        List<PfRunTempHumidityBean> toOrderTempOrgList = getDao().selectListByOneColumnValues(orderTaskIdList, "orderTaskId");
        
        List<PfRunTempHumidityBean> updateOrderTempList =  new ArrayList<PfRunTempHumidityBean>();
        
        toOrderTempOrgList.forEach(e -> {
        	PfRunTempHumidityBean updateOrderTemp = new PfRunTempHumidityBean();
        	updateOrderTemp.setId(e.getId());
        	if(temperature!=null && temperature!=""){
            	updateOrderTemp.setTemperature(temperature);
        	}else{
            	updateOrderTemp.setTemperature(e.getTemperature());
        	}
        	if(humidity!=null && humidity!=""){
            	updateOrderTemp.setHumidity(humidity);
        	}else{
            	updateOrderTemp.setHumidity(e.getHumidity());
        	}
        	if(beginDate!=null){
            	updateOrderTemp.setBeginDate(beginDate);
        	}else{
            	updateOrderTemp.setBeginDate(e.getBeginDate());
        	}
        	if(endDate!=null ){
            	updateOrderTemp.setEndDate(endDate);
        	}else{
            	updateOrderTemp.setEndDate(e.getEndDate());
        	}
        	if(beginDateOne!=null ){
            	updateOrderTemp.setBeginDateOne(beginDateOne);;
        	}else{
            	updateOrderTemp.setBeginDateOne(e.getBeginDateOne());
        	}
           	if(endDateOne!=null){
            	updateOrderTemp.setEndDateOne(endDateOne);
        	}else{
            	updateOrderTemp.setEndDateOne(e.getEndDateOne());
        	}
           	if(beginDateTwo!=null ){
            	updateOrderTemp.setBeginDateTwo(beginDateTwo);
        	}else{
            	updateOrderTemp.setBeginDateTwo(e.getBeginDateTwo());
        	}
           	if(endDateTwo!=null ){
            	updateOrderTemp.setEndDateTwo(endDateTwo);
        	}else{
            	updateOrderTemp.setEndDateTwo(e.getEndDateTwo());
        	}
        	updateOrderTempList.add(updateOrderTemp);
        });
        getDao().update(updateOrderTempList,"temperature","humidity","begindate","enddate","begindateone","enddateone","begindatetwo","enddatetwo");
    	return;
    }
    
    //获取日期区间内的日期
    private static List<LocalDate> getsAllDatesInTheDateRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> localDateList = new ArrayList<>();
        // 开始时间必须小于结束时间
        if (startDate.isAfter(endDate)) {
            return null;
        }
        while (startDate.isBefore(endDate)) {
            localDateList.add(startDate);
            startDate = startDate.plusDays(1);
        }
        localDateList.add(endDate);
        return localDateList;
    }

}
    
    