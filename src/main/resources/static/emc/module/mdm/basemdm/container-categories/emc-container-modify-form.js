//@ sourceURL = emc-container-modify-form.js
emcContainerCategory.modifyContainerPage = {

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type: 'form',
            columns : 1,
            id : 'emc-container-modify-form',
            titleWidth : 80,
            onRendered : function(){
                var type = _this.param.type;
                this.hideFields([ 'containercategoryid' ]);
                if (!Gikam.isEmpty(type) && "rqlx" == type) {
                    this.hideFields([ 'sampleqty','fixative', 'labelqty', 'sampleWeight', 'storeCondition' ]);
                    this.showFields([ 'containercategoryname' ]);
                    Gikam.getComp('emc-container-modify-form').setData({
                        containercategoryid : _this.param.containercategoryid,
                        containercategoryname : _this.param.containercategoryname,
                    });
                }
                if (!Gikam.isEmpty(type) && "zcfs" == type) {
                    this.hideFields([ 'sampleqty','containercategoryname', 'labelqty', 'sampleWeight', 'storeCondition' ]);
                    this.showFields([ 'fixative' ]);
                    Gikam.getComp('emc-container-modify-form').setData({
                        fixative : _this.param.fixative,
                    });
                }
                if (!Gikam.isEmpty(type) && "cygs" == type) {
                    this.hideFields([ 'fixative','containercategoryname', 'labelqty', 'sampleWeight', 'storeCondition' ]);
                    this.showFields([ 'sampleqty' ]);
                    Gikam.getComp('emc-container-modify-form').setData({
                        sampleqty : _this.param.sampleqty,
                    });
                }
                if (!Gikam.isEmpty(type) && "bqgs" == type) {
                    this.hideFields([ 'fixative','containercategoryname', 'sampleqty', 'sampleWeight', 'storeCondition' ]);
                    this.showFields([ 'labelqty' ]);
                    Gikam.getComp('emc-container-modify-form').setData({
                        labelqty : _this.param.labelqty,
                    });
                }
                if (!Gikam.isEmpty(type) && "storeCondition" === type) {
                    this.hideFields([ 'fixative','containercategoryname', 'sampleqty', 'sampleWeight', 'labelqty' ]);
                    this.showFields([ 'storeCondition' ]);
                    Gikam.getComp('emc-container-modify-form').setData({
                        storeCondition : _this.param.storeCondition,
                    });
                }
                if (!Gikam.isEmpty(type) && "sampleWeight" === type) {
                    this.hideFields([ 'fixative','containercategoryname', 'sampleqty', 'labelqty' ,'storeCondition']);
                    this.showFields([ 'sampleWeight' ]);
                    Gikam.getComp('emc-container-modify-form').setData({
                        sampleWeight : _this.param.sampleWeight,
                    });
                }
            },
            fields: [ {
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
            }, {
                field: 'sampleqty',
                title: 'T_EMC_TEST_ORDER_CATEGORY.SAMPLEQTY',
                type : 'number',
                hasArrow : true,
                validators : [ 'integer', 'numRange[1, 9999]' ],
            }, {
                field : 'sampleWeight',
                title : 'T_EMC_TEST_ORDER_CATEGORY.SAMPLEWEIGHT',
                editor: true
            }, {
                field: 'labelqty',
                title: 'T_EMC_TEST_ORDER_CATEGORY.LABELQTY',
                type : 'number',
                hasArrow : true,
                validators : [ 'integer', 'numRange[1, 9999]' ],
            }, {
                field: 'fixative',
                title: 'T_EMC_TEST_ORDER_CATEGORY.FIXATIVE',
                editor : true,
            }, {
                field : 'storeCondition',
                title : 'T_EMC_TEST_ORDER_CATEGORY.STORECONDITION',
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
            }]
        }
    },

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [  {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var type = _this.param.type;
                    var data = Gikam.getComp('emc-container-modify-form').getData();

                    if (Gikam.isEmpty(data.containercategoryname) && "rqlx" == type) {
                        Gikam.alert("EMC.MODULE.BP.TIP.PLEASE_COMPLETE_THE_FORM");
                        return;
                    }
                    if (Gikam.isEmpty(data.sampleqty) && "cygs" == type) {
                        Gikam.alert("EMC.MODULE.BP.TIP.PLEASE_COMPLETE_THE_FORM");
                        return;
                    }
                    if (Gikam.isEmpty(data.fixative) && "zcfs" == type) {
                        Gikam.alert("EMC.MODULE.BP.TIP.PLEASE_COMPLETE_THE_FORM");
                        return;
                    }
                    if (Gikam.isEmpty(data.labelqty) && "bqgs" == type) {
                        Gikam.alert("EMC.MODULE.BP.TIP.PLEASE_COMPLETE_THE_FORM");
                        return;
                    }
                    Gikam.getLastModal().close(data);
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
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
