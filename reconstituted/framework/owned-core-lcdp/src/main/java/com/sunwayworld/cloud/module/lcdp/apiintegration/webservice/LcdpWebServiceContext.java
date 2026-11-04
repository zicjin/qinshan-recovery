/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.webservice;

import com.sun.xml.ws.transport.http.HttpAdapter;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.service.LcdpApiIntegrationService;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.SoapHelper;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.base.SoapDynamicDTO;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.builder.AbstractSoapDynamicBuilder;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.util.DynamicCreateObjectUtil;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LcdpWebServiceContext {
    private static final Logger log = LoggerFactory.getLogger(LcdpWebServiceContext.class);
    private static Map<String, HttpAdapter> mappings = new ConcurrentHashMap<String, HttpAdapter>();

    public static HttpAdapter getEndpointAdapter(String url) {
        return mappings.get(url);
    }

    public static void endpointAdapterMapping(Map<String, Object> mapping) {
        mapping.forEach((k, v) -> mappings.put((String)k, SoapHelper.createEndpointAdapter(v)));
    }

    public static void removeEndpointAdapter(List<String> mapping) {
        mapping.forEach(serviceId -> mappings.remove(serviceId));
    }

    public static void init() {
        LcdpApiIntegrationService apiIntegrationService = ApplicationContextHelper.getBean(LcdpApiIntegrationService.class);
        List<LcdpApiBean> activatedApiList = apiIntegrationService.selectListByFilter(SearchFilter.instance().match("ACTIVATEDFLAG", "1").filter(MatchPattern.SEQ).match("APITYPE", "WebService").filter(MatchPattern.SEQ).match("CALLTYPE", "outer").filter(MatchPattern.SEQ), new Order[0]);
        HashMap<String, Object> mapping = new HashMap<String, Object>();
        activatedApiList.forEach(activatedApi -> {
            try {
                SoapDynamicDTO soapDynamicDTO = new SoapDynamicDTO((LcdpApiBean)activatedApi);
                AbstractSoapDynamicBuilder.Soap11DynamicBuilder soap11DynamicBuilder = new AbstractSoapDynamicBuilder.Soap11DynamicBuilder(DynamicCreateObjectUtil.class, soapDynamicDTO, activatedApi.getApiCode());
                Class<?> soapClass = soap11DynamicBuilder.createSoapClass();
                mapping.put(String.valueOf(activatedApi.getId()), soapClass.newInstance());
            }
            catch (Exception e) {
                log.error("============>webservice\u52a0\u8f7d\u5f02\u5e38\uff1aapiCode\u4e3a" + activatedApi.getApiCode() + "\uff0c\u5f02\u5e38\u65e5\u5fd7\u5982\u4e0b\uff1a" + e.getMessage());
            }
        });
        LcdpWebServiceContext.endpointAdapterMapping(mapping);
    }
}
