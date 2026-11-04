/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.service.impl;

import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderDTO;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiOrderQueryParamDTO;
import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiSystemResponseDTO;
import com.sunwayworld.cloud.module.nfssi.order.service.CoreNfssiSystemService;
import com.sunwayworld.cloud.module.nfssi.order.support.NfssiConvertHelper;
import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportBean;
import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportDTO;
import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportItemBean;
import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportItemDTO;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.exception.NetworkException;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreNfssiSystemServiceImpl
implements CoreNfssiSystemService {
    @Value(value="${sunway.nfssi.orgid:}")
    private Long orgId;
    @Value(value="${sunway.nfssi.password:}")
    private String passWord;
    @Value(value="${sunway.nfssi.tokenOverTime:}")
    private Integer tokenOverTime;
    @Value(value="${sunway.nfssi.url.getToken:}")
    private String pullTokenUrl;
    @Value(value="${sunway.nfssi.url.getOrderNo:}")
    private String pullOrderNoUrl;
    @Value(value="${sunway.nfssi.url.getHealthNo:}")
    private String pullHealthNoUrl;
    @Value(value="${sunway.nfssi.url.getFarmNo:}")
    private String pullFarmNoUrl;
    @Value(value="${sunway.nfssi.url.getOrderInfo:}")
    private String pullOrderInfoUrl;
    @Value(value="${sunway.nfssi.url.pushReport:}")
    private String pushReportUrl;

    @Override
    public String getToken() {
        String token = (String)RedisHelper.get("NFSSI_TOKEN", "nfssiToken");
        if (!StringUtils.isEmpty(token)) {
            return token;
        }
        CoreNfssiSystemResponseDTO coreNfssiSystemResponseDTO = this.requestNfssiSystem(this.pullTokenUrl + "?org_id=" + this.orgId + "&password=" + this.passWord, null, null);
        if (coreNfssiSystemResponseDTO.getCode() != 0L) {
            if (!StringUtils.isEmpty(coreNfssiSystemResponseDTO.getMsg())) {
                throw new NetworkException(coreNfssiSystemResponseDTO.getMsg());
            }
            throw new NetworkException("CORE.MODULE.NFSSI.NFSSI_SYSTEM_INTERNAL_ERROR");
        }
        token = coreNfssiSystemResponseDTO.getData();
        RedisHelper.put("NFSSI_TOKEN", "nfssiToken", token, this.tokenOverTime * 3600);
        return token;
    }

    @Override
    public List<String> getOrderList(CoreNfssiOrderQueryParamDTO coreNfssiAddOrderDTO) {
        String sampleType = coreNfssiAddOrderDTO.getSampleType();
        String url = "1".equals(sampleType) ? this.pullOrderNoUrl : ("2".equals(sampleType) ? this.pullHealthNoUrl : this.pullFarmNoUrl);
        CoreNfssiSystemResponseDTO coreNfssiSystemResponseDTO = this.requestNfssiSystem(url = url + "?startDate=" + coreNfssiAddOrderDTO.getReceivedStartTime() + "&endDate=" + coreNfssiAddOrderDTO.getReceivedEndTime(), this.getToken(), null);
        if (coreNfssiSystemResponseDTO.getCode() != 0L) {
            if (!StringUtils.isEmpty(coreNfssiSystemResponseDTO.getMsg())) {
                throw new NetworkException(coreNfssiSystemResponseDTO.getMsg());
            }
            throw new NetworkException("CORE.MODULE.NFSSI.NFSSI_SYSTEM_INTERNAL_ERROR");
        }
        List<CoreNfssiOrderDTO> coreNfssiOrderDTOList = JsonUtils.parseList(coreNfssiSystemResponseDTO.getData(), CoreNfssiOrderDTO.class);
        List<String> orderIdList = coreNfssiOrderDTOList.stream().map(m -> m.getSp_s_16()).collect(Collectors.toList());
        return orderIdList;
    }

    @Override
    public CoreNfssiOrderDTO getOrderInfoById(String orderId) {
        String url = this.pullOrderInfoUrl + "?sampleNo=" + orderId + "&withoutItem=0";
        CoreNfssiSystemResponseDTO coreNfssiSystemResponseDTO = this.requestNfssiSystem(url, this.getToken(), null);
        if (coreNfssiSystemResponseDTO.getCode() != 0L) {
            if (!StringUtils.isEmpty(coreNfssiSystemResponseDTO.getMsg())) {
                throw new NetworkException(coreNfssiSystemResponseDTO.getMsg());
            }
            throw new NetworkException("CORE.MODULE.NFSSI.NFSSI_SYSTEM_INTERNAL_ERROR");
        }
        CoreNfssiOrderDTO coreNfssiOrderDTO = NfssiConvertHelper.paraseCoreNfssiOrderDTO(coreNfssiSystemResponseDTO.getData());
        return coreNfssiOrderDTO;
    }

    @Override
    public CoreNfssiSystemResponseDTO pushReport(CoreNfssiReportBean coreNfssiReport, List<CoreNfssiReportItemBean> coreNfssiReportItemList) {
        CoreNfssiReportDTO coreNfssiReportDTO = new CoreNfssiReportDTO();
        ArrayList<CoreNfssiReportItemDTO> coreNfssiReportItemDTOList = new ArrayList<CoreNfssiReportItemDTO>();
        NfssiConvertHelper.convert(coreNfssiReport, coreNfssiReportDTO);
        coreNfssiReportItemList.forEach(e -> {
            CoreNfssiReportItemDTO coreNfssiReportItemDTO = new CoreNfssiReportItemDTO();
            NfssiConvertHelper.convert(e, coreNfssiReportItemDTO);
            coreNfssiReportItemDTOList.add(coreNfssiReportItemDTO);
        });
        coreNfssiReportDTO.setTestItemInfo(coreNfssiReportItemDTOList);
        String json = JsonUtils.toJsonString(coreNfssiReportDTO);
        try {
            json = URLEncoder.encode(json, "UTF-8");
        }
        catch (UnsupportedEncodingException e1) {
            throw new UnexpectedException("CORE.MODULE.NFSSI.URL_ENCODE_ERROR");
        }
        String url = this.pushReportUrl + "?json=" + json;
        return this.requestNfssiSystem(url, this.getToken(), null);
    }

    private CoreNfssiSystemResponseDTO requestNfssiSystem(String url, String token, String body) {
        this.limitRequestTime(url);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.header("Content-Type", "application/json");
        requestBuilder.header("Accept", "application/json");
        if (!StringUtils.isEmpty(token)) {
            requestBuilder.header("token", token);
        }
        requestBuilder.url(url);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), StringUtils.isEmpty(body) ? "{}" : body);
        requestBuilder.method("POST", requestBody);
        Request request = requestBuilder.build();
        Response response = null;
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        OkHttpClient client = clientBuilder.build();
        client = clientBuilder.build();
        try {
            response = client.newCall(request).execute();
        }
        catch (IOException e) {
            throw new NetworkException("CORE.MODULE.NFSSI.FAILED_CONNECT_WITH_NFSSI_SYSTEM");
        }
        String responseJson = null;
        try {
            responseJson = new String(response.body().bytes());
            return JsonUtils.parse(responseJson, CoreNfssiSystemResponseDTO.class);
        }
        catch (IOException e) {
            throw new NetworkException("CORE.MODULE.NFSSI.NFSSI_SYSTEM_INTERNAL_ERROR");
        }
    }

    private void limitRequestTime(String url) {
        String key = null;
        long limitTime = 0L;
        if (url.contains(this.pullOrderNoUrl)) {
            key = "pullOrderNo";
            limitTime = 50L;
        } else if (url.contains(this.pullHealthNoUrl)) {
            key = "pullHealthNo";
            limitTime = 50L;
        } else if (url.contains(this.pullFarmNoUrl)) {
            key = "pullFarmNo";
            limitTime = 50L;
        } else if (url.contains(this.pullOrderInfoUrl)) {
            key = "pullOrderInfo";
            limitTime = 50L;
        } else if (url.contains(this.pushReportUrl)) {
            key = "pushReport";
            limitTime = 30L;
        } else {
            return;
        }
        GikamConcurrentLocker.block("NFSSI_REQUEST_LIMIT" + key);
        LinkedList<Long> requestTimeList = (LinkedList<Long>)RedisHelper.get("NFSSI_REQUEST_LIMIT", key);
        if (requestTimeList == null) {
            requestTimeList = new LinkedList<Long>();
        }
        Long curTime = System.currentTimeMillis();
        if ((long)requestTimeList.size() >= limitTime) {
            Long preTime = (Long)requestTimeList.poll();
            long waitTime = 59000L - (curTime - preTime);
            if (waitTime > 0L) {
                try {
                    Thread.sleep(waitTime);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
        }
        requestTimeList.offer(curTime);
        RedisHelper.put("NFSSI_REQUEST_LIMIT", key, requestTimeList, 60);
    }
}
