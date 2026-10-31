package com.sunwayworld.emc.module.bp.result.olifilm.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 油膜滤筒信息表
 * 
 * @author licm@sunwayworld.com
 * @date 2022-08-17
 */
@Table("T_EMC_OIL_FILM")
public class EmcOilFilmBean extends AbstractInsertable<Long> implements Insertable<Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8353303800494130868L;
	/**
	 * 
	 */
	@Transient

    @Id
    private Long id;// 主键
    private String oilFilmNo;// 滤筒或油膜编号
    private String weighingUserId;// 称重人id
    private String weighingUserName;// 称重人
    private Long projId;//项目id
    private Long orderId;//样品id
    private Long orderNo;//样品编号
    private String projNo;// 项目编号
    private LocalDate weighingDate;// 称重时间
    private String emptyFilterBarrelMOneOne;// 空滤桶M1-1(g)
    private String emptyFilterBarrelMOneTwo;// 空滤桶M1-2(g)
    private String emptyFilterBarrelMOneThree;// 空滤桶M1-3(g)
    private String emptyFilterBarrelMOneFour;// 空滤桶M1-4(g)
    private String emptyFilterBarrelMOneFive;// 空滤桶M1-5(g)
    private String emptyFilterBarrelMOneSix;// 空滤桶M1-6(g)
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称
    private String orgId;// 父单位编码

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getWeighingUserId() {
        return weighingUserId;
    }

    public void setWeighingUserId(String weighingUserId) {
        this.weighingUserId = weighingUserId;
    }

    public String getWeighingUserName() {
        return weighingUserName;
    }

    public void setWeighingUserName(String weighingUserName) {
        this.weighingUserName = weighingUserName;
    }

    public String getProjNo() {
        return projNo;
    }

    public void setProjNo(String projNo) {
        this.projNo = projNo;
    }

    public LocalDate getWeighingDate() {
        return weighingDate;
    }

    public void setWeighingDate(LocalDate weighingDate) {
        this.weighingDate = weighingDate;
    }

    public String getEmptyFilterBarrelMOneOne() {
        return emptyFilterBarrelMOneOne;
    }

    public void setEmptyFilterBarrelMOneOne(String emptyFilterBarrelMOneOne) {
        this.emptyFilterBarrelMOneOne = emptyFilterBarrelMOneOne;
    }

    public String getEmptyFilterBarrelMOneTwo() {
        return emptyFilterBarrelMOneTwo;
    }

    public void setEmptyFilterBarrelMOneTwo(String emptyFilterBarrelMOneTwo) {
        this.emptyFilterBarrelMOneTwo = emptyFilterBarrelMOneTwo;
    }

    public String getEmptyFilterBarrelMOneThree() {
        return emptyFilterBarrelMOneThree;
    }

    public void setEmptyFilterBarrelMOneThree(String emptyFilterBarrelMOneThree) {
        this.emptyFilterBarrelMOneThree = emptyFilterBarrelMOneThree;
    }

    public String getEmptyFilterBarrelMOneFour() {
        return emptyFilterBarrelMOneFour;
    }

    public void setEmptyFilterBarrelMOneFour(String emptyFilterBarrelMOneFour) {
        this.emptyFilterBarrelMOneFour = emptyFilterBarrelMOneFour;
    }

    public String getEmptyFilterBarrelMOneFive() {
        return emptyFilterBarrelMOneFive;
    }

    public void setEmptyFilterBarrelMOneFive(String emptyFilterBarrelMOneFive) {
        this.emptyFilterBarrelMOneFive = emptyFilterBarrelMOneFive;
    }

    public String getEmptyFilterBarrelMOneSix() {
        return emptyFilterBarrelMOneSix;
    }

    public void setEmptyFilterBarrelMOneSix(String emptyFilterBarrelMOneSix) {
        this.emptyFilterBarrelMOneSix = emptyFilterBarrelMOneSix;
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

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

	public Long getProjId() {
		return projId;
	}

	public void setProjId(Long projId) {
		this.projId = projId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Long orderNo) {
		this.orderNo = orderNo;
	}

	public String getOilFilmNo() {
		return oilFilmNo;
	}

	public void setOilFilmNo(String oilFilmNo) {
		this.oilFilmNo = oilFilmNo;
	}

}
