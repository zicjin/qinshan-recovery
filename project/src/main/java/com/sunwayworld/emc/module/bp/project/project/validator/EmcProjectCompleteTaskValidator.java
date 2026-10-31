package com.sunwayworld.emc.module.bp.project.project.validator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjSchemeService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

@GikamBean
@Repository
public class EmcProjectCompleteTaskValidator implements DataValidator {

    @Autowired
    private EmcProjSchemeService emcProjSchemeService;

    @Autowired
    private EmcProjectService projectService;

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcFolderService folderService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);

        List<Long> projectIdList = wrapper.parse(EmcProjectBean.class).stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        List<EmcProjectBean> projectList = projectService.selectListByFilter(SearchFilter.instance().match("id", projectIdList).filter(MatchPattern.OR));

        // 判断项目是否需要检测，如果都不检测，则可不添加方案
        boolean notDetect = projectList.stream().allMatch(p -> Constant.NO.equals(p.getExt$Item("detect")));
        if (notDetect) {
            return true;
        }

        // 对于来样的任务，如果没有添加样品，则不让提交
        List<Long> noSamplingProjectIdList = projectList.stream().filter(project -> !StringUtils.equals(project.getSample(), Constant.YES)).map(EmcProjectBean::getId).collect(Collectors.toList());
        if (!noSamplingProjectIdList.isEmpty()) {
            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(noSamplingProjectIdList, "PROJID", Arrays.asList("ID", "PROJID"));
            boolean noOrder = noSamplingProjectIdList.stream().anyMatch(projectId -> orderList.stream().noneMatch(order -> ObjectUtils.equals(projectId, order.getProjId())));
            if (noOrder) {
                this.addConstraintViolation("EMC.MODULE.BP.PROJECT.PROJECTS.TIP.NO_PROJECT_ORDER");
                return false;
            }

            List<EmcFolderBean> folderList = folderService.getDao().selectListByOneColumnValues(noSamplingProjectIdList, "PROJID", Arrays.asList("ID", "FOLDERNO", "PROJID"));
            for (Long projectId : noSamplingProjectIdList) {
                List<EmcFolderBean> projectFolderList = folderList.stream().filter(f -> ObjectUtils.equals(f.getProjId(), projectId)).collect(Collectors.toList());
                List<String> projectFolderNoList = projectFolderList.stream().map(EmcFolderBean::getFolderNo).sorted(Comparator.naturalOrder()).collect(Collectors.toList());
                // 判断是否存在001点位和001样品
                if (projectFolderNoList.isEmpty() || !StringUtils.endsWith(projectFolderNoList.get(0), "001")) {
                    this.addConstraintViolation("EMC.MODULE.BP.PROJECT.PROJECTS.TIP.NOT_EXIST_FIRST_ORDER");
                    return false;
                }

                // 判断样品号是否连续
                List<Integer> noList = projectFolderNoList.stream().mapToInt(no -> NumberUtils.parseInt(no.substring(no.length() - 3))).boxed().collect(Collectors.toList());
                for(int i = 0; i < noList.size() - 1; i++) {
                    if (noList.get(i + 1) != noList.get(i) + 1) {
                        this.addConstraintViolation("EMC.MODULE.BP.PROJECT.PROJECTS.TIP.ORDER_NO_DISCONTINUITY");
                        return false;
                    }
                }
            }

        }

        List<Long> sampleProjectIdList = projectList.stream().filter(e -> Constant.YES.equals(e.getSample())
                && !Constant.NO.equals(e.getExt$Item("detect"))).map(EmcProjectBean::getId).collect(Collectors.toList());

        List<EmcProjSchemeBean> schemeList = emcProjSchemeService.getDao().selectListByOneColumnValues(sampleProjectIdList, "PROJID");

        for (Long projId : sampleProjectIdList) {
            List<EmcProjSchemeBean> projSchemeList = schemeList.stream().filter(e -> ObjectUtils.equals(projId, e.getProjId())).collect(Collectors.toList());
            if (projSchemeList.isEmpty()) {
                this.addConstraintViolation("EMC.MODULE.BP.PROJECT.PROJECTS.TIP.NO_PROJECT_SCHEME");
                return false;
            }

            // 判断当前项目方案是否缺少001点位，如果缺少001点位，则不让提交
            List<String> folderNoList = projSchemeList.stream().map(EmcProjSchemeBean::getFolderNo).sorted(Comparator.naturalOrder()).collect(Collectors.toList());
            if (!StringUtils.endsWith(folderNoList.get(0), "001")) {
                this.addConstraintViolation("EMC.MODULE.BP.PROJECT.PROJECTS.TIP.NOT_EXIST_FIRST_FOLDER");
                return false;
            }

            // 判断监测方案下的点位号是否连续
            List<Integer> noList = folderNoList.stream().mapToInt(no -> NumberUtils.parseInt(no.substring(no.length() - 3))).boxed().collect(Collectors.toList());
            for(int i = 0; i < noList.size() - 1; i++) {
                if (noList.get(i + 1) != noList.get(i) + 1) {
                    this.addConstraintViolation("EMC.MODULE.BP.PROJECT.PROJECTS.TIP.FOLDER_NO_DISCONTINUITY");
                    return false;
                }
            }

        }

        List<EmcProjSchemeBean> noSampleOrgSchemeList = schemeList.stream().filter(e->ObjectUtils.isEmpty(e.getSampleOrgId())).collect(Collectors.toList());
        if(!noSampleOrgSchemeList.isEmpty()){
            this.addConstraintViolation("EMC.MODULE.BP.PROJECT.PROJECTS.TIP.NO_SAMPLE_ORG");
            return false;
        }

        return true;
    }
}
