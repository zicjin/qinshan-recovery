#!/usr/bin/env python3
"""Exercise current original-auth adoption and encoder seam without Oracle."""
import argparse
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]
JAVA = r'''
import com.sunwayworld.boot.security.SunwaySecurityConfiguration;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity.Principal;
import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class OriginalIdentityEncoderHandoffCheck {
  private static int checks;
  interface Attempt { void run() throws Exception; }
  static void check(boolean value, String label) { checks++; if (!value) throw new AssertionError(label); }
  static void rejects(Class<? extends Throwable> type, Attempt action) throws Exception {
    try { action.run(); throw new AssertionError("expected rejection " + type.getSimpleName()); }
    catch (Throwable failure) { if (!type.isInstance(failure)) throw new AssertionError("wrong rejection",failure); checks++; }
  }
  public static void main(String[] args) throws Exception {
    StaticApplicationContext context = new StaticApplicationContext();
    context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test",
        Collections.<String,Object>singletonMap("sunway.user.password.encoder.type", "SM3")));
    context.getBeanFactory().registerSingleton("objectPostProcessor", new ObjectPostProcessor<Object>() {
      public <O extends Object> O postProcess(O object) { return object; }
    });
    SunwaySecurityConfiguration configuration = new SunwaySecurityConfiguration();
    configuration.setApplicationContext(context);
    PasswordEncoder encoder = configuration.passwordEncoder();
    Fixture fixture = new Fixture(); fixture.password = encoder.encode("Fixture2026#Only");
    String[] none = new String[0];
    JdbcPersonnelIdentity identity = new OwnedIdentityConfiguration().ownedJdbcPersonnelIdentity(
        fixture.source, context.getEnvironment(), encoder);
    rejects(IllegalArgumentException.class, () -> new JdbcPersonnelIdentity(fixture.source,"SM3"));
    rejects(NullPointerException.class, () -> new JdbcPersonnelIdentity(fixture.source,"SM3",null,
        none,none,none,none,none,none,none));
    rejects(IllegalArgumentException.class, () -> new JdbcPersonnelIdentity(fixture.source,"unknown",encoder,
        none,none,none,none,none,none,none));
    SecurityContextHolder.clearContext(); fixture.policy = true;
    rejects(IllegalStateException.class, () -> identity.authenticatedPrincipal("fixture-user"));
    setAuthentication(new UsernamePasswordAuthenticationToken("fixture-user",null,Collections.emptyList()));
    rejects(IllegalStateException.class, () -> identity.authenticatedPrincipal("fixture-user"));
    setAuthentication(new AnonymousAuthenticationToken("test", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));
    rejects(IllegalStateException.class, () -> identity.authenticatedPrincipal("fixture-user"));
    setAuthentication(new UsernamePasswordAuthenticationToken(new LoginUser("other-user","erased"),null,Collections.emptyList()));
    rejects(IllegalStateException.class, () -> identity.authenticatedPrincipal("fixture-user"));
    setAuthentication(new UsernamePasswordAuthenticationToken(new LoginUser("fixture-user","erased"),null));
    rejects(IllegalStateException.class, () -> identity.authenticatedPrincipal("fixture-user"));

    setAuthentication(new UsernamePasswordAuthenticationToken(new LoginUser("fixture-user","erased"),null,Collections.emptyList()));
    int before = fixture.configReads;
    Principal adopted = identity.authenticatedPrincipal("fixture-user");
    check("fixture-user".equals(adopted.getId()), "original LoginUser adopted");
    identity.authorizePersonnel(adopted);
    Principal refreshed = identity.refreshPrincipal(adopted);
    identity.authorizePersonnel(refreshed);
    check(fixture.configReads == before, "adoption and refresh do not repeat narrow login policy");
    check(identity.permittedOrganizationIds(refreshed).equals(Collections.singleton("fixture-org")),"organization grant remains explicit");
    fixture.organizationGranted = false;
    check(identity.permittedOrganizationIds(refreshed).isEmpty(),"revoked organization grant read afresh");
    fixture.organizationGranted = true;
    fixture.menuGranted = false;
    rejects(AccessDeniedException.class, () -> identity.authorizePersonnel(refreshed));
    fixture.menuGranted = true;
    fixture.status = "locked";
    rejects(LockedException.class, () -> identity.authorizePersonnel(refreshed));
    rejects(LockedException.class, () -> identity.refreshPrincipal(refreshed));
    fixture.status = "activated"; fixture.suspended = "1";
    rejects(DisabledException.class, () -> identity.permittedOrganizationIds(refreshed));
    fixture.suspended = "0";
    JdbcPersonnelIdentity other = new JdbcPersonnelIdentity(fixture.source,"MD5");
    rejects(AccessDeniedException.class, () -> other.authorizePersonnel(refreshed));
    rejects(IllegalStateException.class, () -> identity.authenticate("fixture-user","Fixture2026#Only".toCharArray()));

    SecurityContextHolder.clearContext(); fixture.policy = false;
    Principal explicit = identity.authenticate("fixture-user","Fixture2026#Only".toCharArray());
    check("fixture-user".equals(explicit.getId()),"actual SM3 encoder correct password accepted with bounded simple config");
    check(com.sunwayworld.framework.cache.memory.MemoryCacheManager.get(Long.toString(Thread.currentThread().getId())) == null,
        "encoder raw-password thread cache cleared");
    rejects(BadCredentialsException.class, () -> identity.authenticate("fixture-user","wrong".toCharArray()));
    fixture.policy = true;
    rejects(IllegalStateException.class, () -> identity.authorizePersonnel(explicit));
    rejects(IllegalStateException.class, () -> identity.refreshPrincipal(explicit));
    rejects(IllegalStateException.class, () -> identity.permittedOrganizationIds(explicit));
    SecurityContextHolder.clearContext(); context.close();
    System.out.println("PASS original authentication/encoder handoff ("+checks+" checks; no Oracle)");
  }
  static void setAuthentication(Authentication authentication) {
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
  static final class Fixture {
    String password, status="activated", suspended="0";
    boolean policy=true,menuGranted=true,organizationGranted=true;
    int configReads;
    DataSource source = proxy(DataSource.class,(p,m,a) -> {
      if (m.getName().equals("getConnection")) return connection();
      throw new AssertionError("unexpected DataSource operation "+m.getName());
    });
    Connection connection() {
      return proxy(Connection.class,(p,m,a) -> {
        if (m.getName().equals("close")) return null;
        if (m.getName().equals("prepareStatement")) return statement((String)a[0]);
        if (m.getName().equals("getMetaData")) return proxy(DatabaseMetaData.class,(p2,m2,a2) -> {
          if (m2.getName().equals("getColumns")) return rows(Collections.emptyList());
          throw new AssertionError("unexpected metadata operation "+m2.getName());
        });
        throw new AssertionError("unexpected Connection operation "+m.getName());
      });
    }
    PreparedStatement statement(String sql) {
      Map<Integer,Object> arguments=new HashMap<>();
      return proxy(PreparedStatement.class,(p,m,a) -> {
        if (m.getName().equals("close")) return null;
        if (m.getName().equals("setString")) { arguments.put((Integer)a[0],a[1]);return null; }
        if (m.getName().equals("executeQuery")) {
          if (sql.contains("FROM T_CORE_LOGIN_CONFIG")) {
            configReads++;return rows(Collections.singletonList(new Object[]{"0","0",null,"0","0","0","0"}));
          }
          if (sql.contains("FROM T_CORE_PASSWORD_POLICY")) return rows(Collections.singletonList(new Object[]{policy?1:0}));
          if (sql.contains("FROM T_CORE_USER WHERE ID = ?")) {
            return rows("fixture-user".equals(arguments.get(1)) ? Collections.singletonList(
                new Object[]{"fixture-user",password,"fixture-org","Fixture Org",status,suspended,null}):Collections.emptyList());
          }
          if (sql.contains("FROM T_CORE_MENU M")) return rows(menuGranted ? Collections.singletonList(new Object[]{1}):Collections.emptyList());
          if (sql.contains("FROM T_CORE_USER_PERMISSION P JOIN T_CORE_ORG")) return rows(organizationGranted ?
              Collections.singletonList(new Object[]{"fixture-org"}):Collections.emptyList());
          throw new AssertionError("unexpected SQL contract");
        }
        throw new AssertionError("unexpected PreparedStatement operation "+m.getName());
      });
    }
  }
  static ResultSet rows(List<Object[]> data) {
    int[] index={-1};
    return proxy(ResultSet.class,(p,m,a) -> {
      if (m.getName().equals("close")) return null;
      if (m.getName().equals("next")) return ++index[0]<data.size();
      if (m.getName().equals("getString")) { Object value=data.get(index[0])[(Integer)a[0]-1];return value==null?null:value.toString(); }
      if (m.getName().equals("getInt")) return ((Number)data.get(index[0])[(Integer)a[0]-1]).intValue();
      if (m.getName().equals("getTimestamp")) return (Timestamp)data.get(index[0])[(Integer)a[0]-1];
      throw new AssertionError("unexpected ResultSet operation "+m.getName());
    });
  }
  @SuppressWarnings("unchecked")
  static <T> T proxy(Class<T> type, InvocationHandler handler) {
    return (T)Proxy.newProxyInstance(type.getClassLoader(),new Class<?>[]{type},handler);
  }
}
'''


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--prerequisite-root', type=Path, default=ROOT)
    args = parser.parse_args()
    prereq = args.prerequisite_root.resolve()
    jdk = prereq / 'build/jdk1.8.0_391/bin'
    framework = sorted((prereq / 'reconstituted/framework').glob('*/target/classes'))
    deps = sorted((prereq / 'build/reconstituted-public-deps').glob('*.jar'))
    if len(framework) != 7 or not deps or not (jdk / 'javac').is_file():
        parser.error('source-built framework classes, public dependencies and Java8 toolchain are required')
    if any(path.name.startswith('gikam-') or path.name.endswith('-run.jar') for path in deps):
        parser.error('supplier JAR is forbidden on this test classpath')
    common = ROOT / 'reconstituted/framework/owned-core-common/src/main/java'
    sources = [ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/JdbcPersonnelIdentity.java',
               ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedIdentityConfiguration.java',
               common / 'com/sunwayworld/boot/security/SunwaySecurityConfiguration.java',
               common / 'com/sunwayworld/framework/utils/EncryptUtils.java']
    with tempfile.TemporaryDirectory(prefix='original-identity-encoder-') as directory:
        temporary = Path(directory)
        fixture = temporary / 'OriginalIdentityEncoderHandoffCheck.java'
        fixture.write_text(JAVA)
        logging = temporary / 'log4j2.xml'
        logging.write_text('<Configuration status="OFF"><Loggers><Root level="OFF"/></Loggers></Configuration>')
        cp = ':'.join(map(str,[temporary,prereq/'owned-framework/target/classes',*framework,*deps]))
        subprocess.run([str(jdk/'javac'),'-encoding','UTF-8','-parameters','-sourcepath',str(temporary),
                        '-cp',cp,'-d',str(temporary),*map(str,sources),str(fixture)],check=True)
        subprocess.run([str(jdk/'java'),'-Dlog4j.configurationFile='+str(logging),'-cp',cp,
                        'OriginalIdentityEncoderHandoffCheck'],check=True,timeout=30)
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
