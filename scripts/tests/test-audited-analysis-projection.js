'use strict';

const assert = require('assert');
const fs = require('fs');
const path = require('path');
const vm = require('vm');
const root = path.resolve(process.argv[2] || path.join(__dirname, '../..'));
const folder = 'project/src/main/resources/static/emc/module/sa/audited-analysiss/';
const bundle = fs.readFileSync(path.join(root,
  'reconstituted/framework/owned-core-web/src/main/resources/static/gikam/main/js/chunk-common.js'), 'utf8');

function extractFunction(text, position) {
  const start = text.indexOf('function', position), brace = text.indexOf('{', start);
  assert(start >= 0 && brace > start, 'actual bundle function missing');
  let depth = 0, quote = null, escaped = false;
  for (let i = brace; i < text.length; i++) {
    const ch = text[i];
    if (quote) {
      if (escaped) escaped = false;
      else if (ch === '\\') escaped = true;
      else if (ch === quote) quote = null;
      continue;
    }
    if (ch === '"' || ch === "'") { quote = ch; continue; }
    if (ch === '{') depth++;
    if (ch === '}' && --depth === 0) return text.slice(start, i + 1);
  }
  throw new Error('actual bundle function not balanced');
}

function fixture() {
  const events = [], pending = [];
  const child = {setOptions(options) { events.push(options); }, refresh() { events.push('refresh'); }};
  const context = vm.createContext({IFM_CONTEXT: '', console, Gikam: {
    getPageObject: value => value,
    getAsyncComp(id) { assert.strictEqual(id, 'emc-audited-analysis-list-run-grid');
      return {done(callback) { pending.push(() => callback(child)); }}; }
  }});
  for (const name of ['emc-audited-analysis.js', 'emc-audited-analysis-edit-list.js']) {
    vm.runInContext(fs.readFileSync(path.join(root, folder, name), 'utf8'), context, {filename: name});
  }
  return {page: context.emcAuditedAnalysis, events, ready() { pending.splice(0).forEach(run => run()); }};
}

const failures = [];
function check(name, run) {
  try { run(); console.log('PASS ' + name); }
  catch (error) { failures.push(name); console.error('FAIL ' + name + ': ' + error.message); }
}

check('project grid uses the six actual bean/mapper fields without invented projection aliases', () => {
  const columns = fixture().page.getEditListColumns();
  const supported = columns.filter(column => column.field && column.field !== 'orderCategory');
  assert.deepStrictEqual(Array.from(supported.map(column => column.field)),
    ['projNo', 'projName', 'createdByName', 'testOrgName', 'entrustOrgName', 'ext$.bizcategoryname']);
  const bean = fs.readFileSync(path.join(root,
    'project/src/main/java/com/sunwayworld/emc/module/bp/project/project/bean/EmcProjectBean.java'), 'utf8');
  for (const name of ['projNo', 'projName', 'createdByName', 'testOrgName', 'entrustOrgName']) {
    assert(bean.includes('private String ' + name + ';'), 'field absent from original ProjectBean: ' + name);
  }
  const mapper = fs.readFileSync(path.join(root,
    'project/src/main/java/com/sunwayworld/emc/module/bp/project/project/mapper/conf/pf-project-oracle-mapper.xml'), 'utf8');
  const projection = mapper.split('<select id="selectAnalysisAuditProject"')[1].split('</select>')[0];
  assert(projection.includes('SELECT A.*') && projection.includes('B.BIZCATEGORYNAME'));
  assert(!projection.includes('JOIN T_EMC_REPORT'), 'fix must not change shared query cardinality with a report join');
});

