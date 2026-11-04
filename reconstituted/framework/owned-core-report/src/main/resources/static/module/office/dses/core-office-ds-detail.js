coreOfficeDs.detailPage = {

    init : function(param) {
        this.param = param;
        this.createPage();
    },

    createPage : function() {
        Gikam.create('layout', {
            north : {
                height : 350,
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
                    Gikam.viewAudit('T_CORE_OFFICE_DS', _this.param.id);
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
                title : 'CORE.MODULE.OFFICE.DSES.TAB.DS_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-office-ds-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreOfficeDsServiceImpl',
                    fields : coreOfficeDs.getBaseInfoFormFields(),
                    columns : 3,
                    onRendered : function() {
                        _this.baseForm = this;
                    },
                    onLoadSuccess : function(data) {
                        if (data.displayCategory != 'list') {
                            _this.baseForm.setData({
                                repeatHeader : '0'
                            }, false)
                            _this.baseForm.toFieldsReadonly([ 'repeatHeader' ]);
                        } else {
                            _this.baseForm.toFieldsEdit([ 'repeatHeader' ]);
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
                title : 'CORE.MODULE.OFFICE.DSES.TAB.DS_FIELD_INFO',
                items : [ {
                    type : 'grid',
                    id : 'core-office-ds-field-list-grid',
                    url : _this.getCompUrl('fieldGrid'),
                    service : 'coreOfficeDsFieldServiceImpl',
                    columns : coreOfficeDs.getFieldEditListColumns(),
                    page : false,
                    columnsFill : false,
                    onLoadSuccess : function(rows) {
                        var _this = this;
                        _this.validate(null, true);
                        rows.forEach(function(item, index) {
                            if (item.isPicture == 0) {
                                _this.toFieldsReadonly(index, [ 'pictureWidth', 'pictureHeight' ]);
                            } else {
                                _this.toFieldsReadonly(index, [ 'writeBack' ]);
                            }
                            if (item.writeBack == 0) {
                                _this.toFieldsReadonly(index, [ 'updateTableName', 'updateColumnName' ]);
                            } else {
                                _this.toFieldsReadonly(index, [ 'isPicture' ]);
                            }
                        });
                    },
                    onUpdated : function() {
                        this.validate(null, true);
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.MOVE_UP',
                        icon : 'move-up',
                        onClick : function() {
                            Gikam.getComp('core-office-ds-field-list-grid').moveUp(coreOfficeDs.baseUrl + '/fields/action/swap/{firstId}/{secondId}')
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.MOVE_DOWN',
                        icon : 'move-down',
                        onClick : function() {
                            Gikam.getComp('core-office-ds-field-list-grid').moveDown(coreOfficeDs.baseUrl + '/fields/action/swap/{firstId}/{secondId}')
                        }
                    } ]
                } ]
            } ]
        }
    },

    urlManager : {
        baseInfoForm : coreOfficeDs.baseUrl + '/{id}',
        fieldGrid : coreOfficeDs.baseUrl + '/{id}/fields/queries',
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-office-ds-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'core-office-ds-field-list-grid' : this.getCompUrl('fieldGrid'),
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

    getUrl : function(url) {
        var data = this.baseForm.getData();
        return Gikam.printf(coreOfficeDs.baseUrl + '/{id}' + url, {
            id : data.id
        });
    },

};