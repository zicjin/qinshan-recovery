package com.sunwayworld.emc.module.bp.project.project.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.PfBizConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectDirectorService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjSchemeService;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.emc.module.bp.project.projscheme.service.PfProjSchemeService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportSendInfoBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportSendInfoService;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.bean.EmcMonitorTmplGroupBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.service.EmcMonitorTmplGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2021年07月19日
 */
@GikamBean
@Repository
public class PfProjectCompleteTaskValidator extends EmcProjectCompleteTaskValidator {

    @Autowired
    private EmcProjSchemeService emcProjSchemeService;

    @Autowired
    private EmcProjectService projectService;

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcMonitorTmplGroupService monitorTmplGroupService;

    @Autowired
    private EmcMonitorTmplService monitorTmplService;

    @Autowired
    private EmcProjOrderService projOrderService;

    @Autowired
    private PfProjSchemeService projSchemeService;

    @Autowired
    private EmcReportSendInfoService reportSendInfoService;

    @Autowired
    private EmcProjectDirectorService emcProjectDirectorService;

    @Autowired
    private EmcOrderItemGroupService emcOrderItemGroupService;

    @Autowired
    private EmcOrderItemService emcOrderItemService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);

        List<Long> projectIdList = wrapper.parse(EmcProjectBean.class).stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        List projDirectorList = emcProjectDirectorService.getDao().selectListByOneColumnValues(projectIdList, "PROJECTID", new Order[0]);
        List<EmcProjectBean> projectList = projectService.selectListByFilter(SearchFilter.instance().match("id", projectIdList).filter(MatchPattern.OR));
        if (projectList.stream().anyMatch(e -> e.getEntrustDate() == null)) {
            this.addConstraintViolation("委托日期不能为空");
            return false;
        }

        List<EmcProjectBean> advisoryProjectList = projectList.stream().filter(p -> StringUtils.endsWith(p.getProjNo(), "X")).collect(Collectors.toList());
        List<EmcProjectBean> normalProjectList = projectList.stream().filter(p -> !StringUtils.endsWith(p.getProjNo(), "X")).collect(Collectors.toList());

        if (!advisoryProjectList.isEmpty()) {
            List<EmcMonitorTmplGroupBean> advisoryGroup = monitorTmplGroupService.selectListByFilter(SearchFilter.instance().match("MONITORTMPLGROUP", PfBizConstant.ADVISORY_MONITOR_TMPL_GROUP).filter(MatchPattern.SB));
            List<Long> advisoryIdGroup = advisoryGroup.stream().map(EmcMonitorTmplGroupBean::getId).collect(Collectors.toList());
            List<EmcMonitorTmplBean> advisoryMonitorTmplList = monitorTmplService.selectListByFilter(SearchFilter.instance().match("MONITORTMPLGROUPID", advisoryIdGroup).filter(MatchPattern.OR).match("ORGID", LocalContextHelper.getLoginUser().getExt$Item("parentOrgId")).filter(MatchPattern.SEQ));
            List<String> advisoryMonitorTmplIdList = advisoryMonitorTmplList.stream().map(EmcMonitorTmplBean::getId).collect(Collectors.toList());

            List<EmcProjectBean> samplingAdvisoryProjectList = advisoryProjectList.stream().filter(p -> Constant.YES.equals(p.getSample())).collect(Collectors.toList());
            List<EmcProjectBean> otherAdvisoryProjectList = advisoryProjectList.stream().filter(p -> !Constant.YES.equals(p.getSample())).collect(Collectors.toList());

            List<Long> samplingAdvisoryProjectIdList = samplingAdvisoryProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());
            List<Long> otherAdvisoryProjectIdList = otherAdvisoryProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());

            List<EmcProjSchemeBean> schemeList = projSchemeService.getDao().selectListByOneColumnValues(samplingAdvisoryProjectIdList, "projId", Arrays.asList("id", "projId", "monitorTmplId"));
            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(otherAdvisoryProjectIdList, "projId", Arrays.asList("id", "projId", "monitorTmplId"));

            Map<Long, List<EmcProjSchemeBean>> schemeMap = schemeList.stream().collect(Collectors.groupingBy(EmcProjSchemeBean::getProjId));
            Map<Long, List<EmcOrderBean>> orderMap = orderList.stream().collect(Collectors.groupingBy(EmcOrderBean::getProjId));

            List<Long> advisoryNormalProjectIdList = new ArrayList<>();
            schemeMap.forEach((projectId, projectSchemeList) -> {
                List<String> curMonitorTmplIdList = projectSchemeList.stream().map(EmcProjSchemeBean::getMonitorTmplId).distinct().collect(Collectors.toList());
                // 如果是咨询类项目，但是下了常规的监测方案，而不是咨询类监测方案，此时需要将其当作常规项目
                boolean noneMatched = curMonitorTmplIdList.stream().noneMatch(advisoryMonitorTmplIdList::contains);
                if (noneMatched) {
                    advisoryNormalProjectIdList.add(projectId);
                }
            });

            orderMap.forEach((projectId, projectOrderList) -> {
                List<String> curMonitorTmplIdList = projectOrderList.stream().map(EmcOrderBean::getMonitorTmplId).distinct().collect(Collectors.toList());
                // 如果是咨询类项目，但是下了常规的监测方案，而不是咨询类监测方案，此时需要将其当作常规项目
                boolean noneMatched = curMonitorTmplIdList.stream().noneMatch(advisoryMonitorTmplIdList::contains);
                if (noneMatched) {
                    advisoryNormalProjectIdList.add(projectId);
                }
            });

            List<EmcProjectBean> advisoryNormalProjectList = advisoryProjectList.stream().filter(p -> advisoryNormalProjectIdList.contains(p.getId())).collect(Collectors.toList());
            normalProjectList.addAll(advisoryNormalProjectList);
        }

        if (normalProjectList.isEmpty()) {
            return true;
        }

        // 对于来样的任务，如果没有添加样品，则不让提交
        List<Long> noSamplingProjectIdList = normalProjectList.stream().filter(project -> !StringUtils.equals(project.getSample(), Constant.YES)).map(EmcProjectBean::getId).collect(Collectors.toList());
        if (!noSamplingProjectIdList.isEmpty()) {
            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(noSamplingProjectIdList, "PROJID");
            boolean noOrder = noSamplingProjectIdList.stream().anyMatch(projectId -> orderList.stream().noneMatch(order -> ObjectUtils.equals(projectId, order.getProjId())));
            if (noOrder) {
                this.addConstraintViolation("EMC.MODULE.BP.PROJECT.PROJECTS.TIP.NO_PROJECT_ORDER");
                return false;
            }

            boolean emptyClientOrderName = orderList.stream().anyMatch(order -> StringUtils.isEmpty(order.getClientOrderName()));
            if (emptyClientOrderName) {
                this.addConstraintViolation("EMC.MODULE.BP.PROJECT.PROJECTS.TIP.CLENT_ORDER_NAME_IS_EMPTY");
                return false;
            }

            boolean emptySenderId = orderList.stream().anyMatch(order -> StringUtils.isEmpty(order.getSenderId()));
            if (emptySenderId) {
                this.addConstraintViolation("送样人不能为空");
                return false;
            }

            boolean emptySendDateTime = orderList.stream().anyMatch(order -> ObjectUtils.isEmpty(order.getSendDate()) || StringUtils.isEmpty(order.getSendTime()));
            if (emptySendDateTime) {
                this.addConstraintViolation("EMC.MODULE.BP.PROJECT.PROJECTS.TIP.SEND_DATE_OR_TIME_IS_EMPTY");
                return false;
            }

            List<EmcOrderBean> emptyPdupOrderList = orderList.stream().filter(order -> PfConstant.QCCATEGORY_QCCATEGORYID_P_DUP.equals(order.getQcCategoryCode()) && StringUtils.isEmpty(order.getParentOrderNo())).collect(Collectors.toList());
            if (!emptyPdupOrderList.isEmpty()) {
                String orderNos = emptyPdupOrderList.stream().map(EmcOrderBean::getOrderNo).collect(Collectors.joining(","));
                this.addConstraintViolation(I18nHelper.getMessage("EMC.MODULE.BP.PROJECT.PROJECTS.TIP.HAS_NO_PARENT_ORDER_NO", orderNos));
                return false;
            }

            List<Long> orderIdList = orderList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList());
            List<EmcOrderItemGroupBean> orderItemGroupList = emcOrderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");
            for (EmcOrderBean order : orderList) {
                if (orderItemGroupList.stream().noneMatch(group -> ObjectUtils.equals(group.getOrderId(), order.getId()))) {
                    this.addConstraintViolation(I18nHelper.getMessage("存在样品下没有维护分析项组：" + order.getOrderNo()));
                    return false;
                }
            }
            List<Long> orderItemGroupIdList = orderItemGroupList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList());
            int batchSize = 1000;
            List<EmcOrderItemBean> orderItemList = new ArrayList<>();
            for (int i = 0; i < orderItemGroupIdList.size(); i += batchSize) {
                List<Long> subList = orderItemGroupIdList.subList(i, Math.min(i + batchSize, orderItemGroupIdList.size()));
                List<EmcOrderItemBean> batchResult = emcOrderItemService.getDao().selectListByOneColumnValues(subList, "ORDERITEMGROUPID");
                orderItemList.addAll(batchResult);
            }
            for (EmcOrderItemGroupBean group : orderItemGroupList) {
                if (orderItemList.stream().noneMatch(item -> ObjectUtils.equals(item.getOrderItemGroupId(), group.getId()))) {
                    this.addConstraintViolation(I18nHelper.getMessage("存在分析项组下没有维护分析项：" + group.getItemGroup()));
                    return false;
                }
            }
        }

        List<Long> sampleProjectIdList = normalProjectList.stream().filter(e -> Constant.YES.equals(e.getSample())).map(EmcProjectBean::getId).collect(Collectors.toList());

        List<EmcProjSchemeBean> schemeList = emcProjSchemeService.getDao().selectListByOneColumnValues(sampleProjectIdList, "PROJID", new Order[0]);

        for (Long projId : sampleProjectIdList) {
            List<EmcProjSchemeBean> projSchemeList = schemeList.stream().filter(e -> ObjectUtils.equals(projId, e.getProjId()) && !StringUtils.equals(e.getReferFolder(), Constant.YES)).collect(Collectors.toList());
            if (projSchemeList.isEmpty()) {
                this.addConstraintViolation("EMC.MODULE.BP.PROJECT.PROJECTS.TIP.NO_PROJECT_SCHEME");
                return false;
            }
        }

        boolean emptyMonitorPointName = schemeList.stream().anyMatch(scheme -> StringUtils.isEmpty(scheme.getMonitorPointName()));
        if (emptyMonitorPointName) {
            this.addConstraintViolation("EMC.MODULE.BP.PROJECT.PROJECTS.TIP.MONITOR_POINT_NAME_IS_EMPTY");
            return false;
        }

        List<EmcProjSchemeBean> noSampleOrgSchemeList = schemeList.stream().filter(e->ObjectUtils.isEmpty(e.getSampleOrgId())).collect(Collectors.toList());
        if(!noSampleOrgSchemeList.isEmpty()){
            this.addConstraintViolation("EMC.MODULE.BP.PROJECT.PROJECTS.TIP.NO_SAMPLE_ORG");
            return false;
        }

        boolean emptyCycleOrFrequency = schemeList.stream().anyMatch(scheme -> ObjectUtils.isEmpty(scheme.getCycle()) || ObjectUtils.isEmpty(scheme.getFrequency()));
        if(emptyCycleOrFrequency){
            this.addConstraintViolation("EMC.MODULE.BP.PROJECT.PROJECTS.TIP.CYCLE_OR_FREQUENCY_IS_EMPTY");
            return false;
        }

        return true;
    }
}
