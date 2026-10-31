package com.sunwayworld.framework;

import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.mybatis.mapper.AdvancedFilterPattern;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.MultiPropertyFilterPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Contract checks for mapper parameter state independent of database services. */
public final class MapperParameterContractTest {
    private static int checks;

    public static void main(String[] args) {
        MapperParameter parameter = new MapperParameter();
        parameter.setTableName("T_SAMPLE");
        parameter.setDetailQueries();
        parameter.setRawQueries();
        parameter.setSearchableQueries();
        parameter.setAuditableQueries();
        parameter.setChoosableQueries();
        parameter.setSelectableQueries();
        parameter.setSuspendableQueries();
        parameter.setUndoableQueries();
        parameter.setWithdrawableQueries();
        check("T_SAMPLE".equals(parameter.get("service_table")) && parameter.isRawQueries(), "query flags are stored");
        check("1".equals(parameter.get("queries_detail")) && "1".equals(parameter.get("queries_withdrawable")), "all query flags use supplier keys");

        SearchFilter filter = SearchFilter.instance().match("name", "Alice").filter(MatchPattern.CISEQ)
                .match("state", "OPEN").special();
        parameter.setFilter(filter);
        check("OPEN".equals(parameter.get("state")), "special filter is copied");
        check(parameter.getFilterParamPatterns() != null && parameter.getFilterParamPatterns().length == 1,
                "standard filter pattern is copied");
        FilterParamPattern additional = new FilterParamPattern("EQ", "CODE", "A");
        parameter.addFilterParamPatterns(additional);
        check(parameter.getFilterParamPatterns().length == 2, "filter patterns append");

        RestJsonWrapperBean.TargetTableFilterData target = new RestJsonWrapperBean.TargetTableFilterData();
        parameter.setTargetFilter(target);
        AdvancedFilterPattern advanced = new AdvancedFilterPattern("NAME", "Alice", "EQ");
        parameter.setAdvancedFilterPatterns(new AdvancedFilterPattern[]{advanced});
        MultiPropertyFilterPattern multi = new MultiPropertyFilterPattern(new String[]{"NAME", "CODE"}, "A", false);
        parameter.setMultiPropertyFilterPattern(multi);
        check(parameter.getTargetFilter() == target && parameter.getAdvancedFilterPatterns()[0] == advanced
                && parameter.getMultiPropertyFilterPattern() == multi, "structured filter values round trip");

        parameter.setSuspendedFlag("0");
        parameter.setActivatedFlag("1");
        check(parameter.getFilterParamPatterns().length == 4, "flag helpers retain existing patterns");
        parameter.setProcessStatus("STATUS", ProcessStatus.DONE);
        check(parameter.getFilterParamPatterns().length == 5, "process status adds a pattern");
        parameter.setAuthorityParameter("audit");
        parameter.setAuditAuthority();
        parameter.setOrgAuthority();
        parameter.setSelectByRoleIdParam(7L);
        parameter.setSelectByRoleIdExclusiveParam(8L);
        parameter.setSimpleColumns("ID,NAME");
        check("1".equals(parameter.get("authority_audit")) && Long.valueOf(7L).equals(parameter.get("select_by_roleId"))
                && "ID,NAME".equals(parameter.get("select_columns")), "authority and projection helpers store values");

        parameter.setOrderParam("NAME", "asc");
        parameter.setOrderParam("CODE", "desc");
        check(parameter.hasOrderParam(), "order helper stores values");
        @SuppressWarnings("unchecked") List<Pair<String, String>> orders = (List<Pair<String, String>>) parameter.get("orderParams");
        check(MapperParameter.hasOrderParams(orders, "name"), "ignored order fields are removed");
        check(orders.size() == 1 && "CODE".equals(orders.get(0).getFirst()), "order removal is case insensitive");

        List<Pair<String, String>> emptyOrders = new ArrayList<Pair<String, String>>(Arrays.asList(Pair.of("A", "asc")));
        check(MapperParameter.hasOrderParams(emptyOrders, null), "order presence handles blank ignore list");
        System.out.println("mapper parameter contract checks passed: " + checks);
    }

    private static void check(boolean value, String message) {
        checks++;
        if (!value) throw new AssertionError(message);
    }
}
