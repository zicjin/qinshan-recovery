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
# Exact values independently observed in the current-production zh-CN map.
# Both original project resources contain these keys with different values;
# the current-production basename order makes the projectfunction value win.
PRODUCTION_PROJECT_MESSAGES = {
    "EMC.MODULE.BP.ORDER.ORDERRECEIPTS.BUTTON.MATCHBARCODE": "匹配二维码",
    "EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.CONFIRM_MATCH_BARCODE": "确认匹配二维码",
    "EMC.MODULE.BP.ORDER.ORDERRECEIPTS.TIP.NOTMATCH": "未匹配上任何二维码",
    "EMC.MODULE.BP.RESULT.RUNS.MODAL.EDIT_EQUIPT": "修改仪器",
    "EMC.MODULE.BP.RESULT.RUNS.TIP.EMPTY_REPORT_RESULT": "【{0}】样品下的报出结果字段为空，不能提交",
    "EMC.MODULE.BP.RESULT.RUNS.TIP.EXIST_UNFINISHED_ORDER_TASK": "【{0}】下的【{1}】测试还没有建批",
    "EMC.MODULE.BP.RESULT.RUNS.TIP.EXIST_UNFINISHED_RUN": "【{0}】下的【{2}】测试批【{1}】还未提交完成",
    "EMC.MODULE.BP.RESULT.RUNS.TITLE.CHOOSE_ORDER_TASK": "选择测试",
    "EMC.MODULE.BP.RESULT.RUNS.TITLE.CHOOSE_RUN": "选择测试批",
    "EMC.MODULE.MDM.BASEMDM.MTLS.STOCK.TIP.BATCH_NO_EXIST": "唯一性编号重复",
    "EMC.MODULE.MDM.EQUIPT.EQUIPTS.TAB.BASE_INFO": "基本信息",
    "GIKAM.BUTTON.LABEL_PRINT": "任务分析表打印",
    "T_EMC_CURVE.MARKINGNO": "标准使用液编号",
    "T_EMC_EQUIPT_CALIBRATION_LINE.GASNAME1": "标准参数",
    "T_EMC_EQUIPT_CALIBRATION_LINE.STANDRESULT1": "标准值",
    "T_EMC_IMPROVEMENT.CREATEDBYNAME": "编制人",
    "T_EMC_IMPROVEMENT.CREATEDTIME": "编制时间",
    "T_EMC_ORDER.LABELNO2": "客户样品编号",
    "T_EMC_ORDER_TASK.STOCKBATCHNO": "标准物质质控编号",
    "T_EMC_PROJECT.CREATEDBYNAME": "下单人",
    "T_EMC_PROJECT.CREATEDTIME": "下单创建日期",
    "T_EMC_PROJECT.ENTRUSTORGNAME": "委托单位",
    "T_EMC_PROJECT.PROJNAME": "任务名称",
    "T_EMC_PROJECT.TESTORGADDRESS": "受检地址",
    "T_EMC_PROJECT.TESTORGNAME": "受检单位",
    "T_EMC_PROJ_ORDER.TESTMETHODBASIS": "方法依据",
    "T_EMC_QUALITY_SUPERVISION_PLAN.CREATEDBYNAME": "编制人",
    "T_EMC_QUALITY_SUPERVISION_PLAN.CREATEDTIME": "编制时间",
    "T_EMC_REPORT.REFUSEREASON": "回退原因",
    "T_EMC_RUN.CREATEDTIME": "建批时间",
    "T_EMC_SOL.TAKEWEIGHTORVOLUME": "标准液称重量(体积)",
    "T_EMC_SOL.TOCALIBRATESOLVOLUME": "待标液称重量(体积)",
}
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
import java.io.*;

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
        boolean configured=args.length>0 && "configured".equals(args[0]);
        StaticApplicationContext ctx = new StaticApplicationContext();
        if (custom) ctx.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test",
            Collections.<String,Object>singletonMap("spring.messages.basename","custom/messages")));
        if (configured) ctx.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test",
            Collections.<String,Object>singletonMap("spring.messages.basename",
                "static/emc/i18n/messages-emc,static/emc/projectfunction/i18n/messages-weipu")));
        CoreI18nConfigBean zh=new CoreI18nConfigBean();zh.setId("zh-CN");
        CoreI18nConfigBean en=new CoreI18nConfigBean();en.setId("en-US");
        List<CoreI18nMessageBean> db=new ArrayList<>();
        if (custom) {db.add(message(1L,"GIKAM.BUTTON.INSERT","数据库添加"));db.add(message(2L,"GIKAM.BUTTON.SAVE","数据库保存"));db.add(message(3L,"GIKAM.BUTTON.DELETE","数据库删除"));db.add(message(7L,"GIKAM.TIP.CHOOSE_ONE_ITEM","数据库选择提示"));db.add(message(8L,"GIKAM.TIP.INVALID_INPUT_FIELDS","数据库输入提示"));db.add(message(9L,"GIKAM.VALIDATOR.REQUIRED","数据库必填提示"));} else if (project) {db.add(message(4L,"GIKAM.BUTTON.SEARCH","数据库查询"));db.add(message(5L,"GIKAM.BUTTON.COPY","数据库复制"));}
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
            if (!custom) {
                Properties expected = new Properties();
                String expectedPath=configured?"/configured-project-messages.properties":"/production-project-messages.properties";
                try(Reader reader=new InputStreamReader(ButtonI18nContract.class.getResourceAsStream(expectedPath),"UTF-8")){expected.load(reader);}
                equal(32,expected.size(),"complete production duplicate-key regression scope");
                for(String code:expected.stringPropertyNames()) {
                    equal(expected.getProperty(code),CoreI18nCache.instance().getI18nLocaleMap("zh-CN").get(code),"public locale map project precedence "+code);
                    equal(expected.getProperty(code),I18nHelper.getMessage(code),"message resolver project precedence "+code);
                }
            }
            equal(custom?"数据库添加":"添加",I18nHelper.getMessage("GIKAM.BUTTON.INSERT"),"actual helper INSERT");
            equal(custom?"自定义删除":"删除",I18nHelper.getMessage("GIKAM.BUTTON.DELETE"),"actual helper DELETE");
            equal(custom?"数据库保存":"保存",I18nHelper.getMessage("GIKAM.BUTTON.SAVE"),"actual helper SAVE");
            equal("导入",I18nHelper.getMessage("GIKAM.BUTTON.IMPORT"),"actual helper IMPORT");
            equal("导出",I18nHelper.getMessage("GIKAM.BUTTON.EXPORT"),"actual helper EXPORT");
            if(!custom){equal("查询",I18nHelper.getMessage("GIKAM.BUTTON.SEARCH"),"existing project SEARCH");equal("复制",I18nHelper.getMessage("GIKAM.BUTTON.COPY"),"existing project COPY");}
            equal("GIKAM.BUTTON.NOT_DEFINED",I18nHelper.getMessage("GIKAM.BUTTON.NOT_DEFINED"),"unknown key remains unknown");
            equal(custom?"项目选择提示":"请选择一条数据",I18nHelper.getMessage("GIKAM.TIP.CHOOSE_ONE_ITEM"),"actual selection prompt/default and project override");
            equal(custom?"数据库输入提示":"突出显示的字段的输入无效:",I18nHelper.getMessage("GIKAM.TIP.INVALID_INPUT_FIELDS"),"actual invalid input prompt/default and database override");
            equal(custom?"项目必填提示":"必填项，请填写",I18nHelper.getMessage("GIKAM.VALIDATOR.REQUIRED"),"actual required validator/default and project override");
            equal("GIKAM.TIP.NOT_DEFINED",I18nHelper.getMessage("GIKAM.TIP.NOT_DEFINED"),"unknown prompt remains unknown");
            for(String code:Arrays.asList("GIKAM.TIP.CHOOSE_ONE_ITEM","GIKAM.TIP.INVALID_INPUT_FIELDS","GIKAM.VALIDATOR.REQUIRED"))
                equal(null,CoreI18nCache.instance().getI18nLocaleMap("en-US").get(code),"no invented prompt English locale "+code);

            if(!custom) {
                Properties defaults=new Properties();
                try(java.io.Reader reader=new java.io.InputStreamReader(ButtonI18nContract.class.getResourceAsStream("/i18n/core-button-defaults_zh-cn.properties"),"UTF-8")){defaults.load(reader);}
                equal(28,defaults.size(),"finite25button plus3source-backed prompts packaged");
                for(String code:defaults.stringPropertyNames())equal(defaults.getProperty(code),I18nHelper.getMessage(code),"original SQL default "+code);
            }
            equal(null,CoreI18nCache.instance().getI18nLocaleMap("en-US").get("GIKAM.BUTTON.INSERT"),"no invented English locale");
            equal(null,CoreI18nCache.instance().getI18nLocaleMap("zh-CN").get("GIKAM.SEARCH"),"unrelated R21 key excluded");
            if(!custom && !project) {
                db.add(message(6L,"GIKAM.BUTTON.INSERT","重载后的数据库添加"));
                db.add(message(10L,"GIKAM.TIP.CHOOSE_ONE_ITEM","重载后的数据库选择提示"));
                Method load=CoreI18nCache.class.getDeclaredMethod("load");load.setAccessible(true);load.invoke(CoreI18nCache.instance());
                equal("重载后的数据库添加",I18nHelper.getMessage("GIKAM.BUTTON.INSERT"),"later database override must survive repeated cache reload");
                equal("删除",I18nHelper.getMessage("GIKAM.BUTTON.DELETE"),"defaults remain available after reload");
                equal("重载后的数据库选择提示",I18nHelper.getMessage("GIKAM.TIP.CHOOSE_ONE_ITEM"),"later database prompt override survives cache reload");
                equal("必填项，请填写",I18nHelper.getMessage("GIKAM.VALIDATOR.REQUIRED"),"other prompt default remains after reload");
            }
            System.out.println("button i18n actual cache/helper "+(custom?"database/custom precedence":configured?"explicit basename precedence":project?"original project-over-database precedence":"production basename defaults and subsequent database reload")+": PASS");
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
    button_entries={k:v for k,v in entries.items()if k.startswith('GIKAM.BUTTON.')}
    if len(button_entries)!=25 or set(button_entries)!={x['code']for x in provenance['entries']}:raise AssertionError('finite25button provenance mismatch')
    prompt_provenance=json.loads((ROOT/'docs/reconstitution/validation-i18n-defaults.json').read_text())
    if len(entries)!=28 or set(entries)-set(button_entries)!={x['code']for x in prompt_provenance['entries']}:raise AssertionError('finite3prompt provenance mismatch')
    if prompt_provenance['source_sha256']!=hashlib.sha256(sql).hexdigest()or prompt_provenance['resource_sha256']!=hashlib.sha256(resource.read_bytes()).hexdigest():raise AssertionError('prompt source/resource provenance hash changed')
    for item in prompt_provenance['entries']:
        statement=sql.decode().splitlines()[item['line']-1]
        if hashlib.sha256(statement.encode()).hexdigest()!=item['statement_sha256']or entries[item['code']]!=item['value']:raise AssertionError('SQL exact prompt mismatch: '+item['code'])
    for item in provenance['entries']:
        statement=sql.decode().splitlines()[item['line']-1]
        if hashlib.sha256(statement.encode()).hexdigest()!=item['statement_sha256'] or entries[item['code']]!=item['value']:raise AssertionError('SQL exact translation mismatch: '+item['code'])
    source=ROOT/'reconstituted/framework/owned-core-common/src/main/java/com/sunwayworld/module/sys/i18n/CoreI18nCache.java'
    resources=ROOT/'reconstituted/framework/owned-core-common/src/main/resources'
    with tempfile.TemporaryDirectory(prefix='button-i18n-')as directory:
        out=Path(directory);test=out/'ButtonI18nContract.java';test.write_text(JAVA,encoding='utf-8');custom=out/'custom';custom.mkdir();(custom/'messages_zh-cn.properties').write_text('GIKAM.BUTTON.DELETE=自定义删除\nGIKAM.TIP.CHOOSE_ONE_ITEM=项目选择提示\nGIKAM.VALIDATOR.REQUIRED=项目必填提示\n',encoding='utf-8')
        (out/'production-project-messages.properties').write_text(''.join(k+'='+v+'\n' for k,v in PRODUCTION_PROJECT_MESSAGES.items()),encoding='utf-8')
        original_first={}
        for line in (ROOT/'project/src/main/resources/static/emc/i18n/messages-emc_zh-cn.properties').read_text(encoding='utf-8').splitlines():
            if line.strip() and not line.strip().startswith('#') and '=' in line:
                key,value=line.split('=',1);original_first.setdefault(key.strip(),value.strip())
        (out/'configured-project-messages.properties').write_text(''.join(k+'='+original_first[k]+'\n' for k in PRODUCTION_PROJECT_MESSAGES),encoding='utf-8')
        cp=':'.join(str(x)for x in [out,resources,ROOT/'project/src/main/resources',*jars])
        subprocess.run([javac,'-source','8','-target','8','-encoding','UTF-8','-proc:none','-cp',cp,'-d',str(out),str(source),str(test)],check=True)
        for scenario in [[],['custom'],['project'],['configured']]:subprocess.run([java,'-cp',cp,'ButtonI18nContract',*scenario],check=True)

if __name__=='__main__':main()
