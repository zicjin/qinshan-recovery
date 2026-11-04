/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.remotecall.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallConfigBean;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallRequestBean;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallResponseBean;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallResponseDTO;
import com.sunwayworld.module.sys.remotecall.dao.CoreRemoteCallRequestDao;
import com.sunwayworld.module.sys.remotecall.exception.CoreRemoteCallException;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallConfigService;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallRequestService;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallResponseService;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreRemoteCallRequestServiceImpl
implements CoreRemoteCallRequestService {
    @Autowired
    private CoreRemoteCallRequestDao remoteCallRequestDao;
    @Lazy
    @Autowired
    private CoreRemoteCallConfigService remoteCallConfigService;
    @Lazy
    @Autowired
    private CoreRemoteCallResponseService remoteCallResponseService;
    @Lazy
    @Autowired
    private CoreRemoteCallRequestService proxyInstance;

    @Override
    public CoreRemoteCallRequestDao getDao() {
        return this.remoteCallRequestDao;
    }

    @Override
    @Transactional
    public Long insert(String remoteCallConfigId, String requestData) {
        CoreRemoteCallRequestBean request = new CoreRemoteCallRequestBean();
        request.setId(ApplicationContextHelper.getNextIdentity());
        request.setConfigId(remoteCallConfigId);
        request.setRequestData(requestData);
        request.setRequestDateTime(LocalDateTime.now());
        request.setStatus("0");
        this.getDao().insert(request);
        return request.getId();
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public Long insertInNewTransaction(String remoteCallConfigId, String requestData) {
        CoreRemoteCallRequestBean request = new CoreRemoteCallRequestBean();
        request.setId(ApplicationContextHelper.getNextIdentity());
        request.setConfigId(remoteCallConfigId);
        request.setRequestData(requestData);
        request.setRequestDateTime(LocalDateTime.now());
        request.setStatus("0");
        this.getDao().insert(request);
        return request.getId();
    }

    @Override
    @Transactional
    public void processAsyncRequest(String remoteCallConfigId, String requestData) {
        this.proxyInstance.insert(remoteCallConfigId, requestData);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public String processSyncRequest(String remoteCallConfigId, String requestData) {
        Long requestId = this.proxyInstance.insertInNewTransaction(remoteCallConfigId, requestData);
        CoreRemoteCallRequestBean request = (CoreRemoteCallRequestBean)this.getDao().selectById(requestId);
        return this.doProcessRequest(request);
    }

    @Override
    @Transactional
    public <T> void processAsyncRequest(String remoteCallConfigId, T requestData) {
        String requestDataStr = this.convertToString(requestData);
        this.proxyInstance.insert(remoteCallConfigId, requestDataStr);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public <T, R> R processSyncRequest(String remoteCallConfigId, T requestData, Class<R> rClass) {
        String requestDataStr = this.convertToString(requestData);
        Long requestId = this.proxyInstance.insertInNewTransaction(remoteCallConfigId, requestDataStr);
        CoreRemoteCallRequestBean request = (CoreRemoteCallRequestBean)this.getDao().selectById(requestId);
        String processRequest = this.doProcessRequest(request);
        return JsonUtils.parse(processRequest, rClass);
    }

    @Override
    @Transactional
    public void processAsyncRequest(CoreRemoteCallRequestBean request) {
        this.doProcessRequest(request);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void record(CoreRemoteCallRequestBean request, CoreRemoteCallResponseBean insertResponse) {
        CoreRemoteCallConfigBean config = (CoreRemoteCallConfigBean)this.remoteCallConfigService.selectById(request.getConfigId());
        if ("0".equals(insertResponse.getStatus())) {
            request.setFailedCount(request.getFailedCount() + 1L);
            if ("0".equals(config.getSynch()) && (long)ApplicationContextHelper.getEnvironment().getProperty("sunway.remote-call.max-retries", Integer.class, 5).intValue() > request.getFailedCount()) {
                request.setStatus("0");
            } else {
                request.setStatus("-1");
            }
            this.getDao().update(request, new String[]{"FAILEDCOUNT", "STATUS"});
        } else {
            request.setStatus("1");
            this.getDao().update(request, new String[]{"FAILEDCOUNT", "STATUS"});
        }
        insertResponse.setId(ApplicationContextHelper.getNextIdentity());
        insertResponse.setRequestId(request.getId());
        this.remoteCallResponseService.getDao().insert((CoreRemoteCallResponseBean)insertResponse);
    }

    @Override
    @Transactional
    public void reset(RestJsonWrapperBean wrapper) {
        String synch = wrapper.getParamValue("synch");
        List<CoreRemoteCallRequestBean> coreRemoteCallRequestList = wrapper.parse(CoreRemoteCallRequestBean.class);
        if (!coreRemoteCallRequestList.isEmpty()) {
            coreRemoteCallRequestList.stream().forEach(e -> {
                e.setStatus("0");
                e.setFailedCount(0L);
            });
        }
        if ("1".equals(synch)) {
            coreRemoteCallRequestList.forEach(e -> this.doProcessRequest((CoreRemoteCallRequestBean)e));
        } else if (!coreRemoteCallRequestList.isEmpty()) {
            this.getDao().update(coreRemoteCallRequestList, new String[]{"FAILEDCOUNT", "STATUS"});
        }
    }

    private String doProcessRequest(CoreRemoteCallRequestBean request) {
        GikamConcurrentLocker.isolated("CORE_REMOTE_CALL_REQ$" + request.getId());
        CoreRemoteCallConfigBean config = (CoreRemoteCallConfigBean)this.remoteCallConfigService.selectById(request.getConfigId());
        if (config.getLastSuspendedFlag().equals("1") || request.getStatus().equals("1")) {
            return null;
        }
        CoreRemoteCallResponseBean insertResponse = new CoreRemoteCallResponseBean();
        insertResponse.setRequestId(request.getId());
        try {
            Object returnValue;
            Class callClass = ClassUtils.getClass(config.getCallClass());
            Method callMethod = ReflectionUtils.getUniqueMethodByName(callClass, config.getCallMethod());
            if (callMethod == null) {
                String string = null;
                return string;
            }
            Parameter[] parameters = callMethod.getParameters();
            int qty = parameters.length;
            Object[] args = new Object[parameters.length];
            if (qty > 0) {
                if (qty == 1) {
                    args[0] = JsonUtils.parse(request.getRequestData(), parameters[0].getType());
                } else {
                    JSONObject json = JSON.parseObject(request.getRequestData());
                    for (int i = 0; i < qty; ++i) {
                        Parameter parameter = parameters[i];
                        args[i] = ConvertUtils.convert(json.get(parameter.getName()), parameter.getType());
                    }
                }
            }
            if ((returnValue = ReflectionUtils.invokeMethod(callMethod, ApplicationContextHelper.getBean(callClass), args)) != null) {
                if (returnValue instanceof CoreRemoteCallResponseDTO) {
                    CoreRemoteCallResponseDTO reponseDTO = (CoreRemoteCallResponseDTO)returnValue;
                    insertResponse.setStatus(reponseDTO.getResponseStatus());
                    insertResponse.setResponseData(reponseDTO.getResponseData());
                } else {
                    insertResponse.setStatus("1");
                    insertResponse.setResponseData(this.convertToString(returnValue));
                }
            } else {
                insertResponse.setStatus("1");
            }
            String string = insertResponse.getResponseData();
            return string;
        }
        catch (Exception ex) {
            insertResponse.setStatus("0");
            insertResponse.setLog(ObjectUtils.getStackTrace(ex));
            throw new CoreRemoteCallException(ex);
        }
        finally {
            insertResponse.setResponseDateTime(LocalDateTime.now());
            this.proxyInstance.record(request, insertResponse);
        }
    }

    private <T> String convertToString(T requestData) {
        boolean canConvert = ConvertUtils.canConvert(requestData, String.class);
        String requestDataStr = canConvert ? ConvertUtils.convert(requestData, String.class) : JSON.toJSONString(requestData);
        return requestDataStr;
    }
}
