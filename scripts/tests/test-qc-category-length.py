#!/usr/bin/env python3
"""QC public Resource/Service length boundary with an isolated database adapter."""
import argparse
from pathlib import Path
import subprocess
import tempfile
ROOT=Path(__file__).resolve().parents[2]
JAVA=r'''
import java.lang.reflect.*;import java.util.*;
import com.alibaba.fastjson.JSON;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.dao.EmcQcCategoryDao;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.resource.impl.EmcQcCategoryResourceImpl;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.service.impl.EmcQcCategoryServiceImpl;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.id.*;
import com.sunwayworld.framework.at.annotation.*;import com.sunwayworld.framework.audit.aunnotation.Audit;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.http.HttpStatus;import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.transaction.annotation.Transactional;
public class QcCategoryLengthCheck {
 static int assertions;static void need(boolean b,String m){assertions++;if(!b)throw new AssertionError(m);}
 interface Action {void run();}
 static void rejected(Action action){try{action.run();throw new AssertionError("Overlength QC code escaped controlled validation");}catch(ApplicationWarningException expected){need(expected.getClass().getAnnotation(ResponseStatus.class).value()==HttpStatus.BAD_REQUEST,"existing controlled400 exception");need(expected.getMessage().contains("16"),"specific sixteen-byte message");}}
 static void inject(Object target,Class<?> owner,String name,Object value)throws Exception{Field f=owner.getDeclaredField(name);f.setAccessible(true);f.set(target,value);}
 static EmcQcCategoryBean row(long id,String value){EmcQcCategoryBean b=new EmcQcCategoryBean();b.setId(id);b.setQcCategoryId(value);b.setQcName("QC length fixture");return b;}
 static RestJsonWrapperBean body(Object... rows){List<String> json=new ArrayList<>();for(Object row:rows)json.add(JSON.toJSONString(row));return new RestJsonWrapperBean(new HashMap<>(),Collections.singletonList(json),Collections.singletonList(null));}
 static final class Database implements InvocationHandler {
  Map<Long,EmcQcCategoryBean> rows=new LinkedHashMap<>();
  EmcQcCategoryDao dao=(EmcQcCategoryDao)Proxy.newProxyInstance(QcCategoryLengthCheck.class.getClassLoader(),new Class[]{EmcQcCategoryDao.class},this);
  public Object invoke(Object proxy,Method m,Object[] args){
   if(m.getName().equals("getType"))return EmcQcCategoryBean.class;
   if(m.getName().equals("insert")){EmcQcCategoryBean b=(EmcQcCategoryBean)args[0];rows.put(b.getId(),b);return null;}
   if(m.getName().equals("selectById"))return rows.get(args[0]);
   if(m.getName().equals("updateIfChanged")){
    List<EmcQcCategoryBean> batch=args[0] instanceof List?(List<EmcQcCategoryBean>)args[0]:Arrays.asList((EmcQcCategoryBean)args[0]);
    for(EmcQcCategoryBean b:batch){EmcQcCategoryBean old=rows.get(b.getId());if(old!=null&&b.getQcCategoryId()!=null)old.setQcCategoryId(b.getQcCategoryId());}
    return args[0] instanceof List?batch:(batch.isEmpty()?null:batch.get(0));
   }
   throw new AssertionError("Unexpected database boundary "+m.getName());
  }
 }
 public static void main(String[] args)throws Exception{
  StaticMessageSource messages=new StaticMessageSource();messages.setUseCodeAsDefaultMessage(true);messages.addMessage("EMC.QC_CATEGORY.CODE_MAX_BYTES",Locale.getDefault(),"QC code must contain at most 16 UTF-8 bytes");new I18nHelper().setMessageSource(messages);
  // Isolate ID generation from Redis; only this process-local snowflake worker is set.
  Field idWorker=GikamIdHelper.class.getDeclaredField("worker");idWorker.setAccessible(true);Constructor<?> workerConstructor=Class.forName("com.sunwayworld.framework.id.SnowflakeIdWorker").getDeclaredConstructor(long.class);workerConstructor.setAccessible(true);idWorker.set(null,workerConstructor.newInstance(0L));
  Database db=new Database();EmcQcCategoryServiceImpl service=new EmcQcCategoryServiceImpl();inject(service,service.getClass(),"qcCategoryDao",db.dao);
  EmcQcCategoryResourceImpl resource=new EmcQcCategoryResourceImpl();inject(resource,resource.getClass(),"qcCategoryService",service);
  rejected(()->resource.insert(body(row(0,"ABCDEFGHIJKLMNOPQ"))));need(db.rows.isEmpty(),"invalid insert created no entity");
  rejected(()->resource.insert(body(row(0,"中文中文中文"))));need(db.rows.isEmpty(),"18 UTF8 bytes rejected despite six characters");
  rejected(()->resource.insert(body(row(0,"😀😀😀😀A"))));need(db.rows.isEmpty(),"17 UTF8 bytes rejected with surrogate pairs");
  for(String value:new String[]{"ABCDEFGHIJKLMNOP","中文中文中A","😀😀😀😀",null,""}){
   Long id=resource.insert(body(row(0,value)));need(id!=null,"valid code returns ID");need(Objects.equals(service.selectById(id).getQcCategoryId(),value),"valid code readback unchanged");
  }
  db.rows.put(91L,row(91,"before-a"));db.rows.put(92L,row(92,"before-b"));
  rejected(()->resource.instantSave(body(row(91,"valid-change"),row(92,"ABCDEFGHIJKLMNOPQ"))));
  need(service.selectById(91L).getQcCategoryId().equals("before-a")&&service.selectById(92L).getQcCategoryId().equals("before-b"),"whole list validated before first entity update");
  rejected(()->resource.save(91L,body(row(91,"中文中文中文"))));need(service.selectById(91L).getQcCategoryId().equals("before-a"),"PUT save invalid code leaves original unchanged");
  rejected(()->service.updateIfChanged(row(91,"ABCDEFGHIJKLMNOPQ")));need(service.selectById(91L).getQcCategoryId().equals("before-a"),"direct bean update also protected");
  resource.instantSave(body(row(91,"ABCDEFGHIJKLMNOP")));need(service.selectById(91L).getQcCategoryId().equals("ABCDEFGHIJKLMNOP"),"valid inline sixteen byte update readback");
  for(Class<?> type:new Class[]{EmcQcCategoryBean.class,List.class}){
   Method m=service.getClass().getMethod("updateIfChanged",type);need(m.getAnnotation(Transactional.class)!=null,"override keeps transaction");need(m.getAnnotation(AuditTrailEntry.class).value()==AuditTrailType.UPDATE,"override keeps audit trail");need("AUDIT.SAVE".equals(m.getAnnotation(Audit.class).value()),"override keeps audit semantics");
  }
  idWorker.set(null,null);System.out.println("PASS actual QC Resource/Service controlled UTF8 length, readback and batch no-partial-write ("+assertions+" assertions)");
 }
}
'''
def main():
 parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('--dependency-root',type=Path,required=True);parser.add_argument('--temp-root',type=Path,required=True);args=parser.parse_args()
 dep=args.dependency_root.resolve();args.temp_root.mkdir(parents=True,exist_ok=True)
 with tempfile.TemporaryDirectory(prefix='qc-length-',dir=args.temp_root) as folder:
  out=Path(folder);java=out/'QcCategoryLengthCheck.java';java.write_text(JAVA,encoding='utf-8');dist=dep/'owned-distribution/target/distribution'
  cp=':'.join(map(str,[out,dist/'app/lims-owned-application.jar',dist/'lib/*']));base=ROOT/'project/src/main/java/com/sunwayworld/emc/module/mdm/basemdm/qccategory'
  subprocess.run([str(dep/'build/jdk1.8.0_391/bin/javac'),'-source','8','-target','8','-encoding','UTF-8','-cp',cp,'-d',str(out),str(base/'bean/EmcQcCategoryBean.java'),str(base/'service/impl/EmcQcCategoryServiceImpl.java'),str(base/'resource/impl/EmcQcCategoryResourceImpl.java'),str(java)],check=True)
  subprocess.run([str(dep/'build/jdk1.8.0_391/bin/java'),'-cp',cp,'QcCategoryLengthCheck'],check=True)
if __name__=='__main__':main()
