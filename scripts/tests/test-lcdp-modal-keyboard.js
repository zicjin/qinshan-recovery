'use strict';

const assert = require('assert');
const fs = require('fs');
const path = require('path');
const vm = require('vm');

const root = path.resolve(__dirname, '../..');
const sourcePath = path.resolve(process.argv[2] || path.join(root,
  'project/src/main/resources/lcdp-source/emc/equiptInOut/page/equiptInOutButtonPage.js'));
const bundlePath = path.join(root,
  'reconstituted/framework/owned-core-web/src/main/resources/static/gikam/main/js/chunk-common.js');
const pageSource = fs.readFileSync(sourcePath, 'utf8');
const bundle = fs.readFileSync(bundlePath, 'utf8');

// Execute the shipped Modal helper bodies, not an independently implemented
// keyboard manager. These four functions contain no regex/template literals.
function extractFunction(text, position) {
  const start = text.indexOf('function', position);
  assert(start >= position, 'bundle function missing');
  const brace = text.indexOf('{', start);
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
  throw new Error('unbalanced bundle function');
}

const ftStart = bundle.indexOf('ft=function');
assert(ftStart >= 0, 'actual Modal ft keyboard callback missing');
const bindStart = bundle.indexOf('key:"bindButtonEnter",value:', ftStart);
assert(bindStart > ftStart, 'actual Modal.bindButtonEnter missing');
const helperBodies = {ft: extractFunction(bundle, ftStart)};
for (const name of ['onBeforeClose', 'bindButtonEnter', 'unBindButtonEnter', 'close']) {
  const marker = 'key:"' + name + '",value:';
  const at = bundle.indexOf(marker, ftStart);
  assert(at >= ftStart && at < bindStart + 1000, 'actual Modal.' + name + ' missing');
  helperBodies[name] = extractFunction(bundle, at + marker.length);
}

function fixture(previousHandler) {
  const listeners = new Set(), elements = new Map(), comps = new Map();
  const modals = [], requests = [], pageLoads = [], validations = [];
  let onkeydown = previousHandler, globalAssignments = 0, valid = true;
  const document = {
    addEventListener(type, fn) { assert.strictEqual(type, 'keydown'); listeners.add(fn); },
    removeEventListener(type, fn) { assert.strictEqual(type, 'keydown'); listeners.delete(fn); },
    getElementById(id) { return elements.get(id); }
  };
  Object.defineProperty(document, 'onkeydown', {
    get() { return onkeydown; },
    set(fn) { globalAssignments++; onkeydown = fn; }
  });
  function dispatch(keyCode) {
    const event = {keyCode, stopped: false, prevented: false,
      stopPropagation() { this.stopped = true; },
      preventDefault() { this.prevented = true; }};
    if (document.onkeydown) document.onkeydown(event);
    for (const fn of Array.from(listeners)) fn(event);
    return event;
  }
  const Gikam = {
    isFunction(value) { return typeof value === 'function'; },
    getComp(id) { return comps.get(id); },
    getLastModal() { return modals[modals.length - 1]; },
    getJsonWrapper(params, body) { return {p: params, b: body}; },
    post(url, body) {
      requests.push({url, body});
      return {done(fn) { fn({}); return this; }, always(fn) { fn(); return this; }};
    },
    create(type, options) {
      if (type === 'modal') {
        const modal = {options, listeners: {beforeClose() { return true; }},
          elementIds: [], componentIds: [], beforeCloseCalls: 0,
          window: {$dom: {}, showMask() {}, closeMask() {}}};
        for (const name of ['onBeforeClose', 'bindButtonEnter', 'unBindButtonEnter', 'close']) {
          modal[name] = vm.runInContext('(' + helperBodies[name] + ')', context);
        }
        // The Vue/DOM child is the only lifecycle stand-in. It invokes the
        // actual Modal.beforeClose hook registered by actual bindButtonEnter.
        modal.model = {$children: [{close() {
          modal.beforeCloseCalls++;
          if (modal.listeners.beforeClose() === false) return;
          modal.elementIds.forEach(id => elements.delete(id));
          modal.componentIds.forEach(id => comps.delete(id));
          const index = modals.indexOf(modal);
          if (index >= 0) modals.splice(index, 1);
        }}]};
        modals.push(modal);
        return modal;
      }
      if (type === 'layout') {
        const modal = Gikam.getLastModal();
        const buttons = options.center.items.flatMap(item => item.items || []);
        modal.buttons = buttons;
        for (const button of buttons) {
          if (button.id) {
            elements.set(button.id, {click() { button.onClick(); }});
            modal.elementIds.push(button.id);
          }
        }
        return {options: {center: {$dom: {}}}};
      }
      assert.strictEqual(type, 'form', 'unexpected page component');
      const form = {options, validate() { validations.push(options.id); return valid; },
        getData() { return {username: 'fixture-user', password: 'fixture-password', userid: 'fixture-id'}; },
        getField() { return {focus() {}}; }};
      comps.set(options.id, form);
      Gikam.getLastModal().componentIds.push(options.id);
      if (options.onRendered) options.onRendered.call(form);
      return form;
    }
  };
  const context = vm.createContext({document, console, Gikam, o: {a: Gikam},
    CURRENT_WINDOW: {getPageParam() { return {}; }},
    workspace: {window: {loadPage(resourcePath, params) { pageLoads.push({resourcePath, params}); }}},
    setTimeout(fn) { fn(); }, window: {}});
  vm.runInContext('var ft = (' + helperBodies.ft + ');', context, {filename: bundlePath});
  vm.runInContext(pageSource, context, {filename: sourcePath});
  return {context, document, dispatch, listeners, modals, requests, pageLoads, validations,
    setValid(value) { valid = value; },
    globalAssignments() { return globalAssignments; },
    open(direction) {
      context[direction === 'in' ? 'button_2_onclick' : 'button_1_onclick']();
      return Gikam.getLastModal();
    },
    otherModal() { return Gikam.create('modal', {title: 'another original page'}); }};
}

