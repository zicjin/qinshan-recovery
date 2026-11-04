package com.sunwayworld.cloud.module.lcdp.apiintegration.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiFieldBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiReqBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiResBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.service.LcdpApiIntegrationService;
import com.sunwayworld.cloud.module.lcdp.apiintegration.service.LcdpApiReqService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.NetworkException;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.http.HttpSimpleClient;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingMessageInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.web.util.UriComponentsBuilder;

public class LcdpApiClientHelper {
   private static final long DEFAULT_CONNECTION_TIMEOUT = 10000L;
   private static final long DEFAULT_RECEIVE_TIMEOUT = 300000L;
   private static HttpClientManager httpClinet = HttpClientManager.getInstance();
   private static ThreadLocal<Boolean> testFlag = new ThreadLocal<>();
   private static LcdpApiIntegrationService lcdpApiIntegrationService = ApplicationContextHelper.getBean(LcdpApiIntegrationService.class);

   public static String sendHttp(String apiCode, String body) {
      return sendHttp(apiCode, null, null, body);
   }

   public static String sendHttp(String apiCode, Map<String, Object> queryParams, Map<String, String> header, String body) {
      LcdpApiBean api = findApi(apiCode);
      String result = null;
      Consumer headerConsumer = parseHeader(api, header);
      boolean isMultipart = validateMultipart(api, header);
      String url = parseUrl(api, queryParams);
      String finalBody = parseBody(api, body);
      LcdpApiReqBean request = new LcdpApiReqBean();
      request.setApiCode(apiCode);
      request.setRequestBody(finalBody);
      request.setRequestDateTime(LocalDateTime.now());
      request.setStatus("0");
      request.setApiId(api.getId());
      LcdpApiResBean insertResponse = new LcdpApiResBean();
      insertResponse.setRequestId(request.getId());

      String var21;
      try {
         String returnValue = api.getRestfulMethod();
         switch (returnValue) {
            case "GET":
               result = httpClinet.sendHttpGet(url, headerConsumer);
               break;
            case "POST":
               result = isMultipart ? sendMultiPart(url, finalBody, headerConsumer) : httpClinet.sendHttpPost(url, finalBody, headerConsumer);
               break;
            case "PUT":
               result = httpClinet.sendHttpPut(url, finalBody, headerConsumer);
               break;
            case "DELETE":
               result = httpClinet.sendHttpDelete(url, headerConsumer);
               break;
            case "PATCH":
               result = httpClinet.sendHttpPatch(url, finalBody, headerConsumer);
               break;
            default:
               throw new CheckedException(I18nHelper.getMessage("LCDP.MODULE.API_INTEGRATION.TIP.REQUEST_METHOD_NOT_SUPPORT", api.getRestfulMethod()));
         }

         returnValue = (String)resultMapping(result, api);
         if (returnValue != null) {
            insertResponse.setStatus("1");
            insertResponse.setResponseBody(returnValue);
         } else {
            insertResponse.setStatus("1");
         }

         var21 = returnValue;
      } catch (Exception var17) {
         insertResponse.setStatus("0");
         insertResponse.setLog(ObjectUtils.getStackTrace(var17));
         throw var17;
      } finally {
         insertResponse.setResponseDateTime(LocalDateTime.now());
         ApplicationContextHelper.getBean(LcdpApiReqService.class).record(request, insertResponse);
      }

      return var21;
   }

   public static Object sendWebService(String apiCode, String operation, Object... args) {
      LcdpApiBean api = findApi(apiCode);
      LcdpApiReqBean request = new LcdpApiReqBean();
      request.setApiCode(apiCode);
      request.setRequestBody(JSON.toJSONString(args));
      request.setRequestDateTime(LocalDateTime.now());
      request.setStatus("0");
      request.setApiId(api.getId());
      LcdpApiResBean insertResponse = new LcdpApiResBean();
      insertResponse.setRequestId(request.getId());

      Object var7;
      try {
         Object returnValue = resultMapping(getData(api.getApiUrl(), operation, args)[0], api);
         if (returnValue != null) {
            insertResponse.setStatus("1");
            insertResponse.setResponseBody(JSON.toJSONString(returnValue));
         } else {
            insertResponse.setStatus("1");
         }

         var7 = returnValue;
      } catch (Exception var11) {
         insertResponse.setStatus("0");
         insertResponse.setLog(ObjectUtils.getStackTrace(var11));
         throw var11;
      } finally {
         insertResponse.setResponseDateTime(LocalDateTime.now());
         ApplicationContextHelper.getBean(LcdpApiReqService.class).record(request, insertResponse);
      }

      return var7;
   }

