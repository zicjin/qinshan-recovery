package com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.service;

import java.util.List;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.bean.EmcEquiptAcqFileBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.bean.EmcEquiptAcqResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasParamBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasPreAnalyBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEquiptAcqScriptBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface EmcEquiptAcqResultService extends GenericService<EmcEquiptAcqResultBean, Long>, GenericChoosableService<EmcEquiptAcqResultBean, Long> {

    void insertResultByAnalyAcqFiles(List<EmcEquiptAcqFileBean> acqFileList);

    void insertSceneResultByAnalyAcqFiles(List<EmcEquiptAcqFileBean> acqFileList);

    String preAnalyContent(String content, List<EmcEasPreAnalyBean> scriptPreAnalyList);

    String analyParagraphArray(Long acqFileId, String rowDataSeparator, List<EmcEasParamBean> scriptParamList, List<EmcEasParamBean> orderNoParamList, List<EmcEasResultBean> scriptResultList, EmcEquiptAcqScriptBean script,
            String[] paragraphArray, List<EmcEquiptAcqResultBean> resultList);

    String analyParagraphArray(Long acqFileId, String rowDataSeparator, List<EmcEasParamBean> scriptParamList, List<EmcEasParamBean> orderNoParamList, List<EmcEasParamBean> testNameParamList,List<EmcEasResultBean> scriptResultList, EmcEquiptAcqScriptBean script,
                               String[] paragraphArray, List<EmcEquiptAcqResultBean> resultList);

    List<EmcEquiptAcqResultBean> getSceneResultList(List<String> contentList, Long scriptId);

}
