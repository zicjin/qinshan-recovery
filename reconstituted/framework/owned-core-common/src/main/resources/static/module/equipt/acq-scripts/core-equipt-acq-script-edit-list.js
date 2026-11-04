coreEquiptAcqScript.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-equipt-acq-script-edit-list-grid',
            url : coreEquiptAcqScript.baseUrl + '/queries/raw',
            columns : coreEquiptAcqScript.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.EQUIPT.ACQSCRIPTS.MODAL.ADD',
                        fields : coreEquiptAcqScript.getBaseInfoFormFields(),
                        url : coreEquiptAcqScript.baseUrl
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-equipt-acq-script-edit-list-grid').deleteRows(coreEquiptAcqScript.baseUrl);
                }
            } ]
        }
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreEquiptAcqScript.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-equipt-acq-script-edit-list-layout',
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
