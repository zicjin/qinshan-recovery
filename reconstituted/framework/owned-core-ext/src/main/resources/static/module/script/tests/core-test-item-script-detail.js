//@ sourceURL=core-item-script-detail.js
Gikam.jQuery.fn.extend({
    "insertValueInFocus" : function(value) {

        var dthis = Gikam.jQuery(this)[0]; // 将jQuery对象转换为DOM元素

        // IE下
        if (document.selection) {

            Gikam.jQuery(dthis).focus(); // 输入元素textara获取焦点
            var fus = document.selection.createRange();// 获取光标位置
            fus.text = value.text; // 在光标位置插入值
            Gikam.jQuery(dthis).focus(); // /输入元素textara获取焦点

            fus.moveStart('character', 0);
            fus.collapse(true);
            fus.select();

        }
        // 火狐下标准
        else if (dthis.selectionStart || dthis.selectionStart == '0') {

            var start = dthis.selectionStart;
            var end = dthis.selectionEnd;
            var top = dthis.scrollTop;

            // 以下这句，应该是在焦点之前，和焦点之后的位置，中间插入我们传入的值
            dthis.value = dthis.value.substring(0, start) + value.text + dthis.value.substring(end, dthis.value.length);

            Gikam.jQuery(dthis).focus();

            var focusPosition = start + value.text.length;
            dthis.setSelectionRange(focusPosition, focusPosition);

        }

        // 在输入元素textara没有定位光标的情况
        else {
            this.value += value.text;
            this.focus();
        }

        return Gikam.jQuery(this);
    }
});

