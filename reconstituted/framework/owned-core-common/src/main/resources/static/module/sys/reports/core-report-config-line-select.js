coreReportConfig.lineSelectPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('core-report-config-line-select-base-info-form').getData().reportName);
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
            type : 'tab',
            width : 300,
            panels : [ {
                title : 'CORE.MODULE.SYS.REPORTS.LINE_SELECT.TAB.CHOOSE_REPORT',
                items : [ {
                    type : 'form',
                    id : 'core-report-config-line-select-base-info-form',
                    columns : 1.3,
                    fields : coreReportConfig.getLineSelectFormFields()
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            north : {
                items : [ _this.getBtnToolbar() ]
            },
            center : {
                items : [ _this.getBaseInfoTab() ]
            },
        });
    },

    init : function(param) {
        this.param = param;

        this.create();
    }
};