package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.web.InMemoryAuditEventSink;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpController;
import com.sunwayworld.owned.internal.web.OwnedLoginCredentialDecoder;
import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

/** Public owned identity boundary tested against a disposable, synthetic Oracle schema. */
public final class JdbcPersonnelIdentityContractTest {
    private static int checks;
    private interface Attempt { void run() throws Exception; }
    private static void check(boolean value,String label) { checks++; if(!value)throw new AssertionError(label); }
    private static void fails(Class<? extends Throwable> type,Attempt attempt) throws Exception {
        try { attempt.run(); throw new AssertionError("expected rejection "+type.getSimpleName()); }
        catch(Throwable failure) { if(!type.isInstance(failure))throw new AssertionError("wrong rejection class",failure); checks++; }
    }
    public static void main(String[] args) throws Exception {
        Properties p=new Properties(); try(FileInputStream in=new FileInputStream(args[0])){p.load(in);}
        OracleDataSource ds=new OracleDataSource(); ds.setURL(p.getProperty("url")); ds.setUser(p.getProperty("user")); ds.setPassword(p.getProperty("password"));
        try(Connection c=ds.getConnection();Statement s=c.createStatement()) {
            s.execute("CREATE TABLE T_CORE_USER (ID VARCHAR2(64) PRIMARY KEY, PASSWORD VARCHAR2(120), ORGID VARCHAR2(64), ORGNAME VARCHAR2(100), STATUS VARCHAR2(20), LASTSUSPENDEDFLAG VARCHAR2(1), EXPIRYDATE TIMESTAMP)");
            s.execute("CREATE TABLE T_CORE_ROLE (ID NUMBER PRIMARY KEY, LASTSUSPENDEDFLAG VARCHAR2(1), USEDBYORGID VARCHAR2(100))");
            s.execute("CREATE TABLE T_CORE_ROLE_USER (ROLEID NUMBER, USERID VARCHAR2(64))");
            s.execute("CREATE TABLE T_CORE_ROLE_PERMISSION (ROLEID NUMBER, PERMISSIONTYPEID VARCHAR2(64), TARGETID VARCHAR2(64))");
            s.execute("CREATE TABLE T_CORE_MENU (ID VARCHAR2(64) PRIMARY KEY)");
            s.execute("CREATE TABLE T_CORE_ORG (ID VARCHAR2(64) PRIMARY KEY)");
            s.execute("CREATE TABLE T_CORE_USER_PERMISSION (USERID VARCHAR2(64), PERMISSIONTYPEID VARCHAR2(64), TARGETID VARCHAR2(64))");
            s.execute("CREATE TABLE T_CORE_LOGIN_CONFIG (ID VARCHAR2(64) PRIMARY KEY, LOGINORG VARCHAR2(1), LOGINROLE VARCHAR2(1), LOGINTENANT VARCHAR2(1), LOGINAUTHMETHOD VARCHAR2(36), CAPTCHA VARCHAR2(1), ADDOMAINSUPPORT VARCHAR2(1), SHOWLOGINAUTHOPTION VARCHAR2(1), QCCODE VARCHAR2(1))");
            s.execute("INSERT INTO T_CORE_LOGIN_CONFIG VALUES ('login-config','0','0',NULL,'system','0','0','0','0')");
            s.execute("CREATE TABLE T_CORE_PASSWORD_POLICY (ID NUMBER PRIMARY KEY, MAXFAILEDATTEMPTS NUMBER, EXPIRYINTERVAL NUMBER, EXPIREDPOLICY VARCHAR2(36), LASTSUSPENDEDFLAG VARCHAR2(1))");
            s.execute("INSERT INTO T_CORE_PASSWORD_POLICY VALUES (1,0,0,'optional','0')");
            s.execute("INSERT INTO T_CORE_MENU VALUES ('100001')"); s.execute("INSERT INTO T_CORE_ORG VALUES ('org-a')");
            // Published MD5 test vector for the synthetic input 'secret'.
            for(String id:new String[]{"allowed","low","locked","suspended","admin","expired","deprecated","unknown-state"}) {
                try(PreparedStatement q=c.prepareStatement("INSERT INTO T_CORE_USER VALUES (?, '5ebe2294ecd0e0f08eab7690d2a6ee69','org-a','Fixture Org',? ,?,NULL)")) {
                    q.setString(1,id);
                    q.setString(2,id.equals("locked")?"locked":id.equals("deprecated")?"deprecated":id.equals("unknown-state")?"mystery":"activated");
                    q.setString(3,id.equals("suspended")?"1":"0");q.executeUpdate();
                }
            }
            try (PreparedStatement q=c.prepareStatement("UPDATE T_CORE_USER SET EXPIRYDATE = SYSTIMESTAMP - INTERVAL '1' DAY WHERE ID = 'expired'")) { q.executeUpdate(); }
            s.execute("INSERT INTO T_CORE_ROLE VALUES (1,'0',NULL)");s.execute("INSERT INTO T_CORE_ROLE VALUES (2,'1',NULL)");s.execute("INSERT INTO T_CORE_ROLE VALUES (3,'0',NULL)");
            s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (1,'allowed')");s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (2,'low')");s.execute("INSERT INTO T_CORE_ROLE_USER VALUES (3,'admin')");
            s.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (1,'T_CORE_MENU','100001')");s.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (2,'T_CORE_MENU','100001')");s.execute("INSERT INTO T_CORE_ROLE_PERMISSION VALUES (3,'T_CORE_MENU','0')");
            s.execute("INSERT INTO T_CORE_USER_PERMISSION VALUES ('allowed','T_CORE_ORG','org-a')");s.execute("INSERT INTO T_CORE_USER_PERMISSION VALUES ('allowed','T_CORE_ORG','missing-org')");
        }
        final JdbcPersonnelIdentity identity=new JdbcPersonnelIdentity(ds,"MD5");
        fails(BadCredentialsException.class,()->identity.authenticate(null,"secret".toCharArray()));
        fails(BadCredentialsException.class,()->identity.authenticate("allowed",null));
        fails(BadCredentialsException.class,()->identity.authenticate("missing","secret".toCharArray()));
        fails(BadCredentialsException.class,()->identity.authenticate("allowed","wrong".toCharArray()));
        fails(BadCredentialsException.class,()->identity.authenticate("allowed' OR '1'='1","secret".toCharArray()));
        fails(LockedException.class,()->identity.authenticate("locked","secret".toCharArray()));
        fails(LockedException.class,()->identity.authenticate("deprecated","secret".toCharArray()));
        fails(org.springframework.security.authentication.AccountExpiredException.class,()->identity.authenticate("expired","secret".toCharArray()));
        fails(DisabledException.class,()->identity.authenticate("suspended","secret".toCharArray()));
        fails(DisabledException.class,()->identity.authenticate("unknown-state","secret".toCharArray()));
        JdbcPersonnelIdentity.Principal allowed=identity.authenticate("allowed","secret".toCharArray());
        check("allowed".equals(allowed.getId()),"database user identity");check("org-a".equals(allowed.getOrgId()),"database organization");
        JdbcPersonnelIdentity.Principal adopted=identity.authenticatedPrincipal("allowed");
        check("allowed".equals(adopted.getId()) && "org-a".equals(adopted.getOrgId()),"authenticated project identity is revalidated");
        fails(AccessDeniedException.class,()->identity.authenticatedPrincipal("missing"));
        identity.authorizePersonnel(allowed);checks++;
        fails(AccessDeniedException.class,()->identity.authorizePersonnel(identity.authenticate("low","secret".toCharArray())));
        identity.authorizePersonnel(identity.authenticate("admin","secret".toCharArray()));checks++;
        check(identity.permittedOrganizationIds(allowed).equals(java.util.Collections.singleton("org-a")),"scope joins actual organization");
        fails(UnsupportedOperationException.class,()->identity.permittedOrganizationIds(allowed).add("injected-org"));
        fails(AccessDeniedException.class,()->identity.authorizePersonnel(null));
        JdbcPersonnelIdentityProvider webProvider=new JdbcPersonnelIdentityProvider(identity);
        InMemoryAuditEventSink webAudit=new InMemoryAuditEventSink();
        OwnedIdentityHttpBoundary webBoundary=new OwnedIdentityHttpBoundary(webProvider,webAudit);
        OracleSessionFixture webSession=new OracleSessionFixture();
        OwnedIdentityHttpController webController=new OwnedIdentityHttpController(webBoundary,new OwnedLoginCredentialDecoder(){
            public char[] decode(String userId,String encodedPassword,String transportKey){
                if(!"allowed".equals(userId)||!"ciphertext".equals(encodedPassword)||!"rsa-key".equals(transportKey)) throw new IllegalArgumentException("wire fields differ");
                return "secret".toCharArray();
            }
        });
        ResponseEntity<?> webLogin=webController.login(webSession.request("allowed","ciphertext","rsa-key"));
        check(webLogin.getStatusCode()==HttpStatus.OK,"Oracle identity accepts observed login route");
        webBoundary.requirePermission(webSession.session(),OwnedIdentityProvider.PERSONNEL_READ); checks++;
        check(webBoundary.permittedOrganizationIds(webSession.session()).equals(java.util.Collections.singleton("org-a")),"Oracle identity exposes current organization scope through HTTP seam");
        ResponseEntity<?> webLogout=webController.logout(webSession.request("allowed","ciphertext","rsa-key"));
        check(webLogout.getStatusCode()==HttpStatus.OK&&!webSession.hasPrincipal(),"Oracle identity logout clears HTTP session");
        check(webAudit.events().size()>=5,"Oracle HTTP identity emits login, permission, scope and logout audit events");
        try(Connection c=ds.getConnection();Statement s=c.createStatement()){s.executeUpdate("UPDATE T_CORE_USER SET ORGID='org-b',ORGNAME='Moved Org' WHERE ID='allowed'");}
        JdbcPersonnelIdentity.Principal refreshed=identity.refreshPrincipal(allowed);
        check("org-b".equals(refreshed.getOrgId()) && "Moved Org".equals(refreshed.getOrgName()),"fresh database organization snapshot");
        check("org-a".equals(allowed.getOrgId()),"original principal remains immutable");
        fails(AccessDeniedException.class,()->new JdbcPersonnelIdentity(ds,"MD5").authorizePersonnel(allowed));
        try(Connection c=ds.getConnection();Statement s=c.createStatement()){s.executeUpdate("DELETE FROM T_CORE_ROLE_PERMISSION WHERE ROLEID=1");}
        fails(AccessDeniedException.class,()->identity.authorizePersonnel(allowed));
        fails(IllegalArgumentException.class,()->new JdbcPersonnelIdentity(ds,"SM3"));
        try(Connection c=ds.getConnection();Statement s=c.createStatement()){s.executeUpdate("UPDATE T_CORE_PASSWORD_POLICY SET MAXFAILEDATTEMPTS=3");}
        fails(IllegalStateException.class,()->identity.authenticate("admin","secret".toCharArray()));
        try(Connection c=ds.getConnection();Statement s=c.createStatement()){s.executeUpdate("UPDATE T_CORE_PASSWORD_POLICY SET MAXFAILEDATTEMPTS=0");}
        for(String field:new String[]{"LOGINORG","LOGINROLE","LOGINTENANT","CAPTCHA","ADDOMAINSUPPORT","SHOWLOGINAUTHOPTION","QCCODE"}) {
            try(Connection c=ds.getConnection();Statement s=c.createStatement()){s.executeUpdate("UPDATE T_CORE_LOGIN_CONFIG SET "+field+"='1'");}
            fails(IllegalStateException.class,()->identity.authenticate("admin","secret".toCharArray()));
            fails(IllegalStateException.class,()->identity.authorizePersonnel(allowed));
            fails(IllegalStateException.class,()->identity.refreshPrincipal(allowed));
            fails(IllegalStateException.class,()->identity.permittedOrganizationIds(allowed));
            try(Connection c=ds.getConnection();Statement s=c.createStatement()){s.executeUpdate("UPDATE T_CORE_LOGIN_CONFIG SET "+field+"='0'");}
        }
        try(Connection c=ds.getConnection();Statement s=c.createStatement()){s.executeUpdate("UPDATE T_CORE_LOGIN_CONFIG SET LOGINAUTHMETHOD='sso'");}
        fails(IllegalStateException.class,()->identity.authenticate("admin","secret".toCharArray()));
        try(Connection c=ds.getConnection();Statement s=c.createStatement()){s.executeUpdate("UPDATE T_CORE_LOGIN_CONFIG SET LOGINAUTHMETHOD='system'");s.executeUpdate("UPDATE T_CORE_USER SET LASTSUSPENDEDFLAG='1' WHERE ID='allowed'");}
        fails(DisabledException.class,()->identity.authorizePersonnel(allowed));
        fails(DisabledException.class,()->identity.refreshPrincipal(allowed));
        fails(DisabledException.class,()->identity.permittedOrganizationIds(allowed));
        try(Connection c=ds.getConnection();Statement s=c.createStatement()){s.executeUpdate("DELETE FROM T_CORE_LOGIN_CONFIG");}
        fails(IllegalStateException.class,()->identity.authenticate("admin","secret".toCharArray()));
        try(Connection c=ds.getConnection();Statement s=c.createStatement()){s.execute("DROP TABLE T_CORE_LOGIN_CONFIG");}
        fails(SQLException.class,()->identity.authenticate("admin","secret".toCharArray()));
        System.out.println("JDBC personnel identity: PASS ("+checks+" checks; synthetic Oracle only)");
    }

    private static final class OracleSessionFixture implements InvocationHandler {
        private final java.util.Map<String,Object> values=new java.util.HashMap<String,Object>();
        private final HttpSession session=(HttpSession)Proxy.newProxyInstance(HttpSession.class.getClassLoader(),new Class<?>[]{HttpSession.class},this);
        HttpSession session(){return session;}
        boolean hasPrincipal(){return values.containsKey(OwnedIdentityHttpBoundary.SESSION_ATTRIBUTE);}
        HttpServletRequest request(final String userId,final String password,final String key){
            return (HttpServletRequest)Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(),new Class<?>[]{HttpServletRequest.class},new InvocationHandler(){
                public Object invoke(Object proxy,Method method,Object[] args){
                    String name=method.getName();
                    if("getParameter".equals(name)) return "id".equals(args[0])?userId:"password".equals(args[0])?password:null;
                    if("getHeader".equals(name)) return "SECURITY-RSA-KEY".equals(args[0])?key:null;
                    if("getSession".equals(name)) return session;
                    Class<?> type=method.getReturnType();
                    if(!type.isPrimitive()) return null;
                    if(type==boolean.class)return false;
                    if(type==byte.class||type==short.class||type==int.class||type==long.class||type==float.class||type==double.class)return 0;
                    if(type==char.class)return '\0';
                    return null;
                }
            });
        }
        public Object invoke(Object proxy,Method method,Object[] args){
            String name=method.getName();
            if("getAttribute".equals(name))return values.get(args[0]);
            if("setAttribute".equals(name)){values.put((String)args[0],args[1]);return null;}
            if("removeAttribute".equals(name)){values.remove(args[0]);return null;}
            if("getId".equals(name))return "oracle-fixture-session";
            Class<?> type=method.getReturnType();
            if(!type.isPrimitive())return null;
            if(type==boolean.class)return false;
            if(type==byte.class||type==short.class||type==int.class||type==long.class||type==float.class||type==double.class)return 0;
            if(type==char.class)return '\0';
            return null;
        }
    }
}
