coreTrain.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-train-search-list-grid',
            url : coreTrain.baseUrl + '/queries/searchable',
            requestData : {
                activatedFlag_SEQ : '1',
                queries_signable : '1'
            },
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.TRAIN.TRAIN.BUTTON.PRINT',
                icon : 'modify',
                onClick : function() {
                    var selectedRows = Gikam.getComp('core-train-search-list-grid').getSelections();
                    if (Gikam.isEmpty(selectedRows)) {
                        Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
                        return;
                    }
                    if (selectedRows.length !== 1) {
                        Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
                        return;
                    }
                    var selectedRow = selectedRows[0];
                    if (selectedRow.activatedFlag === '0') {
                        Gikam.alert("CORE.MODULE.TRAIN.TRAIN.TIP.SIGN_VALIDATOR");
                        return;
                    }
                    _this.sign(selectedRow);
                }
            } ],
            columns : coreTrain.getSearchListColumns(),
            onCellClick : function(field, row) {
                field === 'ext$.theme' && _this.loadDetailPage(row.id);
            }
        }
    },

    sign : function(selectedRow) {
        var _this = this;
        var modal = Gikam.create('modal', {
            title : 'CORE.MODULE.TRAIN.TRAIN.MODAL.TITLE.TRAIN_SIGN',
            width : 400,
            height : 400
        });
        Gikam.create('layout', {
            renderTo : modal.window.$dom,
            center : {
                items : [ {
                    type : 'form',
                    id : 'core-train-sign-form',
                    columns : 1,
                    fields : coreTrain.getSignBaseInfoFormFields(),
                    url : Gikam.printf(coreTrain.baseUrl + '/{id}', {
                        id : selectedRow.id,
                    })
                }, {
                    type : 'btnToolbar',
                    items : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        'class' : 'blue',
                        onClick : function() {
                            var form = Gikam.getComp('core-train-sign-form');
                            if (!form.validate()) {
                                return;
                            }
                            _this.doSign(form.getData());
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

    doSign : function(data) {
        var _this = this;
        var url = Gikam.printf(coreTrain.baseUrl + '/{id}/action/user-sign', {
            id : data.id,
        });

        Gikam.getLastModal().window.showMask(true);

        Gikam.put(url, Gikam.getJsonWrapper({}, [ '', [ data ] ])).done(function() {
            Gikam.alert('CORE.MODULE.TRAIN.TRAIN.TIP.SIGN_SUCCESS');
            Gikam.getLastModal().close();
        }).always(function() {
            Gikam.getLastModal().window.closeMask();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-train-search-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreTrain.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function(param) {
        var _this = this;
        _this.param = param;
        _this.create();
    }
};