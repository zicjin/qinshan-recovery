package com.sunwayworld.owned.install;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.boot.security.SunwaySecurityConfiguration;
import com.sunwayworld.framework.cache.memory.MemoryCacheManager;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.module.sys.password.bean.CorePasswordPolicyBean;
import com.sunwayworld.module.sys.password.service.CorePasswordHisService;
import com.sunwayworld.module.sys.password.service.impl.CorePasswordPolicyServiceImpl;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.ObjectPostProcessor;

/** One-time installation command, deliberately not a Spring component or HTTP route. */
public final class FirstAdministrator {
    private static final String[] EMPTY_TABLES = {"T_CORE_USER", "T_CORE_ORG", "T_CORE_ROLE",
        "T_CORE_ROLE_USER", "T_CORE_ROLE_PERMISSION", "T_CORE_USER_PERMISSION", "T_CORE_ORG_USER",
        "T_CORE_PASSWORD_POLICY", "T_CORE_PASSWORD_HIS"};
    private static final Set<String> INPUT_FIELDS = fields("expected_schema", "user_id", "user_name",
        "company_id", "company_name", "department_id", "department_name", "role_name", "password", "policy");
    private static final Set<String> POLICY_FIELDS = fields("min_length", "max_length", "min_number",
        "min_lowercase", "min_uppercase", "min_special_characters", "composition", "max_failed_attempts",
        "expiry_days", "expiry_warning_days", "history_length", "reuse_days", "expired_policy", "expired_max_uses");

    private FirstAdministrator() {}

    private static Set<String> fields(String... names) { return new HashSet<>(Arrays.asList(names)); }

    private static final class Rejected extends RuntimeException {
        Rejected(String code) { super(code); }
    }
    private static void require(boolean valid, String code) { if (!valid) throw new Rejected(code); }

    private static String text(JSONObject input, String key, int bytes) {
        Object value = input.get(key);
        require(value instanceof String, "INVALID_INPUT");
        String result = (String) value;
        require(!result.trim().isEmpty() && result.getBytes(StandardCharsets.UTF_8).length <= bytes
            && result.chars().noneMatch(c -> c < 32 || c == 127), "INVALID_INPUT");
        return result;
    }
    private static long number(JSONObject input, String key, long minimum, long maximum) {
        Object value = input.get(key);
        require(value instanceof Integer || value instanceof Long, "INVALID_POLICY");
        long result = ((Number) value).longValue();
        require(result >= minimum && result <= maximum, "INVALID_POLICY");
        return result;
    }

    static CorePasswordPolicyBean policy(JSONObject json) {
        require(json != null && json.keySet().equals(POLICY_FIELDS), "INVALID_POLICY");
        CorePasswordPolicyBean p = new CorePasswordPolicyBean();
        p.setId(1L); p.setName("Installation password policy"); p.setType("GLOBAL");
        p.setMinLength(number(json,"min_length",1,1024));
        p.setMaxLength(number(json,"max_length",p.getMinLength(),1024));
        p.setMinNumber(number(json,"min_number",0,p.getMaxLength()));
        p.setMinLowercase(number(json,"min_lowercase",0,p.getMaxLength()));
        p.setMinUppercase(number(json,"min_uppercase",0,p.getMaxLength()));
        p.setMinSpecialCharacters(number(json,"min_special_characters",0,p.getMaxLength()));
        require(p.getMinNumber()+p.getMinLowercase()+p.getMinUppercase()+p.getMinSpecialCharacters() <= p.getMaxLength(), "INVALID_POLICY");
        p.setComposition(number(json,"composition",0,4));
        p.setMaxFailedAttempts(number(json,"max_failed_attempts",1,1000));
        p.setExpiryInterval(number(json,"expiry_days",1,36500));
        p.setExpiryWarningInterval(number(json,"expiry_warning_days",1,p.getExpiryInterval()));
        p.setHistoryLength(number(json,"history_length",0,1000));
        p.setReuseInterval(number(json,"reuse_days",0,36500));
        p.setExpiredMaxUse(number(json,"expired_max_uses",0,1000));
        String expired = text(json,"expired_policy",16);
        require("optional".equals(expired) || "mandatory".equals(expired), "INVALID_POLICY");
        p.setExpiredPolicy(expired); p.setFailedAttemptPolicy("total"); p.setLastSuspendedFlag("0");
        return p;
    }

