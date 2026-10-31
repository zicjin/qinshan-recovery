emcHomeAttachment.editPage = {

    getAttachmentGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-home-attachment-edit-list-grid',
            url : emcHomeAttachment.baseUrl + '/queries/raw',
            columns : emcHomeAttachment.getEditListColumns(),
            autoSave : true,
            service : 'emcHomeAttachmentServiceImpl',
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.ITEM.HOMEATTACHMENTS.BUTTON.UPLOAD',
                icon : 'upload',
                onClick : function() {
                    _this.uploadAttachment();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-home-attachment-edit-list-grid').deleteRows(emcHomeAttachment.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    _this.activateRow();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DEACTIVE',
                icon : 'disable',
                onClick : function() {
                    _this.deactivateRow();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.ITEM.HOMEATTACHMENTS.BUTTON.BATCH_CHANGE_END_DATE',
                icon : 'modify',
                onClick : function() {
                    _this.batchChangeDate();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.ITEM.HOMEATTACHMENTS.BUTTON.BATCH_EMPOWER',
                icon : 'link',
                onClick : function() {
                    _this.batchEmPower();
                }
            } ],
            checkOnActive : true,
            onLoadSuccess : function(data) {
                if (data.length > 0) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-home-attachment-edit-list-permission-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                Gikam.refreshGrids({
                    'emc-home-attachment-edit-list-permission-grid' : emcHomeAttachment.baseUrl + '/{id}/permissions/queries',
                }, {
                    id : row.id
                });
            },
            deleteFormatter : function(row) {
                return row.ext$.filename || row.id;
            },
            onCellClick : function(field, row) {
                field === 'ext$.filename' && _this.downloadFile(row.id);
            },
        }
    },

    getAttachmentPermissionTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.ITEM.HOMEATTACHMENTS.TITLE.ATTACHMENT_PERMISSION',
                items : [ {
                    type : 'grid',
                    id : 'emc-home-attachment-edit-list-permission-grid',
                    filterOpen : false,
                    page : false,
                    service : 'emcHomeAttachmentPermissionServiceImpl',
                    onRendered : function() {

                    },
                    deleteFormatter : function(row) {
                        return row.ext$.username || row.userId || row.id;
                    },
                    columns : emcHomeAttachment.getPermissionEditListColumns(),
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            _this.addPermission();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.removePermission();
                        }
                    } ]
                } ]
            } ],
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-home-attachment-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ _this.getAttachmentGridParam() ]
                    },
                    center : {
                        items : [ _this.getAttachmentPermissionTab() ]
                    }
                } ]
            }
        });
    },

    init : function() {
        this.create();
    },

    uploadAttachment : function() {
        Gikam.getComp('emc-home-attachment-edit-list-grid').insert(emcHomeAttachment.baseUrl).done(function(id) {
            Gikam.create('simpleUploader', {
                id : 'emc-home-attachment-edit-list-grid-uploader',
                dbTable : 'T_EMC_HOME_ATTACHMENT',
                bizId : id,
                onAfterClose : function() {
                    Gikam.getComp('emc-home-attachment-edit-list-grid').refresh();
                }
            });

        });
    },

    addPermission : function() {
        var activeRow = Gikam.getComp('emc-home-attachment-edit-list-grid').getActivedRow();
        var url = emcHomeAttachment.baseUrl + '/permissions';
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('user').title,
            url : Gikam.choose.getConfig('user').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_HOME_ATTA_PERMISSION',
                targetUserIdColumn : 'USERID',
                targetFilterColumn : 'ATTACHMENTID',
                targetFilterValue : activeRow.id
            }),
            width : 600,
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        userId : item.id,
                        attachmentId : activeRow.id
                    }
                });
                Gikam.getComp('emc-home-attachment-edit-list-permission-grid').insert(url, chooseData);
            }
        });
    },

    removePermission : function() {
        var rows = Gikam.getComp('emc-home-attachment-edit-list-permission-grid').getSelections();

        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var url = emcHomeAttachment.baseUrl + '/permissions';
        Gikam.getComp('emc-home-attachment-edit-list-permission-grid').deleteRows(url).done(function() {
            Gikam.getComp('emc-home-attachment-edit-list-permission-grid').refresh();
        });
    },

    activateRow : function() {
        var selections = Gikam.getComp('emc-home-attachment-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections) || selections.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM')
            return;
        }
        var applyRows = selections.filter(function(item) {
            return item.activatedFlag != 0;
        });
        if (applyRows.length) {
            Gikam.alert('EMC.MODULE.ITEM.HOMEATTACHMENTS.TIP.CHOOSE_DEACTIVATE_ROW')
            return;
        }
        Gikam.confirm('EMC.MODULE.ITEM.HOMEATTACHMENTS.TITLE.ACTIVATE', 'GIKAM.TIP.CONFIRM_TO_ACTIVATE', function() {
            Gikam.put(emcHomeAttachment.baseUrl + '/action/activate', Gikam.getJsonWrapper.apply(null, [ null, [ null, selections ] ])).done(function() {
                Gikam.getComp('emc-home-attachment-edit-list-grid').refresh();
            });
        })
    },

    deactivateRow : function() {
        var selections = Gikam.getComp('emc-home-attachment-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections) || selections.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var applyRows = selections.filter(function(item) {
            return item.activatedFlag == 0;
        });
        if (applyRows.length) {
            Gikam.alert('EMC.MODULE.ITEM.HOMEATTACHMENTS.TIP.CHOOSE_ACTIVATE_ROW')
            return;
        }
        Gikam.confirm('EMC.MODULE.ITEM.HOMEATTACHMENTS.TITLE.DEACTIVATE', 'GIKAM.TIP.CONFIRM_TO_DEACTIVATE', function() {
            Gikam.put(emcHomeAttachment.baseUrl + '/action/deactivate', Gikam.getJsonWrapper.apply(null, [ null, [ null, selections ] ])).done(function() {
                Gikam.getComp('emc-home-attachment-edit-list-grid').refresh();
            })
        })
    },

    downloadFile : function(id) {
        var url = Gikam.printf(emcHomeAttachment.baseUrl + '/{id}/action/get-url', {
            id : id
        });
        Gikam.postText(url).done(function(data) {
            Gikam.emcDownloadFile(data);
        });
    },

    batchChangeDate : function() {
        var selections = Gikam.getComp('emc-home-attachment-edit-list-grid').getSelections();
        if (selections.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            height : 300,
            width : 400,
            title : 'EMC.MODULE.ITEM.HOMEATTACHMENTS.TITLE.END_DATE',
            url : emcHomeAttachment.baseUrl + '/page/choose-date-detail',
            onAfterClose : function(date) {
                if (Gikam.isEmpty(date)) {
                    return;
                }
                selections.map(function(item) {
                    Gikam.getComp('emc-home-attachment-edit-list-grid').setData({
                        index : Gikam.getComp('emc-home-attachment-edit-list-grid').getRowIndexById(item.id),
                        endDate : date
                    });
                });
            }
        });
    },

    batchEmPower : function() {
        var selections = Gikam.getComp('emc-home-attachment-edit-list-grid').getSelections();
        if (selections.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'CORE.MODULE.MDM.USERS.MODAL.CHOOSE',
            url : Gikam.choose.getConfig("user").url,
            width : 600,
            onAfterClose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var data = rows.map(function(item) {
                        return item.id;
                    });
                    workspace.window.showMask(true);
                    var url = Gikam.printf(emcHomeAttachment.baseUrl + '/action/empower');
                    var params = {
                        userId : data.join(',')
                    };
                    Gikam.post(url, Gikam.getJsonWrapper(params, [ '', selections ])).always(function() {
                        workspace.window.closeMask();
                    });
                    Gikam.getComp('emc-home-attachment-edit-list-grid').refresh();
                }
            }
        });
    }
};
