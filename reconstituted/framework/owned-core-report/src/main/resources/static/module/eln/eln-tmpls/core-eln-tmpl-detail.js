coreElnTmpl.detailPage = {

    init : function(param) {
        this.param = param;
        this.createPage();
    },

    createPage : function() {
        var items = [ this.getBtnToolbar(), this.getBaseInfoTab() ];
        items.push(this.getBottomLayout());
        Gikam.create('layout', {
            center : {
                items : items
            },
            renderTo : workspace.window.$dom,
        });
        return this;
    },

    getBottomLayout : function() {
        var _this = this;
        return {
            type : 'layout',
            west : {
                width : '40%',
                items : [ _this.getDsGridParam() ]
            },
            center : {
                items : [ _this.getversionGridParam() ]
            }
        }
    },

    getBtnToolbar : function() {
        var _this = this;
        var items = [ {
            type : 'button',
            text : 'GIKAM.BUTTON.BACK',
            icon : 'back',
            onClick : function() {
                workspace.window.goBack();
            }
        } ];
        return {
            type : 'btnToolbar',
            items : items
        }
    },

    baseForm : null,
    getBaseInfoTab : function() {
        var _this = this;
        var fields = coreElnTmpl.getBaseInfoFormFields();
        if (_this.param.process == 'test') {
            fields.forEach(function(item) {
                item.readonly = true;
            })
        }
        return {
            type : 'tab',
            width : Gikam.width.L,
            panels : [ {
                title : 'CORE.MODULE.ITEM.ELN.TMPLS.TAB.TMPL_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-eln-tmpl-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreElnTmplServiceImpl',
                    fields : fields,
                    onRendered : function() {
                        _this.baseForm = this;
                    }
                } ]
            } ]
        }
    },

    dsGrid : null,
    getDsGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.M,
            panels : [ {
                title : 'CORE.MODULE.ITEM.ELN.TMPLS.TAB.DS_INFO',
                items : [ {
                    type : 'grid',
                    id : 'core-eln-tmpl-ds-list-grid',
                    url : _this.getCompUrl('dsGrid'),
                    service : 'coreElnTmplDsServiceImpl',
                    page : false,
                    deleteFormatter : function(row) {
                        return row.ext$.elndsname || row.id;
                    },
                    onRendered : function() {
                        _this.dsGrid = this;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertDs();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteDs();
                        }
                    } ],
                    columns : coreElnTmpl.getDsEditListColumns(),
                } ]
            } ]
        }
    },

    versionGrid : null,
    getversionGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.M,
            panels : [ {
                title : 'CORE.MODULE.ITEM.ELN.TMPLS.TAB.VERSION_INFO',
                items : [ {
                    type : 'grid',
                    id : 'core-eln-tmpl-ver-list-grid',
                    url : _this.getCompUrl('versionGrid'),
                    service : 'coreElnTmplVerServiceImpl',
                    page : false,
                    onRendered : function() {
                        _this.versionGrid = this;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertVersion();
                        }
                    }, {
                        type : 'button',
                        text : 'CORE.MODULE.ITEM.ELN.TMPLS.BUTTON.EDIT_ELN_TMPL',
                        icon : 'edit',
                        onClick : function() {
                            _this.editTmpl();
                        }
                    }, {
                        type : 'button',
                        text : 'CORE.MODULE.ITEM.ELN.TMPLS.BUTTON.TEST_ELN',
                        icon : 'enable',
                        onClick : function() {
                            _this.testTmpl();
                        }
                    } ],
                    columns : coreElnTmpl.getVersionEditListColumns(),
                } ]
            } ]
        }
    },

    urlManager : {
        baseInfoForm : coreElnTmpl.baseUrl + '/{id}',
        dsGrid : coreElnTmpl.baseUrl + '/{id}/dses/queries/raw',
        versionGrid : coreElnTmpl.baseUrl + '/{id}/versions/queries/raw'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-eln-tmpl-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'core-eln-tmpl-ds-list-grid' : this.getCompUrl('dsGrid'),
            'core-eln-tmpl-ver-list-grid' : this.getCompUrl('versionGrid')
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

    insertDs : function() {
        var _this = this;
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('core-eln-ds').title,
            url : Gikam.choose.getConfig('core-eln-ds').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_CORE_ELN_TMPL_DS',
                targetDsIdColumn : 'ELNDSID',
                targetFilterColumn : 'ELNTMPLID',
                targetFilterValue : _this.param.id,
                SCOPE_SEQ : 'protect',
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        elnDsId : item.id,
                        elnDsName : item.elnDsName,
                        elnTmplId : _this.param.id,
                    }
                });
                var url = _this.getUrl('/dses');
                _this.dsGrid.insert(url, chooseData);
            }
        });
    },

    deleteDs : function() {
        var _this = this;
        _this.dsGrid.deleteRows(_this.getUrl('/dses'));
    },

    getUrl : function(url) {
        var data = this.baseForm.getData();
        return Gikam.printf(coreElnTmpl.baseUrl + '/{id}' + url, {
            id : data.id
        });
    },

    editTmpl : function() {
        var _this = this;
        if (parent.isOpenEln == '1') {
            Gikam.alert('CORE.MODULE.ITEM.ELN.TMPLS.TIP.CLOSE_ELN');
            return;
        }
        var versionRows = _this.versionGrid.getSelections();
        if (versionRows.length != 1) {
            Gikam.alert('CORE.MODULE.ITEM.ELN.TMPLS.TIP.SELECT_ONE_VERSION');
            return;
        }
        var url = IFM_CONTEXT + '/static/plugin/spread-sheets-11.2.6/designer/index/index.html' + Gikam.param({
            operateType : 0,
            versionId : versionRows[0].id,
            elnTmplId : this.baseForm.getData().id,
            IFM_CONTEXT : IFM_CONTEXT
        })
        window.open(url, 'eln');
    },

    testTmpl : function() {
        var _this = this;
        if (parent.isOpenEln == '1') {
            Gikam.alert('CORE.MODULE.ITEM.ELN.TMPLS.TIP.CLOSE_ELN');
            return;
        }
        var versionRows = _this.versionGrid.getSelections();
        if (versionRows.length != 1) {
            Gikam.alert('CORE.MODULE.ITEM.ELN.TMPLS.TIP.SELECT_ONE');
            return;
        }
        var data = this.baseForm.getData();
        Gikam.create('modal', {
            title : 'CORE.MODULE.ITEM.ELN.TMPLS.TAB.TEST_TMPL_INFO',
            url : coreElnTmpl.baseUrl + '/page/test-tmpl' + Gikam.param({
                id : versionRows[0].id,
                elnTmplId : data.id
            }),
            width : 800,
            height : 300
        });
    },

    insertVersion : function() {
        var _this = this;
        var url = _this.getUrl('/versions');
        _this.versionGrid.insert(url, [ {
            elnTmplId : _this.param.id
        } ]);
    },
}
