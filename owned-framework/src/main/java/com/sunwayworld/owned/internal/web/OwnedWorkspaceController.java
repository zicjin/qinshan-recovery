package com.sunwayworld.owned.internal.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Source-owned route facade for the original workspace controller family.
 *
 * The project view names remain the source of truth.  This facade supplies the
 * missing framework superclass seam and deliberately keeps both historical
 * core and EMC URL prefixes while page behavior is migrated incrementally.
 */
@Controller
@RequestMapping({
        "/secure/core/module/index/workspace",
        "/secure/emc/module/index/workspace"
})
public final class OwnedWorkspaceController {
    @GetMapping({"", "/", "/index"})
    public String index() {
        return "emc/module/index/workspace/index";
    }

    @GetMapping("/todo")
    public String todo() {
        return "emc/module/index/workspace/emc-todo";
    }

    @GetMapping("/analysis-department-statistics")
    public String analysisDepartmentStatistics() {
        return "emc/module/index/workspace/emc-analysis-department-statistics";
    }

    @GetMapping("/personnel-workload-statistics")
    public String personnelWorkloadStatistics() {
        return "emc/module/index/workspace/emc-personnel-workload-statistics";
    }

    @GetMapping("/project-statistics")
    public String projectStatistics() {
        return "emc/module/index/workspace/emc-project-statistics";
    }

    @GetMapping("/annual-project-statistics")
    public String annualProjectStatistics() {
        return "emc/module/index/workspace/emc-annual-project-statistics";
    }

    @GetMapping("/report")
    public String report() {
        return "emc/module/index/workspace/emc-report";
    }

    @GetMapping("/run-analysis")
    public String runAnalysis() {
        return "emc/module/index/workspace/emc-run-analysis";
    }

    @GetMapping("/sample")
    public String sample() {
        return "emc/module/index/workspace/emc-sample";
    }

    @GetMapping("/business-process-diagram")
    public String businessProcessDiagram() {
        return "emc/module/index/workspace/emc-business-process-diagram";
    }
}
