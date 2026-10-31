emcEquiptFileScript.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-file-script-edit-list-grid',
            filterOpen : true,
            url : emcEquiptFileScript.baseUrl + '/queries/raw',
            columns : emcEquiptFileScript.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            },
            deleteFormatter : function(row) {
                return row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.EQUIPT.FILE_SCRIPTS.TAB.ADD',
                        height : 230,
                        fields : emcEquiptFileScript.getBaseInfoFormFields(),
                        url : emcEquiptFileScript.baseUrl,
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-equipt-file-script-edit-list-grid').deleteRows(emcEquiptFileScript.baseUrl);
                }
            } ]
        }
    },

    create : function() {
        Gikam.create('layout', {
            id : 'emc-equipt-file-script-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(emcEquiptFileScript.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function() {
        this.create();
    }
};