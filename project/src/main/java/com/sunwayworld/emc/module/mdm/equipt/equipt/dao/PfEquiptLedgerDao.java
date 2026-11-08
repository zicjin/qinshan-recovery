package com.sunwayworld.emc.module.mdm.equipt.equipt.dao;

import java.util.List;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptPlanDTO;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptPerCheckDTO;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.PfEquiptUserDTO;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;

/** Dedicated DTO queries; keep the generic equipment DAO contract unchanged. */
public interface PfEquiptLedgerDao {
    List<PfEquiptPlanDTO> selectLedgerPlan(MapperParameter parameter);

    List<PfEquiptPerCheckDTO> selectLedgerPerCheck(MapperParameter parameter);

    List<PfEquiptUserDTO> selectLedgerUser(MapperParameter parameter);
}
