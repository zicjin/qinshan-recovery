package com.sunwayworld.emc.module.erp.service.impl;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.erp.constant.ErpConstant;
import com.sunwayworld.emc.module.erp.service.ErpFileService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.OkHttpClientUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;

/**
 * @author zhengxy@sunwayworld.com 2023年11月07日
 */
@Repository
public class ErpFileServiceImpl implements ErpFileService {

    private static final Logger logger = LogManager.getLogger(ErpFileServiceImpl.class);

    @Autowired
    private CoreOrgService coreOrgService;

    @Override
    public void downloadFile(String fileSign, File targetFile) {
        FormBody.Builder requestBodyBuilder = new FormBody.Builder();
        requestBodyBuilder.add("fileSign", fileSign);

        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.url(getErpServiceUrl() + ErpConstant.DOWNLOAD_FILE_INTERFACE)
                .method("POST", requestBodyBuilder.build()).build();

        try (Response response = OkHttpClientUtils.execute(request)) {
            ResponseBody responseBody = response.body();
            if (Objects.nonNull(responseBody)) {
                InputStream inputStream = responseBody.byteStream();
                FileUtils.write(targetFile, inputStream);
                logger.info("附件{}下载成功", fileSign);
            }
        }
    }

    @Override
    public String getMerchantId() {
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
        CoreOrgBean parentOrg = coreOrgService.selectById(parentOrgId);

        return parentOrg.getExt$Item("erpMerchantId");
    }

    private String getErpServiceUrl() {
        String merchantId = getMerchantId();
        String constantKey = StringUtils.isEmpty(merchantId) ? "ERP_SERVICE_URL" : "ERP_SERVICE_URL_" + merchantId;
        return Optional.ofNullable(ApplicationContextHelper.getConstantValue(constantKey)).orElse(ErpConstant.ERP_BASE_URL);
    }

}
