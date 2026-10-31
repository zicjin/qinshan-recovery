emcCheckMethod.editPage = {

    getSelectForm : function() {
        var _this = this;

        return {
            type : 'form',
            id : 'emc-check-method-edit-list-form',
            autoSave : false,
            columns : 4,
            fields : [ {
                title : 'EMC.MODULE.BP.EDIT.CHECKMETHODS.TAB.CHOOSE_TEST',
                field : 'testId',
                type : 'select',
                search : true,
                category : 'test',
                onChange : function(field, value) {

                    _this.testId = value;

                    _this.grid = Gikam.getComp('emc-check-method-edit-list-grid');

                    if (!Gikam.isEmpty(value)) {

                        var url = '/secure/emc/module/mdm/bizmdm/tests/queries/selectable' + Gikam.param({
                            testId : value
                        });

                        Gikam.getJson(url).done(function(items) {
                            _this.form.setSelectOptions('methodName', items.map(function(item) {
                                return {
                                    text : item.ext$.methodname,
                                    value : item.id
                                };
                            }), true);

                            if (items.length > 0) {
                                _this.form.setData({
                                    methodName : items[0].id
                                });
                            } else {
                                _this.form.setData({
                                    methodName : ""
                                });
                            }
                        });
                    } else {
                        _this.form.setSelectOptions('methodName', []);
                        _this.form.setData({
                            methodName : ""
                        });
                        _this.grid.cleanData();
                    }
                }
            }, {
                title : 'EMC.MODULE.BP.EDIT.CHECKMETHODS.TAB.CHOOSE_METHOD',
                field : 'methodName',
                type : 'select',
                search : true,
                onChange : function(field, value, rowIndex, oldValue, text) {
                    _this.methodName = text;
                    if (!Gikam.isEmpty(value)) {

                        _this.grid.setOptions({
                            requestData : {
                                testId_SEQ : _this.testId,
                                methodName_SEQ : _this.methodName,
                            }
                        });
                        _this.grid.refresh({
                            url : '/secure/emc/module/bp/bp/order-tasks/queries',
                        });
                    }
                },
            } ],
            onRendered : function() {
                _this.form = this;
            }

        };
    },

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-check-method-edit-list-grid',
            columns : emcCheckMethod.getEditListColumns(),
            url : '',
            deleteFormatter : function(row) {
                return row.testName || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.EDIT.CHECKMETHODS.BUTTON.EDIT',
                icon : 'edit',
                onClick : function() {
                    _this.edit();
                },
            } ]
        }
    },

    edit : function() {
        var _this = this;

        var selections = Gikam.getComp('emc-check-method-edit-list-grid').getSelections();

        if (selections.length === 1) {
            Gikam.create('modal', {

                title : Gikam.choose.getConfig('testMethod').title,
                url : '/secure/emc/module/mdm/bizmdm/tests/page/check-method-edit-test-method-choose-list' + Gikam.param({
                    testId_SEQ : _this.testId,
                    methodName_DIFFER : _this.methodName,
                    single : true,
                }),
                onAfterClose : function(rows) {
                    if (Gikam.isEmpty(rows)) {
                        return;
                    }
                    workspace.window.showMask(true);
                    Gikam.put(Gikam.printf('/secure/emc/module/bp/bp/orders/order-tasks/{orderTaskId}/action/method-edit', {
                        orderTaskId : selections[0].id
                    }), Gikam.getJsonWrapper({}, [ '', rows ])).done(function(id) {
                        Gikam.getComp('emc-check-method-edit-list-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            north : {
                height : 60,
                items : [ _this.getSelectForm() ]
            },
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
