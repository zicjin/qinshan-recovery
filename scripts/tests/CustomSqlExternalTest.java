import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsSqlBean;
import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsSqlFieldBean;
import com.sunwayworld.cloud.module.item.customds.persistent.dao.CoreCustomDsSqlDao;
import com.sunwayworld.cloud.module.item.customds.persistent.dao.CoreCustomDsSqlFieldDao;
import com.sunwayworld.cloud.module.item.customds.service.impl.CoreCustomDsSqlFieldServiceImpl;
import com.sunwayworld.cloud.module.item.customds.service.impl.CoreCustomDsSqlServiceImpl;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.database.dialect.DialectRepository;
import com.sunwayworld.framework.database.dialect.OracleDialect;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/** Controlled DAO/JDBC fixture; no live database, HTTP, Redis or rollback claim. */
public class CustomSqlExternalTest {
    private static final Long ID = 41L;
    private static final String SQL = "SELECT ID, AMOUNT, EVENTTIME FROM T_CRM_CONTRACT@LIMS_CZJKY_HT ORDER BY ID";
    private static final String SAVE_SQL = "SELECT ID, AMOUNT, EVENTTIME FROM T_CRM_PAYMENT@LIMS_CZJKY_HT ORDER BY ID";
    private final String mode;
    private final List<String> statements = new ArrayList<>();
    private final List<String> events = new ArrayList<>();
    private final List<CoreCustomDsSqlFieldBean> fields = new ArrayList<>();
    private CoreCustomDsSqlBean stored = bean(SQL);
    private final CoreCustomDsSqlServiceImpl service = new CoreCustomDsSqlServiceImpl();
    private final DataSource jdbc;

