package auditfixture;

import com.sunwayworld.framework.at.AuditTrailHelper;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.aspect.AuditTrailAspect;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.id.GikamIdHelper;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean;
import com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordService;
import com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordLineService;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;
import javax.sql.DataSource;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.*;

/** Public service boundary regression. All persisted rows use real MyBatis/JDBC. */
public class AuditTrailServiceCheck {
    private static int checks;
    private static final Long ID = Long.valueOf(1234567L);
    private static final Object OBJECT = new Object();
    private static final List<String> LIST = Arrays.asList("one", "two");
    private static final RuntimeException BUSINESS_FAILURE = new IllegalArgumentException("business rejected");

    public interface FixtureMapper {
        @Insert("INSERT INTO BUSINESS_ROW(ID) VALUES(#{id})")
        void business(@Param("id") long id);
        @Insert("INSERT INTO AUDIT_RECORD(ID, NAME, TARGET) VALUES(#{id}, #{auditTrailName}, #{targetId})")
        void record(CoreAuditTrailRecordBean record);
        @Insert("INSERT INTO AUDIT_LINE(RECORD_ID, LABEL) VALUES(#{recordId}, #{newValue})")
        void line(CoreAuditTrailRecordLineBean line);
    }

    public static class Writer implements GenericService<CoreAuditTrailRecordBean, Long> {
        private final FixtureMapper mapper;
        private final String label;
        Writer(FixtureMapper mapper, String label) { this.mapper = mapper; this.label = label; }
        @Override public GenericDao<CoreAuditTrailRecordBean, Long> getDao() {
            throw new AssertionError("No HTTP request: primary service DAO is not needed");
        }
        private void write(long id, boolean invalidAudit) {
            mapper.business(id);
            if (AuditTrailHelper.auditTrail()) {
                CoreAuditTrailRecordLineBean line = new CoreAuditTrailRecordLineBean();
                line.setNewValue(invalidAudit ? null : label);
                AuditTrailAspect.addRecordLine("FIXTURE$" + id, line);
            }
        }
        @Transactional @AuditTrailEntry(value=AuditTrailType.INSERT, name="outer entry")
        public Long insertLong() { write(1, false); return ID; }
        @Transactional @AuditTrailEntry(AuditTrailType.INSERT)
        public Object insertObject() { write(2, false); return OBJECT; }
        @Transactional @AuditTrailEntry(AuditTrailType.INSERT)
        public List<String> insertList() { write(3, false); return LIST; }
        @Transactional @AuditTrailEntry(AuditTrailType.INSERT)
        public Object insertNull() { write(4, false); return null; }
        @Transactional @AuditTrailEntry(AuditTrailType.INSERT)
        public Object insertVoidLike() { return null; }
        @Transactional @AuditTrailEntry(AuditTrailType.INSERT)
        public Long throwing() { write(5, false); throw BUSINESS_FAILURE; }
        @Transactional @AuditTrailEntry(AuditTrailType.INSERT)
        public Long auditFailure() { write(6, true); return ID; }
        @Transactional @AuditTrailEntry(value=AuditTrailType.INSERT, name="outer entry")
        public Object nested() {
            AuditTrailAspect.AuditTrailEntryData entry = AuditTrailAspect.getAuditTrailEntryData();
            write(7, false);
            Object result = ApplicationContextHelper.getBean(NestedWriter.class).nestedWrite(entry);
            same(entry, AuditTrailAspect.getAuditTrailEntryData(), "nested call retains outer entry");
            return result;
        }
        @Transactional @AuditTrailEntry(AuditTrailType.INSERT)
        public Long reusedAfterFailure() { write(9, false); return ID; }
    }

