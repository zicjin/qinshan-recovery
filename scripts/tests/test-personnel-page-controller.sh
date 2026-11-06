#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
JAVAC_BIN="$JDK/bin/javac" JAR_BIN="$JDK/bin/jar" JAVA_BIN="$JDK/bin/java" "$ROOT/scripts/build-owned-framework.sh" >"$TMP/build.log"
mkdir -p "$TMP/classes" "$TMP/test"
CP="$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/public-deps/*"
"$JDK/bin/javac" -source 8 -target 8 -cp "$CP" -d "$TMP/classes" \
  "$ROOT/project/src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/controller/EmcEmpController.java" \
  "$ROOT/project/src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/controller/impl/EmcEmpControllerImpl.java"
cat > "$TMP/test/ControllerCheck.java" <<'JAVA'
import com.sunwayworld.emc.module.mdm.emp.emp.controller.impl.EmcEmpControllerImpl;
public final class ControllerCheck {
  public static void main(String[] args) {
    EmcEmpControllerImpl c = new EmcEmpControllerImpl();
    if (!"emc/module/mdm/emp/emps/emc-emp-edit-list".equals(c.editListPage())) throw new AssertionError();
    if (!"emc/module/mdm/emp/emps/emc-emp-detail".equals(c.detailPage())) throw new AssertionError();
    if (!"emc/module/mdm/emp/emps/emc-emp-choose-list".equals(c.choosePage())) throw new AssertionError();
    if (!"emc/module/mdm/emp/emps/emc-emp-report-choose-list".equals(c.reportEmpChoosePage())) throw new AssertionError();
    if (!"emc/module/mdm/emp/emps/emc-emp-sample-choose-list".equals(c.sampleEmpChoosePage())) throw new AssertionError();
    System.out.println("personnel page controller: PASS (5 template contracts)");
  }
}
JAVA
"$JDK/bin/javac" -source 8 -target 8 -cp "$CP:$TMP/classes" -d "$TMP/test" "$TMP/test/ControllerCheck.java"
"$JDK/bin/java" -cp "$CP:$TMP/classes:$TMP/test" ControllerCheck
cat > "$TMP/test/SpringControllerCheck.java" <<'JAVA'
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.method.HandlerMethod;
import com.sunwayworld.emc.module.mdm.emp.emp.controller.impl.EmcEmpControllerImpl;
public final class SpringControllerCheck {
  public static void main(String[] args) {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.register(EmcEmpControllerImpl.class);
    context.refresh();
    RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();
    mapping.setApplicationContext(context);
    mapping.afterPropertiesSet();
    Set<String> paths = new HashSet<String>();
    for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : mapping.getHandlerMethods().entrySet()) {
      for (String pattern : entry.getKey().getPatternsCondition().getPatterns()) paths.add(pattern);
    }
    for (String expected : new String[]{
        "/secure/emc/module/mdm/emp/emps/page/edit-list",
        "/secure/emc/module/mdm/emp/emps/page/detail",
        "/secure/emc/module/mdm/emp/emps/page/choose-list",
        "/secure/emc/module/mdm/emp/emps/page/report-users/choose-list",
        "/secure/emc/module/mdm/emp/emps/page/sample/choose-list"}) {
      if (!paths.contains(expected)) throw new AssertionError("missing Spring mapping: " + expected + " in " + paths);
    }
    context.close();
    System.out.println("personnel Spring mappings: PASS (5 registered routes)");
  }
}
JAVA
"$JDK/bin/javac" -source 8 -target 8 -cp "$CP:$TMP/classes:$TMP/test" -d "$TMP/test" "$TMP/test/SpringControllerCheck.java"
"$JDK/bin/java" -cp "$CP:$TMP/classes:$TMP/test" SpringControllerCheck
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/test-classes:$ROOT/owned-framework/target/public-deps/*" com.sunwayworld.framework.GikamConditionTest
python3 - "$ROOT" "$TMP" <<'PY'
import hashlib, json, pathlib, sys, zipfile
root, output = map(pathlib.Path, sys.argv[1:])
name = 'BOOT-INF/lib/gikam-core-common-14.1.5.jar'
expected = next(row['sha256'] for row in json.loads((root/'.scratch/sunway-supplier-exit-api-first/evidence-archive/framework-contract/jars.json').read_text()) if row['jar'] == name)
with zipfile.ZipFile(root/'materials/gikam-third-czjky-14.1.5-run.jar') as archive:
    data = archive.read(name)
    assert hashlib.sha256(data).hexdigest() == expected, 'supplier reference hash differs'
    (output/'supplier.jar').write_bytes(data)
PY
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/test-classes" com.sunwayworld.framework.ControllerDifferentialTest \
  "$ROOT/owned-framework/target/public-deps" "$ROOT/owned-framework/target/classes" \
  "$ROOT/owned-framework/target/test-classes" "$TMP/classes" "$TMP/supplier.jar"
echo 'test-personnel-page-controller: PASS'
