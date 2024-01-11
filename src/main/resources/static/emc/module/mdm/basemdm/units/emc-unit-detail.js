emcUnit.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_EMC_UNIT', _this.param.id);
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
                title : 'EMC.MODULE.MDM.BASEMDM.UNITS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    url : _this.getCompUrl('baseInfoForm'),
                    id : 'emc-unit-detail-base-info-form',
                    service : 'emcUnitServiceImpl',
                    columns : 3,
                    fields : emcUnit.getBaseInfoFormFields()
                } ]
            } ]
        }
    },

    getUnitConvertGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.UNITS.TAB.UNIT_CONVERT',
                items : [ {
                    type : 'grid',
                    id : 'emc-unit-detail-convert-grid',
                    url : _this.getCompUrl('convertGrid'),
                    service : 'emcUnitConvertServiceImpl',
                    deleteFormatter : function(row) {
                        return row.ext$.convertunitname || row.id;
                    },
                    columns : emcUnit.getConvertListColumns()
                } ]
            } ]
        }
    },

    urlManager : {
        baseInfoForm : emcUnit.baseUrl + '/{id}',
        convertGrid : emcUnit.baseUrl + '/{id}/converts/queries'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'emc-unit-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'emc-unit-detail-convert-grid' : this.getCompUrl('convertGrid')
        });
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    createPage : function() {
        Gikam.create('layout', {
            cls : 'detail-page',
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab(), this.getUnitConvertGridParam() ]
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