#!/usr/bin/env python3
"""Render original equipment controller views and execute their original JS.

Requires source-only framework/public build inputs and the matching spring-test
JAR. Build inputs are copied to a temporary directory; no shared target is used.
The UI boundary records layout options; no business HTTP or database is used.
"""
import argparse
from pathlib import Path
import shutil
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]
CASES = ['verification-edit', 'verification-audit', 'verification-search',
         'check-edit', 'check-audit', 'check-search', 'appearance-edit', 'appearance-choose']
JAVA = r'''
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import com.sunwayworld.emc.module.mdm.equipt.verification.controller.impl.EmcEquipVerificationControllerImpl;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.controller.impl.EmcEquipCheckControllerImpl;

public final class EquipmentPageFragmentsCheck {
    public static final class Resources {
        private final ScriptEngine javascript;
        public int loaded;
        Resources(ScriptEngine javascript) { this.javascript = javascript; }
        public void load(String path) throws Exception {
            if (!path.startsWith("/static/") || !path.endsWith(".js"))
                throw new AssertionError("Unexpected script dependency: " + path);
            try (InputStream input = getClass().getClassLoader().getResourceAsStream(path.substring(1))) {
                if (input == null) throw new AssertionError("Missing rendered JavaScript: " + path);
                javascript.eval(new InputStreamReader(input, "UTF-8"));
            }
            loaded++;
        }
    }
    private static void require(ScriptEngine javascript, String expression, String message) throws Exception {
        if (!Boolean.TRUE.equals(javascript.eval(expression))) throw new AssertionError(message);
    }
    public static void main(String[] args) throws Exception {
        String[] name = args[0].split("-");
        String family = name[0], page = name[1];
        boolean verification = family.equals("verification"), appearance = family.equals("appearance");
        String directory = "emc/module/mdm/equipt/" + (verification ? "equip-verifications" : "equip-checks");
        String stem = "emc-equip-" + family + "-" + page + "-list";
        String route = appearance ? (page.equals("edit") ? "appearance-list" : "check-list") : page + "-list";
        String url = "/secure/" + directory + "/page/" + route;
        ClassLoaderTemplateResolver templates = new ClassLoaderTemplateResolver();
        templates.setPrefix("templates/");
        templates.setSuffix(".html");
        templates.setCharacterEncoding("UTF-8");
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templates);
        ThymeleafViewResolver views = new ThymeleafViewResolver();
        views.setTemplateEngine(engine);
        views.setCharacterEncoding("UTF-8");
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new EmcEquipVerificationControllerImpl(),
                new EmcEquipCheckControllerImpl()).setViewResolvers(views).build();
        MvcResult response = mvc.perform(MockMvcRequestBuilders.get(url)
                .param("single", "true").param("marker", "preserve-me")).andReturn();
        if (response.getResponse().getStatus() != 200) throw new AssertionError("GET failed: " + url);
        if (response.getModelAndView() == null || !(directory + "/" + stem).equals(response.getModelAndView().getViewName()))
            throw new AssertionError("Original controller view changed: " + url);
        ScriptEngine javascript = new ScriptEngineManager().getEngineByName("nashorn");
        if (javascript == null) throw new AssertionError("Use the supported JDK 8 runtime");
        Resources resources = new Resources(javascript);
        javascript.put("resources", resources);
        javascript.eval("var IFM_CONTEXT = ''; var renderedLayout = null; var layoutCount = 0;"
                + "var workspace = {window: {$dom: {id: 'workspace'}}, user: {id: 'test-user'}};"
                + "var modal = {window: {$dom: {id: 'modal'}}};"
                + "var Gikam = {getPageObject: function(o) {return Object.create(o);},"
                + "requirejs: function(paths) {paths.forEach(function(p) {resources.load(p);});},"
                + "getElParam: function(p) {var o = {}; Object.keys(p || {}).forEach(function(k) {o[k] = p[k][0];}); return o;},"
                + "status: {getBpmnColumn: function(table) {return {field: 'bpmnStatus', table: table};}},"
                + "getLastModal: function() {return modal;},"
                + "create: function(type, options) {if (type !== 'layout') throw new Error('Unexpected UI: ' + type);"
                + "renderedLayout = options; layoutCount++;}};");
        Matcher scripts = Pattern.compile("<script[^>]*>([\\s\\S]*?)</script>").matcher(response.getResponse().getContentAsString());
        int inlineCount = 0;
        while (scripts.find()) { javascript.eval(scripts.group(1)); inlineCount++; }
        if (inlineCount == 0 || resources.loaded == 0) throw new AssertionError("Page has no executable original fragment");
        require(javascript, "layoutCount === 1 && renderedLayout.id === '" + stem + "-layout'", "Initializer did not render original layout");
        require(javascript, "renderedLayout.renderTo === " + (page.equals("choose") ? "modal" : "workspace") + ".window.$dom", "Wrong page/modal render target");
        String grid = !verification && !appearance ? "renderedLayout.north.items[0]" : "renderedLayout.center.items[0]";
        require(javascript, grid + ".id === '" + stem + "-grid' && " + grid + ".columns.length > 0", "Original grid missing");
        String suffix = page.equals("edit") ? "raw" : page.equals("audit") ? "tasks" : page.equals("choose") ? "choosable" : "searchable";
        String base = "/secure/emc/module/mdm/equipt/" + (verification ? "equip-verifications" : appearance ? "equip-appearances" : "equip-checks");
        require(javascript, grid + ".url === '" + base + "/queries/" + suffix + "'", "Wrong original query endpoint");
        if (page.equals("audit")) {
            String field = verification ? "confirmerId_SEQ" : "auditorId_SEQ";
            require(javascript, grid + ".requestData." + field + " === 'test-user' && " + grid + ".requestData.marker === 'preserve-me'", "Audit initializer lost user/request scope");
        }
        if (page.equals("choose")) require(javascript, grid + ".columns[0].radio === true && " + grid + ".requestData.marker === 'preserve-me'", "Chooser parameters lost");
        if (!verification && !appearance) {
            require(javascript, "renderedLayout.center.items[0].panels[0].items[0].id === 'emc-equip-check-part-" + page + "-list-grid'"
                    + " && renderedLayout.center.items[0].panels[1].items[0].id === 'emc-equip-check-data-" + page + "-list-grid'", "Original parts/data tabs missing");
        }
        System.out.println(args[0] + ": PASS (original GET, Thymeleaf, dependency loading and JS initialization)");
    }
}
'''


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--jdk', type=Path, default=ROOT/'build/jdk1.8.0_391')
    parser.add_argument('--framework-jar', type=Path, default=ROOT/'reconstituted/framework/owned-core-common/target/owned-core-common-1.0.0-SNAPSHOT.jar')
    parser.add_argument('--public-deps', type=Path, default=ROOT/'build/reconstituted-public-deps')
    parser.add_argument('--spring-test', type=Path, required=True)
    parser.add_argument('--case', choices=['all'] + CASES, default='all')
    args = parser.parse_args()
    for path in [args.jdk/'bin/javac', args.framework_jar, args.public_deps, args.spring_test]:
        if not path.exists():
            parser.error(f'missing source-only test prerequisite: {path}')
    with tempfile.TemporaryDirectory(prefix='equipment-page-fragments-') as temporary:
        temp = Path(temporary)
        deps = temp/'deps'
        shutil.copytree(args.public_deps, deps)
        shutil.copy2(args.framework_jar, temp/'framework.jar')
        shutil.copy2(args.spring_test, temp/'spring-test.jar')
        harness = temp/'EquipmentPageFragmentsCheck.java'
        harness.write_text(JAVA, encoding='utf-8')
        cp = ':'.join(map(str, [temp, ROOT/'project/src/main/resources',
                              ROOT/'reconstituted/framework/owned-core-common/src/main/resources',
                              temp/'framework.jar', deps/'*', temp/'spring-test.jar']))
        source_root = ROOT/'project/src/main/java/com/sunwayworld/emc/module/mdm/equipt'
        controllers = []
        for folder, name in [('verification', 'EmcEquipVerificationController'),
                             ('equiptcheck', 'EmcEquipCheckController')]:
            controller = source_root/folder/'controller'
            controllers.extend([controller/(name+'.java'), controller/'impl'/(name+'Impl.java')])
        subprocess.run([str(args.jdk/'bin/javac'), '-source', '8', '-target', '8', '-encoding', 'UTF-8',
                        '-cp', cp, '-d', str(temp), *map(str, controllers), str(harness)], check=True)
        for case in CASES if args.case == 'all' else [args.case]:
            subprocess.run([str(args.jdk/'bin/java'), '-cp', cp, 'EquipmentPageFragmentsCheck', case], check=True)
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