   private static LcdpApiBean findApi(String apiCode) {
      LcdpApiBean api = lcdpApiIntegrationService.selectApiByCode(apiCode);
      if (ObjectUtils.isEmpty(api)) {
         throw new CheckedException(I18nHelper.getMessage("LCDP.MODULE.API_INTEGRATION.TIP.NOT_EXIST", apiCode));
      } else if (!StringUtils.equals(api.getCallType(), "invoke")) {
         throw new CheckedException(I18nHelper.getMessage("LCDP.MODULE.API_INTEGRATION.TIP.CAN_NOT_INVOKE", apiCode));
      } else if (StringUtils.isBlank(api.getApiUrl())) {
         throw new CheckedException(I18nHelper.getMessage("LCDP.MODULE.API_INTEGRATION.TIP.EMPTY_URL", apiCode));
      } else if (StringUtils.equals(api.getActivatedFlag(), "1") || testFlag.get() != null && testFlag.get()) {
         return api;
      } else {
         throw new CheckedException(I18nHelper.getMessage("LCDP.MODULE.API_INTEGRATION.TIP.CAN_NOT_INVOKE", apiCode));
      }
   }

   private static Object resultMapping(Object result, LcdpApiBean api) {
      if (result instanceof String && JSONValidator.from((String)result).validate()) {
         Object resultJsonObject = JSON.parse((String)result);
         List<LcdpApiFieldBean> lcdpApiFieldList = lcdpApiIntegrationService.selectCacheableApiFieldList(api.getId());
         Map<String, String> fieldMapping = lcdpApiFieldList.stream()
            .collect(Collectors.toMap(LcdpApiFieldBean::getApiField, LcdpApiFieldBean::getSystemField));
         if (resultJsonObject instanceof JSONObject) {
            replaceKeyByMapping((JSONObject)resultJsonObject, fieldMapping);
            return JSONObject.toJSONString(resultJsonObject);
         }

         if (resultJsonObject instanceof JSONArray) {
            ((JSONArray)resultJsonObject).forEach(resultObject -> replaceKeyByMapping(resultObject, fieldMapping));
            return JSONObject.toJSONString(resultJsonObject);
         }
      }

      return result;
   }

   private static void replaceKeyByMapping(Object target, Map<String, String> fieldMapping) {
      if (target instanceof JSONObject) {
         JSONObject targetImpl = (JSONObject)target;
         fieldMapping.forEach((apiField, systemFiled) -> {
            if (targetImpl.containsKey(apiField)) {
               targetImpl.put(systemFiled, targetImpl.remove(apiField));
            }
         });
      }
   }

   private static Consumer<HttpRequestBase> parseHeader(LcdpApiBean api, Map<String, String> header) {
      Object parse = JSON.parse(StringUtils.isBlank(api.getRestfulHeader()) ? "{}" : api.getRestfulHeader());
      if (parse instanceof JSONObject) {
         return request -> {
            ((JSONObject)parse).forEach((k, v) -> request.addHeader(k, v.toString()));
            if (!ObjectUtils.isEmpty(header)) {
               header.forEach((k, v) -> request.addHeader(k, v));
            }
         };
      } else {
         throw new CheckedException("LCDP.MODULE.API_INTEGRATION.RESTFUL_HEADER.TIP.PARSE_ERROR");
      }
   }

   private static String parseUrl(LcdpApiBean api, Map<String, Object> params) {
      String apiUrl = api.getApiUrl();
      UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(apiUrl);
      if (!StringUtils.isBlank(api.getRestfulQueryParam())) {
         JSONObject jsonObject = JSONObject.parseObject(api.getRestfulQueryParam());
         jsonObject.forEach((k, v) -> uriComponentsBuilder.queryParam(k, v));
      }

      if (!ObjectUtils.isEmpty(params)) {
         params.forEach((k, v) -> uriComponentsBuilder.queryParam(k, v));
      }

      return uriComponentsBuilder.build().toString();
   }

   private static String parseBody(LcdpApiBean api, String body) {
      if (!StringUtils.isBlank(api.getRestfulReqBody())) {
         if (StringUtils.isBlank(body)) {
            return api.getRestfulReqBody();
         } else {
            try {
               return jsonMerge(JSONObject.parseObject(body), JSONObject.parseObject(api.getRestfulReqBody())).toJSONString();
            } catch (Exception var3) {
               return body;
            }
         }
      } else {
         return body;
      }
   }

   private static JSONObject jsonMerge(JSONObject source, JSONObject target) {
      if (target == null) {
         return source;
      } else {
         for (String key : source.keySet()) {
            Object value = source.get(key);
            if (!target.containsKey(key)) {
               target.put(key, value);
            } else if (value instanceof JSONObject) {
               JSONObject valueJson = (JSONObject)value;
               JSONObject targetValue = jsonMerge(valueJson, target.getJSONObject(key));
               target.put(key, targetValue);
            } else if (value instanceof JSONArray) {
               JSONArray valueArray = (JSONArray)value;

               for (int i = 0; i < valueArray.size(); i++) {
                  JSONObject obj = (JSONObject)valueArray.get(i);
                  JSONObject targetValue = jsonMerge(obj, (JSONObject)target.getJSONArray(key).get(i));
                  target.getJSONArray(key).set(i, targetValue);
               }
            } else {
               target.put(key, value);
            }
         }

         return target;
      }
   }

