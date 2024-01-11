emcSealRule.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-seal-rule-edit-list-grid',
            url : emcSealRule.baseUrl + '/queries/raw',
            service : 'emcSealRuleServiceImpl',
            dbTable : 'T_EMC_SEAL_RULE',
            columns : emcSealRule.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-seal-rule-edit-list-rule-details-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-seal-rule-edit-list-rule-details-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcSealRule.baseUrl + '/{id}/rule-detailss/queries', {
                            id : row.id
                        })
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BASEMDM.SEALRULES.MODAL.ADD',
                        fields : emcSealRule.getEditListColumns(),
                        url : emcSealRule.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-seal-rule-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-seal-rule-edit-list-grid').deleteRows(emcSealRule.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.COPY',
                icon : 'copy',
                onClick : function() {
                    var grid = Gikam.getComp('emc-seal-rule-edit-list-grid');
                    var selections = grid.getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    Gikam.post(emcSealRule.baseUrl, Gikam.getJsonWrapper('', ['', selections])).done(function () {
                        grid.refresh();
                    })
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.MDM.BASEMDM.SEALRULES.BUTTON.ACTIVE',
                icon : 'activate',
                onClick : function() {
                    var grid = Gikam.getComp('emc-seal-rule-edit-list-grid');
                    var selections = grid.getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    var updateData = selections.map(function (item) {
                        return {
                            id: item.id,
                            activedFlag: '1'
                        }
                    });
                    Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper('', ['emcSealRuleServiceImpl', updateData])).done(function () {
                        grid.refresh();
                    })
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.MDM.BASEMDM.SEALRULES.BUTTON.DISACTIVE',
                icon : 'cancel',
                onClick : function() {
                    var grid = Gikam.getComp('emc-seal-rule-edit-list-grid');
                    var selections = grid.getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    var updateData = selections.map(function (item) {
                        return {
                            id: item.id,
                            activedFlag: '0'
                        }
                    });
                    Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper('', ['emcSealRuleServiceImpl', updateData])).done(function () {
                        grid.refresh();
                    })
                }
            } ]
        }
    },

    getDetailsGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.SEALRULES.TAB.RULE_DETAILS',
                items : [ {
                    type : 'grid',
                    id : 'emc-seal-rule-edit-list-rule-details-grid',
                    service : 'emcSealRuleDetailsServiceImpl',
                    dbTable : 'T_EMC_SEAL_RULE_DETAILS',
                    columns : emcSealRule.getRuleDetailsListColumns(),
                    onCellClick : function(field, row) {
                        field === 'ext$.filename' && Gikam.openFile(row.ext$.fileid, true);
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            var rule = Gikam.getComp('emc-seal-rule-edit-list-grid').getActivedRow();
                            if (Gikam.isEmpty(rule)) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                return;
                            }
                            Gikam.getComp('emc-seal-rule-edit-list-rule-details-grid').insert(Gikam.printf(emcSealRule.baseUrl + '/{id}/rule-detailss', {
                                id : rule.id
                            }), [ {} ]);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            var rule = Gikam.getComp('emc-seal-rule-edit-list-grid').getActivedRow();
                            if (Gikam.isEmpty(rule)) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                return;
                            }
                            Gikam.getComp('emc-seal-rule-edit-list-rule-details-grid').deleteRows(Gikam.printf(emcSealRule.baseUrl + '/{id}/rule-detailss', {
                                id : rule.id
                            }));
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.FILE.BUTTON.UPLOAD_FILE',
                        icon : 'upload',
                        onClick : function() {
                            _this.uploadDetailsFile();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.FILE.BUTTON.DELETE_FILE',
                        icon : 'file-remove',
                        onClick : function() {
                            _this.deleteDetailsFile();
                        }
                    } ]
                } ]
            } ]
        }
    },

    uploadDetailsFile : function() {
        var grid = Gikam.getComp('emc-seal-rule-edit-list-rule-details-grid');
        var row = grid.getActivedRow();
        if (!Gikam.isEmpty(row)) {
            var bizId = row.id;
            Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
                f : {
                    targetId : 'T_EMC_SEAL_RULE_DETAILS$' + bizId
                },
                n : -1,
                s : 50
            })).done(function(r) {
                if (r.total > 0) {
                    Gikam.confirm('提示', '已存在附件，确认覆盖？', function() {
                        Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', r.rows ])).done(function(r) {
                            Gikam.create('simpleUploader', {
                                dbTable : 'T_EMC_SEAL_RULE_DETAILS',
                                bizId : bizId,
                                multiple : false,
                                onAfterClose : function() {
                                    grid.refresh()
                                }
                            });
                        });
                    })
                } else {
                    Gikam.create('simpleUploader', {
                        dbTable : 'T_EMC_SEAL_RULE_DETAILS',
                        bizId : bizId,
                        multiple : false,
                        onAfterClose : function() {
                            grid.refresh()
                        }
                    });
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    deleteDetailsFile : function() {
        var grid = Gikam.getComp('emc-seal-rule-edit-list-rule-details-grid');
        var selections = grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.confirm('提示', '确认删除所选信息的附件？', function() {
            workspace.window.showMask(true);
            var ids = rows.map(function(item) {
                return {
                    id : item.ext$.fileid
                }
            });
            Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', ids ])).done(function() {
                grid.refresh()
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-seal-rule-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '38%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getDetailsGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
