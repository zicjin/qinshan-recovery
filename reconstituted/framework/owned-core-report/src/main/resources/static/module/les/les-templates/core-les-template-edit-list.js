coreLesTemplate.editListPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-les-template-edit-list-grid',
            url : coreLesTemplate.baseUrl + '/queries',
            columns : coreLesTemplate.getEditListColumns(),
            onCellClick : function(field, row) {
                var readonly = !(row.processStatus.indexOf("draft") === 0 && row.templateStatus != 'expired');
                field === 'templateNo' && _this.loadDetailPage(row.id, readonly);
            },
            deleteFormatter : function(row) {
                return row.templateName;
            },
            requestData : {
                processStatus_IN : 'draft,draft_rejected,done',
            },
            onRowActive : function(index, row) {
                if (row) {
                    _this.param.id = row.id;
                    Gikam.getComp('core-les-template-edit-list-used-dept-grid').refresh({
                        url : coreLesTemplate.baseUrl + '/template-orgs/queries',
                        requestData : {
                            templateId_EQ : row.id
                        }
                    });

                    Gikam.getComp('core-les-template-edit-list-uploader').setOptions({
                        bizId : row.id,
                    });
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.ITEM.LES_TEMPLATES.MODAL.NEW_TMPL',
                        fields : coreLesTemplate.getPreInsertFormFields(),
                        url : coreLesTemplate.baseUrl,
                        disableNotEmptyValidate : true,
                        titleWidth : 120
                    }).done(function(id) {
                        _this.loadDetailPage(id, false);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    _this.deleteTemplate();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.LES.LES_TEMPLATES.BUTTON.COPY',
                icon : 'copy',
                onClick : function() {
                    _this.copy();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.LES.LES_TEMPLATES.BUTTON.NEWVERSION',
                icon : 'activate',
                onClick : function() {
                    _this.newVersion();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.LES.LES_TEMPLATES.BUTTON.EXPIRE',
                icon : 'reject',
                onClick : function() {
                    _this.expire();
                }
            } ]
        }
    },

    getUsedDeptGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'CORE.MODULE.LES.LES_TEMPLATES.TAB.USEDDEPT',
                items : [ _this.getUsedDeptGrid() ]
            }, {
                title : 'CORE.MODULE.LES.LES_TEMPLATES.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'core-les-template-edit-list-uploader',
                    dbTable : coreLesTemplate.workflow.dbTable
                } ]
            } ]
        }
    },

    getUsedDeptGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-les-template-edit-list-used-dept-grid',
            columns : coreLesTemplate.getUsedOrgListColumns(),
            page : false,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add-row',
                onClick : function() {
                    if (!Gikam.isEmpty(_this.param.id)) {
                        _this.insertUsedOrgRow();
                    }
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    if (!Gikam.isEmpty(_this.param.id)) {
                        _this.deleteUsedOrgRow();
                    }
                }
            } ],
            deleteFormatter : function(row) {
                return row.ext$.usedorgname || row.id;
            },
        }
    },

    deleteTemplate : function() {
        var selectedRows = Gikam.getComp('core-les-template-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selectedRows)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
            return;
        }
        for (var i = 0; i < selectedRows.length; i++) {
            if (selectedRows[i].processStatus !== 'draft' && selectedRows[i].processStatus !== 'draft_rejected') {
                Gikam.alert('CORE.MODULE.LES.LES_TEMPLATES.TIP.CAN_NOT_DELETE_SUBMIITED_DATA');
                return;
            }
        }
        Gikam.create('signature', {
            showReason : false,
            onConfirm : function(reason) {
                Gikam.getComp('core-les-template-edit-list-grid').deleteRows(coreLesTemplate.baseUrl);
            }
        });
    },

    submit : function() {
        var grid = Gikam.getComp('core-les-template-edit-list-grid');
        var selectedRows = Gikam.getComp('core-les-template-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selectedRows)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
            return;
        }
        for (var i = 0; i < selectedRows.length; i++) {
            if (selectedRows[i].templateStatus === 'expired') {
                Gikam.alert('CORE.MODULE.LES.LES_TEMPLATES.TIP.CAN_NOT_SUBMIT_EXPIRED_DATA');
                return;
            }
            if (selectedRows[i].processStatus !== 'draft' && selectedRows[i].processStatus !== 'draft_rejected') {
                Gikam.alert('CORE.MODULE.LES.LES_TEMPLATES.TIP.CAN_NOT_SUBMIT_SUBMIITED_DATA');
                return;
            }
        }

        Gikam.post(coreLesTemplate.baseUrl + '/action/submit-validate', Gikam.getJsonWrapper({}, [ '', selectedRows ])).done(function(result) {
            if (!result.valid) {
                Gikam.alert(result.message);
                return;
            }
            Gikam.create('workflow').submit({
                data : grid.getSelections(),
                pageObject : coreLesTemplate
            }).done(function() {
                grid.refresh();
            });
        });
    },

    copy : function() {
        var _this = this;
        var selectedRows = Gikam.getComp('core-les-template-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selectedRows) || selectedRows.length > 1) {
            Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
            return;
        }
        Gikam.preInsert({
            modalTitle : 'CORE.MODULE.LES.LES_TEMPLATES.MODEL.COPY',
            fields : coreLesTemplate.getPreInsertFormFields(),
            titleWidth : 120,
            disableNotEmptyValidate : true,
            url : Gikam.printf(coreLesTemplate.baseUrl + '/{id}/action/copy', {
                id : selectedRows[0].id
            }),
        }).done(function(id) {
            _this.loadDetailPage(id, false);
        });
    },

    newVersion : function() {
        var _this = this;
        var selectedRows = Gikam.getComp('core-les-template-edit-list-grid').getSelections();

        if (Gikam.isEmpty(selectedRows)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
            return;
        }

        if (selectedRows.length !== 1) {
            Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
            return;
        }

        if (selectedRows[0].processStatus === 'draft' || selectedRows[0].processStatus === 'draft_rejected') {
            Gikam.confirm('CORE.MODULE.LES.LES_TEMPLATES.MODEL.NEWVERSION', 'CORE.MODULE.LES.LES_TEMPLATES.TIP.DRAFT', function() {
                workspace.window.showMask(true);
                Gikam.preInsert({
                    modalTitle : 'CORE.MODULE.LES.LES_TEMPLATES.MODEL.NEWVERSION',
                    url : Gikam.printf(coreLesTemplate.baseUrl + '/{id}/new-versions', {
                        id : selectedRows[0].id
                    }),
                }).done(function(id) {
                    _this.loadDetailPage(id, false);
                }).always(function() {
                    workspace.window.closeMask();
                });
            });
        } else {
            workspace.window.showMask(true);
            Gikam.preInsert({
                modalTitle : 'CORE.MODULE.LES.LES_TEMPLATES.MODEL.NEWVERSION',
                url : Gikam.printf(coreLesTemplate.baseUrl + '/{id}/new-versions', {
                    id : selectedRows[0].id
                }),
            }).done(function(id) {
                _this.loadDetailPage(id, false);
            }).always(function() {
                workspace.window.closeMask();
            });
        }
    },

    expire : function() {
        var selectedRows = Gikam.getComp('core-les-template-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selectedRows)) {
            Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
            return;
        }
        for (var i = 0; i < selectedRows.length; i++) {
            if (selectedRows[i].templateStatus === 'expired') {
                Gikam.alert("PQ.MODULE.MDM.BIZMDM.METHOD.TIP.HAS_EXPIRED_DATA");
                return;
            }
        }

        Gikam.create('signature', {
            showReason : false,
            onConfirm : function(reason) {
                Gikam.getComp('core-les-template-edit-list-grid').processRows({
                    url : coreLesTemplate.baseUrl + "/action/expire",
                    confirm : {
                        msg : 'CORE.MODULE.LES.LES_TEMPLATES.MODEL.CONFIRM_EXPIRE',
                        title : 'CORE.MODULE.LES.LES_TEMPLATES.MODEL.EXPIRE',
                    },
                    formatter : function(row) {
                        return row.templateName;
                    },
                    method : 'put'
                }).done(function() {
                    Gikam.getComp('core-les-template-edit-list-grid').refresh();
                });
            }
        });
    },

    insertUsedOrgRow : function() {
        var templateRow = Gikam.getComp('core-les-template-edit-list-grid').getActivedRow();

        if (Gikam.isEmpty(templateRow)) {
            Gikam.alert('CORE.MODULE.LES.LES_TEMPLATES.TIP.TEMPLATE_ACTIVED_CANNOT_EMPTY');
            return;
        }

        var insertUsedOrgUrl = coreLesTemplate.baseUrl + '/template-orgs';

        Gikam.create('modal', {
            title : 'CORE.MODULE.LES.LES_TEMPLATES.MODAL.TITLE_USED_ORG_CHOOSE',
            url : IFM_CONTEXT + Gikam.choose.getConfig('org-simple').url + '?target_filter='
                    + encodeURI("{type:'unassigned',targetTable:'T_CORE_LES_TEMPLATE_ORG',targetMatchColumn:'USEDORGID',thisMatchColumn:'ID',filter:\"[{targetFilterColumn:'TEMPLATEID',targetFilterValue:'" + templateRow.id + "'}]\"}"),

            onAfterClose : function(rows) {
                if (rows) {
                    var chooseData = rows.map(function(item) {
                        return {
                            templateId : templateRow.id,
                            usedOrgId : item.id
                        }
                    });
                    Gikam.getComp('core-les-template-edit-list-used-dept-grid').insert(insertUsedOrgUrl, chooseData).done(function() {
                    });
                }
            }
        });
    },

    deleteUsedOrgRow : function() {
        var templateRow = Gikam.getComp('core-les-template-edit-list-grid').getActivedRow();

        if (Gikam.isEmpty(templateRow)) {
            Gikam.alert('CORE.MODULE.LES.LES_TEMPLATES.TIP.TEMPLATE_ACTIVED_CANNOT_EMPTY');
            return;
        }

        var deleteUsedOrgUrl = coreLesTemplate.baseUrl + '/template-orgs';

        Gikam.getComp('core-les-template-edit-list-used-dept-grid').deleteRows(deleteUsedOrgUrl).done(function() {
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            west : {
                width : '70%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getUsedDeptGridParam() ]
            }
        });
    },

    loadDetailPage : function(id, readonly) {
        workspace.window.load(coreLesTemplate.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            readonly : readonly
        }));
    },

    init : function() {
        this.param = {};
        this.create();
    }
};