   private static final Object[] getData(String wsdlUrl, String operation, Object... args) {
      JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();

      try (Client client = dcf.createClient(new URL(wsdlUrl))) {
         HTTPConduit httpConduit = (HTTPConduit)client.getConduit();
         HTTPClientPolicy policy = new HTTPClientPolicy();
         policy.setConnectionTimeout(10000L);
         policy.setReceiveTimeout(300000L);
         httpConduit.setClient(policy);
         QName qname = getOperateQName(client, operation);
         Object[] realArgs = getRealArgs(client, qname, args);
         return client.invoke(qname, realArgs);
      } catch (Exception var22) {
         throw new NetworkException(var22);
      }
   }

   private static Object[] getRealArgs(Client client, QName bindName, Object[] args) {
      Endpoint endpoint = client.getEndpoint();
      ServiceInfo serviceInfo = endpoint.getService().getServiceInfos().get(0);
      BindingInfo binding = serviceInfo.getBindings().stream().findAny().get();
      BindingOperationInfo boi = binding.getOperation(bindName);
      BindingMessageInfo inputMessageInfo = null;
      if (!boi.isUnwrapped()) {
         inputMessageInfo = boi.getWrappedOperation().getInput();
      } else {
         inputMessageInfo = boi.getUnwrappedOperation().getInput();
      }

      List<MessagePartInfo> parts = inputMessageInfo.getMessageParts();
      Object[] parameters = new Object[parts.size()];

      for (int m = 0; m < parts.size(); m++) {
         MessagePartInfo part = parts.get(m);
         Class<?> partClass = part.getTypeClass();
         System.out.println(partClass.getCanonicalName());
         Object initDomain = null;
         if (!"java.lang.String".equalsIgnoreCase(partClass.getCanonicalName()) && !"int".equalsIgnoreCase(partClass.getCanonicalName())) {
            if (partClass.getCanonicalName().indexOf("[]") > -1) {
               initDomain = JSON.parseArray(args[m].toString(), partClass.getComponentType());
            } else {
               initDomain = JSON.parseObject(args[m].toString(), partClass);
            }
         } else {
            initDomain = args[m].toString();
         }

         parameters[m] = initDomain;
      }

      return parameters;
   }

   private static final QName getOperateQName(Client client, String operation) {
      Endpoint endpoint = client.getEndpoint();
      QName opName = new QName(endpoint.getService().getName().getNamespaceURI(), operation);
      BindingInfo bindingInfo = endpoint.getEndpointInfo().getBinding();
      if (bindingInfo.getOperation(opName) == null) {
         for (BindingOperationInfo operationInfo : bindingInfo.getOperations()) {
            if (operation.equals(operationInfo.getName().getLocalPart())) {
               return operationInfo.getName();
            }
         }
      }

      return opName;
   }

   private static boolean validateMultipart(LcdpApiBean api, Map<String, String> header) {
      Object parse = JSON.parse(StringUtils.isBlank(api.getRestfulHeader()) ? "{}" : api.getRestfulHeader());
      boolean[] isMultipart = new boolean[]{false};
      if (parse instanceof JSONObject) {
         ((JSONObject)parse).forEach((k, v) -> {
            if (StringUtils.equalsIgnoreCase("content-type", k) && StringUtils.containsIgnoreCase(String.valueOf(v), "multipart")) {
               isMultipart[0] = true;
            }
         });
         if (!ObjectUtils.isEmpty(header)) {
            header.forEach((k, v) -> {
               if (StringUtils.equalsIgnoreCase("content-type", k) && StringUtils.containsIgnoreCase(String.valueOf(v), "multipart")) {
                  isMultipart[0] = true;
               }
            });
         }

         return isMultipart[0];
      } else {
         throw new CheckedException("LCDP.MODULE.API_INTEGRATION.RESTFUL_HEADER.TIP.PARSE_ERROR");
      }
   }

   private static String sendMultiPart(String url, String finalBody, Consumer headerConsumer) {
      try (CloseableHttpClient httpClient = HttpSimpleClient.getInstance().getHttpClient()) {
         MultipartEntityBuilder builder = MultipartEntityBuilder.create();
         builder.setCharset(Charset.forName("UTF-8"));
         builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
         finalBody = StringUtils.isBlank(finalBody) ? "{}" : finalBody;
         if (JSONValidator.from(finalBody).validate()) {
            JSONObject jsonObject = JSON.parseObject(finalBody);

            for (Entry<String, Object> entry : jsonObject.entrySet()) {
               builder.addPart(entry.getKey(), new StringBody(String.valueOf(entry.getValue()), ContentType.create("text/plain", Consts.UTF_8)));
            }
         }

         HttpPost httpPost = new HttpPost(url);
         httpPost.setEntity(builder.build());
         if (headerConsumer != null) {
            headerConsumer.accept(httpPost);
         }

         String var10;
         try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity body = response.getEntity();
            var10 = EntityUtils.toString(body, "UTF-8");
         }

         return var10;
      } catch (IOException var39) {
         throw new NetworkException(var39);
      }
   }

   public static void setTestFlag(boolean testFlag) {
      LcdpApiClientHelper.testFlag.set(testFlag);
   }
}
