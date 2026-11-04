/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.fr.helper;

import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FrReportHelper {
    private static CoreReportService coreReportService;
    public static final String FINE_DEPLOYMENT_MODE_INTEGRATION = "integration";
    public static final String FINE_DEPLOYMENT_MODE_INDEPENDENT = "independent";
    public static final String FINE_VERSION_9 = "9.0";
    public static final String FINE_VERSION_10 = "10.0";
    public static final String FINE_VERSION_11 = "11.0";

    public static Path getLocalReportletPath() {
        Path reportletPath = null;
        String deploymentMode = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.deploy-mode");
        if (FINE_DEPLOYMENT_MODE_INTEGRATION.equals(deploymentMode)) {
            reportletPath = FilePathManager.getApplicationRootPath().resolve("tomcat");
        } else if (FINE_DEPLOYMENT_MODE_INDEPENDENT.equals(deploymentMode)) {
            reportletPath = Paths.get(ApplicationContextHelper.getEnvironment().getProperty("sunway.config.fine.independent-deploy-path"), new String[0]);
        }
        return reportletPath.resolve("WEB-INF").resolve("reportlets");
    }

    @Autowired
    public void setCoreReportService(CoreReportService coreReportService) {
        FrReportHelper.coreReportService = coreReportService;
    }

    public static <T> List<Pair<CoreReportConfigLineBean, String>> exportByConfigId(String exportType, String reportConfigId, T t) {
        return coreReportService.exportByConfigId(exportType, reportConfigId, t);
    }

    public static <T> Pair<CoreReportConfigLineBean, String> exportByConfigLineId(String exportType, Long reportConfigLineId, T t) {
        return FrReportHelper.exportByConfigLineId(exportType, null, reportConfigLineId, t);
    }

    public static <T> Pair<CoreReportConfigLineBean, String> exportByConfigLineId(String exportType, String exportFileName, Long reportConfigLineId, T t) {
        return coreReportService.exportByConfigLineId(exportType, exportFileName, reportConfigLineId, t);
    }

    public static FilePathDTO getFilePathDTO(String exportPath) {
        if (StringUtils.isEmpty(exportPath)) {
            return null;
        }
        List<String> frPartList = ArrayUtils.asList(StringUtils.split(exportPath, "/"));
        int scopeIndex = frPartList.indexOf(FileScope.export.name());
        return FilePathDTO.of(FileScope.export.name(), frPartList.get(scopeIndex + 1), frPartList.get(scopeIndex + 2), frPartList.get(scopeIndex + 3));
    }
}
