package com.sunwayworld.emc.module.mdm.emp.emp.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 人员培训
 *
 * @author huangc@sunwayworld.com
 * @date 2020-05-19
 */
@Table("T_EMC_EMP_TRAIN")
public class EmcEmpTrainBean extends AbstractInsertable<Long> implements Insertable<Long> {

	@Transient
	private static final long serialVersionUID = 1967444192895176546L;

	@Id
    private Long id;// 主键
	private String empId; // 用户名
    private LocalDate trainTime;// 培训时间
    private String trainContent;// 内容
    private String trainResult;// 培训结果
    private String courseCode;// 培训课程编码
    private String courseName;// 课程名称
    private String trainDept;// 机构
    private String trainDeptCode;// 机构编码
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public LocalDate getTrainTime() {
        return trainTime;
    }

    public void setTrainTime(LocalDate trainTime) {
        this.trainTime = trainTime;
    }

    public String getTrainContent() {
        return trainContent;
    }

    public void setTrainContent(String trainContent) {
        this.trainContent = trainContent;
    }

    public String getTrainResult() {
        return trainResult;
    }

    public void setTrainResult(String trainResult) {
        this.trainResult = trainResult;
    }

    public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getTrainDept() {
        return trainDept;
    }

    public void setTrainDept(String trainDept) {
        this.trainDept = trainDept;
    }

    public String getTrainDeptCode() {
		return trainDeptCode;
	}

	public void setTrainDeptCode(String trainDeptCode) {
		this.trainDeptCode = trainDeptCode;
	}

	public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

}
