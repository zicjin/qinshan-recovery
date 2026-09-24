/**
 * @Company 全域旅游
 * @Title: EnvOrderOtherFeeInfoEntity.java
 * @Package com.weipu.sh.erp.sample.entity
 * @author luhaiqing
 * @date 2021年06月08日  14:19:53
 * @version V1.0
 */
package com.sunwayworld.emc.module.erp.bean;
import javax.persistence.*;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

/**
 * @ClassName: EnvOrderOtherFeeInfoEntity
 * @Description: EnvOrderOtherFeeInfo的实体定义
 * @author luhaiqing
 */
@Entity
@Table(name = "t_env_order_other_fee_info" )
public class EnvOrderOtherFeeInfoEntity {

	private static final long serialVersionUID = -1623133193434L;


	/**
	 * @Fields id : id
	 */
	@Id
	@GeneratedValue
	@Column(name="id" ,nullable=false ,length=11)
	private Integer id;

	@Column(name="contract_gb_id" ,nullable=false ,length=20)
	private Long contractGbId;
	/**
	 * @Fields orderGbId : 一开订单唯一标识
	 */
	@Column(name="order_gb_id" ,nullable=false ,length=20)
	private Long orderGbId;
	/**
	 * @Fields feeType : 费用类型 1->人天费(苏州大市内200元/人/天，苏州大市外300元/人/天);2->交通费(苏州大市内200元/天，苏州大市外300元/天);3->现场勘查费;4->设备租赁费;5->加急费;6->专家费;7->咨询费;8->其他
	 */
	@Column(name="fee_type" ,nullable=false ,length=11)
	private Integer feeType;

	/**
	 * @Fields feeName : 费用名称
	 */
	@Column(name="fee_name" ,length=64)
	private String feeName;
	/**
	 * @Fields feeCount : 数量
	 */
	@Column(name="fee_count" ,nullable=false ,length=11)
	private Integer feeCount;
	/**
	 * @Fields feeType : 单价
	 */
	@Column(name="fee_single_price" ,nullable=false ,length=11)
	private Long feeSinglePrice;
	/**
	 * @Fields amount : 该条费用类型对应的费用金额，单位分
	 */
	@Column(name="amount" ,nullable=false ,length=20)
	private Long amount;

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
		return contractGbId;
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

	public Integer getFeeType() {
		return this.feeType;
	}
	public void setFeeType(Integer feeType) {
		this.feeType = feeType;
	}

	public Long getAmount() {
		return this.amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Integer getFeeCount() {
		return feeCount;
	}

	public void setFeeCount(Integer feeCount) {
		this.feeCount = feeCount;
	}

	public Long getFeeSinglePrice() {
		return feeSinglePrice;
	}

	public void setFeeSinglePrice(Long feeSinglePrice) {
		this.feeSinglePrice = feeSinglePrice;
	}

	public String getFeeName() {
		return feeName;
	}

	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}

	public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}

	/*====================hashCode and equals ====================*/
	/**
	 * <p>Description: EnvOrderOtherFeeInfoEntity 自动生成的hashcode方法</p>
	 * @author luhaiqing
	 * @date 2021年06月08日  14:19:53
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
	 * <p>Description:EnvOrderOtherFeeInfoEntity 自动生成的equals方法 </p>
	 * @author luhaiqing
	 * @date 2021年06月08日  14:19:53
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
		EnvOrderOtherFeeInfoEntity other =(EnvOrderOtherFeeInfoEntity) obj;
		if (id == null) {if (other.id != null){ return false;} }else if (!id.equals(other.id)){ return false;}
		return true;
	}

	/*==================== toString() ====================*/
	/**
	 * <p>Description:EnvOrderOtherFeeInfoEntity 自动生成的toString方法 </p>
	 * @author luhaiqing
	 * @date 2021年06月08日  14:19:53
	 * @return
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "EnvOrderOtherFeeInfoEntity [" +
				"id=" + id + "," +
				"orderGbId=" + orderGbId + "," +
				"feeType=" + feeType + "," +
				"amount=" + amount + "," +
		"]";
	}



}
