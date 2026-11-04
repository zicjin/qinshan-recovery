//@ sourceURL=core-query-config-detail.js
coreQueryConfig.detailPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_CORE_QUERY_CONFIG', _this.param.id);
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
        return {
            type : 'tab',
            width : 900,
            panels : [ {
                title : 'CORE.MODULE.ITEM.QUERY-CONFIGS.TAB.BASE-INFO-FORM',
                items : [ {
                    type : 'form',
                    id : 'core-query-config-detail-base-info-form',
                    url : this.getCompUrl('baseInfoForm'),
                    service : 'coreQueryConfigServiceImpl',
                    fields : coreQueryConfig.getBaseInfoFormFields(),
                    onUpdated : function(data) {
                        if (data.hasOwnProperty('sql')) {
                            Gikam.getComp('core-query-config-detail-column-list-grid').refresh();
                        }
                    }
                } ]
            } ]
        }
    },

    getConfigTabParam : function() {
        var _this = this;

        return {
            type : 'tab',
            id : 'core-query-config-detail-grid-tab',
            width : 900,
            panels : [ {
                title : 'CORE.MODULE.ITEM.QUERY-CONFIGS.TAB.CONFIG-DETAIL-COLUMN',
                items : [ {
                    type : 'grid',
                    id : 'core-query-config-detail-column-list-grid',
                    url : this.getCompUrl('configColumnGrid'),
                    service : 'coreQueryConfigColumnServiceImpl',
                    columns : coreQueryConfig.getColumnEditListColumns(),
                    suspendFormatter : function(row) {
                        return row.columnId;
                    },
                    columnsFill : false,
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.ACTIVE',
                        icon : 'enable',
                        onClick : function() {
                            if (_this.param.id) {
                                _this.save(_this.unsuspendConfigColumnRow, true);
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.SUSPEND',
                        icon : 'disable',
                        onClick : function() {
                            if (_this.param.id) {
                                _this.save(_this.suspendConfigColumnRow, true);
                            }
                        }
                    } ]
                } ]
            } ]
        }
    },

    suspendConfigColumnRow : function() {
        var configForm = Gikam.getComp('core-query-config-detail-base-info-form');

        var configData = configForm.getData();

        var suspendUrl = Gikam.printf(coreQueryConfig.baseUrl + '/{id}/query-config-columns/action/suspend', {
            id : configData.id
        });

        Gikam.getComp('core-query-config-detail-column-list-grid').suspendRows(suspendUrl).done(function() {
        });
    },

    unsuspendConfigColumnRow : function() {
        var configForm = Gikam.getComp('core-query-config-detail-base-info-form');

        var configData = configForm.getData();

        var unsuspendUrl = Gikam.printf(coreQueryConfig.baseUrl + '/{id}/query-config-columns/action/unsuspend', {
            id : configData.id
        });

        Gikam.getComp('core-query-config-detail-column-list-grid').unsuspendRows(unsuspendUrl).done(function() {
        });
    },

    urlManager : {
        baseInfoForm : coreQueryConfig.baseUrl + '/{id}',
        configColumnGrid : coreQueryConfig.baseUrl + '/{id}/query-config-columns/queries'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-query-config-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'core-query-config-detail-column-list-grid' : this.getCompUrl('configColumnGrid')
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

    save : function(callback, showLoading, param) {
        workspace.window.save({
            url : coreQueryConfig.baseUrl + (Gikam.isEmpty(this.param.id) ? '' : '/' + this.param.id),
            page : this,
            showLoading : showLoading
        }).done(function() {
            callback && callback(param);
        });
    },

    createPage : function() {
        Gikam.create('layout', {
            cls : 'detail-page',
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab(), this.getConfigTabParam() ]
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