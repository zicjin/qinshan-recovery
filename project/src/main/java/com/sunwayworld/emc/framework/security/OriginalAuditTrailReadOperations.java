package com.sunwayworld.emc.framework.security;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordService;
import com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordLineService;
import com.sunwayworld.owned.internal.web.AuditTrailReadOperations;
import java.util.Map;
import org.springframework.stereotype.Component;

/** Preserve the original wrapper, Service, Mapper and typed Page pipeline behind the owned access gate. */
@Component
public final class OriginalAuditTrailReadOperations implements AuditTrailReadOperations {
    private final CoreAuditTrailRecordService records;
    private final CoreAuditTrailRecordLineService lines;

    public OriginalAuditTrailReadOperations(CoreAuditTrailRecordService records, CoreAuditTrailRecordLineService lines) {
        this.records = records;
        this.lines = lines;
    }

    @Override
    public Page<?> records(RestJsonWrapperBean wrapper, String trustedOrganization) {
        requireOrganization(trustedOrganization);
        SearchFilter scope = SearchFilter.instance().match("CREATEDBYORGID", trustedOrganization).filter(MatchPattern.EQ);
        return records.selectPaginationByFilter(scope, wrapper);
    }

    @Override
    public Page<?> lines(RestJsonWrapperBean wrapper, String trustedOrganization) {
        requireOrganization(trustedOrganization);
        Long recordId = recordId(wrapper);
        SearchFilter scope = SearchFilter.instance().match("RECORDID", recordId).filter(MatchPattern.EQ)
                .match("ownedAuditOrgId", trustedOrganization).special();
        // GenericService extracts all caller criteria before applying this server filter.
        // The line Mapper binds parent record organization, without projecting an internal column.
        return lines.selectPaginationByFilter(scope, wrapper);
    }

    private static void requireOrganization(String organization) {
        if (organization == null || organization.trim().isEmpty()) {
            throw new IllegalArgumentException("audit organization is required");
        }
    }

    private static Long recordId(RestJsonWrapperBean wrapper) {
        // Direct p.recordId_SEQ is retained for older owned callers. Normal original
        // grid criteria arrive via f/qf; the full wrapper remains intact downstream.
        String value = wrapper.getParamValue("recordId_SEQ");
        if (value == null) {
            MapperParameter parameter = wrapper.extractMapFilter();
            FilterParamPattern[] patterns = parameter.getFilterParamPatterns();
            if (patterns != null) for (FilterParamPattern pattern : patterns) {
                if (!"SEQ".equalsIgnoreCase(pattern.getMatchPattern()) && !"EQ".equalsIgnoreCase(pattern.getMatchPattern())) continue;
                for (Map.Entry<String, Object> entry : pattern.getMap().entrySet()) {
                    if ("RECORDID".equalsIgnoreCase(entry.getKey()) && entry.getValue() != null) {
                        value = String.valueOf(entry.getValue());
                        break;
                    }
                }
                if (value != null) break;
            }
        }
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException("recordId is required for audit lines");
        try { return Long.valueOf(value.trim()); }
        catch (NumberFormatException invalid) { throw new IllegalArgumentException("recordId is invalid", invalid); }
    }
}
