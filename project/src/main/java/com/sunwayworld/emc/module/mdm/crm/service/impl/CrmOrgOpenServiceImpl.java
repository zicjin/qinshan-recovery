package com.sunwayworld.emc.module.mdm.crm.service.impl;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityRequestDTO;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityResponseDTO;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOrgOpenService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;

/**
 * @author zhengxy@sunwayworld.com 2021年11月11日
 */
@Repository
public class CrmOrgOpenServiceImpl implements CrmOrgOpenService {

    private static final Logger logger = LogManager.getLogger(CrmOrgOpenServiceImpl.class);

    private static final String INSERT_OPERATE_TYPE = "insert";
    private static final String UPDATE_OPERATE_TYPE = "update";
    private static final String DELETE_OPERATE_TYPE = "delete";

    @Autowired
    private CoreOrgService orgService;

    @Override
    public CrmOpportunityResponseDTO receiveOrg(RestJsonWrapperBean wrapper) {
        String requestJson = wrapper.getRequestJson();
        CrmOpportunityRequestDTO<CoreOrgBean> coreOrgRequest = JSON.parseObject(requestJson, new TypeReference<CrmOpportunityRequestDTO<CoreOrgBean>>() {});
        logger.info("[Receive-Crm-Org-Info]=======================");
        logger.info(JSON.toJSONString(coreOrgRequest));

        String operateType = coreOrgRequest.getOperType();
        List<CoreOrgBean> dataList = coreOrgRequest.getData();

        switch (operateType) {
            case INSERT_OPERATE_TYPE:
                orgService.getDao().insert(dataList);
                break;
            case UPDATE_OPERATE_TYPE:
                orgService.getDao().update(dataList);
                break;
            case DELETE_OPERATE_TYPE:
                orgService.getDao().deleteBy(dataList, "id");
                break;
            default:
                break;
        }

        return CrmOpportunityResponseDTO.ok();
    }

    @Override
    public void syncOrgInfo(RestJsonWrapperBean wrapper) {
        String crmServerUrl = ApplicationContextHelper.getConstantValue("CRM_SERVER_URL");
        String syncInterface = ApplicationContextHelper.getConstantValue("CRM_SYNC_ORG_INFO_INTERFACE");

        if (StringUtils.isEmpty(crmServerUrl) || StringUtils.isEmpty(syncInterface)) {
            return;
        }

        List<CoreOrgBean> paramOrgList = wrapper.parse(CoreOrgBean.class);

        CrmOpportunityRequestDTO<CoreOrgBean> requestDTO = new CrmOpportunityRequestDTO<>();
        requestDTO.setOperType(UPDATE_OPERATE_TYPE);
        requestDTO.setData(paramOrgList);

        String url = crmServerUrl + syncInterface;
        String requestJson = JSON.toJSONString(requestDTO);

        logger.info("[Sync-Org-Info-To-Crm]=======================");
        logger.info("{} : {}", url, requestJson);

        String result = HttpClientManager.getInstance().sendHttpPost(url, requestJson);

        try {
            // 如果接口返回的数据是错误的，直接解析则会抛出异常
            JSON.parseObject(result, CrmOpportunityResponseDTO.class);
            logger.info("[Sync-Org-Info-Success]=======================");
            logger.info(result);
        } catch (JSONException e) {
            logger.error("[Sync-Org-Info-Failure]=======================");
            logger.error(result);
        }
    }


}
