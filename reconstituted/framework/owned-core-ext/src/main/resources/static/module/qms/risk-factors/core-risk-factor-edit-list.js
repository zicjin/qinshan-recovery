coreRiskFactor.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            id : 'core-risk-factor-edit-list-grid',
            type : 'treeGrid',
            url : coreRiskFactor.baseUrl + '/tree-grid',
            requestData : _this.param,
            columns : coreRiskFactor.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.riskFactor || row.id;
            },
            page : false,
            toolbar : [ {
                id : 'core-risk-factor-edit-list-search-form',
                type : 'form',
                fields : [ {
                    title : 'T_CORE_RISK_FACTOR.RISKFACTOR',
                    type : 'select',
                    search : 'true',
                    field : 'riskFactor',
                    category : 'risk-factor',
                    onChange : function(field, value, rowIndex, oldValue, text) {
                        if (Gikam.isEmpty(value)) {
                            Gikam.getComp('core-risk-factor-edit-list-grid').refresh({
                                url : coreRiskFactor.baseUrl + '/tree-grid',
                                requestData : {
                                    parentId_EQ : '',
                                }
                            });
                        } else {
                            Gikam.getComp('core-risk-factor-edit-list-grid').refresh({
                                url : coreRiskFactor.baseUrl + '/tree-grid',
                                requestData : {
                                    parentId_EQ : value,
                                }
                            });
                        }
                    }
                } ]
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.insertRiskFactor();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('core-risk-factor-edit-list-grid').deleteRows(coreRiskFactor.baseUrl).done(function() {
                        _this.freshRiskFactorSelectItem();
                    });
                }
            } ],
        }
    },

    insertRiskFactor : function() {
        var _this = this;
        var modal = Gikam.create('modal', {
            title : 'CORE.MODULE.QMS.RISKFACTORS.MODAL.ADD',
            width : 400,
            height : 200
        });
        Gikam.create('layout', {
            renderTo : modal.window.$dom,
            center : {
                items : [ {
                    type : 'form',
                    id : 'core-risk-factor-insert-form',
                    columns : 1,
                    fields : coreRiskFactor.getPreInsertFormFields()
                }, {
                    type : 'btnToolbar',
                    items : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        'class' : 'blue',
                        onClick : function() {
                            var form = Gikam.getComp('core-risk-factor-insert-form');
                            if (Gikam.isFalse(form.validate()))
                                return;
                            var insertData = form.getData();
                            var parentId = Gikam.getComp('core-risk-factor-edit-list-search-form').getData().riskFactor;
                            if (Gikam.isNotEmpty(parentId)) {
                                insertData.parentId = parentId;
                            }
                            workspace.window.showMask(true);
                            Gikam.post(coreRiskFactor.baseUrl, Gikam.getJsonWrapper({}, [ '', [ insertData ] ])).done(function(item) {
                                modal.close();
                                Gikam.getComp('core-risk-factor-edit-list-grid').refresh();
                                _this.freshRiskFactorSelectItem();
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        onClick : function() {
                            modal.close();
                        }
                    } ]
                } ]
            }
        });
    },
    
    freshRiskFactorSelectItem : function() {
        Gikam.getJson(coreRiskFactor.baseUrl + '/queries/selectable', Gikam.getJsonWrapper({}, [])).done(function(data) {
            var selectData = data.map(function(item) {
                var selectItem = {};
                selectItem.text = item.riskFactor;
                selectItem.value = item.id;
                return selectItem;
            });
            var selectItem = {};
            selectItem.text = '';
            selectItem.value = '';
            selectData.push(selectItem);
            Gikam.getComp('core-risk-factor-edit-list-search-form').setSelectOptions('riskFactor', selectData);
        });
    },

    createPage : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-risk-factor-edit-list-layout',
            center : {
                width : '50%',
                items : [ _this.getGridParam() ]
            },
            renderTo : workspace.window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}