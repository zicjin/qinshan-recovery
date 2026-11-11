#!/usr/bin/env python3
"""Exercise the real report access filter with in-memory Servlet/Fine API fixtures.

This is an isolated access-boundary regression, not report rendering or HTTP
evidence. It opens no sockets and uses no database, Redis or licensed engine.
"""
import argparse
import os
import re
from pathlib import Path
import subprocess
import tempfile
import shutil
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[2]


def assert_optional_user_number_contract():
    # The filter's narrow optional-USERNO policy is justified only by these
    # original templates. An effective userid predicate needs a new review.
    for name in ('commission-summary.cpt', 'contract-summary.cpt'):
        root = ET.parse(ROOT / 'deployment/report-engine/assets' / name).getroot()
        queries = root.findall('.//Query')
        if not queries:
            raise AssertionError('summary template has no SQL contract: ' + name)
        for query in queries:
            sql = ''.join(query.itertext())
            sql = re.sub(r'/\*.*?\*/', '', sql, flags=re.DOTALL)
            sql = re.sub(r'--[^\r\n]*', '', sql)
            if re.search(r'\buserid\b', sql, flags=re.IGNORECASE):
                raise AssertionError('optional USERNO summary gained a userid SQL dependency: ' + name)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--jdk', type=Path, default=ROOT / 'build/jdk1.8.0_391')
    parser.add_argument('--distribution', type=Path,
                        default=ROOT / 'owned-distribution/target/distribution')
    args = parser.parse_args()
    assert_optional_user_number_contract()
    app = args.distribution / 'app/lims-owned-application.jar'
    public = args.distribution / 'lib'
    source = ROOT / 'owned-distribution/src/main/java/com/sunwayworld/owned/report/engine/ReportEngineAccessFilter.java'
    fixtures = sorted((ROOT / 'scripts/tests/fixtures/report-engine-access').rglob('*.java'))
    for path in [args.jdk / 'bin/javac', args.jdk / 'bin/java', app, public, source, *fixtures]:
        if not path.exists():
            parser.error('missing source-only test prerequisite: ' + str(path))
    if len(fixtures) != 5:
        parser.error('report access fixture source set is incomplete')

    private = ROOT / 'private'
    private.mkdir(exist_ok=True)
    with tempfile.TemporaryDirectory(prefix='report-engine-access-', dir=private) as temporary:
        temp = Path(temporary)
        registry = temp / 'com/sunwayworld/owned/report/engine/home-report-templates.json'
        registry.parent.mkdir(parents=True)
        shutil.copyfile(ROOT / 'owned-distribution/src/main/resources/com/sunwayworld/owned/report/engine/home-report-templates.json', registry)
        (temp / 'log4j2-test.xml').write_text(
            '<Configuration status="OFF"><Appenders/><Loggers><Root level="off"/></Loggers></Configuration>\n',
            encoding='utf-8')
        cp = os.pathsep.join(map(str, [temp, app, public / '*']))
        subprocess.run([str(args.jdk / 'bin/javac'), '-proc:none', '-encoding', 'UTF-8',
                        '-source', '8', '-target', '8', '-cp', cp, '-d', str(temp),
                        str(source), *map(str, fixtures)], check=True)
        subprocess.run([str(args.jdk / 'bin/java'),
                        '-Dlog4j.configurationFile=' + str(temp / 'log4j2-test.xml'),
                        '-cp', cp,
                        'com.sunwayworld.owned.report.engine.ReportEngineAccessCheck'],
                       check=True)
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
