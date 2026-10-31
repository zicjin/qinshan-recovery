package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.resource.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.bean.EmcEquiptAcqResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasParamBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasPreAnalyBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.resource.EmcEquiptAcqScriptResource;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEasParamService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEasPreAnalyService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEasResultService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEquiptAcqScriptService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule(value = "设备台账-仪器采集脚本配置")
@RestController
@GikamBean
public class EmcEquiptAcqScriptResourceImpl implements EmcEquiptAcqScriptResource {

    @Autowired
    private EmcEquiptAcqScriptService equiptAcqScriptService;
    @Autowired
    private EmcEasParamService easParamService;
    @Autowired
    private EmcEasPreAnalyService easPreAnalyService;
    @Autowired
    private EmcEasResultService easResultService;

    @Override
    public EmcEquiptAcqScriptService getService() {
        return equiptAcqScriptService;
    }

    @Log(value = "新增设备台账-仪器采集脚本参数", type = LogType.INSERT)
    @Override
    public void insertParam(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().insertParam(id, wrapper);
    }

    @Log(value = "删除设备台账-仪器采集脚本参数", type = LogType.DELETE)
    @Override
    public void deleteParam(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().deleteParam(id, wrapper);
    }

    @Log(value = "新增设备台账-仪器采集脚本预处理", type = LogType.INSERT)
    @Override
    public Long insertPreAnaly(@PathVariable Long id) {
        return getService().insertPreAnaly(id);
    }

    @Log(value = "删除设备台账-仪器采集脚本预处理", type = LogType.DELETE)
    @Override
    public void deletePreAnaly(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().deletePreAnaly(id, wrapper);
    }

    @Log(value = "新增设备台账-仪器采集脚本结果参数", type = LogType.INSERT)
    @Override
    public Long insertResult(@PathVariable Long id) {
        return getService().insertResult(id);
    }

    @Log(value = "删除设备台账-仪器采集脚本结果参数", type = LogType.DELETE)
    @Override
    public void deleteResult(Long id, RestJsonWrapperBean wrapper) {
        getService().deleteResult(id, wrapper);
    }

    @Log(value = "查询设备台账-仪器采集脚本参数", type = LogType.SELECT)
    @Override
    public Page<EmcEasParamBean> selectSearchParamPaginationByScript(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return easParamService.selectPaginationByFilter(SearchFilter.instance().match("SCRIPTID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Log(value = "查询设备台账-仪器采集脚本预处理", type = LogType.SELECT)
    @Override
    public Page<EmcEasPreAnalyBean> selectSearchPreAnalyPaginationByScript(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return easPreAnalyService.selectPaginationByFilter(SearchFilter.instance().match("SCRIPTID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Log(value = "查询设备台账-仪器采集脚本结果参数", type = LogType.SELECT)
    @Override
    public Page<EmcEasResultBean> selectSearchResultPaginationByScript(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return easResultService.selectPaginationByFilter(SearchFilter.instance().match("SCRIPTID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Log(value = "测试解析 获取文件文本内容 查询文件信息", type = LogType.SELECT)
    @Override
    public Map<String, String> extractTextFromFileForTest(@PathVariable Long id, @PathVariable Long fileId) {
        return getService().extractTextFromFileForTest(id, fileId);
    }

    @Log(value = "测试解析 获取预处理结果", type = LogType.SELECT)
    @Override
    public Map<String, String> preAnalyTest(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().preAnalyTest(id, wrapper);
    }

    @Log(value = "测试解析 获取解析到结果数据", type = LogType.SELECT)
    @Override
    public Page<EmcEquiptAcqResultBean> analyResultTest(@PathVariable Long id, @PathVariable Long fileId, RestJsonWrapperBean wrapper) {
        return getService().analyResultTest(id, fileId);
    }

    @Override
    @Log(value = "验证编码名称唯一性", type = LogType.VALIDATE)
    @RequestMapping(value = "/results/action/validate-unique", method = RequestMethod.POST)
    public RestValidationResultBean validateFieldName(RestJsonWrapperBean wrapper) {
        return getService().validateFieldName(wrapper);
    }

    @Override
    @Log(value = "复制仪器采集脚本结果参数", type = LogType.INSERT)
    public void copyResult(Long id, RestJsonWrapperBean wrapper) {
        getService().copyResult(id, wrapper);
    }

}
