emcFile.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'Grid',
            id : 'emc-file-edit-list-grid',
            url : emcFile.baseUrl + '/queries/raw',
            columns : emcFile.getEditListColumns(),
            checkOnActive : true,
            page : true,
            deleteFormatter : function(row) {
                return row.fileNo || row.id;
            },
            onCellClick : function(field, row) {
                field === 'fileNo' && _this.loadDetailPage(row.id);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.MODAL.EQUIP_ADD',
                        fields : emcFile.getBaseInfoFormFields(),
                        url : emcFile.baseUrl
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-file-edit-list-grid').deleteRows(emcFile.baseUrl);
                }
            } ]
        }
    },

    loadDetailPage : function(id) {
        workspace.window.load(emcFile.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            operType : 'edit'
        }));
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-file-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
