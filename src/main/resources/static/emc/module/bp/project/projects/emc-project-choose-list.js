emcProject.choosePage = {

        getGridParam : function() {
            var _this = this, columns = emcProject.getChooseListColumns(), requestData = this.param;
            _this.param.single ? columns.unshift({
                radio : true
            }) : columns.unshift({
                checkbox : true
            });
            var url = emcProject.baseUrl + '/queries/choosable';
            if(_this.param.targetType == 'projEdit'){
                url = emcProject.baseUrl + '/queries/raw';
            }
            return {
                type : 'grid',
                id : 'emc-project-choose-list-grid',
                url : url,
                requestData : requestData,
                columns : columns,
                page : true,
                pageSize : 100,
                filterOpen : true,
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CONFIRM',
                    icon : 'select',
                    onClick : function() {
                        Gikam.getLastModal().close(Gikam.getComp('emc-project-choose-list-grid').getSelections());
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CANCEL',
                    icon : 'cancel',
                    onClick : function() {
                        Gikam.getLastModal().close();
                    }
                } ]
            }
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
                        Gikam.getLastModal().close(Gikam.getComp('emc-project-choose-list-grid').getSelections());
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

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
}
