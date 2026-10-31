package com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.validating;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.bean.EmcDataCorrelationDTO;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.formula.GikamComputingDataHolder;
import com.sunwayworld.framework.formula.GikamFormulaHelper;
import com.sunwayworld.framework.formula.GikamFormulaRegistry;
import com.sunwayworld.framework.script.BshExecutor;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 数据相关性校验器
 * @author shixw@sunwayworld.com
 * @date 2020-1-8
 *
 */
public class EmcDataCorrelationValidatingEngine {

    private List<EmcDataCorrelationDTO> dataCorrelationDTOList;

    private static final Logger logger = LogManager.getLogger("");
    
    // 校验结果：1:合理;0:不合理
    private static final String CORRELATION_VALIDATE_FLAG_IRRATIONAL = "0";

    private EmcDataCorrelationValidatingEngine() {
    }

    public static EmcDataCorrelationValidatingEngine getEngine(List<EmcDataCorrelationDTO> dataCorrelationDTOList) {
        EmcDataCorrelationValidatingEngine engine = new EmcDataCorrelationValidatingEngine();
        engine.dataCorrelationDTOList = dataCorrelationDTOList;

        return engine;
    }

    public BshExecutor getBshExecutor() {
        BshExecutor bsh = BshExecutor.instance();

        bsh.eval("import com.sunwayworld.emc.module.bp.bp.computing.bean.OrderTaskResultDTO;");
        bsh.eval("import com.sunwayworld.framework.utils.StringUtils;");
        bsh.eval("import com.sunwayworld.framework.utils.ArithUtils;");
        bsh.eval("import com.sunwayworld.framework.utils.NumberUtils;");
        bsh.eval("import com.sunwayworld.emc.module.bp.bp.bean.*;");
        bsh.eval("import java.text.*;");

        GikamFormulaRegistry.INSTANCE.getRepository().forEach(c -> {
            bsh.eval("import static " + c.getName() + ".*;");
        });

        return bsh;
    }

    public boolean validate() {
        //是否全部合理数据
        boolean allRational = true;
        
        BshExecutor bsh = this.getBshExecutor();

        List<EmcDataCorrelationDTO> dataCorrelationDTOList = this.dataCorrelationDTOList;

        List<EmcOrderTaskResultBean> updateOrderTaskResultList = new ArrayList<>();
        for (EmcDataCorrelationDTO dataCorrelationDTO : dataCorrelationDTOList) {
            try {
                GikamComputingDataHolder.setCurrentData(dataCorrelationDTO);

                String script = GikamFormulaHelper.getRevisedScript(dataCorrelationDTO.getDataCorrelation().getRelateScript());

                if (!StringUtils.isEmpty(script)) {
                    Object validateValue = bsh.eval(script);

                    if (!ObjectUtils.isEmpty(validateValue)) {
                        Boolean validateFlag = (Boolean) validateValue;

                        if (!validateFlag) {
                            updateOrderTaskResultList.addAll(dataCorrelationDTO.getOrderTaskResultList());
                            allRational = false;
                        }

                    }
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                GikamComputingDataHolder.removeCurrentData();
            }
        }

        EmcOrderTaskResultBean updateOrderTaskResult = new EmcOrderTaskResultBean();
        updateOrderTaskResult.setCorrelationValidateFlag(CORRELATION_VALIDATE_FLAG_IRRATIONAL);

        EmcOrderTaskResultService taskResultService = ApplicationContextHelper.getBean(EmcOrderTaskResultService.class);

        taskResultService.getDao().updateByIds(updateOrderTaskResult, updateOrderTaskResultList.stream().map(e -> e.getId()).collect(Collectors.toList()), "correlationValidateFlag");
        
        return allRational;
    }

}
