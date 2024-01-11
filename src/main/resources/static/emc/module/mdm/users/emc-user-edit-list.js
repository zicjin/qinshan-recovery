Gikam.extend(coreUser.editPage, {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-user-edit-list-grid',
            url : coreUser.baseUrl + '/queries/raw',
            requestData : {
                orgId_SB : workspace.user.ext$.parentorgid
            },
//            onRendered : function() {
//                Gikam.getComp('user-insert-button').hide();
//                Gikam.getComp('user-deprecate-button').hide();
//            },
            columns : coreUser.getEditListColumns(),
            filterOpen : true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                id : 'user-insert-button',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.MDM.USERS.TAB.ADD',
                        fields : coreUser.getBaseInfoFormFields(),
                        url : coreUser.baseUrl,
//                        onFormRendered : function(form) {
//                            var url = Gikam.printf(coreUser.baseUrl + '/action/select-maxid');
//                            var that = this;
//                            Gikam.postText(url, Gikam.getJsonWrapper({})).done(function(data) {
//                                var resultId = data;
//                                setTimeout(() => {
//                                     that.setData({id : resultId},false);
//                                }, 10)
//                            }).always(function() {});
//                   	    }
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.MDM.USERS.BUTTON.CHANGE_PASSWORD',
                icon : 'edit',
                onClick : function() {
                    _this.changePassWord();
                }
            }, {
                type : 'button',
                id : 'user-deprecate-button',
                text : 'CORE.MODULE.MDM.USERS.BUTTON.DEPRECATE',
                icon : 'remove-row',
                onClick : function() {
                    _this.deprecateRows();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.MDM.USERS.BUTTON.RESET_PASSWORD',
                icon : 'default',
                onClick : function() {
                    _this.resetPassword();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.EXCEL_EXPORT',
                icon : 'edit',
                onClick : function() {
                    Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}', {
                        templateCode : 'pfUserExportTemplate',
                        service : 'pfUserExportTemplate'
                    })).done(function(fileUri) {
                        var fileUrl = IFM_CONTEXT + fileUri;
                        Gikam.download(fileUrl);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.EXCEL_IMPORT',
                icon : 'edit',
                onClick : function() {
                    Gikam.importExcel({
                        url : IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'pfUserImportFileFunction',
                        dbTable : 'T_CORE_USER',
                        onImportSuccess : function(files) {
                            Gikam.getComp('core-user-edit-list-grid').refresh();
                        }
                    });
                }
            } ],
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            }
        }
    },

});
