emcReport.assignListPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-report-assign-list-grid',
            url : '/secure/emc/module/bp/project/projects/queries',
            columns : emcReport.getProjectAssignListColumns(),
            service : 'emcProjectServiceImpl',
            page : true,
            filterOpen : true,
            pageSize : 50,
            requestData :  {
                status_IN : 'reportEdit',
                orgId_SB : workspace.user.ext$.parentorgid,
                reportAssignStatus_IN : '0,',
                targetType : 'reportAssign',
                showAll : '0'
            },
            order : {
                id : 'desc'
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BATCH_INPUT',
                icon : 'edit',
                onClick : function () {
                    _this.batchInput();
                }
            }, {
                type : 'form',
                autoSave : false,
                fields : [ {
                    title : '显示已指派',
                    field : 'showAssigned',
                    type : 'simpleCheckbox',
                    refreshGrid : false,
                    styleFormatter : function() {
                        return {
                            paddingLeft : '0'
                        }
                    },
                    onChange : function(field, value, index) {
                        Gikam.getComp('emc-report-assign-list-grid').refresh({
                            requestData : {
                                status_IN : (value === '1' ) ? 'reportEdit,reportAudit' : 'reportEdit',
                                reportAssignStatus_IN : (value === '1') ? '1' : '0,'
                            }
                        });
                    },
                } ]
            }, {
                type : 'form',
                autoSave : false,
                fields : [ {
                    title : '显示全部',
                    field : 'showAll',
                    type : 'simpleCheckbox',
                    refreshGrid : false,
                    styleFormatter : function() {
                        return {
                            paddingLeft : '0'
                        }
                    },
                    onChange : function(field, value, index) {
                        Gikam.getComp('emc-report-assign-list-grid').refresh({
                            requestData : {
                                showAll : value === '1' ? '1' : '0'
                            }
                        });
                    },
                } ]
            }  ]
        }
    },

    submit : function() {
        let grid = Gikam.getComp('emc-report-assign-list-grid');
        let selections = grid.getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        if (!grid.validate(selections)) {
            return;
        }

        let assigned = selections.some(item => item.reportAssignStatus === '1');
        if (assigned) {
            return;
        }

        Gikam.confirm('确认操作', '是否确认提交?', function() {
            workspace.window.showMask(true);
            Gikam.postText("/secure/emc/module/bp/project/projects/action/report-assign-submit", Gikam.getJsonWrapper({}, [ '', selections ])).done(function () {
                grid.refresh();
            }).always(function () {
                workspace.window.closeMask();
            });
        })
    },

    batchInput : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-report-assign-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 350,
            height : 260,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        icon : 'select',
                        onClick : function() {
                            var formData = form.getData();
                            var updateData = selections.map(item => {
                                return {
                                    id : item.id,
                                    reportEditUserId : formData.reportEditUserId,
                                    reportEditUserName : formData.reportEditUserName
                                }
                            });

                            workspace.window.showMask(true);

                            Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcProjectServiceImpl', updateData ])).done(function() {
                                Gikam.getComp('emc-report-assign-list-grid').refresh();
                            }).always(function() {
                                workspace.window.closeMask();
                                modal.close();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        icon : 'cancel',
                        onClick : function () {
                            modal.close();
                        }
                    } ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            id : 'emc-test-capacity-batch-input-form',
            columns: 1,
            titleWidth: 80,
            fields: [ {
                field : 'reportEditUserName',
                title : 'T_EMC_PROJECT.REPORTEDITUSERNAME',
                validators : ['notEmpty'],
                category : 'emp',
                type : 'choose',
                single : true,
                onBeforeChoose : function() {
                    return {
                        editReport_SEQ : '1',
                        orgId_SEQ : workspace.user.ext$.parentorgid,
                    }
                },
                onAfterChoose : function(rows) {
                    if (!Gikam.isEmpty(rows)) {
                        this.setData({
                            reportEditUserName : rows[0].userName,
                            reportEditUserId : rows[0].id,
                        });
                    }
                },
                onClean : function() {
                    this.setData({
                        reportEditUserName : '',
                        reportEditUserId : ''
                    });
                }
            } ],
            autoSave: false
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-report-assign-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
