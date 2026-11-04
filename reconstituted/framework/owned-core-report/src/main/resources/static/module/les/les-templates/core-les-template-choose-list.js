coreLesTemplate.chooseListPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-les-template-choose-list-grid',
            url : coreLesTemplate.baseUrl + '/queries/choosable',
            requestData : _this.param,
            columns : coreLesTemplate.getChooseListColumns(_this.param.single),
            filterOpen : true,
            toolbar : Gikam.isEmpty(_this.param.notCurrent) ? _this.getToolCurrentBar() : _this.getToolbar()
        }
    },
    
    getToolbar : function() {
        return [ {
            type : 'button',
            text : 'GIKAM.BUTTON.CONFIRM',
            icon : 'select',
            onClick : function() {
                var selections = Gikam.getComp('core-les-template-choose-list-grid').getSelections();
                for (var i = 0; i < selections.length; i++) {
                    for (var j = i; j < selections.length; j++) {
                        if (j > i && selections[i].templateNo === selections[j].templateNo) {
                            Gikam.alert('CORE.MODULE.LES.LES_TEMPLATES.TIP.PER_TEMPLATE_CAN_ONLY_CHOOSE_ONE_VERSION');
                            return;
                        }
                    }
                }
                Gikam.getLastModal().close(selections);
            }
        }, {
            type : 'button',
            text : 'GIKAM.BUTTON.CANCEL',
            icon : 'cancel',
            onClick : function() {
                Gikam.getLastModal().close();
            }
        } ];
    },
    
    getToolCurrentBar : function() {
        return [ {
            type : 'button',
            text : 'GIKAM.BUTTON.CONFIRM',
            icon : 'select',
            onClick : function() {
                var selections = Gikam.getComp('core-les-template-choose-list-grid').getSelections();
                for (var i = 0; i < selections.length; i++) {
                    for (var j = i; j < selections.length; j++) {
                        if (j > i && selections[i].templateNo === selections[j].templateNo) {
                            Gikam.alert('CORE.MODULE.LES.LES_TEMPLATES.TIP.PER_TEMPLATE_CAN_ONLY_CHOOSE_ONE_VERSION');
                            return;
                        }
                    }
                }
                Gikam.getLastModal().close(selections);
            }
        }, {
            type : 'button',
            text : 'CORE.MODULE.LES.LES_TEMPLATES.SELECT_CURRENT',
            icon : 'default',
            onClick : function() {
                var selections = Gikam.getComp('core-les-template-choose-list-grid').getSelections();
                for (var i = 0; i < selections.length; i++) {
                    for (var j = i; j <selections.length; j++) {
                        if (j > i && selections[i].templateNo === selections[j].templateNo) {
                            Gikam.alert('CORE.MODULE.LES.LES_TEMPLATES.TIP.PER_TEMPLATE_CAN_ONLY_CHOOSE_ONE_VERSION');
                            return;
                        }
                    }
                    selections[i].templateVersion = 'C';
                }
                Gikam.getLastModal().close(selections);
            }
        }, {
            type : 'button',
            text : 'GIKAM.BUTTON.CANCEL',
            icon : 'cancel',
            onClick : function() {
                Gikam.getLastModal().close();
            }
        } ];
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};