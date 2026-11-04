coreCustomButton.choosePage = {

    getChooseGridParam : function() {
        var _this = this, columns = coreCustomButton.getChooseListColumns();
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            page : true,
            id : 'core-custom-button-choose-list-grid',
            url : coreCustomButton.baseUrl + '/queries/choosable',
            requestData : _this.param,
            filterOpen : true,
            columns : columns
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
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add',
                    onClick : function() {
                        _this.preInsert();
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CONFIRM',
                    icon : 'select',
                    onClick : function() {
                        Gikam.getLastModal().close(Gikam.getComp('core-custom-button-choose-list-grid').getSelections());
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
                items : [ this.getBtnToolbarParam(), this.getChooseGridParam() ]
            }
        });
    },

    preInsert : function () {
        Gikam.preInsert({
            modalTitle : 'CORE.MODULE.ITEM.CUSTOM_BUTTONS.TITLE.ADD',
            fields : coreCustomButton.getBaseInfoFormFields(),
            url : coreCustomButton.baseUrl,
            height : 350
        }).done(function(id) {
            Gikam.getComp('core-custom-button-choose-list-grid').refresh();
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
