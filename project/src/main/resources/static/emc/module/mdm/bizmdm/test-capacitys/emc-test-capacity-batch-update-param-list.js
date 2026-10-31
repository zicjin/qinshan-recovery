emcTestCapacity.batchUpdateParamPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function() {
                    var comp = Gikam.getComp('emc-method-param-edit-test-capacity-list-form');
                    if (comp.validate()) {
                        var formData = comp.getData();

                        //没填写值的，默认是""
                        var fields = comp.getOptions().fields;
                        fields.map(function(field) {
                            var name = field.field;
                            if(!formData.hasOwnProperty(name)){
                                formData[name]="";
                            }
                        });
                        var selectFormData = Gikam.getComp('emc-method-param-edit-test-capacity-select-form').getData(); // 选择的模板
                        var templateUse = selectFormData.templateUse;
                        console.log(templateUse);
                        console.log([formData]);
                        // id
                        var rows = Gikam.getComp('emc-test-capacity-edit-list-grid').getSelections();
                        console.log(rows);
                        var flag = _this.param.flag;
                        Gikam.getLastModal().window.showMask(true);
                        var url = '/secure/emc/module/mdm/basemdm/property-tmpls/test-capacity/property-datas/action/batch-update/'+templateUse+'/flag/'+flag;
                        Gikam.post(url, Gikam.getJsonWrapper({},[ 'pfTestCapacityServiceImpl',rows ], [ 'pfPropertyDataServiceImpl', [ formData ] ])).done(function() {
                            Gikam.getLastModal().close();
                        }).always(function() {
                            Gikam.getLastModal().window.closeMask();
                        });
                    }
                }
            } ]
        }
    },

    getTestCapacitySelectForm : function() {
        var _this = this;
        return {
            type : 'form',
            titleWidthAuto : true,
            id : 'emc-method-param-edit-test-capacity-select-form',
            fields : emcTestCapacity.getTestCapacityBatchParamSelectColumns(),
            autoSave : false,
            onRendered : function(data) {
                // 获取数据
                /*Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/result-runs/0/property-datas/' + _this.param.id).done(function(data) {
                	this.setData({
                        templateUse : data.tmplId
                    }, false);
                }.bind(this));*/
            }
        }
    },

    baseForm : null,
    getBaseInfoForm : function() {
        var _this = this;
        return {
            type : 'form',
            titleWidthAuto : true,
            id : 'emc-method-param-edit-test-capacity-list-form',
            fields : [],
            autoSave : false,
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            cls : 'detail-page',
            north : {
                height : 65,
                items : [ this.getBtnToolbar(), this.getTestCapacitySelectForm() ]
            },
            center : {
                items : [ this.getBaseInfoForm() ]
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
