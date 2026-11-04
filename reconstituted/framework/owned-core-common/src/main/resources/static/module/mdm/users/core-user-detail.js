coreUser.detailPage = {

    getBtnToolbar : function () {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function () {
                    Gikam.viewAudit('T_CORE_USER', _this.param.id);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function () {
                    workspace.window.goBack().done(function () {
                        if (_this.param.id) {
                            Gikam.getComp('core-user-edit-list-grid').selectRowById(_this.param.id);
                        }
                    })
                }
            } ]
        }
    },

    getBaseInfoTab : function () {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-user-detail-base-info-tab',
            panels : [ {
                title : 'CORE.MODULE.MDM.USERS.TAB.BASE_INFO',
                id : 'core-user-detail-base-info-panel',
                items : [ {
                    type : 'form',
                    id : 'core-user-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    fields : coreUser.getBaseInfoFormFields(),
                    columns : 3,
                    service : 'coreUserServiceImpl',
                    dbTable : 'T_CORE_USER',
                    onLoadSuccess : function (data) {
                        if (_this.param.id) {
                            this.toFieldsReadonly([ 'id' ]);
                            this.removeFieldValidator('id', [ 'remote' ]);
                            Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
                                f : {
                                    targetId : 'T_CORE_USER$' + _this.param.id,
                                    bizCategory_SEQ : 'SIGNATURE'
                                },
                                n : -1,
                                s : 50
                            })).done(function (fileList) {
                                if (!Gikam.isEmpty(fileList.rows)) {
                                    Gikam.getComp('core-user-detail-base-info-form').setData({
                                        ext$ : {
                                            electronicsignature : fileList.rows[ 0 ].downloadUrl
                                        }
                                    }, false);

                                    _this.electronicSignatureFileId = fileList.rows[ 0 ].id;
                                }
                            });
                        }
                    },onBeforeUpdate : function(data,keys) {
                    	if( data.readonly ===undefined && Gikam.getComp('core-user-detail-base-info-form').getData().readonly===0){
                    		Gikam.alert('该用户是只读账号，没有更改权限。');
                            return false ;
                    	}
                        
                    }
                } ]
            } ]
        }
    },

    getPermissionGridParam : function () {
        var _this = this, panels = [];

        Gikam.each(this.param.typeArray, function (index, value) {

            var toolbar = [ {
                type : 'button',
                id : 'add-' + value.id + '-btn',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add-row',
                onClick : function () {
                    _this.insertPermissionRow(value);
                }
            }, {
                type : 'button',
                id : 'remove-' + value.id + '-btn',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function () {
                    _this.deletePermissionRow(value);
                }
            } ];

            if (value.id === 'T_CORE_ORG' || value.id === 'T_CORE_ROLE') {
                toolbar.push({
                    type : 'button',
                    id : 'set-default-' + value.id + '-btn',
                    text : 'CORE.MODULE.MDM.USERS.BUTTON.DEFAULT',
                    icon : 'remove-row',
                    onClick : function () {
                        _this.setDefaultPermissionRow(value);
                    }
                });
            }
            panels.push({
                title : value.typeName,
                id : 'core-user-detail-' + value.id + '-tab-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-user-detail-' + value.id + '-permission-list-grid',
                    url : Gikam.isEmpty(_this.param.id) ? '' : IFM_CONTEXT + value.gridQueryUrl,
                    genericQuery : false,
                    requestData : {
                        'userId_SEQ' : (value.id === 'T_CORE_ROLE') ? _this.param.id : '',
                        'user_assigned' : _this.param.id,
                        'permissionTypeId' : (value.id === 'T_CORE_ROLE' || value.id === 'T_CORE_ORG') ? '' : value.id,
                    },
                    deleteFormatter : function (row) {

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
            id : 'core-user-detail-permission-tab',
            width : 900,
            panels : panels
        }
    },

    insertPermissionRow : function (value) {
        var userData = Gikam.getComp('core-user-detail-base-info-form').getData();
        var _this = coreUser.detailPage;

        if (!userData.id) {
            return;
        }
        if (!value.gridChooseUrl) {
            return;
        }
        var url = IFM_CONTEXT + value.gridChooseUrl;

        if (value.id === 'T_CORE_ROLE') {
            url += Gikam.param({
                target_unassigned : 1,
                targetTable : 'T_CORE_ROLE_USER',
                targetRoleIdColumn : 'ROLEID',
                targetFilterColumn : 'USERID',
                targetFilterValue : userData.id
            });
        } else {
            url += Gikam.param({
                user_unassigned : userData.id,
                permissionTypeId : value.id
            });

        }
        Gikam.create('modal', {
            title : Gikam.propI18N(value.typeName),
            url : url,
            onAfterClose : function (rows) {
                if (rows) {
                    if (value.id === 'T_CORE_ROLE') {

                        var chooseData = rows.map(function (item) {
                            return {
                                roleId : item.id,
                                userId : userData.id
                            }
                        });

                        Gikam.getComp('core-user-detail-' + value.id + '-permission-list-grid').insert(IFM_CONTEXT + '/core/module/sys/role-users', chooseData);

                        Gikam.getComp('core-user-detail-' + value.id + '-permission-list-grid').refresh({
                            url : IFM_CONTEXT + value.gridQueryUrl,
                            requestData : {
                                'userId_SEQ' : userData.id
                            }
                        });

                        return;
                    }

                    var chooseData = rows.map(function (item) {
                        return {
                            targetId : item.id
                        }
                    });

                    var userPermissionUrl = Gikam.printf(coreUser.baseUrl + '/{id}/user-permission-types/{permissionTypeId}/user-permissions', {
                        id : userData.id,
                        permissionTypeId : value.id
                    });

                    Gikam.getComp('core-user-detail-' + value.id + '-permission-list-grid').insert(userPermissionUrl, chooseData);

                    Gikam.getComp('core-user-detail-' + value.id + '-permission-list-grid').refresh({
                        url : IFM_CONTEXT + value.gridQueryUrl,
                        requestData : {
                            'user_assigned' : _this.param.id,
                            'permissionTypeId' : (value.id === 'T_CORE_ORG') ? '' : value.id,
                        }
                    });
                }
            }
        });
    },

    deletePermissionRow : function (value) {
        var userData = Gikam.getComp('core-user-detail-base-info-form').getData();

        if (!userData.id) {
            return;
        }

        var userPermissionUrl;

        if (value.id === 'T_CORE_ORG') {
            userPermissionUrl = IFM_CONTEXT + '/secure/core/module/mdm/user-permissions';
        } else if (value.id === 'T_CORE_ROLE') {
            userPermissionUrl = Gikam.printf(IFM_CONTEXT + '/secure/core/module/sys/roles/role-users');
        } else {
            userPermissionUrl = Gikam.printf(coreUser.baseUrl + '/{id}/user-permission-types/{permissionTypeId}/user-permissions', {
                id : userData.id,
                permissionTypeId : value.id
            });
        }

        Gikam.getComp('core-user-detail-' + value.id + '-permission-list-grid').deleteRows(userPermissionUrl).done(function () {
        });
    },

    setDefaultPermissionRow : function (value) {
        var selections = Gikam.getComp('core-user-detail-' + value.id + '-permission-list-grid').getSelections();

        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var userData = Gikam.getComp('core-user-detail-base-info-form').getData();

        if (!userData.id) {
            return;
        }

        var url = Gikam.printf(coreUser.baseUrl + '/{id}/user-permission-types/{permissionTypeId}/user-permissions/action/set-default', {
            id : userData.id,
            permissionTypeId : value.id
        });

        if (value.id === 'T_CORE_ROLE') {
            url = Gikam.printf(IFM_CONTEXT + '/core/module/sys/role-users/users/{userId}/action/set-default', {
                userId : userData.id
            });
        }

        Gikam.put(url, Gikam.getJsonWrapper({}, [ '', selections ])).done(function () {
            Gikam.getComp('core-user-detail-' + value.id + '-permission-list-grid').refresh();
        });

    },

    createPage : function () {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab(), this.getPermissionGridParam() ]
            },
            renderTo : workspace.window.$dom
        });
        return this;
    },

    urlManager : {
        baseInfoForm : coreUser.baseUrl + '/{id}/info'
    },

    setCompUrl : function () {
        Gikam.setCompUrl({
            'core-user-detail-base-info-form' : this.getCompUrl('baseInfoForm')
        });
        var _this = this;
        Gikam.each(this.param.typeArray, function (index, value) {
            var comp = Gikam.getComp('core-user-detail-' + value.id + '-permission-list-grid');
            comp && comp.setOptions({
                requestData : {
                    'user_assigned' : _this.param.id,
                    'permissionTypeId' : value.id,
                },
            });
        });
    },

    getCompUrl : function (name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[ name ], {
            id : encodeURIComponent(this.param.id)
        });
    },

    uploadElectronicSignature : function () {
        var _this = this;
        var userForm = Gikam.getComp('core-user-detail-base-info-form');
        var bizId = userForm.getData().id;
        Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
            f : {
                targetId : 'T_CORE_USER$' + bizId,
                bizCategory_SEQ : 'SIGNATURE'
            },
            n : -1,
            s : 50
        })).done(function (r) {
            if (r.total > 0) {
                Gikam.confirm('CORE.MODULE.MDM.USERS.TITLE.UPLOAD_ELECTRONICSIGNATURE', 'CORE.MODULE.MDM.USERS.TIP.OVERRIDE', function () {
                    Gikam.create('simpleUploader', {
                        id : 'core-user-detail-uploader',
                        dbTable : 'T_CORE_USER',
                        bizId : bizId,
                        bizCategory : 'SIGNATURE',
                        accept : 'image',
                        multiple : false,
                        onAfterClose : function (fileList) {
                            if (fileList.length > 0) {
                                Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', r.rows ]));
                                Gikam.getJson(Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}', {
                                    id : fileList[ 0 ].id
                                })).done(function (r) {
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
                    id : 'core-user-detail-uploader',
                    dbTable : 'T_CORE_USER',
                    bizId : bizId,
                    bizCategory : 'SIGNATURE',
                    accept : 'image',
                    multiple : false,
                    onAfterClose : function (fileList) {
                        if (fileList.length === 0) {
                            return;
                        }
                        Gikam.getJson(Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}', {
                            id : fileList[ 0 ].id
                        })).done(function (r) {
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

    init : function (param) {
        this.param = param;
        var _this = this;
        Gikam.getJson(IFM_CONTEXT + '/secure/core/module/mdm/user-permission-types/queries/selectable').done(function (data) {
            _this.param.typeArray = data;
            _this.createPage();
        });
    }
}