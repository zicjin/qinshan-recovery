/**
 * @Company 全域旅游
 * @Title: EnvContractDiscountInfoEntity.java
 * @Package com.weipu.sh.erp.sample.entity
 * @author luhaiqing
 * @date 2021年08月03日  14:00:36
 * @version V1.0
 */
package com.sunwayworld.emc.module.erp.bean;
import javax.persistence.*;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

/**
 * @ClassName: EnvContractDiscountInfoEntity
 * @Description: EnvContractDiscountInfo的实体定义
 * @author luhaiqing
 */
@Entity
@Table(name = "t_env_contract_discount_info" )
public class EnvContractDiscountInfoEntity {

	private static final long serialVersionUID = -1627970436049L;


	/**
	 * @Fields id : id
	 */
	@Id
	@GeneratedValue
	@Column(name="id" ,nullable=false ,length=11)
	private Integer id;
	/**
	 * @Fields contractGbId : 一开合同唯一标识
	 */
	@Column(name="contract_gb_id" ,nullable=false ,length=20)
	private Long contractGbId;
	/**
	 * @Fields discountReviewStatus : 折扣审核状态，0->未开始;1->进行中;2->已完成
	 */
	@Column(name="discount_review_status" ,nullable=false ,length=11)
	private Integer discountReviewStatus;
	/**
	 * @Fields reviewGbId : 审核人gbId
	 */
	@Column(name="review_gb_id" ,nullable=false ,length=20)
	private Long reviewGbId;
	/**
	 * @Fields reviewName : 审核人名称
	 */
	@Column(name="review_name" ,nullable=false ,length=20)
	private String reviewName;

	/**
	 * @Fields merchantId : 商户id
	 */
	@Column(name="merchant_id" )
	private Integer merchantId;

	/*=========================getter and setter ===================*/



	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public Long getContractGbId() {
		return this.contractGbId;
	}
	public void setContractGbId(Long contractGbId) {
		this.contractGbId = contractGbId;
	}

	public Integer getDiscountReviewStatus() {
		return this.discountReviewStatus;
	}
	public void setDiscountReviewStatus(Integer discountReviewStatus) {
		this.discountReviewStatus = discountReviewStatus;
	}

	public Long getReviewGbId() {
		return this.reviewGbId;
	}
	public void setReviewGbId(Long reviewGbId) {
		this.reviewGbId = reviewGbId;
	}

	public String getReviewName() {
		return this.reviewName;
	}
	public void setReviewName(String reviewName) {
		this.reviewName = reviewName;
	}

	public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}

	/*====================hashCode and equals ====================*/
	/**
	 * <p>Description: EnvContractDiscountInfoEntity 自动生成的hashcode方法</p>
	 * @author luhaiqing
	 * @date 2021年08月03日  14:00:36
	 * @return
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = 1;
		int prime = 31;
		result = 31 * result+ ((id == null) ? 0 : id.hashCode());
		return result;
	}
	/**
	 * <p>Description:EnvContractDiscountInfoEntity 自动生成的equals方法 </p>
	 * @author luhaiqing
	 * @date 2021年08月03日  14:00:36
	 * @param obj
	 * @return
	 * @see Object#equals
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		EnvContractDiscountInfoEntity other =(EnvContractDiscountInfoEntity) obj;
		if (id == null) {if (other.id != null){ return false;} }else if (!id.equals(other.id)){ return false;}
		return true;
	}

	/*==================== toString() ====================*/
	/**
	 * <p>Description:EnvContractDiscountInfoEntity 自动生成的toString方法 </p>
	 * @author luhaiqing
	 * @date 2021年08月03日  14:00:36
	 * @return
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "EnvContractDiscountInfoEntity [" +
				"id=" + id + "," +
				"contractGbId=" + contractGbId + "," +
				"discountReviewStatus=" + discountReviewStatus + "," +
				"reviewGbId=" + reviewGbId + "," +
				"reviewName=" + reviewName + "," +
		"]";
	}



}
