/**
 * @Company 全域旅游
 * @Title: EnvOrderExtraInfoEntity.java
 * @Package com.weipu.sh.erp.sample.entity
 * @author luhaiqing
 * @date 2021年06月08日  14:19:33
 * @version V1.0
 */
package com.sunwayworld.emc.module.erp.bean;
import javax.persistence.*;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

/**
 * @ClassName: EnvOrderExtraInfoEntity
 * @Description: EnvOrderExtraInfo的实体定义
 * @author luhaiqing
 */
@Entity
@Table(name = "t_env_order_extra_info" )
public class EnvOrderExtraInfoEntity {

	private static final long serialVersionUID = -1623133173320L;


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
	@Column(name="contract_gb_id" ,length=20)
	private Long contractGbId;
	/**
	 * @Fields orderGbId : 一开订单唯一标识
	 */
	@Column(name="order_gb_id" ,nullable=false ,length=20)
	private Long orderGbId;
	/**
	 * @Fields relateOut : 一开与二开关联字段
	 */
	@Column(name="relate_out" ,nullable=false ,length=20)
	private Long relateOut;
	/**
	 * @Fields samplingType : 采样类型 1->采样;2->送样
	 */
	@Column(name="sampling_type" ,nullable=false ,length=2)
	private Integer samplingType;
	@Column(name="sampling_status" ,length=2)
	private Integer samplingStatus;
	/**
	 * @Fields hasSendFinishRecord : 是否存在送检完毕采样记录 0->不存在;1->存在
	 */
	@Column(name="has_send_finish_record",length=2)
	private Integer hasSendFinishRecord;
	/**
	 * @Fields hasReceiveFinishRecord : 是否存在接样完毕采样记录 0->不存在;1->存在
	 */
	@Column(name="has_receive_finish_record",length=2)
	private Integer hasReceiveFinishRecord;
	/**
	 * @Fields reportType : 报告类型 1->普通检测数据报告;2->验收监测报告;3->其他
	 */
	@Column(name="report_type" ,nullable=false ,length=2)
	private Integer reportType;
	/**
	 * @Fields reportFee : 若报告类型为其他，需要手动录入报告编制费，费用单位为分
	 */
	@Column(name="report_fee"  ,length=20)
	private Long reportFee;
	/**
	 * @Fields securityLevel : 保密度 1->低;2->中;3->高
	 */
	@Column(name="security_level" ,nullable=false ,length=2)
	private Integer securityLevel;
	/**
	 * @Fields parentIndustryCode : 父级行业群编号
	 */
	@Column(name="parent_industry_code"  ,length=32)
	private String parentIndustryCode;
	/**
	 * @Fields parentIndustryName : 父级行业群名称
	 */
	@Column(name="parent_industry_name"  ,length=32)
	private String parentIndustryName;

	/**
	 * @Fields parentIndustryGbId : 父级行业群唯一标识
	 */
	@Column(name="parent_industry_gb_id"  ,length=32)
	private Long parentIndustryGbId;
	/**
	 * @Fields currentIndustryCode : 中间行业群编号
	 */
	@Column(name="current_industry_code"  ,length=32)
	private String currentIndustryCode;
	/**
	 * @Fields currentIndustryName : 中间行业群名称
	 */
	@Column(name="current_industry_name"  ,length=32)
	private String currentIndustryName;

	/**
	 * @Fields currentIndustryGbId : 中间行业群唯一标识
	 */
	@Column(name="current_industry_gb_id"  ,length=32)
	private Long currentIndustryGbId;
	/**
	 * @Fields childIndustryCode : 子级行业群编号
	 */
	@Column(name="child_industry_code"  ,length=32)
	private String childIndustryCode;
	/**
	 * @Fields childIndustryName : 子级行业群名称
	 */
	@Column(name="child_industry_name"  ,length=32)
	private String childIndustryName;

	/**
	 * @Fields childIndustryName : 子级行业群唯一标识
	 */
	@Column(name="child_industry_gb_id"  ,length=32)
	private Long childIndustryGbId;
	/**
	 * @Fields labNumber : 实验室内部编号
	 */
	@Column(name="lab_number"  ,length=32)
	private String labNumber;
	/**
	 * @Fields workPoint : 订单总工分
	 */
	@Column(name="work_point"  ,length=11)
	private Integer workPoint;

	/**
	 * @Fields laboratory : 该订单对应的所有样品的实验室去重拼接的字符串
	 */
	@Column(name="laboratory")
	private String laboratory;

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

	public Long getRelateOut() {
		return this.relateOut;
	}
	public void setRelateOut(Long relateOut) {
		this.relateOut = relateOut;
	}

	public Integer getSamplingType() {
		return this.samplingType;
	}
	public void setSamplingType(Integer samplingType) {
		this.samplingType = samplingType;
	}

	public Integer getSamplingStatus() {
		return samplingStatus;
	}

	public void setSamplingStatus(Integer samplingStatus) {
		this.samplingStatus = samplingStatus;
	}

	public Integer getHasSendFinishRecord() {
		return hasSendFinishRecord;
	}

