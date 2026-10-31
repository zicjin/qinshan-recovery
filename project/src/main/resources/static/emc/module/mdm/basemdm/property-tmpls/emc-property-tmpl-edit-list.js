emcPropertyTmpl.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-property-tmpl-edit-list-grid',
            url : emcPropertyTmpl.baseUrl + '/queries/raw',
            columns : emcPropertyTmpl.getEditListColumns(),
            service : 'emcPropertyTmplServiceImpl',
            deleteFormatter : function(row) {
                return row.templateName || row.id;
            },
            onBeforeUpdate : function(data) {

                Gikam.select.clean('propertyTmpl',{
                    templateUse : 'methodParam'
                });
                return true;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }
            },
            onRowActive : function(index, row) {
                _this.onPropertyTmplActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    var url = Gikam.printf(emcPropertyTmpl.baseUrl);
                    Gikam.getComp('emc-property-tmpl-edit-list-grid').insert(url, [ {} ]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-property-tmpl-edit-list-grid').deleteRows(emcPropertyTmpl.baseUrl);
                    Gikam.select.clean('propertyTmpl',{
                        templateUse : 'methodParam'
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.COPY',
                icon : 'copy',
                onClick : function() {
                    var url = Gikam.printf(emcPropertyTmpl.baseUrl + "/copy");
                    var grid = Gikam.getComp('emc-property-tmpl-edit-list-grid');
                    var data = grid.getSelections();
                    Gikam.post(url, Gikam.getJsonWrapper(null, ['', data])).always(function () {
                        grid.refresh();
                    })
                }
            } ]
        }
    },

    getFieldParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            width : Gikam.width.M,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.PROPERTYTMPLS.TAB.FIELD',
                items : [ {
                    type : 'grid',
                    id : 'emc-property-tmpl-detail-field-grid',
                    service : 'emcPropertyFieldServiceImpl',
                    url : null,
                    checkOnActive : true,
                    deleteFormatter : function(row) {
                        return row.fieldName || row.id;
                    },
                    beforeUpdate : function(data1, data2) {
                        var gridData = this.getData();
                        var internalFieldValue = gridData.map(function(item) {
                            return item.internalField;
                        });
                        if (internalFieldValue.indexOf(data1.internalField) !== internalFieldValue.lastIndexOf(data1.internalField)) {
                            Gikam.alert('EMC.MODULE.MDM.BASEMDM.PROPERTYTMPLS.TIP.CHOOSE_DIFFER_INTERNAL');
                            this.refreshRowById(data1.id);
                            return false;
                        }
                        return true;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var activedRows = Gikam.getComp('emc-property-tmpl-edit-list-grid').getActivedRow();
                            var url = Gikam.printf(emcPropertyTmpl.baseUrl + '/{id}/fields', {
                                id : activedRows.id
                            });
                            Gikam.getComp('emc-property-tmpl-detail-field-grid').insert(url, [ {} ]);
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

    onPropertyTmplActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-property-tmpl-detail-field-grid').refresh({
                url : _this.getPropertyTmplUrl(row.id),
            });
        }
    },

    getPropertyTmplUrl : function(id) {
        if (id) {
            return Gikam.printf(emcPropertyTmpl.baseUrl + '/{id}/fields/queries', {
                id : id
            })
        }
    },
    
    loadDetailPage : function(row) {
        var value = row.split(",");
        if (value[1] == 'undefined') {
            Gikam.alert('EMC.MODULE.MDM.BASEMDM.PROPERTYTMPLS.TIP.CHOOSE_ONE_FILE_TYPE');
            return;
        }
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BASEMDM.PROPERTYTMPLS.TITLE.PROPERTY_TMPL_ITEM_DETAIL',
            height : 350,
            url : emcPropertyTmpl.baseUrl + '/page/item-detail' + Gikam.param({
                id : value[0],
                fieldType : value[1]
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-property-tmpl-detail-field-grid').refresh();
            }
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-property-tmpl-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '26%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getFieldParam() ]
            }
        });
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

    init : function() {
        this.create();
    }
};
