package com.sunwayworld.framework;

import com.sunwayworld.framework.exception.OutOfRangeException;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Contract checks for the observed b/b1 request envelope and body parsing. */
public final class RestJsonWrapperBeanContractTest {
    private static int checks;

    public static void main(String[] args) {
        String request = "{\"p\":{\"f\":\"{\\\"name\\\":\\\"alice\\\"}\"},"
                + "\"b\":[\"\",[{\"id\":1,\"name\":\"first\"},{\"id\":1,\"name\":\"duplicate\"}]],"
                + "\"b1\":[\"\",[{\"id\":2,\"name\":\"second\"}]]}";
        RestJsonWrapperBean wrapper = new RestJsonWrapperBean(request, null, null, null);
        check(wrapper.getBodyList().size() == 2, "two body groups are parsed");
        check(wrapper.getBodyServiceList().size() == 2, "service list tracks body groups");
        check(wrapper.getBodyList().get(0).size() == 2, "records are retained in order");
        check(wrapper.getBodyList().get(0).get(0).contains("first"), "record JSON is retained");
        check(wrapper.getParamMap().isEmpty(), "query params stay separate from request envelope");

        List<Dto> all = wrapper.parse(Dto.class);
        check(all.size() == 3 && all.get(0).getId() == 1L && "second".equals(all.get(2).getName()), "parse converts all body records");
        check(wrapper.parse(Dto.class, 1).size() == 1, "indexed parse selects one group");
        RestJsonWrapperBean ids = new RestJsonWrapperBean("{\"b\":[\"\",[{\"id\":1},{\"id\":1},{\"id\":2}]]}", null, null, null);
        check(ids.parseId(Long.class).size() == 2 && ids.parseId(Long.class).get(0) == 1L, "parseId is distinct and scoped to one group");
        expectOutOfRange(new RestJsonWrapperBean("{\"b\":[\"\",[{\"id\":1},{\"id\":2}]]}", null, null, null), "parseUnique requires one record");
        check(new RestJsonWrapperBean("{\"b\":[\"\",[{\"id\":1}]]}", null, null, null).parseUnique(Dto.class).getId() == 1L, "parseUnique converts one record");

        List<List<String>> mutableBody = new ArrayList<List<String>>();
        mutableBody.add(new ArrayList<String>(Collections.singletonList("{\"id\":3}")));
        RestJsonWrapperBean copied = new RestJsonWrapperBean(null, mutableBody, null);
        mutableBody.get(0).clear();
        check(copied.getBodyList().get(0).size() == 1, "body input is defensively copied");
        expectUnsupported(new Runnable() { public void run() { copied.getBodyList().get(0).add("{}"); } }, "body list is immutable");
        expectUnsupported(new Runnable() { public void run() { copied.getBodyServiceList().add(null); } }, "service list is immutable");

        expectIllegal(new Runnable() { public void run() { new RestJsonWrapperBean("{\"b\":\"bad\"}", null, null, null); } }, "malformed body group is rejected");
        expectIllegal(new Runnable() { public void run() { new RestJsonWrapperBean("{\"b\":[\"\",[1]]}", null, null, null); } }, "non-object body record is rejected");
        expectIllegal(new Runnable() { public void run() { new RestJsonWrapperBean("{\"b\":[\"\",[\"bad\"]]}", null, null, null); } }, "malformed explicit body record is rejected");
        check(RestJsonWrapperBean.newEmpty().getBodyList().isEmpty(), "newEmpty returns an empty wrapper");

        RestJsonWrapperBean state = new RestJsonWrapperBean();
        state.setFilterValue("status", "OPEN");
        check("OPEN".equals(state.getFilterValue("status")), "filter values round trip through JSON");
        state.setAuthority("read");
        state.setQueries("detail");
        check("1".equals(state.getFilterValue("authority_read")), "authority filter is recorded");
        check("1".equals(state.getFilterValue("queries_detail")), "query category is recorded");
        state.setTotalFieldNames("amount", "total");
        check(state.getSumProperties().length == 2 && "total".equals(state.getSumProperties()[1]), "total fields are split");
        state.setParamValue("n", "3");
        state.setParamValue("s", "12");
        check(state.extractPageRequest().getPageNumber() == 3 && state.extractPageRequest().getPageSize() == 12,
                "wrapper extracts page request");
        state.removePageParams();
        check(!state.extractPageRowBounds().isPageEnabled(), "removePageParams disables row bounds");

        state.addAuthority("write");
        check(state.getAuthorityList().contains("authority_write"), "authority list records explicit authority");
        state.addExtFilter("trustedScope", "scope");
        check("scope".equals(state.getExtFilter().get("trustedScope")), "extension filter is retained");
        expectUnsupported(new Runnable() { public void run() { state.getExtFilter().put("x", "y"); } }, "extension filter view is immutable");
        state.addExtStdFilter("name", java.util.Arrays.asList("alice", "bob"), MatchPattern.IN);
        state.setLcdpSql("select 1");
        state.setLcdpOrderList(java.util.Collections.singletonList(Order.asc("name")));
        state.setLcdpTreeNodeName("parent");
        check("select 1".equals(state.getLcdpSql()) && state.getLcdpOrderList().size() == 1
                && "parent".equals(state.getLcdpTreeNodeName()), "lcdp metadata round trips");
        RestJsonWrapperBean.TargetTableFilterData target = new RestJsonWrapperBean.TargetTableFilterData();
        target.setType("EXISTS");
        target.setTargetTable("T_TARGET");
        target.setTargetMatchColumn("TARGET_ID");
        target.setThisMatchColumn("ID");
        target.addTargetFilter("STATUS", "ACTIVE");
        check("EXISTS".equals(target.getType()) && "T_TARGET".equals(target.getTargetTable())
                && "TARGET_ID".equals(target.getTargetMatchColumn()) && "ID".equals(target.getThisMatchColumn())
                && "ACTIVE".equals(target.getTargetFilter().get("STATUS")), "target filter data round trips");

        RestJsonWrapperBean filters = new RestJsonWrapperBean();
        filters.setParamValue("f", "{\"name_CISEQ\":\"alice\",\"status\":\"OPEN\",\"age_NEQ\":3}");
        filters.setParamValue("qf", "{\"code_IN\":\"A,B\"}");
        filters.setParamValue("af", "[{\"children\":[{\"name\":\"NAME\",\"match\":\"EQ\",\"value\":\"Alice\"}]}]");
        filters.setParamValue("mf", "{\"name\":\"NAME,CODE\",\"value\":\"alice\",\"caseSensitive\":false}");
        filters.setParamValue("o", "[{\"name\":\"asc\"}]");
        filters.setParamValue("r", "{\"status\":\"{\\\"OPEN\\\":\\\"Open\\\"}\"}");
        filters.addExtFilter("trustedScope", "scope");
        MapperParameter extracted = filters.extractMapFilter();
        check("OPEN".equals(extracted.get("status")) && "scope".equals(extracted.get("trustedScope")), "map filter keeps scalar and extension values");
        FilterParamPattern[] patterns = extracted.getFilterParamPatterns();
        check(patterns != null && patterns.length == 3, "map filter extracts standard patterns");
        FilterParamPattern ci = findPattern(patterns, "CISEQ");
        FilterParamPattern in = findPattern(patterns, "OR");
        check(ci != null && "ALICE".equals(mapValue(ci, "NAME")), "case insensitive filter is normalized");
        check(in != null && in.getMap().size() == 2, "IN filter becomes an OR group");
        check(extracted.getAdvancedFilterPatterns().length == 1 && extracted.getMultiPropertyFilterPattern() != null,
                "advanced and multi-property filters are extracted");
        check(extracted.hasOrderParam() && extracted.get("optionsMap") != null, "order and options metadata are extracted");
        expectIllegal(new Runnable() { public void run() {
            RestJsonWrapperBean unsafe = new RestJsonWrapperBean();
            unsafe.setParamValue("f", "{\"NAME_EQ;DROP\":\"x\"}");
            unsafe.extractMapFilter();
        } }, "unsafe map filter key is rejected");
        check(state.clearParam() == state && "{}".equals(state.getParamValue("f")), "clearParam resets filter state");
        System.out.println("rest json wrapper contract checks passed: " + checks);
    }

    private static void expectOutOfRange(RestJsonWrapperBean wrapper, String label) {
        try { wrapper.parseUnique(Dto.class); throw new AssertionError(label); }
        catch (OutOfRangeException expected) { checks++; }
    }

    private static void expectIllegal(Runnable action, String label) {
        try { action.run(); throw new AssertionError(label); }
        catch (IllegalArgumentException expected) { checks++; }
    }

    private static void expectUnsupported(Runnable action, String label) {
        try { action.run(); throw new AssertionError(label); }
        catch (UnsupportedOperationException expected) { checks++; }
    }

    private static void check(boolean value, String label) { checks++; if (!value) throw new AssertionError(label); }

    private static FilterParamPattern findPattern(FilterParamPattern[] patterns, String name) {
        for (FilterParamPattern pattern : patterns) if (name.equals(pattern.getMatchPattern())) return pattern;
        return null;
    }

    private static Object mapValue(FilterParamPattern pattern, String key) {
        for (java.util.Map.Entry<String, Object> entry : pattern.getMap().entrySet()) {
            if (key.equalsIgnoreCase(entry.getKey())) return entry.getValue();
        }
        return null;
    }

    public static final class Dto {
        private Long id;
        private String name;
        public Dto() { }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
