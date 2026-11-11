#!/usr/bin/env python3
"""Exercise the stored custom datasource execution seam without a database."""
import hashlib
import importlib.util
import json
import os
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]
HELPER = ROOT / 'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/framework/database/sql/ExternalLogicalSql.java'
SERVICE = ROOT / 'reconstituted/framework/owned-core-base-service/src/main/java/com/sunwayworld/cloud/module/item/customds/service/impl/CoreCustomDsServiceImpl.java'
JAVA = r'''
import com.sunwayworld.framework.database.sql.ExternalLogicalSql;
public class DynamicExternalSqlCheck {
    static void equal(String expected,String actual) {
        if(!expected.equals(actual)) throw new AssertionError("SQL projection changed unexpected bytes");
    }
    static void reject(String sql) {
        try { ExternalLogicalSql.render(sql); throw new AssertionError("unsafe qualifier accepted"); }
        catch(IllegalArgumentException expected) { }
    }
    static void service(String prefix,String qualifier) throws Exception {
        com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsBean bean=new com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsBean();
        bean.setDsSql("SELECT SUM(A.CONTRACTAMOUNT) FROM T_CRM_CONTRACT@LIMS_CZJKY_HT A");
        com.sunwayworld.cloud.module.item.customds.persistent.dao.CoreCustomDsDao dao=(com.sunwayworld.cloud.module.item.customds.persistent.dao.CoreCustomDsDao)
            java.lang.reflect.Proxy.newProxyInstance(DynamicExternalSqlCheck.class.getClassLoader(),new Class[]{com.sunwayworld.cloud.module.item.customds.persistent.dao.CoreCustomDsDao.class},
                (proxy,method,arguments) -> { if(method.getName().equals("selectById")) return bean; throw new AssertionError("unexpected DAO call"); });
        com.sunwayworld.cloud.module.item.customds.service.impl.CoreCustomDsServiceImpl service=new com.sunwayworld.cloud.module.item.customds.service.impl.CoreCustomDsServiceImpl(){
            @Override public com.sunwayworld.cloud.module.item.customds.persistent.dao.CoreCustomDsDao getDao(){return dao;}
        };
        Object mapper=java.lang.reflect.Proxy.newProxyInstance(DynamicExternalSqlCheck.class.getClassLoader(),new Class[]{com.sunwayworld.framework.mybatis.mapper.GlobalMapper.class},
            (proxy,method,arguments) -> {
                if(!method.getName().equals("selectListBySqlListUnionAllAppendWhere")) throw new AssertionError("unexpected SQL execution");
                java.util.Map parameter=(java.util.Map)arguments[0];
                equal("SELECT SUM(A.CONTRACTAMOUNT) FROM "+prefix+"T_CRM_CONTRACT"+qualifier+" A",(String)((java.util.List)parameter.get("sqlList")).get(0));
                return java.util.Collections.singletonList(java.util.Collections.singletonMap("total",17));
            });
        java.lang.reflect.Field field=service.getClass().getSuperclass().getDeclaredField("globalMapper");field.setAccessible(true);field.set(service,mapper);
        java.util.List<java.util.Map<String,Object>> rows=service.getCustomDsDataById(1L,new com.sunwayworld.framework.restful.data.RestJsonWrapperBean());
        if(rows.size()!=1 || !Integer.valueOf(17).equals(rows.get(0).get("TOTAL"))) throw new AssertionError("customDS lost real mapper output");
    }
    public static void main(String[] args) {
        if(args[0].equals("original-db-link")) {
            String original="SELECT '@UNCHANGED' FROM T_CRM_CONTRACT@LIMS_CZJKY_HT A -- original transport\n";
            equal(original,ExternalLogicalSql.render(original));
            String other="SELECT * FROM OTHER.T_UNKNOWN@OTHER_LINK.DOMAIN";
            equal(other,ExternalLogicalSql.render(other));
            try { service("","@LIMS_CZJKY_HT"); } catch(Exception failure) { throw new AssertionError(failure); }
            System.out.println("dynamic external SQL: PASS "+args[0]);
            return;
        }
        String prefix=args[0].equals("direct-schema")?"LIMS_EXT_HT.":"";
        equal("SELECT SUM(A.CONTRACTAMOUNT) FROM "+prefix+"T_CRM_CONTRACT A",
            ExternalLogicalSql.render("SELECT SUM(A.CONTRACTAMOUNT) FROM T_CRM_CONTRACT@LIMS_CZJKY_HT A"));
        equal("SELECT '@UNKNOWN', q'[T_UNKNOWN@UNKNOWN]' FROM "+prefix+"T_CRM_CONTRACT /* @UNKNOWN */  -- @OTHER\n A",
            ExternalLogicalSql.render("SELECT '@UNKNOWN', q'[T_UNKNOWN@UNKNOWN]' FROM T_CRM_CONTRACT /* @UNKNOWN */ @LIMS_CZJKY_HT -- @OTHER\n A"));
        equal("SELECT 'it''s @UNCHANGED' FROM DUAL -- @UNCHANGED\n",ExternalLogicalSql.render("SELECT 'it''s @UNCHANGED' FROM DUAL -- @UNCHANGED\n"));
        reject("SELECT * FROM T_UNKNOWN@LIMS_CZJKY_HT");
        reject("SELECT * FROM T_CRM_CONTRACT@UNKNOWN");
        reject("SELECT * FROM OTHER.T_CRM_CONTRACT@LIMS_CZJKY_HT");
        reject("SELECT * FROM T_CRM_CONTRACT@LIMS_CZJKY_HT.EXTRA");
        reject("SELECT * FROM \"T_CRM_CONTRACT\"@LIMS_CZJKY_HT");
        reject("SELECT * FROM T_CRM_CONTRACT@");
        try { service(prefix,""); } catch(Exception failure) { throw new AssertionError(failure); }
        System.out.println("dynamic external SQL: PASS "+args[0]);
    }
}
'''


