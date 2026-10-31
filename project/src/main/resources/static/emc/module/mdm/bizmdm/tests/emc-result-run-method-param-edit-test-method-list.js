emcTestMethod.resultRunMethodParamDetailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function() {
                    var comp = Gikam.getComp('emc-method-param-edit-test-method-list-form');
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
                        var selectFormData = Gikam.getComp('emc-method-param-edit-test-method-select-form').getData(); // 选择的模板
                        // id

                        Gikam.getLastModal().window.showMask(true);
                        Gikam.put('/secure/emc/module/mdm/basemdm/property-tmpls/result-runs/property-datas/' + selectFormData.templateUse + '/action/' + _this.param.id, Gikam.getJsonWrapper({}, [ '', [ formData ] ])).done(function(id) {
                            Gikam.getLastModal().close();
                        }).always(function() {
                            Gikam.getLastModal().window.closeMask();
                        });
                    }
                }
            } ]
        }
    },

    getTestMethodSelectForm : function() {
        var _this = this;
        return {
            type : 'form',
            titleWidthAuto : true,
            id : 'emc-method-param-edit-test-method-select-form',
            fields : emcTestMethod.getRunTestMethodParamSelectColumns(_this.param.id),
            autoSave : false,
            onRendered : function(data) {
                // 获取数据 老版本获取的是方法上的数据，或者是run上的数据
                //新版获取数据是 /secure/emc/module/mdm/basemdm/property-tmpls/result-runs/property-datas/'  + runId  ， 这个获取是run的数据
                Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/result-runs/0/property-datas/' + _this.param.id).done(function(data) {
                    this.setData({
                        templateUse : data.tmplId
                    }, false);
                }.bind(this));
            }
        }
    },

    baseForm : null,
    getBaseInfoForm : function() {
        var _this = this;
        return {
            type : 'form',
            titleWidthAuto : true,
            id : 'emc-method-param-edit-test-method-list-form',
            fields : [],
            autoSave : false,
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            cls : 'detail-page',
            north : {
                height : 65,
                items : [ this.getBtnToolbar(), this.getTestMethodSelectForm() ]
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
