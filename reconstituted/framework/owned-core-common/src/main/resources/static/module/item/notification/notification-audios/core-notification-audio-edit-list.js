coreNotificationAudio.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-notification-audio-edit-list-grid',
            url : coreNotificationAudio.baseUrl + '/queries/raw',
            service : 'coreNotificationAudioServiceImpl',
            dbTable : 'T_CORE_NOTIFICATION_AUDIO',
            columns : coreNotificationAudio.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.audioName || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.ITEM.NOTIFICATION.AUDIOS.MODAL.ADD',
                        fields : coreNotificationAudio.getBaseInfoFormFields(),
                        displayReadonly : false,
                        url : coreNotificationAudio.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('core-notification-audio-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    var selectRows = Gikam.getComp('core-notification-audio-edit-list-grid').getSelections();
                    var deleteRows =  selectRows.filter(function (item) {
                        return 'system' === item.audioType;
                    })
                    if (Gikam.isNotEmpty(deleteRows)) {
                        Gikam.alert('CORE.MODULE.ITEM.NOTIFICATION.AUDIOS.TIP.UNABLE_DELETE_SYSTEM_AUDIO');
                        return;
                    }
                    Gikam.getComp('core-notification-audio-edit-list-grid').deleteRows(coreNotificationAudio.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.FILE.BUTTON.UPLOAD_FILE',
                icon : 'upload',
                onClick : function() {
                    _this.uploaderOnly();
                }
            }, {
                type : 'button',
                text : 'GIKAM.FILE.BUTTON.DELETE_FILE',
                icon : 'remove-row',
                onClick : function() {
                    _this.deleteAudio();
                }
            } ]
        }
    },

    // 上传附件
    uploaderOnly : function() {
        var _this = this;
        var selectRows = Gikam.getComp('core-notification-audio-edit-list-grid').getSelections();
        if (selectRows.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return
        }
        if (selectRows.length === 1 && selectRows[0].audioType === 'system') {
            Gikam.alert('CORE.MODULE.ITEM.NOTIFICATION.AUDIOS.TIP.UNABLE_COVER_SYSTEM_AUDIO')
            return;
        }
        // 数据未上传过附件
        if (Gikam.isEmpty(selectRows[0].ext$.filename)) {
            _this.uploaderAudio(selectRows)
        } else {
            Gikam.confirm('GIKAM.FILE.MODAL.UPLOAD_FILE', 'CORE.MODULE.ITEM.NOTIFICATION.AUDIOS.TIP.ATTACHMENT_EXISTS', function() {
                Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', [ {
                    'id' : selectRows[0].ext$.fileid
                } ] ])).done(function(r) {
                    _this.uploaderAudio(selectRows)
                });
            })
        }
    },

    uploaderAudio : function(selectRows) {
        var uploader = Gikam.create('simpleUploader', {
            id : 'core-notification-audio-edit-list-uploader',
            dbTable : 'T_CORE_NOTIFICATION_AUDIO',
            bizId : selectRows[0].id,
            multiple : false,
            onUploadSuccess : function(fileList) {
                Gikam.getJson(Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}', {
                    id : fileList[0].id
                })).done(function(r) {
                    // 同步附件的路径至表中
                    selectRows[0].url = r.downloadUrl;
                    Gikam.put(Gikam.printf(coreNotificationAudio.baseUrl + '/{id}', {
                        id : selectRows[0].id
                    }), Gikam.getJsonWrapper(null, [ '', selectRows]))
                })
            },
            onAfterClose : function() {
                Gikam.getComp('core-notification-audio-edit-list-grid').refresh();
            }
        });
    },

    deleteAudio : function() {
        var _this = this;
        var selectRows = Gikam.getComp('core-notification-audio-edit-list-grid').getSelections();
        if (selectRows.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
        if (selectRows.length === 1 && selectRows[0].audioType === 'system') {
            Gikam.alert('CORE.MODULE.ITEM.NOTIFICATION.AUDIOS.TIP.UNABLE_DELETE_SYSTEM_AUDIO');
            return;
        }

        if (Gikam.isEmpty(selectRows[0].ext$.filename)) {
            Gikam.alert('QMS.MODULE.FILEMANAGE.TIP.NO_FILE')
        } else {
            Gikam.confirm('GIKAM.FILE.MODAL.DELETE_FILE', selectRows[0].ext$.filename, function() {
                Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', [ {
                    'id' : selectRows[0].ext$.fileid
                } ] ])).done(function(r) {
                    selectRows[0].url = '';
                    // 修改该条单据的url为空
                    Gikam.put(Gikam.printf(coreNotificationAudio.baseUrl + '/{id}', {
                        id : selectRows[0].id
                    }), Gikam.getJsonWrapper(null, [ '', selectRows])).done(function (data) {
                        Gikam.getComp('core-notification-audio-edit-list-grid').refresh();
                    })
                });
            })
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-notification-audio-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
