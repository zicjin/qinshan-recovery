#!/usr/bin/env python3
"""Render the original detail via actual Spring MVC/Thymeleaf, then execute its JS.

Uses isolated temporary class output and existing source-only/public build inputs;
no real HTTP, database, runtime refresh or shared target mutation is performed.
"""
import argparse
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]
JAVA = r"""
import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.script.*;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.controller.impl.EmcEquiptRequisitionControllerImpl;

public final class EquiptRequisitionDetailCheck {
    static void need(boolean ok,String message){if(!ok)throw new AssertionError(message);}
    static void jsNeed(ScriptEngine js,String expression,String message)throws Exception{need(Boolean.TRUE.equals(js.eval(expression)),message);}
    public static final class Resources {
        final ScriptEngine js;final List<String> loaded=new ArrayList<>();Resources(ScriptEngine js){this.js=js;}
        public void load(String name)throws Exception{
            try(InputStream input=getClass().getResourceAsStream(name)){
                need(input!=null,"missing original JS "+name);js.eval(new InputStreamReader(input,"UTF-8"));loaded.add(name);
            }
        }
    }
    public static void main(String[] args)throws Exception{
        String base="/secure/emc/module/mdm/basemdm/equipt-requisitions/page";
        String directory="emc/module/mdm/basemdm/equipt-requisitions/";
        ClassLoaderTemplateResolver resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
        SpringTemplateEngine engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
        ThymeleafViewResolver views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
        MockMvc mvc=MockMvcBuilders.standaloneSetup(new EmcEquiptRequisitionControllerImpl()).setViewResolvers(views).build();
        MvcResult response=mvc.perform(MockMvcRequestBuilders.get(base+"/detail").param("id","23").param("operType","search")).andReturn();
        need(response.getResponse().getStatus()==200,"original detail route must resolve, actual="+response.getResponse().getStatus());
        need(response.getModelAndView()!=null && (directory+"emc-equipt-requisition-detail").equals(response.getModelAndView().getViewName()),"existing detail view mapping");
        String html=response.getResponse().getContentAsString();need(!html.contains("[[${")&&!html.contains("th:inline"),"real Thymeleaf must evaluate request parameters");
        ScriptEngine js=new ScriptEngineManager().getEngineByName("nashorn");need(js!=null,"JDK8 required");Resources resources=new Resources(js);js.put("resources",resources);
        js.eval("Object.assign=Object.assign||function(t){for(var i=1;i<arguments.length;i++){var s=arguments[i];Object.keys(s).forEach(function(k){t[k]=s[k];});}return t;};var IFM_CONTEXT='';var layout=null;var layouts=0;var writes=0;var modal={window:{$dom:{id:'detail-modal'}}};"
            +"var workspace={user:{id:'test',userName:'test',orgName:'test',ext$:{parentorgid:'test-org'}},window:{}};"
            +"var Gikam={getPageObject:function(o){return Object.assign({},o);},requirejs:function(p){p.forEach(function(s){resources.load(s);});},"
            +"getElParam:function(p){var r={};Object.keys(p).forEach(function(k){r[k]=p[k][0];});return r;},"
            +"getLastModal:function(){return modal;},isEmpty:function(v){return v==null||v==='';},printf:function(s,p){return s.replace('{id}',p.id);},"
            +"create:function(type,o){if(type!=='layout')throw new Error('unexpectedUI '+type);layout=o;layouts++;}};");
        Matcher scripts=Pattern.compile("<script[^>]*>([\\s\\S]*?)</script>").matcher(html);int scriptCount=0;
        while(scripts.find()){js.eval(scripts.group(1));scriptCount++;}
        need(scriptCount==1,"exact original inline initializer");
        need(resources.loaded.equals(Arrays.asList("/static/"+directory+"emc-equipt-requisition.js","/static/"+directory+"emc-equipt-requisition-detail.js")),"original dependency order");
        jsNeed(js,"layouts===1&&layout.renderTo===modal.window.$dom","real original modal initialization");
        jsNeed(js,"emcEquiptRequisition.detailPage.param.id==='23'&&emcEquiptRequisition.detailPage.param.operType==='search'","real rendered id/search parameters");
        jsNeed(js,"layout.center.items[0].url==='/secure/emc/module/mdm/basemdm/equipt-requisitions/23'","original entity readback URL");
        jsNeed(js,"layout.center.items[0].fields.length===22&&layout.center.items[0].fields.every(function(f){return !!f.field&&f.readonly===true&&!f.editor&&!f.preInsert&&!f.checkbox;})","all original business fields must be readonly in search detail");
        jsNeed(js,"['ext$.equiptcode','ext$.equiptname','rentUser','rentOrg','rentDate','rentTime','backUser','backOrg','backDate','backTime','remark','projNos','confirmUserName','accessory'].every(function(k){return layout.center.items[0].fields.some(function(f){return f.field===k;});})","original business fields retained");
        jsNeed(js,"emcEquiptRequisition.getEditListColumns().some(function(f){return f.field==='rentDate'&&f.editor===true;})","detail readonly must not mutate edit columns");
        jsNeed(js,"emcEquiptRequisition.detailPage.getBtnToolbar().items.length===1&&emcEquiptRequisition.detailPage.getBtnToolbar().items[0].text==='GIKAM.BUTTON.BACK'","search detail must not expose Save");
        // Existing adjacent routes must remain registered, with no second controller.
        for(String route:new String[]{"edit-list","audit-list","search-list","instrument-out"}){
            MvcResult page=mvc.perform(MockMvcRequestBuilders.get(base+"/"+route)).andReturn();need(page.getResponse().getStatus()==200,"existing route lost "+route);
        }
        System.out.println("equipment requisition detail: PASS (Spring mapping, real Thymeleaf, original JS,22readonly fields,original entity URL and adjacent routes)");
    }
}
"""

def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--dependency-root',type=Path,default=ROOT)
    parser.add_argument('--jdk',type=Path)
    parser.add_argument('--spring-test',type=Path,required=True)
    args=parser.parse_args();dep=args.dependency_root.resolve();jdk=args.jdk or dep/'build/jdk1.8.0_391'
    framework=dep/'reconstituted/framework/owned-core-common/target/owned-core-common-1.0.0-SNAPSHOT.jar';deps=dep/'build/reconstituted-public-deps'
    for f in [jdk/'bin/javac',framework,deps,args.spring_test]:
        if not f.exists():parser.error('missing test prerequisite: '+str(f))
    with tempfile.TemporaryDirectory(prefix='equipt-requisition-detail-')as folder:
        out=Path(folder);test=out/'EquiptRequisitionDetailCheck.java';test.write_text(JAVA,encoding='utf-8')
        cp=':'.join(map(str,[out,ROOT/'project/src/main/resources',ROOT/'reconstituted/framework/owned-core-common/src/main/resources',framework,deps/'*',args.spring_test]))
        c=ROOT/'project/src/main/java/com/sunwayworld/emc/module/mdm/basemdm/equiptrequisition/controller'
        subprocess.run([str(jdk/'bin/javac'),'-source','8','-target','8','-encoding','UTF-8','-cp',cp,'-d',str(out),str(c/'EmcEquiptRequisitionController.java'),str(c/'impl/EmcEquiptRequisitionControllerImpl.java'),str(test)],check=True)
        subprocess.run([str(jdk/'bin/java'),'-cp',cp,'EquiptRequisitionDetailCheck'],check=True)

if __name__=='__main__':main()
