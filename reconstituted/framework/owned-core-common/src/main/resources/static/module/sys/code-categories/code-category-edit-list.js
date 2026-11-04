codeCategory.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'lims-code-category-edit-list-grid',
            filterOpen : true,
            url : codeCategory.baseUrl + '/queries/raw',
            columns : codeCategory.getEditListColumns(),
            requestData : {
                codeclassid_SEQ : 'biz'
            },
            deleteFormatter : function(row) {
                return row.id;
            },
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.SYS.CODECATEGORIES.TAB.ADD',
                        fields : codeCategory.getBaseInfoFormFields(),
                        url : codeCategory.baseUrl
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('lims-code-category-edit-list-grid').deleteRows(codeCategory.baseUrl);
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'lims-code-category-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(codeCategory.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function() {
        this.create();
    }
};
