#!/usr/bin/env python3
"""Exercise the actual i18n cache and message resolver without HTTP, SQL or Redis."""
import argparse
from pathlib import Path
import shutil
import subprocess
import tempfile
import hashlib
import json

ROOT = Path(__file__).resolve().parents[2]
JAVA = r"""
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.i18n.DatabaseDrivenMessageSource;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.tenant.TenantManager;
import com.sunwayworld.module.sys.i18n.CoreI18nCache;
import com.sunwayworld.module.sys.i18n.bean.*;
import com.sunwayworld.module.sys.i18n.service.*;
import com.sunwayworld.module.sys.i18n.dao.CoreI18nMessageDao;
import org.springframework.context.i18n.LocaleContextHolder;

public class ButtonI18nContract {
    static void equal(Object expected, Object actual, String msg) {
        if (!Objects.equals(expected, actual)) throw new AssertionError(msg + ": expected="+expected+", actual="+actual);
    }
    public static class DirectTenantManager extends TenantManager {
        @Override public void masterCall(String span, Runnable task) { task.run(); }
        @Override public void call(String span, Runnable task, String... ignored) { task.run(); }
    }
    public static class MemoryRedis extends RedisTemplate<String,Object> {
        final Map<String,Object> values = new HashMap<>();
        final ValueOperations<String,Object> ops = (ValueOperations<String,Object>)Proxy.newProxyInstance(
            ValueOperations.class.getClassLoader(), new Class<?>[]{ValueOperations.class}, (p,m,a)->{
                if (m.getName().equals("get")) return values.get(a[0]);
                if (m.getName().equals("set")) {values.put((String)a[0],a[1]); return null;}
                if (m.getName().equals("getOperations")) return this;
                throw new AssertionError("unexpected Redis op "+m.getName());
            });
        @Override public ValueOperations<String,Object> opsForValue(){return ops;}
        @Override public Boolean delete(String key){return values.remove(key)!=null;}
    }
    static CoreI18nMessageBean message(long id,String code,String value) {
        CoreI18nMessageBean m=new CoreI18nMessageBean();m.setId(id);m.setI18nConfigId("zh-CN");m.setMessage(value);m.setExt$Item("code",code);return m;
    }
    public static void main(String[] args) throws Exception {
        boolean custom=args.length>0 && "custom".equals(args[0]);
        boolean project=args.length>0 && "project".equals(args[0]);
        StaticApplicationContext ctx = new StaticApplicationContext();
        if (custom) ctx.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test",
            Collections.<String,Object>singletonMap("spring.messages.basename","custom/messages")));
        CoreI18nConfigBean zh=new CoreI18nConfigBean();zh.setId("zh-CN");
        CoreI18nConfigBean en=new CoreI18nConfigBean();en.setId("en-US");
        List<CoreI18nMessageBean> db=new ArrayList<>();
        if (custom) {db.add(message(1L,"GIKAM.BUTTON.INSERT","数据库添加"));db.add(message(2L,"GIKAM.BUTTON.SAVE","数据库保存"));db.add(message(3L,"GIKAM.BUTTON.DELETE","数据库删除"));} else if (project) {db.add(message(4L,"GIKAM.BUTTON.SEARCH","数据库查询"));db.add(message(5L,"GIKAM.BUTTON.COPY","数据库复制"));}
        CoreI18nConfigService configs=(CoreI18nConfigService)Proxy.newProxyInstance(CoreI18nConfigService.class.getClassLoader(),
            new Class<?>[]{CoreI18nConfigService.class},(p,m,a)->{
                if(m.getName().equals("selectAll"))return Arrays.asList(zh,en);
                throw new AssertionError("unexpected config operation "+m.getName());
            });
        CoreI18nMessageDao dao=(CoreI18nMessageDao)Proxy.newProxyInstance(CoreI18nMessageDao.class.getClassLoader(),
            new Class<?>[]{CoreI18nMessageDao.class},(p,m,a)->{
                if(m.getName().equals("count"))return db.size();
                throw new AssertionError("unexpected DAO operation "+m.getName());
            });
        CoreI18nMessageService messages=(CoreI18nMessageService)Proxy.newProxyInstance(CoreI18nMessageService.class.getClassLoader(),
            new Class<?>[]{CoreI18nMessageService.class},(p,m,a)->{
                if(m.getName().equals("getDao"))return dao;
                if(m.getName().equals("selectPagination"))return new Pagination<CoreI18nMessageBean>(db);
                throw new AssertionError("unexpected message operation "+m.getName());
            });
        ctx.getBeanFactory().registerSingleton("configs",configs);ctx.getBeanFactory().registerSingleton("messages",messages);
        ctx.getBeanFactory().registerSingleton("tenant",new DirectTenantManager());ctx.refresh();
        ApplicationContextHelper.setApplicationContext(ctx);
        new RedisHelper().setRedisTemplate(new MemoryRedis());
        ThreadPoolExecutor executor=(ThreadPoolExecutor)Executors.newFixedThreadPool(1);
        new ExecutorHelper().setExecutor(executor);
        try {
            DatabaseDrivenMessageSource resolver=new DatabaseDrivenMessageSource(ctx);
            new I18nHelper().setMessageSource(resolver);LocaleContextHolder.setLocale(Locale.CHINA);
            equal(custom?"数据库添加":"添加",I18nHelper.getMessage("GIKAM.BUTTON.INSERT"),"actual helper INSERT");
            equal(custom?"自定义删除":"删除",I18nHelper.getMessage("GIKAM.BUTTON.DELETE"),"actual helper DELETE");
            equal(custom?"数据库保存":"保存",I18nHelper.getMessage("GIKAM.BUTTON.SAVE"),"actual helper SAVE");
            equal("导入",I18nHelper.getMessage("GIKAM.BUTTON.IMPORT"),"actual helper IMPORT");
            equal("导出",I18nHelper.getMessage("GIKAM.BUTTON.EXPORT"),"actual helper EXPORT");
            if(!custom){equal("查询",I18nHelper.getMessage("GIKAM.BUTTON.SEARCH"),"existing project SEARCH");equal("复制",I18nHelper.getMessage("GIKAM.BUTTON.COPY"),"existing project COPY");}
            equal("GIKAM.BUTTON.NOT_DEFINED",I18nHelper.getMessage("GIKAM.BUTTON.NOT_DEFINED"),"unknown key remains unknown");
            if(!custom) {
                Properties defaults=new Properties();
                try(java.io.Reader reader=new java.io.InputStreamReader(ButtonI18nContract.class.getResourceAsStream("/i18n/core-button-defaults_zh-cn.properties"),"UTF-8")){defaults.load(reader);}
                equal(25,defaults.size(),"all source-backed defaults packaged");
                for(String code:defaults.stringPropertyNames())equal(defaults.getProperty(code),I18nHelper.getMessage(code),"original SQL default "+code);
            }
            equal(null,CoreI18nCache.instance().getI18nLocaleMap("en-US").get("GIKAM.BUTTON.INSERT"),"no invented English locale");
            equal(null,CoreI18nCache.instance().getI18nLocaleMap("zh-CN").get("GIKAM.SEARCH"),"unrelated R21 key excluded");
            if(!custom && !project) {
                db.add(message(6L,"GIKAM.BUTTON.INSERT","重载后的数据库添加"));
                Method load=CoreI18nCache.class.getDeclaredMethod("load");load.setAccessible(true);load.invoke(CoreI18nCache.instance());
                equal("重载后的数据库添加",I18nHelper.getMessage("GIKAM.BUTTON.INSERT"),"later database override must survive repeated cache reload");
                equal("删除",I18nHelper.getMessage("GIKAM.BUTTON.DELETE"),"defaults remain available after reload");
            }
            System.out.println("button i18n actual cache/helper "+(custom?"database/custom precedence":project?"original project-over-database precedence":"source defaults and subsequent database reload")+": PASS");
        } finally {executor.shutdownNow();LocaleContextHolder.resetLocaleContext();ctx.close();}
    }
}
"""

