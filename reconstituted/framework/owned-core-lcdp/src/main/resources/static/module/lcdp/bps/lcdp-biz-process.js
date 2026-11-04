var lcdpBizProcess = Gikam.getPageObject({

    menuIconsList : [],

    todoIconsList : [],

    getEditListColumns : function () {
        return [ {
            checkbox : true
        }, {
            field : 'bpCode',
            title : 'T_LCDP_BIZ_PROCESS.BPCODE',
            type : 'link'
        }, {
            field : 'bpName',
            title : 'T_LCDP_BIZ_PROCESS.BPNAME'
        }, {
            field : 'tableName',
            title : 'T_LCDP_BIZ_PROCESS.TABLENAME'
        }, {
            field : 'statusField',
            title : 'T_LCDP_BIZ_PROCESS.STATUSFIELD'
        }, {
            field : 'statusDisplayField',
            title : 'T_LCDP_BIZ_PROCESS.STATUSDISPLAYFIELD'
        }, {
            field : 'rejectField',
            title : 'T_LCDP_BIZ_PROCESS.REJECTFIELD'
        }, {
            field : 'version',
            title : 'T_LCDP_BIZ_PROCESS.VERSION'
        }, {
            field : 'previewProcess',
            title : 'T_LCDP_BIZ_PROCESS.PREVIEW_PROCESS',
            type : 'link',
            formatter : function () {
                return Gikam.propI18N('LCDP.MODULE.BPS.BUTTON.PROCESSVIEW');
            }
        }, {
            field : 'activatedFlag',
            title : 'T_LCDP_BIZ_PROCESS.ACTIVATEDFLAG',
            type : 'select',
            category : 'yesorno'
        }, {
            field : 'parentMenuName',
            title : 'T_LCDP_BIZ_PROCESS.PARENTMENUNAME'
        }, {
            field : 'resourceName',
            title : 'T_LCDP_BIZ_PROCESS.RESOURCENAME'
        }, {
            field : 'bpDesc',
            title : 'T_LCDP_BIZ_PROCESS.BPDESC'
        } ];
    },

    getPreInsertFormFields : function () {
        return [ {
            field : 'bpCode',
            title : 'T_LCDP_BIZ_PROCESS.BPCODE',
            preInsert : true,
            validators : [ 'remote[' + lcdpBizProcess.baseUrl + '/action/validate-unique]', 'strLength[0,30]', 'notEmpty' ]
        }, {
            field : 'bpName',
            title : 'T_LCDP_BIZ_PROCESS.BPNAME',
            preInsert : true,
            validators : [ 'strLength[0,40]' ]
        }, {
            field : 'tableName',
            title : 'T_LCDP_BIZ_PROCESS.TABLENAME',
            preInsert : true,
            type : 'choose',
            category : 'lcdp-table',
            single : true,
            targetFields : [ {
                tableName : 'tableName'
            } ]
        } ];
    },

    getBaseInfoFormFields : function () {
        return [ {
            field : 'bpCode',
            title : 'T_LCDP_BIZ_PROCESS.BPCODE',
            readonly : true
        }, {
            field : 'bpName',
            title : 'T_LCDP_BIZ_PROCESS.BPNAME',
            validators : [ 'strLength[0,40]', 'notEmpty' ]
        }, {
            field : 'parentMenuName',
            title : 'T_LCDP_BIZ_PROCESS.PARENTMENUNAME',
            type : 'choose',
            category : 'lcdp-menu',
            single : true,
            targetFields : [ {
                parentMenuId : 'id'
            }, {
                parentMenuName : 'menuName'
            } ]
        }, {
            field : 'resourceName',
            title : 'T_LCDP_BIZ_PROCESS.RESOURCENAME',
            type : 'choose',
            category : 'pageChoose',
            single : true,
            targetFields : [ {
                resourceId : 'id'
            }, {
                resourceName : 'resourceName'
            }, {
                resourcePath : 'path'
            } ],
            validators : [ 'notEmpty' ]
        }, {
            field : 'tableName',
            title : 'T_LCDP_BIZ_PROCESS.TABLENAME',
            type : 'choose',
            category : 'lcdp-table',
            single : true,
            targetFields : [ {
                tableName : 'tableName'
            } ],
            validators : [ 'notEmpty' ],
            onChange : function (field, value) {
                Gikam.post(IFM_CONTEXT + "/secure/core/module/lcdp/tables/fields/queries/physical", Gikam.getJsonWrapper({f : {TABLENAME_CISEQ : value}}))
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

                        Gikam.getComp('lcdp-biz-process-detail-base-info-form').getField('statusField').setValue('');
                        Gikam.getComp('lcdp-biz-process-detail-base-info-form').getField('statusDisplayField').setValue('');
                        Gikam.getComp('lcdp-biz-process-detail-base-info-form').getField('rejectField').setValue('');
                    })
            }
        }, {
            field : 'version',
            title : 'T_LCDP_BIZ_PROCESS.VERSION',
            readonly : true
        }, {
            field : 'statusField',
            title : 'T_LCDP_BIZ_PROCESS.STATUSFIELD',
            search : true,
            validators : [ 'notEmpty' ],
            type : 'select'
        }, {
            field : 'statusDisplayField',
            title : 'T_LCDP_BIZ_PROCESS.STATUSDISPLAYFIELD',
            search : true,
            validators : [ 'notEmpty' ],
            type : 'select'
        }, {
            field : 'rejectField',
            title : 'T_LCDP_BIZ_PROCESS.REJECTFIELD',
            search : true,
            validators : [ 'notEmpty' ],
            type : 'select'
        }, {
            field : 'bpDesc',
            title : 'T_LCDP_BIZ_PROCESS.BPDESC',
            validators : [ 'strLength[0,100]' ],
            type : 'textarea',
            colspan : 3,
            height : 50
        } ];
    },

    getBpNodeListColumns : function () {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'menuAddFlag',
            title : 'T_LCDP_BIZ_PROCESS_NODE.MENUADDFLAG',
            type : 'select',
            width : 85,
            category : 'yesorno'
        }, {
            field : 'bpNodeCode',
            title : 'T_LCDP_BIZ_PROCESS_NODE.BPNODECODE',
            width : 85
        }, {
            field : 'bpNodeName',
            title : 'T_LCDP_BIZ_PROCESS_NODE.BPNODENAME',
            editor : true,
            width : 85,
            validators : [ 'strLength[0,40]', 'notEmpty' ]
        }, {
            field : 'bpNodeDesc',
            title : 'T_LCDP_BIZ_PROCESS_NODE.BPNODEDESC',
            editor : true,
            type : 'richText',
            validators : [ 'strLength[0,120]' ]
        } ];
    },

    getBpNodeInfoFormFields : function () {
        var bpId = lcdpBizProcess.detailPage.param.id;
        return [ {
            field : 'bpNodeCode',
            title : 'T_LCDP_BIZ_PROCESS_NODE.BPNODECODE',
            preInsert : true,
            validators : [ 'unique[' + lcdpBizProcess.baseUrl + '/' + bpId + '/nodes/action/validate-unique]', 'strLength[0,36]', 'notEmpty' ]
        }, {
            field : 'bpNodeName',
            title : 'T_LCDP_BIZ_PROCESS_NODE.BPNODENAME',
            preInsert : true,
            validators : [ 'strLength[0,40]', 'notEmpty' ]
        }, {
            field : 'menuAddFlag',
            title : 'T_LCDP_BIZ_PROCESS_NODE.MENUADDFLAG',
            type : 'select',
            preInsert : true,
            value : '1',
            category : 'yesorno'
        } ];
    },

    getSpecialAllocationFormFields : function () {
        return [ {
            field : 'special',
            title : 'T_LCDP_BIZ_PROCESS_NODE.SPECIAL',
            type : 'select',
            items : [ {
                text : '',
                value : ''
            }, {
                text : Gikam.propI18N('T_LCDP_BIZ_PROCESS_NODE.SUBMITTER'),
                value : 'submitter'
            }, {
                text : Gikam.propI18N('T_LCDP_BIZ_PROCESS_NODE.EXPRESSION'),
                value : 'expression'
            }, {
                text : Gikam.propI18N('T_LCDP_BIZ_PROCESS_NODE.SELECTSQL'),
                value : 'selectSQL'
            }, {
                text : Gikam.propI18N('T_LCDP_BIZ_PROCESS_NODE.FILTERSQL'),
                value : 'filterSQL'
            } ],
            onChange : function (field, value, rowIndex, oldValue, text) {
                switch (value) {
                    case '':
                        this.hideFields([ 'specialExpression' ]);
                        this.setData({special : value, specialExpression : ''});
                        break;
                    case 'submitter':
                        this.hideFields([ 'specialExpression' ]);
                        this.setData({special : value, specialExpression : ''});
                        break;
                    case 'expression':
                        this.showFields([ 'specialExpression' ]);
                        this.setData({special : value, specialExpression : ''});
                        break;
                    case 'selectSQL':
                        this.showFields([ 'specialExpression' ]);
                        this.setData({special : value, specialExpression : ''});
                        break;
                    case 'filterSQL':
                        this.showFields([ 'specialExpression' ]);
                        this.setData({special : value, specialExpression : ''});
                        break;
                }
            }
        }, {
            field : 'specialExpression',
            title : 'T_LCDP_BIZ_PROCESS_NODE.EXPRESSION'
        } ];
    },

    getBpNodeUserListColumns : function () {
        return [ {
            checkbox : true
        }, {
            field : 'userId',
            title : 'T_LCDP_BIZ_PROCESS_NODE_USER.USERID'
        }, {
            field : 'userName',
            title : 'T_LCDP_BIZ_PROCESS_NODE_USER.USERNAME'
        } ];
    },

    getBpNodeRoleListColumns : function () {
        return [ {
            checkbox : true
        }, {
            field : 'roleName',
            title : 'T_LCDP_BIZ_PROCESS_NODE_ROLE.ROLENAME'
        } ];
    },

    getBpNodeNameField : function () {
        if (!Gikam.isTrue(lcdpBizProcess.detailPage.param.readonly)) {
            return {
                field : 'nextBpNodeCode',
                title : 'T_LCDP_BIZ_PROCESS_NODE_ACTION.NEXTBPNODECODE',
                editor : true,
                validators : [ 'notEmpty' ],
                type : 'select',
                onBeforeEditorRender : function () {
                    var nodeList = Gikam.getComp('lcdp-biz-process-detail-node-grid').getData();
                    var selectItems = nodeList.map(function (node) {
                        return {
                            text : node.bpNodeName,
                            value : node.bpNodeCode
                        }
                    });
                    return {
                        type : 'select',
                        items : selectItems
                    }
                },
                onChange : function (field, value, rowIndex) {
                    let grid = Gikam.getComp('lcdp-biz-process-detail-node-action-grid');
                    var nodeList = Gikam.getComp('lcdp-biz-process-detail-node-grid').getData();
                    nodeList.forEach(function (node) {
                        if (node.bpNodeCode === value) {
                            grid.setData({
                                index : rowIndex,
                                nextBpNodeId : node.id,
                                nextBpNodeCode : node.bpNodeCode,
                                nextBpNodeName : node.bpNodeName
                            })
                        }
                    });
                    lcdpBizProcess.detailPage.refreshDiagramViewer(lcdpBizProcess.detailPage.param.id, null, null);
                }
            }
        } else {
            return {
                field : 'nextBpNodeCode',
                title : 'T_LCDP_BIZ_PROCESS_NODE_ACTION.NEXTBPNODECODE',
                formatter : function (i, v, row) {
                    return row.nextBpNodeName;
                }
            }
        }
    },

    getScriptEditFormFields : function () {
        return [];
    },

    showBpNodeActionText : function (v, row, scriptType) {
        var name = "LCDP.MODULE.BPS.TITLE.SCRIPT_VIEW";
        if (Gikam.isEmpty(v)) {
            name = "LCDP.MODULE.BPS.TITLE.SCRIPT_ADD"
        }
        return Gikam.printf('<a href="javascript:;" onclick="lcdpBizProcess.detailPage.loadScriptPage(\'{scriptType}\',\'{actionId}\')">{name}</a>', {
            scriptType : scriptType,
            actionId : row.id,
            name : Gikam.propI18N(name)
        });
    },

    getBpNodeActionListColumns : function () {
        var _this = this;
        return [ {
            checkbox : true
        }, {
            field : 'bpActionCode',
            title : 'T_LCDP_BIZ_PROCESS_NODE_ACTION.BPACTIONCODE',
            editor : true,
            preInsert : true,
            validators : [ 'strLength[0,36]', 'notEmpty' ]
        }, {
            field : 'bpActionName',
            title : 'T_LCDP_BIZ_PROCESS_NODE_ACTION.BPACTIONNAME',
            editor : true,
            preInsert : true,
            validators : [ 'strLength[0,20]', 'notEmpty' ]
        }, {
            field : 'defaultFlag',
            title : 'T_LCDP_BIZ_PROCESS_NODE_ACTION.DEFAULTFLAG',
            type : 'select',
            category : 'yesorno'
        }, {
            field : 'orderNo',
            title : 'T_LCDP_BIZ_PROCESS_NODE_ACTION.ORDERNO',
            type : 'number',
            editor : true,
            isInteger : true,
            validators : [ 'numRange(0,99)' ]
        }, _this.getBpNodeNameField(), {
            field : 'actionScript',
            title : 'T_LCDP_BIZ_PROCESS_NODE_ACTION.ACTIONSCRIPT',
            formatter : function (i, v, row) {
                return _this.showBpNodeActionText(v, row, 'action');
            }
        }, {
            field : 'checkScriptJs',
            title : 'T_LCDP_BIZ_PROCESS_NODE_ACTION.CHECKSCRIPTJS',
            formatter : function (i, v, row) {
                return _this.showBpNodeActionText(v, row, 'checkJs');
            }
        }, {
            field : 'checkScriptJava',
            title : 'T_LCDP_BIZ_PROCESS_NODE_ACTION.CHECKSCRIPTJAVA',
            formatter : function (i, v, row) {
                return _this.showBpNodeActionText(v, row, 'checkJava');
            }
        }, {
            field : 'rejectFlag',
            title : 'T_LCDP_BIZ_PROCESS_NODE_ACTION.REJECTFLAG',
            editor : true,
            preInsert : true,
            type : 'select',
            category : 'yesorno',
            firstBlank : false
        }, {
            field : 'elecSignFlag',
            title : 'T_LCDP_BIZ_PROCESS_NODE_ACTION.ELECSIGNFLAG',
            editor : true,
            type : 'select',
            category : 'yesorno',
            firstBlank : false
        }, {
            field : 'counterSignFlag',
            title : 'T_LCDP_BIZ_PROCESS_NODE_ACTION.COUNTERSIGNFLAG',
            editor : true,
            type : 'select',
            category : 'yesorno',
            firstBlank : false,
            onChange : function (field, value) {
                if (value === '1') {
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
            }
        }, {
            field : 'remark',
            title : 'T_LCDP_BIZ_PROCESS_NODE_ACTION.REMARK',
            editor : true,
            type : 'richText',
            validators : [ 'strLength[0,100]' ]
        } ];
    },

    getBpNodeActionInfoFormFields : function () {
        return [ {
            field : 'bpActionCode',
            title : 'T_LCDP_BIZ_PROCESS_NODE_ACTION.BPACTIONCODE',
            preInsert : true,
            validators : [ 'strLength[0,36]' ]
        }, {
            field : 'bpActionName',
            title : 'T_LCDP_BIZ_PROCESS_NODE_ACTION.BPACTIONNAME',
            preInsert : true,
            validators : [ 'strLength[0,20]' ]
        } ];
    },

    getSignUserListColumns : function () {
        return [ {
            checkbox : true
        }, {
            field : 'userId',
            title : 'T_LCDP_BP_NODE_SIGN_USER.USERID'
        }, {
            field : 'userName',
            title : 'T_LCDP_BP_NODE_SIGN_USER.USERNAME'
        } ];
    },

    getBpNodeMenuInfoFormFields : function () {
        var _this = this;
        return [ {
            field : 'parentMenuName',
            title : 'T_LCDP_BIZ_PROCESS_NODE_MENU.PARENTMENUNAME',
            type : 'choose',
            category : 'lcdp-menu',
            single : true,
            targetFields : [ {
                parentMenuId : 'id'
            }, {
                parentMenuName : 'menuName'
            } ]
        }, {
            field : 'menuName',
            title : 'T_LCDP_BIZ_PROCESS_NODE_MENU.MENUNAME',
            validators : [ 'strLength[0,40]' ]
        }, {
            field : 'englishMenuName',
            title : 'T_LCDP_BIZ_PROCESS_NODE_MENU.ENGLISHMENUNAME',
            validators : [ 'strLength[0,40]' ]
        }, {
            field : 'chineseMenuName',
            title : 'T_LCDP_BIZ_PROCESS_NODE_MENU.CHINESEMENUNAME',
            validators : [ 'strLength[0,40]' ]
        }, {
            field : 'resourceName',
            title : 'T_LCDP_BIZ_PROCESS_NODE_MENU.RESOURCENAME',
            type : 'choose',
            category : 'pageChoose',
            single : true,
            targetFields : [ {
                resourceId : 'id'
            }, {
                resourceName : 'resourceName'
            }, {
                resourcePath : 'path'
            } ],
            validators : [ 'notEmpty' ]
        }, {
            field : 'menuParam',
            title : 'T_LCDP_BIZ_PROCESS_NODE_MENU.MENUPARAM',
            type : 'codeEditor',
            language : 'json',
            colspan : 3,
            rows : 2,
            height : 50
        }, {
            field : 'clientScript',
            title : 'T_LCDP_BIZ_PROCESS_NODE_MENU.CLIENTSCRIPT',
            type : 'codeEditor',
            language : 'javascript',
            colspan : 3,
            rows : 5,
            height : 100
        }, {
            field : 'todoScript',
            title : 'T_LCDP_BIZ_PROCESS_NODE_MENU.TODOSCRIPT',
            type : 'codeEditor',
            language : 'sql',
            colspan : 3,
            rows : 3,
            height : 50
        }, {
            field : 'ext$.menuiconurl',
            title : 'T_CORE_MENU.MENUICON',
            type : 'image',
            height : 30,
            align : 'center',
            enlarged : false,
            onClick : function () {
                _this.getShowImgPanel('menuIconUrl');
            }
        }, {
            field : 'ext$.todoiconurl',
            title : 'T_CORE_MENU.TODOICON',
            type : 'image',
            height : 30,
            align : 'center',
            enlarged : false,
            onClick : function (img) {
                _this.getShowImgPanel('todoIconUrl')
            }
        } ];
    },

    getShowImgPanel : function (field) {
        var _this = this;
        var listObj = {
            menuIconUrl : this.menuIconsList,
            todoIconUrl : this.todoIconsList
        }
        var urlObj = {
            menuIconUrl : '/secure/core/module/sys/menus/icons/queries',
            todoIconUrl : '/secure/core/module/sys/menus/todo-icons/queries'
        };
        if (listObj[ field ].length === 0) {
            Gikam.getJson(urlObj[ field ]).done(function (data) {
                data.forEach(function (item) {
                    item.id = item.iconName;
                    item.src = IFM_CONTEXT + item.iconUrl;
                });
                field === 'menuIconUrl' && (_this.menuIconsList = data);
                field === 'todoIconUrl' && (_this.todoIconsList = data);
                _this.imgPanelHandle(field, false);
            }).fail(function (e) {
                _this.imgPanelHandle(field, true);
            });
        } else {
            this.imgPanelHandle(field, false);
        }
    },

    imgPanelHandle : function (field, error) {
        var _this = this;
        var listObj = {
            menuIconUrl : this.menuIconsList,
            todoIconUrl : this.todoIconsList
        }
        var titleObj = {
            menuIconUrl : '菜单图标配置',
            todoIconUrl : '待办图标配置'
        }
        var iconPanel = Gikam.create('iconPanel', {
            renderTo : workspace.window.$dom,
            id : 'core-menu-edit-list-icon-panel',
            title : titleObj[ field ],
            data : listObj[ field ],
            noRequest : error,
            onConfirm : function (data) {
                var form = Gikam.getComp('lcdp-biz-process-detail-node-menu-form');
                var iconUrl = data.iconUrl ? data.iconUrl.replace(/blue|black/g, 'white') : error ? '/static/icon/menu/peony/white/' + data.id + '.svg' : null;
                field === 'menuIconUrl' && form.setData({
                    icon : error ? data.id + '.svg' : data.iconName,
                    ext$ : {
                        menuiconurl : iconUrl
                    }
                });
                field === 'todoIconUrl' && form.setData({
                    todoIcon : data.iconName,
                    ext$ : {
                        todoiconurl : iconUrl
                    }
                });
                iconPanel.cancel();
            }
        });
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/lcdp/bps'
});