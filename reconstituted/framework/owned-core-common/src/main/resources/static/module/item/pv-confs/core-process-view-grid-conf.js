coreProcessViewConf.gridConfPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function() {
                    _this.insertGridConfRow();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.ITEM.PVCONFS.TAB.GRID_CONF_SQL',
                items : [ {
                    type : 'form',
                    id : 'core-process-view-grid-conf-form',
                    fields : coreProcessViewConf.getGridConfFields(),
                    onRendered : function() {
                        Gikam.getTextSync(Gikam.printf(coreProcessViewConf.baseUrl + '/confs/{sourceId}/source-types/{sourceType}/grid-sql', {
                            sourceId : _this.param.sourceId,
                            sourceType : _this.param.sourceType
                        })).done(function(data) {
                            if (!Gikam.isEmpty(data)) {
                                var form = Gikam.getComp('core-process-view-grid-conf-form');
                                form.setData({
                                    gridSql : data
                                });
                            }
                        });
                    }
                } ]
            } ]
        }
    },

    getConfigTabParam : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.ITEM.PVCONFS.TAB.GRID_CONF_COLUMNS',
                items : [ {
                    type : 'grid',
                    id : 'core-process-view-grid-conf-grid',
                    service : 'coreProcessViewGridConfServiceImpl',
                    url : coreProcessViewConf.baseUrl + '/grid-confs/queries',
                    columns : coreProcessViewConf.getGridConfListColumns(),
                    requestData : {
                        sourceId_SEQ : _this.param.sourceId,
                        sourceType_SEQ : _this.param.sourceType
                    },
                    activateFormatter : function(row) {
                        return row.field || row.id;
                    },
                    deactivateFormatter : function(row) {
                        return row.field || row.id;
                    },
                    toolbar : [ {
                        type : 'btnGroup',
                        items : [ {
                            type : 'button',
                            text : 'GIKAM.BUTTON.ACTIVE',
                            icon : 'enable',
                            onClick : function() {
                                Gikam.getComp('core-process-view-grid-conf-grid').activateRows(coreProcessViewConf.baseUrl + '/grid-confs/action/activate');
                            }
                        }, {
                            type : 'button',
                            text : 'GIKAM.BUTTON.DEACTIVE',
                            icon : 'disable',
                            onClick : function() {
                                Gikam.getComp('core-process-view-grid-conf-grid').deactivateRows(coreProcessViewConf.baseUrl + '/grid-confs/action/deactivate');
                            }
                        } ]
                    } ]
                } ]
            } ]
        }
    },

    insertGridConfRow : function() {
        var formData = Gikam.getComp('core-process-view-grid-conf-form').getData();
        if (Gikam.isEmpty(formData.gridSql)) {
            Gikam.alert('CORE.MODULE.ITEM.PVCONFS.TIP.EDIT_GRIDSQL');
            return;
        }

        Gikam.postText(coreProcessViewConf.baseUrl + '/grid-confs', Gikam.getJsonWrapper({
            gridSql : formData.gridSql,
            sourceId : this.param.sourceId,
            sourceType : this.param.sourceType
        })).done(function() {
            Gikam.getComp('core-process-view-grid-conf-grid').refresh();
        });
    },

    createPage : function() {
        Gikam.create('layout', {
            id : 'core-process-view-grid-conf-layout',
            center : {
                items : [ this.getConfigTabParam() ]
            },
            north : {
                height : 190,
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });

        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}