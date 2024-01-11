package com.sunwayworld.emc.module.mdm.basemdm.mtl.mapper;

import org.springframework.stereotype.Repository;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月19日 下午6:07:57
 */
@GikamBean
@Repository
public interface EmcMtlMapper extends GenericMapper<String> {

    void updateStockQty(String id);
}
