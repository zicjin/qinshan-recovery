import com.sunwayworld.boot.ApplicationConfiguration;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.dialect.DialectRepository;
import com.sunwayworld.framework.database.dialect.OracleDialect;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSession;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/** Calls the unchanged real factory method; the only test dependency is JDBC. */
public final class ApplicationMapperLocationsCheck {
    private static final String[] REQUIRED = {
        "com.sunwayworld.cloud.module.mobile.zwdd.persistent.mapper.ZwddUserMapper",
        "com.sunwayworld.cloud.module.mobile.zwdd.persistent.mapper.ZwddMessageMapper",
        "com.sunwayworld.cloud.module.equipt.monitorconfig.persistent.mapper.CoreEquiptMonitorConfigMapper",
        "com.sunwayworld.cloud.module.equipt.monitorconfig.persistent.mapper.CoreEquiptMonitorLogMapper"
    };

    private static void executeOriginalQuery(SqlSessionFactory factory, String statement, String label) {
        MapperParameter parameter = new MapperParameter();
        parameter.setFilter(SearchFilter.instance().match("ID", -987654321L).filter(MatchPattern.EQ));
        String sql = factory.getConfiguration().getMappedStatement(statement).getBoundSql(parameter).getSql();
        if (!sql.contains("?") || sql.contains("-987654321")) {
            throw new AssertionError(label + " lost original bound no-match predicate: " + sql);
        }
        try (SqlSession session = factory.openSession()) {
            List<?> rows = session.selectList(statement, parameter);
            if (!rows.isEmpty()) throw new AssertionError(label + " no-match query returned a row");
            session.rollback();
        }
        System.out.println(label + ": PASS (original XML + global filter + actual H2 JDBC no-match)");
    }

    public static void main(String[] args) throws Exception {
        DriverManagerDataSource source = new DriverManagerDataSource(
                "jdbc:h2:mem:original_mapper_locations;MODE=Oracle;DB_CLOSE_DELAY=-1", "sa", "");
        source.setDriverClassName("org.h2.Driver");
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.getEnvironment().getSystemProperties().put("spring.datasource.driver-class-name", "org.h2.Driver");
            context.getBeanFactory().registerSingleton("dataSource", source);
            context.refresh();
            ApplicationContextHelper.setApplicationContext(context);
            DialectRepository.setDialect(new OracleDialect());
            ApplicationConfiguration application = new ApplicationConfiguration();
            application.setApplicationContext(context);
            SqlSessionFactory factory = application.sqlSessionFactory();
            List<String> missing = new ArrayList<>();
            for (String namespace : REQUIRED) {
                String id = namespace + ".selectByCondition";
                if (!factory.getConfiguration().hasStatement(id, false)) missing.add(id);
            }
            for (Object item : factory.getConfiguration().getMappedStatements()) {
                if (!(item instanceof MappedStatement)) continue;
                String resource = ((MappedStatement) item).getResource();
                if (resource != null && resource.contains(".xml") && !resource.contains("-oracle-mapper.xml")) {
                    throw new AssertionError("Non-Oracle mapper entered actual factory: " + resource);
                }
            }
            System.out.println("Exact Oracle mapper selection: PASS (all registered XML statements)");
            if (!missing.isEmpty()) {
                throw new AssertionError("Actual ApplicationConfiguration factory omitted original mapper statements: " + missing);
            }
            for (String namespace : REQUIRED) {
                MappedStatement statement = factory.getConfiguration().getMappedStatement(namespace + ".selectByCondition");
                String resource = statement.getResource();
                if (resource == null || !resource.contains("-oracle-mapper.xml")) {
                    throw new AssertionError("Original Oracle XML was not selected: " + namespace + " / " + resource);
                }
                System.out.println(namespace + ": PASS (actual factory, original Oracle XML)");
            }
            if (!factory.getConfiguration().hasStatement("com.sunwayworld.framework.mybatis.mapper.GlobalMapper.selectOne")) {
                throw new AssertionError("Existing mapper/conf GlobalMapper registration regressed");
            }
            String globalResource = factory.getConfiguration().getMappedStatement(
                    "com.sunwayworld.framework.mybatis.mapper.GlobalMapper.selectOne").getResource();
            if (globalResource == null || !globalResource.contains("/mapper/conf/global-oracle-mapper.xml")) {
                throw new AssertionError("GlobalMapper did not come from original mapper/conf XML: " + globalResource);
            }
            System.out.println("GlobalMapper mapper/conf registration: PASS");
            if (!"registration".equals(args[0])) {
                JdbcTemplate db = new JdbcTemplate(source);
                // Original Bean fields, temporary H2 only. No runtime schema or
                // generic simplified mapper substitutes participate in this check.
                db.execute("CREATE TABLE T_CORE_MOBILE_ZWDD_MESSAGE (ID BIGINT PRIMARY KEY, TITLE VARCHAR(200), CONTENT CLOB, DATA CLOB, PUSHEDUSERID VARCHAR(80), PUSHEDUSERNAME VARCHAR(80), AGENTID VARCHAR(80), PUSHTIME TIMESTAMP, PUSHBYID VARCHAR(80), PUSHBYNAME VARCHAR(80), PUSHFLAG VARCHAR(8), REMARK VARCHAR(200), CREATEDBYID VARCHAR(80), CREATEDBYNAME VARCHAR(80), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR(80), CREATEDBYORGNAME VARCHAR(80))");
                db.execute("CREATE TABLE T_CORE_EQUIPT_MONITOR_CONFIG (ID BIGINT PRIMARY KEY, CODE VARCHAR(80), IP VARCHAR(80), PORT BIGINT, THRESHOLD DOUBLE, EQUIPTID VARCHAR(80), CREATEDBYNAME VARCHAR(80), CREATEDTIME TIMESTAMP, CREATEDBYORGID VARCHAR(80), CREATEDBYORGNAME VARCHAR(80))");
                db.update("INSERT INTO T_CORE_MOBILE_ZWDD_MESSAGE (ID,TITLE) VALUES (1,'unmatched control')");
                db.update("INSERT INTO T_CORE_EQUIPT_MONITOR_CONFIG (ID,CODE) VALUES (1,'unmatched-control')");
                executeOriginalQuery(factory, REQUIRED[1] + ".selectByCondition", "ZwddMessage query");
                executeOriginalQuery(factory, REQUIRED[2] + ".selectByCondition", "EquiptMonitorConfig query");
                try (SqlSession session = factory.openSession()) {
                    java.util.Map<String,Object> parameter = new java.util.HashMap<>();
                    parameter.put("sql", "SELECT 37 AS ORIGINAL_CONF_CONTROL FROM DUAL");
                    java.util.Map<?,?> row = session.selectOne("com.sunwayworld.framework.mybatis.mapper.GlobalMapper.selectOne", parameter);
                    if (row == null || !"37".equals(String.valueOf(row.get("ORIGINAL_CONF_CONTROL")))) {
                        throw new AssertionError("Original mapper/conf GlobalMapper JDBC behavior regressed");
                    }
                    session.rollback();
                }
                System.out.println("GlobalMapper mapper/conf execution: PASS (actual JDBC value37)");
                db.execute("SHUTDOWN");
            }
        } finally {
            ApplicationContextHelper.setApplicationContext(null);
        }
    }
}
