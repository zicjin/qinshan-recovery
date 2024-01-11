package com.sunwayworld.emc.module.mdm.crm.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityRequestDTO;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityResponseDTO;
import com.sunwayworld.emc.module.mdm.crm.service.CrmUserOpenService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;

/**
 * @author zhengxy@sunwayworld.com 2021年11月11日
 */
@Repository
public class CrmUserOpenServiceImpl implements CrmUserOpenService {

    private static final Logger logger = LogManager.getLogger(CrmUserOpenServiceImpl.class);

    private static final String INSERT_OPERATE_TYPE = "insert";
    private static final String UPDATE_OPERATE_TYPE = "update";
    private static final String DELETE_OPERATE_TYPE = "delete";

    @Autowired
    private CoreUserService userService;

    @Override
    public CrmOpportunityResponseDTO receiveUser(RestJsonWrapperBean wrapper) {
        String requestJson = wrapper.getRequestJson();
        CrmOpportunityRequestDTO<CoreUserBean> coreUserRequest = JSON.parseObject(requestJson, new TypeReference<CrmOpportunityRequestDTO<CoreUserBean>>() {});
        logger.info("[Receive-Crm-User]=======================");
        logger.info(JSON.toJSONString(coreUserRequest));

        String operateType = coreUserRequest.getOperType();
        List<CoreUserBean> dataList = coreUserRequest.getData();
        // 从token字段取出password的值
        dataList.forEach(user -> user.setPassword(user.getExt$Item("token")));

        switch (operateType) {
            case INSERT_OPERATE_TYPE:
                userService.getDao().insert(dataList);
                break;
            case UPDATE_OPERATE_TYPE:
                userService.getDao().update(dataList);
                break;
            case DELETE_OPERATE_TYPE:
                userService.getDao().deleteBy(dataList, "id");
                break;
            default:
                break;
        }

        return CrmOpportunityResponseDTO.ok();
    }

    @Override
    public void syncUserInfo(RestJsonWrapperBean wrapper) {
        String crmServerUrl = ApplicationContextHelper.getConstantValue("CRM_SERVER_URL");
        String syncInterface = ApplicationContextHelper.getConstantValue("CRM_SYNC_USER_INFO_INTERFACE");

        if (StringUtils.isEmpty(crmServerUrl) || StringUtils.isEmpty(syncInterface)) {
            return;
        }

        List<CoreUserBean> paramUserList = wrapper.parse(CoreUserBean.class);
        List<String> userIdList = paramUserList.stream().map(CoreUserBean::getId).collect(Collectors.toList());

        List<CoreUserBean> userList = userService.getDao().selectListByOneColumnValues(userIdList, "id", Arrays.asList("id", "password"));
        // 使用token字段存放password值
        userList.forEach(user -> user.setExt$Item("token", user.getPassword()));

        CrmOpportunityRequestDTO<CoreUserBean> requestDTO = new CrmOpportunityRequestDTO<>();
        requestDTO.setOperType(UPDATE_OPERATE_TYPE);
        requestDTO.setData(userList);

        String url = crmServerUrl + syncInterface;
        String requestJson = JSON.toJSONString(requestDTO);

        logger.info("[Sync-User-To-Crm]=======================");
        logger.info("{} : {}", url, requestJson);

        String result = HttpClientManager.getInstance().sendHttpPost(url, requestJson);

        try {
            // 如果接口返回的数据是错误的，直接解析则会抛出异常
            JSON.parseObject(result, CrmOpportunityResponseDTO.class);
            logger.info("[Sync-User-Info-Success]=======================");
            logger.info(result);
        } catch (JSONException e) {
            logger.error("[Sync-User-Info-Failure]=======================");
            logger.error(result);
        }
    }


}
