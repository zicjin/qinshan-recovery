package com.sunwayworld.emc.module.erp.service;

import com.sunwayworld.emc.module.erp.bean.ErpProjectCompareVO;

/**
 * @author zhengxy@sunwayworld.com 2023年10月24日
 */
public interface ErpCompareService {

    /**
     * 比对项目信息变化
     * @param compareVo
     */
    void compareProjectInfo(ErpProjectCompareVO compareVo);

    /**
     * 比对监测方案变化
     * @param compareVo
     */
    void compareProjScheme(ErpProjectCompareVO compareVo);

    /**
     * 比对监测方案下的测试数据变化
     * @param compareVo
     */
    void compareProjOrder(ErpProjectCompareVO compareVo);

    /**
     * 比对样品数据变化
     * @param compareVo
     */
    void compareOrder(ErpProjectCompareVO compareVo);

    /**
     * 比对样品分析项组数据变化
     * @param compareVo
     */
    void compareOrderItemGroup(ErpProjectCompareVO compareVo);
}
