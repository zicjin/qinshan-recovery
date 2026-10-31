package com.sunwayworld.emc.module.mdm.crm.bean;


import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 核销表
* @author licm@sunwayworld.com 2022年02月18日
 */
@Table("T_CRM_WRITEOFF")
public class CrmWriteOffBean extends AbstractInsertable<String> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8642341239747504634L;
	/**
	 * 
	 */
	private String id;
    private String projid;
    private String type;// 核销类型
    private Double writeoffamount;// 核销金额
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getProjid() {
		return projid;
	}
	public void setProjid(String projid) {
		this.projid = projid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public  Double getWriteoffamount() {
		return writeoffamount;
	}
	public void setWriteoffamount(Double writeoffamount) {
		this.writeoffamount = writeoffamount;
	}



}