coreTest.itemScriptDetailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnGroup',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {

                    var saveData = {};

                    var formData = _this.form.getData();
                    saveData.id = formData.id;
                    saveData.script = formData.script;

                    Gikam.put(coreTest.baseUrl + '/instant', Gikam.getJsonWrapper({}, [ 'coreItemServiceImpl', [ saveData ] ])).done(function() {
                        Gikam.getLastModal().close();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    getScriptTabParam : function() {
        var _this = this;
        return {
            type : 'tab',
            shrink : false,
            panels : [ {
                title : 'CORE.MODULE.SCRIPT.TESTS.TAB.SCRIPT',
                items : [ {
                    type : 'btnGroup',
                    margin : '10px 10px 0px 5px',
                    items : [ {
                        type : 'button',
                        id : 'core-test-item-script-detail-add-button',
                        text : '+',
                        onClick : function() {
                            _this.insertCalcFormula("+");
                        },
                        onRendered : function() {
                            Gikam.jQuery('#core-test-item-script-detail-add-button').find('.button').css('font-size', '20px').css('color', '#007aff');
                        }
                    }, {
                        type : 'button',
                        text : '-',
                        id : 'core-test-item-script-detail-sub-button',
                        onClick : function() {
                            _this.insertCalcFormula("-");
                        },
                        onRendered : function() {
                            Gikam.jQuery('#core-test-item-script-detail-sub-button').find('.button').css('font-size', '20px').css('color', '#007aff');
                        }
                    }, {
                        type : 'button',
                        text : '×',
                        id : 'core-test-item-script-detail-mul-button',
                        onClick : function() {
                            _this.insertCalcFormula("*");
                        },
                        onRendered : function() {
                            Gikam.jQuery('#core-test-item-script-detail-mul-button').find('.button').css('font-size', '20px').css('color', '#007aff');
                        }
                    }, {
                        type : 'button',
                        text : '÷',
                        id : 'core-test-item-script-detail-div-button',
                        onClick : function() {
                            _this.insertCalcFormula("/");
                        },
                        onRendered : function() {
                            Gikam.jQuery('#core-test-item-script-detail-div-button').find('.button').css('font-size', '20px').css('color', '#007aff');
                        }
                    } ]
                }, {
                    type : 'form',
                    columns : 1,
                    id : 'core-test-item-script-detail-form',
                    url : coreTest.baseUrl + '/items/' + _this.param.itemId,
                    titleWidth : 5,
                    fields : [ {
                        field : 'script',
                        type : 'textarea',
                        height : 200,
                        onChange : function(field, value) {
                            _this.loadTestData();
                        }
                    } ],
                    autoSave : false,
                    onRendered : function() {
                        _this.form = this;
                    },
                    onLoadSuccess : function(data) {
                        _this.loadTestData();
                    }
                } ]
            } ]
        };
    },

    loadTestData : function() {
        var script = Gikam.getComp('core-test-item-script-detail-form').getData().script;

        if (!Gikam.isEmpty(script)) {
            var regex = /\[(.+?)\]/g;
            var selectorArray = script.match(regex);

            if (Gikam.isEmpty(selectorArray)) {
                return;
            }

            var testData = [];
            var distinctSelectorArray = [];

            selectorArray.map(function(selector) {
                if (distinctSelectorArray.indexOf(selector) < 0) {
                    distinctSelectorArray.push(selector);
                    testData.push({
                        selector : selector
                    });
                }
            });

            Gikam.getComp('core-test-item-script-detail-test-grid').loadData(testData);

        }

    },

    getFuncGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            shrink : false,
            panels : [ {
                title : 'CORE.MODULE.SCRIPT.TESTS.TAB.FUNC',
                items : [ {
                    type : 'grid',
                    titleAlign : 'left',
                    generalButtonGroup : false,
                    id : 'core-test-item-script-detail-func-grid',
                    columns : [ {
                        field : 'func',
                        title : 'T_CORE_ITEM.FUNC'
                    } ],
                    page : false,
                    onRendered : function() {
                        this.loadData([ {
                            func : 'if () else'
                        }, {
                            func : 'max()'
                        }, {
                            func : 'min()'
                        }, {
                            func : 'stdev()'
                        }, {
                            func : 'avg()'
                        } ]);

                        Gikam.jQuery('#core-test-item-script-detail-func-grid').find('.toolbar,.grid-header').css('display', 'none');

                    },
                    onCellClick : function(field, row) {
                        if (_this.param.readonly !== 'true') {
                            _this.insertCalcFormula(row.func);
                        }
                    }
                } ]
            } ]
        };
    },

    insertCalcFormula : function(content) {
        var _this = this;

        if (content.indexOf('if ()') >= 0) {
            content = 'if () {\r\n\r\n} else {\r\n\r\n}';
        }

        var scriptTextArea = Gikam.jQuery('#core-test-item-script-detail-form').find("textarea[name='script']");
        scriptTextArea.insertValueInFocus({
            "text" : content
        });

        _this.form.setData({
            script : scriptTextArea.val()
        });

        _this.loadTestData();

    },

    getSelectorGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            shrink : false,
            panels : [ {
                title : 'CORE.MODULE.SCRIPT.TESTS.TAB.SELECTOR',
                items : [ {
                    type : 'grid',
                    titleAlign : 'left',
                    generalButtonGroup : false,
                    id : 'core-test-item-script-detail-selector-grid',
                    page : false,
                    columns : [ {
                        field : 'selector',
                        title : 'T_CORE_ITEM.SELECTOR'
                    } ],
                    onRendered : function() {
                        this.loadData([ {
                            type : 'item',
                            selector : Gikam.propI18N('T_CORE_ITEM.ITEM_SELECTOR')
                        }, {
                            type : 'testItem',
                            selector : Gikam.propI18N('T_CORE_ITEM.TEST_ITEM_SELECTOR')
                        } ]);

                        Gikam.jQuery('#core-test-item-script-detail-selector-grid').find('.toolbar,.grid-header').css('display', 'none');

                    },
                    onCellClick : function(field, row) {
                        // 加载选择器页面
                        _this.loadSelectorPage(row.type);

                    }
                } ]
            } ]
        };
    },

    loadSelectorPage : function(selector) {
        var _this = this;

        var url = coreTest.baseUrl + '/page/item-script-selector';

        Gikam.create('modal', {
            width : 600,
            height : 400,
            title : 'CORE.MODULE.SCRIPT.TESTS.MODAL.' + selector.toUpperCase() + '_SELECTOR',
            url : url + Gikam.param({
                type : selector,
                testId : _this.param.testId,
                itemId : _this.param.itemId
            }),
            onAfterClose : function(expression) {
                if (!Gikam.isEmpty(expression)) {
                    _this.insertCalcFormula(expression);

                }
            }
        });

    },

    getTestGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            shrink : false,
            panels : [ {
                title : 'CORE.MODULE.SCRIPT.TESTS.TAB.TEST',
                items : [ {
                    type : 'grid',
                    titleAlign : 'left',
                    generalButtonGroup : false,
                    id : 'core-test-item-script-detail-test-grid',
                    page : false,
                    columns : [ {
                        field : 'selector',
                        width : 284,
                        title : 'T_CORE_ITEM.ITEM'
                    }, {
                        field : 'value',
                        title : 'T_CORE_ITEM.VALUE',
                        editor : true,
                        type : 'number'
                    } ],
                    toolbar : [ {
                        type : 'button',
                        icon : 'pass',
                        text : 'CORE.MODULE.SCRIPT.TESTS.BUTTON.TEST',
                        onClick : function() {
                            var selectorData = Gikam.getComp('core-test-item-script-detail-test-grid').getData();

                            var script = _this.form.getData().script;

                            if (!Gikam.isEmpty(selectorData) && !Gikam.isEmpty(script)) {

                                var valiateFlag = true;
                                Gikam.each(selectorData, function(index, selector) {
                                    if (Gikam.isEmpty(selector.value)) {
                                        Gikam.alert('CORE.MODULE.SCRIPT.TESTS.TIP.VALUE_EMPTY');
                                        valiateFlag = false;
                                        return false;
                                    }
                                });

                                if (!valiateFlag) {
                                    return;
                                }

                                Gikam.postText(Gikam.printf(coreTest.baseUrl + '/items/{itemId}/action/test-script', {
                                    itemId : _this.param.itemId
                                }), Gikam.getJsonWrapper({
                                    script : script
                                }, [ '', selectorData ])).done(function(result) {
                                    Gikam.alert('result=' + (Gikam.isEmpty(result) ? 'null' : result));
                                });

                            }

                        }
                    } ]
                } ]
            } ]
        };
    },

    createPage : function() {
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ {
                    type : 'layout',
                    west : {
                        width : 131,
                        items : [ {
                            type : 'layout',
                            north : {
                                height : '50%',
                                items : [ this.getFuncGridParam() ]
                            },
                            center : {
                                items : [ this.getSelectorGridParam() ]
                            }
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'layout',
                            north : {
                                height : 300,
                                items : [ this.getScriptTabParam() ]
                            },
                            center : {
                                items : [ this.getTestGridParam() ]
                            }
                        } ]
                    }

                } ]
            },
            south : {
                height : 50,
                padding : '10px 10px 10px 800px',
                items : [ this.getBtnToolbar() ]
            }
        });
    },

    init : function(param) {
        this.param = param;

        this.createPage();
    }
}
