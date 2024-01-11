emcTestCapacity.methodParamDetailPage = {

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
                    	console.log(_this.param);
                    	var flag1 = _this.param.flag1;
                    	console.log(flag1);
                        //没填写值的，默认是""
                        var fields = comp.getOptions().fields;
                        fields.map(function(field) {
                            var name = field.field;
                            if(!formData.hasOwnProperty(name)){
                                formData[name]="";
                            }
                        });
                        var selectFormData = Gikam.getComp('emc-method-param-edit-test-capacity-select-form').getData(); // 选择的模板
                        // id
                        Gikam.getLastModal().window.showMask(true);
                        Gikam.put('/secure/emc/module/mdm/basemdm/property-tmpls/property-datas/test-capacity/' + selectFormData.templateUse + '/action/' + _this.param.id+'/flag/'+flag1, Gikam.getJsonWrapper({}, [ '', [ formData ] ])).done(function(id) {
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
            fields : emcTestCapacity.getTestCapacityParamSelectColumns(_this.param.id,_this.param.flag1),
            autoSave : false,
            onRendered : function(data) {
                // 获取数据
            	console.log(_this.param);
                Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/test-capacity/0/property-datas/' + _this.param.id+'/flag/'+_this.param.flag1).done(function(data) {
                    this.setData({
                        templateUse : data.tmplId,
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
