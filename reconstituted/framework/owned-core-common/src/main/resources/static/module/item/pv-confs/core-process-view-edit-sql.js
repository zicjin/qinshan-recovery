coreProcessViewConf.editSqlPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function() {
                    var formData = Gikam.getComp('core-process-view-edit-sql-form').getData();
                    Gikam.getLastModal().close(formData);
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'form',
            columns : 1,
            id : 'core-process-view-edit-sql-form',
            service : _this.param.serviceName,
            fields : _this.getEditSqlFields(),
            url : _this.getUrl()
        }
    },

    getUrl : function() {
        var _this = this;

        return Gikam.printf(coreProcessViewConf.baseUrl + (_this.param.serviceName === 'coreProcessViewMasterConfServiceImpl' ? '/master-confs/{id}' : '/sub-confs/{id}'), {
            id : _this.param.id
        });

    },

    getEditSqlFields : function() {
        var _this = this;
        return [ {
            field : _this.param.field,
            title : 'T_CORE_PROCESS_VIEW_CONF.SQL',
            type : 'textarea',
            height : 250
        } ];
    },

    createPage : function() {
        Gikam.create('layout', {
            id : 'core-process-view-edit-sql-layout',
            center : {
                items : [ this.getBaseInfoTab() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });

        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}