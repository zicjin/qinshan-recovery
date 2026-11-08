package com.sunwayworld.framework.database.dialect;

import java.util.*;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.env.MapPropertySource;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.context.*;
import com.sunwayworld.framework.database.context.instance.EntityContextInstance;
import com.sunwayworld.framework.database.context.instance.TableContextInstance;
import com.sunwayworld.framework.mybatis.mapper.GlobalBoundSql;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskHisBean;

/** Exercise the three public Oracle bulk SQL boundaries without a database. */
public class OracleBatchClobCheck {
    private static final String TABLE = "T_CORE_BPMN_INST_TASK_HIS";
    private static final String JSON = "{\"roleIds\":[\"fixture's-role\"]}";
    private static final String SHORT_TEXT = "reviewer's-note";
    private static final String LONG_TEXT = "long's-" + String.join("", Collections.nCopies(1001, "x"));
    private static final List<String> COLUMNS = Arrays.asList("ID", "CANDIDATORJSON", "INSTID", "STATUSCODE");

    static void expect(boolean condition, String label) {
        if (!condition) throw new AssertionError(label);
    }

    private static EntityContext entityContext() {
        EntityContext context = new EntityContext();
        context.setTableName(TABLE);
        context.setColumnContextList(new ArrayList<EntityColumnContext>());
        for (String name : COLUMNS) {
            EntityColumnContext column = new EntityColumnContext();
            column.setColumnName(name);
            column.setEntityClass(CoreBpmnInstanceTaskHisBean.class);
            column.setType(name.endsWith("ID") ? Long.class : String.class);
            column.setClob("CANDIDATORJSON".equals(name));
            context.getColumnContextList().add(column);
            if ("ID".equals(name)) context.setIdContext(column);
        }
        return context;
    }

    private static List<CoreBpmnInstanceTaskHisBean> entities() {
        CoreBpmnInstanceTaskHisBean first = new CoreBpmnInstanceTaskHisBean();
        first.setId(1L); first.setInstId(11L); first.setStatusCode(LONG_TEXT);
        first.setCandidatorJson(JSON);
        CoreBpmnInstanceTaskHisBean second = new CoreBpmnInstanceTaskHisBean();
        second.setId(2L); second.setInstId(12L); second.setStatusCode(null);
        second.setCandidatorJson(null);
        CoreBpmnInstanceTaskHisBean third = new CoreBpmnInstanceTaskHisBean();
        third.setId(3L); third.setInstId(13L); third.setStatusCode(SHORT_TEXT);
        third.setCandidatorJson(null);
        return Arrays.asList(first, second, third);
    }

    private static TableContext tableContext() {
        TableContext context = new TableContext();
        context.setTableName(TABLE);
        context.setColumnContextList(new ArrayList<ColumnContext>());
        for (String name : COLUMNS) {
            ColumnContext column = new ColumnContext();
            column.setColumnName(name);
            column.setType(name.endsWith("ID") ? Long.class : String.class);
            column.setClob("CANDIDATORJSON".equals(name));
            context.getColumnContextList().add(column);
            if ("ID".equals(name)) context.setIdColumnContext(column);
        }
        return context;
    }

    private static GlobalBoundSql entityInsert(OracleDialect dialect) {
        EntityContext context = entityContext();
        List<EntityContextInstance> items = new ArrayList<>();
        for (CoreBpmnInstanceTaskHisBean entity : entities()) {
            items.add(EntityContextInstance.insertable(context, entity));
        }
        return dialect.getBatchInsertSql(context, items);
    }

    private static GlobalBoundSql entityUpdate(OracleDialect dialect) {
        EntityContext context = entityContext();
        List<EntityContextInstance> items = new ArrayList<>();
        for (CoreBpmnInstanceTaskHisBean entity : entities()) {
            items.add(EntityContextInstance.updatable(context, entity, "CANDIDATORJSON", "STATUSCODE"));
        }
        return dialect.getBatchUpdateByIdsSql(context, items, Arrays.asList("CANDIDATORJSON", "STATUSCODE"));
    }

