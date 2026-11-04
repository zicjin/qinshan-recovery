lcdpBizProcess.detailPage = {

    getBtnToolbar : function () {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function () {
                    workspace.window.goBack();
                }
            } ]
        }
    },

    getBaseInfoTab : function () {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'LCDP.MODULE.BPS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    columns : 3,
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'lcdpBizProcessServiceImpl',
                    id : 'lcdp-biz-process-detail-base-info-form',
                    readonly : _this.param.readonly,
                    fields : lcdpBizProcess.getBaseInfoFormFields(),
                    onLoadSuccess : function (data) {
                        if (!Gikam.isEmpty(data)) {
                            if (data.tableName && data.tableName != '') {
                                Gikam.post(IFM_CONTEXT + "/secure/core/module/lcdp/tables/fields/queries/physical", Gikam.getJsonWrapper({f : {TABLENAME_CISEQ : data.tableName}}))
                                    .done(function (datas) {
                                        var selectItems = datas.map(function (node) {
                                            return {
                                                text : node.fieldName,
                                                value : node.fieldName
                                            }
                                        });
                                        Gikam.getComp('lcdp-biz-process-detail-base-info-form').getField('statusField').setItems(selectItems);
                                        Gikam.getComp('lcdp-biz-process-detail-base-info-form').getField('statusDisplayField').setItems(selectItems);
                                        Gikam.getComp('lcdp-biz-process-detail-base-info-form').getField('rejectField').setItems(selectItems);
                                    })
                            }

                            if (data.activatedFlag == '1') {
                                Gikam.getComp('lcdp-biz-process-detail-node-special-form').setReadonly(true);
                            } else {
                                Gikam.getComp('lcdp-biz-process-detail-node-special-form').setReadonly(false);
                            }
                        }
                    }
                } ]
            } ]
        }
    },

    urlManager : {
        baseInfoForm : lcdpBizProcess.baseUrl + '/{id}'
    },

    getCompUrl : function (name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[ name ], {
            id : this.param.id
        });
    },

    setCompUrl : function () {
        Gikam.setCompUrl({
            'lcdp-biz-process-detail-base-info-form' : this.getCompUrl('baseInfoForm')
        });
    },

    getLeftTab : function () {
        var _this = this;
        return {
            type : 'tab',
            panels : [ _this.getBpNodeGrid() ]
        }
    },

    getBpNodeGrid : function () {
        var _this = this;
        return {
            title : 'LCDP.MODULE.BPS.TAB.PROCESS_NODE',
            items : [ {
                type : 'grid',
                id : 'lcdp-biz-process-detail-node-grid',
                service : 'lcdpBizProcessNodeServiceImpl',
                url : Gikam.printf(lcdpBizProcess.baseUrl + '/{id}/nodes/queries', {
                    id : _this.param.id
                }),
                activateFirstRow : true,
                page : false,
                deleteFormatter : function (row) {
                    return row.bpNodeName;
                },
                onLoadSuccess : function (data) {
                    if (!Gikam.isEmpty(data)) {
                        this.activeRowByIndex(0);
                    } else {
                        Gikam.cleanCompData([ "lcdp-biz-process-detail-node-user-grid",
                            "lcdp-biz-process-detail-node-role-grid",
                            "lcdp-biz-process-detail-node-action-grid",
                            "lcdp-biz-process-detail-node-menu-form",
                            "lcdp-biz-process-detail-node-special-form",
                            "lcdp-biz-process-detail-sign-user-grid" ]);
                    }
                },
                onRowActive : function (index, row) {
                    Gikam.getAsyncComp('lcdp-biz-process-detail-node-user-grid').done(function (grid) {
                        grid.refresh({
                            url : Gikam.printf(lcdpBizProcess.baseUrl + '/nodes/{id}/users/queries', {
                                id : row.id
                            })
                        });
                    });

                    Gikam.getAsyncComp('lcdp-biz-process-detail-node-role-grid').done(function (grid) {
                        grid.refresh({
                            url : Gikam.printf(lcdpBizProcess.baseUrl + '/nodes/{id}/roles/queries', {
                                id : row.id
                            })
                        });
                    });

                    Gikam.getAsyncComp('lcdp-biz-process-detail-node-action-grid').done(function (grid) {
                        grid.refresh({
                            url : Gikam.printf(lcdpBizProcess.baseUrl + '/nodes/{id}/actions/queries', {
                                id : row.id
                            })
                        });
                    });

                    Gikam.getComp('lcdp-biz-process-detail-node-menu-form').refresh({
                        url : Gikam.printf(lcdpBizProcess.baseUrl + '/nodes/{id}/menu-info', {
                            id : row.id
                        })
                    });

                    Gikam.getComp('lcdp-biz-process-detail-node-special-form').refresh({
                        url : Gikam.printf(lcdpBizProcess.baseUrl + '/nodes/{bpNodeId}', {
                            bpNodeId : row.id
                        })
                    });

                    if (!Gikam.isTrue(_this.param.readonly)) {
                        if (row.menuAddFlag === '0') {
                            Gikam.getComp('lcdp-biz-process-detail-node-user-grid').hideToolbar();
                            Gikam.getComp('lcdp-biz-process-detail-node-role-grid').hideToolbar();
                            Gikam.getComp('lcdp-biz-process-detail-node-action-grid').hideToolbar();
                            Gikam.getComp('lcdp-biz-process-detail-node-special-form').setReadonly(true);
                            Gikam.getComp('lcdp-biz-process-detail-node-menu-form').setReadonly(true);
                        } else {
                            Gikam.getComp('lcdp-biz-process-detail-node-user-grid').showToolbar();
                            Gikam.getComp('lcdp-biz-process-detail-node-role-grid').showToolbar();
                            Gikam.getComp('lcdp-biz-process-detail-node-action-grid').showToolbar();
                            Gikam.getComp('lcdp-biz-process-detail-node-special-form').setReadonly(false);
                            Gikam.getComp('lcdp-biz-process-detail-node-menu-form').setReadonly(false);
                        }
                    }

                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add-row',
                    onClick : function () {
                        var bpId = _this.param.id;

                        Gikam.preInsert({
                            modalTitle : 'LCDP.MODULE.BPS.MODAL.ADD_BPNODE',
                            fields : lcdpBizProcess.getBpNodeInfoFormFields(),
                            url : Gikam.printf(lcdpBizProcess.baseUrl + '/{bpId}/nodes', {
                                bpId : bpId
                            }),
                            titleWidth : 100
                        }).done(function (id) {
                            _this.refreshDiagramViewer(_this.param.id, null, null);
                            Gikam.getComp('lcdp-biz-process-detail-node-grid').refresh();
                        });
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function () {
                        Gikam.getComp('lcdp-biz-process-detail-node-grid').deleteRows(lcdpBizProcess.baseUrl + '/nodes').done(function () {
                            _this.refreshDiagramViewer(_this.param.id, null, null);
                        });
                    }
                } ],
                columns : lcdpBizProcess.getBpNodeListColumns()
            } ]
        };
    },

    getRightTab : function () {
        var _this = this;
        return {
            type : 'tab',
            panels : [ _this.getActionTab(), _this.getBpNodeMenuTab(), _this.getPermissionsTab() ]
        }
    },

    getBpNodeMenuTab : function () {
        var _this = this;
        return {
            title : 'LCDP.MODULE.BPS.FORM.PROCESS_NODE_MENU',
            items : [ {
                type : 'form',
                id : 'lcdp-biz-process-detail-node-menu-form',
                service : 'lcdpBizProcessNodeMenuServiceImpl',
                dbTable : 'T_LCDP_BIZ_PROCESS_NODE_MENU',
                autoSave : true,
                fields : lcdpBizProcess.getBpNodeMenuInfoFormFields(),
                onUpdated : function (data) {
                    if (data.hasOwnProperty('menuName')) {
                        this.setData({chineseMenuName : data.menuName});
                        this.setData({regenerateFlag : '1'});
                    }

                    if (data.hasOwnProperty('parentMenuName')) {
                        this.setData({regenerateFlag : '1'});
                    }
                }
            } ]
        };
    },

    getActionTab : function () {
        var _this = this;
        return {
            title : 'LCDP.MODULE.BPS.TAB.PROCESS_NODE_ACTION',
            items : [ {
                type : 'layout',
                id : 'lcdp-biz-process-detail-node-action-layout',
                center : {
                    items : [ _this.getBpNodeActionGrid() ]
                },
                south : {
                    height : '0%',
                    items : [ _this.getSignUserGrid() ]
                }
            } ]
        }
    },

    getSignUserGrid : function () {
        var _this = this;

        return {
            type : 'tab',
            id : 'lcdp-biz-process-detail-sign-user-tab',
            panels : [ {
                title : 'LCDP.MODULE.BPS.TAB.COUNTER_SIGN_USER',
                items : [ {
                    type : 'grid',
                    id : 'lcdp-biz-process-detail-sign-user-grid',
                    service : 'lcdpBpNodeSignUserServiceImpl',
                    dbTable : 'T_LCDP_BP_NODE_SIGN_USER',
                    page : false,
                    deleteFormatter : function (row) {
                        return row.userId;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function () {
                            Gikam.create('modal', {
                                title : 'LCDP.MODULE.BPS.MODAL.ADD_BPCOUNTERSIGNUSER',
                                url : IFM_CONTEXT + Gikam.choose.getConfig('user').url + Gikam.param({
                                    target_filter : encodeURI(JSON.stringify({
                                        type : 'unassigned',
                                        targetTable : 'T_LCDP_BP_NODE_SIGN_USER',
                                        targetMatchColumn : 'USERID',
                                        thisMatchColumn : 'ID',
                                        filter : [ {
                                            targetFilterColumn : 'BPACTIONID',
                                            targetFilterValue : Gikam.getComp('lcdp-biz-process-detail-node-action-grid').getActivedRow().id
                                        } ]
                                    }))
                                }),
                                onAfterClose : function (row) {
                                    if (row && row.length > 0) {
                                        var chooseData = row.map(function (e) {
                                            return {
                                                bpId : _this.param.id,
                                                bpNodeId : Gikam.getComp('lcdp-biz-process-detail-node-grid').getActivedRow().id,
                                                bpActionId : Gikam.getComp('lcdp-biz-process-detail-node-action-grid').getActivedRow().id,
                                                userId : e.id,
                                                userName : e.userName
                                            }
                                        });
                                    }
                                    if (chooseData) {
                                        Gikam.post(lcdpBizProcess.baseUrl + '/sign-users', Gikam.getJsonWrapper(null, [ 'lcdpBpNodeSignUserServiceImpl', chooseData ])).done(function () {
                                            Gikam.getComp('lcdp-biz-process-detail-sign-user-grid').refresh();
                                        });
                                    }
                                }
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function () {
                            Gikam.getComp('lcdp-biz-process-detail-sign-user-grid').deleteRows(lcdpBizProcess.baseUrl + '/sign-users');
                        }
                    } ],
                    columns : lcdpBizProcess.getSignUserListColumns()
                } ]
            } ]
        }
    },

    getBpNodeActionGrid : function () {
        var _this = this;
        return {
            type : 'grid',
            id : 'lcdp-biz-process-detail-node-action-grid',
            service : 'lcdpBizProcessNodeActionServiceImpl',
            dbTable : 'T_LCDP_BIZ_PROCESS_NODE_ACTION',
            deleteFormatter : function (row) {
                return row.bpActionCode;
            },
            page : false,
            activateFirstRow : true,
            onLoadSuccess : function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "lcdp-biz-process-detail-sign-user-grid" ]);
                }
            },
            onUpdated : function (data, fields) {
                if (fields.indexOf("orderNo") > -1) {
                    Gikam.getComp('lcdp-biz-process-detail-node-action-grid').refresh();
                }
            },
            onRowActive : function (index, row) {
                if (row.counterSignFlag === '1') {
                    Gikam.getComp('lcdp-biz-process-detail-sign-user-tab').show();
                    Gikam.getComp('lcdp-biz-process-detail-node-action-layout').setSize({
                        south : {
                            height : '50%',
                        }
                    });
                    Gikam.getComp('lcdp-biz-process-detail-bp-node-layout').setSize({
                        height : 560
                    });
                } else {
                    Gikam.getComp('lcdp-biz-process-detail-sign-user-tab').hide();
                    Gikam.getComp('lcdp-biz-process-detail-node-action-layout').setSize({
                        south : {
                            height : '0%',
                        }
                    });
                    Gikam.getComp('lcdp-biz-process-detail-bp-node-layout').setSize({
                        height : 280
                    });
                }

                Gikam.getAsyncComp('lcdp-biz-process-detail-sign-user-grid').done(function (grid) {
                    grid.refresh({
                        url : Gikam.printf(lcdpBizProcess.baseUrl + '/node-actions/{id}/sign-users/queries', {
                            id : row.id
                        })
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add-row',
                onClick : function () {
                    Gikam.preInsert({
                        modalTitle : 'LCDP.MODULE.BPS.MODAL.ADD_BPNODEACTION',
                        fields : lcdpBizProcess.getBpNodeActionInfoFormFields(),
                        url : lcdpBizProcess.baseUrl + '/node-actions',
                        param : {
                            bpId : _this.param.id,
                            bpNodeId : Gikam.getComp('lcdp-biz-process-detail-node-grid').getActivedRow().id
                        }
                    }).done(function (id) {
                        Gikam.getComp('lcdp-biz-process-detail-node-action-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function () {
                    Gikam.getComp('lcdp-biz-process-detail-node-action-grid').deleteRows(lcdpBizProcess.baseUrl + '/node-actions').done(function () {
                        _this.refreshDiagramViewer(_this.param.id, null, null);
                    });
                }
            }, {
                type : 'button',
                text : 'LCDP.MODULE.BPS.TITLE.SET_DEFAULT',
                icon : 'default',
                onClick : function () {
                    _this.setActionDefault();
                }
            }, {
                type : 'button',
                text : 'LCDP.MODULE.BPS.TITLE.CANCEL_DEFAULT',
                icon : 'cancel',
                onClick : function () {
                    _this.cancelActionDefault();
                }
            } ],
            columns : lcdpBizProcess.getBpNodeActionListColumns()
        };
    },

    setActionDefault : function () {
        var selections = Gikam.getComp('lcdp-biz-process-detail-node-action-grid').getSelections();
        if (selections.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var url = lcdpBizProcess.baseUrl + '/node-actions/action/set-default';
        Gikam.put(url, Gikam.getJsonWrapper({}, [ 'lcdpBizProcessNodeActionServiceImpl', selections ])).done(function () {
            Gikam.getComp('lcdp-biz-process-detail-node-action-grid').refresh();
        });
    },

    cancelActionDefault : function () {
        var selections = Gikam.getComp('lcdp-biz-process-detail-node-action-grid').getSelections();
        if (selections.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var url = lcdpBizProcess.baseUrl + '/node-actions/action/cancel-default';
        Gikam.put(url, Gikam.getJsonWrapper({}, [ 'lcdpBizProcessNodeActionServiceImpl', selections ])).done(function () {
            Gikam.getComp('lcdp-biz-process-detail-node-action-grid').refresh();
        });
    },

    getPermissionsTab : function () {
        var _this = this;
        return {
            title : 'LCDP.MODULE.BPS.TAB.ASSIGN_PERMISSIONS',
            items : [ _this.getBpNodeSpecialAllocationForm(), _this.getBpNodeAssignTab() ]
        }
    },

    getBpNodeAssignTab : function () {
        var _this = this;

        return {
            type : 'layout',
            height : 400,
            west : {
                width : '50%',
                items : [ _this.getBpNodeUserGrid() ]
            },
            center : {
                items : [ _this.getBpNodeRoleGrid() ]
            }
        }
    },

    getBpNodeRoleGrid : function () {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'LCDP.MODULE.BPS.TAB.PROCESS_NODE_ROLE',
                items : [ {
                    type : 'grid',
                    id : 'lcdp-biz-process-detail-node-role-grid',
                    service : 'lcdpBizProcessNodeRoleServiceImpl',
                    dbTable : 'T_LCDP_BIZ_PROCESS_NODE_ROLE',
                    deleteFormatter : function (row) {
                        return row.roleName;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function () {
                            Gikam.create('modal', {
                                title : 'LCDP.MODULE.BPS.MODAL.ADD_BPNODEROLE',
                                url : IFM_CONTEXT + Gikam.choose.getConfig('role').url + Gikam.param({
                                    target_filter : encodeURI(JSON.stringify({
                                        type : 'unassigned',
                                        targetTable : 'T_LCDP_BIZ_PROCESS_NODE_ROLE',
                                        targetMatchColumn : 'ROLEID',
                                        thisMatchColumn : 'ID',
                                        filter : [ {
                                            targetFilterColumn : 'BPNODEID',
                                            targetFilterValue : Gikam.getComp('lcdp-biz-process-detail-node-grid').getActivedRow().id
                                        } ]
                                    }))
                                }),
                                onAfterClose : function (row) {
                                    if (row && row.length > 0) {
                                        var chooseData = row.map(function (e) {
                                            return {
                                                bpId : _this.param.id,
                                                bpNodeId : Gikam.getComp('lcdp-biz-process-detail-node-grid').getActivedRow().id,
                                                roleId : e.id,
                                                roleName : e.roleName
                                            }
                                        });
                                    }
                                    if (chooseData) {
                                        Gikam.post(lcdpBizProcess.baseUrl + '/node-roles', Gikam.getJsonWrapper(null, [ 'lcdpBizProcessNodeRoleService', chooseData ])).done(function () {
                                            Gikam.getComp('lcdp-biz-process-detail-node-role-grid').refresh();
                                        });
                                    }
                                }
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function () {
                            Gikam.getComp('lcdp-biz-process-detail-node-role-grid').deleteRows(lcdpBizProcess.baseUrl + '/node-roles');
                        }
                    } ],
                    columns : lcdpBizProcess.getBpNodeRoleListColumns()
                } ]
            } ]
        }
    },

    getBpNodeUserGrid : function () {
        var _this = this;

        return {
            type : 'tab',
            panels : [ {
                title : 'LCDP.MODULE.BPS.TAB.PROCESS_NODE_USER',
                items : [ {
                    type : 'grid',
                    id : 'lcdp-biz-process-detail-node-user-grid',
                    service : 'lcdpBizProcessNodeUserServiceImpl',
                    dbTable : 'T_LCDP_BIZ_PROCESS_NODE_USER',
                    deleteFormatter : function (row) {
                        return row.userId;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function () {
                            Gikam.create('modal', {
                                title : 'LCDP.MODULE.BPS.MODAL.ADD_BPNODEUSER',
                                url : IFM_CONTEXT + Gikam.choose.getConfig('user').url + Gikam.param({
                                    target_filter : encodeURI(JSON.stringify({
                                        type : 'unassigned',
                                        targetTable : 'T_LCDP_BIZ_PROCESS_NODE_USER',
                                        targetMatchColumn : 'USERID',
                                        thisMatchColumn : 'ID',
                                        filter : [ {
                                            targetFilterColumn : 'BPNODEID',
                                            targetFilterValue : Gikam.getComp('lcdp-biz-process-detail-node-grid').getActivedRow().id
                                        } ]
                                    }))
                                }),
                                onAfterClose : function (row) {
                                    if (row && row.length > 0) {
                                        var chooseData = row.map(function (e) {
                                            return {
                                                bpId : _this.param.id,
                                                bpNodeId : Gikam.getComp('lcdp-biz-process-detail-node-grid').getActivedRow().id,
                                                userId : e.id,
                                                userName : e.userName
                                            }
                                        });
                                    }
                                    if (chooseData) {
                                        Gikam.post(lcdpBizProcess.baseUrl + '/node-users', Gikam.getJsonWrapper(null, [ 'lcdpBizProcessNodeUserService', chooseData ])).done(function () {
                                            Gikam.getComp('lcdp-biz-process-detail-node-user-grid').refresh();
                                        });
                                    }
                                }
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function () {
                            Gikam.getComp('lcdp-biz-process-detail-node-user-grid').deleteRows(lcdpBizProcess.baseUrl + '/node-users');
                        }
                    } ],
                    columns : lcdpBizProcess.getBpNodeUserListColumns()
                } ]
            } ]
        }
    },

    getBpNodeSpecialAllocationForm : function () {
        return {
            type : 'form',
            id : 'lcdp-biz-process-detail-node-special-form',
            service : 'lcdpBizProcessNodeServiceImpl',
            dbTable : 'T_LCDP_BIZ_PROCESS_NODE',
            onLoadSuccess : function (data) {
                if (Gikam.isEmpty(data.special) || data.special === 'submitter') {
                    this.hideFields([ 'specialExpression' ]);
                } else {
                    this.showFields([ 'specialExpression' ]);
                }
            },
            fields : lcdpBizProcess.getSpecialAllocationFormFields()
        }
    },

    loadScriptPage : function (scriptType, actionId) {
        var _this = this;
        Gikam.create('modal', {
            title : 'LCDP.MODULE.BPS.TITLE.SCRIPT_EDIT',
            width : '90%',
            height : '95%',
            url : lcdpBizProcess.baseUrl + '/page/script-edit' + Gikam.param({
                scriptType : scriptType,
                actionId : actionId,
                readonly : _this.param.readonly
            }),
            onAfterClose : function (rows) {
                Gikam.getComp('lcdp-biz-process-detail-node-action-grid').refresh();
            }
        });

    },

    getBpViewerInfoTab : function () {
        var _this = this;

        var url = Gikam.printf(lcdpBizProcess.baseUrl + '/page/diagram-viewer?bpId={bpId}&bizId={bizId}&tableName={tableName}', {
            bpId : _this.param.id,
            bizId : _this.param.bizId,
            tableName : _this.param.tableName
        });
        return {
            type : 'tab',
            panels : [ {
                title : 'LCDP.MODULE.BPS.TAB.BPVIWERINFO',
                items : [ {
                    type : 'iframe',
                    height : 300,
                    id : 'lcdp-biz-process-detail-iframe',
                    src : url
                } ]
            } ]
        }
    },

    refreshDiagramViewer : function (bpId, bizId, tableName) {
        var url = Gikam.printf(lcdpBizProcess.baseUrl + '/page/diagram-viewer?bpId={bpId}&bizId={bizId}&tableName={tableName}&' + new Date().getTime(), {
            bpId : bpId,
            bizId : bizId,
            tableName : tableName
        });

        Gikam.getComp('lcdp-biz-process-detail-iframe').refresh({
            src : url
        });
    },

    getBpNodeTab : function () {
        var _this = this;

        return {
            type : 'layout',
            height : 280,
            id : 'lcdp-biz-process-detail-bp-node-layout',
            west : {
                width : '40%',
                items : [ _this.getLeftTab() ]
            },
            center : {
                items : [ _this.getRightTab() ]
            }
        }
    },

    createPage : function () {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab(), this.getBpNodeTab(), this.getBpViewerInfoTab() ]
            },
            renderTo : workspace.window.$dom
        });
        return this;
    },

    init : function (param) {
        this.param = param;
        this.createPage();
    }
}
