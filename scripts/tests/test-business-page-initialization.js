const fs = require('fs');
const vm = require('vm');
const path = require('path');
const assert = require('assert');
const resources = path.resolve(process.argv[2] || path.join(__dirname, '../../project/src/main/resources'));
const equipmentDir = 'static/emc/module/mdm/basemdm/equipt-requisitions/';
const improvementDir = 'static/emc/module/item/improvements/';
const failures = [];
function check(name, run) { try { run(); console.log('PASS ' + name); } catch (e) { failures.push(name); console.log('FAIL ' + name + ': ' + e.message); } }
function context() {
  const comps = {}, queued = {}, events = [];
  const c = {IFM_CONTEXT:'', workspace:{user:{id:'test',userName:'test',orgName:'test',ext$:{parentorgid:'test'}},window:{$dom:{}}},
    console, comps, queued, events};
  c.Gikam = {getPageObject:o=>Object.assign({workflow:{dbTable:'T_EMC_EQUIPT_REQUISITION'}},o),status:{getBpmnColumn:t=>({field:'processStatus',table:t})},
    isEmpty:x=>x==null || (Array.isArray(x) && x.length===0), getComp:id=>comps[id], getAsyncComp:id=>({done(fn){if(comps[id])fn(comps[id]);else(queued[id]||(queued[id]=[])).push(fn);}}),
    printf:(s,p)=>s.replace('{id}',p.id),create(type,options){c.layout=options;},DateUtils:{formatter:()=>''},param:()=>''};
  c.mount = (id,type) => { const comp = {cleanData(){events.push(id+':clean');},setOptions(o){events.push(id+':options');Object.assign(this,o);},refresh(o){events.push(id+':refresh');this.url=o.url;},activeRowByIndex(i){events.push(id+':active:'+i);}};
    if(type==='uploader')comp.grid={cleanData(){events.push(id+':grid-clean');}};
    comps[id]=comp;(queued[id]||[]).forEach(fn=>fn(comp));delete queued[id];return comp; };
  vm.createContext(c);return c;
}
function load(c,filename){vm.runInContext(fs.readFileSync(path.join(resources,filename),'utf8'),c,{filename});}
for(const page of ['audit','search'])check('equipment-'+page+'-columns',()=>{
 const c=context();load(c,equipmentDir+'emc-equipt-requisition.js');load(c,equipmentDir+'emc-equipt-requisition-'+page+'-list.js');
 const grid=c.emcEquiptRequisition[page+'Page'].getGridParam();assert.strictEqual(grid.columns.length,23);const fields=grid.columns.map(x=>x.field).filter(Boolean);
 assert.deepStrictEqual(Array.from(fields), Array.from(c.emcEquiptRequisition.getEditListColumns().map(x=>x.field).filter(Boolean)), 'all original22 business fields must survive');
 for(const f of ['ext$.equiptcode','ext$.equiptname','rentDate','rentTime','backDate','backTime','accessory','projNos','confirmUserName'])assert(fields.includes(f),'lost real equipment field '+f);
 assert(grid.columns.filter(x=>x.field).every(x=>!x.editor&&!x.preInsert),'read/audit columns must not remain inline writable');
 const edit=c.emcEquiptRequisition.getEditListColumns();assert(edit.find(x=>x.field==='rentDate').editor===true,'edit mode mutated by read columns');
 assert.strictEqual(grid.url,'/secure/emc/module/mdm/basemdm/equipt-requisitions/queries/'+(page==='audit'?'tasks':'searchable'));
 assert(grid.columns.some(x=>x.checkbox===true),'workflow/selection checkbox lost');
});
for(const page of ['edit','search','audit']){
 check('improvement-'+page+'-empty-mounted',()=>{
  const c=context();load(c,improvementDir+'emc-improvement.js');load(c,improvementDir+'emc-improvement-'+page+'-list.js');const pg=c.emcImprovement[page+'Page'];const opts=[pg.getFormParam(),pg.getReportFormParam(),pg.getAttachmentGridParam()];for(const o of opts)c.mount(o.id,o.type);const grid=pg.getGridParam();grid.onLoadSuccess([]);
  assert(c.events.includes(opts[0].id+':clean'),'detail form not cleared');assert(c.events.includes(opts[1].id+':clean'),'report form not cleared through Form API');assert(c.events.includes(opts[2].id+':grid-clean'),'uploader grid not cleared');
 });
 check('improvement-'+page+'-empty-before-mount',()=>{
  const c=context();load(c,improvementDir+'emc-improvement.js');load(c,improvementDir+'emc-improvement-'+page+'-list.js');const pg=c.emcImprovement[page+'Page'];pg.getGridParam().onLoadSuccess([]);const opts=[pg.getFormParam(),pg.getReportFormParam(),pg.getAttachmentGridParam()];for(const o of opts)c.mount(o.id,o.type);
  for(const o of opts)assert(c.events.includes(o.id+(o.type==='uploader'?':grid-clean':':clean')),'initial clearing lost before '+o.id+' mounted');
 });
 check('improvement-'+page+'-selected-before-mount',()=>{
  const c=context();load(c,improvementDir+'emc-improvement.js');load(c,improvementDir+'emc-improvement-'+page+'-list.js');const pg=c.emcImprovement[page+'Page'];pg.getGridParam().onRowActive(0,{id:23});const opts=[pg.getFormParam(),pg.getReportFormParam(),pg.getAttachmentGridParam()];for(const o of opts)c.mount(o.id,o.type);
  assert.strictEqual(c.comps[opts[0].id].url,'/secure/emc/module/item/improvements/23');assert.strictEqual(c.comps[opts[1].id].url,'/secure/emc/module/item/improvements/23');assert.strictEqual(c.comps[opts[2].id].bizId,23);
 });
}
if(failures.length){console.log(failures.length+' regression(s) failed');process.exitCode=1;}
