//@ sourceURL=emc-container-maintenance-choose-tm-list.js
emcContainerCategory.chooseTestMethodPage = {

    getGridParam : function() {
        var _this = this, columns = emcContainerCategory.getChooseTMListColumns(),requestData = this.param;
        return {
            type : 'grid',
            id : 'emc-container-maintenance-choose-tm-list',
            url :  IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests/queries/choosable',
            columns : columns,
            requestData : Object.assign({}, requestData, {
                target_assigned : '1',
                targetAssignedTable : 'T_EMC_TEST_CAPACITY',
                targetTestMethodIdColumn : "TESTMETHODID",
                targetAssignedFilterColumn : "ORGID",
                targetAssignedFilterValue : workspace.user.ext$.parentorgid,
            }),
            readonly : true,
            filterOpen : true,
            fixed : true,
            page : true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var selections = Gikam.getComp('emc-container-maintenance-choose-tm-list').getSelections();
                    if (selections.length === 0) {
                        Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
                        return;
                    }
                    var testmethodids = '';
                    for (var i = 0; i < selections.length; i++) {
                        testmethodids = testmethodids + selections[i].id + ",";
                    }
                    var param = {}
                    param.testmethodids = testmethodids;
                    Gikam.getLastModal().close(param);
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
