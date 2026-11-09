"use strict";

const assert = require("assert");
const fs = require("fs");
const path = require("path");
const vm = require("vm");
const root = path.resolve(process.argv[2] || path.join(__dirname, "../.."));
const source = fs.readFileSync(path.join(root,
    "project/src/main/resources/static/emc/module/qrcode/hm.js"), "utf8");
const start = source.indexOf("(function(){var e=mt.lang,a=mt.d,b=h.S,k={Ha:");
const endMarker = "return h.Ia=k})();";
const end = source.indexOf(endMarker, start) + endMarker.length;
assert(start >= 0 && end > start, "Original click-listener module must be present");
let passed = 0;
const failures = [];

function check(name, run) {
    try { run(); passed++; console.log("PASS " + name); }
    catch (error) { failures.push(name); console.log("FAIL " + name + ": " + error.message); }
}

function fixture() {
    const paths = [], callbacks = [], selectors = [], writes = [];
    const context = {
        h: {S: {H(value) { selectors.push(value); return ["#button"] ; }}},
        mt: {
            lang: {J: value => value && typeof value === "object"},
            d: {Wa(target) { paths.push(target); return ["button[1]"]; }}
        }
    };
    vm.runInNewContext(source.slice(start, end), context, {filename: "original-hm-click-listener.js"});
    const contract = {P: "HM_fix", ba(value) { callbacks.push(value); }};
    const listener = context.h.Ia.Ha(contract, "original-events");
    function element(attributes) {
        return {
            nodeType: 1,
            getAttribute(name) { return attributes[name] || null; },
            removeAttribute(name) { delete attributes[name]; writes.push(name); }
        };
    }
    return {listener, paths, callbacks, selectors, writes, element};
}

for (const [name, target] of [
    ["document", {nodeType: 9}],
    ["window", {}],
    ["text node", {nodeType: 3, parentElement: {nodeType: 1}}],
    ["null target", null],
    ["element without attribute capability", {nodeType: 1}],
    ["non-element attribute lookalike", {nodeType: 9, getAttribute() { throw new Error("not an Element"); }}]
]) {
    check("actual telemetry listener ignores " + name, () => {
        const test = fixture();
        test.listener({target, clientX: 12, clientY: 34});
        assert.equal(test.paths.length, 0);
        assert.equal(test.callbacks.length, 0);
        assert.equal(test.selectors.length, 0, "Non-Element must not enter click-tracking lookup");
    });
}

check("actual telemetry listener preserves real Element click tracking", () => {
    const test = fixture(), target = test.element({});
    test.listener({target, clientX: 12, clientY: 34});
    assert.equal(test.paths[0], target);
    assert.equal(test.selectors[0], "original-events");
    assert.equal(test.callbacks.length, 1);
    assert.equal(test.callbacks[0]["button[1]"], "");
    assert.equal(test.callbacks[0].button, "");
    assert.equal(test.writes.length, 0);
});

check("actual telemetry listener preserves legacy srcElement clicks", () => {
    const test = fixture(), target = test.element({});
    test.listener({srcElement: target, clientX: 12, clientY: 34});
    assert.equal(test.paths[0], target);
    assert.equal(test.callbacks.length, 1);
});

check("actual telemetry listener retains HM_fix matching-coordinate suppression", () => {
    const test = fixture(), attributes = {HM_fix: "12:34"}, target = test.element(attributes);
    test.listener({target, clientX: 12, clientY: 34});
    assert.equal(attributes.HM_fix, undefined);
    assert.deepEqual(test.writes, ["HM_fix"]);
    assert.equal(test.paths.length, 0);
    assert.equal(test.callbacks.length, 0);
});

check("actual telemetry listener tracks Element when HM_fix coordinates differ", () => {
    const test = fixture(), attributes = {HM_fix: "1:2"}, target = test.element(attributes);
    test.listener({target, clientX: 12, clientY: 34});
    assert.equal(attributes.HM_fix, "1:2");
    assert.equal(test.paths[0], target);
    assert.equal(test.callbacks.length, 1);
});

console.log(JSON.stringify({passed, failed: failures}));
if (failures.length) process.exitCode = 1;
