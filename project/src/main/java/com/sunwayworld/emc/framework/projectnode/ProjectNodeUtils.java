package com.sunwayworld.emc.framework.projectnode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.framework.utils.ObjectUtils;

/**
 *
 * @author magt 项目节点状态工具类，自动判断项目节点状态更新节点
 */
@Component
public class ProjectNodeUtils {

    @Autowired
    @Lazy
    private EmcOrderTaskService orderTaskService;

    @Autowired
    @Lazy
    private EmcProjectService projectService;

    private static ProjectNodeUtils projectNodeUtils;

    // @PostConstruct初始化时进行的操作
    @PostConstruct
    public void init() {
        projectNodeUtils = this;
        projectNodeUtils.orderTaskService = this.orderTaskService;
        projectNodeUtils.projectService = this.projectService;
    }

    public static void updateNodes(List<Long> projectIdList) {

        List<EmcProjectBean> projectList = getProject(projectIdList);
        List<EmcOrderTaskBean> orderTaskList = new ArrayList<>();
        // 是否需要用ordertask表数据判断
        boolean orderTask = projectList.stream().anyMatch(e -> EmcBizConstant.PROJECT_STATUS_AUDITFINISHED.equals(e.getStatus()));
        if (orderTask) {
            orderTaskList = projectNodeUtils.orderTaskService.getDao().selectListByOneColumnValues(projectIdList, "PROJID");
        }

        // 返回更新数据
        List<EmcProjectBean> returnList = new ArrayList<>();
        for (EmcProjectBean e : projectList) {

            String projectStatus = e.getStatus();
            Long id = e.getId();

            if (EmcBizConstant.PROJECT_STATUS_CREATE.equals(projectStatus)) {
                // 项目登记
                returnList.add(getBean(id, EmcBizConstant.PROJECT_CN_NODE_XMDJ));

            } else if (EmcBizConstant.PROJECT_CN_NODE_QUALITY_CONTROL.equals(projectStatus)) {
                // 质量审核
                returnList.add(getBean(id, EmcBizConstant.PROJECT_CN_NODE_QUALITY_CONTROL));

            } else if (EmcBizConstant.PROJECT_STATUS_AUDITFINISHED.equals(projectStatus)) {
                List<EmcOrderTaskBean> selectList = orderTaskList.stream().filter(a -> ObjectUtils.equals(id, a.getProjId())).collect(Collectors.toList());

                if (isSamping(selectList)) {
                    // 采样中
                    returnList.add(getBean(id, EmcBizConstant.PROJECT_CN_NODE_CYZ));
                } else if (isAccepting(selectList)) {
                    // 样品接收
                    returnList.add(getBean(id, EmcBizConstant.PROJECT_CN_NODE_YPJS));
                } else if (isAnalysising(selectList)) {
                    // 分析中
                    returnList.add(getBean(id, EmcBizConstant.PROJECT_CN_NODE_FXZ));
                }

            } else if (EmcBizConstant.PROJECT_STATUS_REPORTEDIT.equals(projectStatus)) {
                // 报告编制
                returnList.add(getBean(id, EmcBizConstant.PROJECT_CN_NODE_BGBZ));
            }
        }
        projectNodeUtils.projectService.getDao().update(returnList, "PROJNODE");;
    }

    private static EmcProjectBean getBean(Long id, String nodeStatus) {
        EmcProjectBean filter = new EmcProjectBean();
        filter.setId(id);
        filter.setProjNode(nodeStatus);
        return filter;
    }

    private static List<EmcProjectBean> getProject(List<Long> projectIdList) {
        return projectNodeUtils.projectService.getDao().selectListByIds(projectIdList);
    }

    /**
     * 判断流程状态是否是采样中
     *
     * @param selectList
     * @return true/false
     */
    private static boolean isSamping(List<EmcOrderTaskBean> selectList) {
        return selectList.stream().anyMatch(a -> a.getStatus().indexOf(EmcBizConstant.ORDERTASK_STATUS_DRAFT) != -1);
    }

    /**
     * 判断流程状态是否是样品接收
     * 需要在isSamping()方法判断为否后使用才能保证准确性
     *
     * @param selectList
     * @return true/false
     */
    private static boolean isAccepting(List<EmcOrderTaskBean> selectList) {
        List<EmcOrderTaskBean> analysisSelectList = selectList.stream().filter(a -> EmcBizConstant.ANALYSISCATEGORY_ANALYSIS.equals(a.getAnalysisType())).collect(Collectors.toList());
        return analysisSelectList.stream().anyMatch(a -> a.getStatus().indexOf(EmcBizConstant.ORDERTASK_STATUS_PRELOGGED) != -1);
    }

    /**
     * 判断流程状态是否是分析中
     *  需要在isAccepting()方法判断为否后使用才能保证准确性
     *
     * @param selectList
     * @return true/false
     */
    private static boolean isAnalysising(List<EmcOrderTaskBean> selectList) {
        return !selectList.stream().allMatch(a -> a.getStatus().indexOf(EmcBizConstant.ORDERTASK_STATUS_DONE) != -1);
    }

}