const failures = [];
function check(name, run) {
  try { run(); console.log('PASS ' + name); }
  catch (error) { failures.push(name); console.error('FAIL ' + name + ': ' + error.message); }
}

check('page-load-does-not-assign-document-onkeydown', () => {
  const f = fixture();
  assert.strictEqual(f.globalAssignments(), 0, 'page load assigns a stale global keyboard handler');
  assert.strictEqual(f.document.onkeydown, undefined);
});

check('existing-unrelated-document-onkeydown-survives-load-open-close', () => {
  let called = 0;
  const previous = () => { called++; };
  const f = fixture(previous);
  assert.strictEqual(f.document.onkeydown, previous, 'page overwrites unrelated onkeydown');
  f.open('out').close();
  f.otherModal();
  f.dispatch(13);
  assert.strictEqual(called, 1);
  assert.strictEqual(f.globalAssignments(), 0);
});

check('login-enter-executes-real-confirm-validation-and-request-branch', () => {
  const f = fixture();
  const modal = f.open('out');
  const event = f.dispatch(13);
  assert.strictEqual(f.validations.length, 1, 'real confirm must call form.validate once');
  assert.strictEqual(f.requests.length, 1);
  assert.strictEqual(f.requests[0].url,
    'emc.equiptInOut.server.LcdpEmcEquiptInoutService.checkPassword');
  assert.strictEqual(f.requests[0].body.p.username, 'fixture-user');
  assert.strictEqual(f.requests[0].body.p.password, 'fixture-password');
  assert.strictEqual(f.pageLoads.length, 1);
  assert.strictEqual(f.pageLoads[0].resourcePath, 'emc.equiptInOut.page.equiptInOutEditPage');
  assert.strictEqual(f.pageLoads[0].params.type, 'out');
  assert(event.stopped && event.prevented, 'actual ft must handle login Enter');
  assert.strictEqual(modal.beforeCloseCalls, 1);
  assert.strictEqual(f.listeners.size, 0, 'successful confirm must unbind on modal close');
});

check('invalid-validation-prevents-request-for-enter-and-normal-click', () => {
  const f = fixture();
  f.setValid(false);
  const modal = f.open('in');
  f.dispatch(13);
  assert.strictEqual(f.validations.length, 1);
  assert.strictEqual(f.requests.length, 0);
  assert.strictEqual(f.pageLoads.length, 0);
  assert.strictEqual(f.listeners.size, 1);
  assert.strictEqual(f.dispatch(65).prevented, false, 'non-Enter key must pass through');
  assert.strictEqual(f.validations.length, 1);
  modal.buttons.find(button => button.text === 'GIKAM.BUTTON.CONFIRM').onClick();
  assert.strictEqual(f.validations.length, 2);
  assert.strictEqual(f.requests.length, 0);
  modal.close();
});

check('normal-confirm-click-still-executes-real-confirm', () => {
  const f = fixture();
  const modal = f.open('in');
  modal.buttons.find(button => button.text === 'GIKAM.BUTTON.CONFIRM').onClick();
  assert.strictEqual(f.validations.length, 1);
  assert.strictEqual(f.requests.length, 1);
  assert.strictEqual(f.pageLoads[0].params.type, 'in');
  assert.strictEqual(f.listeners.size, 0);
});

check('cancel-unbinds-before-other-modal-enter-with-no-stale-validate', () => {
  const f = fixture();
  const modal = f.open('out');
  const listenersWhileOpen = f.listeners.size;
  modal.buttons.find(button => button.text === 'GIKAM.BUTTON.CANCEL').onClick();
  f.otherModal();
  assert.doesNotThrow(() => f.dispatch(13), 'other page modal must not dereference old form.validate');
  assert.strictEqual(listenersWhileOpen, 1, 'actual Modal helper must own one listener');
  assert.strictEqual(f.listeners.size, 0);
  assert.strictEqual(modal.addButtonEnterHandler, null);
  assert.strictEqual(f.validations.length, 0);
  assert.strictEqual(f.requests.length, 0);
});

check('actual-modal-binding-is-idempotent-and-reopening-does-not-leak', () => {
  const f = fixture();
  const modal = f.open('out');
  const button = modal.buttons.find(item => item.text === 'GIKAM.BUTTON.CONFIRM');
  modal.bindButtonEnter(button.id);
  assert.strictEqual(f.listeners.size, 1);
  modal.close();
  assert.strictEqual(f.listeners.size, 0);
  f.open('in');
  f.dispatch(13);
  assert.strictEqual(f.validations.length, 1);
  assert.strictEqual(f.requests.length, 1);
  assert.strictEqual(f.listeners.size, 0);
});

console.log('source=' + sourcePath);
console.log('helpers=actual chunk-common Modal.bindButtonEnter/unBindButtonEnter/onBeforeClose/close + ft');
console.log('scope=VM source regression; DOM/Vue lifecycle and HTTP collaborators simulated; no real HTTP');
console.log('result=' + (7 - failures.length) + '/7 passed');
if (failures.length) process.exitCode = 1;
