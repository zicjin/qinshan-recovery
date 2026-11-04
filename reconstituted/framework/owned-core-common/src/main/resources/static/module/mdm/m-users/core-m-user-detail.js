modifiedUser.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_CORE_M_USER', _this.param.id);
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'modified-user-detail-base-info-tab',
            panels : [ {
                title : 'CORE.MODULE.MDM.MODIFICATION_USERS.TAB.BASE_INFO',
                id : 'modified-user-detail-base-info-panel',
                items : [ {
                    type : 'form',
                    id : 'modified-user-detail-base-info-form',
                    url : modifiedUser.baseUrl + '/' + _this.param.id,
                    fields : modifiedUser.getBaseInfoFormFields(),
                    columns: 3,
                    service : 'coreModifiedUserServiceImpl',
                    onLoadSuccess : function(data) {
                        if (_this.param.id) {
                            Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
                                f : {
                                    targetId : 'T_CORE_M_USER$' + _this.param.id,
                                    bizCategory_SEQ : 'SIGNATURE'
                                },
                                n : -1,
                                s : 50
                            })).done(function(fileList) {
                                if (!Gikam.isEmpty(fileList.rows)) {
                                    Gikam.getComp('modified-user-detail-base-info-form').setData({
                                        ext$ : {
                                            electronicsignature : fileList.rows[0].downloadUrl
                                        }
                                    }, false);

                                    _this.electronicSignatureFileId = fileList.rows[0].id;
                                }
                            });
                        }
                    }
                } ]
            } ]
        }
    },

    getPermissionGridParam : function() {
        var _this = this, panels = [];

        Gikam.each(this.param.typeArray, function(index, value) {
            var toolbar = [ {
                type : 'button',
                id : 'add-' + value.id + '-btn',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add-row',
                onClick : function() {
                    _this.insertPermissionRow(value);
                }
            }, {
                type : 'button',
                id : 'remove-' + value.id + '-btn',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    _this.deletePermissionRow(value);
                }
            } ];

            if (value.id === 'T_CORE_ORG' || value.id === 'T_CORE_ROLE') {
                toolbar.push({
                    type : 'button',
                    id : 'set-default-' + value.id + '-btn',
                    text : 'CORE.MODULE.MDM.USERS.BUTTON.DEFAULT',
                    icon : 'remove-row',
                    onClick : function() {
                        _this.setDefaultPermissionRow(value);
                    }
                });
            }
            
            panels.push({
                title : value.typeName,
                id : 'modified-user-detail-' + value.id + '-tab-panel',
                items : [ {
                    type : 'grid',
                    id : 'modified-user-detail-' + value.id + '-permission-list-grid',
                    url : IFM_CONTEXT + Gikam.printf(value.gridModifiedQueryUrl, {
                        id : _this.param.id
                    }),
                    genericQuery : false,
                    deleteFormatter : function(row) {
                        if (value.id === 'T_CORE_ORG') {
                            return row.ext$.orgname || row.targetId || row.id;
                        } else if (value.id === 'T_CORE_ROLE') {
                            return row.ext$.rolename || row.id;
                        } else {
                            return row.targetId || row.id;
                        }
                    },
                    toolbar : toolbar,
                    columns : [ {
                        checkbox : true
                    } ].concat(eval('[' + value.gridColumnSettings + ']'))
                } ]
            });
        });

        return {
            type : 'tab',
            id : 'modified-user-detail-permission-tab',
            width : 900,
            panels : panels
        }
    },

    insertPermissionRow : function(value) {
        var userData = Gikam.getComp('modified-user-detail-base-info-form').getData();
        var _this = modifiedUser.detailPage;
        
        if (!userData.id) {
            return;
        }
        if (!value.gridChooseUrl) {
            return;
        }
        var url = IFM_CONTEXT + value.gridChooseUrl;

        if (value.id === 'T_CORE_ROLE') {
            url += Gikam.param({
                target_filter : encodeURI(JSON.stringify({
                    type : 'unassigned',
                    targetTable : 'T_CORE_M_USER_ROLE',
                    targetMatchColumn : 'ROLEID',
                    thisMatchColumn : 'ID',
                    filter : [ {
                        targetFilterColumn : 'MODIFIEDUSERID',
                        targetFilterValue : userData.id,
                    } ]
                }))
            });
        } else {
            url += Gikam.param({
                target_filter : encodeURI(JSON.stringify({
                    type : 'unassigned',
                    targetTable : 'T_CORE_M_USER_PERMISSION',
                    targetMatchColumn : 'TARGETID',
                    thisMatchColumn : 'ID',
                    filter : [ {
                        targetFilterColumn : 'MODIFIEDUSERID',
                        targetFilterValue : userData.id,
                    }, {
                        targetFilterColumn : 'PERMISSIONTYPEID',
                        targetFilterValue : value.id,
                    } ]
                }))
            });
        }
        Gikam.create('modal', {
            title : Gikam.propI18N(value.typeName) + Gikam.propI18N('CORE.MODULE.MDM.USERS.MODAL.CHOOSE'),
            url : url,
            onAfterClose : function(rows) {
                if (rows) {
                    if (value.id === 'T_CORE_ROLE') {
                        var chooseData = rows.map(function(item) {
                            return {
                                roleId : item.id,
                                modifiedUserId : userData.id
                            }
                        });
                        
                        Gikam.getComp('modified-user-detail-' + value.id + '-permission-list-grid').insert(Gikam.printf(modifiedUser.baseUrl + '/{id}/user-roles', {
                            id : userData.id
                        }), chooseData);
                    } else {
                        var chooseData = rows.map(function(item) {
                            return {
                                targetId : item.id
                            }
                        });
                        
                        var userPermissionUrl = Gikam.printf(modifiedUser.baseUrl + '/{id}/user-permissions?permissionType={permissionType}', {
                            id : userData.id,
                            permissionType : value.id
                        });
                        
                        Gikam.getComp('modified-user-detail-' + value.id + '-permission-list-grid').insert(userPermissionUrl, chooseData);
                    }
                    
                    Gikam.getComp('modified-user-detail-' + value.id + '-permission-list-grid').refresh();
                }
            }
        });
    },

    deletePermissionRow : function(value) {
        var userData = Gikam.getComp('modified-user-detail-base-info-form').getData();

        if (!userData.id) {
            return;
        }

        var userPermissionUrl;

        if (value.id === 'T_CORE_ROLE') {
            userPermissionUrl = Gikam.printf(modifiedUser.baseUrl + '/{id}/user-roles', {
                id : userData.id
            });
        } else {
            userPermissionUrl = Gikam.printf(modifiedUser.baseUrl + '/{id}/user-permissions', {
                id : userData.id
            });
        }

        Gikam.getComp('modified-user-detail-' + value.id + '-permission-list-grid').deleteRows(userPermissionUrl).done(function() {
        });
    },

    setDefaultPermissionRow : function(value) {
        var selections = Gikam.getComp('modified-user-detail-' + value.id + '-permission-list-grid').getSelections();

        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var userData = Gikam.getComp('modified-user-detail-base-info-form').getData();

        if (!userData.id) {
            return;
        }

        var url = null;
        
        if (value.id === 'T_CORE_ROLE') {
            url = Gikam.printf( IFM_CONTEXT + '/secure/core/module/mdm/m-users/{id}/user-roles/{userRoleId}/action/set-default', {
                id : userData.id,
                userRoleId : selections[0].id
            });
        } else {
            url = Gikam.printf( IFM_CONTEXT + '/secure/core/module/mdm/m-users/{id}/user-permissions/{userPermissionId}/action/set-default', {
                id : userData.id,
                userPermissionId : selections[0].id
            });
        }

        Gikam.put(url).done(function() {
            Gikam.getComp('modified-user-detail-' + value.id + '-permission-list-grid').refresh();
        });
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() , this.getPermissionGridParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
            readonly : Gikam.isEmpty(this.param.edit)
        });
        return this;
    },

    uploadElectronicSignature : function() {
        var _this = this;
        var userForm = Gikam.getComp('modified-user-detail-base-info-form');
        var bizId = userForm.getData().id;
        Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
            f : {
                targetId : 'T_CORE_M_USER$' + bizId,
                bizCategory_SEQ : 'SIGNATURE'
            },
            n : -1,
            s : 50
        })).done(function(r) {
            if (r.total > 0) {
                Gikam.confirm('CORE.MODULE.MDM.USERS.TITLE.UPLOAD_ELECTRONICSIGNATURE', 'CORE.MODULE.MDM.USERS.TIP.OVERRIDE', function() {
                    Gikam.create('simpleUploader', {
                        id : 'modified-user-detail-uploader',
                        dbTable : 'T_CORE_M_USER',
                        bizId : bizId,
                        bizCategory : 'SIGNATURE',
                        accept : 'image',
                        multiple : false,
                        onAfterClose : function(fileList) {
                            if (fileList.length > 0) {
                                Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', r.rows ]));
                                Gikam.getJson(Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}', {
                                    id : fileList[0].id
                                })).done(function(r) {
                                    userForm.setData({
                                        ext$ : {
                                            electronicsignature : r.downloadUrl
                                        }
                                    }, false);

                                    _this.electronicSignatureFileId = r.id;
                                });
                            }
                        }
                    });
                })
            } else {
                Gikam.create('simpleUploader', {
                    id : 'modified-user-detail-uploader',
                    dbTable : 'T_CORE_M_USER',
                    bizId : bizId,
                    bizCategory : 'SIGNATURE',
                    accept : 'image',
                    multiple : false,
                    onAfterClose : function(fileList) {
                        if (fileList.length === 0) {
                            return;
                        }
                        Gikam.getJson(Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}', {
                            id : fileList[0].id
                        })).done(function(r) {
                            userForm.setData({
                                ext$ : {
                                    electronicsignature : r.downloadUrl
                                }
                            }, false);

                            _this.electronicSignatureFileId = r.id;
                        });
                    }
                });
            }
        });
    },

    init : function(param) {
        this.param = param;
        var _this = this;
        Gikam.getJson(IFM_CONTEXT + '/secure/core/module/mdm/user-permission-types/queries/selectable').done(function(data) {
            _this.param.typeArray = data;
            _this.createPage();
        });
    }
}