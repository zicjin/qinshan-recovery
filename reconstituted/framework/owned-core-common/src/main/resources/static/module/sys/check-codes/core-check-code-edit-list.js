coreCheckCode.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-check-code-edit-list-grid',
            url : coreCheckCode.baseUrl + '/queries/raw',
            service : 'coreCheckCodeServiceImpl',
            columns : coreCheckCode.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.codeName || row.code || row.id;
            },
            order : {
                'codeCategory' : 'asc',
                'groupName' : 'asc',
                'orderNo' : 'asc'
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.SYS.CHECKCODES.MODAL.ADD',
                        fields : coreCheckCode.getBaseInfoFormFields(),
                        url : coreCheckCode.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('core-check-code-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-check-code-edit-list-grid').deleteRows(coreCheckCode.baseUrl);
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-check-code-edit-list-layout',
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
