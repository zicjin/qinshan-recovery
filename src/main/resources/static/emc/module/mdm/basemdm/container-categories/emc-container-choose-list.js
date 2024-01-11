//@ sourceURL = emc-container-choose-list.js
emcContainerCategory.chooseContainerPage = {

    getGridParam : function() {
        var _this = this, columns = emcContainerCategory.getChooseTMListColumns(),requestData = this.param;
        return {
            type : 'grid',
            id : 'emc-container-choose-list',
            url : '/secure/emc/module/mdm/bizmdm/tests/container-maintenance/queries/raw',
            columns : emcContainerCategory.getMaintenanceEditListColumns(),
            requestData : requestData,
            readonly : true,
            filterOpen : true,
            fixed : true,
            toolbar : [  {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var selections = Gikam.getComp('emc-container-choose-list').getSelections();
                    if (selections.length !== 1) {
                        Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
                        return;
                    }
                    Gikam.getLastModal().close(selections[0]);
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

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            },
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
