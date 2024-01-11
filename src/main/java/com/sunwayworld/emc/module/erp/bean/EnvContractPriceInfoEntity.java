/**
 * @Company 全域旅游
 * @Title: EnvContractPriceInfoEntity.java
 * @Package com.weipu.sh.erp.sample.entity
 * @author luhaiqing
 * @date 2021年08月03日  14:01:16
 * @version V1.0
 */
package com.sunwayworld.emc.module.erp.bean;
import javax.persistence.*;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

/**
 * @ClassName: EnvContractPriceInfoEntity
 * @Description: EnvContractPriceInfo的实体定义
 * @author luhaiqing
 */
@Entity
@Table(name = "t_env_contract_price_info" )
public class EnvContractPriceInfoEntity {

	private static final long serialVersionUID = -1627970476627L;

	/**
	 * @Fields id :
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
	 * @Fields analyzeSamplingFee : 该合同采样分析费总计，单位分
	 */
	@Column(name="analyze_sampling_fee" ,nullable=false ,length=20)
	private Long analyzeSamplingFee;
	/**
	 * @Fields discountAnalyzeSamplingFee : 销售修改的采样分析费折扣价，默认等于采样费与分析费的加和，销售可以修改变更
	 */
	@Column(name="discount_analyze_sampling_fee" ,nullable=false ,length=20)
	private Long discountAnalyzeSamplingFee;
	/**
	 * @Fields discount : 折扣系数
	 */
	@Column(name="discount" ,nullable=false )
	private java.math.BigDecimal discount;
	/**
	 * @Fields contractAmount : 合同金额，单位分
	 */
	@Column(name="contract_amount" ,nullable=false ,length=20)
	private Long contractAmount;

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

	public Long getAnalyzeSamplingFee() {
		return this.analyzeSamplingFee;
	}
	public void setAnalyzeSamplingFee(Long analyzeSamplingFee) {
		this.analyzeSamplingFee = analyzeSamplingFee;
	}

	public Long getDiscountAnalyzeSamplingFee() {
		return this.discountAnalyzeSamplingFee;
	}
	public void setDiscountAnalyzeSamplingFee(Long discountAnalyzeSamplingFee) {
		this.discountAnalyzeSamplingFee = discountAnalyzeSamplingFee;
	}

	public java.math.BigDecimal getDiscount() {
		return this.discount;
	}
	public void setDiscount(java.math.BigDecimal discount) {
		this.discount = discount;
	}

	public Long getContractAmount() {
		return this.contractAmount;
	}
	public void setContractAmount(Long contractAmount) {
		this.contractAmount = contractAmount;
	}

	public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}

	/*====================hashCode and equals ====================*/
	/**
	 * <p>Description: EnvContractPriceInfoEntity 自动生成的hashcode方法</p>
	 * @author luhaiqing
	 * @date 2021年08月03日  14:01:16
	 * @return
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = 1;
		final int prime = 31;
		result = prime * result+ ((id == null) ? 0 : id.hashCode());
		return result;
	}
	/**
	 * <p>Description:EnvContractPriceInfoEntity 自动生成的equals方法 </p>
	 * @author luhaiqing
	 * @date 2021年08月03日  14:01:16
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
		EnvContractPriceInfoEntity other =(EnvContractPriceInfoEntity) obj;
		if (id == null) {if (other.id != null){ return false;} }else if (!id.equals(other.id)){ return false;}
		return true;
	}

	/*==================== toString() ====================*/
	/**
	 * <p>Description:EnvContractPriceInfoEntity 自动生成的toString方法 </p>
	 * @author luhaiqing
	 * @date 2021年08月03日  14:01:16
	 * @return
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "EnvContractPriceInfoEntity [" +
				"id=" + id + "," +
				"contractGbId=" + contractGbId + "," +
				"analyzeSamplingFee=" + analyzeSamplingFee + "," +
				"discountAnalyzeSamplingFee=" + discountAnalyzeSamplingFee + "," +
				"discount=" + discount + "," +
				"contractAmount=" + contractAmount + "," +
		"]";
	}



}
