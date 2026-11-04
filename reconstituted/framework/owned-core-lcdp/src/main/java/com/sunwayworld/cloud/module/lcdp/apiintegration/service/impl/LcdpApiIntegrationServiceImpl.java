/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.sun.xml.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.ws.transport.http.HttpAdapter;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiFieldBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiReqBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiResBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.persistent.dao.LcdpApiDao;
import com.sunwayworld.cloud.module.lcdp.apiintegration.service.LcdpApiFieldService;
import com.sunwayworld.cloud.module.lcdp.apiintegration.service.LcdpApiIntegrationService;
import com.sunwayworld.cloud.module.lcdp.apiintegration.service.LcdpApiReqService;
import com.sunwayworld.cloud.module.lcdp.apiintegration.support.LcdpApiClientHelper;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.LcdpWebServiceContext;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.SoapHelper;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.base.SoapConnection;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.base.SoapDynamicDTO;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.base.SoapRequestUrl;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.builder.AbstractSoapDynamicBuilder;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.util.DynamicCreateObjectUtil;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.util.EsbXmlUtils;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpApiIntegrationServiceImpl
implements LcdpApiIntegrationService {
    @Autowired
    private LcdpApiDao lcdpApiDao;
    @Autowired
    private LcdpApiFieldService lcdpApiFieldService;
    @Autowired
    private LcdpApiReqService lcdpApiReqService;

    @Override
    public LcdpApiDao getDao() {
        return this.lcdpApiDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        LcdpApiBean lcdpApiIntegration = jsonWrapper.parseUnique(LcdpApiBean.class);
        lcdpApiIntegration.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(lcdpApiIntegration);
        return lcdpApiIntegration.getId();
    }

    @Override
    public Page<LcdpApiFieldBean> selectFieldPaginationByApiIntegrationId(Long id, RestJsonWrapperBean wrapper) {
        return this.lcdpApiFieldService.selectPaginationByFilter(SearchFilter.instance().match("APIID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Page<LcdpApiReqBean> selectRequestPaginationByApiIntegrationId(Long id, RestJsonWrapperBean wrapper) {
        return this.lcdpApiReqService.selectPaginationByFilter(SearchFilter.instance().match("APIID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Page<LcdpApiResBean> selectResponsePaginationByApiRequestId(Long id, RestJsonWrapperBean wrapper) {
        return this.lcdpApiReqService.selectResPaginationByApiReqId(id, wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insertField(Long id, RestJsonWrapperBean wrapper) {
        List<LcdpApiFieldBean> lcdpApiFieldList = wrapper.parse(LcdpApiFieldBean.class);
        lcdpApiFieldList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setApiId(id);
        });
        this.lcdpApiFieldService.getDao().insert(lcdpApiFieldList);
        return lcdpApiFieldList.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    public void deleteField(Long id, RestJsonWrapperBean wrapper) {
        this.lcdpApiFieldService.delete(wrapper);
    }

    @Override
    @Cacheable(value={"T_LCDP_API"}, key="#apiCode", unless="#result == null")
    public LcdpApiBean selectApiByCode(String apiCode) {
        List apiList = this.selectListByFilter(SearchFilter.instance().match("APICODE", apiCode).filter(MatchPattern.SEQ), new Order[0]);
        if (apiList.isEmpty()) {
            return null;
        }
        return (LcdpApiBean)apiList.get(0);
    }

    @Override
    @Cacheable(value={"T_LCDP_API_URL"}, key="#url", unless="#result == null")
    public LcdpApiBean selectActivatedApiByUrl(String url) {
        List apiList = this.selectListByFilter(SearchFilter.instance().match("APIURL", url.toUpperCase()).filter(MatchPattern.CISB).match("ACTIVATEDFLAG", "1").filter(MatchPattern.SEQ).match("CALLTYPE", "outer").filter(MatchPattern.SEQ), new Order[0]);
        if (apiList.isEmpty()) {
            return null;
        }
        return (LcdpApiBean)apiList.get(0);
    }

    @Override
    @Cacheable(value={"T_LCDP_API_FIELD"}, key="#apiId", unless="#result == null")
    public List<LcdpApiFieldBean> selectCacheableApiFieldList(Long apiId) {
        return this.lcdpApiFieldService.selectListByFilter(SearchFilter.instance().match("APIID", apiId).filter(MatchPattern.EQ).match("APIFIELD", null).filter(MatchPattern.ISNOTNULL).match("SYSTEMFIELD", null).filter(MatchPattern.ISNOTNULL), new Order[0]);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.ACTIVATE)
    @Audit(value="AUDIT.ACTIVATE")
    public void activate(RestJsonWrapperBean wrapper) {
        LcdpApiIntegrationService.super.activate(wrapper);
        LcdpApiBean parse = wrapper.parseUnique(LcdpApiBean.class);
        LcdpApiBean lcdpApiBean = (LcdpApiBean)this.selectById(parse.getId());
        lcdpApiBean.setEffectVersion(ObjectUtils.isEmpty(lcdpApiBean.getEffectVersion()) ? 1L : lcdpApiBean.getEffectVersion() + 1L);
        if (StringUtils.equals(lcdpApiBean.getApiType(), "WebService") && StringUtils.equals(lcdpApiBean.getCallType(), "outer")) {
            HashMap<String, Object> mapping = new HashMap<String, Object>();
            try {
                SoapDynamicDTO soapDynamicDTO = new SoapDynamicDTO(lcdpApiBean);
                if (!ObjectUtils.isEmpty(soapDynamicDTO.getParamConfig())) {
                    soapDynamicDTO.getParamConfig().values().forEach(config -> {
                        if (!JSONValidator.from(config).validate() && !EsbXmlUtils.isXml(config)) {
                            throw new CheckedException("LCDP.MODULE.API_INTEGRATION.TIP.ENTITY_EXAMPLE_FORMAT");
                        }
                    });
                }
                AbstractSoapDynamicBuilder.Soap11DynamicBuilder soap11DynamicBuilder = new AbstractSoapDynamicBuilder.Soap11DynamicBuilder(DynamicCreateObjectUtil.class, soapDynamicDTO, lcdpApiBean.getApiCode());
                Class<?> soapClass = soap11DynamicBuilder.createSoapClass();
                mapping.put(String.valueOf(lcdpApiBean.getId()), soapClass.newInstance());
            }
            catch (Exception e) {
                throw new CheckedException(e);
            }
            LcdpWebServiceContext.endpointAdapterMapping(mapping);
        }
        this.getDao().update(lcdpApiBean, new String[]{"EFFECTVERSION"});
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DEACTIVATE)
    @Audit(value="AUDIT.DEACTIVATE")
    public void deactivate(RestJsonWrapperBean wrapper) {
        LcdpApiIntegrationService.super.deactivate(wrapper);
        List<LcdpApiBean> parse = wrapper.parse(LcdpApiBean.class);
        List<String> mapping = parse.stream().map(LcdpApiBean::getId).map(String::valueOf).collect(Collectors.toList());
        LcdpWebServiceContext.removeEndpointAdapter(mapping);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    @Audit(value="AUDIT.SAVE")
    public void resetRequest(Long id) {
        this.lcdpApiReqService.reset(id);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    @Audit(value="AUDIT.SAVE")
    public Object testApi(String apiCode, JSONObject paramMap) {
        LcdpApiBean api = this.selectApiByCode(apiCode);
        String body = paramMap.getString("body");
        String operation = paramMap.getString("operation");
        JSONArray args = paramMap.getJSONArray("args");
        try {
            LcdpApiClientHelper.setTestFlag(true);
            if (StringUtils.equals("invoke", api.getCallType())) {
                if (StringUtils.equals("Restful", api.getApiType())) {
                    String string = LcdpApiClientHelper.sendHttp(apiCode, body);
                    return string;
                }
                Object object = LcdpApiClientHelper.sendWebService(apiCode, operation, ObjectUtils.isEmpty(args) ? null : args.toArray());
                return object;
            }
            if (StringUtils.equals("Restful", api.getApiType())) {
                Object object = this.callApiScript(apiCode, body);
                return object;
            }
            Object object = this.callApiScript(apiCode, this.transformJsonToParams(api, args));
            return object;
        }
        catch (Exception exception) {
            throw new CheckedException(ObjectUtils.getStackTrace(exception));
        }
        finally {
            LcdpApiClientHelper.setTestFlag(false);
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    @Audit(value="AUDIT.SAVE")
    public Object callRestfulApi(String url, String requestBody) {
        LcdpApiBean lcdpApiBean = this.selectActivatedApiByUrl(url);
        if (ObjectUtils.isEmpty(lcdpApiBean) || !StringUtils.equals(lcdpApiBean.getApiType(), "Restful")) {
            ServletUtils.responseError(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.name());
            return null;
        }
        return this.callApiScript(lcdpApiBean.getApiCode(), requestBody);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    @Audit(value="AUDIT.SAVE")
    public void callSoapApi(HttpServletRequest request, HttpServletResponse response) throws Exception {
        LcdpApiBean lcdpApiBean = this.selectActivatedApiByUrl(ServletUtils.getRequestUri(request));
        if (ObjectUtils.isEmpty(lcdpApiBean) || !StringUtils.equals(lcdpApiBean.getApiType(), "WebService")) {
            ServletUtils.responseError(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.name());
            return;
        }
        SoapRequestUrl soapRequestUrl = SoapRequestUrl.newInstance(request);
        HttpAdapter endpointAdapter = LcdpWebServiceContext.getEndpointAdapter(String.valueOf(lcdpApiBean.getId()));
        WebServiceContextDelegate delegate = SoapHelper.createDelegate(endpointAdapter, soapRequestUrl);
        SoapConnection connection = new SoapConnection(request, response, soapRequestUrl, delegate);
        if (SoapHelper.isWsdlRequest(soapRequestUrl.queryString)) {
            endpointAdapter.publishWSDL(connection);
        } else {
            endpointAdapter.handle(connection);
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    @Audit(value="AUDIT.SAVE")
    public Object callApiScript(String apiCode, Object ... args) {
        LcdpApiBean api = this.selectApiByCode(apiCode);
        String requestBody = JSON.toJSONString(StringUtils.equals("Restful", api.getApiType()) ? args[0] : ArrayUtils.asList(args));
        LcdpApiReqBean request = new LcdpApiReqBean();
        request.setApiCode(apiCode);
        request.setRequestBody(requestBody);
        request.setRequestDateTime(LocalDateTime.now());
        request.setStatus("0");
        request.setApiId(api.getId());
        request.setNotifyFlag("0");
        if (ObjectUtils.isEmpty(api) || !StringUtils.equals(api.getCallType(), "outer")) {
            throw new CheckedException(I18nHelper.getMessage("LCDP.MODULE.API_INTEGRATION.TIP.NOT_EXIST", apiCode));
        }
        return this.lcdpApiReqService.doProcessRequest(request, args);
    }

    @Override
    public RestValidationResultBean validateUrl(Long id, RestJsonWrapperBean wrapper) {
        List apiBeanList;
        int index;
        String url = wrapper.getParamMap().get("url");
        if (!StringUtils.isEmpty(url) && (index = url.indexOf(63)) != -1) {
            url = url.substring(0, index);
        }
        if ((apiBeanList = this.selectListByFilter(SearchFilter.instance().match("ID", id).filter(MatchPattern.DIFFER).match("ACTIVATEDFLAG", "1").filter(MatchPattern.SEQ).match("APIURL", url.toUpperCase()).filter(MatchPattern.CISB), new Order[0])).isEmpty()) {
            return new RestValidationResultBean(true);
        }
        return new RestValidationResultBean(false, I18nHelper.getMessage("core.validator.column.not-unique", new String[0]));
    }

    private Map<String, Object> transformJsonToParams(LcdpApiBean api, JSONArray args) {
        SoapDynamicDTO soapDynamicDTO = new SoapDynamicDTO(api);
        List<SoapDynamicDTO.WebParam> webParamList = soapDynamicDTO.getWebService().getWebMethod().getWebParam();
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        for (int i = 0; i < webParamList.size(); ++i) {
            parameters.put(webParamList.get(i).getName(), args.get(i));
        }
        return parameters;
    }
}
