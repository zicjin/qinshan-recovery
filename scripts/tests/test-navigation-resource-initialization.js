'use strict';
const fs = require('fs');
const vm = require('vm');
const assert = require('assert');
const root = require('path').resolve(process.argv[2] || require('path').join(__dirname, '../..'));
const paths = {
 index: 'project/src/main/resources/static/emc/module/mdm/bizmdm/index-categories/emc-index-category-edit-list.js',
 change: 'project/src/main/resources/static/emc/module/bp/edit/project-changes/emc-project-change-edit-list.js',
 search: 'project/src/main/resources/static/emc/module/bp/edit/project-changes/emc-project-change-search-list.js',
 common: 'reconstituted/framework/owned-core-web/src/main/resources/static/gikam/main/js/chunk-common.js',
 diagram: 'reconstituted/framework/owned-core-web/src/main/resources/static/gikam/main/js/chunk-b0813c9a.js'
};
const sources = Object.fromEntries(Object.entries(paths).map(([k,p])=>[k,fs.readFileSync(root+'/'+p,'utf8')]));
function onlyReplace(text, old, value) {
 assert.strictEqual(text.split(old).length,2,'one exact source replacement');
 return text.replace(old,value);
}
function extractFunction(s, position) {
 let start=s.indexOf('function',position), brace=s.indexOf('{',start), depth=0, quote=null, escaped=false;
 for(let i=brace;i<s.length;i++) {
  const ch=s[i];
  if(quote) {if(escaped){escaped=false;}else if(ch==='\\'){escaped=true;}else if(ch===quote){quote=null;}continue;}
  if(ch==='"'||ch==="'"||ch==='`'){quote=ch;continue;}
  if(ch==='{')depth++;
  if(ch==='}'&&--depth===0)return s.slice(start,i+1);
 }
 throw Error('function not balanced');
}
function indexTests(s) {
 let activeRow=null,hidden=new Set(),ready=[],requests=[];
 const originalFields=['indexLowLimit','indexHighLimit','textIndexValue'];
 const configured=[{field:'indexLowLimit'},{field:'indexHighLimit'},{field:'textIndexValue'},{field:'unit'}];
 const detail={options:{columns:configured.slice()},store:{state:{columns:configured.slice()},commit(name,columns){this.state.columns=columns;}},hideColumnArray:[]};
 // Use the real Grid algorithms; their hidden-column cache survives refreshColumns.
 for(const method of ['hideColumns','showColumns']){
  const at=sources.common.indexOf('key:"'+method+'",value:')+('key:"'+method+'",value:').length;
  detail[method]=vm.runInNewContext('('+extractFunction(sources.common,at)+')');
 }
 function hiddenFields(){return originalFields.filter(f=>!detail.store.state.columns.some(c=>c.field===f));}
 function noDuplicate(){const f=detail.store.state.columns.map(c=>c.field);assert.strictEqual(new Set(f).size,f.length,'real grid columns must never duplicate');}

 const main={getActivedRow(){return activeRow;}};
 const field={refresh(options){requests.push(options);}};
 const ctx={emcIndexCategory:{getIndexSetColumns(){return [];}},Gikam:{width:{M:300},
  getComp(){return main;},getAsyncComp(id){return {done(callback){ready.push(()=>callback(id==='emc-index-set-detail-field-grid'?detail:field));}};}}};
 vm.runInNewContext(s,ctx);const page=ctx.emcIndexCategory.editPage;
 page.getOrderIndexUrl=id=>'/original/'+id+'/order-indexes/queries';
 const rendered=page.getIndexSetParam().panels[0].items[0].onRendered;
 const activate=page.getGridParam.bind(page);
 // getGridParam needs only original configuration-producing collaborators.
 ctx.emcIndexCategory.getEditListColumns=()=>[];page.getIndexCategoryButton=()=>[];
 const onRowActive=activate().onRowActive;
 rendered.call(detail);assert.strictEqual(hiddenFields().length,0,'no active row must keep ordinary columns');
 activeRow={id:'day',ext$:{templatename:'昼夜评价'}};
 onRowActive(0,activeRow);ready.splice(0).forEach(cb=>cb());
 assert.deepStrictEqual(hiddenFields().sort(),originalFields.slice().sort(),'actual parent selection hides daytime columns after readiness');
 activeRow={id:'normal',ext$:{templatename:'普通评价'}};
 onRowActive(1,activeRow);ready.splice(0).forEach(cb=>cb());
 assert.strictEqual(hiddenFields().length,0,'actual normal selection restores all three columns');
 assert(requests.some(q=>q.url==='/original/day/order-indexes/queries'));
 assert(requests.some(q=>q.url==='/original/normal/order-indexes/queries'));
 // Multiple parent changes before child is mounted must apply latest selection.
 activeRow={id:'late-day',ext$:{templatename:'昼夜评价'}};onRowActive(2,activeRow);
 activeRow={id:'latest-normal',ext$:{templatename:'默认评价'}};onRowActive(3,activeRow);
 ready.splice(0).forEach(cb=>cb());assert.strictEqual(hiddenFields().length,0,'late child readiness honors latest parent selection');
 activeRow={id:'missing-ext'};onRowActive(4,activeRow);ready.splice(0).forEach(cb=>cb());assert.strictEqual(hiddenFields().length,0);
 activeRow={id:'day-again',ext$:{templatename:'昼夜评价'}};rendered.call(detail);
 assert.strictEqual(hiddenFields().length,3,'child rendering itself applies current real parent template');
 // Exact browser regression: original refreshColumns installs fresh full columns,
 // while the previous hide cache still contains the same three field records.
 const unrelatedHidden={columnsIndex:10,field:{field:'unrelatedHidden'}};detail.hideColumnArray.push(unrelatedHidden);
 detail.store.commit('refreshColumns',configured.slice());
 activeRow={id:'normal-after-refresh',ext$:{templatename:'默认评价'}};onRowActive(5,activeRow);
 ready.splice(0).forEach(cb=>cb());noDuplicate();assert(detail.hideColumnArray.includes(unrelatedHidden),'other column hide state preserved');
 detail.hideColumnArray=detail.hideColumnArray.filter(c=>c!==unrelatedHidden);
 for(let i=0;i<4;i++){
  activeRow={id:'cycle-day-'+i,ext$:{templatename:'昼夜评价'}};onRowActive(6+i,activeRow);ready.splice(0).forEach(cb=>cb());noDuplicate();assert.strictEqual(hiddenFields().length,3);
  detail.store.commit('refreshColumns',configured.slice());
  activeRow={id:'cycle-normal-'+i,ext$:{templatename:'默认评价'}};onRowActive(10+i,activeRow);ready.splice(0).forEach(cb=>cb());noDuplicate();assert.strictEqual(hiddenFields().length,0);
 }

}
function panelTests(s,page) {
 const base=page==='editPage'?'edit':'search';
 const body=s.slice(s.indexOf('onProjectActive : function(row) {')+'onProjectActive : function(row) {'.length,s.indexOf("            Gikam.getAsyncComp('emc-project-change-"+base+"-list-base-info-form')"));
 assert(body.length>0);
 for(const hasOptional of [false,true]) {
  const ids=new Set(['emc-project-change-'+base+'-list-order-panel','emc-project-change-'+base+'-list-base-info-panel',base==='edit'?'emc-project-scheme-edit-panel':'emc-pcs-scheme-edit-panel']);
  if(hasOptional)ids.add('emc-project-sample-subpackage-info-panel');
  let removed=[],reverted=[];
  const tab={options:{panels:Array.from(ids).map(id=>({id}))},show(){},removePanel(id){assert(ids.has(id),'exact captured invalid removePanel '+id);removed.push(id);},showPanel(id){assert(ids.has(id));},revertPanel(id){assert(ids.has(id));reverted.push(id);}};
  const fn=vm.runInNewContext('(function(row){'+body+'}})',{Gikam:{getComp(){return tab;}}});
  const context={};fn.call(context,{sample:'1',id:'proj',testOrgId:'test-org',ext$:{editmonitorinfo:'1',sampleorgid:'sample-org'}});
  assert(removed.includes('emc-project-change-'+base+'-list-order-panel'));
  assert.strictEqual(removed.includes('emc-project-sample-subpackage-info-panel'),hasOptional,'optional panel removal only if declared');
  assert(reverted.includes(base==='edit'?'emc-project-scheme-edit-panel':'emc-pcs-scheme-edit-panel'));
  assert.strictEqual(context.param.projId,'proj');assert.strictEqual(context.param.sampleOrgId,'sample-org');
 }
}
function monitorTests(s) {
 const anchor=s.indexOf('components:{Monitor:function()');assert(anchor>0);
 const start=s.lastIndexOf('key:"createModel",value:',anchor)+'key:"createModel",value:'.length;
 const fnText=extractFunction(s,start);
 let registry={},mountedOptions;
 const g={createDom(){return {id:''};},setInstance(el,owner){if(el.id)registry[el.id]=owner;}};
 function Vue(opts){mountedOptions=opts;const inst={$el:opts.el};opts.mounted.call(inst);return inst;}
 const owner={options:{id:'core-monitor-edit-list',renderTo:{} }};
 vm.runInNewContext('('+fnText+')',{o:{a:g},n:{default:Vue}}).call(owner);
 assert.strictEqual(registry['core-monitor-edit-list'],owner,'actual DOM instance registers same component');
 assert.strictEqual(typeof mountedOptions.components.Monitor,'function','original lazy monitor remains');
 const chunk=fs.readFileSync(root+'/reconstituted/framework/owned-core-web/src/main/resources/static/gikam/main/js/chunk-51d60ada.js','utf8');
 assert(chunk.includes('e&&e.includes("monitor")?this.connect():o["a"].alert(tt["a"].prop("system.waring.server.not.enabled"))'),'disabled monitoring still follows original unavailable path');
}
function topologyTests(s) {
 const k=s.indexOf('background:function(){');assert(k>0);
 const fn=vm.runInNewContext('('+extractFunction(s,k+'background:'.length)+')');
 for(const value of [undefined,null,''])assert.strictEqual(fn.call({options:{backgroundImageUrl:value}}).backgroundImage,undefined,'unset background must not request /undefined');
 assert.strictEqual(fn.call({options:{backgroundImageUrl:'/static/approved.png'}}).backgroundImage,'url("/static/approved.png")','configured resource preserved');
}

