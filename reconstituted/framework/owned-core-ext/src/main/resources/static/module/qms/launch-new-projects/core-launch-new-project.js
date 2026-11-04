var coreLaunchNewProject = Gikam.getPageObject({

    getListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreLaunchNewProject.workflow.dbTable), {
            field : 'projectNo',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.PROJECTNO'
        }, {
            field : 'projectName',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.PROJECTNAME'
        }, {
            field : 'createdByName',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CREATEDTIME'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.ID',
            preInsert : true
        }, {
            field : 'projectNo',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.PROJECTNO',
            validators : [ 'strLength[0,12]' ]
        }, {
            field : 'projectName',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.PROJECTNAME',
            validators : [ 'strLength[0,200]' ]
        }, {
            field : 'reason',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REASON'
        }, {
            field : 'resources',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.RESOURCES'
        }, {
            field : 'carryOrgId',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CARRYORGID',
            validators : [ 'strLength[0,12]' ]
        }, {
            field : 'carryOrgName',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CARRYORGNAME',
            validators : [ 'strLength[0,90]' ]
        }, {
            field : 'groupReviewAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.GROUPREVIEWADVICE'
        }, {
            field : 'groupReviewResult',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.GROUPREVIEWRESULT'
        }, {
            field : 'qmAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.QMADVICE'
        }, {
            field : 'carryTestContent',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CARRYTESTCONTENT'
        }, {
            field : 'carryTestResult',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CARRYTESTRESULT'
        }, {
            field : 'reviewContent',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWCONTENT'
        }, {
            field : 'reviewImprovedContent',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWIMPROVEDCONTENT'
        }, {
            field : 'reviewTechnicalAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWTECHNICALADVICE'
        }, {
            field : 'reviewQmAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWQMADVICE'
        }, {
            field : 'reviewRemark',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWREMARK'
        }, {
            field : 'confirmStandardName',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMSTANDARDNAME',
            validators : [ 'strLength[0,200]' ]
        }, {
            field : 'confirmStandardNo',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMSTANDARDNO',
            validators : [ 'strLength[0,90]' ]
        }, {
            field : 'confirmContent',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMCONTENT'
        }, {
            field : 'confirmResult',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMRESULT'
        }, {
            field : 'confirmQmAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMQMADVICE'
        }, {
            field : 'confirmLeaderAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMLEADERADVICE'
        }, {
            field : 'confirmRemark',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMREMARK'
        } ];
    },

    getNewProjectItemListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'folderNo',
            title : 'T_CORE_LAUNCH_NEW_PROJECT_ITEM.FOLDERNO',
            editor : true
        }, {
            field : 'type',
            title : 'T_CORE_LAUNCH_NEW_PROJECT_ITEM.TYPE',
            editor : true
        }, {
            field : 'sortor',
            title : 'T_CORE_LAUNCH_NEW_PROJECT_ITEM.SORTOR',
            editor : true
        }, {
            field : 'testStandard',
            title : 'T_CORE_LAUNCH_NEW_PROJECT_ITEM.TESTSTANDARD',
            editor : true
        }, {
            field : 'remark',
            title : 'T_CORE_LAUNCH_NEW_PROJECT_ITEM.REMARK',
            editor : true
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreLaunchNewProject.workflow.dbTable), {
            field : 'projectNo',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.PROJECTNO'
        }, {
            field : 'projectName',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.PROJECTNAME'
        }, {
            field : 'reason',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REASON'
        }, {
            field : 'resources',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.RESOURCES'
        }, {
            field : 'carryOrgId',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CARRYORGID'
        }, {
            field : 'carryOrgName',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CARRYORGNAME'
        }, {
            field : 'groupReviewAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.GROUPREVIEWADVICE'
        }, {
            field : 'groupReviewResult',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.GROUPREVIEWRESULT'
        }, {
            field : 'qmAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.QMADVICE'
        }, {
            field : 'carryTestContent',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CARRYTESTCONTENT'
        }, {
            field : 'carryTestResult',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CARRYTESTRESULT'
        }, {
            field : 'reviewContent',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWCONTENT'
        }, {
            field : 'reviewImprovedContent',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWIMPROVEDCONTENT'
        }, {
            field : 'reviewTechnicalAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWTECHNICALADVICE'
        }, {
            field : 'reviewQmAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWQMADVICE'
        }, {
            field : 'reviewRemark',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWREMARK'
        }, {
            field : 'confirmStandardName',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMSTANDARDNAME'
        }, {
            field : 'confirmStandardNo',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMSTANDARDNO'
        }, {
            field : 'confirmContent',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMCONTENT'
        }, {
            field : 'confirmResult',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMRESULT'
        }, {
            field : 'confirmQmAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMQMADVICE'
        }, {
            field : 'confirmLeaderAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMLEADERADVICE'
        }, {
            field : 'confirmRemark',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMREMARK'
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreLaunchNewProject.workflow.dbTable), {
            field : 'id',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.ID',
            type : 'link'
        }, {
            field : 'projectNo',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.PROJECTNO'
        }, {
            field : 'projectName',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.PROJECTNAME'
        }, {
            field : 'reason',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REASON'
        }, {
            field : 'resources',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.RESOURCES'
        }, {
            field : 'carryOrgId',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CARRYORGID'
        }, {
            field : 'carryOrgName',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CARRYORGNAME'
        }, {
            field : 'groupReviewAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.GROUPREVIEWADVICE'
        }, {
            field : 'groupReviewResult',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.GROUPREVIEWRESULT'
        }, {
            field : 'qmAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.QMADVICE'
        }, {
            field : 'carryTestContent',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CARRYTESTCONTENT'
        }, {
            field : 'carryTestResult',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CARRYTESTRESULT'
        }, {
            field : 'reviewContent',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWCONTENT'
        }, {
            field : 'reviewImprovedContent',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWIMPROVEDCONTENT'
        }, {
            field : 'reviewTechnicalAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWTECHNICALADVICE'
        }, {
            field : 'reviewQmAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWQMADVICE'
        }, {
            field : 'reviewRemark',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWREMARK'
        }, {
            field : 'confirmStandardName',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMSTANDARDNAME'
        }, {
            field : 'confirmStandardNo',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMSTANDARDNO'
        }, {
            field : 'confirmContent',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMCONTENT'
        }, {
            field : 'confirmResult',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMRESULT'
        }, {
            field : 'confirmQmAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMQMADVICE'
        }, {
            field : 'confirmLeaderAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMLEADERADVICE'
        }, {
            field : 'confirmRemark',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMREMARK'
        } ];
    },

    getChooseListColumns : function(isSingle) {
        var columns = [ {
            field : 'projectNo',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.PROJECTNO'
        }, {
            field : 'projectName',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.PROJECTNAME'
        }, {
            field : 'reason',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REASON'
        }, {
            field : 'resources',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.RESOURCES'
        }, {
            field : 'carryOrgId',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CARRYORGID'
        }, {
            field : 'carryOrgName',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CARRYORGNAME'
        }, {
            field : 'groupReviewAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.GROUPREVIEWADVICE'
        }, {
            field : 'groupReviewResult',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.GROUPREVIEWRESULT'
        }, {
            field : 'qmAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.QMADVICE'
        }, {
            field : 'carryTestContent',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CARRYTESTCONTENT'
        }, {
            field : 'carryTestResult',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CARRYTESTRESULT'
        }, {
            field : 'reviewContent',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWCONTENT'
        }, {
            field : 'reviewImprovedContent',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWIMPROVEDCONTENT'
        }, {
            field : 'reviewTechnicalAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWTECHNICALADVICE'
        }, {
            field : 'reviewQmAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWQMADVICE'
        }, {
            field : 'reviewRemark',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWREMARK'
        }, {
            field : 'confirmStandardName',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMSTANDARDNAME'
        }, {
            field : 'confirmStandardNo',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMSTANDARDNO'
        }, {
            field : 'confirmContent',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMCONTENT'
        }, {
            field : 'confirmResult',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMRESULT'
        }, {
            field : 'confirmQmAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMQMADVICE'
        }, {
            field : 'confirmLeaderAdvice',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMLEADERADVICE'
        }, {
            field : 'confirmRemark',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMREMARK'
        } ];
        if (isSingle == 1) {
            columns.splice(0, 1, {
                radio : true
            })
        }
        return columns;
    },

    getLaunchNewProjectApplyPanel : function() {
        return {
            title : 'T_CORE_LAUNCH_NEW_PROJECT.LAUNCH_NEW_PROJECT_APPLY',
            id : 'core-launch-new-project-apply-panel',
            fill : true,
            items : [ {
                type : 'form',
                columns : 1,
                id : 'core-launch-new-project-apply-form',
                service : 'CoreLaunchNewProjectServiceImpl',
                panels : [ {
                    fields : [ {
                        field : 'projectName',
                        title : 'T_CORE_LAUNCH_NEW_PROJECT.PROJECTNAME',
                        readonly : true,
                        validators : [ 'notEmpty' ]
                    }, {
                        field : 'reason',
                        title : 'T_CORE_LAUNCH_NEW_PROJECT.REASON',
                        type : 'textarea',
                        height : 90,
                        readonly : true,
                        validators : [ 'notEmpty' ]
                    }, {
                        field : 'resources',
                        title : 'T_CORE_LAUNCH_NEW_PROJECT.RESOURCES',
                        type : 'textarea',
                        height : 90,
                        readonly : true,
                        validators : [ 'notEmpty' ]
                    }, {
                        field : 'carryOrgName',
                        title : 'T_CORE_LAUNCH_NEW_PROJECT.CARRYORGNAME',
                        type : 'choose',
                        single : true,
                        readonly : true,
                        category : 'org',
                        validators : [ 'notEmpty' ],
                        targetFields : [ {
                            carryOrgName : 'orgName'
                        }, {
                            carryOrgId : 'orderNo'
                        } ]
                    } ]
                }, {
                    fields : [ {
                        field : 'groupReviewAdvice',
                        title : 'T_CORE_LAUNCH_NEW_PROJECT.GROUPREVIEWADVICE',
                        type : 'textarea',
                        height : 90,
                        readonly : true,
                        validators : [ 'notEmpty' ]
                    }, {
                        field : 'groupReviewResult',
                        title : 'T_CORE_LAUNCH_NEW_PROJECT.GROUPREVIEWRESULT',
                        type : 'textarea',
                        height : 90,
                        readonly : true,
                        validators : [ 'notEmpty' ]
                    } ]
                }, {
                    fields : [ {
                        field : 'qmAdvice',
                        title : 'T_CORE_LAUNCH_NEW_PROJECT.QMADVICE',
                        type : 'textarea',
                        height : 90,
                        readonly : true,
                        validators : [ 'notEmpty' ]
                    } ]
                } ]
            } ]
        };
    },

    getLaunchNewProjectItemPanel : function() {
        var _this = this;
        return {
            title : 'T_CORE_LAUNCH_NEW_PROJECT.NEW_PROJECT_EXECUTE',
            id : 'core-launch-new-project-item-panel',
            fill : true,
            items : [ {
                type : 'layout',
                north : {
                    height : '35%',
                    items : [ {
                        type : 'grid',
                        id : 'core-launch-new-project-item-panel-grid',
                        page : false,
                        toolbar : _this.getLaunchNewProjectItemPanelButtons(),
                        deleteFormatter : function(row) {
                            return row.folderNo;
                        },
                        onRowActive  : function(index, row) {
                            Gikam.getAsyncComp('core-launch-new-project-item-panel-uploader').done(function(uploader) {
                                uploader.setOptions({
                                    bizId : row.id
                                });
                            });
                        },
                        columns : [ {
                            checkbox : true
                        }, {
                            field : 'folderNo',
                            title : 'T_CORE_LAUNCH_NEW_PROJECT_ITEM.FOLDERNO',
                            readonly : true,
                        }, {
                            field : 'type',
                            title : 'T_CORE_LAUNCH_NEW_PROJECT_ITEM.TYPE',
                            readonly : true,
                        }, {
                            field : 'sortOr',
                            title : 'T_CORE_LAUNCH_NEW_PROJECT_ITEM.SORTOR',
                            readonly : true,
                        }, {
                            field : 'testStandard',
                            title : 'T_CORE_LAUNCH_NEW_PROJECT_ITEM.TESTSTANDARD',
                            readonly : true,
                        }, {
                            field : 'remark',
                            title : 'T_CORE_LAUNCH_NEW_PROJECT_ITEM.REMARK',
                            readonly : true,
                        } ]
                    } ]
                },
                south : {
                    height : '30%',
                    items : [ {
                        type : 'form',
                        id : 'core-launch-new-project-item-panel-Form',
                        columns : 1,
                        service : 'CoreLaunchNewProjectServiceImpl',
                        fields : [ {
                            field : 'carryTestContent',
                            title : 'T_CORE_LAUNCH_NEW_PROJECT.CARRYTESTCONTENT',
                            type : 'textarea',
                            height : 70,
                            validators : [ 'notEmpty' ],
                            readonly : true,
                        }, {
                            field : 'carryTestResult',
                            title : 'T_CORE_LAUNCH_NEW_PROJECT.CARRYTESTRESULT',
                            type : 'textarea',
                            height : 70,
                            validators : [ 'notEmpty' ],
                            readonly : true,
                        } ]
                    } ]
                },
                center : {
                    items : [ _this.getAttachment() ]
                }
            } ]
        };
    },

    getLaunchNewProjectAuditPanel : function() {
        return {
            title : 'T_CORE_LAUNCH_NEW_PROJECT.NEW_PROJECT_EXECUTE_REVIEW',
            id : 'core-launch-new-project-audit-panel',
            fill : true,
            items : [ {
                type : 'form',
                id : 'core-launch-new-project-audit-form',
                columns : 1,
                autoSave : true,
                service : 'CoreLaunchNewProjectServiceImpl',
                fields : [ {
                    field : 'projectName',
                    title : 'T_CORE_LAUNCH_NEW_PROJECT.PROJECTNAME',
                    validators : [ 'notEmpty' ],
                    readonly : true,
                }, {
                    field : 'reviewContent',
                    title : 'T_CORE_LAUNCH_NEW_PROJECT.REVIEWCONTENT',
                    type : 'textarea',
                    height : 100,
                    validators : [ 'notEmpty' ],
                    readonly : true,
                }, {
                    field : 'reviewImprovedContent',
                    title : 'T_CORE_LAUNCH_NEW_PROJECT.IMPROVE_CONTENT',
                    type : 'textarea',
                    height : 100,
                    validators : [ 'notEmpty' ],
                    readonly : true,
                }, {
                    field : 'reviewTechnicalAdvice',
                    title : 'T_CORE_LAUNCH_NEW_PROJECT.TECH_MANAGER_ADVICE',
                    type : 'textarea',
                    height : 100,
                    validators : [ 'notEmpty' ],
                    readonly : true,
                }, {
                    field : 'reviewQmAdvice',
                    title : 'T_CORE_LAUNCH_NEW_PROJECT.QUALITY_MANAGER_ADVICE',
                    type : 'textarea',
                    height : 100,
                    validators : [ 'notEmpty' ],
                    readonly : true,
                }, {
                    field : 'reviewRemark',
                    title : 'T_CORE_LAUNCH_NEW_PROJECT.REMARK',
                    type : 'textarea',
                    height : 100,
                    validators : [ 'notEmpty' ],
                    readonly : true,
                } ]
            } ]
        }
    },

    getMethodConfirmPanel : function() {
        return {
            title : 'T_CORE_LAUNCH_NEW_PROJECT.METHOD_CONFIRM',
            id : 'core-launch-new-project-method-confirm-panel',
            fill : true,
            items : [ {
                type : 'form',
                id : 'core-launch-new-project-method-confirm-form',
                columns : 1,
                titleWidth : 100,
                service : 'CoreLaunchNewProjectServiceImpl',
                fields : [ {
                    field : 'confirmStandardName',
                    title : 'T_CORE_LAUNCH_NEW_PROJECT.STANDARD_NAME',
                    validators : [ 'notEmpty' ],
                    readonly : true,
                }, {
                    field : 'confirmStandardNo',
                    title : 'T_CORE_LAUNCH_NEW_PROJECT.STANDARD_NO',
                    type : 'textarea',
                    height : 80,
                    validators : [ 'notEmpty' ],
                    readonly : true,
                }, {
                    field : 'confirmContent',
                    title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMCONTENT',
                    type : 'textarea',
                    height : 80,
                    validators : [ 'notEmpty' ],
                    readonly : true,
                }, {
                    field : 'confirmResult',
                    title : 'T_CORE_LAUNCH_NEW_PROJECT.RESULT',
                    type : 'textarea',
                    height : 80,
                    validators : [ 'notEmpty' ],
                    readonly : true,
                }, {
                    field : 'confirmQmAdvice',
                    title : 'T_CORE_LAUNCH_NEW_PROJECT.QUALITY_MANAGER_ADVICE',
                    type : 'textarea',
                    height : 80,
                    validators : [ 'notEmpty' ],
                    readonly : true,
                }, {
                    field : 'confirmLeaderAdvice',
                    title : 'T_CORE_LAUNCH_NEW_PROJECT.CONFIRMLEADERADVICE',
                    type : 'textarea',
                    height : 80,
                    validators : [ 'notEmpty' ],
                    readonly : true,
                }, {
                    field : 'confirmRemark',
                    title : 'T_CORE_LAUNCH_NEW_PROJECT.REMARK',
                    type : 'textarea',
                    height : 80,
                    validators : [ 'notEmpty' ],
                    readonly : true,
                } ]
            } ]
        }
    },

    getAttachmentPanel : function() {
        return {
            title : 'GIKAM.FILE.ATTACHMENT',
            items : [{
                id : 'core-launch-new-project-attachment-panel-uploader',
                type : 'uploader',
                dbTable : 'T_CORE_LAUNCH_NEW_PROJECT',
                bizId : 0,
                onBeforeDelete : function() {
                    var rows = this.grid.data;
                    var canDelete = true;
                    Gikam.each(rows, function(index, row) {
                        if(workspace.user.id != row.createdById) {
                            canDelete = false;
                            Gikam.alert('QMS.UPLOADER.ALERT.CANNOT_DELETE_FILE_UPLOADED_BY_OTHERS');
                            return;
                        }
                    })
                    return canDelete;
                }
            }]
        }
    },

    getDetailTab : function() {
        var _this = this;
        return {
            id : 'core-launch-new-project-detail-tab',
            type : 'tab',
            fill : true,
            panels : [ _this.getLaunchNewProjectApplyPanel(), _this.getLaunchNewProjectItemPanel(), _this.getLaunchNewProjectAuditPanel(), _this.getMethodConfirmPanel(), _this.getAttachmentPanel() ]
        }
    },

    getLaunchNewProjectItemPanelButtons : function() {
        var _this = this;
        return [ {
            type : 'button',
            id : 'core-launch-new-project-add-research-config-button',
            text : 'T_CORE_LAUNCH_NEW_PROJECT.ADD_EXECUTE_CONFIG_BUTTON',
            icon : 'add',
            onClick : function() {
                var id = Gikam.getComp('core-launch-new-project-item-panel-Form').getData().id;
                if (id == undefined) {
                    Gikam.alert('T_CORE_LAUNCH_NEW_PROJECT.MSG.PLEASE_CHOOSE_ONE_ITEM_BEFORE');
                    return;
                }
                Gikam.preInsert({
                    modalTitle : 'T_CORE_LAUNCH_NEW_PROJECT.ADD_EXECUTE_CONFIG_BUTTON',
                    fields : [ {
                        field : 'sortName',
                        title : 'T_CORE_LAUNCH_NEW_PROJECT.SORT_NAME',
                        validators : [ 'notEmpty' ],
                        preInsert : true
                    }, {
                        field : 'folderNo',
                        title : 'T_CORE_LAUNCH_NEW_PROJECT_ITEM.FOLDERNO',
                        validators : [ 'notEmpty' ],
                        preInsert : true
                    }, {
                        field : 'testStandard',
                        title : 'T_CORE_LAUNCH_NEW_PROJECT_ITEM.TESTSTANDARD',
                        validators : [ 'notEmpty' ],
                        preInsert : true,
                        type : 'textarea',
                        height : 100,
                    } ],
                    url : Gikam.printf(_this.baseUrl + '/{id}/new-project-items', {
                        id : id
                    })
                }).done(function(itemId) {
                    Gikam.getComp('core-launch-new-project-item-panel-grid').refresh({
                        url : Gikam.printf(_this.baseUrl + '/{id}/new-project-items/queries', {
                            id : id
                        })
                    });
                });
            }
        }, {
            type : 'button',
            id : 'core-launch-new-project-delete-research-config-button',
            text : 'GIKAM.BUTTON.DELETE',
            icon : 'remove',
            onClick : function() {
                var id = Gikam.getComp('core-launch-new-project-item-panel-Form').getData().id;
                if (id == undefined) {
                    Gikam.alert('T_CORE_LAUNCH_NEW_PROJECT.MSG.PLEASE_CHOOSE_ONE_ITEM_BEFORE');
                    return;
                }
                var url = _this.baseUrl + '/{id}/new-project-items';
                Gikam.getComp('core-launch-new-project-item-panel-grid').deleteRows(Gikam.printf(url, {
                    id : id
                }));
            }
        } ]
    },

    getBtnToolbar : function(gridId) {
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    var rows = Gikam.getComp(gridId).getSelections();
                    if (rows.length != 1) {
                        Gikam.alert('QMS.MODULE.ANNUALPLAN.PRINT.ONLY_SELECT_ONE_ROW');
                        return;
                    }
                    Gikam.viewReport('QMS_LAUNCH_NEW_PROJECT', {
                        id : rows[0].id
                    });
                }
            } ]
        };
    },

    getAttachment : function() {
        return {
            id : 'core-launch-new-project-item-panel-uploader',
            type : 'uploader',
            dbTable : 'T_CORE_LAUNCH_NEW_PROJECT_ITEM',
            bizId : 0,
            onBeforeDelete : function() {
                var rows = this.grid.data;
                var canDelete = true;
                Gikam.each(rows, function(index, row) {
                    if (workspace.user.id != row.createdById) {
                        canDelete = false;
                        Gikam.alert('QMS.UPLOADER.ALERT.CANNOT_DELETE_FILE_UPLOADED_BY_OTHERS');
                        return;
                    }
                })
                return canDelete;
            }
        }
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/launch-new-projects',

    workflow : {
        dbTable : 'T_CORE_LAUNCH_NEW_PROJECT',
        columns : [ {
            field : 'id',
            title : 'T_CORE_LAUNCH_NEW_PROJECT.ID'
        } ]
    }
});
