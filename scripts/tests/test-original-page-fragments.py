#!/usr/bin/env python3
"""Exercise original controller HTTP mappings and rendered static dependencies.

Requires a source-only framework build and the matching public spring-test JAR.
The caller may provide read-only build inputs from another checkout; compiler
output always stays in a fresh temporary directory.
"""
import argparse
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]
JAVA = r'''
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.controller.impl.EmcEquiptRequisitionControllerImpl;

public final class OriginalPageFragmentsCheck {
    private static void dependencies(String html) throws Exception {
        Matcher matches = Pattern.compile("['\"](/static/[^'\"]+\\.js)['\"]").matcher(html);
        int count = 0;
        while (matches.find()) {
            String path = matches.group(1).substring(1);
            try (InputStream input = OriginalPageFragmentsCheck.class.getClassLoader().getResourceAsStream(path)) {
                if (input == null) throw new AssertionError("Rendered page requests missing JavaScript: /" + path);
            }
            count++;
        }
        if (count == 0) throw new AssertionError("Expected original fragment JavaScript dependencies");
    }
    public static void main(String[] args) throws Exception {
        ClassLoaderTemplateResolver templates = new ClassLoaderTemplateResolver();
        templates.setPrefix("templates/");
        templates.setSuffix(".html");
        templates.setCharacterEncoding("UTF-8");
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templates);
        if (args[0].equals("sample-scheme")) {
            String html = engine.process("module/item/sample-schemes/core-sample-scheme-edit-list", new Context(Locale.CHINA));
            if (!html.contains("coreSampleScheme.editPage.init()")) throw new AssertionError("Original initializer lost");
            dependencies(html);
            ScriptEngine javascript = new ScriptEngineManager().getEngineByName("nashorn");
            if (javascript == null) throw new AssertionError("Use the supported JDK 8 test runtime");
            javascript.eval("var renderedLayout = null; var workspace = {window: {$dom: {}}}; var Gikam = {create: function(type, options) { renderedLayout = options; }};");
            try (InputStream source = OriginalPageFragmentsCheck.class.getClassLoader().getResourceAsStream("static/module/item/sample-schemes/core-sample-scheme-edit-list.js")) {
                javascript.eval(new InputStreamReader(source, "UTF-8"));
            }
            javascript.eval("coreSampleScheme.editPage.init();");
            if (!Boolean.TRUE.equals(javascript.eval("renderedLayout.center.items[0].id === 'core-sample-scheme-edit-list-aql'"))) throw new AssertionError("Original sample-scheme initializer did not create its grid");
            System.out.println("sample-scheme fragment: PASS (all requested JavaScript exists)");
            return;
        }
        ThymeleafViewResolver views = new ThymeleafViewResolver();
        views.setTemplateEngine(engine);
        views.setCharacterEncoding("UTF-8");
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new EmcEquiptRequisitionControllerImpl()).setViewResolvers(views).build();
        for (String page : new String[]{"audit", "search"}) {
            String url = "/secure/emc/module/mdm/basemdm/equipt-requisitions/page/" + page + "-list";
            MvcResult result = mvc.perform(MockMvcRequestBuilders.get(url)).andReturn();
            if (result.getResponse().getStatus() != 200) throw new AssertionError(url + " returned " + result.getResponse().getStatus());
            String expected = "emc/module/mdm/basemdm/equipt-requisitions/emc-equipt-requisition-" + page + "-list";
            if (result.getModelAndView() == null || !expected.equals(result.getModelAndView().getViewName())) throw new AssertionError("Wrong original view for " + url);
            String html = result.getResponse().getContentAsString();
            if (!html.contains("emcEquiptRequisition." + page + "Page.init()")) throw new AssertionError("Original fragment missing for " + url);
            dependencies(html);
        }
        System.out.println("requisition page GET: PASS (audit/search routes, original HTML and JavaScript dependencies)");
    }
}
'''


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--jdk', type=Path, default=ROOT/'build/jdk1.8.0_391')
    parser.add_argument('--framework-jar', type=Path, default=ROOT/'reconstituted/framework/owned-core-common/target/owned-core-common-1.0.0-SNAPSHOT.jar')
    parser.add_argument('--public-deps', type=Path, default=ROOT/'build/reconstituted-public-deps')
    parser.add_argument('--spring-test', type=Path, required=True)
    parser.add_argument('--case', choices=['requisition', 'sample-scheme'], default='requisition')
    args = parser.parse_args()
    for path in [args.jdk/'bin/javac', args.framework_jar, args.public_deps, args.spring_test]:
        if not path.exists():
            parser.error(f'missing source-only test prerequisite: {path}')
    with tempfile.TemporaryDirectory(prefix='original-page-fragments-') as temporary:
        temp = Path(temporary)
        harness = temp/'OriginalPageFragmentsCheck.java'
        harness.write_text(JAVA)
        folder = ROOT/'project/src/main/java/com/sunwayworld/emc/module/mdm/basemdm/equiptrequisition/controller'
        cp = ':'.join(map(str, [temp, ROOT/'project/src/main/resources',
                              ROOT/'reconstituted/framework/owned-core-common/src/main/resources',
                              args.framework_jar, args.public_deps/'*', args.spring_test]))
        subprocess.run([str(args.jdk/'bin/javac'), '-source', '8', '-target', '8', '-encoding', 'UTF-8', '-cp', cp,
                        '-d', str(temp), str(folder/'EmcEquiptRequisitionController.java'),
                        str(folder/'impl/EmcEquiptRequisitionControllerImpl.java'), str(harness)], check=True)
        subprocess.run([str(args.jdk/'bin/java'), '-cp', cp, 'OriginalPageFragmentsCheck', args.case], check=True)
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
