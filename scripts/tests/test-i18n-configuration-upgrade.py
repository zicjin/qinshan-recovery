#!/usr/bin/env python3
"""Run the exact language upgrade in a temporary user of an existing test Oracle."""
import argparse
import json
from pathlib import Path
import re
import secrets
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]
UPGRADE = ROOT / 'deployment/oracle/upgrades/20261111-complete-i18n.sql'


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--oracle-container', required=True)
    parser.add_argument('--service', default='FREEPDB1')
    args = parser.parse_args()
    if not re.fullmatch(r'[A-Za-z0-9_-]+', args.oracle_container):
        parser.error('invalid test Oracle container')
    if not re.fullmatch(r'[A-Za-z0-9_]+', args.service):
        parser.error('invalid test service')
    source = json.loads((ROOT / 'deployment/oracle/core-schema/i18n-runtime-source.json').read_text())
    entries = {x['code']: x for x in source['entries']}
    assert entries['GIKAM.FILE.BUTTON.UPLOAD_FILE']['messages']['zh-CN'] == '上传附件'
    assert entries['GIKAM.WORKFLOW.BUTTON.SUBMIT']['messages']['zh-CN'] == '提交'
    schema = 'LIMS_I18N_' + secrets.token_hex(5).upper()
    password = 'T' + secrets.token_hex(16)
    command = ['docker', 'exec', '-i', '-e', 'NLS_LANG=.AL32UTF8', args.oracle_container,
               'sh', '-c', 'exec "$ORACLE_HOME/bin/sqlplus" -s / as sysdba']
    created = False
    with tempfile.TemporaryDirectory(prefix='i18n-upgrade-') as tmp:
        private = Path(tmp)

        def execute(label, sql, target=True, failure=None):
            prefix = 'SET ECHO OFF VERIFY OFF\nWHENEVER SQLERROR EXIT FAILURE ROLLBACK\n'
            if target:
                prefix += 'CONNECT ' + schema + '/"' + password + '"@//127.0.0.1:1521/' + args.service + '\n'
            else:
                prefix += 'ALTER SESSION SET CONTAINER=' + args.service + ';\n'
            result = subprocess.run(command, input=prefix + sql + '\nEXIT\n', text=True,
                                    capture_output=True, timeout=120)
            text = result.stdout + result.stderr
            (private / (label + '.log')).write_text(text)
            errors = re.findall(r'(?:ORA|SP2)-\d+', text)
            if failure:
                assert failure in errors, label + ': expected controlled error, got ' + str(errors)
            else:
                assert result.returncode == 0 and not errors, label + ': Oracle errors ' + str(errors)
            return text

        try:
            execute('create', 'CREATE USER ' + schema + ' IDENTIFIED BY "' + password + '" '
                    'DEFAULT TABLESPACE USERS QUOTA 32M ON USERS;\nGRANT CREATE SESSION, CREATE TABLE TO ' + schema + ';', False)
            created = True
            ddl = (ROOT / 'deployment/oracle/core-schema/20-tables.sql').read_text()
            tables = ('T_CORE_I18N_CONFIG', 'T_CORE_I18N', 'T_CORE_I18N_MESSAGE')
            fixture = '\n'.join(ddl.split('-- BEGIN OBJECT TABLE ' + name + '\n', 1)[1].split(
                '-- END OBJECT TABLE ' + name, 1)[0] for name in tables)
            fixture += """
INSERT INTO T_CORE_I18N_CONFIG(ID,LOCALENAME) VALUES('zh-CN','中文');
INSERT INTO T_CORE_I18N_CONFIG(ID,LOCALENAME) VALUES('en-US','English');
INSERT INTO T_CORE_I18N(ID,CODE,DESCRIPTION,DEFAULTMESSAGE)
 VALUES(1,'GIKAM.FILE.BUTTON.UPLOAD_FILE','目标描述','目标默认');
INSERT INTO T_CORE_I18N_MESSAGE(ID,I18NID,I18NCONFIGID,MESSAGE)
 VALUES(1,1,'zh-CN','目标定制上传');
INSERT INTO T_CORE_I18N(ID,CODE) VALUES(2,'GIKAM.WORKFLOW.BUTTON.SUBMIT');
INSERT INTO T_CORE_I18N_MESSAGE(ID,I18NID,I18NCONFIGID,MESSAGE) VALUES(2,2,'zh-CN',NULL);
INSERT INTO T_CORE_I18N_MESSAGE(ID,I18NID,I18NCONFIGID,MESSAGE)
 VALUES(999,9000,'zh-CN','孤立消息必须保持孤立');
COMMIT;
"""
            execute('fixture', fixture)
            # The actual source-pinned SQL, not an imitation in Python.
            execute('apply', UPGRADE.read_text())
            execute('assertions', """
DECLARE n NUMBER; v VARCHAR2(100);
BEGIN
 SELECT DBMS_LOB.SUBSTR(MESSAGE,100,1) INTO v FROM T_CORE_I18N_MESSAGE WHERE ID=1;
 IF v <> '目标定制上传' THEN RAISE_APPLICATION_ERROR(-20901,'Custom message overwritten'); END IF;
 SELECT DBMS_LOB.SUBSTR(DESCRIPTION,100,1) INTO v FROM T_CORE_I18N WHERE ID=1;
 IF v <> '目标描述' THEN RAISE_APPLICATION_ERROR(-20901,'Definition overwritten'); END IF;
 SELECT DBMS_LOB.SUBSTR(MESSAGE,100,1) INTO v FROM T_CORE_I18N_MESSAGE WHERE ID=2;
 IF v <> '提交' THEN RAISE_APPLICATION_ERROR(-20901,'Empty message not filled'); END IF;
 SELECT COUNT(*) INTO n FROM T_CORE_I18N WHERE ID=9000;
 IF n <> 0 THEN RAISE_APPLICATION_ERROR(-20901,'Old orphan adopted'); END IF;
 SELECT COUNT(*) INTO n FROM T_CORE_I18N WHERE ID>2 AND ID<=9000;
 IF n <> 0 THEN RAISE_APPLICATION_ERROR(-20901,'Unsafe identity allocation'); END IF;
END;
/
""")
            repeated = execute('repeat', UPGRADE.read_text())
            changes = re.findall(r'I18N delta definitions=(\d+) messages=(\d+) filled=(\d+)', repeated)
            assert changes and all(x == ('0', '0', '0') for x in changes), 'Repeat changed populated data'
            # Ambiguity late in the script must roll back earlier blocks too.
            first, last = source['entries'][0]['code'], source['entries'][-1]['code']
            quoted = lambda value: "'" + value.replace("'", "''") + "'"
            execute('ambiguous-fixture', 'DELETE FROM T_CORE_I18N_MESSAGE WHERE I18NID=(SELECT ID FROM T_CORE_I18N WHERE CODE=' + quoted(first) + ');\n'
                    'DELETE FROM T_CORE_I18N WHERE CODE=' + quoted(first) + ';\n'
                    'INSERT INTO T_CORE_I18N(ID,CODE) SELECT MAX(ID)+1,' + quoted(last) + ' FROM T_CORE_I18N;\nCOMMIT;')
            execute('refuse-ambiguous', UPGRADE.read_text(), failure='ORA-20410')
            execute('rollback-check', 'DECLARE n NUMBER; BEGIN SELECT COUNT(*) INTO n FROM T_CORE_I18N WHERE CODE=' + quoted(first) + "; IF n<>0 THEN RAISE_APPLICATION_ERROR(-20901,'Earlier blocks were committed'); END IF; END;\n/")
            print('i18n upgrade: preserved overrides/orphans, filled missing configuration, repeat no-op, ambiguity rollback PASS')
        finally:
            if created:
                execute('drop', 'DROP USER ' + schema + ' CASCADE;', False)


if __name__ == '__main__':
    main()