function auditTests(s){
 const anchor=s.indexOf('kI={tableName:"",id:"",vertical:1}');assert(anchor>0);
 const methodAt=s.indexOf('key:"setData",value:',anchor)+'key:"setData",value:'.length;
 const fn=vm.runInNewContext('('+extractFunction(s,methodAt)+')');
 const owner={options:{vertical:1,id:'',tableName:''},model:{$children:[]}};
 fn.call(owner,{id:'first-record',tableName:'FIRST_TABLE'});
 fn.call(owner,{id:'latest-record',tableName:'LATEST_TABLE'});
 assert.strictEqual(owner.options.id,'latest-record','early selection preserved');
 let calls=[];
 owner.model.$children=[{$refs:{'audit-view':{init(data,table,reload){calls.push([data.recordId,table,reload,'vertical']);}},'audit-view-time':{init(data,table,reload){calls.push([data.recordId,table,reload,'time']);}}}}];
 const createAt=s.indexOf('key:"createModel",value:',anchor)+'key:"createModel",value:'.length;
 const create=extractFunction(s,createAt);
 const hookAt=create.indexOf('"hook:mounted":');assert(hookAt>=0,'real lazy-child mounted hook retries retained selection');
 const tickQueue=[];
 const mounted=vm.runInNewContext('('+extractFunction(create,hookAt)+')',{e:Object.assign(owner,{setData:fn}),n:{default:{nextTick(callback){tickQueue.push(callback);}}}});
 mounted();
 assert.strictEqual(calls.length,0,'lazy-child lifecycle waits until root model assignment/refs settle');
 assert.strictEqual(tickQueue.length,1,'one framework lifecycle tick, no polling');
 tickQueue.shift()();
 assert.deepStrictEqual(calls,[['latest-record','LATEST_TABLE',true,'vertical']]);
 fn.call(owner,{id:'third',tableName:'THIRD_TABLE'},0);
 assert.deepStrictEqual(calls[1],['third','THIRD_TABLE',true,'time']);
 assert.strictEqual(owner.options.vertical,0);
 // A cached async component can mount synchronously inside new Vue, before
 // the containing this.model assignment completes. Its lifecycle must wait
 // for that assignment and still initialize the latest actual table/entity.
 const warm={options:{vertical:1,id:'warm',tableName:'WARM_TABLE'},setData:fn};
 const warmTicks=[];
 const warmHook=vm.runInNewContext('('+extractFunction(create,hookAt)+')',{e:warm,n:{default:{nextTick(callback){warmTicks.push(callback);}}}});
 warmHook();
 warm.model={$children:owner.model.$children};
 assert.strictEqual(warmTicks.length,1);warmTicks.shift()();
 assert.deepStrictEqual(calls[2],['warm','WARM_TABLE',true,'vertical']);
}

