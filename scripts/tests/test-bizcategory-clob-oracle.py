#!/usr/bin/env python3
"""Emit SELECT-only Oracle fixtures from the real public MyBatis statement.

This does not connect to a database. Execute the emitted file with SQLPlus on
an authorized Oracle session. H2 cannot validate XMLQUERY/CLOB aggregation.
"""
import argparse
import base64
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]
JAVA = r'''
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.Configuration;
import com.sunwayworld.framework.database.sql.SqlHelper;
public class BizCategoryClobFixture {
  static Configuration config = new Configuration();
  static void parse(String file) throws Exception {
    try(InputStream in=new FileInputStream(file)) {
      new XMLMapperBuilder(in,config,file,config.getSqlFragments()).parse();
    }
  }
  static String literal(Object v) {
    if(v==null) return "NULL";
    if(v instanceof Number) return v.toString();
    return "'"+v.toString().replace("'","''")+"'";
  }
  static void emit(String name,Map<String,Object> p,boolean count,boolean page) {
    BoundSql bound=config.getMappedStatement("com.sunwayworld.emc.module.mdm.basemdm.bizcategory.mapper.EmcBizCategoryMapper.selectByCondition").getBoundSql(p);
    String sql=bound.getSql();
    for(ParameterMapping m:bound.getParameterMappings()) {
      Object value=bound.hasAdditionalParameter(m.getProperty()) ? bound.getAdditionalParameter(m.getProperty()) : config.newMetaObject(p).getValue(m.getProperty());
      int position=sql.indexOf('?');
      if(position<0) throw new AssertionError("Missing JDBC parameter");
      sql=sql.substring(0,position)+literal(value)+sql.substring(position+1);
    }
    if(count) sql=SqlHelper.getSelectTotalSql(sql,new String[0],new String[0]);
    if(page) sql=new com.sunwayworld.framework.database.dialect.OracleDialect().getLimitSql(sql,1,1);
    System.out.println(name+"\t"+Base64.getEncoder().encodeToString(sql.getBytes(StandardCharsets.UTF_8)));
  }
  public static void main(String[] args) throws Exception {
    org.springframework.context.support.StaticApplicationContext app=new org.springframework.context.support.StaticApplicationContext();
    app.getEnvironment().getSystemProperties().put("spring.datasource.driver-class-name","oracle.jdbc.OracleDriver");
    com.sunwayworld.framework.context.ApplicationContextHelper.setApplicationContext(app);
    com.sunwayworld.framework.database.dialect.DialectRepository.setDialect(new com.sunwayworld.framework.database.dialect.OracleDialect());
    parse(args[0]); parse(args[1]);
    Map<String,Object> p=new HashMap<>();
    emit("complete_names",p,false,false);
    emit("total",p,true,false);
    Map<String,Object> order=new HashMap<>(); order.put("first","ID"); order.put("second","DESC");
    p.put("orderParams",Arrays.asList(order)); emit("sorted_page",p,false,true);
    p.clear(); p.put("filter_by_item_list",Arrays.asList(Collections.singletonMap("ID",101)));
    emit("scalar_filter",p,false,false);
    p.put("filter_by_item_list",Arrays.asList(Collections.singletonMap("ID",999)));
    emit("no_match",p,false,false);
    p.clear(); p.put("queries_selectable","1");
    Map<String,Object> user=new HashMap<>(); user.put("id","alice"); user.put("userType","normal");
    p.put("loginUser",user); emit("user_scope",p,false,false); emit("user_total",p,true,false);
    user.put("id","absent"); emit("unassigned_user",p,false,false);
    user.put("userType","sysAdmin"); emit("admin_scope",p,false,false);
    p.clear(); p.put("isolateAbandon","0"); p.put("loginUser",user); p.put("loginOrgId","lab2");
    emit("org_scope",p,false,false);
  }
}
'''

