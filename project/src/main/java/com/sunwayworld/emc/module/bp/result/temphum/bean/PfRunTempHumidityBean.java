package com.sunwayworld.emc.module.bp.result.temphum.bean;



import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 分析录入温湿度表
 *
 * @author licm@sunwayworld.com
 * @date 2022-01-10
 */
@Table("T_EMC_RUN_TEMP_HUMIDITY")
public class PfRunTempHumidityBean extends AbstractInsertable<Long> implements Insertable<Long> {

    @Transient

	private static final long serialVersionUID = -5364593588409637658L;

	@Id
    private Long id;// 主键Id
    private String runId;//批号
    private Long projId;
    private String temperature; 
    private String humidity;
    private Long orderTaskId;
    private Long equiptId;
    private String orgid;//批号
    private Long orderId;//样品id
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginDate; // 分析开始日期一
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate; // 分析结束日期一
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginDateOne; // 分析开始日期二
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDateOne; // 分析结束日期二
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginDateTwo; // 分析开始日期三
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDateTwo; // 分析开始日期三
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称

	@Override
    public Long getId() {
		return id;
	}

	@Override
    public void setId(Long id) {
		this.id = id;
	}

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

    @Override
    public String getCreatedById() {
        return createdById;
    }

	public Long getProjId() {
		return projId;
	}

	public void setProjId(Long projId) {
		this.projId = projId;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public Long getOrderTaskId() {
		return orderTaskId;
	}

	public void setOrderTaskId(Long orderTaskId) {
		this.orderTaskId = orderTaskId;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}
	
    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

	public String getOrgid() {
		return orgid;
	}

	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}

	public Long getEquiptId() {
		return equiptId;
	}

	public void setEquiptId(Long equiptId) {
		this.equiptId = equiptId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(LocalDate beginDate) {
        this.beginDate = beginDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
   
    public LocalDate getBeginDateOne() {
        return beginDateOne;
    }

    public void setBeginDateOne(LocalDate beginDateOne) {
        this.beginDateOne = beginDateOne;
    }
    
    public LocalDate getEndDateOne() {
        return endDateOne;
    }

    public void setEndDateOne(LocalDate endDateOne) {
        this.endDateOne = endDateOne;
    }
    
    public LocalDate getBeginDateTwo() {
        return beginDateTwo;
    }

    public void setBeginDateTwo(LocalDate beginDateTwo) {
        this.beginDateTwo = beginDateTwo;
    }
    
    public LocalDate getEndDateTwo() {
        return endDateTwo;
    }

    public void setEndDateTwo(LocalDate endDateTwo) {
        this.endDateTwo = endDateTwo;
    }
}
