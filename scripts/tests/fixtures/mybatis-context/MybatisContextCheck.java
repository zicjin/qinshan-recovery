package mybatiscontextfixture;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.dialect.DialectRepository;
import com.sunwayworld.framework.database.dialect.OracleDialect;
import com.sunwayworld.framework.mybatis.interceptor.MybatisInterceptor;
import com.sunwayworld.framework.mybatis.mapper.DaoMapper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/** Real MyBatis executor/statement interception and a reentrant current-user lookup.
 * The fixture service executes the same generic DAO query used by the real user DAO.
 * No Redis, HTTP listener, Oracle, supplier classes or synthetic success response.
 */
public final class MybatisContextCheck {
    static final String USER_QUERY = DaoMapper.class.getName() + ".selectByIds";
    static final String BUSINESS_QUERY = "fixture.BusinessMapper.selectScoped";
    static final String BUSINESS_UPDATE = "fixture.BusinessMapper.updateScoped";
    static int checks;

    static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    static void statement(Configuration config, String id, SqlCommandType command,
                          String sql, String... parameters) {
        List<ParameterMapping> mappings = new ArrayList<>();
        for (String name : parameters) {
            mappings.add(new ParameterMapping.Builder(config, name, String.class).build());
        }
        MappedStatement.Builder builder = new MappedStatement.Builder(config, id,
                new StaticSqlSource(config, sql, mappings), command);
        if (command == SqlCommandType.SELECT) {
            builder.resultMaps(Collections.singletonList(new ResultMap.Builder(config,
                    id + "-result", HashMap.class, Collections.emptyList()).build()));
        }
        config.addMappedStatement(builder.build());
    }

    static Map<String, Object> userParameters(String id) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", id);
        return parameters;
    }

    public static void main(String[] args) throws Exception {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("fixture",
                    Collections.singletonMap("spring.datasource.driver-class-name", "org.h2.Driver")));
            context.refresh();
            ApplicationContextHelper.setApplicationContext(context);
            DialectRepository.setDialect(new OracleDialect());
            DriverManagerDataSource ds = new DriverManagerDataSource(
                    "jdbc:h2:mem:mybatis-context;MODE=Oracle;DB_CLOSE_DELAY=-1", "sa", "");
            JdbcTemplate db = new JdbcTemplate(ds);
            db.execute("CREATE TABLE T_CONTEXT_USER(ID VARCHAR(40), USERNAME VARCHAR(40), ORGID VARCHAR(40))");
            db.update("INSERT INTO T_CONTEXT_USER VALUES('fixture-user','Fixture User','org-a')");
            db.execute("CREATE TABLE T_CONTEXT_RECORD(ID VARCHAR(40), ORGID VARCHAR(40), LABEL VARCHAR(40))");
            db.update("INSERT INTO T_CONTEXT_RECORD VALUES('a','org-a','before'),('b','org-b','before')");
            Configuration config = new Configuration(new Environment("fixture", new JdbcTransactionFactory(), ds));
            config.addInterceptor(new MybatisInterceptor());
            statement(config, USER_QUERY, SqlCommandType.SELECT,
                    "SELECT ID, USERNAME, ORGID FROM T_CONTEXT_USER WHERE ID=?", "id");
            statement(config, BUSINESS_QUERY, SqlCommandType.SELECT,
                    "SELECT ID FROM T_CONTEXT_RECORD WHERE ORGID=? AND ?='fixture-user'", "loginOrgId", "loginUserId");
            statement(config, BUSINESS_UPDATE, SqlCommandType.UPDATE,
                    "UPDATE T_CONTEXT_RECORD SET LABEL=? WHERE ORGID=? AND ?='fixture-user'",
                    "label", "loginOrgId", "loginUserId");
            SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(config);
            LoginUser principal = new LoginUser("fixture-user", "");
            principal.setOrgId("org-a");
            principal.setOrgName("Fixture Organization");
            principal.setRoleId(17L);
            principal.setRoleName("Fixture Role");
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(principal, "", Collections.emptyList()));
            for (int run = 0; run < 2; run++) {
                try (SqlSession session = factory.openSession()) {
                    AtomicInteger userLoads = new AtomicInteger();
                    CoreUserService users = (CoreUserService) Proxy.newProxyInstance(
                            CoreUserService.class.getClassLoader(), new Class<?>[]{CoreUserService.class},
                            (proxy, method, values) -> {
                                if (!"selectById".equals(method.getName())) {
                                    throw new UnsupportedOperationException(method.getName());
                                }
                                userLoads.incrementAndGet();
                                List<Map<String, Object>> rows = session.selectList(USER_QUERY,
                                        userParameters((String) values[0]));
                                if (rows.isEmpty()) return null;
                                CoreUserBean user = new CoreUserBean();
                                user.setId((String) rows.get(0).get("ID"));
                                user.setUserName((String) rows.get(0).get("USERNAME"));
                                user.setOrgId((String) rows.get(0).get("ORGID"));
                                return user;
                            });
                    new LocalContextHelper().setCoreUserService(users);
                    CoreUserBean actual = LocalContextHelper.getLoginUser();
                    check("fixture-user".equals(actual.getId()), "cold current-user lookup returns the actual row");
                    check(userLoads.get() == 1, "generic DAO must not recursively resolve current user");
                    Map<String, Object> generic = userParameters("fixture-user");
                    List<?> genericRows = session.selectList(USER_QUERY, generic);
                    check(genericRows.size() == 1, "generic DAO still executes its bound query");
                    check(!generic.containsKey("loginUser"), "generic DAO map is not enriched with current user");
                    Map<String, Object> scoped = new HashMap<>();
                    List<Map<String, Object>> rows = session.selectList(BUSINESS_QUERY, scoped);
                    check(rows.size() == 1 && "a".equals(rows.get(0).get("ID")), "business query retains organization scope");
                    check(scoped.get("loginUser") instanceof CoreUserBean, "business mapper still receives actual user");
                    check("fixture-user".equals(scoped.get("loginUserId")), "business user ID is retained");
                    check(Long.valueOf(17L).equals(scoped.get("loginRoleId")), "business role is retained");
                    Map<String, Object> explicit = new HashMap<>();
                    explicit.put("loginOrgId", "org-b");
                    rows = session.selectList(BUSINESS_QUERY, explicit);
                    check(rows.size() == 1 && "b".equals(rows.get(0).get("ID")), "explicit mapper parameter remains authoritative");
                    Map<String, Object> update = new HashMap<>();
                    update.put("label", "updated");
                    check(session.update(BUSINESS_UPDATE, update) == 1, "write mapper receives login parameters");
                    session.rollback();
                    check("before".equals(db.queryForObject("SELECT LABEL FROM T_CONTEXT_RECORD WHERE ID='a'", String.class)),
                            "real fixture transaction rolled back");
                }
            }
            db.execute("SHUTDOWN");
        } finally {
            SecurityContextHolder.clearContext();
            new LocalContextHelper().setCoreUserService(null);
            ApplicationContextHelper.setApplicationContext(null);
        }
        System.out.println("MyBatis cold-user context regression: PASS (" + checks + " checks)");
    }
}
