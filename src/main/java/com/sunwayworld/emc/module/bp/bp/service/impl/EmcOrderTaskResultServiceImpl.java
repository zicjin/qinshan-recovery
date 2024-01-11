package com.sunwayworld.emc.module.bp.bp.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.DataQueryDTO;
import com.sunwayworld.emc.module.bp.bp.bean.DataSummaryDTO;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcResultLogBean;
import com.sunwayworld.emc.module.bp.bp.computing.EmcOrderTaskResultComputingEngine;
import com.sunwayworld.emc.module.bp.bp.dao.EmcOrderTaskResultDao;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.mdm.basemdm.revisionrule.service.EmcRevisionRuleService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemRevisionBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.table.TableCellDescriptor;
import com.sunwayworld.framework.support.table.TableHelper;
import com.sunwayworld.framework.support.table.bean.TableHeadBean;
import com.sunwayworld.framework.support.table.bean.TableRowBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

@Repository
@GikamBean
public class EmcOrderTaskResultServiceImpl implements EmcOrderTaskResultService {

    @Autowired
    private EmcOrderTaskResultDao orderTaskResultDao;

    @Autowired
    private EmcRevisionRuleService revisionRuleService;

    @Autowired
    private EmcItemService itemService;

    @Autowired
    @Lazy
    private EmcFolderService folderService;

    @Autowired
    @Lazy
    private EmcOrderService orderService;

    @Autowired
    @Lazy
    private EmcOrderTaskService orderTaskService;

    @Autowired
    @Lazy
    private EmcOrderTaskResultService orderTaskResultService;


