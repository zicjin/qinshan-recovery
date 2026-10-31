package com.sunwayworld.emc.module.bp.info.infodata.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.info.infodata.bean.EmcInfoDataBean;
import com.sunwayworld.emc.module.bp.info.infodata.bean.EmcInfoDataItemBean;
import com.sunwayworld.emc.module.bp.info.infodata.dao.EmcInfoDataDao;
import com.sunwayworld.emc.module.bp.info.infodata.service.EmcInfoDataItemService;
import com.sunwayworld.emc.module.bp.info.infodata.service.EmcInfoDataService;
import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplFieldBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplPointBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.service.EmcInfoTmplFieldService;
import com.sunwayworld.emc.module.bp.info.infotmpl.service.EmcInfoTmplPointService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;

@Repository
@GikamBean
public class EmcInfoDataServiceImpl implements EmcInfoDataService {

    @Autowired
    private EmcInfoDataDao infoDataDao;

    @Autowired
    private EmcInfoDataItemService infoDataItemService;

    @Autowired
    private EmcReportService reportService;

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcInfoTmplFieldService infoTmplFieldService;

    @Autowired
    EmcInfoTmplPointService infoTmplPointService;

    @SuppressWarnings("unchecked")
    @Override
    public EmcInfoDataDao getDao() {
        return infoDataDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertReportInfoData(List<Long> reportIdList) {
        // 所有报告数据
        List<EmcReportBean> emcReportList = reportService.selectListByIds(reportIdList);
        // 通过监测项目id和监测模板id去重
        List<EmcReportBean> distinctReportList = new ArrayList<>(
                emcReportList.stream().collect(Collectors.groupingBy(v -> v.getProjId() + v.getMonitorTmplId(), Collectors.collectingAndThen(Collectors.toList(), e -> e.get(0)))).values());

        MapperParameter parameter = new MapperParameter();
        parameter.setFilter(distinctReportList, "PROJID", "MONITORTMPLID");
        parameter.setFilter(SearchFilter.instance().match("REPORTDISPLAY", "1").filter(MatchPattern.EQ).match("MONITORPOINTNO", null).filter(MatchPattern.DIFFER));
        // 根据报告的监测项目id和监测模板id查询出来的样品测试,样品测试结果,点位的相关数据
        List<EmcOrderTaskResultBean> emcOrderTaskResultList = orderTaskResultService.selectList(parameter);
        // 所有的监测点编码
        List<String> pointNoList = emcOrderTaskResultList.stream().map(e -> {
            return e.getExt$Item("monitorpointno");
        }).distinct().collect(Collectors.toList());
        SearchFilter filter = SearchFilter.instance().match("MONITORPOINTNO", pointNoList).filter(MatchPattern.OR);
        // 所有的监测点编码对应的监测点模板
        List<EmcInfoTmplPointBean> infoTmplPointList = infoTmplPointService.selectListByFilter(filter);
        Map<String, List<EmcInfoTmplPointBean>> infoTmplMap = infoTmplPointList.stream().collect(Collectors.groupingBy(v -> v.getExt$Item("monitorpointno")));
        // 所有的监测点模板
        List<Long> infoTmplIdList = infoTmplPointList.stream().map(x -> {
            return x.getInfoTmplId();
        }).collect(Collectors.toList());

        // 根据样品测试结果的数据的分析项id分组
        Map<Long, List<EmcOrderTaskResultBean>> itemMap = emcOrderTaskResultList.stream().collect(Collectors.groupingBy(m -> m.getItemId()));
        List<Long> itemIdlist = new ArrayList<>();
        itemMap.forEach((k, v) -> {
            itemIdlist.add(k);
        });
        SearchFilter itemFilter = SearchFilter.instance().match("INFOTMPLID", infoTmplIdList).filter(MatchPattern.OR);
        // 查询出每个模板的字段和分析项信息
        List<EmcInfoTmplFieldBean> emcInfoTmplFieldItemList = infoTmplFieldService.selectListByFilter(itemFilter).stream().filter(e -> {
            return itemIdlist.stream().anyMatch(m -> {
                return StringUtils.equals(String.valueOf(m), e.getExt$Item("itemid"));
            }) || StringUtils.isEmpty(e.getExt$Item("itemid"));
        }).collect(Collectors.toList());
        // 根据字段和分析项信息的模板id分组
        Map<Long, List<EmcInfoTmplFieldBean>> emcInfoTmplFieldMap = emcInfoTmplFieldItemList.stream().collect(Collectors.groupingBy(m -> m.getInfoTmplId()));

        // 储存每个模板的分析项字段对应关系和监测日期对应关系
        Map<Long, Map<String, String>> tmplItemRnMap = new HashMap<>();
        Map<Long, String> monitorDateColMap = new HashMap<>();
        for (Entry<Long, List<EmcInfoTmplFieldBean>> emcInfoTmplField : emcInfoTmplFieldMap.entrySet()) {
            Long tmplId = emcInfoTmplField.getKey();
            List<EmcInfoTmplFieldBean> tmplList = emcInfoTmplField.getValue();
            Map<String, String> m = new HashMap<>();
            String monitorDateColName = "";
            for (EmcInfoTmplFieldBean tmplField : tmplList) {

                m.put(tmplField.getExt$Item("itemid"), StringUtils.trim(StringUtils.lowerCase(tmplField.getField())));
                String primaryName = tmplField.getPrimaryName();
                String bizField = tmplField.getBizField();
                if (StringUtils.isEmpty(monitorDateColName) && "MonitorDate".equals(primaryName)) {
                    monitorDateColName = bizField;
                }
            }
            monitorDateColMap.put(tmplId, monitorDateColName);
            tmplItemRnMap.put(tmplId, m);
        }
        // 根据样品测试结果的数据监测点编码确定一个监测点
        Map<String, List<EmcOrderTaskResultBean>> monitorPointMap = emcOrderTaskResultList.stream().collect(Collectors.groupingBy(v -> v.getExt$Item("monitorpointno")));
        List<EmcInfoDataBean> emcInfoDataList = new ArrayList<>();
        // 存储综合信息分析插入时根据模板关联综合信息id
        Map<String, Map<Long, Long>> dataInfoIdMap = new HashMap<>();

        for (Entry<String, List<EmcOrderTaskResultBean>> monitorEntry : monitorPointMap.entrySet()) {
            List<EmcInfoTmplPointBean> infoTmplValueList = infoTmplMap.get(monitorEntry.getKey());
            // 该监测点有几个监测点模板就循环几次
            for (EmcInfoTmplPointBean emcInfoTmplPoint : infoTmplValueList) {
                Map<Long, Long> infoIdMap = new HashMap<>();
                Map<String, List<EmcOrderTaskResultBean>> dateMap = monitorEntry.getValue().stream()
                        .collect(Collectors.groupingBy(m -> m.getExt$().get(monitorDateColMap.get(emcInfoTmplPoint.getInfoTmplId()))));
                for (Entry<String, List<EmcOrderTaskResultBean>> dateEntry : dateMap.entrySet()) {
                    List<EmcOrderTaskResultBean> resultList = dateEntry.getValue();
                    EmcInfoDataBean emcInfoData = new EmcInfoDataBean();
                    emcInfoData.setId(ApplicationContextHelper.getNextIdentity());
                    for (EmcOrderTaskResultBean result : resultList) {
                        infoIdMap.put(result.getId(), emcInfoData.getId());
                        if (Objects.isNull(ReflectionUtils.getFieldValue(emcInfoData, "monitorDate"))) {
                            ReflectionUtils.setFieldValue(emcInfoData, "monitorDate", DateTimeUtils.parseLocalDate(result.getExt$Item(monitorDateColMap.get(emcInfoTmplPoint.getInfoTmplId()))));
                        }
                        ReflectionUtils.setFieldValue(emcInfoData, tmplItemRnMap.get(emcInfoTmplPoint.getInfoTmplId()).get(String.valueOf(result.getItemId())), result.getReportResult());
                    }
                    emcInfoDataList.add(emcInfoData);
                }
                dataInfoIdMap.put(emcInfoTmplPoint.getInfoTmplId()+emcInfoTmplPoint.getExt$Item("monitorpointno"), infoIdMap);
            }

        }

        // 综合信息分析数据
        List<EmcInfoDataItemBean> emcInfoDataItemList = new ArrayList<>();
        for (EmcOrderTaskResultBean emcOrderTaskResult : emcOrderTaskResultList) {
            List<EmcInfoTmplPointBean> infoTmplValueList = infoTmplMap.get(emcOrderTaskResult.getExt$Item("monitorpointno"));
            for (EmcInfoTmplPointBean emcInfoTmplPoint : infoTmplValueList) {
                Map<Long, Long> infoIdMap = dataInfoIdMap.get(emcInfoTmplPoint.getInfoTmplId()+emcInfoTmplPoint.getExt$Item("monitorpointno"));
                EmcInfoDataItemBean emcInfoDataItem = new EmcInfoDataItemBean();
                emcInfoDataItem.setId(ApplicationContextHelper.getNextIdentity());
                emcInfoDataItem.setInfoDataId(infoIdMap.get(emcOrderTaskResult.getId()));
                emcInfoDataItem.setMonitorPointName(emcOrderTaskResult.getExt$().get("monitorpointname"));
                emcInfoDataItem.setItemName(emcOrderTaskResult.getItemName());
                emcInfoDataItem.setResult(emcOrderTaskResult.getReportResult());
                emcInfoDataItem.setMonitorDate(DateTimeUtils.parseLocalDate(emcOrderTaskResult.getExt$Item(monitorDateColMap.get(emcInfoTmplPoint.getInfoTmplId()))));
                emcInfoDataItemList.add(emcInfoDataItem);
            }

        }

        getDao().insert(emcInfoDataList);
        infoDataItemService.getDao().insert(emcInfoDataItemList);

    }

}
