#!/usr/bin/env python3
"""Compile the actual menu service to a temporary output and exercise icon URLs.

Uses source-only module outputs and public dependencies. Does not build modules,
access HTTP/SQL, mutate a shared target, or load a supplier private JAR.
"""
import argparse
from pathlib import Path
import shutil
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]
JAVA = r'''
import java.lang.reflect.*;
import com.sunwayworld.module.sys.menu.service.impl.CoreMenuServiceImpl;
import com.sunwayworld.module.sys.config.bean.CoreSystemConfigBean;
import com.sunwayworld.module.sys.config.service.CoreSystemConfigService;
public class MenuIconSentinelContract {
    private static void equal(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + ": expected=" + expected + ",actual=" + actual);
        }
    }
    public static void main(String[] args) throws Exception {
        CoreMenuServiceImpl service = new CoreMenuServiceImpl();
        Method icon = CoreMenuServiceImpl.class.getDeclaredMethod("getMenuIconUrl", String.class);
        icon.setAccessible(true);
        CoreSystemConfigBean config = new CoreSystemConfigBean();
        config.setPageStyle("xialan"); config.setPageSkin("#123456");
        CoreSystemConfigService configService = (CoreSystemConfigService) Proxy.newProxyInstance(
            CoreSystemConfigService.class.getClassLoader(), new Class<?>[] {CoreSystemConfigService.class},
            (proxy, method, values) -> {
                if ("selectById".equals(method.getName())) return config;
                throw new AssertionError("unexpected configuration operation " + method.getName());
            });
        Field configField = CoreMenuServiceImpl.class.getDeclaredField("systemConfigService");
        configField.setAccessible(true); configField.set(service, configService);
        for (String value : new String[] {null, "", "null", "NULL", " null ", " ", "undefined", "UNDEFINED"}) {
            equal(null, icon.invoke(service, value), "missing icon must not build a resource URL: " + value);
        }
        equal("/static/icon/menu/xialan/005.svg", icon.invoke(service, "005.svg"), "uncached style color prefix");
        config.setPageStyle("peony"); config.setPageSkin("white");
        equal("/static/icon/menu/peony/white/061.svg", icon.invoke(service, "061.svg"), "uncached named skin prefix");
        Field context = CoreMenuServiceImpl.class.getDeclaredField("LOCAL_SELECT_MENU_REQUIRED_DATA");
        context.setAccessible(true);
        ThreadLocal local = (ThreadLocal) context.get(null);
        Class<?> required = Class.forName(CoreMenuServiceImpl.class.getName() + "$SelectMenuRequiredData");
        Method of = required.getDeclaredMethod("of", String.class, String.class, Long.class, String.class);
        of.setAccessible(true);
        try {
            for (String prefix : new String[] {"/static/icon/menu/xialan/", "/static/icon/menu/peony/white/"}) {
                local.set(of.invoke(null, "test-principal", "test-org", 1L, prefix));
                for (String value : new String[] {"005.svg", "061.svg", "common.svg", "007.png"}) {
                    equal(prefix + value, icon.invoke(service, value), "actual named resource and skin preserved");
                }
                equal(null, icon.invoke(service, "null"), "sentinel remains absent with cached prefix");
            }
        } finally { local.remove(); }
        System.out.println("menu icon sentinel: PASS (actual service, absent/sentinel values, both prefix branches)");
    }
}
'''

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--repo-root", type=Path, default=ROOT)
    parser.add_argument("--jdk", type=Path)
    args = parser.parse_args()
    root = args.repo_root.resolve()
    dependencies = sorted((ROOT / "build/reconstituted-public-deps").glob("*.jar"))
    modules = sorted((ROOT / "reconstituted/framework").glob("*/target/*.jar"))
    owned = ROOT / "owned-framework/target/lims-owned-framework-1.0.0.jar"
    if not dependencies or not modules or not owned.is_file():
        parser.error("existing source-only modules and public dependencies required; this test never builds them")
    javac = str(args.jdk / "bin/javac") if args.jdk else shutil.which("javac")
    java = str(args.jdk / "bin/java") if args.jdk else shutil.which("java")
    if not javac or not java:
        parser.error("Java compiler/runtime required")
    actual = root / "reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/module/sys/menu/service/impl/CoreMenuServiceImpl.java"
    with tempfile.TemporaryDirectory(prefix="menu-icon-sentinel-") as folder:
        temp = Path(folder)
        source = temp / "MenuIconSentinelContract.java"
        source.write_text(JAVA, encoding="utf-8")
        classpath = ":".join(str(path) for path in [temp, owned, *modules, *dependencies])
        subprocess.run([javac, "-source", "8", "-target", "8", "-encoding", "UTF-8", "-cp", classpath,
                        "-d", str(temp), "-proc:none", str(actual), str(source)], check=True)
        subprocess.run([java, "-cp", classpath, "MenuIconSentinelContract"], check=True)

if __name__ == "__main__":
    main()