    @Override
    @SuppressWarnings("unchecked")
    public EmcOrderTaskResultDao getDao() {
        return orderTaskResultDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcOrderTaskResultBean emcOrderTaskResult = jsonWrapper.parseUnique(EmcOrderTaskResultBean.class);
        emcOrderTaskResult.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcOrderTaskResult);
        return emcOrderTaskResult.getId();
    }

    /*
     * 修约并更新结果状态
     */
    @Override
    public void reviseResult(EmcOrderTaskResultBean resultBean) {
        if (NumberUtils.isNumber(resultBean.getReportResult())) {
            if (!StringUtils.isEmpty(resultBean.getRevisionRule()) && !EmcConstant.REVISION_RULE_NONE_DESC.equals(resultBean.getRevisionRule())) {
                resultBean.setReportResult(revisionRuleService.getRuledValue(resultBean.getReportResult(), resultBean.getRevisionRule()));
            }
        }
    }

    @Override
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void initTaskResult(Long id, RestJsonWrapperBean wrapper) {
        // 查询当前采样单下分析类型为采样的分析项
    	if(!ObjectUtils.isEmpty(id)) {
    		List<EmcOrderTaskResultBean> orderTaskResultList = this
                    .selectListByFilter(SearchFilter.instance().match("SAMPLEID", id).filter(MatchPattern.SEQ).match("ANALYSISTYPE", "sampling").filter(MatchPattern.SEQ), wrapper);
            orderTaskResultList.stream().forEach(e -> {
                e.setOriginalResult(null);
                e.setReportResult(null);
                e.setResultStatus(EmcBizConstant.ORDERTASKRESULT_STATUS_LOGGED);
            });
            getDao().update(orderTaskResultList, "ORIGINALRESULT", "REPORTRESULT", "RESULTSTATUS");
    	}
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void setDoneTaskResult(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskResultBean> orderTaskResultList = wrapper.parse(EmcOrderTaskResultBean.class);
        // 执行之后把原始结果和报出结果都更新成/ 当前行就是选中的行
        orderTaskResultList.stream().forEach(e -> {
            e.setOriginalResult("/");
            e.setReportResult("/");
            e.setResultStatus(EmcBizConstant.ORDERTASKRESULT_STATUS_DONE);
            e.setReason(e.getExt$Item("reason"));
        });
        getDao().update(orderTaskResultList, "originalResult", "reportResult", "resultStatus", "reason");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void setDoneOrder(RestJsonWrapperBean wrapper) {
        // 执行之后把原始结果和报出结果都更新成/
        // 未录结果（原始结果为空的数据）就是当前选中的样品下所有样品结果中所有没录的分析项（分析类型为采样）
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcOrderTaskResultBean> orderTaskResultList = new ArrayList<>();

        orderTaskList.stream().forEach(e -> {
            EmcOrderTaskResultBean result = new EmcOrderTaskResultBean();
            result.setOrderId(e.getOrderId());
            result.setRunId(e.getRunId());
            orderTaskResultList.add(result);
        });

        List<EmcOrderTaskResultBean> orderTaskResultAllList = getDao().selectList(orderTaskResultList, Arrays.asList("ORDERID", "RUNID"), Collections.emptyList());
        orderTaskResultAllList.stream().forEach(e -> {
            if (StringUtils.isEmpty(e.getOriginalResult())) {
                e.setOriginalResult("/");
                e.setReportResult("/");
                e.setResultStatus(EmcBizConstant.ORDERTASKRESULT_STATUS_DONE);
                e.setReason(e.getExt$Item("reason"));
            }
        });
        getDao().update(orderTaskResultAllList, "originalResult", "reportResult", "resultStatus", "reason");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void setDoneSampleOrder(RestJsonWrapperBean wrapper) {
        // 执行之后把原始结果和报出结果都更新成/
        // 未录结果（原始结果为空的数据）就是当前选中的样品下所有样品结果中所有没录的分析项（分析类型为采样）
        List<EmcFolderBean> folderList = wrapper.parse(EmcFolderBean.class);
        String sampleId = folderList.get(0).getExt$Item("sampleid");

        List<Long> folderIdList = folderList.stream().map(e -> e.getId()).collect(Collectors.toList());

        if(folderIdList.size()>0) {
        	List<EmcOrderTaskResultBean> orderTaskResultFilterList = this.selectListByFilter(SearchFilter.instance().match("FOLDERID", folderIdList).filter(MatchPattern.SEQ).match("SAMPLEID", sampleId)
                    .filter(MatchPattern.SEQ).match("ANALYSISTYPE", "sampling").filter(MatchPattern.SEQ), wrapper);

            orderTaskResultFilterList.stream().forEach(e -> {
                if (StringUtils.isEmpty(e.getOriginalResult())) {
                    e.setOriginalResult("/");
                    e.setReportResult("/");
                    e.setResultStatus(EmcBizConstant.ORDERTASKRESULT_STATUS_DONE);
                    e.setReason(e.getExt$Item("reason"));
                }
            });
            getDao().update(orderTaskResultFilterList, "originalResult", "reportResult", "resultStatus", "reason");
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void setDataInputDefaultTaskResult(Long id, RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> folderList = wrapper.parse(EmcOrderBean.class);
        List<Long> folderIdList = folderList.stream().map(e -> e.getId()).collect(Collectors.toList());

        
        if(folderIdList.size()>0) {
        	List<EmcOrderTaskResultBean> orderTaskResultList = this.selectListByFilter(SearchFilter.instance().match("FOLDERID", folderIdList).filter(MatchPattern.SEQ).match("SAMPLEID", id)
                    .filter(MatchPattern.SEQ).match("ANALYSISTYPE", "sampling").filter(MatchPattern.SEQ), wrapper);
            List<Long> itemIdList = orderTaskResultList.stream().map(t -> t.getItemId()).collect(Collectors.toList());
            List<EmcItemBean> itemList = itemService.selectListByIds(itemIdList);

            orderTaskResultList.forEach(e -> {
                itemList.stream().filter(i -> i.getId().equals(e.getItemId())).forEach(v -> {
                    if (NumberUtils.isNumber(v.getDefaultResult())) {
                        e.setOriginalResult(v.getDefaultResult());

                        if (!StringUtils.isEmpty(e.getRevisionRule()) && !StringUtils.isEmpty(e.getOriginalResult())) {
                            e.setReportResult(NumberUtils.rounding(e.getOriginalResult(), e.getRevisionRule()));
                            e.setResultStatus(StringUtils.isEmpty(e.getOriginalResult()) ? null : "Done");
                        } else {
                            e.setReportResult(e.getOriginalResult());
                        }
                    }
                });
            });
            getDao().update(orderTaskResultList, "ORIGINALRESULT", "REPORTRESULT", "RESULTSTATUS", "UNITNAME", "REVISIONRULE");
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void changeResult(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskResultBean> orderTaskResultList = wrapper.parse(EmcOrderTaskResultBean.class);

        EmcOrderTaskResultComputingEngine.getEngine(orderTaskResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList())).compute();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void addRepeatNo(Long id, RestJsonWrapperBean wrapper) {
    	List<EmcOrderTaskResultBean> oldorderTaskResultList =  wrapper.parse(EmcOrderTaskResultBean.class);
    	List<EmcOrderTaskResultBean> addOrderTaskResult = new ArrayList<>();
    	for(int j=0;j<oldorderTaskResultList.size();j++) {
    		EmcOrderTaskResultBean orderTaskResult = oldorderTaskResultList.get(j);
    		Long repeatNo = orderTaskResult.getRepeatNo();
            // 根据 itemId 和 orderTaskId 查询所有的结果并过滤出最大的重复数
            List<EmcOrderTaskResultBean> orderTaskResultList = getDao().selectList(orderTaskResult, Arrays.asList("ITEMID", "ORDERTASKID", "ANALYSISTYPE"), Collections.emptyList());
            EmcOrderTaskResultBean maxRepeatNoOrderTaskResultBean = orderTaskResultList.stream().max(Comparator.comparingLong(EmcOrderTaskResultBean::getRepeatNo)).get();

            for (int i = 1; i <= repeatNo; i++) {
                EmcOrderTaskResultBean orderTaskResultBean = new EmcOrderTaskResultBean();

                BeanUtils.copyProperties(maxRepeatNoOrderTaskResultBean, orderTaskResultBean);
                orderTaskResultBean.setId(ApplicationContextHelper.getNextIdentity());
                orderTaskResultBean.setRepeatNo(orderTaskResultBean.getRepeatNo() + i);
                addOrderTaskResult.add(orderTaskResultBean);
            }
    	}
        getDao().insert(addOrderTaskResult);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void deleteRepeatNo(RestJsonWrapperBean wrapper) {
    	List<EmcOrderTaskResultBean> deleteOrderTaskResultList =  wrapper.parse(EmcOrderTaskResultBean.class);
    	//根据样品ID,分析类型和分析项ID获取分析项结果记录
    	List<EmcOrderTaskResultBean> orderTaskResultList = getDao().selectList(deleteOrderTaskResultList, Arrays.asList("ANALYSISTYPE", "ORDERID", "ITEMID"),  Collections.emptyList());
    	//先根据分析项ID分组，然后获取重复数最大的分析项记录
        Map<Long,EmcOrderTaskResultBean> mapList = orderTaskResultList.stream().collect(Collectors.groupingBy(EmcOrderTaskResultBean::getItemId,
                Collectors.collectingAndThen(
                        Collectors.maxBy(Comparator.comparingLong(EmcOrderTaskResultBean::getRepeatNo)), Optional:: get)));
        //Map转list
        Collection<EmcOrderTaskResultBean> valueCollection = mapList.values();
        List<EmcOrderTaskResultBean> resultList= new ArrayList<EmcOrderTaskResultBean>(valueCollection);
        //删除重复数最大的分析项结果记录
        List<Long> resultsIds = resultList.stream().map(e -> e.getId()).collect(Collectors.toList());
        getDao().deleteByIdList(resultsIds);
    }

    @Override
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void syncTmplInfo(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskResultBean> orderTaskResultList = wrapper.parse(EmcOrderTaskResultBean.class);
        List<Long> itemIds = orderTaskResultList.stream().map(e -> e.getItemId()).collect(Collectors.toList());
        if(itemIds.size()>0) {
        	// itemId 查 t_emc_item
            List<EmcItemBean> itemList = itemService.selectListByFilter(SearchFilter.instance().match("ID", itemIds).filter(MatchPattern.OR));
            List<Long> orderTaskIdsList = orderTaskResultList.stream().map(e -> e.getOrderTaskId()).collect(Collectors.toList());
            // 查分析项相同的样品集合
            List<EmcOrderTaskResultBean> orderTaskResultAllItemList = this.selectListByFilter(SearchFilter.instance().match("ITEMID", itemIds).filter(MatchPattern.OR)
                    .match("ORDERTASKID", orderTaskIdsList).filter(MatchPattern.OR).match("ANALYSISTYPE", orderTaskResultList.get(0).getExt$Item("analysisType")).filter(MatchPattern.SEQ), wrapper);
            orderTaskResultAllItemList.stream().forEach(e -> {
                itemList.stream().forEach(v -> {
                    if (e.getItemId().equals(v.getId())) {
                        e.setItemName(v.getItemName());
                        e.setItemOrderNo(v.getOrderNo());
                        e.setItemAliasName(v.getItemAliasName());
                        e.setRevisionRule(v.getRevisionRule());
                        e.setUnitName(v.getUnitName());
                        e.setLowLimit(v.getLowLimit());
                        e.setReportDisplay(v.getReportDisplay());
                        e.setScriptFlag(v.getScriptFlag());
                        e.setResultCategory(v.getResultCategory());
                    }
                });
            });
            getDao().update(orderTaskResultAllItemList, "ITEMID", "ITEMNAME", "ITEMORDERNO", "ITEMALIASNAME", "REVISIONRULE", "UNITNAME", "LOWLIMIT", "REPORTDISPLAY", "SCRIPTFLAG", "RESULTCATEGORY");
        }
        
    }

    @Override
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void removeFomula(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskResultBean> orderTaskList = wrapper.parse(EmcOrderTaskResultBean.class);
        List<EmcOrderTaskResultBean> orderTaskResultList = getDao().selectList(orderTaskList, Arrays.asList("ITEMID", "ORDERTASKID"), Collections.emptyList());
        orderTaskResultList.stream().forEach(e -> {
            e.setScriptFlag("0");
        });
        getDao().update(orderTaskResultList, "SCRIPTFLAG");
    }

    @Override
    public void setDefaultTaskResult(Long id, RestJsonWrapperBean wrapper) {
        // 根据点位查询所有结果信息
        List<EmcFolderBean> folderList = wrapper.parse(EmcFolderBean.class);
        List<Long> folderIdsList = folderList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> allOrderTaskResultList = this.selectListByFilter(SearchFilter.instance().match("FOLDERID", folderIdsList).filter(MatchPattern.OR).match("SAMPLEID", id)
                .filter(MatchPattern.SEQ).match("ANALYSISTYPE", "sampling").filter(MatchPattern.SEQ), wrapper);
        // 选出全部样品测试结果中与选择的样品测试结果相同的数据
        List<EmcOrderTaskResultBean> orderTaskResultList = wrapper.parse(EmcOrderTaskResultBean.class);
        List<EmcOrderTaskResultBean> newOrderTaskResultList = new ArrayList<EmcOrderTaskResultBean>();
        for (EmcOrderTaskResultBean allOrderTask : allOrderTaskResultList) {
            for (EmcOrderTaskResultBean orderTask : orderTaskResultList) {
                if (allOrderTask.getItemId().equals(orderTask.getItemId())) {
                    newOrderTaskResultList.add(allOrderTask);
                }
            }
        }
        List<Long> itemIdList = newOrderTaskResultList.stream().map(t -> t.getItemId()).collect(Collectors.toList());
        List<EmcItemBean> itemList = itemService.selectListByIds(itemIdList);
        // 对于有默认值的样品测试结果，需要自动触发一次计算公式
        List<Long> defaultValueResultIdList = new ArrayList<>();

        newOrderTaskResultList.forEach(e -> {
            itemList.stream().filter(i -> i.getId().equals(e.getItemId())).forEach(v -> {
            	//当测试管理中分析项默认值不会空而且当前结果值为空的情况下才进行默认结果
            	if(StringUtils.isEmpty(e.getReportResult()) && !StringUtils.isEmpty(v.getDefaultResult())) {
            		 e.setOriginalResult(v.getDefaultResult());
            		 defaultValueResultIdList.add(e.getId());
            	}
            });
        });
        orderTaskResultService.getDao().update(newOrderTaskResultList, "ORIGINALRESULT");
        if (!defaultValueResultIdList.isEmpty()) {
            EmcOrderTaskResultComputingEngine.getEngine(defaultValueResultIdList).compute();
        }
    }

    @Override
    public List<EmcOrderTaskResultBean> selectScriptListByFilter(SearchFilter filter) {
        MapperParameter parameter = new MapperParameter();
        parameter.setFilter(filter);

        List<Map<String, Object>> mapList = getDao().selectScriptByCondition(parameter);

        if (mapList.isEmpty()) {
            return Collections.emptyList();
        }

        return mapList.stream().map(m -> PersistableHelper.mapToPersistable(m, getDao().getType())).collect(Collectors.toList());

    }

    @Override
    public List<TableHeadBean> selectResultDataSummaryTitleList(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskResultBean> resultList = getDataSummaryResultList(wrapper);
        List<TableHeadBean> headList = new ArrayList<TableHeadBean>();

        List<DataSummaryDTO> dataList = this.getDataSummaryDTO(resultList);
        for (DataSummaryDTO data : dataList) {
            TableHeadBean head = new TableHeadBean();
            head.setField(data.getColId());
            head.setTitle(data.getColTitle());
            headList.add(head);
        }
        return headList;
    }

    @Override
    public List<TableRowBean> selectResultDataSummaryRowList(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskResultBean> resultList = getDataSummaryResultList(wrapper);

        List<DataSummaryDTO> dataList = this.getDataSummaryDTO(resultList);

        TableCellDescriptor<DataSummaryDTO> cellDescriptor = new TableCellDescriptor<>("colId", "colVal");
        cellDescriptor.addPivot("rowNo");

        List<TableRowBean> tableRowList = TableHelper.parseTableRowList(dataList, cellDescriptor);
        for (TableRowBean row : tableRowList) {
            String orderNo = row.getPivotValue("rowNo");
            EmcOrderTaskResultBean result = resultList.stream().filter(f -> ObjectUtils.equals(orderNo, f.getOrderNo())).findFirst().orElse(null);

            if (result != null) {
                row.put("sampleCompleteDate", DateTimeUtils.parseLocalDate(result.getExt$Item("samplecompletedate")));
                row.put("monitorPointName", result.getExt$Item("monitorpointname"));
                row.put("sampleCompleteTime", result.getExt$Item("samplecompletetime"));
                row.put("orderNo", result.getOrderNo());
                row.put("orderDesc", result.getExt$Item("orderdesc"));
                row.put("testOrgName", result.getExt$Item("testorgname"));
                row.put("projName", result.getExt$Item("projname"));
                row.put("folderNo", result.getExt$Item("folderno"));
                row.put("monitorPointNo", result.getExt$Item("monitorpointno"));
            }
        }
        return tableRowList;
    }

    @Override
    public List<TableHeadBean> selectDataQueryTitleList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        List<EmcOrderTaskResultBean> resultList = this.selectList(parameter);
        List<EmcOrderTaskResultBean> showList = resultList.size() > 1000 ? resultList.subList(0, 1000) : resultList;
        List<TableHeadBean> headList = new ArrayList<TableHeadBean>();

        List<DataQueryDTO> dataList = this.getDataTitleQueryDTO(showList);
        for (DataQueryDTO data : dataList) {
            TableHeadBean head = new TableHeadBean();
            head.setField(data.getColId());
            head.setTitle(data.getColTitle());
            headList.add(head);
        }
        return headList;
    }

    @Override
    public Page<TableRowBean> selectDataQueryRowList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        List<EmcOrderTaskResultBean> resultList = this.selectList(parameter);
        List<EmcOrderTaskResultBean> showList = resultList.size() > 1000 ? resultList.subList(0, 1000) : resultList;
        List<DataQueryDTO> dataList = this.getDataQueryDTO(showList);

        TableCellDescriptor<DataQueryDTO> cellDescriptor = new TableCellDescriptor<>("colId", "colVal");
        cellDescriptor.addPivot("rowNo");

        List<TableRowBean> tableRowList = TableHelper.parseTableRowList(dataList, cellDescriptor);
        for (TableRowBean row : tableRowList) {
            String orderNo = row.getPivotValue("rowNo");
            EmcOrderTaskResultBean result = showList.stream().filter(f -> ObjectUtils.equals(orderNo, f.getOrderNo())).findFirst().orElse(null);

            if (result != null) {
                row.put("projNo", result.getExt$Item("projno"));
                row.put("registDate", result.getExt$Item("registdate"));
                row.put("projName", result.getExt$Item("projname"));
                row.put("testOrgName", result.getExt$Item("testorgname"));
                row.put("monitorPointName", result.getExt$Item("monitorpointname"));
                row.put("orderNo", result.getOrderNo());
                row.put("sampleBeginDate", DateTimeUtils.parseLocalDate(result.getExt$Item("samplebegindate")));
            }
        }
        return new Pagination<>(tableRowList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateResult(RestJsonWrapperBean wrapper) {
        EmcResultLogBean resultLog = wrapper.parseUnique(EmcResultLogBean.class);

        EmcOrderTaskResultBean orderTaskResult = new EmcOrderTaskResultBean();
        orderTaskResult.setId(resultLog.getResultId());
        orderTaskResult.setOriginalResult(resultLog.getValue());
        getDao().update(orderTaskResult, "ORIGINALRESULT");
        // 更新结果之后计算
        // Long folderId = NumberUtils.parseLong(wrapper.getFilterValue("folderId"));
        EmcOrderTaskResultComputingEngine.getEngine(Arrays.asList(resultLog.getResultId())).compute();
    }

    @Override
    public Page<EmcOrderTaskResultBean> selectUpdateIndexOrderTaskResult(SearchFilter filter, RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        parameter.setFilter(filter);
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectUpdateIndexOrderTaskResult(parameter), rowBounds);
    }

    /**
     * 根据原始结果计算报出结果
     */
    @Override
    public void setReportResultValue(EmcOrderTaskResultBean resultBean, EmcItemBean item, List<EmcItemRevisionBean> itemRevisionList) {
        if (item == null) {
            return;
        }

        String value = resultBean.getOriginalResult();
        if (!StringUtils.isEmpty(value) && NumberUtils.isNumber(value)) {
            double valueNum = NumberUtils.parseDouble(value);
            // 过滤出对应的分段修约数据
            List<EmcItemRevisionBean> filterItemRevisionList = itemRevisionList.stream().filter(e -> e.getItemId().equals(resultBean.getItemId())).collect(Collectors.toList());
            String revisionRule = resultBean.getRevisionRule();

            // 处理分段修约：
            // 1、根据值查询他所在区间范围内的分段修约
            for (EmcItemRevisionBean e : filterItemRevisionList) {
                // 下限符号
                String lowLimitSymbol = e.getLowLimitSymbol();
                // 下限
                String lowLimit = e.getLowLimit();
                // 上限符号
                String highLimitSymbol = e.getHighLimitSymbol();
                // 上限
                String highLimit = e.getHighLimit();
                if (!StringUtils.equals("/", lowLimitSymbol) && !StringUtils.isEmpty(lowLimit) && NumberUtils.isNumber(lowLimit)) {
                    double lowLimitNum = NumberUtils.parseDouble(lowLimit);
                    if (StringUtils.equals(">", lowLimitSymbol)) {
                        if (valueNum <= lowLimitNum) {
                            continue;
                        }
                    } else {
                        if (valueNum < lowLimitNum) {
                            continue;
                        }
                    }
                }

                if (!StringUtils.equals("/", highLimitSymbol) && !StringUtils.isEmpty(highLimit) && NumberUtils.isNumber(highLimit)) {
                    double highLimitNum = NumberUtils.parseDouble(highLimit);
                    if (StringUtils.equals("<", lowLimitSymbol)) {
                        if (valueNum >= highLimitNum) {
                            continue;
                        }
                    } else {
                        if (valueNum > highLimitNum) {
                            continue;
                        }
                    }
                }
                revisionRule = e.getRevisionRule();
            }

            String revisionValue = value;
            if (!StringUtils.isEmpty(revisionRule) && !EmcConstant.REVISION_RULE_NONE_DESC.equals(revisionRule)) {
                revisionValue = NumberUtils.rounding(revisionValue, revisionRule);
            }

            String limitRevision = item.getLimitRevision();
            if (!StringUtils.isEmpty(limitRevision) && Constant.YES.equals(limitRevision) && !ObjectUtils.isEmpty(resultBean.getLowLimit())) {
                // 是否根据检出限修约为是, 判断检出低限是否为空
                String lowLimitStr = ObjectUtils.toString(resultBean.getLowLimit());
                int radix = getRadix(lowLimitStr);
                // 检出限修约值
                String lowLimitRevisionValue = NumberUtils.rounding(value, "ISO," + radix);
                // 比较检出限修约值和之前的计算修约值，取小数点位数小的值
                int isoRadix = getRadix(lowLimitRevisionValue);

                int revisionRadix = getRadix(revisionValue);
                if (isoRadix < revisionRadix) {
                    revisionValue = lowLimitRevisionValue;
                }
            }
            resultBean.setReportResult(revisionValue);
        } else {
            resultBean.setReportResult(value);
        }
    }

    @Override
    public Page<EmcOrderTaskResultBean> selectAnalysisProjectTaskResultValidation(RestJsonWrapperBean wrapper) {
        List<EmcProjectBean> analysisProjectList = wrapper.parse(EmcProjectBean.class);
        MapperParameter parameter = wrapper.extractMapFilter();
        parameter.setFilter(analysisProjectList, "PROJID", "MONITORTMPLID");
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectAnalysisProjectTaskResultValidation(parameter), rowBounds);
    }

    // -----------------------------------------------------------------------------
    // 私有方法
    // -----------------------------------------------------------------------------
    /**
     * 数据汇总-查询要汇总的样品结果
     *
     * @param wrapper
     * @return
     */
    protected List<EmcOrderTaskResultBean> getDataSummaryResultList(RestJsonWrapperBean wrapper) {
        String ids = wrapper.getParamValue("ids");
        String summaryType = wrapper.getParamValue("summaryType");
        String summaryFlag = wrapper.getParamValue("summaryFlag");

        List<Long> orderIdList = this.getOrderIdList(summaryType, ids);
        if (orderIdList.isEmpty() || orderIdList.size()<=0) {
            return Collections.emptyList();
        }

        SearchFilter filter = SearchFilter.instance().match("ORDERID", orderIdList).filter(MatchPattern.OR);
        if (Constant.YES.equals(summaryFlag)) {// 判断是否只查reportDisplay=1的测试分析项
            filter.match("REPORTDISPLAY", summaryFlag).filter(MatchPattern.EQ);
        }

        return selectListByFilter(filter);
    }

    /**
     * 根据样品结果生成数据汇总行专列对象
     */
    private List<DataSummaryDTO> getDataSummaryDTO(List<EmcOrderTaskResultBean> resultList) {
        List<DataSummaryDTO> dto = new ArrayList<>();
        for (EmcOrderTaskResultBean result : resultList) {
            DataSummaryDTO data = new DataSummaryDTO();
            data.setId(result.getId());
            data.setColId(String.valueOf(result.getTestId()) + result.getItemId() + result.getRepeatNo());
            data.setColTitle(result.getExt$Item("testname") + "<br>" + result.getItemName() + "<br>" + result.getRepeatNo());
            data.setColVal(result.getReportResult());
            data.setRowNo(result.getOrderNo());
            dto.add(data);
        }
        return dto;
    }

    /**
     * 根据样品结果生成数据汇总行专列对象
     */
    private List<DataQueryDTO> getDataQueryDTO(List<EmcOrderTaskResultBean> resultList) {
        List<DataQueryDTO> dto = new ArrayList<>();
        for (EmcOrderTaskResultBean result : resultList) {
            DataQueryDTO data = new DataQueryDTO();
            data.setId(result.getId());
            data.setColId(String.valueOf(result.getTestId()) + result.getItemId() + result.getRepeatNo());
            data.setColTitle("<font color=\"blue\">" + result.getItemAliasName() + "</font>");
            data.setColVal(result.getReportResult());
            data.setRowNo(result.getOrderNo());
            dto.add(data);
        }
        return dto;
    }

    /**
     * 根据样品结果生成数据汇总行专列对象
     */
    private List<DataQueryDTO> getDataTitleQueryDTO(List<EmcOrderTaskResultBean> resultList) {
        List<DataQueryDTO> dto = new ArrayList<>();
        List<String> colIdList = new ArrayList<>();
        for (EmcOrderTaskResultBean result : resultList) {
            DataQueryDTO data = new DataQueryDTO();
            data.setId(result.getId());
            data.setColId(String.valueOf(result.getTestId()) + result.getItemId() + result.getRepeatNo());
            data.setColTitle("<font color=\"blue\">" + result.getItemAliasName() + "</font>");
            data.setColVal(result.getReportResult());
            data.setRowNo(result.getOrderNo());
            if (!colIdList.contains(data.getColId())) {
                colIdList.add(data.getColId());
                dto.add(data);
            }
        }
        return dto;
    }

    /**
     * 通过项目,点位,批查询样品ID集合
     */
    private List<Long> getOrderIdList(String summaryType, String ids) {
        List<Long> orderIdList = null;
        if (EmcConstant.SUMMARY_TYPE_PROJECT.equals(summaryType)) {// 项目入口
            List<Long> projIdList = Arrays.asList(ids.split(",")).stream().map(e -> NumberUtils.parseLong(e)).collect(Collectors.toList());
            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(projIdList, "PROJID");
            orderIdList = orderList.stream().map(e -> e.getId()).collect(Collectors.toList());
        } else if (EmcConstant.SUMMARY_TYPE_RUN.equals(summaryType)) {// 批入口
            List<Long> runIdList = Arrays.asList(ids.split(",")).stream().map(e -> NumberUtils.parseLong(e)).collect(Collectors.toList());
            List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(runIdList, "RUNID");
            orderIdList = orderTaskList.stream().map(e -> e.getOrderId()).distinct().collect(Collectors.toList());
        } else if (EmcConstant.SUMMARY_TYPE_FOLDER.equals(summaryType)) {// 点位入口
            List<Long> folderIdList = Arrays.asList(ids.split(",")).stream().map(e -> NumberUtils.parseLong(e)).collect(Collectors.toList());
            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(folderIdList, "FOLDERID");
            orderIdList = orderList.stream().map(e -> e.getId()).collect(Collectors.toList());
        }
        return orderIdList;
    }

    /**
     * 计算当前数值小数点后的位数
     */
    private static int getRadix(String number) {
        String num = number;

        // 如果数值采用科学计数法，则先转换成实际值，再取小数位
        boolean scientificNumber = NumberUtils.isScientificNumber(number);
        if (scientificNumber) {
            num = NumberUtils.formatNumber(new BigDecimal(number));
        }

        if (!num.contains(".")) {
            return 0;
        }

        return num.length() - num.indexOf(".") - 1;
    }

}
