coreOfficeTmpl.choosePage = {

    init : function(param) {
        this.param = param;
        this.createPage();
    },

    createPage : function() {
        var _this = this;
        var items = [ _this.getBtnToolbarParam(), _this.getOfficeTmplGridParam() ];
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : items
            }
        });
        return this;
    },

    getBtnToolbarParam : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'btnGroup',
                items : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CONFIRM',
                    icon : 'select',
                    onClick : function() {
                        Gikam.getLastModal().close(Gikam.getComp('core-office-tmpl-choose-list-grid').getSelections());
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CANCEL',
                    icon : 'cancel',
                    onClick : function() {
                        Gikam.getLastModal().close();
                    }
                } ]
            } ]
        }
    },

    getOfficeTmplGridParam : function() {
        var _this = this;
        var url = coreOfficeTmpl.baseUrl + '/queries/choosable';
        var columns = coreOfficeTmpl.getOfficeTmplEditListColumns();
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        var requestData = {};
        return {
            type : 'grid',
            page : true,
            id : 'core-office-tmpl-choose-list-grid',
            url : url,
            filterOpen : true,
            requestData : requestData,
            columns : columns
        }
    }

};