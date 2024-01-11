/**
 * @Company 全域旅游
 * @Title: EnvSampleSkuSamplingTypeEntity.java
 * @Package com.weipu.sh.erp.sample.entity
 * @author luhaiqing
 * @date 2021年06月15日  13:28:27
 * @version V1.0
 */
package com.sunwayworld.emc.module.erp.bean;
import javax.persistence.*;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

/**
 * @ClassName: EnvSampleSkuSamplingTypeEntity
 * @Description: EnvSampleSkuSamplingType的实体定义
 * @author luhaiqing
 */
@Entity
@Table(name = "t_env_sample_sku_sampling_type" )
public class EnvSampleSkuSamplingTypeEntity {

	private static final long serialVersionUID = -1623734907090L;


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
	 * @Fields orderGbId : 一开订单唯一标识
	 */
	@Column(name="order_gb_id" ,nullable=false ,length=20)
	private Long orderGbId;
	/**
	 * @Fields sampleGbId : 一开样品唯一标识
	 */
	@Column(name="sample_gb_id" ,nullable=false ,length=20)
	private Long sampleGbId;
	/**
	 * @Fields skuGbId : 一开sku唯一标识
	 */
	@Column(name="sku_gb_id" ,nullable=false ,length=20)
	private Long skuGbId;
	/**
	 * @Fields costNo : 二开针对sku添加的采样成本编号
	 */
	@Column(name="cost_no" ,nullable=false ,length=20)
	private Long costNo;
	/**
	 * @Fields samplingCost : 该成本编号对应的价格
	 */
	@Column(name="sampling_cost" ,nullable=false ,length=20)
	private Long samplingCost;
	/**
	 * @Fields needDetected : 是否需要检测 0->不需要;1->需要 默认需要检测
	 */
	@Column(name="need_detected" ,nullable=false ,length=1)
	private Boolean needDetected;
	/**
	 * @Fields belongSkuId : 若为套餐内的单项sku，则记录该sku归属的套餐gbId
	 */
	@Column(name="belong_sku_id"  ,length=20)
	private Long belongSkuId;
	/**
	 * @Fields laboratory : 实验室
	 */
	@Column(name="laboratory"  ,length=64)
	private String laboratory;
	/**
	 * @Fields samplingFee : 采样费
	 */
	@Column(name="sampling_fee"  ,length=20)
	private Long samplingFee;

	/**
	 * @Fields analyzeFee : 分析费
	 */
	@Column(name="analyze_fee"  ,length=20)
	private Long analyzeFee;

	/**
	 * @Fields skuStandardMethodContent : 方法标准
	 */
	@Column(name="sku_standard_method_content" )
	private String skuStandardMethodContent;

	/**
	 * @Fields skuName : sku名称
	 */
	@Column(name="sku_name")
	private String skuName;

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

	public Long getOrderGbId() {
		return this.orderGbId;
	}
	public void setOrderGbId(Long orderGbId) {
		this.orderGbId = orderGbId;
	}

	public Long getSampleGbId() {
		return this.sampleGbId;
	}
	public void setSampleGbId(Long sampleGbId) {
		this.sampleGbId = sampleGbId;
	}

	public Long getSkuGbId() {
		return this.skuGbId;
	}
	public void setSkuGbId(Long skuGbId) {
		this.skuGbId = skuGbId;
	}

	public Long getCostNo() {
		return this.costNo;
	}
	public void setCostNo(Long costNo) {
		this.costNo = costNo;
	}

	public Long getSamplingCost() {
		return this.samplingCost;
	}
	public void setSamplingCost(Long samplingCost) {
		this.samplingCost = samplingCost;
	}

	public Boolean getNeedDetected() {
		return this.needDetected;
	}
	public void setNeedDetected(Boolean needDetected) {
		this.needDetected = needDetected;
	}

	public Long getBelongSkuId() {
		return this.belongSkuId;
	}
	public void setBelongSkuId(Long belongSkuId) {
		this.belongSkuId = belongSkuId;
	}

	public String getLaboratory() {
		return laboratory;
	}

	public void setLaboratory(String laboratory) {
		this.laboratory = laboratory;
	}

	public Long getSamplingFee() {
		return samplingFee;
	}

	public void setSamplingFee(Long samplingFee) {
		this.samplingFee = samplingFee;
	}

	public Long getAnalyzeFee() {
		return analyzeFee;
	}

	public void setAnalyzeFee(Long analyzeFee) {
		this.analyzeFee = analyzeFee;
	}

	public String getSkuStandardMethodContent() {
		return skuStandardMethodContent;
	}

	public void setSkuStandardMethodContent(String skuStandardMethodContent) {
		this.skuStandardMethodContent = skuStandardMethodContent;
	}

	public String getSkuName() {
		return skuName;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}

	/*====================hashCode and equals ====================*/
	/**
	 * <p>Description: EnvSampleSkuSamplingTypeEntity 自动生成的hashcode方法</p>
	 * @author luhaiqing
	 * @date 2021年06月29日  14:14:12
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
	 * <p>Description:EnvSampleSkuSamplingTypeEntity 自动生成的equals方法 </p>
	 * @author luhaiqing
	 * @date 2021年06月29日  14:14:12
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
		EnvSampleSkuSamplingTypeEntity other =(EnvSampleSkuSamplingTypeEntity) obj;
		if (id == null) {if (other.id != null){ return false;} }else if (!id.equals(other.id)){ return false;}
		return true;
	}

	/*==================== toString() ====================*/
	/**
	 * <p>Description:EnvSampleSkuSamplingTypeEntity 自动生成的toString方法 </p>
	 * @author luhaiqing
	 * @date 2021年06月29日  14:14:12
	 * @return
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "EnvSampleSkuSamplingTypeEntity [" +
				"id=" + id + "," +
				"contractGbId=" + contractGbId + "," +
				"orderGbId=" + orderGbId + "," +
				"sampleGbId=" + sampleGbId + "," +
				"skuGbId=" + skuGbId + "," +
				"costNo=" + costNo + "," +
				"samplingCost=" + samplingCost + "," +
				"needDetected=" + needDetected + "," +
				"belongSkuId=" + belongSkuId + "," +
		"]";
	}



}
