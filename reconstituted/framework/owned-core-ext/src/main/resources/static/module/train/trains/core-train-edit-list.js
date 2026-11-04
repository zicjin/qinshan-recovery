coreTrain.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-train-edit-list-grid',
            url : coreTrain.baseUrl + '/queries/raw',
            requestData : {
                activatedFlag_SEQ : '0'
            },
            deleteFormatter : function(row) {
                return row.ext$.theme || row.id;
            },
            activateFormatter : function(row) {
                return row.ext$.theme || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.TRAIN.TRAIN.BUTTON.ADD',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.TRAIN.TRAIN.MODAL.TITLE.ADD_TRAIN',
                        fields : coreTrain.getBaseInfoFormFields(),
                        url : coreTrain.baseUrl,
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('core-train-edit-list-grid').deleteRows(coreTrain.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    _this.activate();
                }
            } ],
            columns : coreTrain.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'ext$.theme' && _this.loadDetailPage(row.id);
            }
        }
    },
    
    activate : function() {
        var selectedRows = Gikam.getComp('core-train-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selectedRows)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
            return;
        }
        
        var validFlag = true;
        for (var i = 0; i < selectedRows.length; i++) {
            var item = selectedRows[i];
            if (item.activatedFlag === '1') {
                validFlag = false;
                break;
            }
        }

        if (!validFlag) {
            Gikam.alert("CORE.MODULE.TRAIN.QUESTION.TIP.QUESTION_ALREADY_ACTIVATED");
            return;
        }
        
        var url = coreTrain.baseUrl + '/action/activate';
        workspace.window.showMask(true);
        Gikam.put(url, Gikam.getJsonWrapper(null, [ '', selectedRows ])).done(function(r) {
            Gikam.getComp('core-train-edit-list-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-train-edit-list-layout',
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