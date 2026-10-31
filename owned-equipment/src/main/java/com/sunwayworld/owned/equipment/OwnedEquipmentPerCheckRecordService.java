package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptPerCheckRecordDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPerCheckRecordService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import java.util.List;

/** Isolated period-check record service for the unchanged equipment resource. */
public final class OwnedEquipmentPerCheckRecordService implements EmcEquiptPerCheckRecordService {
    private final OwnedEquipmentPerCheckRecordDao dao;
    public OwnedEquipmentPerCheckRecordService(OwnedEquipmentPerCheckRecordDao dao){this.dao=dao;}
    @Override public EmcEquiptPerCheckRecordDao getDao(){return dao;}
    @Override public Long insert(RestJsonWrapperBean wrapper){return dao.insertRecord(wrapper);}
    @Override public void delete(RestJsonWrapperBean wrapper){dao.deleteRecords(wrapper);}
    @Override public Page<EmcEquiptPerCheckRecordBean> selectPaginationByFilter(SearchFilter filter,RestJsonWrapperBean wrapper){Object id=filter==null?null:filter.getFilterMap().get("equiptId");if(id==null&&filter!=null&&!filter.getStdFilterList().isEmpty())id=filter.getStdFilterList().get(0).getMap().values().iterator().next();if(id==null)throw new IllegalArgumentException("equipment id is required");return dao.readRecords(Long.valueOf(String.valueOf(id)),wrapper);}
    @Override public List<EmcEquiptMaintBean> selectRecordMaintSelectableList(Long id){return dao.selectable(id);}
    @Override public void complete(RestJsonWrapperBean wrapper){dao.complete(wrapper);}
}
