package com.sunwayworld.emc.module.mdm.equipt.equipt.bean;

public class PfEquiptUserDTO {

    private String equiptNo; // 站内编号

    private String equiptName; // 仪器名称

    private String specification; // 规格型号

    private String users;// 使用人员

	public String getEquiptNo() {
		return equiptNo;
	}

	public void setEquiptNo(String equiptNo) {
		this.equiptNo = equiptNo;
	}

	public String getEquiptName() {
		return equiptName;
	}

	public void setEquiptName(String equiptName) {
		this.equiptName = equiptName;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}
    
    
}
