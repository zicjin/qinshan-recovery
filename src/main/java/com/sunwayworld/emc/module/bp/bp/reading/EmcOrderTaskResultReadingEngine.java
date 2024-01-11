package com.sunwayworld.emc.module.bp.bp.reading;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestItemBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestItemService;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.bean.EmcEquiptAcqFileBean;
import com.sunwayworld.emc.module.mdm.equipt.filescript.bean.EmcEquiptFileScriptBean;
import com.sunwayworld.emc.module.mdm.equipt.filescript.service.EmcEquiptFileScriptService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.ScriptException;
import com.sunwayworld.framework.formula.GikamComputingDataHolder;
import com.sunwayworld.framework.formula.GikamFormulaHelper;
import com.sunwayworld.framework.formula.GikamFormulaRegistry;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.script.BshExecutor;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 仪器采集脚本解析
 * 
 * @author magh@sunwayworld.com
 * @date 2020-1-19
 *
 */
public class EmcOrderTaskResultReadingEngine {

    private static final Logger logger = LogManager.getLogger("");

    private EmcEquiptAcqFileBean equiptAcqFile;
    private List<EmcEquiptTestItemBean> equiptTestItemList;
    private List<String> testNameList;
    private List<EmcOrderTaskResultBean> orderTaskResultList;

    private EmcOrderTaskResultReadingEngine() {
    }

    public static EmcOrderTaskResultReadingEngine getEngine(EmcEquiptAcqFileBean equiptAcqFile) {
        EmcOrderTaskResultReadingEngine engine = new EmcOrderTaskResultReadingEngine();
        engine.equiptAcqFile = equiptAcqFile;

        EmcEquiptTestItemService equiptTestItemService = ApplicationContextHelper.getBean(EmcEquiptTestItemService.class);
        List<EmcEquiptTestItemBean> equiptTestItemList = equiptTestItemService.selectListByFilter(SearchFilter.instance().match("equiptId", equiptAcqFile.getEquiptId()).filter(MatchPattern.EQ));
        engine.equiptTestItemList = equiptTestItemList;

        EmcTestService testService = ApplicationContextHelper.getBean(EmcTestService.class);
        List<String> testNameList = testService.selectAll().stream().map(e -> e.getTestName()).distinct().collect(Collectors.toList());
        engine.testNameList = testNameList;

        EmcOrderTaskResultService orderTaskResultService = ApplicationContextHelper.getBean(EmcOrderTaskResultService.class);
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.selectAll().stream().collect(Collectors.toList());
        engine.orderTaskResultList = orderTaskResultList;

        return engine;
    }

    public BshExecutor getBshExecutor() {
        BshExecutor bsh = BshExecutor.instance();

        bsh.eval("import com.sunwayworld.emc.module.bp.bp.bean.*;");
        bsh.eval("import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.*;");

        GikamFormulaRegistry.INSTANCE.getRepository().forEach(c -> {
            bsh.eval("import static " + c.getName() + ".*;");
        });

        return bsh;
    }

    @SuppressWarnings("unchecked")
    public List<EmcOrderTaskResultBean> read() {

        try {

            BshExecutor bsh = this.getBshExecutor();

            EmcEquiptAcqFileBean equiptAcqFile = this.equiptAcqFile;

            EmcEquiptService equiptService = ApplicationContextHelper.getBean(EmcEquiptService.class);

            Long equiptId = equiptAcqFile.getEquiptId();
            EmcEquiptBean equipt = equiptService.selectById(equiptId);

            EmcEquiptFileScriptService equiptFileScriptService = ApplicationContextHelper.getBean(EmcEquiptFileScriptService.class);
            EmcEquiptFileScriptBean equiptFileScript = equiptFileScriptService.selectById(equipt.getAcqScriptId());

            if (StringUtils.isEmpty(equiptFileScript.getScript())) {
                throw new ScriptException("EMC.MODULE.BP.BP.FOLDERS.READING.EXCEPTION.NO_SCRIPT");
            }

            GikamComputingDataHolder.put("equiptAcqFile", equiptAcqFile);
            GikamComputingDataHolder.put("equiptTestItemList", equiptTestItemList);
            GikamComputingDataHolder.put("testNameList", testNameList);
            GikamComputingDataHolder.put("orderTaskResultList", orderTaskResultList);

            String script = GikamFormulaHelper.getRevisedScript(equiptFileScript.getScript());

            return (List<EmcOrderTaskResultBean>) bsh.eval(script);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            GikamComputingDataHolder.removeCurrentData();
        }
        return Collections.emptyList();
    }

}
