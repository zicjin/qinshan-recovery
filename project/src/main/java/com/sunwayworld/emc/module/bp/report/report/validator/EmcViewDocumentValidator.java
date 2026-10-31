package com.sunwayworld.emc.module.bp.report.report.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author magh 2019年12月18日
 */
@Repository
public class EmcViewDocumentValidator implements DataValidator {

    @Autowired
    private EmcProjectService projectService;

    @Autowired
    private EmcFolderService folderService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);

        EmcReportBean report = wrapper.parseUnique(EmcReportBean.class);
        EmcProjectBean project = projectService.selectDetail(report.getProjId());
        List<EmcFolderBean> folderList = folderService.selectListByFilter(SearchFilter.instance().match("projId", project.getId()).filter(MatchPattern.EQ)
                .match("termination", "0").filter(MatchPattern.EQ));

        if (folderList.isEmpty() || folderList.size() == 0) {
            this.addConstraintViolation("EMC.MODULE.BP.REPORT.REPORTS.TIP.NO_FOLDER");
            return false;
        }

        return true;
    }

}
