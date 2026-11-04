/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.base;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.util.JsonToSoapEntityUtil;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class SoapDynamicDTO
implements Serializable {
    private static final long serialVersionUID = -4093559938608266869L;
    private String version;
    private WebService webService;
    private Map<String, String> paramConfig;
    private List<Map<String, String>> entityConfig;
    private Long serviceId;

    public SoapDynamicDTO() {
    }

    public SoapDynamicDTO(LcdpApiBean api) {
        this.serviceId = Long.valueOf(api.getId() + "" + api.getEffectVersion());
        SoapDynamicDTO soapDynamicDTO = (SoapDynamicDTO)JSON.parseObject(api.getSoapConfig(), this.getClass());
        BeanUtils.copyProperties((Object)soapDynamicDTO, (Object)this, "serviceId");
        this.webService.setName(api.getApiName());
        HashMap<String, String> param = new HashMap<String, String>();
        if (!ObjectUtils.isEmpty(this.entityConfig)) {
            this.entityConfig.forEach(entity -> param.put((String)entity.get("entityName"), (String)entity.get("entityExample")));
            this.setParamConfig(param);
        }
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public WebService getWebService() {
        return this.webService;
    }

    public void setWebService(WebService webService) {
        this.webService = webService;
    }

    public Long getServiceId() {
        return this.serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Map<String, String> getParamConfig() {
        return this.paramConfig;
    }

    public void setParamConfig(Map<String, String> paramConfig) {
        this.paramConfig = CollectionUtils.isEmpty(paramConfig) ? paramConfig : paramConfig.entrySet().stream().collect(Collectors.toMap(k -> {
            if (StringUtils.isEmpty((CharSequence)k.getKey()) || !((String)k.getKey()).matches("^[a-zA-Z]*")) {
                throw new ApplicationRuntimeException("LCDP.MODULE.API_INTEGRATION.TIP.SOAP_DYNAMIC_ENTITY_NOT_ALLOWED", (String)k.getKey());
            }
            return JsonToSoapEntityUtil.getFirstUppercase((String)k.getKey());
        }, Map.Entry::getValue));
    }

    public List<Map<String, String>> getEntityConfig() {
        return this.entityConfig;
    }

    public void setEntityConfig(List<Map<String, String>> entityConfig) {
        this.entityConfig = entityConfig;
    }

    public static class WebParam
    extends WebResult
    implements Serializable {
        private static final long serialVersionUID = -1248520557839258484L;
        @NotNull
        private String paramDefinition;
        @NotNull
        private String paramType;
        private Boolean arrayFlag = Boolean.FALSE;

        public String getParamDefinition() {
            return this.paramDefinition;
        }

        public void setParamDefinition(String paramDefinition) {
            this.paramDefinition = !paramDefinition.contains(".") ? JsonToSoapEntityUtil.getFirstUppercase(paramDefinition) : paramDefinition;
        }

        public String getParamType() {
            return this.paramType;
        }

        public void setParamType(String paramType) {
            this.paramType = paramType;
        }

        public Boolean getArrayFlag() {
            return this.arrayFlag;
        }

        public void setArrayFlag(Boolean arrayFlag) {
            this.arrayFlag = arrayFlag;
        }
    }

    public static class WebResult
    implements Serializable {
        private static final long serialVersionUID = -6711738994278614452L;
        private String name = "";
        private String partName = "";
        private String targetNamespace = "";
        private Boolean header = Boolean.FALSE;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPartName() {
            return this.partName;
        }

        public void setPartName(String partName) {
            this.partName = partName;
        }

        public String getTargetNamespace() {
            return this.targetNamespace;
        }

        public void setTargetNamespace(String targetNamespace) {
            this.targetNamespace = targetNamespace;
        }

        public Boolean getHeader() {
            return this.header;
        }

        public void setHeader(Boolean header) {
            this.header = header;
        }
    }

    public static class WebMethod
    implements Serializable {
        private static final long serialVersionUID = 8341719709332060095L;
        @NotNull
        private String operationName = "";
        private String action = "";
        private Boolean exclude = Boolean.FALSE;
        private WebParam webResult;
        private List<WebParam> webParam;

        public String getOperationName() {
            return this.operationName;
        }

        public void setOperationName(String operationName) {
            if (StringUtils.isEmpty(operationName) || !operationName.matches("^[a-zA-Z]*")) {
                throw new ApplicationRuntimeException("LCDP.MODULE.API_INTEGRATION.TIP.SOAP_DYNAMIC_METHOD_NOT_ALLOWED", operationName);
            }
            this.operationName = operationName;
        }

        public String getAction() {
            return this.action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public Boolean isExclude() {
            return this.exclude;
        }

        public void setExclude(Boolean exclude) {
            this.exclude = exclude;
        }

        public WebParam getWebResult() {
            return this.webResult;
        }

        public void setWebResult(WebParam webResult) {
            this.webResult = webResult;
        }

        public List<WebParam> getWebParam() {
            return this.webParam;
        }

        public void setWebParam(List<WebParam> webParam) {
            this.webParam = webParam;
        }
    }

    public static class WebService
    implements Serializable {
        private static final long serialVersionUID = -2934400999433853254L;
        private String name = "";
        @NotNull
        private String targetNamespace = "";
        @NotNull
        private String serviceName = "";
        private String portName = "";
        private String wsdlLocation = "";
        private String endpointInterface = "";
        @NotNull
        private WebMethod webMethod;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTargetNamespace() {
            return this.targetNamespace;
        }

        public void setTargetNamespace(String targetNamespace) {
            this.targetNamespace = targetNamespace;
        }

        public String getServiceName() {
            return this.serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getPortName() {
            return this.portName;
        }

        public void setPortName(String portName) {
            this.portName = portName;
        }

        public String getWsdlLocation() {
            return this.wsdlLocation;
        }

        public void setWsdlLocation(String wsdlLocation) {
            this.wsdlLocation = wsdlLocation;
        }

        public String getEndpointInterface() {
            return this.endpointInterface;
        }

        public void setEndpointInterface(String endpointInterface) {
            this.endpointInterface = endpointInterface;
        }

        public WebMethod getWebMethod() {
            return this.webMethod;
        }

        public void setWebMethod(WebMethod webMethod) {
            this.webMethod = webMethod;
        }
    }
}
