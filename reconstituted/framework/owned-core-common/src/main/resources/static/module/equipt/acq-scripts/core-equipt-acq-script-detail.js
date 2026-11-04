coreEquiptAcqScript.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function() {
                    workspace.window.save({
                        url : coreEquiptAcqScript.baseUrl + (Gikam.isEmpty(_this.param.id) ? '' : '/' + _this.param.id),
                        page : coreEquiptAcqScript.detailPage,
                        showLoading : true
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_CORE_EQUIPT_ACQ_SCRIPT', _this.param.id);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.EQUIPT.ACQSCRIPTS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    columns : 2,
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreEquiptAcqScriptServiceImpl',
                    autoSave : false,
                    id : 'core-equipt-acq-script-detail-base-info-form',
                    fields : coreEquiptAcqScript.getBaseInfoFormFields()
                } ]
            } ]
        }
    },

    urlManager : {
        baseInfoForm : coreEquiptAcqScript.baseUrl + '/{id}'
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-equipt-acq-script-detail-base-info-form' : this.getCompUrl('baseInfoForm')
        });
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
            renderTo : workspace.window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
