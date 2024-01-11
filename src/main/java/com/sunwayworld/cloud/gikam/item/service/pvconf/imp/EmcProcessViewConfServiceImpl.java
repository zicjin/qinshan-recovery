package com.sunwayworld.cloud.gikam.item.service.pvconf.imp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.cloud.gikam.item.bean.pvconf.CoreProcessViewConfBean;
import com.sunwayworld.cloud.gikam.item.bean.pvconf.CoreProcessViewConfDTO;
import com.sunwayworld.cloud.gikam.item.bean.pvconf.CoreProcessViewMasterConfBean;
import com.sunwayworld.cloud.gikam.item.bean.pvconf.CoreProcessViewMasterConfDTO;
import com.sunwayworld.cloud.gikam.item.bean.pvconf.CoreProcessViewSubConfBean;
import com.sunwayworld.cloud.gikam.item.bean.pvconf.CoreProcessViewSubConfDTO;
import com.sunwayworld.cloud.gikam.item.service.pvconf.CoreProcessViewMasterConfService;
import com.sunwayworld.cloud.gikam.item.service.pvconf.CoreProcessViewSubConfService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2022年12月06日
 */
@GikamBean
@Repository
public class EmcProcessViewConfServiceImpl extends CoreProcessViewConfServiceImpl {

    @Autowired
    private CoreProcessViewMasterConfService processViewMasterConfService;
    @Autowired
    private CoreProcessViewSubConfService processViewSubConfService;

    @Autowired
    private GlobalMapper globalMapper;

