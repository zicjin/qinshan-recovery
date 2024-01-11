package com.sunwayworld.emc.module.mdm.crm.bean;

/**
 * @author zhengxy@sunwayworld.com 2021年08月31日
 */
public class CrmOpportunityResponseDTO {

    private Integer code;
    private String message;

    public CrmOpportunityResponseDTO(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static CrmOpportunityResponseDTO ok() {
        return new CrmOpportunityResponseDTO(0, "success");
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CrmOpportunityResponseDTO{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
