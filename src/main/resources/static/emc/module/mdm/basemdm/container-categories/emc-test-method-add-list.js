//@ sourceURL=emc-test-method-add-list.js
emcContainerCategory.chooseTMPage = {

    getGridParam : function() {
        var _this = this, columns = emcContainerCategory.getChooseTMListColumns();
        return {
            type : 'grid',
            id : 'emc-test-method-choose-list-grid',
            url :  IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests/queries/choosable',
            columns : columns,
            readonly : true,
            filterOpen : true,
            fixed : true,
            page : true,
            requestData : {
                target_assigned : '1',
                targetAssignedTable : 'T_EMC_TEST_CAPACITY',
                targetTestMethodIdColumn : "TESTMETHODID",
                targetAssignedFilterColumn : "ORGID",
                targetAssignedFilterValue : workspace.user.ext$.parentorgid,
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var selections = Gikam.getComp('emc-test-method-choose-list-grid').getSelections();
                    if (selections.length === 0) {
                        Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
                        return;
                    }

                    var form = Gikam.getComp('emc-mainentance-test-method-choose-form');
                    if (!form.validate()) {
                        return;
                    }

                    var testMethodIds = selections.map(function (item) {
                       return item.id;
                    }).join();

                    var data = form.getData();
                    var param = {
                        id : selections[0].id,
                        testMethodIds : testMethodIds,
                        containerGroup : data.containerGroup,
                        fixative : data.fixative,
                        monitorTmplId : data.monitortmplid,
                        monitorTmplName : data.monitortmplname,
                        containerCategoryId : data.containercategoryid,
                        storeCondition : data.storeConditionText,
                        sampleWeight : data.sampleWeight
                    };

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

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type: 'form',
            columns : 2,
            id : 'emc-mainentance-test-method-choose-form',
            titleWidth : 80,
            onRendered : function(){
                this.hideFields([ 'monitortmplid','containercategoryid' ]);
            },
            fields: [{
                field: 'monitortmplid',
                title: 'T_EMC_TEST_ORDER_CATEGORY.MONITORTMPLNAME',
            }, {
                field: 'monitortmplname',
                title: 'T_EMC_TEST_ORDER_CATEGORY.MONITORTMPLNAME',
                type : 'choose',
                category : 'monitorTmpl',
                single : true,
                targetFields : [{monitortmplid : 'id'}, {monitortmplname : 'monitorTmplName'} ],
                editor : true,
                validators : [ 'notEmpty' ]
            }, {
                field: 'containercategoryid',
                title: 'T_EMC_TEST_ORDER_CATEGORY.CONTAINERCATEGORYNAME',
            }, {
                field: 'containercategoryname',
                title: 'T_EMC_TEST_ORDER_CATEGORY.CONTAINERCATEGORYNAME',
                type : 'choose',
                category : 'containerCategory',
                single : true,
                targetFields : [{containercategoryid : 'id'}, {containercategoryname : 'containerCategoryName'} ],
                editor : true,
                validators : [ 'notEmpty' ]
            }, {
                field: 'containerGroup',
                title: 'T_EMC_TEST_ORDER_CATEGORY.CONTAINERGROUP',
                editor : true,
                validators : [ 'notEmpty' ],
                //validators : [ 'remote[' + '/secure/emc/module/mdm/bizmdm/tests/container-maintenance/action/validate-unique]', 'notEmpty', 'strLength[0,36]' ],
            }, {
                field: 'fixative',
                title: 'T_EMC_TEST_ORDER_CATEGORY.FIXATIVE',
                editor : true,
            }, {
                field : 'sampleWeight',
                title : 'T_EMC_TEST_ORDER_CATEGORY.SAMPLEWEIGHT',
                editor: true
            }, {
                field: 'storeCondition',
                title: 'T_EMC_TEST_ORDER_CATEGORY.STORECONDITION',
                type : 'select',
                search : false,
                category : 'sys',
                param : {
                    codeCategoryId : 'storeCondition',
                },
                editor : true,
                multiple : true,
                onChange : function(field, value, index, oldValue, text) {
                    this.setData({
                        index : index,
                        storeCondition : value,
                        storeConditionText : text.join(';')
                    });
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            north : {
                height : 137,
                items : [ this.getBaseInfoTab() ]
            },
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
