Gikam.extend(coreReportConfig.detailPage, {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_CORE_REPORT_CONFIG', _this.param.id);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                    Gikam.getComp('core-report-config-edit-list-grid').refresh({
                        url : coreReportConfig.baseUrl + '/queries/raw',
                        order : {
                            orderNo : 'asc',
                        },
                    });
                }
            } ]
        }
    },

    getCoreReportConfigLineTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            panels : [ {
                title : 'CORE.MODULE.SYS.REPORTS.TAB.LINE',
                items : [ {
                    type : 'grid',
                    id : 'core-report-config-detail-line-grid',
                    url : _this.getCompUrl("lineListGrid"),
                    service : 'coreReportConfigLineServiceImpl',
                    deleteFormatter : function(row) {
                        return row.reportName || row.id;
                    },
                    activateFormatter : function(row) {
                        return row.reportName || row.id;
                    },
                    onLoadSuccess : function(data) {
                        if (data.length > 0) {
                            this.activeRowByIndex(0);
                        }
                    },
                    checkOnActive : true,
                    onRowActive : function(index, row) {
                        _this.userPermissionGrid.refresh({
                            url : _this.getCompUrl("userPermissionGrid"),
                            requestData : {
                                'targetId_SEQ' : row.id
                            }
                        });
                        Gikam.getComp('core-reportline-edit-list-role-grid').refresh({
                           url : IFM_CONTEXT + '/core/module/sys/role-permissions/queries',
                            requestData : {
                                'targetId_SEQ' : row.id
                            }
                        });
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertCoreReportConfigLine();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            if (_this.param.id) {
                                var selectrows = Gikam.getComp('core-report-config-detail-line-grid').getSelections();
                                if (Gikam.isEmpty(selectrows)) {
                                    Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.SELECT_BEFORE_ACTION');
                                    return;
                                }
                                _this.deleteCoreReportConfigLineRow();
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.ACTIVE',
                        icon : 'enable',
                        onClick : function() {
                            var selectrows = Gikam.getComp('core-report-config-detail-line-grid').getSelections();
                            if (Gikam.isEmpty(selectrows)) {
                                Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.SELECT_BEFORE_ACTION');
                                return;
                            }
                            for (var i = 0; i < selectrows.length; i++) {
                                if (selectrows[i].activatedFlag === '1') {
                                    Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.LINE_ACTIVATE_FAILURE');
                                    return;
                                }
                                if (selectrows[i].reportCategory === '' || selectrows[i].reportCategory === null || selectrows[i].reportCategory === undefined) {
                                    Gikam.alert('EMC.MODULE.SYS.REPORTS.TIP.SET_REPORT_CATEGORY');
                                    return;
                                }

                                if (selectrows[i].reportCategory === 'fanruan') {
                                    if (selectrows[i].ext$.filename === '' || selectrows[i].ext$.filename === null || selectrows[i].ext$.filename === undefined) {
                                        Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.LINE_VALID_ATTACHMENT_FAILURE');
                                        return;
                                    }
                                } else if (selectrows[i].reportCategory === 'reportingService') {
                                    if (selectrows[i].ext$.reportpath === '' || selectrows[i].ext$.reportpath === null || selectrows[i].ext$.reportpath === undefined) {
                                        Gikam.alert('EMC.MODULE.SYS.REPORTS.TIP.LINE_VALID_REPORT_PATH_FAILURE');
                                        return;
                                    }
                                }
                            }
                            _this.activateCoreReportConfigLineRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DEACTIVE',
                        icon : 'disable',
                        onClick : function() {
                            var selectrows = Gikam.getComp('core-report-config-detail-line-grid').getSelections();
                            if (Gikam.isEmpty(selectrows)) {
                                Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.SELECT_BEFORE_ACTION');
                                return;
                            }
                            for (var i = 0; i < selectrows.length; i++) {
                                if (selectrows[i].activatedFlag === '0') {
                                    Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.LINE_DEACTIVATE_FAILURE');
                                    return;
                                }
                            }
                            _this.deactivateCoreReportConfigLineRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.FILE.BUTTON.UPLOAD_FILE',
                        icon : 'upload',
                        onClick : function() {
                            var selectrows = Gikam.getComp('core-report-config-detail-line-grid').getSelections();
                            if (Gikam.isEmpty(selectrows)) {
                                Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.SELECT_BEFORE_ACTION');
                                return;
                            }
                            if (selectrows.lenth > 1) {
                                Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.LINE_MULTI_CHOOSE_FAILURE');
                                return;
                            }
                            _this.uploaderOnly();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.VIEW',
                        icon : 'browse',
                        onClick : function() {
                            var selectrows = Gikam.getComp('core-report-config-detail-line-grid').getSelections();
                            if (Gikam.isEmpty(selectrows)) {
                                Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.SELECT_BEFORE_ACTION');
                                return;
                            }
                            if (selectrows.lenth > 1) {
                                Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.LINE_MULTI_CHOOSE_FAILURE');
                                return;
                            }
                            _this.view();
                        }
                    }, {
                        type : 'button',
                        text : '下载',
                        icon : 'browse',
                        onClick : function() {
                            var selectrows = Gikam.getComp('core-report-config-detail-line-grid').getSelections();
                            if (selectrows.length != 1) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                                return;
                            }
                            var selectedRow = Gikam.getComp('core-report-config-detail-line-grid').getSelections()[0];
                            var bizId = selectedRow.id;

                            Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
                                f : {
                                    targetId : 'T_CORE_REPORT_CONFIG_LINE$' + bizId
                                },
                                n : -1,
                                s : 50
                            })).done(function(r) {
                                if (r.rows.length > 0) {
                                    Gikam.emcDownloadFileById(r.rows[0].id);
                                } else {
                                    Gikam.alert('未维护附件');
                                }
                            });

                        }
                    } ],
                    columns : coreReportConfig.getLineListColumns(),
                } ]
            } ]
        }
    },

    getPermissionTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            panels : [ {
                title : 'CORE.MODULE.SYS.REPORTS.TAB.USER_PERMISSION',
                items : [ _this.getUserPermissionGridParam() ]
            }, {
                   title : 'CORE.MODULE.MDM.USERS.TAB.ROLE', // 访问角色
                  items : [ {
                      type : 'grid',
                      id : 'core-reportline-edit-list-role-grid',
                      columns : coreReportConfig.getMenuRoleColumns(),
                      requestData : {
                          permissionTypeId_SEQ : 'T_CORE_REPORT_CONFIG_LINE',
                      },
                      page : true,
                      deleteFormatter : function(row) {
                          return row.ext$.rolename
                      },
                      toolbar : [ {
                          type : 'button',
                          text : 'GIKAM.BUTTON.INSERT',
                          icon : 'add-row',
                          onClick : function() {
                              Gikam.create('modal', {
                                  title : 'CORE.MODULE.SYS.ROLES.MODAL.CHOOSE', // 选择角色
                                  url : IFM_CONTEXT + '/secure/core/module/sys/roles/page/choose-list' + Gikam.param({
                                      target_filter : encodeURI(JSON.stringify({
                                          type : 'unassigned',
                                          targetTable : 'T_CORE_ROLE_PERMISSION',
                                          targetMatchColumn : 'ROLEID',
                                          thisMatchColumn : 'ID',
                                          filter : [ {
                                              targetFilterColumn : 'TARGETID',
                                              targetFilterValue : Gikam.getComp('core-report-config-detail-line-grid').getActivedRow().id
                                          }, {
                                              targetFilterColumn : 'PERMISSIONTYPEID',
                                              targetFilterValue : 'T_CORE_MENU'
                                          } ]
                                      }))
                                  }),
                                  onAfterClose : function(callbackObj) {
                                      if (Gikam.isEmpty(callbackObj)) {
                                          return;
                                      }
                                      var menuId = Gikam.getComp('core-report-config-detail-line-grid').getActivedRow().id;
                                      var rolePermissions = callbackObj.map(function(item) {
                                          return {
                                              roleId : item.id,
                                              targetId : menuId,
                                              permissionTypeId : 'T_CORE_REPORT_CONFIG_LINE'
                                          }
                                      })

                                      Gikam.getComp('core-reportline-edit-list-role-grid').insert(IFM_CONTEXT + '/core/module/sys/role-permissions', rolePermissions);
                                  }
                              })
                          }
                      }, {
                          type : 'button',
                          text : 'GIKAM.BUTTON.DELETE',
                          icon : 'remove-row',
                          onClick : function() {
                              Gikam.getComp('core-reportline-edit-list-role-grid').deleteRows(IFM_CONTEXT + '/core/module/sys/role-permissions');
                          }
                      } ]
                  } ]
              }]
        }
    },
    
    userPermissionGrid : null,
    getUserPermissionGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-report-config-detail-user-permission-grid',
            requestData : {
                'permissionTypeId_SEQ' : 'T_CORE_REPORT_CONFIG_LINE'
            },
            deleteFormatter : function(row) {
                return row.userId || row.id;
            },
            onRendered : function() {
                _this.userPermissionGrid = this;
            },
            genericQuery : false,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add-row',
                onClick : function() {
                    _this.insertUserPermission();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    _this.delteUserPermission();
                }
            },{
                type : 'button',
                text : '按科室新增用户权限',
                icon : 'add-row',
                onClick : function() {
                    _this.insertPermissionByDept();
                }
            }, {
                type : 'button',
                text : '按科室删除用户权限',
                icon : 'remove-row',
                onClick : function() {
                    _this.deletePermissionByDept();
                }
            } ],
            columns : coreReportConfig.getUserPermissionListColumns()
        }
    },


    insertUserPermission : function() {
        var _this = coreReportConfig.detailPage;

        var activedRow = Gikam.getComp('core-report-config-detail-line-grid').getActivedRow();
        if (!activedRow) {
            Gikam.alert("CORE.MODULE.SYS.REPORTS.TIP.CHOOSE_LINE");
            return;
        }

        var reportLineId = activedRow.id;
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('mult-user').title,
            url : IFM_CONTEXT + Gikam.choose.getConfig('mult-user').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_CORE_USER_PERMISSION',
                targetUserIdColumn : 'USERID',
                targetFilterColumn : 'TARGETID',
                targetFilterValue : reportLineId,
                targetFilterColumn1 : 'PERMISSIONTYPEID',
                targetFilterValue1 : 'T_CORE_REPORT_CONFIG_LINE',
                org_filter : '1',
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }

                var chooseData = rows.map(function(item) {
                    return {
                        userId : item.id,
                        permissionTypeId : 'T_CORE_REPORT_CONFIG_LINE',
                        targetId : reportLineId
                    }
                });
                _this.userPermissionGrid.insert(IFM_CONTEXT + '/secure/core/module/mdm/user-permissions', chooseData);
            }
        });
    },

    delteUserPermission : function() {
        coreReportConfig.detailPage.userPermissionGrid.deleteRows('/secure/core/module/mdm/user-permissions');
    },

    insertPermissionByDept : function() {
        var _this = coreReportConfig.detailPage;
        var activedRow = Gikam.getComp('core-report-config-detail-line-grid').getActivedRow();
        if (!activedRow) {
            Gikam.alert("CORE.MODULE.SYS.REPORTS.TIP.CHOOSE_LINE");
            return;
        }

        var reportLineId = activedRow.id;
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('org-simple').title,
            url : IFM_CONTEXT + Gikam.choose.getConfig('org-simple').url + Gikam.param({
                orgCategory_SEQ : 'department',
                // parentId_SB : workspace.user.ext$.parentorgid
            }),
            onAfterClose : function(rows) {
            	console.log(rows);
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var urlNew = IFM_CONTEXT +'/secure/core/module/mdm/users/action/insert-report-config-by-dept';
                Gikam.postText(urlNew, Gikam.getJsonWrapper({
                },  [ 'CoreOrgService', rows ], [ 'CoreReportConfigLineService', [activedRow]  ])).done(function() {
                    Gikam.getComp('core-report-config-detail-user-permission-grid').refresh();
                });
            }
        });
    },

    deletePermissionByDept : function() {
        var _this = coreReportConfig.detailPage;
        var activedRow = Gikam.getComp('core-report-config-detail-line-grid').getActivedRow();
        if (!activedRow) {
            Gikam.alert("CORE.MODULE.SYS.REPORTS.TIP.CHOOSE_LINE");
            return;
        }

        var reportLineId = activedRow.id;
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('org-simple').title,
            url : IFM_CONTEXT + Gikam.choose.getConfig('org-simple').url + Gikam.param({
                orgCategory_SEQ : 'department',
                // parentId_SB : workspace.user.ext$.parentorgid,
                
            }),
            onAfterClose : function(rows) {
            	console.log(rows);
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var urlNew = IFM_CONTEXT +'/secure/core/module/mdm/users/action/delete-report-config-by-dept';
                Gikam.postText(urlNew, Gikam.getJsonWrapper({
                },  [ 'CoreOrgService', rows ], [ 'CoreReportConfigLineService', [activedRow] ])).done(function() {
                    Gikam.getComp('core-report-config-detail-user-permission-grid').refresh();
                });
            }
        });
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            resize : false,
            north : {
                height : 145,
                items : [ _this.getBtnToolbar(), _this.getBaseInfoTab() ]
            },
            center : {
                items : [
                    {
                        type : 'layout',
                        west : {
                            width : '70%',
                            items : [  _this.getCoreReportConfigLineTab() ]
                        },
                        center : {
                            items : [  _this.getPermissionTab()  ]
                        }
                    }
                ]
            },
            renderTo : workspace.window.$dom
        });

        return this;
    }

    
});
