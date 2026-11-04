coreCustomDs.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-custom-ds-edit-list-grid',
            url : coreCustomDs.baseUrl + '/queries/raw',
            service : 'coreCustomDsServiceImpl',
            dbTable : 'T_CORE_CUSTOM_DS',
            columns : coreCustomDs.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.dsName || row.id;
            },
            activateFormatter : function(row) {
                return row.dsName || row.id;
            },
            onCellClick : function(field, row) {
                field === 'dsName' && _this.loadDetailPage(row.id);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.preInsert()
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    var selectedRows = Gikam.getComp('core-custom-ds-edit-list-grid').getSelections();

                    // 只能删除未启用的
                    var activatedFlag = false;
                    selectedRows.forEach(function (item) {
                        if (item.activatedFlag == '1') {
                            activatedFlag = true;
                            return;
                        }
                    });
                    
                    if (activatedFlag) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_DEACTIVATED_ITEM');
                        return;
                    }
                    
                    Gikam.getComp('core-custom-ds-edit-list-grid').deleteRows(coreCustomDs.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    Gikam.getComp('core-custom-ds-edit-list-grid').activateRows(coreCustomDs.baseUrl + '/action/activate');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DEACTIVE',
                icon : 'disable',
                onClick : function() {
                    Gikam.getComp('core-custom-ds-edit-list-grid').deactivateRows(coreCustomDs.baseUrl + '/action/deactivate');
                }
            } ]
        }
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreCustomDs.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    preInsert :function (){
        var _this = this;
        Gikam.preInsert({
            modalTitle : 'CORE.MODULE.ITEM.CUSTOM_DSES.MODAL.ADD',
            fields : coreCustomDs.getBaseInfoFormFields(),
            url : coreCustomDs.baseUrl,
            height : 200,

        }).done(function(id) {
            _this.loadDetailPage(id);
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-custom-ds-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
