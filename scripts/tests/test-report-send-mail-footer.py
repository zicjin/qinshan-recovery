#!/usr/bin/env python3
"""Compile delivered courier Java and capture MIME through the existing mail helper.

Caller-selected source-only outputs/public dependencies are read-only prerequisites.
Compilation and execution use a temporary directory; no SMTP, HTTP, Oracle or build
is started. The SQL package is the tested source, with no private capture input.
"""
import argparse
from pathlib import Path
import re
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]
RESOURCE = '1583577938561594'
HISTORY = '1801415419050528'
CLASS = 'ReportSendExpressLcdpEmcReportSendInfoServicev23'
HISTORY_CLASS = CLASS + 'm9'
JAVA = r'''
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mail.MailHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.framework.support.base.dao.MapDao;
import com.sunwayworld.module.item.file.service.CoreFileService;
import java.lang.reflect.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public final class ReportSendMailFooterCheck {
    static final String FOOTER = "Footer <>&%\r\nsecond 50%\nthird\rfourth";
    static int checks;
    static void check(boolean value, String reason) {
        checks++;
        if (!value) throw new AssertionError(reason);
    }
    static <T> T adapter(Class<T> type, InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
            (proxy, method, args) -> {
                if (method.getDeclaringClass() == Object.class) {
                    if (method.getName().equals("toString")) return "fixture:" + type.getSimpleName();
                    if (method.getName().equals("hashCode")) return System.identityHashCode(proxy);
                    if (method.getName().equals("equals")) return proxy == args[0];
                }
                return handler.invoke(proxy, method, args);
            }));
    }
    static final class CapturingSender extends JavaMailSenderImpl {
        int created, sent;
        MimeMessage captured;
        public MimeMessage createMimeMessage() {
            created++;
            return super.createMimeMessage();
        }
        public void send(MimeMessage message) {
            sent++;
            captured = message;
        }
    }
    static String html(Part part) throws Exception {
        if (part.isMimeType("text/html")) return (String) part.getContent();
        if (part.isMimeType("multipart/*")) {
            Multipart content = (Multipart) part.getContent();
            String result = "";
            for (int i=0;i<content.getCount();i++) result += html(content.getBodyPart(i));
            return result;
        }
        return "";
    }
    public static void main(String[] args) throws Exception {
        boolean missing = args[0].equals("missing");
        CapturingSender sender = new CapturingSender();
        int[] writes = {0};
        EmcReportBean report = new EmcReportBean(); report.setId(71001L); report.setReportNo("RPT-TEST-001");
        GenericDao reportDao = adapter(GenericDao.class, (proxy, method, values) -> {
            if (method.getName().equals("selectListByOneColumnValues")) {
                check(Collections.singletonList("RPT-TEST-001").equals(values[0]), "report query keeps report numbers");
                check("reportno".equals(values[1]), "report query keeps original column");
                return Collections.singletonList(report);
            }
            throw new AssertionError("unexpected report DAO operation");
        });
        EmcReportService reportService = adapter(EmcReportService.class, (proxy, method, values) -> {
            if (method.getName().equals("getDao")) return reportDao;
            throw new AssertionError("unexpected report service operation");
        });
        CoreFileService fileService = adapter(CoreFileService.class, (proxy, method, values) -> {
            if (method.getName().equals("selectListByFilter")) return Collections.emptyList();
            throw new AssertionError("unexpected file service operation");
        });
        MapDao mapDao = adapter(MapDao.class, (proxy, method, values) -> {
            if (method.getName().equals("update")) {
                check(sender.sent == 1, "status update follows successful mail submission");
                check("T_EMC_REPORT_SEND_INFO".equals(values[0]), "status update keeps original table");
                List<Map<String,Object>> rows = (List<Map<String,Object>>) values[1];
                check(rows.size() == 1, "one selected report status updated");
                Map<String,Object> row = rows.get(0);
                check("recipient@example.invalid".equals(row.get("sendemail")), "sent recipient recorded");
                check("recipient@example.invalid".equals(row.get("expressno")), "delivery identifier recorded");
                check("邮箱".equals(row.get("expresstype")), "original delivery type preserved");
                check(row.get("issurerdate") != null, "status update retains completion time");
                writes[0]++;
                return null;
            }
            throw new AssertionError("unexpected status DAO operation");
        });
        GenericApplicationContext context = new GenericApplicationContext();
        context.getEnvironment().getPropertySources().remove("systemProperties");
        context.getEnvironment().getPropertySources().remove("systemEnvironment");
        Map<String,Object> config = new HashMap<>();
        config.put("spring.mail.username", "sender@example.invalid");
        if (!missing) config.put("owned.report-send.mail.footer-text", FOOTER);
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("fixture", config));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.getBeanFactory().registerSingleton("reportService", reportService);
        context.getBeanFactory().registerSingleton("coreFileService", fileService);
        context.getBeanFactory().registerSingleton("mapDao", mapDao);
        context.refresh();
        ApplicationContextHelper.setApplicationContext(context);
        new MailHelper().setJavaMailSender(sender);
        try {
            ReportSendExpressLcdpEmcReportSendInfoServicev23 service =
                context.getAutowireCapableBeanFactory().createBean(ReportSendExpressLcdpEmcReportSendInfoServicev23.class);
            RestJsonWrapperBean wrapper = new RestJsonWrapperBean(
                Collections.singletonMap("email", "recipient@example.invalid"),
                Collections.singletonList(Collections.singletonList("{\"id\":71002,\"reportno\":\"RPT-TEST-001\",\"entrustorgname\":\"Fixture client\",\"businessusername\":\"Fixture contact\",\"mobile\":\"fixture-phone\"}")),
                Collections.singletonList(null));
            if (missing) {
                boolean rejected = false;
                try { service.sendEmail(wrapper); }
                catch (IllegalStateException expected) {
                    rejected = expected.getMessage() != null && expected.getMessage().contains("owned.report-send.mail.footer-text");
                }
                check(rejected, "missing required footer fails explicitly");
                check(sender.created == 0 && sender.sent == 0, "missing footer fails before MIME creation and SMTP submission");
                check(writes[0] == 0, "missing footer leaves report status untouched");
            } else {
                check("1".equals(service.sendEmail(wrapper)), "original success result retained");
                check(sender.created == 1 && sender.sent == 1, "actual mail helper submits one MIME message");
                check(writes[0] == 1, "one post-send status update");
                sender.captured.saveChanges();
                check("recipient@example.invalid".equals(sender.captured.getAllRecipients()[0].toString()), "actual MIME recipient");
                check("sender@example.invalid".equals(sender.captured.getFrom()[0].toString()), "actual MIME sender");
                check("Fixture client您好！RPT-TEST-001检测报告已完成，敬请查收".equals(sender.captured.getSubject()), "original subject placeholders retained");
                String body = html(sender.captured);
                check(body.contains("RPT-TEST-001"), "original report placeholder rendered");
                check(body.contains("Fixture contact") && body.contains("fixture-phone"), "original contact placeholder rendered");
                check(body.contains("Footer &lt;&gt;&amp;%<br>second 50%<br>third<br>fourth"), "footer escapes markup, preserves percent and normalizes newlines");
                check(!body.contains("Footer <>&%"), "footer cannot inject raw HTML");
            }
        } finally {
            ApplicationContextHelper.setApplicationContext(null);
            context.close();
        }
        System.out.println("courier mail footer " + args[0] + ": PASS (" + checks + " checks)");
    }
}
'''