function editorTests(){
 const base=root+'/reconstituted/framework/owned-core-web/src/main/resources/static/plugin/ckeditor/';
 const engine=fs.readFileSync(base+'ckeditor.js','utf8'),configSource=fs.readFileSync(base+'config.js','utf8');
 const plugins=engine.match(/CKEDITOR\.config\.plugins\s*=\s*"([^"]+)"/)[1].split(',');
 const ck={ENTER_BR:2,ENTER_P:1,sourceButton:false};
 vm.runInNewContext(configSource,{CKEDITOR:ck,top:{Gikam:{IFM_CONTEXT:'',locale:'zh-cn'}},parent:{Gikam:{IFM_CONTEXT:''}}});
 const config={font_names:'Arial',specialChars:[]};ck.editorConfig(config);
 const enabled=plugins.filter(name=>!(config.removePlugins||'').split(',').includes(name));
 assert(!(enabled.includes('image')&&enabled.includes('image2')),'original image and replacement image2 conflict');
 assert(enabled.includes('image2'),'image2 remains enabled');assert(!enabled.includes('resize'));
 assert.strictEqual(config.imageUploadUrl,'/core/module/item/files/action/ckeditor-upload');
 assert.strictEqual(config.filebrowserImageUploadUrl,config.imageUploadUrl);assert.strictEqual(config.uploadUrl,config.imageUploadUrl);
 assert.strictEqual(config.skin,'moono-lisa');assert.strictEqual(config.allowedContent,true);
}
function progressTests(){
 const anchor=sources.common.indexOf('C={props:{pageRequesting:Boolean}');assert(anchor>0);
 const end=sources.common.indexOf('},O=C',anchor)+1;
 const queue=[];let canceled=[];
 const timers={setTimeout(fn,ms){queue.push({fn,ms});return queue.length;},clearTimeout(id){canceled.push(id);}};
 const component=vm.runInNewContext('('+sources.common.slice(anchor+2,end)+')',{k:5,x:{0:6,10:5,20:4,30:3,40:2,50:1,60:.5,70:.1,80:.05,90:.01},...timers});
 for(const requesting of [false,true]){
  queue.length=0;const state={pageRequesting:requesting};Object.assign(state,component.data.call(state));
  for(const [name,method]of Object.entries(component.methods))state[name]=method.bind(state);
  if(component.mounted)component.mounted.call(state);
  assert.strictEqual(state.show,requesting,'initial progress visibility must equal actual pending state');
  if(requesting)assert(state.percent>0&&queue.length===1,'initial real request animates');
  else assert.strictEqual(queue.length,0,'idle initial state schedules no loading animation');
  state.pageRequesting=true;component.watch.pageRequesting.call(state,true);assert(state.show&&state.percent>0);
  state.pageRequesting=false;component.watch.pageRequesting.call(state,false);assert.strictEqual(state.percent,100);
  const finish=queue.find(q=>q.ms===400);assert(finish);finish.fn();assert.strictEqual(state.show,false);assert.strictEqual(state.percent,0);
 }
}
const tests=[['R20 actual WindowPageProgress initial state and request completion',progressTests],['R09 empty selection and original day/night branch',()=>indexTests(sources.index)],
 ['R11 edit optional panels and original query context',()=>panelTests(sources.change,'editPage')],
 ['R11 search optional panels and original query context',()=>panelTests(sources.search,'searchPage')],
 ['R12 no-argument Vue mounted and disabled monitor contract',()=>monitorTests(sources.common)],
 ['R10 async audit detail retains latest selection and both views',()=>auditTests(sources.common)],
 ['R13 absent background and configured resource',()=>topologyTests(sources.diagram)],
 ['R15 existing CKEditor image2 and upload configuration',editorTests]];
new vm.Script(sources.common,{filename:'chunk-common.js'});
new vm.Script(sources.diagram,{filename:'chunk-topology.js'});
let failures=0;
for(const [name,test]of tests){try{test();console.log(name+': PASS');}catch(error){failures++;console.error(name+': FAIL '+error.message);}}
process.exitCode=failures?1:0;
