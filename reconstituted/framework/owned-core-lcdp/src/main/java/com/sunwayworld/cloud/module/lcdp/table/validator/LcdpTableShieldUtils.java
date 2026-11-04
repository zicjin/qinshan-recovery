/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.validator;

import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LcdpTableShieldUtils
implements InitializingBean {
    private static final List<String> coreTables = ArrayUtils.asList("T_CORE_USER", "T_CORE_ROLE", "T_CORE_ORG", "T_CORE_ROLE_PERMISSION", "T_LCDP_TABLE", "T_LCDP_RESOURCE_LOCK", "T_LCDP_RESOURCE_VERSION", "T_LCDP_SCRIPT_BLOCK", "T_LCDP_SUBMIT_LOG", "T_LCDP_MODULE_TMPL", "T_LCDP_MODULE_TMPL_CONFIG", "T_LCDP_MODULE_TMPL_RESOURCE", "T_LCDP_MODULE_TMPL_PAGE_COMP", "T_LCDP_CUSTOM_TMPL_RESOURCE", "T_LCDP_CUSTOM_TMPL_PAGE_COMP", "T_LCDP_GLOBAL_CONFIG", "T_LCDP_GLOBAL_COMP_CONFIG", "T_LCDP_CHECKOUT_RECORD", "T_LCDP_CHECKOUT_RECORD_LOG", "T_LCDP_CHECKIN_RECORD", "T_LCDP_CHECKIN_CONFIG", "T_LCDP_CHECKOUT_CONFIG", "T_LCDP_API", "T_LCDP_API_FIELD", "T_LCDP_API_REQ", "T_LCDP_API_RES", "T_LCDP_RESOURCE", "T_LCDP_RESOURCE_HISTORY", "T_LCDP_MODULE_PAGE_COMP", "T_LCDP_RESOURCE_LOCK", "T_LCDP_SERVER_SCRIPT_METHOD", "T_LCDP_RESOURCE_IMPORT_RECORD", "T_LCDP_RS_CHECKOUT_RECORD", "T_LCDP_TABLE", "T_LCDP_TABLE_FIELD", "T_LCDP_TABLE_INDEX", "T_LCDP_VIEW");
    @Value(value="${sunway.lcdp.shielded-tables:}")
    private List<String> shieldedTables;
    private Set<String> shieldedTableSet;

    @Override
    public void afterPropertiesSet() {
        this.shieldedTableSet = new HashSet<String>();
        if (!ObjectUtils.isEmpty(coreTables)) {
            this.shieldedTableSet.addAll(coreTables);
        }
        if (!ObjectUtils.isEmpty(this.shieldedTables)) {
            this.shieldedTableSet.addAll(this.shieldedTables);
        }
    }

    public boolean contains(String name) {
        return this.shieldedTableSet.contains(name);
    }
}