def delivered_classes(package):
    """Decode the finite bootstrap literal grammar, without executing SQL."""
    sql = (package / '70-bootstrap.sql').read_text(encoding='utf-8')
    token = re.compile(r"\s*(?:('(?:[^']|'')*')|(TO_CLOB|EMPTY_CLOB|CHR|NULL)\b|(-?\d+)|(\|\||[,();]))", re.I)

    def value_at(position):
        match = token.match(sql, position)
        if not match:
            raise ValueError('unsupported delivered bootstrap expression')
        quoted, word, number, symbol = match.groups()
        position = match.end()
        if quoted is not None:
            result = quoted[1:-1].replace("''", "'")
        elif number is not None:
            result = number
        elif word and word.upper() == 'NULL':
            result = None
        elif word and word.upper() in ('TO_CLOB', 'CHR', 'EMPTY_CLOB'):
            opening = token.match(sql, position)
            if not opening or opening[4] != '(':
                raise ValueError('unsupported delivered bootstrap function')
            if word.upper() == 'EMPTY_CLOB':
                result, position = '', opening.end()
            else:
                result, position = expression_at(opening.end())
                if word.upper() == 'CHR':
                    result = chr(int(result))
            closing = token.match(sql, position)
            if not closing or closing[4] != ')':
                raise ValueError('unsupported delivered bootstrap function')
            position = closing.end()
        else:
            raise ValueError('unsupported delivered bootstrap value')
        return result, position

    def expression_at(position):
        result, position = value_at(position)
        while True:
            match = token.match(sql, position)
            if not match or match[4] != '||':
                return result, position
            right, position = value_at(match.end())
            result = (result or '') + (right or '')

    result = []
    for table, identity in [('T_LCDP_RESOURCE', RESOURCE), ('T_LCDP_RESOURCE_HISTORY', HISTORY)]:
        header = re.compile(r'^INSERT INTO ' + table + r'\s*\(([^)]*)\)\s+VALUES\s*\(', re.M)
        for match in header.finditer(sql):
            fields = [f.strip().strip('"') for f in match[1].split(',')]
            selected, position = expression_at(match.end())
            if selected != identity:
                continue
            values = [selected]
            while True:
                separator = token.match(sql, position)
                if not separator:
                    raise ValueError('unterminated delivered bootstrap row')
                if separator[4] == ')':
                    break
                if separator[4] != ',':
                    raise ValueError('unsupported delivered bootstrap row')
                item, position = expression_at(separator.end())
                values.append(item)
            if len(fields) != len(values):
                raise ValueError('delivered Java row field count differs')
            row = dict(zip(fields, values))
            source = row.get('CLASSCONTENT', '')
            if ('owned.report-send.mail.footer-text' not in source
                    or not re.search(r'public\s+class\s+' + (CLASS if table == 'T_LCDP_RESOURCE' else HISTORY_CLASS) + r'\b', source)):
                raise ValueError('delivered courier Java is absent or not transformed')
            result.append((table, CLASS if table == 'T_LCDP_RESOURCE' else HISTORY_CLASS, source))
        if len(result) != (1 if table == 'T_LCDP_RESOURCE' else 2):
            raise ValueError('delivered courier published Java row missing or duplicated')
    return result


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--prerequisite-root', type=Path, default=ROOT)
    parser.add_argument('--package', type=Path, default=ROOT / 'deployment/oracle/core-schema')
    parser.add_argument('--case', choices=['missing', 'configured', 'all'], default='all')
    args = parser.parse_args()
    try:
        classes = delivered_classes(args.package)
    except ValueError as error:
        parser.exit(1, str(error) + '\n')
    prerequisite = args.prerequisite_root.resolve()
    jdk = prerequisite / 'build/jdk1.8.0_391/bin'
    public = prerequisite / 'build/reconstituted-public-deps'
    application = prerequisite / 'build/source-only-application/classes'
    framework = sorted((prerequisite / 'reconstituted/framework').glob('*/target/classes'))
    dependencies = sorted(public.glob('*.jar'))
    if not all(p.is_file() for p in [jdk / 'java', jdk / 'javac', public / 'spring-context-5.2.9.RELEASE.jar']):
        parser.error('caller-selected Java 8 and public dependency prerequisites are required')
    if len(framework) != 7 or not (application / 'com/sunwayworld/emc/module/bp/report/report/service/EmcReportService.class').is_file():
        parser.error('caller-selected source-only application and seven framework outputs are required')
    if any(p.name.startswith('gikam-') or p.name.endswith('-run.jar') for p in dependencies):
        parser.error('supplier runtime is forbidden in public dependencies')
    actual_mail = ROOT / 'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/mail/MailHelper.java'
    actual_context = ROOT / 'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/context/ApplicationContextHelper.java'
    with tempfile.TemporaryDirectory(prefix='courier-mail-footer-') as directory:
        temporary = Path(directory)
        logging = temporary / 'log4j2.xml'
        logging.write_text('<Configuration status="OFF"><Loggers><Root level="OFF"/></Loggers></Configuration>')
        test = temporary / 'ReportSendMailFooterCheck.java'
        test.write_text(JAVA, encoding='utf-8')
        classpath = ':'.join(map(str, [temporary, application, *framework, *dependencies]))
        for table, class_name, source in classes:
            dynamic = temporary / (class_name + '.java')
            dynamic.write_text(source, encoding='utf-8')
            test.write_text(JAVA.replace(CLASS, class_name), encoding='utf-8')
            result = subprocess.run([str(jdk / 'javac'), '-encoding', 'UTF-8', '-proc:none',
                '-sourcepath', str(temporary), '-cp', classpath, '-d', str(temporary),
                str(actual_mail), str(actual_context), str(dynamic), str(test)], capture_output=True, text=True)
            if result.returncode:
                # Compiler source excerpts must never expose unreviewed literals.
                parser.exit(1, 'delivered courier Java/MIME fixture compilation failed (' + table + ')\n')
            cases = ['missing', 'configured'] if args.case == 'all' else [args.case]
            for case in cases:
                completed = subprocess.run([str(jdk / 'java'), '-Dlog4j.configurationFile=' + str(logging),
                    '-cp', classpath, 'ReportSendMailFooterCheck', case], capture_output=True, text=True)
                if completed.returncode:
                    parser.exit(1, 'courier mail fixture failed (' + table + ', ' + case + ')\n' + completed.stderr)
                print(table + ': ' + completed.stdout.strip())
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
