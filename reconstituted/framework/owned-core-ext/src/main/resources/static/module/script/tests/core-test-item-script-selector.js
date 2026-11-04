//@ sourceURL=core-item-script-selector.js
coreTest.itemScriptSelectorPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {

                    var validateForms = [];

                    if (_this.param.type === 'testItem') {
                        validateForms = [ 'core-test-item-script-selector-test-form', 'core-test-item-script-selector-item-form' ];
                    } else if (_this.param.type === 'item') {
                        validateForms = [ 'core-test-item-script-selector-item-form' ];
                    }

                    var validateFlag = true;

                    Gikam.each(validateForms, function(index, formId) {
                        if (!Gikam.getComp(formId).validate()) {
                            validateFlag = false;
                            return false;
                        }
                    });

                    if (validateFlag) {
                        Gikam.getLastModal().close(_this.form.getData().expression);
                    }

                }
            } ]
        }
    },

    getFormParam : function() {
        var _this = this;
        return {
            type : 'form',
            columns : 1,
            id : 'core-test-item-script-selector-form',
            fields : [ {
                field : 'expression',
                title : 'T_CORE_ITEM.EXPRESSION',
                readonly : true
            } ],
            onRendered : function() {
                _this.form = this;
            }
        };
    },

    getTabParam : function() {
        var _this = this;
        return {
            type : 'tab',
            shrink : false,
            onRendered : function() {
                if (_this.param.type === 'item') {
                    this.removePanel('core-test-item-script-selector-test-panel');
                    this.showPanel('core-test-item-script-selector-item-panel');
                } else if (_this.param.type === 'testItem') {
                    this.showPanel('core-test-item-script-selector-test-panel');
                }
            },
            panels : [ {
                id : 'core-test-item-script-selector-test-panel',
                title : 'CORE.MODULE.SCRIPT.TESTS.TAB.TEST',
                items : [ {
                    type : 'form',
                    columns : 2,
                    id : 'core-test-item-script-selector-test-form',
                    fields : [ {
                        field : 'testName',
                        title : 'T_CORE_TEST.TESTNAME',
                        type : 'select',
                        colspan : 2,
                        validators : [ 'notEmpty' ],
                        category : 'test',
                        firstBlank : false,
                        onChange : function(field, value, rowIndex, oldValue, text) {
                            _this.fillItemSelectOption(value);
                            _this.testName = text;
                            _this.setExpression();
                        }
                    } ]
                } ]
            }, {
                id : 'core-test-item-script-selector-item-panel',
                title : 'CORE.MODULE.SCRIPT.TESTS.TAB.ITEM',
                items : [ {
                    type : 'form',
                    columns : 2,
                    id : 'core-test-item-script-selector-item-form',
                    fields : [ {
                        field : 'itemName',
                        title : 'T_CORE_ITEM.ITEM',
                        type : 'select',
                        colspan : 2,
                        validators : [ 'notEmpty' ],
                        firstBlank : false,
                        onChange : function(field, value, rowIndex, oldValue, text) {
                            _this.setExpression();
                        }
                    }, {
                        field : 'repeatNoCategory',
                        title : 'T_CORE_ITEM.REPEATNO',
                        type : 'select',
                        validators : [ 'notEmpty' ],
                        firstBlank : false,
                        items : [ {
                            text : 'Same',
                            value : '#'
                        }, {
                            text : 'ALL',
                            value : '*'
                        }, {
                            text : Gikam.propI18N('T_CORE_ITEM.CONCRETE'),
                            value : 'custom'
                        } ],
                        onChange : function(field, value, rowIndex, oldValue, text) {
                            if (value === 'custom') {
                                this.showFields([ 'repeatNo' ]);
                            } else {
                                this.hideFields([ 'repeatNo' ]);
                            }

                            _this.setExpression();

                        }
                    }, {
                        field : 'repeatNo',
                        type : 'number',
                        isInteger : true,
                        value : '1',
                        validators : [ 'notEmpty' ],
                        hasArrow : true,
                        onChange : function() {
                            _this.setExpression();
                        }
                    } ],
                    onRendered : function() {
                        var form = this;
                        form.hideFields([ 'repeatNo' ]);

                        if (_this.param.type === 'item') {
                            _this.fillItemSelectOption(_this.param.testId, _this.param.itemId);
                        }

                    }
                } ]
            } ]
        };
    },

    fillItemSelectOption : function(testId, itemId) {
        if (Gikam.isEmpty(testId)) {
            Gikam.post(coreTest.baseUrl + '/items/queries', Gikam.getJsonWrapper({
                n : -1,
                s : 50
            }, [])).done(function(data) {

                data = data.rows;

                var selectData = data.map(function(item) {
                    var selectItem = {};

                    selectItem.text = item.itemName;
                    selectItem.value = item.itemName;

                    return selectItem;
                });

                Gikam.getComp('core-test-item-script-selector-item-form').setSelectOptions('item', selectData);
            });
            return;
        }

        Gikam.post(Gikam.printf(coreTest.baseUrl + '/{id}/items/queries', {
            id : testId
        }), Gikam.getJsonWrapper({
            n : -1,
            s : 50,
            f : {
                id_DIFFER : Gikam.isEmpty(itemId) ? '' : itemId
            }
        }, [])).done(function(data) {

            data = data.rows;

            var selectData = data.map(function(item) {
                var selectItem = {};

                selectItem.text = item.itemName;
                selectItem.value = item.itemName;

                return selectItem;
            });
            Gikam.getComp('core-test-item-script-selector-item-form').setSelectOptions('itemName', selectData);
        });
    },

    setExpression : function() {
        var _this = this;

        var expression = '';

        if (_this.param.type === 'item') {
            expression = _this.getItemExpression();
        } else if (_this.param.type === 'testItem') {
            expression = _this.getTestItemExpression() + '|' + _this.getItemExpression();
        }

        _this.form.setData({
            expression : '[' + expression + ']'
        });

    },

    getTestItemExpression : function() {
        var _this = this;

        var testData = Gikam.getComp('core-test-item-script-selector-test-form').getData();

        if (Gikam.isEmpty(_this.testName)) {
            return '';
        }

        return _this.testName;

    },

    getItemExpression : function() {
        var _this = this;

        var itemData = Gikam.getComp('core-test-item-script-selector-item-form').getData();

        if (Gikam.isEmpty(itemData.itemName)) {
            return '';
        }

        return itemData.itemName + ';' + _this.getExpressionValue(itemData, 'repeatNoCategory');

    },

    getExpressionValue : function(data, field) {

        var returnValue;

        if (data[field] !== 'custom') {
            returnValue = data[field];
        } else if (field === 'repeatNoCategory') {
            returnValue = data.repeatNo;
        }

        return Gikam.isEmpty(returnValue) ? '' : returnValue;

    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getFormParam(), this.getTabParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
    },

    init : function(param) {
        this.param = param;

        this.testName = null;

        this.createPage();
    }
}
