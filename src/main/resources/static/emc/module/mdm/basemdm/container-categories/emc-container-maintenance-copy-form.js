//@ sourceURL=emc-container-maintenance-copy-form.js
emcContainerCategory.copyCMPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [  {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var data = Gikam.getComp('emc-container-maintenance-copy-form').getData();
                    if (Gikam.isEmpty(data.monitortmplname)) {
                        Gikam.alert("EMC.MODULE.BP.TIP.PLEASE_COMPLETE_THE_FORM");
                        return;
                    }
                    if (data.containerGroup == '' || data.containerGroup == undefined){
                        Gikam.alert('EMC.MODULE.BP.TIP.PLEASE_COMPLETE_THE_FORM');
                        return ;
                    }
                    var param = {}
                    param.monitortmplid = data.monitortmplid;
                    param.containerGroup = data.containerGroup;
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
            columns : 1,
            id : 'emc-container-maintenance-copy-form',
            onRendered : function(){
                this.hideFields([ 'monitortmplid' ]);
            },
            fields: [{
                field : 'containerGroup',
                title : 'T_EMC_TEST_ORDER_CATEGORY.CONTAINERGROUP'
            },{
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
            }]
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
