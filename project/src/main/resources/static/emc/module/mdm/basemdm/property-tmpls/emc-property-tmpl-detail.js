emcPropertyTmpl.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_EMC_PROPERTY_TMPL', _this.param.id);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                id : 'emc-property-tmpl-detail-back-button',
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
            width : Gikam.width.L,
            panels : [ {
                title : 'EMC.MODULE.MDM.PROPERTYTMPLS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'emc-property-tmpl-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'emcPropertyTmplServiceImpl',
                    fields : emcPropertyTmpl.getBaseInfoFormFields(),
                    onRendered : function() {
                        this.toFieldsReadonly([ 'id' ]);
                    }
                } ]
            } ]
        }
    },
    
    getFieldParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.M,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.PROPERTYTMPLS.TAB.FIELD',
                items : [ {
                    type : 'simpleGrid',
                    id : 'emc-property-tmpl-detail-field-grid',
                    url : _this.getCompUrl('fieldGrid'),
                    checkOnActive : true,
                    deleteFormatter : function(row) {
                        return row.fieldName || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            Gikam.preInsert({
                                modalTitle : 'EMC.MODULE.MDM.BASEMDM.PROPERTYTMPLS.MODAL.FIELD_ADD',
                                fields : emcPropertyTmpl.getPropertyField(),
                                url : emcPropertyTmpl.baseUrl + '/' + _this.param.id + '/fields',
                            }).done(function(row) {
                                Gikam.getComp('emc-property-tmpl-detail-field-grid').refresh();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteField();
                        }
                    } ],
                    columns : emcPropertyTmpl.getPropertyFieldColumns()
                } ]
            } ]
        }
    },
    
    deleteField : function() {
        Gikam.getComp('emc-property-tmpl-detail-field-grid').deleteRows(emcPropertyTmpl.baseUrl + '/fields').done(function() {
        });
    },

    urlManager : {
        baseInfoForm : emcPropertyTmpl.baseUrl + '/{id}',
        fieldGrid : emcPropertyTmpl.baseUrl + '/{id}/fields/queries'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'emc-property-tmpl-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'emc-property-tmpl-detail-field-grid' : this.getCompUrl('fieldGrid'),
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
            id : 'emc-property-tmpl-detail-layout',
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab(), this.getFieldParam() ]
            },
            renderTo : workspace.window.$dom,
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
