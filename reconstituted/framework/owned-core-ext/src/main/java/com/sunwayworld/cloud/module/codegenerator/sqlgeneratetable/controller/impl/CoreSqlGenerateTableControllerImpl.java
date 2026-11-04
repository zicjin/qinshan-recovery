/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.controller.impl;

import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.controller.CoreSqlGenerateTableController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@LogModule(value="sql\u751f\u6210\u5668")
@Controller
@GikamBean
public class CoreSqlGenerateTableControllerImpl
implements CoreSqlGenerateTableController {
    @Override
    @Log(value="sql\u751f\u6210\u5668\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/codegenerator/sql-generate-tables/core-sql-generate-table-edit-list";
    }

    @Override
    @Log(value="\u751f\u6210sql\u9875", type=LogType.CONTROLLER)
    @RequestMapping(value={"/sql"})
    public String sqlPage() {
        return "module/codegenerator/sql-generate-tables/core-generate-sql";
    }
}