FIXTURE = """WITH
T_EMC_BIZ_CATEGORY(ID, ORDERNO, SAMPLEORGID, REPORTEDITORGID, REPORTSENDORGID, CREATEDBYORGID) AS (
 SELECT 101, 1, 'lab1', 'lab1', 'lab2', 'lab1' FROM DUAL UNION ALL
 SELECT 102, 2, 'lab1', 'lab1', 'lab2', 'lab1' FROM DUAL UNION ALL
 SELECT 103, 3, 'lab1', 'lab1', 'lab2', 'lab1' FROM DUAL UNION ALL
 SELECT 104, 4, 'lab2', 'lab2', 'lab1', 'lab2' FROM DUAL UNION ALL
 SELECT 105, 5, 'lab2', 'lab2', 'lab1', 'lab2' FROM DUAL
),
T_CORE_ORG(ID, ORGNAME, ROOTORGID) AS (
 SELECT 'lab1', 'Sample & <lab>', 'root1' FROM DUAL UNION ALL
 SELECT 'lab2', 'Other lab', 'root2' FROM DUAL
),
LONG_NAMES(V) AS (
 SELECT TO_CLOB(RPAD('L',4000,'L')) || RPAD('L',4000,'L') || RPAD('L',2000,'L') FROM DUAL
),
SHORT_NAMES(V) AS (
 SELECT 'Z' FROM DUAL UNION ALL SELECT 'A&<lab>"' FROM DUAL UNION ALL
 SELECT 'é' FROM DUAL UNION ALL SELECT '中' FROM DUAL UNION ALL
 SELECT 'Z' FROM DUAL UNION ALL SELECT 'Leading-zero' FROM DUAL
),
T_EMC_ORG_PERMISSION(TARGETID, PERMISSIONTYPEID, ORGID, ORGNAME) AS (
 SELECT CASE WHEN V='Leading-zero' THEN '0101' ELSE '101' END,
        'T_EMC_BIZ_CATEGORY', 'lab1/a', TO_CLOB(V) FROM SHORT_NAMES UNION ALL
 SELECT '102', 'T_EMC_BIZ_CATEGORY', 'lab1/a', V || 'B' FROM LONG_NAMES UNION ALL
 SELECT '102', 'T_EMC_BIZ_CATEGORY', 'lab1/a', V || 'A' FROM LONG_NAMES UNION ALL
 SELECT '102', 'T_EMC_BIZ_CATEGORY', 'lab1/a', V || 'A' FROM LONG_NAMES UNION ALL
 SELECT '103', 'T_EMC_BIZ_CATEGORY', 'lab1/a', TO_CLOB(NULL) FROM DUAL UNION ALL
 SELECT '101', 'UNRELATED', 'lab2/a', TO_CLOB('excluded') FROM DUAL UNION ALL
 SELECT '105', 'UNRELATED', 'lab2/a', TO_CLOB('excluded') FROM DUAL
),
T_CORE_USER_PERMISSION(USERID, PERMISSIONTYPEID, TARGETID) AS (
 SELECT 'alice', 'T_CORE_ORG', 'lab1' FROM DUAL
),
ACTUAL_ROWS AS (
{sql}
)
"""