    @Override
    public CoreProcessViewConfDTO selectProcessView(RestJsonWrapperBean wrapper) {
        CoreProcessViewConfBean pvConfFilter = new CoreProcessViewConfBean();
        pvConfFilter.setActivatedFlag("1");
        List<CoreProcessViewConfBean> pvConfList = this.selectList(pvConfFilter);
        if (pvConfList.isEmpty()) {
            throw new CheckedException("CORE.MODULE.ITEM.PVCONFS.EXCEPTION.NO_ACTIVATED_CONF");
        } else {
            Map<String, String> param = wrapper.getParamMap();
            CoreProcessViewConfBean pvConf = pvConfList.get(0);
            Long confId = pvConf.getId();
            CoreProcessViewConfDTO processViewConfDTO = new CoreProcessViewConfDTO();
            processViewConfDTO.setId(confId);
            processViewConfDTO.setConfName(pvConf.getConfName());
            List<CoreProcessViewMasterConfBean> masterConfList = new ArrayList<>();
            if (Constant.YES.equals(pvConf.getMasterFilter())) {
                String masterFilterConfSql = pvConf.getMasterFilterConf();
                if (!StringUtils.isEmpty(masterFilterConfSql)) {
                    List<Map<String, Object>> subList = this.globalMapper.selectList(this.replaceSqlByParam(masterFilterConfSql, param));
                    masterConfList = subList.stream().map(m -> PersistableHelper.mapToPersistable(m, CoreProcessViewMasterConfBean.class)).collect(Collectors.toList());
                }
            } else {
                masterConfList = this.processViewMasterConfService.getDao().selectListByOneColumnValue(confId, "CONFID", new Order[]{Order.asc("ORDERNO"), Order.asc("ID")});
            }

            List<Long> masterConfIdList = masterConfList.stream().map(CoreProcessViewMasterConfBean::getId).collect(Collectors.toList());
            List<CoreProcessViewSubConfBean> allSubConfList = this.processViewSubConfService.getDao().selectListByOneColumnValues(masterConfIdList, "MASTERCONFID", new Order[]{Order.asc("ORDERNO"), Order.asc("ID")});
            List<CoreProcessViewMasterConfDTO> masterConfDTOList = new ArrayList<>();
            Iterator var12 = ((List)masterConfList).iterator();

            while(var12.hasNext()) {
                CoreProcessViewMasterConfBean masterConf = (CoreProcessViewMasterConfBean)var12.next();
                CoreProcessViewMasterConfDTO processViewMasterConfDTO = new CoreProcessViewMasterConfDTO();
                BeanUtils.copyProperties(masterConf, processViewMasterConfDTO);
                String statusSql = masterConf.getStatusSql();
                String subFilterConfSql;
                if (!StringUtils.isEmpty(statusSql)) {
                    List<Map<String, Object>> statusMapList = this.globalMapper.selectList(this.replaceSqlByParam(statusSql, param));
                    Set<String> statusSet = new HashSet<>();
                    statusMapList.forEach(map -> map.forEach((key, value) -> statusSet.add(value.toString())));
                    boolean ended = statusSet.stream().allMatch(s -> StringUtils.equals(s, "ended"));

                    processViewMasterConfDTO.setStatus(ended ? "ended" : "notstart");
                } else {
                    processViewMasterConfDTO.setStatus("notstart");
                }

                if ("1".equals(masterConf.getDataSearch())) {
                    String totalSql = masterConf.getTotalSql();
                    subFilterConfSql = masterConf.getDoneSql();
                    if (!StringUtils.isEmpty(totalSql) && !StringUtils.isEmpty(subFilterConfSql)) {
                        Map<String, Object> totalMap = this.globalMapper.selectOne(this.replaceSqlByParam(totalSql, param));
                        Map<String, Object> doneMap = this.globalMapper.selectOne(this.replaceSqlByParam(subFilterConfSql, param));
                        Object totalValue = this.getFirstValue(totalMap, 0L);
                        Object doneValue = this.getFirstValue(doneMap, 0L);

                        Long totalCount = Long.valueOf(totalValue.toString());
                        Long doneCount = Long.valueOf(doneValue.toString());
                        Long undoneCount = totalCount - doneCount;
                        processViewMasterConfDTO.setTotalCount(totalCount);
                        processViewMasterConfDTO.setDoneCount(doneCount);
                        processViewMasterConfDTO.setUndoneCount(undoneCount);
                        if (totalCount > 0L) {
                            double doneNum = NumberUtils.toDouble(ArithUtils.mul(ArithUtils.div((double)doneCount, (double)totalCount), 100.0D), 2);
                            String donePercent = doneNum + "%";
                            String undonePercent = 100.0D - doneNum + "%";
                            processViewMasterConfDTO.setDonePercent(donePercent);
                            processViewMasterConfDTO.setUndonePercent(undonePercent);
                        }
                    }
                }

                List<CoreProcessViewSubConfBean> subConfList = new ArrayList<>();
                if ("1".equals(masterConf.getSubFilter())) {
                    subFilterConfSql = masterConf.getSubFilterConf();
                    if (!StringUtils.isEmpty(subFilterConfSql)) {
                        List<Map<String, Object>> mapList = this.globalMapper.selectList(this.replaceSqlByParam(subFilterConfSql, param));
                        subConfList = mapList.stream().map(m -> PersistableHelper.mapToPersistable(m, CoreProcessViewSubConfBean.class)).collect(Collectors.toList());
                    }
                } else {
                    subConfList = allSubConfList.stream().filter(m -> ObjectUtils.equals(m.getMasterConfId(), masterConf.getId())).collect(Collectors.toList());
                }

                List<CoreProcessViewSubConfDTO> subConfDTOList = new ArrayList<>();

                CoreProcessViewSubConfDTO processViewSubConfDTO;
                for(Iterator var37 = ((List)subConfList).iterator(); var37.hasNext(); subConfDTOList.add(processViewSubConfDTO)) {
                    CoreProcessViewSubConfBean subConf = (CoreProcessViewSubConfBean)var37.next();
                    processViewSubConfDTO = new CoreProcessViewSubConfDTO();
                    BeanUtils.copyProperties(subConf, processViewSubConfDTO);
                    if ("1".equals(subConf.getDataSearch())) {
                        String totalSql = subConf.getTotalSql();
                        String doneSql = subConf.getDoneSql();
                        if (!StringUtils.isEmpty(totalSql) && !StringUtils.isEmpty(doneSql)) {
                            Map<String, Object> totalMap = this.globalMapper.selectOne(this.replaceSqlByParam(totalSql, param));
                            Map<String, Object> doneMap = this.globalMapper.selectOne(this.replaceSqlByParam(doneSql, param));
                            Long totalCount = Long.valueOf(this.getFirstValue(totalMap, 0L).toString());
                            Long doneCount = Long.valueOf(this.getFirstValue(doneMap, 0L).toString());
                            Long undoneCount = totalCount - doneCount;
                            processViewSubConfDTO.setTotalCount(totalCount);
                            processViewSubConfDTO.setDoneCount(doneCount);
                            processViewSubConfDTO.setUndoneCount(undoneCount);
                            if (totalCount > 0L) {
                                double doneNum = NumberUtils.toDouble(ArithUtils.mul(ArithUtils.div((double)doneCount, (double)totalCount), 100.0D), 2);
                                String donePercent = doneNum + "%";
                                String undonePercent = 100.0D - doneNum + "%";
                                processViewSubConfDTO.setDonePercent(donePercent);
                                processViewSubConfDTO.setUndonePercent(undonePercent);
                            }
                        }
                    }
                }

                processViewMasterConfDTO.setSubConfList(subConfDTOList);
                masterConfDTOList.add(processViewMasterConfDTO);
            }

            processViewConfDTO.setMasterConfList(masterConfDTOList);
            return processViewConfDTO;
        }
    }

    private String replaceSqlByParam(String sql, Map<String, String> param) {
        Map.Entry entry;
        for(Iterator var3 = param.entrySet().iterator(); var3.hasNext(); sql = StringUtils.replace(sql, "#{" + (String)entry.getKey() + "}", entry.getValue())) {
            entry = (Map.Entry)var3.next();
        }

        return sql;
    }

    private Object getFirstValue(Map<String, Object> map, Object defaultValue) {
        if (map == null) {
            return defaultValue;
        } else {
            Iterator var4 = map.entrySet().iterator();
            if (var4.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry)var4.next();
                return entry.getValue();
            } else {
                return defaultValue;
            }
        }
    }
}