    private CustomSqlExternalTest(String mode) throws Exception {
        this.mode = mode;
        DriverManagerDataSource database = new DriverManagerDataSource("jdbc:h2:mem:customsql;MODE=Oracle;DB_CLOSE_DELAY=-1", "sa", "");
        try (Connection connection = database.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA LIMS_EXT_HT");
            String schema = mode.equals("direct-schema") ? "LIMS_EXT_HT." : "";
            for (String table : Arrays.asList("T_CRM_CONTRACT", "T_CRM_PAYMENT")) {
                statement.execute("CREATE TABLE " + schema + table + " (ID NUMBER(12), AMOUNT NUMBER(12,2), EVENTTIME TIMESTAMP)");
                statement.execute("INSERT INTO " + schema + table + " VALUES (7, 12.50, TIMESTAMP '2026-01-02 03:04:05')");
                statement.execute("INSERT INTO " + schema + table + " VALUES (8, 18.25, NULL)");
            }
        }
        // The original-db-link profile must reach JDBC unchanged. Only this
        // in-process test transport removes its recognized link for H2 execution.
        jdbc = proxy(DataSource.class, (object, method, arguments) -> {
            if (!method.getName().equals("getConnection")) throw new AssertionError("unexpected DataSource operation");
            Connection connection = database.getConnection();
            return proxy(Connection.class, (target, operation, values) -> {
                if (operation.getName().equals("prepareStatement")) {
                    String sql = (String) values[0];
                    statements.add(sql);
                    events.add("metadata-or-query");
                    String executable = sql;
                    if (mode.equals("original-db-link")) {
                        executable = executable.replace("T_CRM_CONTRACT@LIMS_CZJKY_HT", "T_CRM_CONTRACT")
                            .replace("T_CRM_PAYMENT@LIMS_CZJKY_HT", "T_CRM_PAYMENT");
                    }
                    return connection.prepareStatement(executable);
                }
                try { return operation.invoke(connection, values); }
                catch (java.lang.reflect.InvocationTargetException failure) { throw failure.getCause(); }
            });
        });
        GenericApplicationContext context = new GenericApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("controlled-fixture",
            Collections.singletonMap("spring.datasource.driver-class-name", "org.h2.Driver")));
        context.getBeanFactory().registerSingleton("dataSource", jdbc);
        context.refresh();
        ApplicationContextHelper.setApplicationContext(context);
        DialectRepository.setDialect(new OracleDialect());
        StaticMessageSource messages = new StaticMessageSource();
        messages.setUseCodeAsDefaultMessage(true);
        new I18nHelper().setMessageSource(messages);
        java.lang.reflect.Constructor<?> constructor = Class.forName("com.sunwayworld.framework.id.SnowflakeIdWorker").getDeclaredConstructor(long.class);
        constructor.setAccessible(true);
        Field worker = Class.forName("com.sunwayworld.framework.id.GikamIdHelper").getDeclaredField("worker");
        worker.setAccessible(true);
        worker.set(null, constructor.newInstance(1L)); // Local ID fixture; never initializes Redis.
        CoreCustomDsSqlDao dao = proxy(CoreCustomDsSqlDao.class, (object, method, arguments) -> {
            if (method.getName().equals("selectById")) return stored;
            if (method.getName().equals("updateIfChanged")) {
                events.add("dao-update");
                stored = (CoreCustomDsSqlBean) arguments[0];
                return stored;
            }
            throw new AssertionError("unexpected custom SQL DAO operation: " + method.getName());
        });
        CoreCustomDsSqlFieldDao fieldDao = proxy(CoreCustomDsSqlFieldDao.class, (object, method, arguments) -> {
            if (method.getName().equals("getType")) return CoreCustomDsSqlFieldBean.class;
            if (method.getName().equals("selectByCondition")) {
                List<Map<String, Object>> result = new ArrayList<>();
                for (CoreCustomDsSqlFieldBean field : fields) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("ID", field.getId()); row.put("DSSQLID", ID); row.put("COLUMNNAME", field.getColumnName());
                    result.add(row);
                }
                return result;
            }
            if (method.getName().equals("insert")) { fields.addAll((List<CoreCustomDsSqlFieldBean>) arguments[0]); return null; }
            if (method.getName().equals("deleteByIdList")) { fields.removeIf(field -> ((List<?>) arguments[0]).contains(field.getId())); return null; }
            throw new AssertionError("unexpected field DAO operation: " + method.getName());
        });
        CoreCustomDsSqlFieldServiceImpl fieldService = new CoreCustomDsSqlFieldServiceImpl();
        inject(fieldService, "coreCustomDsSqlFieldDao", fieldDao);
        inject(service, "coreCustomDsSqlDao", dao);
        inject(service, "dsSqlFieldService", fieldService);
        inject(service, "globalMapper", proxy(GlobalMapper.class, (object, method, arguments) -> {
            if (!method.getName().equals("selectList")) throw new AssertionError("unexpected mapper operation");
            try (Connection connection = jdbc.getConnection(); java.sql.PreparedStatement statement = connection.prepareStatement((String) arguments[0]);
                 ResultSet rows = statement.executeQuery()) {
                List<Map<String, Object>> result = new ArrayList<>();
                ResultSetMetaData metadata = rows.getMetaData();
                while (rows.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        String label = metadata.getColumnLabel(i);
                        // Deliberately mixed-case mapper keys exercise the service's
                        // uppercase projection; timestamp retains its metadata name.
                        row.put(label.equals("EVENTTIME") ? label : label.toLowerCase(Locale.ROOT), rows.getObject(i));
                    }
                    result.add(row);
                }
                return result;
            } catch (SQLException failure) { throw new RuntimeException("controlled JDBC query failed", failure); }
        }));
    }

    private void preview() {
        List<Map<String, Object>> rows = service.previewSql(ID, new RestJsonWrapperBean());
        check(rows.size() == 2, "preview lost JDBC rows");
        check("7".equals(rows.get(0).get("ID").toString()), "preview ID changed");
        check("12.50".equals(rows.get(0).get("AMOUNT").toString()), "numeric preview changed");
        check("2026-01-02 03:04:05".equals(rows.get(0).get("EVENTTIME")), "metadata date formatting was bypassed");
        check(rows.get(1).get("EVENTTIME") == null, "null timestamp changed");
        for (String key : rows.get(0).keySet()) check(key.equals(key.toUpperCase(Locale.ROOT)), "preview key is not uppercase");
        check(statements.size() == 2, "preview must execute metadata plus data query");
        for (String sql : statements) profileSql(sql, "T_CRM_CONTRACT");
        // Use actual DatabaseManager/JDBC metadata, including Oracle type mapping.
        String projected = mode.equals("direct-schema") ? "LIMS_EXT_HT.T_CRM_CONTRACT" : "T_CRM_CONTRACT";
        List<ColumnContext> metadata = DatabaseManager.selectColumnContextList("SELECT ID, AMOUNT, EVENTTIME FROM " + projected);
        check(metadata.get(0).getType().equals(Number.class) && metadata.get(2).getType().equals(LocalDateTime.class), "fixture did not exercise real metadata types");
    }

    private void validate() {
        RestJsonWrapperBean wrapper = new RestJsonWrapperBean(); wrapper.setParamValue("sqlContent", SQL);
        check(Boolean.TRUE.equals(service.validateSql(wrapper).getValid()), "approved external SQL did not validate");
        check(statements.size() == 1, "validation skipped JDBC"); profileSql(statements.get(0), "T_CRM_CONTRACT");
        wrapper.setParamValue("sqlContent", "SELECT MISSING_COLUMN FROM T_CRM_CONTRACT@LIMS_CZJKY_HT");
        RestValidationResultBean invalid = service.validateSql(wrapper);
        check(Boolean.FALSE.equals(invalid.getValid()) && invalid.getMessage() != null, "SQL errors must retain validator failure behavior");
    }

    private void save() {
        fields.add(field(1L, "ID")); fields.add(field(2L, "STALE_COLUMN"));
        CoreCustomDsSqlBean changed = bean(SAVE_SQL);
        service.save(ID, saveWrapper(changed));
        check(SAVE_SQL.equals(stored.getSqlContent()), "save changed persisted SQL bytes");
        check(events.get(0).equals("dao-update") && events.contains("metadata-or-query"), "save fixture must observe write before metadata");
        check(statements.size() == 1, "save skipped metadata synchronization"); profileSql(statements.get(0), "T_CRM_PAYMENT");
        List<String> names = new ArrayList<>();
        for (CoreCustomDsSqlFieldBean field : fields) { names.add(field.getColumnName()); check(ID.equals(field.getDsSqlId()), "field parent changed"); }
        Collections.sort(names);
        check(names.equals(Arrays.asList("AMOUNT", "EVENTTIME", "ID")), "save did not synchronize inserted/deleted metadata fields");
    }

    private void reject() {
        for (String unsafe : Arrays.asList("SELECT ID FROM T_UNKNOWN@LIMS_CZJKY_HT", "SELECT ID FROM T_CRM_CONTRACT@UNKNOWN")) {
            stored = bean(unsafe); statements.clear();
            try { service.previewSql(ID, new RestJsonWrapperBean()); throw new AssertionError("unknown preview accepted"); }
            catch (RuntimeException expected) { }
            RestJsonWrapperBean wrapper = new RestJsonWrapperBean(); wrapper.setParamValue("sqlContent", unsafe);
            try { check(Boolean.FALSE.equals(service.validateSql(wrapper).getValid()), "unknown validation accepted"); }
            catch (IllegalArgumentException expected) { }
            if (!mode.equals("original-db-link")) check(statements.isEmpty(), "unapproved SQL reached JDBC");
            stored = bean(SQL); statements.clear(); events.clear();
            int previousFields = fields.size();
            try { service.save(ID, saveWrapper(bean(unsafe))); throw new AssertionError("unknown save metadata accepted"); }
            catch (RuntimeException expected) { }
            check(events.get(0).equals("dao-update") && unsafe.equals(stored.getSqlContent()), "save must retain update-before-metadata semantics");
            check(fields.size() == previousFields, "rejected metadata mutated fields");
            if (!mode.equals("original-db-link")) check(statements.isEmpty(), "unapproved saved SQL reached JDBC");
        }
    }

    private void profileSql(String sql, String table) {
        String expected = mode.equals("original-db-link") ? table + "@LIMS_CZJKY_HT" : mode.equals("direct-schema") ? "LIMS_EXT_HT." + table : table;
        check(sql.contains("FROM " + expected), "external object did not follow packaged profile");
        check(mode.equals("original-db-link") == sql.contains("@LIMS_CZJKY_HT"), "DB-link transport bytes changed incorrectly");
    }

    private static CoreCustomDsSqlBean bean(String sql) { CoreCustomDsSqlBean bean = new CoreCustomDsSqlBean(); bean.setId(ID); bean.setSqlName("controlled fixture"); bean.setSqlContent(sql); return bean; }
    private static RestJsonWrapperBean saveWrapper(CoreCustomDsSqlBean bean) { return new RestJsonWrapperBean(Collections.emptyMap(), Collections.singletonList(Collections.singletonList(JSON.toJSONString(bean))), Collections.singletonList(null)); }
    private static CoreCustomDsSqlFieldBean field(Long id, String name) { CoreCustomDsSqlFieldBean field = new CoreCustomDsSqlFieldBean(); field.setId(id); field.setDsSqlId(ID); field.setColumnName(name); return field; }
    private static void check(boolean success, String message) { if (!success) throw new AssertionError(message); }
    private static void inject(Object object, String name, Object value) throws Exception { Field field = object.getClass().getDeclaredField(name); field.setAccessible(true); field.set(object, value); }
    private static <T> T proxy(Class<T> type, java.lang.reflect.InvocationHandler handler) { return type.cast(Proxy.newProxyInstance(CustomSqlExternalTest.class.getClassLoader(), new Class<?>[]{type}, handler)); }

    public static void main(String[] arguments) throws Exception {
        CustomSqlExternalTest fixture = new CustomSqlExternalTest(arguments[0]);
        for (String scenario : arguments[1].equals("all") ? Arrays.asList("preview", "validate", "save", "reject") : Collections.singletonList(arguments[1])) {
            fixture.statements.clear(); fixture.events.clear();
            switch (scenario) { case "preview": fixture.preview(); break; case "validate": fixture.validate(); break; case "save": fixture.save(); break; case "reject": fixture.reject(); break; default: throw new AssertionError("unknown scenario"); }
            System.out.println("custom SQL " + scenario + ": PASS " + arguments[0] + " (controlled fixture)");
        }
    }
}