    static PasswordEncoder encoder(Properties properties) {
        String type = properties.getProperty("sunway.user.password.encoder.type");
        require("SM3".equals(type) || "MD5".equals(type), "EXPLICIT_ENCODER_REQUIRED");
        StaticApplicationContext context = new StaticApplicationContext();
        Map<String,Object> map = new HashMap<>();
        map.put("sunway.user.password.encoder.type", type);
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("installation", map));
        context.getBeanFactory().registerSingleton("objectPostProcessor", new ObjectPostProcessor<Object>() {
            @Override public <O extends Object> O postProcess(O object) { return object; }
        });
        SunwaySecurityConfiguration original = new SunwaySecurityConfiguration();
        original.setApplicationContext(context);
        return original.passwordEncoder();
    }

    private static void validatePassword(final CorePasswordPolicyBean p, String password, PasswordEncoder encoder) throws Exception {
        CorePasswordPolicyServiceImpl original = new CorePasswordPolicyServiceImpl() {
            @Override public List<CorePasswordPolicyBean> selectEffectedList(String userId) {
                return Collections.singletonList(p);
            }
        };
        CorePasswordHisService history = (CorePasswordHisService) Proxy.newProxyInstance(
            CorePasswordHisService.class.getClassLoader(), new Class<?>[]{CorePasswordHisService.class},
            (proxy, method, args) -> {
                if (method.getName().equals("selectList")) return Collections.emptyList();
                throw new IllegalStateException("Unexpected password history operation");
            });
        Field historyField = CorePasswordPolicyServiceImpl.class.getDeclaredField("hisService");
        historyField.setAccessible(true); historyField.set(original, history);
        Field encoderField = CorePasswordPolicyServiceImpl.class.getDeclaredField("passwordEncoder");
        encoderField.setAccessible(true); encoderField.set(original, encoder);
        StaticMessageSource messages = new StaticMessageSource(); messages.setUseCodeAsDefaultMessage(true);
        new I18nHelper().setMessageSource(messages);
        try { original.checkPassword("installation", password); }
        catch (com.sunwayworld.framework.exception.PasswordException rejected) { throw new Rejected("PASSWORD_POLICY_REJECTED"); }
    }

    static JSONObject validate(JSONObject input) {
        require(input != null && input.keySet().equals(INPUT_FIELDS), "INVALID_INPUT");
        String schema = text(input,"expected_schema",128);
        require(schema.matches("[A-Z][A-Z0-9_]{0,127}") && !schema.equals("SYS") && !schema.equals("SYSTEM"), "INVALID_TARGET_SCHEMA");
        String user = text(input,"user_id",32);
        require(user.matches("[A-Za-z][A-Za-z0-9_.-]{0,31}") &&
            !fields("DEFAULT","SYSTEM","ADMIN","ROOT").contains(user.toUpperCase(Locale.ROOT)), "INVALID_USER_ID");
        String company=text(input,"company_id",16), department=text(input,"department_id",16);
        require(company.matches("[A-Za-z][A-Za-z0-9_-]{0,15}") && department.matches("[A-Za-z][A-Za-z0-9_-]{0,15}")
            && !company.equals(department), "INVALID_ORGANIZATION");
        text(input,"user_name",84); text(input,"company_name",270); text(input,"department_name",270);
        text(input,"role_name",64); text(input,"password",4096);
        require(input.get("policy") instanceof JSONObject, "INVALID_POLICY");
        return input;
    }

    private static long scalar(Connection connection, String sql, Object... values) throws SQLException {
        try (PreparedStatement statement=connection.prepareStatement(sql)) {
            for (int i=0;i<values.length;i++) statement.setObject(i+1,values[i]);
            try(ResultSet rows=statement.executeQuery()) { require(rows.next(),"INVALID_TARGET_SCHEMA"); return rows.getLong(1); }
        }
    }
    private static void insert(Connection connection, String table, String columns, Object... values) throws SQLException {
        String parameters=String.join(",",Collections.nCopies(values.length,"?"));
        try(PreparedStatement statement=connection.prepareStatement("INSERT INTO "+table+" ("+columns+") VALUES ("+parameters+")")) {
            for(int i=0;i<values.length;i++) statement.setObject(i+1,values[i]);
            require(statement.executeUpdate()==1,"INSTALL_WRITE_FAILED");
        }
    }
    private static void install(Properties properties, JSONObject input, CorePasswordPolicyBean p, String encoded) throws Exception {
        String url=properties.getProperty("spring.datasource.url"), dbUser=properties.getProperty("spring.datasource.username"), dbPassword=properties.getProperty("spring.datasource.password");
        require(url!=null && url.startsWith("jdbc:oracle:thin:@") && dbUser!=null && dbPassword!=null, "INVALID_RUNTIME_CONFIG");
        require(dbUser.equals(input.getString("expected_schema")),"TARGET_SCHEMA_MISMATCH");
        Class.forName("oracle.jdbc.OracleDriver");
        try(Connection connection=DriverManager.getConnection(url,dbUser,dbPassword)) {
            connection.setAutoCommit(false);
            try {
                try(Statement statement=connection.createStatement(); ResultSet rows=statement.executeQuery("SELECT USER, SYS_CONTEXT('USERENV','CURRENT_SCHEMA') FROM DUAL")) {
                    require(rows.next() && input.getString("expected_schema").equals(rows.getString(1)) && rows.getString(1).equals(rows.getString(2)),"TARGET_SCHEMA_MISMATCH");
                }
                // NOWAIT rejects active fixture/admin writes; do not wait or overwrite.
                try(Statement statement=connection.createStatement()) {
                    for(String table:EMPTY_TABLES) statement.execute("LOCK TABLE "+table+" IN EXCLUSIVE MODE NOWAIT");
                    statement.execute("LOCK TABLE T_CORE_CONSTANT IN EXCLUSIVE MODE NOWAIT");
                }
                for(String table:EMPTY_TABLES) require(scalar(connection,"SELECT COUNT(*) FROM "+table)==0,"EXISTING_IDENTITIES_REFUSED");
                require(scalar(connection,"SELECT COUNT(*) FROM T_CORE_CONSTANT WHERE ID='LIMS_EMC_TOP_ORG_ID'")==0,"EXISTING_TOP_ORGANIZATION_REFUSED");
                require(scalar(connection,"SELECT COUNT(*) FROM T_CORE_LOGIN_CONFIG WHERE ID='login-config'")==1
                    && scalar(connection,"SELECT COUNT(*) FROM T_CORE_SYSTEM_CONFIG WHERE ID='system-config'")==1
                    && scalar(connection,"SELECT COUNT(*) FROM T_CORE_I18N_CONFIG")>0
                    && scalar(connection,"SELECT COUNT(*) FROM T_CORE_MENU")>0,"TECHNICAL_BOOTSTRAP_REQUIRED");
                Timestamp now=new Timestamp(System.currentTimeMillis());
                String user=input.getString("user_id"), company=input.getString("company_id"), department=input.getString("department_id");
                // Identity tables are verified empty. Explicit initial numeric IDs are
                // local installation allocation, never copied production IDs.
                long role=1L;
                insert(connection,"T_CORE_ORG","ID,ORGNAME,PARENTID,ORGCATEGORY,ORDERNO",company,input.getString("company_name"),null,"company",1L);
                insert(connection,"T_CORE_ORG","ID,ORGNAME,PARENTID,ORGCATEGORY,ORDERNO",department,input.getString("department_name"),company,"department",2L);
                insert(connection,"T_CORE_PASSWORD_POLICY","ID,NAME,TYPE,MINNUMBER,MINLOWERCASE,MINUPPERCASE,MINSPECIALCHARACTERS,MINLENGTH,MAXLENGTH,COMPOSITION,FAILEDATTEMPTPOLICY,MAXFAILEDATTEMPTS,EXPIRYINTERVAL,EXPIRYWARNINGINTERVAL,EXPIREDPOLICY,EXPIREDMAXUSE,REUSEINTERVAL,HISTORYLENGTH,LASTSUSPENDEDFLAG",
                    1L,p.getName(),p.getType(),p.getMinNumber(),p.getMinLowercase(),p.getMinUppercase(),p.getMinSpecialCharacters(),p.getMinLength(),p.getMaxLength(),p.getComposition(),p.getFailedAttemptPolicy(),p.getMaxFailedAttempts(),p.getExpiryInterval(),p.getExpiryWarningInterval(),p.getExpiredPolicy(),p.getExpiredMaxUse(),p.getReuseInterval(),p.getHistoryLength(),"0");
                insert(connection,"T_CORE_USER","ID,USERNAME,PASSWORD,ORGID,ORGNAME,PARENTORGID,USERTYPE,STATUS,LASTSUSPENDEDFLAG,READONLY,FAILEDLOGINATTEMPTS,EXPIREDPASSWORDUSES,PASSWORDUPDATEDTIME,MULTILOGIN,CREATEDTIME",
                    user,input.getString("user_name"),encoded,department,input.getString("department_name"),company,"sysAdmin","activated","0","0",0L,0L,now,"0",now);
                insert(connection,"T_CORE_ROLE","ID,ROLENAME,LASTSUSPENDEDFLAG,PASSWORDPOLICYID,ORDERNO,CREATEDTIME",role,input.getString("role_name"),"0",1L,1L,now);
                insert(connection,"T_CORE_ROLE_USER","ID,ROLEID,USERID,DEFAULTFLAG",1L,role,user,"1");
                insert(connection,"T_CORE_ROLE_PERMISSION","ID,ROLEID,PERMISSIONTYPEID,TARGETID",1L,role,"T_CORE_MENU","0");
                insert(connection,"T_CORE_USER_PERMISSION","ID,USERID,PERMISSIONTYPEID,TARGETID,DEFAULTFLAG",1L,user,"T_CORE_ORG",company,"1");
                insert(connection,"T_CORE_USER_PERMISSION","ID,USERID,PERMISSIONTYPEID,TARGETID,DEFAULTFLAG",2L,user,"T_CORE_ORG",department,"0");
                insert(connection,"T_CORE_ORG_USER","ID,ORGID,ORGNAME,USERID,USERNAME,DEFAULTFLAG",1L,department,input.getString("department_name"),user,input.getString("user_name"),"1");
                insert(connection,"T_CORE_CONSTANT","ID,CONSTANTVALUE", "LIMS_EMC_TOP_ORG_ID",company);
                insert(connection,"T_CORE_PASSWORD_HIS","ID,USERID,PASSWORD,CREATEDTIME",1L,user,encoded,now);
                connection.commit();
            } catch(Exception failure) { connection.rollback(); throw failure; }
        }
    }

    private static byte[] readInput(InputStream source) throws Exception {
        ByteArrayOutputStream result=new ByteArrayOutputStream(); byte[] buffer=new byte[4096]; int read;
        while((read=source.read(buffer))!=-1) { require(result.size()+read<=65536,"INPUT_TOO_LARGE"); result.write(buffer,0,read); }
        return result.toByteArray();
    }
    public static void main(String[] args) {
        try {
            require(args.length==1,"USAGE_RUNTIME_PROPERTIES_AND_PRIVATE_JSON_STDIN");
            JSONObject input=validate(JSON.parseObject(new String(readInput(System.in),StandardCharsets.UTF_8)));
            Properties properties=new Properties();
            try(InputStream file=Files.newInputStream(Paths.get(args[0]))) { properties.load(file); }
            PasswordEncoder encoder=encoder(properties);
            CorePasswordPolicyBean policy=policy(input.getJSONObject("policy"));
            String password=input.getString("password");
            validatePassword(policy,password,encoder);
            String encoded;
            try { encoded=encoder.encode(password); }
            finally { MemoryCacheManager.remove(Long.toString(Thread.currentThread().getId())); }
            install(properties,input,policy,encoded);
            System.out.println("{\"status\":\"installed\",\"scope\":\"first-administrator-only\",\"strict_evidence\":false}");
        } catch(Rejected rejected) {
            System.err.println("first-administrator: "+rejected.getMessage()); System.exit(2);
        } catch(SQLException failure) {
            System.err.println("first-administrator: DATABASE_REJECTED oracle_code="+failure.getErrorCode()); System.exit(2);
        } catch(Exception failure) {
            System.err.println("first-administrator: INSTALLATION_FAILED"); System.exit(2);
        }
    }
}
