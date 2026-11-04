coreLesDs.detailPage = {

    init : function(param) {
        this.param = param;
        this.createPage();
    },

    createPage : function() {
        Gikam.create('layout', {
            west : {
                width : '50%',
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
                    Gikam.viewAudit('T_CORE_LES_DS', _this.param.id);
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
                title : 'CORE.MODULE.ITEM.LES_DSES.TAB.DS_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-les-ds-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreLesDsServiceImpl',
                    fields : coreLesDs.getBaseInfoFormFields(),
                    columns : 3,
                    onRendered : function() {
                        _this.baseForm = this;
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
                title : 'CORE.MODULE.ITEM.LES_DSES.TAB.DS_FIELD_INFO',
                items : [ {
                    type : 'grid',
                    id : 'core-les-ds-field-list-grid',
                    url : _this.getCompUrl('fieldGrid'),
                    service : 'coreLesDsFieldServiceImpl',
                    onRendered : function() {
                        _this.fieldGrid = this;
                    },
                    columns : coreLesDs.getFieldEditListColumns(),
                    page : false,
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.MOVE_UP',
                        icon : 'move-up',
                        onClick : function() {
                            Gikam.getComp('core-les-ds-field-list-grid').moveUp(coreLesDs.baseUrl + '/fields/action/swap/{firstId}/{secondId}')
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.MOVE_DOWN',
                        icon : 'move-down',
                        onClick : function() {
                            Gikam.getComp('core-les-ds-field-list-grid').moveDown(coreLesDs.baseUrl + '/fields/action/swap/{firstId}/{secondId}')
                        }
                    } ]
                } ]
            } ]
        }
    },

    urlManager : {
        baseInfoForm : coreLesDs.baseUrl + '/{id}',
        fieldGrid : coreLesDs.baseUrl + '/{id}/fields/queries',
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-les-ds-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'core-les-ds-field-list-grid' : this.getCompUrl('fieldGrid'),
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
        return Gikam.printf(coreLesDs.baseUrl + '/{id}' + url, {
            id : data.id
        });
    },

};