def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--dependency-root',type=Path,default=ROOT)
    parser.add_argument('--jdk',type=Path)
    args=parser.parse_args();dep=args.dependency_root.resolve()
    jars=[*sorted((dep/'reconstituted/framework').glob('*/target/*.jar')),dep/'owned-framework/target/lims-owned-framework-1.0.0.jar',*sorted((dep/'build/reconstituted-public-deps').glob('*.jar'))]
    if not all(x.is_file()for x in jars)or len(jars)<20:parser.error('existing source-only modules and public dependencies required; this test never builds them')
    javac=str(args.jdk/'bin/javac')if args.jdk else shutil.which('javac');java=str(args.jdk/'bin/java')if args.jdk else shutil.which('java')
    if not javac or not java:parser.error('Java compiler and runtime required')
    provenance=json.loads((ROOT/'docs/reconstitution/button-i18n-defaults.json').read_text())
    sql=(ROOT/provenance['source_path']).read_bytes()
    if hashlib.sha256(sql).hexdigest()!=provenance['source_sha256']:raise AssertionError('original SQL provenance hash changed')
    resource=ROOT/provenance['resource_path']
    if hashlib.sha256(resource.read_bytes()).hexdigest()!=provenance['resource_sha256']:raise AssertionError('button defaults resource hash changed')
    entries={line.split('=',1)[0]:line.split('=',1)[1]for line in resource.read_text().splitlines()if line and not line.startswith('#')}
    if len(entries)!=25 or set(entries)!={x['code']for x in provenance['entries']}:raise AssertionError('finite25button provenance mismatch')
    for item in provenance['entries']:
        statement=sql.decode().splitlines()[item['line']-1]
        if hashlib.sha256(statement.encode()).hexdigest()!=item['statement_sha256'] or entries[item['code']]!=item['value']:raise AssertionError('SQL exact translation mismatch: '+item['code'])
    source=ROOT/'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/module/sys/i18n/CoreI18nCache.java'
    resources=ROOT/'reconstituted/framework/owned-core-common/src/main/resources'
    with tempfile.TemporaryDirectory(prefix='button-i18n-')as directory:
        out=Path(directory);test=out/'ButtonI18nContract.java';test.write_text(JAVA,encoding='utf-8');custom=out/'custom';custom.mkdir();(custom/'messages_zh-cn.properties').write_text('GIKAM.BUTTON.DELETE=自定义删除\n',encoding='utf-8')
        cp=':'.join(str(x)for x in [out,resources,ROOT/'project/src/main/resources',*jars])
        subprocess.run([javac,'-source','8','-target','8','-encoding','UTF-8','-proc:none','-cp',cp,'-d',str(out),str(source),str(test)],check=True)
        for scenario in [[],['custom'],['project']]:subprocess.run([java,'-cp',cp,'ButtonI18nContract',*scenario],check=True)

if __name__=='__main__':main()