CHECKS = {
    'complete_names': """SELECT 'complete_names: PASS' AS RESULT,
 1 / CASE WHEN COUNT(*)=5
 AND SUM(CASE WHEN ID=101 AND DBMS_LOB.COMPARE(ORGNAMES,
   TO_CLOB((SELECT LISTAGG(DISTINCT V, ',') WITHIN GROUP (ORDER BY V) FROM SHORT_NAMES)))=0
   AND SAMPLEORGNAME='Sample & <lab>' AND REPORTEDITORGNAME='Sample & <lab>' AND REPORTSENDORGNAME='Other lab' THEN 1 ELSE 0 END)=1
 AND SUM(CASE WHEN ID=102 AND DBMS_LOB.GETLENGTH(ORGNAMES)=20003
   AND DBMS_LOB.COMPARE(ORGNAMES,(SELECT V || 'A,' || V || 'B' FROM LONG_NAMES))=0 THEN 1 ELSE 0 END)=1
 AND SUM(CASE WHEN ID IN (103,104,105) AND ORGNAMES IS NULL THEN 1 ELSE 0 END)=3
 THEN 1 ELSE 0 END AS ASSERTION FROM ACTUAL_ROWS;""",
    'total': "SELECT 'total: PASS' AS RESULT, 1 / CASE WHEN MAX(QTY_)=5 THEN 1 ELSE 0 END AS ASSERTION FROM ACTUAL_ROWS;",
    'sorted_page': "SELECT 'sorted_page: PASS' AS RESULT, 1 / CASE WHEN COUNT(*)=1 AND MAX(ID)=104 THEN 1 ELSE 0 END AS ASSERTION FROM ACTUAL_ROWS;",
    'scalar_filter': "SELECT 'scalar_filter: PASS' AS RESULT, 1 / CASE WHEN COUNT(*)=1 AND MAX(ID)=101 THEN 1 ELSE 0 END AS ASSERTION FROM ACTUAL_ROWS;",
    'no_match': "SELECT 'no_match: PASS' AS RESULT, 1 / CASE WHEN COUNT(*)=0 THEN 1 ELSE 0 END AS ASSERTION FROM ACTUAL_ROWS;",
    'user_scope': "SELECT 'user_scope: PASS' AS RESULT, 1 / CASE WHEN COUNT(*)=3 AND MIN(ID)=101 AND MAX(ID)=103 THEN 1 ELSE 0 END AS ASSERTION FROM ACTUAL_ROWS;",
    'user_total': "SELECT 'user_total: PASS' AS RESULT, 1 / CASE WHEN MAX(QTY_)=3 THEN 1 ELSE 0 END AS ASSERTION FROM ACTUAL_ROWS;",
    'unassigned_user': "SELECT 'unassigned_user: PASS' AS RESULT, 1 / CASE WHEN COUNT(*)=0 THEN 1 ELSE 0 END AS ASSERTION FROM ACTUAL_ROWS;",
    'admin_scope': "SELECT 'admin_scope: PASS' AS RESULT, 1 / CASE WHEN COUNT(*)=5 THEN 1 ELSE 0 END AS ASSERTION FROM ACTUAL_ROWS;",
    'org_scope': "SELECT 'org_scope: PASS' AS RESULT, 1 / CASE WHEN COUNT(*)=2 AND MIN(ID)=104 AND MAX(ID)=105 THEN 1 ELSE 0 END AS ASSERTION FROM ACTUAL_ROWS;",
}

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--jdk', type=Path, required=True)
    parser.add_argument('--public-deps', type=Path, required=True)
    parser.add_argument('--framework-classes', type=Path, required=True)
    parser.add_argument('--emit-oracle-fixture', type=Path, required=True)
    args = parser.parse_args()
    for path in [args.jdk/'bin/javac', args.public_deps, args.framework_classes]:
        if not path.exists(): parser.error('missing explicit prerequisite: '+str(path))
    with tempfile.TemporaryDirectory(prefix='bizcategory-clob-') as tmp:
        folder = Path(tmp)
        java = folder/'BizCategoryClobFixture.java'
        java.write_text(JAVA)
        cp = ':'.join(map(str, [folder,args.framework_classes,args.public_deps/'*']))
        subprocess.run([str(args.jdk/'bin/javac'),'-encoding','UTF-8','-cp',cp,'-d',str(folder),str(java)],check=True)
        emitted = subprocess.run([str(args.jdk/'bin/java'),'-cp',cp,'BizCategoryClobFixture',
            str(ROOT/'reconstituted/framework/owned-core-common/src/main/resources/com/sunwayworld/framework/mybatis/mapper/conf/global-oracle-mapper.xml'),
            str(ROOT/'project/src/main/java/com/sunwayworld/emc/module/mdm/basemdm/bizcategory/mapper/conf/emc-biz-category-oracle-mapper.xml')],check=True,text=True,capture_output=True)
    output = ['-- Generated from actual MyBatis BoundSql. SELECT-only: no business/fixture writes.',
              'WHENEVER SQLERROR EXIT SQL.SQLCODE', 'SET DEFINE OFF', 'SET SQLBLANKLINES ON',
              'SELECT PARAMETER, VALUE FROM NLS_SESSION_PARAMETERS WHERE PARAMETER IN (\'NLS_COMP\',\'NLS_SORT\');']
    seen = []
    for line in emitted.stdout.splitlines():
        if '\t' not in line: continue
        name, encoded = line.split('\t',1)
        if name not in CHECKS: raise ValueError('Unexpected fixture: '+name)
        sql = base64.b64decode(encoded).decode('utf-8')
        output.append(FIXTURE.format(sql=sql)+CHECKS[name])
        seen.append(name)
    if set(seen)!=set(CHECKS): raise ValueError('Missing generated scenarios')
    args.emit_oracle_fixture.write_text('\n\n'.join(output)+'\n',encoding='utf-8')
    print('Emitted '+str(len(seen))+' Oracle cases; database execution pending: '+str(args.emit_oracle_fixture))

if __name__=='__main__': main()