    private static GlobalBoundSql tableInsert(OracleDialect dialect) {
        TableContext context = tableContext();
        List<TableContextInstance> items = new ArrayList<>();
        for (CoreBpmnInstanceTaskHisBean entity : entities()) {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("ID", entity.getId());
            values.put("CANDIDATORJSON", entity.getCandidatorJson());
            values.put("INSTID", entity.getInstId());
            values.put("STATUSCODE", entity.getStatusCode());
            items.add(TableContextInstance.of(context, values));
        }
        return dialect.getBatchInsertSql(context, items);
    }

    private static int occurrences(String text, String needle) {
        int count = 0;
        for (int index = text.indexOf(needle); index >= 0; index = text.indexOf(needle, index + needle.length())) count++;
        return count;
    }

    private static void verify(GlobalBoundSql bound, String seam, boolean kingbase) {
        String sql = (String) bound.get("sql");
        if ("entity-update".equals(seam)) {
            expect(sql.startsWith("UPDATE " + TABLE + " SET CANDIDATORJSON=CASE ID "), "actual batch update SQL");
            expect(sql.endsWith(" WHERE ID IN (1,2,3)"), "all actual entity IDs retained");
            expect(occurrences(sql, " WHEN 2 THEN NULL") == 2, "both nullable update values stay SQL NULL");
        } else {
            expect(sql.startsWith("BEGIN INSERT INTO " + TABLE), "actual batch insert SQL");
            expect(sql.endsWith("END;"), "complete PL/SQL block");
            expect(occurrences(sql, "INSERT INTO ") == 3, "all three rows retained");
            expect(sql.contains("VALUES (2,NULL,12,NULL)"), "null CLOB and null ordinary text stay SQL NULL");
        }
        expect(sql.contains("reviewer''s-note"), "short ordinary text quote is escaped");
        if (kingbase) {
            expect(!sql.contains("TO_CLOB("), "explicit Kingbase uses parameters");
            expect(bound.containsValue(JSON), "short declared CLOB parameter is unchanged");
            expect(bound.containsValue(LONG_TEXT), "long non-CLOB string parameter is unchanged");
            expect(occurrences(sql, "jdbcType=CLOB") == 2, "both CLOB and over-1000 text use typed parameters");
            expect(bound.size() == 3, "SQL plus exactly two non-null CLOB parameters");
            expect(!sql.contains("fixture''s-role") && !sql.contains("long''s-"), "bound large values are not interpolated");
        } else {
            expect(occurrences(sql, "TO_CLOB(") == 2, "short declared CLOB and long non-CLOB string use Oracle expressions");
            expect(sql.contains("TO_CLOB('{\"roleIds\":[\"fixture''s-role\"]}')"), "short JSON preserved and quote escaped");
            expect(sql.contains("TO_CLOB('long''s-" + String.join("", Collections.nCopies(1001, "x")) + "')"), "full long ordinary string preserved and escaped");
            expect(bound.size() == 1, "Oracle default has only SQL, no Kingbase parameters");
        }
    }

    public static void main(String[] args) {
        StaticApplicationContext app = new StaticApplicationContext();
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.datasource.driver-class-name", "oracle.jdbc.OracleDriver");
        if (!"absent".equals(args[0])) properties.put("kingbase.enabled", args[0]);
        app.getEnvironment().getPropertySources().addFirst(new MapPropertySource("fixture", properties));
        ApplicationContextHelper.setApplicationContext(app);
        OracleDialect dialect = new OracleDialect();
        List<String> failures = new ArrayList<>();
        for (String seam : Arrays.asList("entity-insert", "entity-update", "table-insert")) {
            if (args.length > 1 && !seam.equals(args[1])) continue;
            try {
                GlobalBoundSql bound = "entity-insert".equals(seam) ? entityInsert(dialect)
                        : "entity-update".equals(seam) ? entityUpdate(dialect) : tableInsert(dialect);
                verify(bound, seam, "true".equals(args[0]));
                System.out.println("PASS Oracle batch CLOB: seam=" + seam + " kingbase=" + args[0]);
            } catch (Throwable error) {
                failures.add(seam + ": " + error);
                System.err.println("FAIL Oracle batch CLOB: seam=" + seam + " kingbase=" + args[0]);
                error.printStackTrace();
            }
        }
        expect(failures.isEmpty(), "batch SQL failures: " + failures);
    }
}
