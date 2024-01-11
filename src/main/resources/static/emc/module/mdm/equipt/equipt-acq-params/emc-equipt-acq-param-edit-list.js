emcEquiptAcqParam.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-acq-param-edit-list-grid',
            url : emcEquiptAcqParam.baseUrl + '/queries/raw',
            columns : emcEquiptAcqParam.getEditListColumns(),
            service : 'emcEquiptAcqParamServiceImpl',
            deleteFormatter : function(row) {
                return row.fieldName || row.field || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        fields : emcEquiptAcqParam.getEditListColumns(),
                        url : emcEquiptAcqParam.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-equipt-acq-param-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-equipt-acq-param-edit-list-grid').deleteRows(emcEquiptAcqParam.baseUrl);
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-equipt-acq-param-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.create();
    },

};