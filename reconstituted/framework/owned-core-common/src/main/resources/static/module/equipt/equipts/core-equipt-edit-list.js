coreEquipt.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-equipt-edit-list-grid',
            url : coreEquipt.baseUrl + '/queries/raw',
            columns : coreEquipt.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'equiptNo' && _this.loadDetailPage(row.id);
            },
            deleteFormatter : function(row) {
                return row.equiptName || row.equiptNo || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.EQUIPT.EQUIPTS.MODAL.ADD',
                        fields : coreEquipt.getBaseInfoFormFields(),
                        url : coreEquipt.baseUrl,
                        param : {
                            keyEquipt : '1',
                            planUseTime : 8
                        }
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-equipt-edit-list-grid').deleteRows(coreEquipt.baseUrl);
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-equipt-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreEquipt.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function() {
        this.create();
    }
};