coreElnDs.detailPage = {

    init : function(param) {
        this.param = param;
        this.createPage();
    },

    createPage : function() {
        Gikam.create('layout', {
            north : {
                height : '50%',
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
            center : {
                items : [ this.getFieldGridParam() ]
            },
            renderTo : workspace.window.$dom,
        });
        return this;
    },

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_CORE_ELN_DS', _this.param.id);
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

    baseForm : null,
    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.ITEM.ELNS.TAB.DS_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-eln-ds-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreElnDsServiceImpl',
                    fields : coreElnDs.getBaseInfoFormFields(),
                    columns : 4,
                    onRendered : function() {
                        _this.baseForm = this;
                    },
                    onLoadSuccess : function(data) {
                        if (data.displayCategory != 'list') {
                            // 隐藏pivotCategory
                            _this.baseForm.setData({
                                pivotCategory : '0'
                            }, false)
                            _this.baseForm.toFieldsReadonly([ 'pivotCategory' ]);
                        } else {
                            // 取消隐藏pivotCategory
                            _this.baseForm.toFieldsEdit([ 'pivotCategory' ]);
                            if (data.pivotCategory == '1') {
                                Gikam.getAsyncComp('core-eln-ds-field-list-grid').done(function(grid) {
                                    grid.showColumns([ 'pivotColumnCategory' ]);
                                });
                            }
                        }
                    }
                } ]
            } ]
        }
    },

    fieldGrid : null,
    getFieldGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.ITEM.ELNS.TAB.DS_FIELD_INFO',
                items : [ {
                    type : 'grid',
                    id : 'core-eln-ds-field-list-grid',
                    url : _this.getCompUrl('fieldGrid'),
                    service : 'coreElnDsFieldServiceImpl',
                    deleteFormatter : function(row) {
                        return row.elnColumnName || row.columnName || row.id;
                    },
                    onRendered : function() {
                        _this.fieldGrid = this;
                        this.hideColumns([ 'pivotColumnCategory' ]);
                    },
                    columns : coreElnDs.getFieldEditListColumns()
                } ]
            } ]
        }
    },

    urlManager : {
        baseInfoForm : coreElnDs.baseUrl + '/{id}',
        fieldGrid : coreElnDs.baseUrl + '/{id}/fields/queries',
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-eln-ds-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'core-eln-ds-field-list-grid' : this.getCompUrl('fieldGrid'),
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

    insertField : function() {
        var _this = this;
        _this.fieldGrid.insert(_this.getUrl('/fields'), [ {
            elnDsId : _this.param.id
        } ]);
    },

    deleteField : function() {
        var _this = this;
        _this.fieldGrid.deleteRows(_this.getUrl('/fields'));
    },

    getUrl : function(url) {
        var data = this.baseForm.getData();
        return Gikam.printf(coreElnDs.baseUrl + '/{id}' + url, {
            id : data.id
        });
    },

}