check('actual Grid header suppresses only unsupported orderCategory filter and sort', () => {
  const columns = fixture().page.getEditListColumns();
  const renderAt = bundle.indexOf('kt=function', bundle.indexOf('staticClass:"header-container"') - 500);
  assert(renderAt >= 0, 'actual original Grid header render missing');
  const header = vm.runInNewContext('(' + extractFunction(bundle, renderAt) + ')');
  const emitted = [];
  const sortAt = bundle.indexOf('sort:function(e,t,i)', renderAt);
  assert(sortAt >= 0, 'actual Grid.sort missing');
  const sort = vm.runInNewContext('(' + extractFunction(bundle, sortAt) + ')',
    {o: {a: {isMobile: () => false}}});
  const state = {propColumns: columns, filterOpen: true, filter: true, columnResizing: false,
    width: 800, height: 30, propEditorInvisible: false, showSortArrow: false,
    getFieldVisible: () => true, getHeaderKey: column => column.field || 'index',
    getCellWidth: () => '', propI18N: text => text, $xss: text => text, $i18n: text => text,
    showMobileResize: () => false, showResize: () => false,
    grid: {}, $emit: (...args) => emitted.push(args), sort,
    _l: (items, fn) => items.map(fn), _e: () => null, _s: value => String(value), _v: value => value};
  state._self = {_c(tag, data, children) {
    if (Array.isArray(data)) return {tag, data: {}, children: data};
    return {tag, data, children};
  }};
  const tree = header.call(state), editors = [];
  function walk(node) {
    if (Array.isArray(node)) { node.forEach(walk); return; }
    if (!node || typeof node !== 'object') return;
    if (node.tag === 'editor') editors.push(node.data.attrs.options.field);
    walk(node.children);
  }
  walk(tree);
  assert.deepStrictEqual(editors, ['projNo', 'projName', 'createdByName', 'testOrgName',
    'entrustOrgName', 'ext$.bizcategoryname'], 'unsupported field must have no actual header filter editor');
  const unsupported = columns.find(column => column.field === 'orderCategory');
  assert(unsupported, 'retain the original display column until its model contract is decided');
  sort.call(state, {}, unsupported, 0);
  assert.strictEqual(emitted.length, 0, 'unsupported column must not emit invalid SQL ordering');
  sort.call(state, {}, columns.find(column => column.field === 'projNo'), 0);
  assert.deepStrictEqual(emitted, [['sortListGrid', 'projNo', 'asc']]);
});

check('actual Grid filter keeps CISC and all supported fields while retaining request scope', () => {
  const f = fixture(), grid = f.page.editPage.getProjGridParam();
  const at = bundle.indexOf('setHeadRequestData:function(e)');
  assert(at >= 0, 'actual Grid header filter method missing');
  const update = vm.runInNewContext('(' + extractFunction(bundle, at) + ')',
    {v: {a: (target, key, value) => Object.assign(target, {[key]: value})}});
  const parameters = [], pages = [];
  for (const column of grid.columns.filter(column => column.field && column.filter !== false)) {
    update.call({fieldOptions: column, filedFilter: undefined, filter: true,
      filterOriginalData: {}, updatePageNum: value => pages.push(value),
      updateHeadRequestData: value => parameters.push(value)}, {value: 'no-match', match: 'CISC'});
  }
  assert.deepStrictEqual(parameters.map(value => Object.keys(value)[0]),
    ['projNo_CISC', 'projName_CISC', 'createdByName_CISC', 'testOrgName_CISC',
      'entrustOrgName_CISC', 'ext$.bizcategoryname_CISC']);
  assert(pages.every(value => value === 1), 'real filter resets page cursor normally');
  assert.deepStrictEqual(JSON.parse(JSON.stringify(grid.requestData)), {status_SEQ: 'auditQuery', org_filter: '1'});
  assert.strictEqual(grid.url, '/secure/emc/module/bp/project/projects/analysis-projects/queries');
  assert.strictEqual(grid.page, undefined, 'do not change original pagination configuration');
});

check('original onRowActive scopes run query to the selected project primary key', () => {
  const f = fixture(), grid = f.page.editPage.getProjGridParam();
  grid.onRowActive(0, {id: 812, projNo: 'fixture-project', ext$: {bizcategoryname: 'fixture-category'}});
  assert.strictEqual(f.events.length, 0, 'use the original async child readiness boundary');
  f.ready();
  assert.strictEqual(f.events.length, 2);
  assert.strictEqual(f.events[0].requestData.projId, 812, 'project query exposes id, not projId');
  assert.strictEqual(f.events[0].requestData.orgId_filter, '1');
  assert.strictEqual(f.events[0].requestData.scheme_only, '');
  assert.strictEqual(f.events[0].url, '/secure/emc/module/bp/result/runs/queries/undoable');
  assert.strictEqual(f.events[1], 'refresh');
});

console.log('audited analysis projection: ' + (4 - failures.length) + '/4 passed');
if (failures.length) process.exitCode = 1;
