package com.sunwayworld.emc.module.mdm.bizmdm.test.io;

import java.util.ArrayList;
import java.util.List;

import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.testcategory.bean.EmcTestCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.testcategory.service.EmcTestCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodDTO;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;

@Component(value = "emcTestMethodImportFileFunction")
public class EmcTestMethodImportFileFunction implements CoreFileImportFunction {

    @Autowired
    private EmcMethodService emcMethodService;

    @Autowired
    private EmcTestCategoryService emcTestCategoryService;

    @Autowired
    private EmcTestService emcTestService;

    @Autowired
    private CoreOrgService coreOrgService;

    @Autowired
    private CoreCodeService coreCodeService;

    @Autowired
    private CoreUserService coreUserService;

    @Autowired
    private EmcTestMethodService emcTestMethodService;

    @Override
    @Transactional
    public CoreFileImportResultDTO apply(CoreFileBean file) {
        CoreFileImportResultDTO coreFileImportResultDTO = new CoreFileImportResultDTO();

        List<EmcTestMethodDTO> testMethodDTOList = ExcelHelper.readDataFromExcel(CoreFileUtils.getLocalPath(file).toString(), 1, 2, EmcTestMethodDTO.class);

        if (testMethodDTOList.size() > 0) {
            List<EmcTestCategoryBean> testCategoryList = emcTestCategoryService.selectSelectableList();

            List<EmcTestBean> testList = emcTestService.selectAll();
            List<EmcTestMethodBean> testMethodAllList = emcTestMethodService.selectAll();
            List<EmcMethodBean> methodList = emcMethodService.selectListByFilter(SearchFilter.instance().match("ACTIVATEDFLAG", "1").filter(MatchPattern.SEQ));

            List<CoreOrgBean> orgList = coreOrgService.selectAll();

            List<CoreCodeBean> displayMonitorBasisList = coreCodeService.selectListByFilter(SearchFilter.instance().match("CODECATEGORYID", "displayMonitorBasis").filter(MatchPattern.SEQ));

            List<CoreCodeBean> yesOrNoList = coreCodeService.selectListByFilter(SearchFilter.instance().match("CODECATEGORYID", "yesorno").filter(MatchPattern.SEQ));

            List<CoreUserBean> userList = coreUserService
                    .selectListByFilter(SearchFilter.instance().match("STATUS", "deprecated").filter(MatchPattern.DIFFER).match("LASTSUSPENDEDFLAG", "0").filter(MatchPattern.SEQ));

            List<EmcTestMethodBean> testMethodList = new ArrayList<>();
            
            for(EmcTestMethodDTO e : testMethodDTOList) {
                EmcTestMethodBean testMethod = new EmcTestMethodBean();
                testCategoryList.forEach(m -> {
                    if (m.getTestCategoryDesc().equals(e.getTestCategoryName())) {
                        e.setTestCategoryId(m.getId());
                    }
                });
                
                testList.forEach(m -> {
                    if (m.getTestName().equals(e.getTestName())) {
                        e.setTestId(m.getId());
                    }
                });
                
                methodList.forEach(m -> {
                    if (m.getMethodName().equals(e.getMethodName())) {
                        e.setMethodId(m.getId());
                    }
                });
                boolean flag = testMethodAllList.parallelStream().anyMatch(a -> ObjectUtils.equals(a.getTestId(), e.getTestId()) && ObjectUtils.equals(a.getTestCategoryId(), e.getTestCategoryId()) && ObjectUtils.equals(a.getMethodId(), e.getMethodId()));
                if(flag) {
                    continue;
                }
                
                orgList.forEach(m -> {
                    if (m.getOrgName().equals(e.getAnalysisOrgName())) {
                        e.setAnalysisOrgId(m.getId());
                    }
                });
                
                displayMonitorBasisList.forEach(m -> {
                    if (m.getCodeName().equals(e.getDisplayMonitorBasisName())) {
                        e.setDisplayMonitorBasis(m.getCode());
                    }
                });
                
                yesOrNoList.forEach(m -> {
                    if (m.getCodeName().equals(e.getAuthenticationName())) {
                        e.setAuthentication(m.getCode());
                    }
                    if (m.getCodeName().equals(e.getApprovalName())) {
                        e.setApproval(m.getCode());
                    }
                    if (m.getCodeName().equals(e.getDefaultFlagName())) {
                        e.setDefaultFlag(m.getCode());
                    }
                });
                
                if (!ObjectUtils.isEmpty(e.getInputUserName())) {
                    String[] userNameArr = e.getInputUserName().split(",");
                    e.setInputUserId("");
                    userList.forEach(m -> {
                        for (String userName : userNameArr) {
                            if (m.getUserName().equals(userName)) {
                                e.setInputUserId(e.getInputUserId() + m.getId() + ",");
                            }
                        }
                    });
                    if (!ObjectUtils.isEmpty(e.getInputUserId())) {
                        e.setInputUserId(e.getInputUserId().substring(0, e.getInputUserId().length() - 1));
                    }
                }
                
                BeanUtils.copyProperties(e, testMethod);
                testMethod.setId(ApplicationContextHelper.getNextIdentity());
                testMethodList.add(testMethod);
            }

            emcTestMethodService.getDao().insert(testMethodList);
        }
        return coreFileImportResultDTO;
    }
}
