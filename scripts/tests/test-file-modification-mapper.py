#!/usr/bin/env python3
"""Load the original file-change Mapper with real MyBatis and retained scopes."""
import argparse
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]
JAVA = r'''
import java.io.*;
import java.util.*;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;
public class FileModificationMapperCheck {
  static void load(Configuration config, String path) throws Exception {
    try(InputStream in=new FileInputStream(path)) {
      new XMLMapperBuilder(in,config,path,config.getSqlFragments()).parse();
    }
  }
  static String sql(Configuration c, Map<String,Object> p) {
    return c.getMappedStatement("com.sunwayworld.emc.module.mdm.filemodification.mapper.EmcFileModificationMapper.selectByCondition")
      .getBoundSql(p).getSql().replaceAll("\\s+"," ").trim();
  }
  public static void main(String[] args) throws Exception {
    Configuration c=new Configuration(); load(c,args[0]); load(c,args[1]);
    Map<String,Object> p=new HashMap<>();p.put("isolateAbandon","1");
    String plain=sql(c,p);
    if(!plain.contains("FROM T_DINGAN_FILE_MODIFY T"))throw new AssertionError("Original business table changed");
    p.put("isolateAbandon","0");p.put("loginUser",Collections.singletonMap("id", "fixture-user"));p.put("loginOrgId","fixture-org");
    p.put("authority_audit","1");p.put("service_table","T_DINGAN_FILE_MODIFY");p.put("bpmn_statusCode","fileAudit,fileConfirm");
    String scoped=sql(c,p);
    if(!scoped.contains("T.CREATEDBYORGID = O1.ID") || !scoped.contains("T_CORE_BPMN_INST_TASK_ROLE") || !scoped.contains("T_CORE_BPMN_INST_TASK_USER"))
      throw new AssertionError("Organization or audit scope disappeared");
    if(!scoped.contains("K.STATUSCODE IN") || !scoped.contains("ORDER BY T.ID"))throw new AssertionError("Workflow filter/default order lost");
    BoundSql bound=c.getMappedStatement("com.sunwayworld.emc.module.mdm.filemodification.mapper.EmcFileModificationMapper.selectByCondition").getBoundSql(p);
    if(bound.getParameterMappings().size()<5)throw new AssertionError("Scope parameters must remain bound");
    for(int index=2;index<args.length;index++) {
      String[] entry=args[index].split("\\|",-1);Configuration other=new Configuration();load(other,args[0]);load(other,entry[0]);
      String statement=entry[1]+".selectByCondition";
      String query=other.getMappedStatement(statement).getBoundSql(Collections.emptyMap()).getSql();
      if(!query.contains(entry[2]))throw new AssertionError("Original procurement/invoice table changed");
    }
    System.out.println("file modification mapper: PASS (actual namespace, original table, organization/audit predicates)");
  }
}
'''


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--jdk', type=Path, default=ROOT/'build/jdk1.8.0_391')
    parser.add_argument('--distribution', type=Path, default=ROOT/'owned-distribution/target/distribution')
    args = parser.parse_args()
    mapper = ROOT/'project/src/main/java/com/sunwayworld/emc/module/mdm/filemodification/mapper/conf'
    resources = ROOT/'reconstituted/framework/owned-core-common/src/main/resources/com/sunwayworld/framework/mybatis/mapper/conf'
    with tempfile.TemporaryDirectory(prefix='file-modification-mapper-') as temp:
        temp = Path(temp)
        source = temp/'FileModificationMapperCheck.java'
        source.write_text(JAVA)
        cp = ':'.join(map(str, [temp, args.distribution/'app/lims-owned-application.jar', args.distribution/'lib/*']))
        subprocess.run([str(args.jdk/'bin/javac'), '-encoding', 'UTF-8', '-cp', cp, '-d', str(temp), str(source)], check=True)
        extra = []
        base = ROOT/'project/src/main/java/com/sunwayworld/emc/module/mdm'
        for folder, filename, klass, table in [
            ('purchapply', 'emc-purch-apply', 'EmcPurchApplyMapper', 'T_DINGAN_PURCH'),
            ('purchapply', 'emc-purch-detail', 'EmcPurchDetailMapper', 'T_DINGAN_PURCH_DETAIL'),
            ('invoices', 'emc-invoice', 'EmcInvoiceMapper', 'T_EMC_INVOICE'),
            ('invoices', 'emc-returned', 'EmcReturnedMapper', 'T_EMC_RETURNED'),
        ]:
            extra.append('|'.join([str(base/folder/'mapper/conf'/(filename+'-oracle-mapper.xml')),
                                   'com.sunwayworld.emc.module.mdm.'+folder+'.mapper.'+klass, table]))
        subprocess.run([str(args.jdk/'bin/java'), '-cp', cp, 'FileModificationMapperCheck',
                        str(resources/'global-oracle-mapper.xml'), str(mapper/'emc-file-modification-oracle-mapper.xml'), *extra], check=True)
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
