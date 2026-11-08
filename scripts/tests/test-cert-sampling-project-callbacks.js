const fs = require('fs');
const path = require('path');
const vm = require('vm');
const assert = require('assert');
const resources = path.resolve(process.argv[2] || path.join(__dirname, '../../project/src/main/resources'));
const failures = [];
function check(name, run) {
    try { run(); console.log('PASS ' + name); }
    catch (error) { failures.push(name); console.log('FAIL ' + name + ': ' + error.message); }
}
function fixture() {
    const components = {}, pending = {}, events = [];
    const context = {IFM_CONTEXT: '', workspace: {window: {$dom: {}}}, console};
    context.Gikam = {
        width: {XL: 100}, getPageObject: value => value,
        isEmpty: value => value == null || (Array.isArray(value) && value.length === 0),
        getComp: id => components[id],
        getAsyncComp: id => ({done(callback) {
            if (components[id]) callback(components[id]);
            else (pending[id] || (pending[id] = [])).push(callback);
        }}),
        printf: (value, params) => value.replace(/\{id\}/g, params.id),
        cleanCompData: ids => events.push(['clean', Array.from(ids)]),
        alert: value => events.push(['alert', value]),
        preInsert(options) { context.modal = options; return {done(callback) { context.afterInsert = callback; }}; },
        create: () => {},
    };
    context.setTimeout = () => { throw new Error('form initialization must use supplied ready form, not a timer'); };
    context.mount = (id, extra = {}) => {
        const component = Object.assign({
            refresh: options => events.push(['refresh', id, options.url]),
            setOptions: options => events.push(['options', id, options.bizId]),
            cleanData: () => events.push(['clean-one', id]),
        }, extra);
        components[id] = component;
        (pending[id] || []).forEach(callback => callback(component));
        delete pending[id];
        return component;
    };
    vm.createContext(context);
    context.load = relative => vm.runInContext(fs.readFileSync(path.join(resources, relative), 'utf8'), context, {filename: relative});
    return Object.assign(context, {components, pending, events});
}
function certificate() {
    const c = fixture();
    c.emcCert = {baseUrl: '/certs', empBaseUrl: '/emps', getEditListColumns: () => [],
        getCertTestMethodColumns: () => [], getEmpEditListColumns: () => []};
    c.load('static/emc/module/mdm/basemdm/certs/emc-cert-edit-list.js');
    return c;
}
check('certificate actual onRowActive tolerates delayed declared children and ignores removed tabs', () => {
    const c = certificate(), pg = c.emcCert.editPage;
    const childOptions = pg.getOtherGridParam();
    const declared = childOptions.panels.flatMap(panel => panel.items).map(item => item.id);
    assert.deepStrictEqual(Array.from(declared), ['emc-cert-method-edit-list-grid', 'emc-cert-detail-uploader']);
    const grid = pg.getGridParam().panels[0].items[0];
    grid.onRowActive(0, {id: 31});
    c.mount('emc-cert-method-edit-list-grid');
    c.mount('emc-cert-detail-uploader');
    assert.deepStrictEqual(c.events, [
        ['refresh', 'emc-cert-method-edit-list-grid', '/certs/31/cert-test-methods/queries'],
        ['options', 'emc-cert-detail-uploader', 31],
    ]);
    assert(!Object.keys(c.pending).some(id => /monitor-ability|cert-log/.test(id)), 'removed business tabs must not be recreated or awaited');
});
check('certificate latest selected row wins when children mount late', () => {
    const c = certificate(), pg = c.emcCert.editPage;
    pg.onEmpCertActive({id: 31}); pg.onEmpCertActive({id: 32});
    c.mount('emc-cert-method-edit-list-grid'); c.mount('emc-cert-detail-uploader');
    assert.deepStrictEqual(c.events, [
        ['refresh', 'emc-cert-method-edit-list-grid', '/certs/32/cert-test-methods/queries'],
        ['options', 'emc-cert-detail-uploader', 32],
    ]);
});
check('certificate changed employee invalidates queued prior certificate refresh', () => {
    const c = certificate(), pg = c.emcCert.editPage;
    c.mount('emc-cert-edit-list-grid');
    pg.onEmpCertActive({id: 31}); pg.onEmpActive({id: 'another-employee'});
    c.mount('emc-cert-method-edit-list-grid'); c.mount('emc-cert-detail-uploader');
    assert(!c.events.some(event => event[2] === '/certs/31/cert-test-methods/queries' || event[2] === 31));
});
function sampling() {
    const c = fixture();
    c.load('static/emc/module/mdm/bizmdm/samplenumbers/emc-samplenumber.js');
    c.load('static/emc/module/mdm/bizmdm/samplenumbers/emc-samplenumber-edit-list.js');
    return c;
}
check('sampling add opens only own modal, initializes selected report through ready form', () => {
    const c = sampling();
    c.mount('emc-report-config-detail-line-grid', {getSelections: () => [{id: 91}]});
    let refreshed = 0, initialized;
    c.mount('emc-samplenumber-edit-list-grid', {refresh: () => refreshed++});
    c.emcSamplenumber.editPage.getGridParam().toolbar[0].onClick();
    assert.strictEqual(c.modal.url, '/secure/emc/module/mdm/bizmdm/samplenumbers');
    const form = {setData(data, dirty) { initialized = {id: data.reportconfiglineid, dirty}; }};
    c.modal.onFormRendered.call(form);
    assert.deepStrictEqual(initialized, {id: 91, dirty: false});
    assert.strictEqual(refreshed, 0, 'opening modal must not submit/refresh as if persisted');
    c.afterInsert(123);
    assert.strictEqual(refreshed, 1, 'successful own insert refreshes own grid');
});
check('sampling add with no report selection is controlled and creates no modal', () => {
    const c = sampling();
    c.mount('emc-report-config-detail-line-grid', {getSelections: () => []});
    c.emcSamplenumber.editPage.getGridParam().toolbar[0].onClick();
    assert.strictEqual(c.modal, undefined);
    assert.deepStrictEqual(c.events, [['alert', 'GIKAM.TIP.CHOOSE_ONE_ITEM']]);
});
check('project-info empty query clears original detail grids without removing undeclared panel', () => {
    const c = fixture(); c.emcProjectInfo = {baseUrl: '/projects', getEditListColumns: () => []};
    c.load('static/emc/module/bp/edit/project-infos/emc-project-info-edit-list.js');
    let removed = 0;
    c.mount('emc-project-info-edit-list-base-info-tab', {removePanel(id) { removed++; throw new Error('can not find ' + id); }});
    let active;
    const pg = c.emcProjectInfo.editPage;
    pg.getGridParam().onLoadSuccess.call({activeRowByIndex: i => { active = i; }}, []);
    assert.strictEqual(removed, 0);
    assert.deepStrictEqual(c.events, [['clean', ['emc-project-info-edit-list-order-grid', 'emc-project-info-edit-list-order-item-group-grid', 'emc-project-info-edit-list-director-grid']]]);
    pg.getGridParam().onLoadSuccess.call({activeRowByIndex: i => { active = i; }}, [{id: 1}]);
    assert.strictEqual(active, 0, 'nonempty query retains default activation');
});
if (failures.length) { console.log(failures.length + ' regression(s) failed'); process.exitCode = 1; }
