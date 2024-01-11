package com.sunwayworld.emc.boot.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import com.sunwayworld.emc.framework.sso.EmcSsoAuthenticator;
import com.sunwayworld.emc.module.bp.bp.computing.formula.ResultFormulaRepository;
import com.sunwayworld.emc.module.bp.bp.reading.EmcOrderTaskResultReadingRepository;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.validating.formula.EmcDataCorrelationResultFormulaRepository;
import com.sunwayworld.framework.formula.GikamFormulaRegistry;
import com.sunwayworld.framework.security.sso.SsoAuthenticatorRegistry;
import com.sunwayworld.framework.utils.ArithUtils;

/**
 * 应用加载完毕后执行，例如初始化数据等
 *
 * @author zhangjr@sunwayworld.com 2019年11月2日
 *
 */
@Service
public class EmcApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * 应用加载完毕后执行的方法
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        SsoAuthenticatorRegistry.INSTANCE.register("wpsso", EmcSsoAuthenticator.class);

        GikamFormulaRegistry.INSTANCE.register(ResultFormulaRepository.class);
        GikamFormulaRegistry.INSTANCE.register(EmcDataCorrelationResultFormulaRepository.class);
        GikamFormulaRegistry.INSTANCE.register(EmcOrderTaskResultReadingRepository.class);

        // 调用工具类，再次调用时从方法区直接获取，加速加载ArithUtils工具类
        ArithUtils.eval("0+0");
    }
}
