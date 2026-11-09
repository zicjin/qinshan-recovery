"use strict";
const assert=require('assert'),fs=require('fs'),path=require('path'),vm=require('vm');
const root=path.resolve(process.argv[2]||path.join(__dirname,'../..'));
const common=fs.readFileSync(path.join(process.argv[3]||root,'reconstituted/framework/owned-core-web/src/main/resources/static/gikam/main/js/chunk-common.js'),'utf8');
function actualFunction(at){const a=common.indexOf('function',at),b=common.indexOf('{',a);let depth=0,quote=null,esc=false;for(let i=b;i<common.length;i++){const c=common[i];if(quote){if(esc)esc=false;else if(c==='\\')esc=true;else if(c===quote)quote=null;}else if(['"',"'",'`'].includes(c))quote=c;else if(c==='{')depth++;else if(c==='}'&&--depth===0)return common.slice(a,i+1);}throw Error('Missing bundle validator');}
const Gikam={getPageObject:x=>x,isEmpty:x=>x===null||x===undefined||x==='',propI18N:key=>key,validator:{}};
Gikam.validator.strLength=vm.runInNewContext('('+actualFunction(common.indexOf('strLength:function'))+')',{o:{a:Gikam},l:{a:{prop:key=>key}}});
const context={Gikam,IFM_CONTEXT:'',TextEncoder};vm.runInNewContext(fs.readFileSync(path.join(root,'project/src/main/resources/static/emc/module/mdm/basemdm/qc-categories/emc-qc-category.js'),'utf8'),context);
const page=context.emcQcCategory;let checks=0;
for(const fields of [page.getEditListColumns(),page.getBaseInfoFormFields()]){
 const code=fields.find(f=>f.field==='qcCategoryId');assert(code.validators&&code.validators.includes('strLength[0,16]'),'QC code must use original16 character validator');checks++;
 const byteRule=code.validators.find(v=>v==='emcQcCategoryCodeBytes');assert(byteRule,'QC code must also reject over16 UTF8 bytes');checks++;
 for(const [value,valid] of [['ABCDEFGHIJKLMNOP',true],['ABCDEFGHIJKLMNOPQ',false],['中文中文中A',true],['中文中文中文',false],['😀😀😀😀',true],['😀😀😀😀A',false],[null,true],['',true]]){
  let ok=true;for(const rule of code.validators){const i=rule.indexOf('[');const type=i<0?rule:rule.slice(0,i),arg=i<0?undefined:rule.slice(i);ok=ok&&Gikam.validator[type](value,arg).result;}
  assert.equal(ok,valid,'UI length verdict for '+JSON.stringify(value));checks++;
 }
}
console.log('PASS actual original length validator plus QC UTF8 byte validator ('+checks+' assertions)');