def main():
    assert HELPER.is_file(), 'missing shared stored-SQL projection: current customDS still executes a production DB link'
    assert 'ExternalLogicalSql.render(sqlContent)' in SERVICE.read_text(), 'customDS execution does not use shared renderer'
    spec = importlib.util.spec_from_file_location('builder_dynamic_test', ROOT / 'scripts/build-source-only-application.py')
    builder = importlib.util.module_from_spec(spec)
    import sys
    sys.modules[spec.name] = builder
    spec.loader.exec_module(builder)
    jdk = ROOT / 'build/jdk1.8.0_391/bin'
    fastjson = ROOT / 'build/reconstituted-public-deps/fastjson-1.2.83.jar'
    with tempfile.TemporaryDirectory(prefix='dynamic-external-sql-') as directory:
        work = Path(directory)
        classes = work / 'classes'
        classes.mkdir()
        check = work / 'DynamicExternalSqlCheck.java'
        check.write_text(JAVA)
        logging = work / 'log4j2.xml'
        logging.write_text('<Configuration status="OFF"><Appenders/><Loggers><Root level="off"/></Loggers></Configuration>')
        java_options = ['-Dlog4j.configurationFile=' + str(logging)]
        dependencies = sorted((ROOT/'build/reconstituted-public-deps').glob('*.jar'))
        dependencies += sorted((ROOT/'reconstituted/framework').glob('*/target/classes'))
        compile_cp = os.pathsep.join(map(str,dependencies))
        subprocess.run([str(jdk/'javac'), '-proc:none', '-source','8','-target','8','-encoding','UTF-8',
                        '-cp',compile_cp,'-d',str(classes),str(HELPER),str(SERVICE),str(check)],check=True)
        for mode, profile_name in [('local-view','external-schema-runtime-profile-local-view.example.json'),
                                   ('direct-schema','external-schema-runtime-profile.example.json'),
                                   ('original-db-link',None)]:
            resources = work / mode
            for relative in builder.EXTERNAL_SCHEMA_MAPPERS:
                target = resources / relative
                target.parent.mkdir(parents=True,exist_ok=True)
                target.write_bytes((ROOT/'project/src/main/java'/relative).read_bytes())
            profile = ROOT / 'api-fixtures/schema' / profile_name if profile_name else None
            contract = builder.write_external_schema_build_contract(resources, profile)
            if profile_name:
                assert contract['dynamic_sql']['objects_by_qualifier']['LIMS_CZJKY_HT'] == [
                    'T_CRM_CONTRACT','T_CRM_INVOICE','T_CRM_INVOICE_PROJ','T_CRM_PAYMENT','T_CRM_PAY_PROJECT']
            else:
                assert contract['dynamic_sql'] is None
            cp = os.pathsep.join(map(str,[classes,resources,*dependencies]))
            subprocess.run([str(jdk/'java'),*java_options,'-cp',cp,'DynamicExternalSqlCheck',mode],check=True)
            metadata = resources / 'META-INF/lims-external-schema.json'
            metadata.write_bytes(metadata.read_bytes()+b' ')
            failed = subprocess.run([str(jdk/'java'),*java_options,'-cp',cp,'DynamicExternalSqlCheck',mode],capture_output=True,text=True)
            assert failed.returncode != 0 and 'contract hash' in failed.stderr
            if mode == 'original-db-link':
                bad_contract = {**contract,'dynamic_sql':{}}
                raw = json.dumps(bad_contract).encode()
                metadata.write_bytes(raw)
                (resources/'META-INF/lims-external-schema.properties').write_text(
                    'mode=original-db-link\ncontract_sha256='+hashlib.sha256(raw).hexdigest()+'\n')
                failed = subprocess.run([str(jdk/'java'),*java_options,'-cp',cp,'DynamicExternalSqlCheck',mode],capture_output=True,text=True)
                assert failed.returncode != 0 and 'Unapproved external SQL' in failed.stderr
    print('dynamic external SQL regression: PASS')


if __name__ == '__main__':
    main()