	public void setHasSendFinishRecord(Integer hasSendFinishRecord) {
		this.hasSendFinishRecord = hasSendFinishRecord;
	}

	public Integer getHasReceiveFinishRecord() {
		return hasReceiveFinishRecord;
	}

	public void setHasReceiveFinishRecord(Integer hasReceiveFinishRecord) {
		this.hasReceiveFinishRecord = hasReceiveFinishRecord;
	}

	public Integer getReportType() {
		return this.reportType;
	}
	public void setReportType(Integer reportType) {
		this.reportType = reportType;
	}

	public Long getReportFee() {
		return this.reportFee;
	}
	public void setReportFee(Long reportFee) {
		this.reportFee = reportFee;
	}

	public Integer getSecurityLevel() {
		return this.securityLevel;
	}
	public void setSecurityLevel(Integer securityLevel) {
		this.securityLevel = securityLevel;
	}

	public String getParentIndustryCode() {
		return this.parentIndustryCode;
	}
	public void setParentIndustryCode(String parentIndustryCode) {
		this.parentIndustryCode = parentIndustryCode;
	}

	public String getParentIndustryName() {
		return this.parentIndustryName;
	}
	public void setParentIndustryName(String parentIndustryName) {
		this.parentIndustryName = parentIndustryName;
	}

	public String getCurrentIndustryCode() {
		return this.currentIndustryCode;
	}
	public void setCurrentIndustryCode(String currentIndustryCode) {
		this.currentIndustryCode = currentIndustryCode;
	}

	public String getCurrentIndustryName() {
		return this.currentIndustryName;
	}
	public void setCurrentIndustryName(String currentIndustryName) {
		this.currentIndustryName = currentIndustryName;
	}

	public String getChildIndustryCode() {
		return this.childIndustryCode;
	}
	public void setChildIndustryCode(String childIndustryCode) {
		this.childIndustryCode = childIndustryCode;
	}

	public String getChildIndustryName() {
		return this.childIndustryName;
	}
	public void setChildIndustryName(String childIndustryName) {
		this.childIndustryName = childIndustryName;
	}

	public Long getParentIndustryGbId() {
		return parentIndustryGbId;
	}

	public void setParentIndustryGbId(Long parentIndustryGbId) {
		this.parentIndustryGbId = parentIndustryGbId;
	}

	public Long getCurrentIndustryGbId() {
		return currentIndustryGbId;
	}

	public void setCurrentIndustryGbId(Long currentIndustryGbId) {
		this.currentIndustryGbId = currentIndustryGbId;
	}

	public Long getChildIndustryGbId() {
		return childIndustryGbId;
	}

	public void setChildIndustryGbId(Long childIndustryGbId) {
		this.childIndustryGbId = childIndustryGbId;
	}

	public String getLabNumber() {
		return labNumber;
	}

	public void setLabNumber(String labNumber) {
		this.labNumber = labNumber;
	}

	public Integer getWorkPoint() {
		return this.workPoint;
	}
	public void setWorkPoint(Integer workPoint) {
		this.workPoint = workPoint;
	}

	public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}

	public String getLaboratory() {
		return laboratory;
	}

	public void setLaboratory(String laboratory) {
		this.laboratory = laboratory;
	}

	/*====================hashCode and equals ====================*/
	/**
	 * <p>Description: EnvOrderExtraInfoEntity 自动生成的hashcode方法</p>
	 * @author luhaiqing
	 * @date 2021年06月08日  14:19:33
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
	 * <p>Description:EnvOrderExtraInfoEntity 自动生成的equals方法 </p>
	 * @author luhaiqing
	 * @date 2021年06月08日  14:19:33
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
		EnvOrderExtraInfoEntity other =(EnvOrderExtraInfoEntity) obj;
		if (id == null) {if (other.id != null){ return false;} }else if (!id.equals(other.id)){ return false;}
		return true;
	}

	/*==================== toString() ====================*/

	/**
	 * <p>Description:EnvOrderExtraInfoEntity 自动生成的toString方法 </p>
	 *
	 * @return
	 * @author luhaiqing
	 * @date 2021年06月08日  14:19:33
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "EnvOrderExtraInfoEntity{" +
				"id=" + id +
				", contractGbId=" + contractGbId +
				", orderGbId=" + orderGbId +
				", relateOut=" + relateOut +
				", samplingType=" + samplingType +
				", reportType=" + reportType +
				", reportFee=" + reportFee +
				", securityLevel=" + securityLevel +
				", parentIndustryCode='" + parentIndustryCode + '\'' +
				", parentIndustryName='" + parentIndustryName + '\'' +
				", parentIndustryGbId=" + parentIndustryGbId +
				", currentIndustryCode='" + currentIndustryCode + '\'' +
				", currentIndustryName='" + currentIndustryName + '\'' +
				", currentIndustryGbId=" + currentIndustryGbId +
				", childIndustryCode='" + childIndustryCode + '\'' +
				", childIndustryName='" + childIndustryName + '\'' +
				", childIndustryGbID=" + childIndustryGbId +
				", workPoint=" + workPoint +
				'}';
	}


}