    public static class NestedWriter extends Writer {
        NestedWriter(FixtureMapper mapper) { super(mapper, "nested"); }
        @Transactional @AuditTrailEntry(value=AuditTrailType.UPDATE, name="nested entry")
        public Object nestedWrite(AuditTrailAspect.AuditTrailEntryData expected) {
            same(expected, AuditTrailAspect.getAuditTrailEntryData(), "nested shares outer entry");
            super.write(8, false);
            return OBJECT;
        }
    }

    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass=true)
    public static class Config {
        @Bean public DataSource dataSource() {
            return new DriverManagerDataSource("jdbc:h2:mem:auditfixture;DB_CLOSE_DELAY=-1", "sa", "");
        }
        @Bean public DataSourceTransactionManager transactionManager(DataSource ds) { return new DataSourceTransactionManager(ds); }
        @Bean public FixtureMapper fixtureMapper(DataSource ds) throws Exception {
            org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration();
            config.addMapper(FixtureMapper.class);
            SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
            factory.setDataSource(ds); factory.setConfiguration(config);
            return new SqlSessionTemplate(factory.getObject()).getMapper(FixtureMapper.class);
        }
        @Bean public Writer writer(FixtureMapper mapper) { return new Writer(mapper, "outer"); }
        @Bean public NestedWriter nestedWriter(FixtureMapper mapper) { return new NestedWriter(mapper); }
        @Bean public CoreAuditTrailRecordService recordService(FixtureMapper mapper) {
            GenericDao dao = (GenericDao) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{GenericDao.class}, (p,m,a) -> {
                if (m.getName().equals("hashCode")) return System.identityHashCode(p);
                if (m.getName().equals("equals")) return p == a[0];
                if (m.getName().equals("insert")) {
                    for (CoreAuditTrailRecordBean record : (List<CoreAuditTrailRecordBean>)a[0]) mapper.record(record);
                    return null;
                }
                throw new AssertionError("Unexpected audit DAO call: " + m);
            });
            return (CoreAuditTrailRecordService) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{CoreAuditTrailRecordService.class}, (p,m,a) -> {
                if (m.getName().equals("hashCode")) return System.identityHashCode(p);
                if (m.getName().equals("equals")) return p == a[0];
                if (m.getName().equals("getDao")) return dao;
                if (m.getName().equals("toString")) return "fixture audit records";
                throw new AssertionError("Unexpected audit service call: " + m);
            });
        }
        @Bean public CoreAuditTrailRecordLineService recordLineService(FixtureMapper mapper) {
            return (CoreAuditTrailRecordLineService) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{CoreAuditTrailRecordLineService.class}, (p,m,a) -> {
                if (m.getName().equals("hashCode")) return System.identityHashCode(p);
                if (m.getName().equals("equals")) return p == a[0];
                if (m.getName().equals("insert")) {
                    for (CoreAuditTrailRecordLineBean line : (List<CoreAuditTrailRecordLineBean>)a[0]) mapper.line(line);
                    return null;
                }
                if (m.getName().equals("toString")) return "fixture audit lines";
                throw new AssertionError("Unexpected audit line service call: " + m);
            });
        }
    }

    @Configuration
    @EnableTransactionManagement(proxyTargetClass=true)
    @Import(Config.class)
    public static class DefaultTransactionOrder {}

    @Configuration
    @EnableTransactionManagement(proxyTargetClass=true, order=0)
    @Import(Config.class)
    public static class TransactionFirst {}

    static void same(Object expected, Object actual, String message) {
        checks++; if(expected != actual) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        checks++; if(!Objects.equals(expected, actual)) throw new AssertionError(message + ": expected " + expected + ", got " + actual);
    }
    static void clean() {
        same(null, AuditTrailAspect.getAuditTrailEntryData(), "entry cleaned on calling thread");
        try {
            AuditTrailAspect.addRecordLine("OUTSIDE_ENTRY", new CoreAuditTrailRecordLineBean());
            throw new AssertionError("record data leaked outside the service entry");
        } catch (NullPointerException expected) { checks++; }
    }
    static int rows(JdbcTemplate db, String table) { return db.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class); }
    static void reset(JdbcTemplate db) {
        db.update("DELETE FROM AUDIT_LINE"); db.update("DELETE FROM AUDIT_RECORD"); db.update("DELETE FROM BUSINESS_ROW");
    }
    static void persisted(JdbcTemplate db, int business, int records, int lines) {
        equal(business, rows(db, "BUSINESS_ROW"), "business commits");
        equal(records, rows(db, "AUDIT_RECORD"), "audit header commits");
        equal(lines, rows(db, "AUDIT_LINE"), "audit line commits");
        equal(0, db.queryForObject("SELECT COUNT(*) FROM AUDIT_LINE L LEFT JOIN AUDIT_RECORD R ON R.ID=L.RECORD_ID WHERE R.ID IS NULL", Integer.class), "audit lines linked to headers");
        clean();
    }
    static void run(boolean active, String which, boolean transactionFirst) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        if(active) context.getEnvironment().setActiveProfiles("audit-trail");
        context.register(transactionFirst ? TransactionFirst.class : DefaultTransactionOrder.class, AuditTrailAspect.class);
        ApplicationContextHelper.setApplicationContext(context);
        try {
            context.refresh();
            JdbcTemplate db = new JdbcTemplate(context.getBean(DataSource.class));
            db.execute("CREATE TABLE BUSINESS_ROW(ID BIGINT PRIMARY KEY)");
            db.execute("CREATE TABLE AUDIT_RECORD(ID BIGINT PRIMARY KEY, NAME VARCHAR(100), TARGET VARCHAR(100))");
            db.execute("CREATE TABLE AUDIT_LINE(RECORD_ID BIGINT, LABEL VARCHAR(100) NOT NULL)");
            Writer writer = context.getBean("writer", Writer.class);
            equal(true, AopUtils.isAopProxy(writer), "public service is Spring proxy");
            List<String> advisors = new ArrayList<>();
            for (org.springframework.aop.Advisor advisor : ((org.springframework.aop.framework.Advised)writer).getAdvisors()) {
                advisors.add(advisor.getAdvice().getClass().getSimpleName());
            }
            System.out.println("advisor order (" + (transactionFirst ? "transaction-first" : "Spring default") + "): " + advisors);
            if(active) equal(true, advisors.indexOf("TransactionInterceptor") < advisors.indexOf("AspectJAroundAdvice"), "actual transaction encloses audit persistence");
            equal(active ? 1 : 0, context.getBeansOfType(AuditTrailAspect.class).size(), "audit profile selects aspect");
            same(ID, writer.insertLong(), "Long result identity"); persisted(db, 1, active?1:0, active?1:0);
            System.out.println("PASS " + (active?"active":"disabled") + " Long return + Mapper commit");
            if(which.equals("long")) {
                db.execute("SHUTDOWN");
                return;
            }
            reset(db); same(OBJECT, writer.insertObject(), "object result identity"); persisted(db,1,active?1:0,active?1:0);
            reset(db); same(LIST, writer.insertList(), "list result identity"); persisted(db,1,active?1:0,active?1:0);
            reset(db); same(null, writer.insertNull(), "null result"); persisted(db,1,active?1:0,active?1:0);
            System.out.println("PASS " + (active?"active":"disabled") + " object/list/null return");
            reset(db);
            try { writer.throwing(); throw new AssertionError("business exception swallowed"); }
            catch (RuntimeException failure) { same(BUSINESS_FAILURE, failure, "business exception identity"); }
            persisted(db,0,0,0);
            same(null, writer.insertVoidLike(), "empty invocation after business failure"); persisted(db,0,0,0);
            if(active) {
                try { writer.auditFailure(); throw new AssertionError("audit exception swallowed"); }
                catch (org.springframework.dao.DataAccessException expected) { checks++; }
                persisted(db,0,0,0);
                same(null, writer.insertVoidLike(), "empty invocation after audit failure"); persisted(db,0,0,0);
                same(ID, writer.reusedAfterFailure(), "thread reusable after audit failure"); persisted(db,1,1,1);
                equal("FIXTURE$9", db.queryForObject("SELECT TARGET FROM AUDIT_RECORD", String.class), "no stale record data after failure");
                reset(db);
                same(OBJECT, writer.nested(), "nested object return"); persisted(db,2,2,2);
                equal(2, db.queryForObject("SELECT COUNT(*) FROM AUDIT_RECORD WHERE NAME='outer entry'",Integer.class), "outer annotation retained for nested lines");
                equal(2, db.queryForObject("SELECT COUNT(DISTINCT LABEL) FROM AUDIT_LINE",Integer.class), "outer + nested mapper lines collected");
                same(null, writer.insertVoidLike(), "empty invocation after nested success"); persisted(db,2,2,2);
                System.out.println("PASS active nested entry/record cleanup + business/audit failure rollback");
            } else {
                same(ID, writer.auditFailure(), "disabled audit cannot fail business write"); persisted(db,1,0,0);
                System.out.println("PASS disabled audit leaves transactions/returns unchanged");
            }
            reset(db);
            db.execute("SHUTDOWN");
        } finally {
            context.close(); ApplicationContextHelper.setApplicationContext(null);
        }
    }
    public static void main(String[] args) throws Exception {
        // The production ID allocator normally contacts Redis. Seed its worker in this
        // disposable JVM only, so the fixture never opens a real Redis connection.
        Field worker = GikamIdHelper.class.getDeclaredField("worker"); worker.setAccessible(true);
        java.lang.reflect.Constructor<?> ctor = worker.getType().getDeclaredConstructor(long.class);
        ctor.setAccessible(true); worker.set(null, ctor.newInstance(0L));
        run(true, args[0], false);
        if(!args[0].equals("long")) { run(true, args[0], true); run(false, args[0], false); }
        System.out.println("audit trail public service regression: PASS (" + checks + " checks)");
    }
}
