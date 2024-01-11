package com.sunwayworld.emc.module.erp.service;

import java.io.File;

/**
 * @author zhengxy@sunwayworld.com 2023年11月07日
 */
public interface ErpFileService {

    /**
     * 获取商户ID
     * @return
     */
    String getMerchantId();

    /**
     * 下载文件
     * @param fileSign 文件签名
     * @param targetFile 目标文件
     * @return 文件
     */
    void downloadFile(String fileSign, File targetFile);
}
