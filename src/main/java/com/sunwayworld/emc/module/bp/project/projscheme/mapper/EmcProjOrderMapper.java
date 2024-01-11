package com.sunwayworld.emc.module.bp.project.projscheme.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-08
 */
@GikamBean
public interface EmcProjOrderMapper extends GenericMapper<Long> {

    List<Map<String, Object>> selectByProjId(@Param("projId") Long projId);

}
