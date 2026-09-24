var emcReport = Gikam.getPageObject({
    baseUrl: IFM_CONTEXT + '/secure/emc/module/bp/report/reports',

    getProjectAssignListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'projNo',
            title: 'T_EMC_PROJECT.PROJNO'
        }, {
            field: 'projName',
            title: 'T_EMC_PROJECT.PROJNAME'
        }, {
            field: 'sampleOrderCount',
            title: 'T_EMC_PROJECT.ORDERNUM'
        }, {
            field: 'reportEditUserName',
            title: 'T_EMC_PROJECT.REPORTEDITUSERNAME',
            editor: true,
            validators: ['notEmpty'],
            category: 'emp',
            type: 'choose',
            single: true,
            onBeforeChoose: function () {
                return {
                    editReport_SEQ: '1',
                    orgId_SEQ: workspace.user.ext$.parentorgid,
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id: this.getActivedRow().id,
                        reportEditUserName: rows[0].userName,
                        reportEditUserId: rows[0].id,
                    });
                }
            },
            onClean: function () {
                this.setData({
                    id: this.getActivedRow().id,
                    reportEditUserName: '',
                    reportEditUserId: ''
                });
            }
        }, {
            field: 'testOrgName',
            title: 'T_EMC_PROJECT.TESTORG'
        }, {
            field: 'entrustOrgName',
            title: 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field: 'bizCategoryId',
            title: 'T_EMC_PROJECT.BIZCATEGORY',
            type: 'select',
            search: true,
            category: 'bizCategory',
            firstBlank: false,
        }, {
            field: 'toReportEditDate',
            title: '至报告编制日期',
            type: 'date',
        },]
    },

    getProjectListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'toReportEditDate',
            title: '至报告编制日期',
            type: 'date',
        }, {
            field: 'projNo',
            title: '任务编号',
            formatter: function (i, v, r) {
                if (r.urgent == 1) {
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                } else {
                    return v;
                }
            },
        }, {
            field: 'projName',
            title: 'T_EMC_PROJECT.PROJNAME'
        }, {
            field: 'testOrgName',
            title: 'T_EMC_PROJECT.TESTORG'
        }, {
            field: 'entrustOrgName',
            title: 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field: 'bizCategoryId',
            title: 'T_EMC_PROJECT.BIZCATEGORY',
            type: 'select',
            search: true,
            category: 'bizCategory',
            firstBlank: false,
        }, {
            field: 'reportEditUserName',
            title: 'T_EMC_PROJECT.REPORTEDITUSERNAME',
            editor: true,
            category: 'reportEditEmp',
            type: 'choose',
            single: true,
            onBeforeChoose: function () {
                return {
                    editReport_SEQ: '1',
                }
            },
            onAfterChoose: function (rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index: index,
                        reportEditUserName: rows[0].userName,
                        reportEditUserId: rows[0].id
                    });
                }
            },
            onClean: function () {
                this.setData({
                    id: this.getActivedRow().id,
                    reportEditUserName: '',
                    reportEditUserId: ''
                });
            }
        }, {
            field: 'ext$.reportchangeinfo',
            title: '报告变更情况'
        },]
    },

    getEditListColumns: function () {
        var _this = this;
        return [{
            checkbox: true
        }, Gikam.status.getBpmnColumn(emcReport.workflow.dbTable), {
            field: 'ext$.projno',
            title: '任务编号'
            // }, {
            //     field : 'reportName',
            //     title : 'T_EMC_REPORT.REPORTNAME',
            //     editor : true
        }/*, {
            field : 'ext$.reporteditusername',
            title : 'T_EMC_REPORT.REPORTEDITUSERNAME'
        }*/, {
            field: 'reportNo',
            title: '报告编号',
            editor: true
        }, {
            field: 'ext$.pageno',
            title: '页码',
            editor: true
        }, {
            field: 'ext$.showlimit',
            title: '显示限值',
            editor: true,
            type: 'simpleCheckbox'

        }, {
            field: 'ext$.reportreviewername',
            title: 'T_EMC_REPORT.REPORTREVIEWERNAME',
            editor: true,
            width: 100,
            category: 'emp',
            type: 'choose',
            single: true,
            validators: ['notEmpty'],
            onBeforeChoose: function () {
                // var activedRow = Gikam.getComp('emc-report-edit-list-grid').getActivedRow();
                return {
                    REPORTAUDIT_SEQ: '1',
                    ID_DIFFER: workspace.user.id
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-report-edit-list-grid').setData({
                        id: Gikam.getComp('emc-report-edit-list-grid').getActivedRow().id,
                        ext$: {
                            reportreviewername: rows[0].userName,
                            reportreviewerid: rows[0].id
                        }
                    });
                    Gikam.put(_this.baseUrl + '/instant', Gikam.getJsonWrapper({}, ['emcProjectServiceImpl', [{
                        id: Gikam.getComp('emc-report-edit-list-grid').getActivedRow().projId,
                        ext$: {
                            reportreviewername: rows[0].userName,
                            reportreviewerid: rows[0].id
                        }
                    }]]));
                }
            },
            onClean: function () {
                Gikam.getComp('emc-report-edit-list-grid').setData({
                    id: Gikam.getComp('emc-report-edit-list-grid').getActivedRow().id,
                    ext$: {
                        reportreviewername: '',
                        reportreviewerid: ''
                    }
                });
                Gikam.put(_this.baseUrl + '/instant', Gikam.getJsonWrapper({}, ['emcProjectServiceImpl', [{
                    id: Gikam.getComp('emc-report-edit-list-grid').getActivedRow().projId,
                    ext$: {
                        reportreviewername: '',
                        reportreviewerid: ''
                    }
                }]]));
            }
        }, {
            //     field : 'ext$.reportissueusername',
            //     title : 'T_EMC_REPORT.REPORTISSUEUSERNAME',
            //     editor : true,
            //     width : 100,
            //     category : 'emp',
            //     type : 'choose',
            //     single : true,
            //     onBeforeChoose : function() {
            //         // var activedRow = Gikam.getComp('emc-report-edit-list-grid').getActivedRow();
            //         return {
            //             REPORTISSUE_SEQ : '1'
            //         }
            //     },
            //     onAfterChoose : function(rows) {
            //         if (!Gikam.isEmpty(rows)) {
            //             Gikam.getComp('emc-report-edit-list-grid').setData({
            //                 id : Gikam.getComp('emc-report-edit-list-grid').getActivedRow().id,
            //                 ext$ : {
            //                     reportissueusername : rows[0].userName,
            //                     reportissueuserid : rows[0].id
            //                 }
            //             });
            //             Gikam.put(_this.baseUrl + '/instant', Gikam.getJsonWrapper({}, [ 'emcProjectServiceImpl', [ {
            //                 id : Gikam.getComp('emc-report-edit-list-grid').getActivedRow().projId,
            //                 ext$ : {
            //                     reportissueusername : rows[0].userName,
            //                     reportissueuserid : rows[0].id
            //                 }
            //             } ] ]));
            //         }
            //     },
            //     onClean : function() {
            //         Gikam.getComp('emc-report-edit-list-grid').setData({
            //             id : Gikam.getComp('emc-report-edit-list-grid').getActivedRow().id,
            //             ext$ : {
            //                 reportissueusername : '',
            //                 reportissueuserid : ''
            //             }
            //         });
            //         Gikam.put(_this.baseUrl + '/instant', Gikam.getJsonWrapper({}, [ 'emcProjectServiceImpl', [ {
            //             id : Gikam.getComp('emc-report-edit-list-grid').getActivedRow().projId,
            //             ext$ : {
            //                 reportissueusername : '',
            //                 reportissueuserid : ''
            //             }
            //         } ] ]));
            //     }
            // }, {
            field: 'ext$.templatename',
            title: 'T_EMC_REPORT.REPORTCONFIGNAME',
            type: 'choose',
            category: 'report-config',
            single: true,
            onBeforeChoose: function (index) {
                return {
                    reportConfigId_SEQ: 'wordReport',
                };
            },
            onAfterChoose: function (rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index: index,
                        reportConfigLineId: rows[0].id,
                        ext$: {
                            templatename: rows[0].reportName
                        }
                    });
                }
            },
            onClean: function () {
                this.setData({
                    id: this.getActivedRow().id,
                    reportConfigLineId: '',
                    ext$: {
                        templatename: ''
                    }
                });
            },
            editor: true
        }, {
            field: 'reportTemplateName',
            title: '新word报告模板',
            type: 'choose',
            category: 'report-template-choose',
            single: true,
            targetFields: [{
                reportTemplateId: 'id'
            }, {
                reportTemplateName: 'templateName'
            }],
            onBeforeChoose: function () {
                return {
                    subtmplfilter: 1, // 过滤子模版不展示,
                }
            },
            editor: true,
        }, {
            field: 'ext$.qcreportname',
            title: 'T_EMC_REPORT.QCREPORTLINEID',
            type: 'choose',
            category: 'report-config',
            single: true,
            onBeforeChoose: function (index) {
                return {
                    reportConfigId_SEQ: 'wordReport',
                };
            },
            onAfterChoose: function (rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index: index,
                        ext$: {
                            qcreportlineid: rows[0].id,
                            qcreportname: rows[0].reportName
                        }
                    });
                }
            },
            onClean: function () {
                this.setData({
                    id: this.getActivedRow().id,
                    ext$: {
                        qcreportlineid: '',
                        qcreportname: ''
                    }
                });
            },
            editor: true
        }, {
            field: 'dataSheetId',
            title: 'T_EMC_REPORT.DATASHEETID',
            type: 'select',
            search: true,
            category: 'reportConfigLine',
            param: {
                reportConfigId: 'rasproject'
            },
            editor: true
        }, {
            field: 'ifStatistics',
            title: 'T_EMC_REPORT.IFSTATISTICS',
            editor: true,
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            }
        }, {
            field: 'reportAptitude',
            title: 'T_EMC_PROJECT.REPORTAPTITUDE',
            editor: true,
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'reportAptitude'
            },
        }, {
            field: 'ext$.reportremark',
            title: 'T_EMC_REPORT.REMARK',
            type: 'richText',
            // editor: true,
        }, {
            field: 'ext$.projusername',
            title: 'T_EMC_REPORT.PROJUSERID'
        }, {
            field: 'ext$.testorgname',
            title: 'T_EMC_REPORT.TESTORGNAME'
        }, {
            field: 'ext$.entrustorgname',
            title: 'T_EMC_REPORT.ENTRUSTORGNAME'
        }, {
            field: 'ext$.bizcategoryname',
            title: 'T_EMC_REPORT.BIZCATEGORY'
        }, {
            field: 'orderCategory',
            title: '样品类型'
        }, {
            field: 'qcReport',
            title: '是否生成质控表',
            type: 'select',
            search: false,
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            },
            editor: true
        }, {
            field: 'addInfoIndexFlag',
            title: '是否添加评价标准',
            type: 'select',
            search: false,
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            },
            editor: true
        }];
    },

    getUpdateListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.taskname_',
            title: '状态',
        }, {
            field: 'ext$.projno',
            title: '任务编号'
        }, {
            field: 'ext$.projname',
            title: '任务名称'
        }, {
            field: 'orderCategory',
            title: 'T_EMC_REPORT.ORDERCATEGORY'
        }, {
            field: 'ext$.reportname',
            title: 'T_EMC_REPORT.REPORTCONFIGNAME'
        }, {
            field: 'monitorInfo',
            title: 'T_EMC_REPORT.MONITORINFO',
            type: 'richText',
            editor: true
        }, {
            field: 'testConclusion',
            title: 'T_EMC_REPORT.TESTCONCLUSION',
            type: 'richText',
            editor: true
        }, {
            field: 'workCondition',
            title: 'T_EMC_REPORT.WORKCONDITION',
            type: 'richText',
            editor: true
        }, {
            field: 'ext$.reportremark',
            title: 'T_EMC_REPORT.REMARK',
            type: 'richText',
        }, {
            field: 'ext$.projusername',
            title: 'T_EMC_REPORT.PROJUSERID'
        }, {
            field: 'ext$.testorgname',
            title: 'T_EMC_REPORT.TESTORGNAME'
        }, {
            field: 'ext$.entrustorgname',
            title: 'T_EMC_REPORT.ENTRUSTORGNAME'
        }, {
            field: 'ext$.bizcategoryname',
            title: 'T_EMC_REPORT.BIZCATEGORY'
        }, {
            field: 'ext$.firstissuedate',
            title: '入库日期',
            type: 'date',
        },{
            field: 'ext$.projdeliverydate',
            title: '出库日期',
            type: 'date',
        }];
    },

    getAuditListColumns: function () {
        var _this = this;
        return [{
            checkbox: true
        }, Gikam.status.getBpmnColumn(emcReport.workflow.dbTable), {
            field: 'ext$.projno',
            title: '任务编号',
            formatter: function (i, v, r) {
                if (r.ext$.urgent == 1) {
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                } else {
                    return v;
                }
            },
        }, {
            field: 'ext$.projname',
            title: '任务名称'
        }, {
            field: 'ext$.projusername',
            title: '业务员'
        }, {
            field: 'reportNo',
            title: '报告编号',
        }, {
            field: 'ext$.expressno',
            title: '快递单号'
        }, {
            field: 'ext$.reportreviewername',
            title: 'T_EMC_REPORT.REPORTREVIEWERNAME',
            editor: true,
            width: 100,
            category: 'emp',
            type: 'choose',
            single: true,
            validators: ['notEmpty'],
            onBeforeChoose: function () {
                // var activedRow = Gikam.getComp('emc-report-edit-list-grid').getActivedRow();
                return {
                    REPORTAUDIT_SEQ: '1',
                    ID_DIFFER: workspace.user.id
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-report-audit-list-grid').setData({
                        id: Gikam.getComp('emc-report-audit-list-grid').getActivedRow().id,
                        ext$: {
                            reportreviewername: rows[0].userName,
                            reportreviewerid: rows[0].id
                        }
                    });
                    Gikam.put(_this.baseUrl + '/instant', Gikam.getJsonWrapper({}, ['emcProjectServiceImpl', [{
                        id: Gikam.getComp('emc-report-audit-list-grid').getActivedRow().projId,
                        ext$: {
                            reportreviewername: rows[0].userName,
                            reportreviewerid: rows[0].id
                        }
                    }]]));
                }
            },
            onClean: function () {
                Gikam.getComp('emc-report-audit-list-grid').setData({
                    id: Gikam.getComp('emc-report-audit-list-grid').getActivedRow().id,
                    ext$: {
                        reportreviewername: '',
                        reportreviewerid: ''
                    }
                });
                Gikam.put(_this.baseUrl + '/instant', Gikam.getJsonWrapper({}, ['emcProjectServiceImpl', [{
                    id: Gikam.getComp('emc-report-audit-list-grid').getActivedRow().projId,
                    ext$: {
                        reportreviewername: '',
                        reportreviewerid: ''
                    }
                }]]));
            }
        }, {
            field: 'ext$.reportissueusername',
            title: 'T_EMC_REPORT.REPORTISSUEUSERNAME',
            editor: true,
            width: 100,
            category: 'emp',
            type: 'choose',
            single: true,
            validators: ['notEmpty'],
            onBeforeChoose: function () {
                // var activedRow = Gikam.getComp('emc-report-audit-list-grid').getActivedRow();
                return {
                    REPORTISSUE_SEQ: '1'
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-report-audit-list-grid').setData({
                        id: Gikam.getComp('emc-report-audit-list-grid').getActivedRow().id,
                        ext$: {
                            reportissueusername: rows[0].userName,
                            reportissueuserid: rows[0].id
                        }
                    });
                    Gikam.put(_this.baseUrl + '/instant', Gikam.getJsonWrapper({}, ['emcProjectServiceImpl', [{
                        id: Gikam.getComp('emc-report-audit-list-grid').getActivedRow().projId,
                        ext$: {
                            reportissueusername: rows[0].userName,
                            reportissueuserid: rows[0].id
                        }
                    }]]));
                }
            },
            onClean: function () {
                Gikam.getComp('emc-report-audit-list-grid').setData({
                    id: Gikam.getComp('emc-report-audit-list-grid').getActivedRow().id,
                    ext$: {
                        reportissueusername: '',
                        reportissueuserid: ''
                    }
                });
                Gikam.put(_this.baseUrl + '/instant', Gikam.getJsonWrapper({}, ['emcProjectServiceImpl', [{
                    id: Gikam.getComp('emc-report-audit-list-grid').getActivedRow().projId,
                    ext$: {
                        reportissueusername: '',
                        reportissueuserid: ''
                    }
                }]]));
            }
        }, {
            field: 'issueSignMode',
            title: 'T_EMC_REPORT.ISSUESIGNMODE',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'issueSignMode'
            },
            editor: true,
            width: 120
        }, {
            field: 'orderCategory',
            title: 'T_EMC_REPORT.ORDERCATEGORY'
        }, {
            field: 'ext$.reportname',
            title: 'T_EMC_REPORT.REPORTCONFIGNAME'
        }, {
            field: 'ext$.rsreportname',
            title: 'T_EMC_REPORT.RSREPORTCONFIGLINEID',
        }, {
            field: 'ext$.completedate',
            title: '报告签发完成日期',
            type: 'date',
            editor: true
        }, {
            field: 'ext$.projcompledate',
            title: '报告完成期限',
            type: 'date',
        }, {
            field: 'monitorInfo',
            title: 'T_EMC_REPORT.MONITORINFO'
        }, {
            field: 'testConclusion',
            title: 'T_EMC_REPORT.TESTCONCLUSION'
        }, {
            field: 'workCondition',
            title: 'T_EMC_REPORT.WORKCONDITION'
        }, {
            field: 'ext$.reportremark',
            title: 'T_EMC_REPORT.REMARK',
            type: 'richText',
        }, {
            field: 'ext$.testorgname',
            title: 'T_EMC_REPORT.TESTORGNAME'
        }, {
            field: 'ext$.entrustorgname',
            title: 'T_EMC_REPORT.ENTRUSTORGNAME'
        }, {
            field: 'ext$.bizcategoryname',
            title: 'T_EMC_REPORT.BIZCATEGORY'
        },];
    },

    getReportSendInfoListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'expressNo',
            title: '快递单号',
        }, {
            field: 'ext$.projno',
            title: '任务编号',
        }, {
            field: 'ext$.backsendno',
            title: '回签单号',
        }, {
            field: 'ext$.orgid',
            title: '所属公司',
        },];
    },

    /** 报告归档 */
    getArchivingListColumns: function () {
        var _this = this;
        return [{
            checkbox: true
        }, Gikam.status.getBpmnColumn(emcReport.workflow.dbTable), {
            field: 'archivedFlag',
            title: '是否归档',
            type: 'select',
            items: [{
                text: '已归档',
                value: '1'
            }, {
                text: '未归档',
                value: '0'
            }]
        }, {
            field: 'ext$.projno',
            title: '任务编号',
            formatter: function (i, v, r) {
                if (r.ext$.urgent == 1) {
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                } else {
                    return v;
                }
            },
        }, {
            field: 'ext$.projname',
            title: '任务名称'
        },{
            field: 'ext$.subpackage',
            title: 'T_EMC_PROJECT.SUBPACKAGE',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno'
            },
        }, {
            field: 'ext$.reporteditusername',
            title: 'T_EMC_REPORT.REPORTEDITUSERNAME'
        }, {
            field: 'ext$.reportreviewername',
            title: 'T_EMC_REPORT.REPORTREVIEWERNAME',
        }, {
            field: 'ext$.reportissueusername',
            title: 'T_EMC_REPORT.REPORTISSUEUSERNAME',
            width: 100,
            category: 'emp',
            type: 'choose',
            single: true,
        },{
            field: 'ext$.completedate',
            title: '签发日期',
            type: 'date',
        }, {
            field: 'orderCategory',
            title: 'T_EMC_REPORT.ORDERCATEGORY'
        }, {
            field: 'ext$.reportname',
            title: 'T_EMC_REPORT.REPORTCONFIGNAME'
        }, {
            field: 'ext$.rsreportname',
            title: 'T_EMC_REPORT.RSREPORTCONFIGLINEID',
        }, {
            field: 'monitorInfo',
            title: 'T_EMC_REPORT.MONITORINFO'
        }, {
            field: 'testConclusion',
            title: 'T_EMC_REPORT.TESTCONCLUSION'
        }, {
            field: 'workCondition',
            title: 'T_EMC_REPORT.WORKCONDITION'
        }, {
            field: 'ext$.reportremark',
            title: 'T_EMC_REPORT.REMARK',
            type: 'richText',
        }, {
            field: 'ext$.projusername',
            title: 'T_EMC_REPORT.PROJUSERID'
        }, {
            field: 'ext$.testorgname',
            title: 'T_EMC_REPORT.TESTORGNAME'
        }, {
            field: 'ext$.entrustorgname',
            title: 'T_EMC_REPORT.ENTRUSTORGNAME'
        }, {
            field: 'ext$.bizcategoryname',
            title: 'T_EMC_REPORT.BIZCATEGORY'
        }, {
            field: 'archivedDate',
            title: '归档日期'
        }, {
            field: 'archivedUserName',
            title: '归档人'
        },];
    },

    getReportSearchListColumns: function () {
        var _this = this;
        return [{
            checkbox: true
        }, Gikam.status.getBpmnColumn(emcReport.workflow.dbTable), {
            field: 'ext$.orderdate',
            title: '送/采样日期',
        }, {
            field: 'ext$.projno',
            title: '任务编号',
            formatter: function (i, v, r) {
                if (r.ext$.urgent == 1) {
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                } else {
                    return v;
                }
            },
        }, {
            field: 'ext$.projname',
            title: '任务名称'
        }, {
            field: 'ext$.reporteditusername',
            title: 'T_EMC_REPORT.REPORTEDITUSERNAME'
        }, {
            field: 'ext$.reportreviewername',
            title: 'T_EMC_REPORT.REPORTREVIEWERNAME',
        }, {
            field: 'ext$.reportissueusername',
            title: 'T_EMC_REPORT.REPORTISSUEUSERNAME',
            width: 100,
            category: 'emp',
            type: 'choose',
            single: true,
        }, {
            field: 'ext$.reportissuetime',
            title: '报告签发日期',
            type: 'date',
        }, {
            field: 'orderCategory',
            title: 'T_EMC_REPORT.ORDERCATEGORY'
        }, {
            field: 'ext$.pageno',
            title: '页码'
        }, {
            field: 'ext$.reportname',
            title: 'T_EMC_REPORT.REPORTCONFIGNAME'
        }, {
            field: 'ext$.rsreportname',
            title: 'T_EMC_REPORT.RSREPORTCONFIGLINEID',
        }, {
            field: 'monitorInfo',
            title: 'T_EMC_REPORT.MONITORINFO'
        }, {
            field: 'testConclusion',
            title: 'T_EMC_REPORT.TESTCONCLUSION'
        }, {
            field: 'workCondition',
            title: 'T_EMC_REPORT.WORKCONDITION'
        }, {
            field: 'ext$.reportremark',
            title: 'T_EMC_REPORT.REMARK',
            type: 'richText',
        }, {
            field: 'ext$.projusername',
            title: 'T_EMC_REPORT.PROJUSERID'
        }, {
            field: 'ext$.testorgname',
            title: 'T_EMC_REPORT.TESTORGNAME'
        }, {
            field: 'ext$.entrustorgname',
            title: 'T_EMC_REPORT.ENTRUSTORGNAME'
        }, {
            field: 'ext$.bizcategoryname',
            title: 'T_EMC_REPORT.BIZCATEGORY'
        },];
    },

    getFolderEditListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'folderNo',
            title: 'T_EMC_FOLDER.FOLDERNO'
        }, {
            field: 'monitorPointName',
            title: 'T_EMC_FOLDER.MONITORPOINTNAME'
        }, {
            field: 'planSampleDate',
            title: 'T_EMC_FOLDER.PLANSAMPLEDATE'
        }, {
            field: 'sampleOrgName',
            title: 'T_EMC_FOLDER.SAMPLEORGNAME'
        }, {
            field: 'sceneUserName',
            title: 'T_EMC_FOLDER.SCENEUSERNAME'
        }, {
            field: 'sampleUserName',
            title: 'T_EMC_FOLDER.SAMPLEUSERNAME'
        }, {
            field: 'longitude',
            title: 'T_EMC_FOLDER.LONGITUDE'
        }, {
            field: 'latitude',
            title: 'T_EMC_FOLDER.LATITUDE'
        }, {
            field: 'remark',
            title: 'T_EMC_FOLDER.REMARK'
        }];
    },

    getOrderEditListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'labelNo',
            title: 'T_EMC_ORDER.LABELNO'
        }, {
            field: 'orderNo',
            title: 'T_EMC_ORDER.ORDERNO'
        }, {
            field: 'clientOrderName',
            title: 'T_EMC_ORDER.CLIENTORDERNAME'
        }, {
            field: 'orderDesc',
            title: 'T_EMC_ORDER.ORDERDESC'
        }, {
            field: 'sendDate',
            title: 'T_EMC_ORDER.SENDDATE'
        }, {
            field: 'sendTime',
            title: 'T_EMC_ORDER.SENDTIME'
        }, {
            field: 'remark',
            title: 'T_EMC_ORDER.REMARK'
        }];
    },

    geteditRemarkInfoFormFields: function () {
        return [{
            field: 'selectRemark',
            title: 'T_EMC_REPORT.SELECT_REMARK',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'reportRemark',
            },
            onChange: function (field, value, rowIndex, oldValue, text) {
                var form = Gikam.getComp('emc-edit-remark-form');
                var remark = Gikam.isEmpty(form.getData().remark) ? '' : form.getData().remark;
                form.setData({
                    remark: text + '\n' + remark
                });

            }
        }, {
            field: 'remark',
            title: 'T_EMC_REPORT.CUSTOM_REMARK',
            height: 160,
            type: 'textarea'
        }];
    },

    getUpdateTaskIndexListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.folderno',
            title: 'T_EMC_ORDER_TASK.FOLDERNO'
        }, {
            field: 'ext$.orderno',
            title: 'T_EMC_ORDER_TASK.ORDERNO'
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_TASK.TESTMETHOD'
        }, {
            field: 'indexId',
            title: 'T_EMC_ORDER_TASK.INDEXID'
        }, {
            field: 'ext$.indexname',
            title: 'T_EMC_ORDER_TASK.INDEXNAME',
            type: 'choose',
            category: 'indexCategory',
            single: true,
            onAfterChoose: function (rows, index) {
                if (rows) {
                    this.setData({
                        index: index,
                        indexId: rows[0].id,
                        indexCategoryId: rows[0].indexCategoryId,
                        ext$: {
                            indexname: rows[0].indexName,
                            index1: rows[0].index1,
                            index2: rows[0].index2,
                            index3: rows[0].index3,
                            index4: rows[0].index4,
                            index5: rows[0].index5,
                            index6: rows[0].index6
                        }
                    })
                }
            },
            onClean: function () {
                this.setData({
                    id: this.getActivedRow().id,
                    indexId: '',
                    indexCategoryId: '',
                    ext$: {
                        indexname: '',
                        index1: '',
                        index2: '',
                        index3: '',
                        index4: '',
                        index5: '',
                        index6: ''
                    }
                })
            },
            editor: true
        }, {
            field: 'ext$.index1',
            title: 'T_EMC_ORDER_INDEX.INDEX1'
        }, {
            field: 'ext$.index2',
            title: 'T_EMC_ORDER_INDEX.INDEX2'
        }, {
            field: 'ext$.index3',
            title: 'T_EMC_ORDER_INDEX.INDEX3'
        }, {
            field: 'ext$.index4',
            title: 'T_EMC_ORDER_INDEX.INDEX4'
        }, {
            field: 'ext$.index5',
            title: 'T_EMC_ORDER_INDEX.INDEX5'
        }, {
            field: 'ext$.index6',
            title: 'T_EMC_ORDER_INDEX.INDEX6'
        }];
    },

    getAnalysisOrgUndoListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'analysisType',
            title: 'T_EMC_REPORT.ANALYSISTYPE',
            width: 100,
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'analysisCategory',
            }
        }, {
            field: 'analysisOrgName',
            width: 100,
            title: 'T_EMC_REPORT.ANALYSISORGNAME'
        }];
    },

    getSampleEditListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'sampleNo',
            title: 'T_EMC_SAMPLE.SAMPLENO'
        }, {
            field: 'projNo',
            title: 'T_EMC_SAMPLE.PROJNO'
        }, {
            field: 'orderCategory',
            title: 'T_EMC_SAMPLE.ORDERCATEGORY'
        }, {
            field: 'createdByName',
            title: 'T_EMC_SAMPLE.CREATEDBYNAME'
        }, {
            field: 'dataInput',
            title: 'T_EMC_SAMPLE.DATAINPUT',
            formatter: function (i, v, row) {
                return '<a href="javascript:;" onclick="emcReport.editPage.loadDataInputPage(\'' + row.id + '\')">数据录入</a>';
            }
        }, {
            field: 'remark',
            title: 'T_EMC_SAMPLE.REMARK'
        }];
    },

    getSampleEditListColumns1: function () {
        return [{
            checkbox: true
        }, {
            field: 'sampleNo',
            title: 'T_EMC_SAMPLE.SAMPLENO'
        }, {
            field: 'projNo',
            title: 'T_EMC_SAMPLE.PROJNO'
        }, {
            field: 'orderCategory',
            title: 'T_EMC_SAMPLE.ORDERCATEGORY'
        }, {
            field: 'createdByName',
            title: 'T_EMC_SAMPLE.CREATEDBYNAME'
        }, {
            field: 'dataInput',
            title: 'T_EMC_SAMPLE.DATAINPUT',
            formatter: function (i, v, row) {
                return '<a href="javascript:;" onclick="emcReport.updatePage.loadDataInputPage(\'' + row.id + '\')">数据录入</a>';
            }
        }, {
            field: 'remark',
            title: 'T_EMC_SAMPLE.REMARK'
        }];
    },

    getSampleOrderListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'orderNo',
            title: 'T_EMC_ORDER.ORDERNO'
        }, {
            field: 'sampleBeginDate',
            title: 'T_EMC_ORDER.SAMPLEBEGINDATE'
        }, {
            field: 'sampleBeginTime',
            title: 'T_EMC_ORDER.SAMPLEBEGINTIME'
        }, {
            field: 'orderDesc',
            title: 'T_EMC_ORDER.ORDERDESC'
        }, {
            field: 'sampleCompleteDate',
            title: 'T_EMC_ORDER.SAMPLECOMPLETEDATE'
        }, {
            field: 'sampleCompleteTime',
            title: 'T_EMC_ORDER.SAMPLECOMPLETETIME'
        }, {
            field: 'orderCategory',
            title: 'T_EMC_ORDER.ORDERCATEGORY',
        }, {
            field: 'ext$.monitorpointname',
            title: 'T_EMC_FOLDER.MONITORPOINTNAME'
        }, {
            field: 'ext$.monitorpointno',
            title: 'T_EMC_FOLDER.MONITORPOINTNO'
        }, {
            field: 'remark',
            title: 'T_EMC_ORDER.REMARK'
        }, {
            field: 'qcName',
            title: 'T_EMC_ORDER.QCCATEGORYNO'
        }, {
            field: 'labelNo',
            title: 'T_EMC_ORDER.LABELNO'
        }, {
            field: 'cycle',
            title: 'T_EMC_ORDER.CYCLE',
            width: 75
        }, {
            field: 'frequency',
            title: 'T_EMC_ORDER.FREQUENCY',
            width: 75
        }, {
            field: 'sampleNum',
            title: 'T_EMC_ORDER.SAMPLENUM'
        }];
    },

    getSampleTestListColumns: function () {
        return [{
            checkbox: true,
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field: 'containerCategoryName',
            title: 'T_EMC_ORDER_TASK.CONTAINERCATEGORYNAME',
        }, {
            field: 'containerGroup',
            title: 'T_EMC_ORDER_TASK.CONTAINERGROUP'
        }, {
            field: 'fixative',
            title: 'T_EMC_ORDER_TASK.FIXATIVE'
        }];
    },

    getRunEditListColumns: function () {
        return [{
            checkbox: true,
        }, {
            field: 'id',
            title: 'T_EMC_RUN.RUNID'
        }, {
            field: 'orderCategory',
            title: 'T_EMC_RUN.ORDERCATEGORY',
        }, {
            field: 'testName',
            title: 'T_EMC_RUN.TESTNAME',
        }, {
            field: 'analysisUserName',
            title: 'T_EMC_RUN.ANALYSISUSERNAME'
        }, {
            field: 'partakeUserName',
            title: 'T_EMC_RUN.PARTAKEUSERNAME'
        }, {
            field: 'checkUserName',
            title: 'T_EMC_RUN.CHECKUSERNAME'
        }, {
            field: 'ext$.equiptcode',
            title: 'T_EMC_RUN.EQUIPTID',
        }, {
            field: 'analysisBeginDate',
            title: 'T_EMC_RUN.ANALYSISBEGINDATE'
        }, {
            field: 'analysisBeginTime',
            title: 'T_EMC_RUN.ANALYSISBEGINTIME'
        }, {
            field: 'analysisEndDate',
            title: 'T_EMC_RUN.ANALYSISENDDATE'
        }, {
            field: 'analysisEndTime',
            title: 'T_EMC_RUN.ANALYSISENDTIME'
        }, {
            field: 'ext$.assistequiptname',
            title: 'T_EMC_RUN.ASSISTEQUIPTID'
        }, {
            field: 'temperature',
            title: 'T_EMC_RUN.TEMPERATURE',
            width: 80
        }, {
            field: 'humidity',
            title: 'T_EMC_RUN.HUMIDITY',
            width: 80
        }, {
            field: 'receiveDate',
            title: 'T_EMC_RUN.RECEIVEDATE'
        }, {
            field: 'receiveTime',
            title: 'T_EMC_RUN.RECEIVETIME'
        }, {
            field: 'handleDate',
            title: 'T_EMC_RUN.HANDLEDATE'
        }, {
            field: 'handleMethod',
            title: 'T_EMC_RUN.HANDLEMETHOD',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'handleMethod'
            }
        }, {
            field: 'beforeHandle',
            title: 'T_EMC_RUN.BEFOREHANDLE'
        }, {
            field: 'methodParam',
            title: 'T_EMC_RUN.METHODPARAM'
        }];
    },

    getRunOrderTaskListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'cupNo',
            title: 'T_EMC_ORDER_TASK.CUPNO'
        }, {
            field: 'ext$.orderno',
            title: 'T_EMC_ORDER_TASK.ORDERID',
        }, {
            field: 'ext$.labelno',
            title: 'T_EMC_ORDER_TASK.LABELNO',
        }, {
            field: 'ext$.monitorpointname',
            title: 'T_EMC_ORDER_TASK.MONITORPOINTNAME',
        }, {
            field: 'qcName',
            title: 'T_EMC_ORDER_TASK.QCNAME',
        }, {
            field: 'qcCategoryCode',
            title: 'T_EMC_ORDER_TASK.QCCATEGORYCODE',
        }, {
            field: 'ext$.orderdesc',
            title: 'T_EMC_ORDER_TASK.ORDERDESC',
        }, {
            field: 'ext$.samplebegindate',
            title: 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
            formatter: function (i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, {
            field: 'ext$.samplebegintime',
            title: 'T_EMC_ORDER_TASK.SAMPLEBEGINTIME'
        }, {
            field: 'equiptAcquNo',
            title: 'T_EMC_ORDER_TASK.EQUIPTACQUNO'
        }, {
            field: 'ext$.parentorderno',
            title: 'T_EMC_ORDER_TASK.PARENTORDERNO',
        }, {
            field: 'stockBatchNo',
            title: 'T_EMC_ORDER_TASK.STOCKBATCHNO',
        }];
    },

    getRunOrderTaskResultListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.testname',
            title: 'T_EMC_ORDER_TASK_RESULT.TESTNAME'
        }, {
            field: 'ext$.methodname',
            title: 'T_EMC_ORDER_TASK_RESULT.METHODNAME'
        }, {
            field: 'ext$.testmethodbasis',
            title: 'T_EMC_ORDER_TASK_RESULT.TESTMETHODBASIS'
        }, {
            field: 'repeatNo',
            title: 'T_EMC_ORDER_TASK_RESULT.REPEATNO'
        }, {
            field: 'itemName',
            title: 'T_EMC_ORDER_TASK_RESULT.ITEMNAME'
        }, {
            field: 'originalResult',
            styleFormatter: function (row) {
                if (row.scriptFlag != '1') {
                    return {
                        backgroundColor: 'palegreen'
                    }
                }
            },
            title: 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT'
        }, {
            field: 'reportResult',
            title: 'T_EMC_ORDER_TASK_RESULT.REPORTRESULT',
            formatter: function (i, v, r) {
                return Gikam.scientificNotation(v);
            }
        }, {
            field: 'highLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.HIGHLIMIT'
        }, {
            field: 'revisionRule',
            title: 'T_EMC_ORDER_TASK_RESULT.REVISIONRULE'
        }, {
            field: 'unitName',
            title: 'T_EMC_ORDER_TASK_RESULT.UNITNAME'
        }, {
            field: 'lowLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT'
        }, {
            field: 'indexLowLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.INDEXLOWLIMIT'
        }, {
            field: 'indexHighLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.INDEXHIGHLIMIT'
        }, {
            field: 'resultStatus',
            title: 'T_EMC_ORDER_TASK_RESULT.RESULTSTATUS'
        }, {
            field: 'relativeDeviation',
            title: 'T_EMC_ORDER_TASK_RESULT.RELATIVEDEVIATION'
        }, {
            field: 'avgResult',
            title: 'T_EMC_ORDER_TASK_RESULT.AVERAGE'
        }];
    },

    getAuditRecordListColumns: function () {
        return [{
            field: 'logDesc',
            title: 'T_EMC_AUDIT_RECORD.LOGDESC'
        }, {
            field: 'taskName',
            title: 'T_EMC_AUDIT_RECORD.TASKNAME'
        }, {
            field: 'nextTaskName',
            title: 'T_EMC_AUDIT_RECORD.NEXTTASKNAME'
        }, {
            field: 'operateCategory',
            title: 'T_EMC_AUDIT_RECORD.OPERATECATEGORY'
        }, {
            field: 'comments',
            title: 'T_EMC_AUDIT_RECORD.COMMENT'
        }, {
            field: 'createdByName',
            title: 'T_EMC_AUDIT_RECORD.CREATEDBYNAME'
        }, {
            field: 'createdTime',
            title: 'T_EMC_AUDIT_RECORD.CREATEDTIME'
        }, {
            field: 'auditCategory',
            title: 'T_EMC_AUDIT_RECORD.AUDITCATEGORY'
        }];
    },

    /** 归档文件 */
    getArchiveFileListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'categoryName',
            title: 'T_EMC_ARCHIVE_CATEGORY.CATEGORYNAME',
            width: 300
        }];
    },

    // 详细信息 - 原始数据列表
    getDetailOriginalListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'cycle',
            title: 'T_EMC_ORDER_TASK_RESULT.CYCLE',
            width: 60
        }, {
            field: 'frequency',
            title: 'T_EMC_ORDER_TASK_RESULT.FREQUENCY',
            width: 60
        }, {
            field: 'orderNo',
            title: 'T_EMC_ORDER_TASK.ORDERID',
        }, {
            field: 'qcCategoryCode',
            title: 'T_EMC_ORDER_TASK.QCCATEGORYCODE',
        }, {
            field: 'ext$.testname',
            title: 'T_EMC_ORDER_TASK_RESULT.TESTNAME'
        }, {
            field: 'itemAliasName',
            title: 'T_EMC_ORDER_TASK_RESULT.ITEMALIASNAME'
        }, {
            field: 'itemName',
            title: 'T_EMC_ORDER_TASK_RESULT.ITEMNAME'
        }, {
            field: 'reportResult',
            title: 'T_EMC_REPORT_RESULT.ANALYSISVALUE',
            width: 100,
            formatter: function (i, v, r) {
                return Gikam.scientificNotation(v);
            }
        }, {
            field: 'unitName',
            title: 'T_EMC_ORDER_TASK_RESULT.UNITNAME',
            width: 120
        }, {
            field: 'revisionRule',
            title: 'T_EMC_ORDER_TASK_RESULT.REVISIONRULE'
        }, {
            field: 'lowLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT'
        }, {
            field: 'highLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.HIGHLIMIT'
        }, {
            field: 'indexLowLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.INDEXLOWLIMIT',
            editor: true
        }, {
            field: 'indexHighLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.INDEXHIGHLIMIT',
            editor: true
        }, {
            field: 'resultStatus',
            title: 'T_EMC_ORDER_TASK_RESULT.RESULTSTATUS',
        }, {
            field: 'reportDisplay',
            title: 'T_EMC_REPORT_RESULT.REPORTDISPLAY',
            editor: true,
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            }
        }, {
            field: 'ext$.indexaddflag',
            title: '是否加指标',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            }
        }, {
            field: 'ext$.analysisusername',
            title: 'T_EMC_REPORT_RESULT.CREATEDBYNAME',
        }, {
            field: 'ext$.analysisbegindate',
            title: 'T_EMC_REPORT_RESULT.CREATEDTIME',
            formatter: function (i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }];
    },

    // 详细信息 - 报告数据列表
    getDetailReportListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'cycle',
            title: 'T_EMC_ORDER_TASK_RESULT.CYCLE',
            width: 60
        }, {
            field: 'frequency',
            title: 'T_EMC_ORDER_TASK_RESULT.FREQUENCY',
            width: 60
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_TASK_RESULT.TESTNAME'
        }, {
            field: 'sampleCompleteDate',
            title: 'T_EMC_REPORT_RESULT.SAMPLEDATE',
            width: 120
        }, {
            field: 'sampleCompleteTime',
            title: 'T_EMC_REPORT_RESULT.SAMPLETIME',
            width: 120
        }, {
            field: 'dataType',
            title: 'T_EMC_REPORT_RESULT.DATATYPE',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'pnFunc'
            },
        }, {
            field: 'itemAliasName',
            title: 'T_EMC_ORDER_TASK_RESULT.ITEMALIASNAME'
        }, {
            field: 'itemName',
            title: 'T_EMC_ORDER_TASK_RESULT.ITEMNAME'
        }, {
            field: 'reportResult',
            title: 'T_EMC_REPORT_RESULT.ANALYSISVALUE',
            formatter: function (i, v, r) {
                return Gikam.scientificNotation(v);
            }
        }, {
            field: 'unitName',
            title: 'T_EMC_ORDER_TASK_RESULT.UNITNAME',
            editor: true,
            type: 'choose',
            category: 'unit',
            single: true,
            onAfterChoose: function (rows) {
                if (rows) {
                    this.setData({
                        unitName: rows[0].unitName
                    })
                }
            }
        }, {
            field: 'lowLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT',
            editor: true
        }, {
            field: 'highLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.HIGHLIMIT',
            editor: true
        }, {
            field: 'indexLowLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.INDEXLOWLIMIT',
            editor: true
        }, {
            field: 'indexHighLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.INDEXHIGHLIMIT',
            editor: true
        }, {
            field: 'reportDisplay',
            title: 'T_EMC_REPORT_RESULT.REPORTDISPLAY',
            editor: true,
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            }
        }];
    },

    // 点位信息-按测试更换指标-检测样品测试表
    getUpdateIndexOrderTaskColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.monitorpointname',
            title: 'T_EMC_ORDER_TASK.MONITORPOINTNAME'
        }, {
            field: 'ext$.samplebegindate',
            title: 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE'
        }, {
            field: 'ext$.samplebegintime',
            title: 'T_EMC_ORDER_TASK.SAMPLEBEGINTIME'
        }, {
            field: 'ext$.orderno',
            title: 'T_EMC_ORDER_TASK.ORDERNO'
        }, {
            field: 'analysisType',
            title: 'T_EMC_REPORT.ANALYSISTYPE',
            width: 100,
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'analysisCategory',
            }
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field: 'indexId',
            title: 'T_EMC_ORDER_TASK.INDEXID'
        }, {
            field: 'indexCategoryId',
            title: 'T_EMC_ORDER_TASK.INDEXCATEGORYID'
        }];
    },

    // 点位信息-按测试更换指标-检测分析项表
    getUpdateIndexOrderTaskResultColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'itemAliasName',
            title: 'T_EMC_ORDER_TASK_RESULT.ITEMALIASNAME'
        }, {
            field: 'unitName',
            title: 'T_EMC_ORDER_TASK_RESULT.UNITNAME'
        }, {
            field: 'indexLowLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.INDEXLOWLIMIT',
            type: 'number'
        }, {
            field: 'indexHighLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.INDEXHIGHLIMIT',
            type: 'number'
        }, {
            field: 'orderNo',
            title: 'T_EMC_ORDER_TASK_RESULT.ORDERNO'
        }, {
            field: 'ext$.testname',
            title: 'T_EMC_ORDER_TASK_RESULT.TESTNAME'
        }, {
            field: 'ext$.indexid',
            title: 'T_EMC_ORDER_TASK_RESULT.INDEXID'
        }];
    },

    // 点位信息-按测试更换指标-指标表
    getUpdateIndexOrderIndexColumns: function () {
        return [{
            field: 'id',
            title: 'T_EMC_ORDER_INDEX.ID'
        }, {
            field: 'indexName',
            title: 'T_EMC_ORDER_INDEX.INDEXNAME'
        }, {
            field: 'index1',
            title: 'T_EMC_ORDER_INDEX.INDEX1'
        }, {
            field: 'index2',
            title: 'T_EMC_ORDER_INDEX.INDEX2'
        }, {
            field: 'index3',
            title: 'T_EMC_ORDER_INDEX.INDEX3'
        }, {
            field: 'index4',
            title: 'T_EMC_ORDER_INDEX.INDEX4'
        }, {
            field: 'index5',
            title: 'T_EMC_ORDER_INDEX.INDEX5'
        }, {
            field: 'index6',
            title: 'T_EMC_ORDER_INDEX.INDEX6'
        }, {
            field: 'reportDisplayName',
            title: 'T_EMC_ORDER_INDEX.REPORTDISPLAYNAME'
        }];
    },

    // 点位信息-按测试更换指标-指标分析项表
    getUpdateIndexIndexSetFields: function () {
        return [{
            field: 'evalIndex',
            title: 'T_EMC_INDEX_SET.EVALINDEX',
            preInsert: true,
            validators: ['strLength[0,10]'],
        }, {
            field: 'ext$.indexitemname',
            title: 'T_EMC_INDEX_SET.INDEXITEMID',
            type: 'choose',
            category: 'indexItem',
            editor: true,
            onAfterChoose: function (rows) {
                if (rows) {
                    this.setData({
                        indexItemId: rows[0].id,
                        ext$: {
                            indexitemname: rows[0].indexItemName,
                        }
                    });
                }
            },
            onClean: function () {
                this.setData({
                    indexItemId: '',
                    ext$: {
                        indexitemname: '',
                    }
                });
            },
        },];
    },

    // 点位信息-按测试更换指标-指标分析项表
    getUpdateIndexIndexSetColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'evalIndex',
            title: 'T_EMC_INDEX_SET.EVALINDEX',
            validators: ['strLength[0,10]']
        }, {
            field: 'ext$.indexitemname',
            title: 'T_EMC_INDEX_SET.INDEXITEMID',
            type: 'choose',
            category: 'indexItem',
            single: true,
            onAfterChoose: function (rows, index) {
                if (rows) {
                    this.setData({
                        index: index,
                        indexItemId: rows[0].id,
                        ext$: {
                            indexitemname: rows[0].indexItemName,
                        }
                    });
                }
            },
            onClean: function () {
                this.setData({
                    id: this.getActivedRow().id,
                    indexItemId: '',
                    ext$: {
                        indexitemname: '',
                    }
                });
            },
        }, {
            field: 'indexLowLimit',
            title: 'T_EMC_INDEX_SET.INDEXLOWLIMIT',
            type: 'number'
        }, {
            field: 'indexHighLimit',
            title: 'T_EMC_INDEX_SET.INDEXHIGHLIMIT',
            type: 'number'
        },];
    },

    getReportFileListColumns: function (a) {
        return [{
            checkbox: true
        }, {
            index: true
        }, {
            field: 'name',
            title: 'T_CORE_FILE.NAME',
            width: 300,
            formatter: function (i, v, r) {
                // 如果是word文档则打开编辑否则为下载文件
                var typeArray = [
                    'doc', 'docm', 'docx', 'dot', 'dotm', 'dotx',
                    'epub', 'fodt', 'htm', 'html', 'mht', 'odt',
                    'ott', 'pdf', 'rtf', 'txt', 'djvu', 'xps',
                    'csv', 'fods', 'ods', 'ots', 'xls', 'xlsm',
                    'xlsx', 'xlt', 'xltm', 'xltx', 'fodp', 'odp',
                    'otp', 'pot', 'potm', 'potx', 'pps', 'ppsm',
                    'ppsx', 'ppt', 'pptm', 'pptx'
                ];
                if (typeArray.includes(r.fileExt)) {
                    var readonly = Gikam.isEmpty(a) ? false : true;
                    return `<a href="javascript:;" onclick="Gikam.openFile('${r.id}',${readonly})">${r.name}</a>`;
                } else {
                    return `<a href="javascript:;" onclick="Gikam.printFile('${r.id}', true, null, false, true)">${r.name}</a>`;
                }
            }
        }, {
            field: 'bizCategory',
            title: 'T_CORE_FILE.FILECATEGORY',
            type: 'select',
            search: true,
            editor: true,
            category: 'sys',
            param: {
                codeCategoryId: 'fileBizCategory',
            }
        }, {
            field: 'createdByName',
            title: 'T_DINGAN_FILE.VIEW_UPLOADER_USERNAME',
        }, {
            field: 'createdTime',
            title: 'T_DINGAN_FILE.VIEW_UPLOADER_TIME',
        }, {
            width: 100,
            title: '操作',
            formatter: function (i, v, r) {
                let str = '<div style= "display: flex; justify-content: space-between ">';
                str += `<a href="javascript:;" onclick="Gikam.emcDownloadFile('${Gikam.IFM_CONTEXT}${r.downloadUrl}','${r.name}')" key = "download" style="flex: 1;text-align: center;">下载</a>`;
                str += '</div>';
                return str;
            }
        }];
    },

    getFinalReportFileListColumns: function () {
        return [{
            checkbox: true
        }, {
            index: true
        }, {
            field: 'name',
            title: 'T_CORE_FILE.NAME',
            width: 300,
            formatter: function (i, v, r) {
                // 如果是word文档则打开编辑否则为下载文件
                var typeArray = [
                    'doc', 'docm', 'docx', 'dot', 'dotm', 'dotx',
                    'epub', 'fodt', 'htm', 'html', 'mht', 'odt',
                    'ott', 'pdf', 'rtf', 'txt', 'djvu', 'xps',
                    'csv', 'fods', 'ods', 'ots', 'xls', 'xlsm',
                    'xlsx', 'xlt', 'xltm', 'xltx', 'fodp', 'odp',
                    'otp', 'pot', 'potm', 'potx', 'pps', 'ppsm',
                    'ppsx', 'ppt', 'pptm', 'pptx'
                ];
                if (typeArray.includes(r.fileExt)) {
                    return `<a href="javascript:;" onclick="Gikam.openFile('${r.id}', true)">${r.name}</a>`;
                } else {
                    return `<a href="javascript:;" onclick="Gikam.printFile('${r.id}', true, null, false, true)">${r.name}</a>`;
                }
            }
        }, {
            field: 'bizCategory',
            title: 'T_CORE_FILE.FILECATEGORY',
            type: 'select',
            search: true,
            editor: true,
            category: 'sys',
            param: {
                codeCategoryId: 'fileBizCategory',
            }
        }, {
            field: 'createdByName',
            title: 'T_DINGAN_FILE.VIEW_UPLOADER_USERNAME',
        }, {
            field: 'createdTime',
            title: 'T_DINGAN_FILE.VIEW_UPLOADER_TIME',
        }, {
            width: 100,
            title: '操作',
            formatter: function (i, v, r) {
                let str = '<div style= "display: flex; justify-content: space-between ">';
                str += `<a href="javascript:;" onclick="Gikam.emcDownloadFile('${Gikam.IFM_CONTEXT}${r.downloadUrl}','${r.name}')" key = "download" style="flex: 1;text-align: center;">下载</a>`;
                str += '</div>';
                return str;
            }
        }];
    },

    getReportQueryFormFields: function () {
        return [{
            field: 'bizcategoryId',
            title: 'T_EMC_REPORT.BIZCATEGORY',
            type: 'select',
            category: 'bizCategory',
        }, {
            field: 'projName',
            title: '任务名称'
        }, {
            field: 'monitorTmplId',
            title: 'T_EMC_REPORT.ORDERCATEGORY',
            type: 'select',
            category: 'monitorTmpl',
        }, {
            field: 'testOrgName',
            title: 'T_EMC_REPORT.TESTORGNAME'
        }, {
            field: 'entrustOrgName',
            title: 'T_EMC_REPORT.ENTRUSTORGNAME'
        },];
    },

    getReportQueryListColumns: function () {
        return [{
            field: 'ext$.projno',
            title: '任务编号'
        }, {
            field: 'ext$.projname',
            title: '任务名称'
        }, {
            field: 'orderCategory',
            title: 'T_EMC_REPORT.ORDERCATEGORY'
        }, {
            field: 'reportNo',
            title: '报告编号',
            editor: true
        }, {
            field: 'ext$.pageno',
            title: '页码'
        }, {
            field: 'ext$.bizcategoryname',
            title: 'T_EMC_REPORT.BIZCATEGORY'
        },];
    },

    getFolderQueryListColumns: function () {
        return [{
            field: 'folderNo',
            title: 'T_EMC_FOLDER.FOLDERNO'
        }, {
            field: 'monitorPointName',
            title: 'T_EMC_FOLDER.MONITORPOINTNAME'
        }, {
            field: 'monitorPointNo',
            title: 'T_EMC_FOLDER.MONITORPOINTNO'
        }, {
            field: 'monitorPointCategory',
            title: 'T_EMC_FOLDER.MONITORPOINTCATEGORY'
        }];
    },

    getReportOrderTaskShuttleFrameColumns: function () {
        return [{
            field: 'cupNo',
            title: 'T_EMC_ORDER_TASK.CUPNO'
        }, {
            field: 'ext$.orderno',
            title: 'T_EMC_ORDER_TASK.ORDERID',
        }, {
            field: 'ext$.labelno',
            title: 'T_EMC_ORDER_TASK.LABELNO',
        }, {
            field: 'ext$.monitorpointname',
            title: 'T_EMC_ORDER_TASK.MONITORPOINTNAME',
        }, {
            field: 'ext$.orderdesc',
            title: 'T_EMC_ORDER_TASK.ORDERDESC',
        }, {
            field: 'ext$.samplebegindate',
            title: 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
            formatter: function (i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, {
            field: 'ext$.samplebegintime',
            title: 'T_EMC_ORDER_TASK.SAMPLEBEGINTIME'
        }, {
            field: 'ext$.parentorderno',
            title: 'T_EMC_ORDER_TASK.PARENTORDERNO',
        }, {
            field: 'stockBatchNo',
            title: 'T_EMC_ORDER_TASK.STOCKBATCHNO',
        }];
    },

    getFolderOrderTaskColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field: 'methodName',
            title: 'T_EMC_ORDER_TASK_RESULT.METHODNAME'
        }, {
            field: 'testMethodBasis',
            title: 'T_EMC_ORDER_TASK_RESULT.TESTMETHODBASIS',
        }]
    },

    getReportOrderTaskColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.folderno',
            title: 'T_EMC_FOLDER.FOLDERNO'
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field: 'methodName',
            title: 'T_EMC_ORDER_TASK_RESULT.METHODNAME'
        }, {
            field: 'testMethodBasis',
            title: 'T_EMC_ORDER_TASK_RESULT.TESTMETHODBASIS',
        }]
    },

    getOrgEditListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'orgName',
            title: 'T_CORE_ORG.ORGNAME',
        },]
    },

    getElectronReportFormFields: function () {
        return [{
            field: 'sendToSaler',
            title: 'T_EMC_REPORT_SEND_INFO.SENDTOSALER',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            },
        }, {
            field: 'sendToQuoter',
            title: 'T_EMC_REPORT_SEND_INFO.SENDTOQUOTER',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            },
        }, {
            field: 'sendEmail',
            title: 'T_EMC_REPORT_SEND_INFO.SENDEMAIL',
        }]
    },

    getPaperReportFormFields: function () {
        return [{
            field: 'ext$.entrustorgcontact',
            title: '联系人',
        }, {
            field: 'ext$.entrustorgphone',
            title: '联系电话',
        }, {
            field: 'ext$.entrustorgaddress',
            title: '单位地址',
            validators: ['notEmpty',],
            colspan: 2
        }, {
            field: 'expressType',
            title: 'T_EMC_REPORT_SEND_INFO.EXPRESSTYPE',
            validators: ['notEmpty'],
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'expressType',
            },
        }, {
            field: 'expressNo',
            title: 'T_EMC_REPORT_SEND_INFO.EXPRESSNO',
            validators: ['notEmpty']
        }, {
            field: 'remark',
            title: 'T_EMC_REPORT_SEND_INFO.REMARK',
        }, {
            title: 'T_EMC_EMP.PIC',
            field: 'ext$.pic',
            type: 'image',
            height: 60,
            onClick: function () {
                debugger;
                var fromdata = Gikam.getComp('emc-report-send-info-paper-form').getData();
                var bizId = fromdata.projId;
                Gikam.create('simpleUploader', {
                    id: 'emc-emp-detail-electronic-signature-uploader',
                    dbTable: 'T_EMC_REPORT',
                    bizId: bizId,
                    accept: 'image',
                    multiple: false,
                    onAfterClose: function (fileList) {
                        if (fileList.length > 0) {

                            Gikam.getJson(Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}', {
                                id: fileList[0].id
                            })).done(function (r) {
                                debugger
                                Gikam.getComp('emc-report-send-info-paper-form').setData({
                                    pic: IFM_CONTEXT + r.downloadUrl,
                                    username: fromdata.ext$.entrustorgcontact,
                                    phone: fromdata.ext$.entrustorgphone,
                                    address: fromdata.ext$.entrustorgaddress
                                })
                            });
                        }
                    }
                });


            }
        },]
    },

    getBatchInputFields: function () {
        return [{
            field: 'ext$.reportreviewername',
            title: 'T_EMC_REPORT.REPORTREVIEWERNAME',
            editor: true,
            category: 'emp',
            type: 'choose',
            single: true,
            onBeforeChoose: function () {
                var activedRow = Gikam.getComp('emc-report-edit-list-grid').getActivedRow();
                return {
                    departmentid_SEQ: activedRow.createdByOrgId
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        ext$: {
                            reportreviewername: rows[0].userName,
                            reportreviewerid: rows[0].id
                        }
                    });
                }
            },
            onClean: function () {
                this.setData({
                    ext$: {
                        reportreviewername: '',
                        reportreviewerid: ''
                    }
                });
            }
        }, {
            field: 'ext$.reportissueusername',
            title: 'T_EMC_REPORT.REPORTISSUEUSERNAME',
            editor: true,
            category: 'emp',
            type: 'choose',
            single: true,
            onBeforeChoose: function () {
                var activedRow = Gikam.getComp('emc-report-edit-list-grid').getActivedRow();
                return {
                    departmentid_SEQ: activedRow.createdByOrgId
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        ext$: {
                            reportissueusername: rows[0].userName,
                            reportissueuserid: rows[0].id
                        }
                    });
                }
            },
            onClean: function () {
                this.setData({
                    ext$: {
                        reportissueusername: '',
                        reportissueuserid: ''
                    }
                });
            }
        }, {
            field: 'ext$.reportname',
            title: 'T_EMC_REPORT.REPORTCONFIGNAME',
            type: 'choose',
            category: 'report-config',
            single: true,
            onBeforeChoose: function (index) {
                return {
                    reportConfigId_SEQ: 'wordReport',
                };
            },
            onAfterChoose: function (rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        reportConfigLineId: rows[0].id,
                        ext$: {
                            reportname: rows[0].reportName
                        }
                    });
                }
            },
            onClean: function () {
                this.setData({
                    reportConfigLineId: '',
                    ext$: {
                        reportname: ''
                    }
                });
            },
            editor: true
        }, {
            field: 'ext$.qcreportname',
            title: 'T_EMC_REPORT.QCREPORTLINEID',
            type: 'choose',
            category: 'report-config',
            single: true,
            onBeforeChoose: function (index) {
                return {
                    reportConfigId_SEQ: 'wordReport',
                };
            },
            onAfterChoose: function (rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        ext$: {
                            qcreportlineid: rows[0].id,
                            qcreportname: rows[0].reportName
                        }
                    });
                }
            },
            onClean: function () {
                this.setData({
                    ext$: {
                        qcreportlineid: '',
                        qcreportname: ''
                    }
                });
            },
            editor: true
        }]
    },

    workflow: {
        dbTable: 'T_EMC_REPORT',
        columns: [{
            field: 'id',
            title: 'T_EMC_REPORT.ID'
        }]
    },

    /** 获取工作流节点状态，下拉选择按照实际的节点名称，比如报告编制、报告审核、报告签发等 */
    getBpmnColumn: function (dbTable) {
        return {
            field: 'status',
            title: 'GIKAM.BPMN.FIELD.TITLE.STATUS',
            sort: false,
            type: 'select',
            match: 'SB',
            items: [{
                text: '',
                value: ''
            }, {
                text: Gikam.propI18N('EMC.MODULE.BP.REPORT.REPORTS.BPMN.STATUS.REPORTEDIT'),
                value: 'reportEdit'
            }, {
                text: Gikam.propI18N('EMC.MODULE.BP.REPORT.REPORTS.BPMN.STATUS.REPORTAUDIT'),
                value: 'reportAudit'
            }, {
                text: Gikam.propI18N('EMC.MODULE.BP.REPORT.REPORTS.BPMN.STATUS.REPORTISSUE'),
                value: 'reportIssue'
            }, {
                text: Gikam.propI18N('EMC.MODULE.BP.REPORT.REPORTS.BPMN.STATUS.REPORTPRINT'),
                value: 'reportPrint'
            }, {
                text: Gikam.propI18N('EMC.MODULE.BP.REPORT.REPORTS.BPMN.STATUS.REPORTSEND'),
                value: 'reportSend'
            }, {
                text: Gikam.propI18N('GIKAM.BPMN.STATUS.DONE'),
                value: 'reportArchiving'
            }],
            width: 100,
            formatter: function (index, value, row, text) {
                var taskName = Gikam.getFieldValue(row, 'ext$.taskname_');
                if (taskName) {
                    return '<a href="javascript:;" onclick="Gikam.bpmn.openProcessStatus(\'' + dbTable + '\',\'' + row.id + '\')">' + taskName + '</a>';
                }

                if (row['processStatus'].indexOf('draft') === 0) {
                    if (row['processStatus'] === 'draft_rejected') {
                        return '<a href="javascript:;" style="color:red;" onclick="Gikam.bpmn.openProcessStatus(\'' + dbTable + '\',\'' + row.id + '\')">' + Gikam.propI18N('GIKAM.BPMN.STATUS.RAW')
                            + '</a>';
                    } else if (row['processStatus'] === 'draft_undo') {
                        return '<a href="javascript:;" style="color:#ffba00;" onclick="Gikam.bpmn.openProcessStatus(\'' + dbTable + '\',\'' + row.id + '\')">'
                            + Gikam.propI18N('GIKAM.BPMN.STATUS.RAW') + '</a>';
                    } else {
                        return '<a href="javascript:;" onclick="Gikam.bpmn.openProcessStatus(\'' + dbTable + '\',\'' + row.id + '\')">' + Gikam.propI18N('GIKAM.BPMN.STATUS.RAW') + '</a>';
                    }
                } else if (row['processStatus'].indexOf('approve') === 0 && row['status'].indexOf('reportArchiving') === -1) {
                    if (row['processStatus'] === 'approve_rejected') {
                        return '<a href="javascript:;" style="color:red;" onclick="Gikam.bpmn.openProcessStatus(\'' + dbTable + '\',\'' + row.id + '\')">' + Gikam.propI18N('GIKAM.BPMN.STATUS.PASS')
                            + '</a>';
                    } else {
                        return '<a href="javascript:;" onclick="Gikam.bpmn.openProcessStatus(\'' + dbTable + '\',\'' + row.id + '\')">' + Gikam.propI18N('GIKAM.BPMN.STATUS.PASS') + '</a>';
                    }
                } else if (row['processStatus'].indexOf('done') === 0 || row['status'].indexOf('reportArchiving') === 0) {
                    return '<a href="javascript:;" onclick="Gikam.bpmn.openProcessStatus(\'' + dbTable + '\',\'' + row.id + '\')">' + Gikam.propI18N('GIKAM.BPMN.STATUS.DONE') + '</a>';
                }

                return '';
            }
        }
    },

    getEmissionFolderListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.projno',
            title: '任务编号'
        }, {
            field: 'ext$.projname',
            title: 'T_EMC_FOLDER.PROJNAME'
        }, {
            field: 'monitorPointName',
            title: 'T_EMC_FOLDER.MONITORPOINTNAME'
        }, {
            field: 'folderNo',
            title: 'T_EMC_FOLDER.FOLDERNO',
            width: 120,
        }, {
            field: 'ext$.testorgname',
            title: 'T_EMC_FOLDER.TESTORGNAME'
        }, {
            field: 'orderCategory',
            title: 'T_EMC_FOLDER.ORDERCATEGORY'
        }, {
            field: 'monitorPointNo',
            title: 'T_EMC_FOLDER.MONITORPOINTNO'
        }, {
            field: 'planSampleDate',
            title: 'T_EMC_FOLDER.PLANSAMPLEDATE'
        }, {
            field: 'sampleOrgName',
            title: 'T_EMC_FOLDER.SAMPLEORGNAME'
        }, {
            field: 'sceneUserName',
            title: 'T_EMC_FOLDER.SCENEUSERNAME',
        }, {
            field: 'sampleUserName',
            title: 'T_EMC_FOLDER.SAMPLEUSERNAME',
        }, {
            field: 'monitorPointCategory',
            title: 'T_EMC_FOLDER.MONITORPOINTCATEGORY'
        }, {
            field: 'longitude',
            title: 'T_EMC_FOLDER.LONGITUDE'
        }, {
            field: 'latitude',
            title: 'T_EMC_FOLDER.LATITUDE'
        }, {
            field: 'remark',
            title: 'T_EMC_FOLDER.REMARK'
        },];
    },

    getEmissionOrderListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'orderNo',
            title: 'T_EMC_ORDER.ORDERNO',
            width: 120,
        }, {
            field: 'qcName',
            title: 'T_EMC_ORDER.QCCATEGORYNO',
            width: 120,
        }, {
            field: 'sampleBeginDate',
            title: 'T_EMC_ORDER.SAMPLEBEGINDATE'
        }, {
            field: 'sampleBeginTime',
            title: 'T_EMC_ORDER.SAMPLEBEGINTIME'
        }, {
            field: 'sampleCompleteDate',
            title: 'T_EMC_ORDER.SAMPLECOMPLETEDATE'
        }, {
            field: 'sampleCompleteTime',
            title: 'T_EMC_ORDER.SAMPLECOMPLETETIME'
        }, {
            field: 'parentOrderNo',
            title: 'T_EMC_ORDER.PARENTORDERNO',
        }, {
            field: 'orderDesc',
            title: 'T_EMC_ORDER.ORDERDESC'
        }, {
            field: 'remark',
            title: 'T_EMC_ORDER.REMARK',
        }, {
            field: 'labelNo',
            title: 'T_EMC_ORDER.LABELNO'
        }, {
            field: 'cycle',
            title: 'T_EMC_ORDER.CYCLE',
        }, {
            field: 'frequency',
            title: 'T_EMC_ORDER.FREQUENCY'
        }, {
            field: 'sampleNum',
            title: 'T_EMC_ORDER.SAMPLENUM'
        }];
    },

    getResultPublicColumns: function () {
        return [{
            checkbox: true,
            fixed: true
        }, {
            index: true,
            fixed: true
        }, {
            field: 'ext$.testname',
            title: 'T_EMC_ORDER_TASK_RESULT.TESTNAME',
            sort: false,
            fixed: true
        }, {
            field: 'repeatNo',
            title: 'T_EMC_ORDER_TASK_RESULT.REPEATNO',
            sort: false,
            fixed: true
        }, {
            field: 'itemAliasName',
            title: 'T_EMC_ORDER_TASK_RESULT.ITEMNAME',
            sort: false,
            fixed: true
        }, {
            field: 'originalResult',
            title: 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT',
            // editor : true,
            sort: false,
            textStyleFormatter: function () {
                return {
                    backgroundColor: 'palegreen'
                }
            },
            onBeforeEditorRender: function (row) {
                if (row.scriptFlag == '1') {
                    return false;
                }
                return true;
            },
            onFocus: function (field, value, index) {
                var _this = this;
                var data = _this.getData()[index];
                if ('select' === data.resultCategory) {
                    Gikam.create('modal', {
                        width: 400,
                        height: 300,
                        title: Gikam.choose.getConfig('itemResult').title,
                        url: Gikam.choose.getConfig('itemResult').url + Gikam.param({
                            single: true,
                            itemId_SEQ: data.itemId,
                        }),
                        onAfterClose: function (rows) {
                            if (Gikam.isEmpty(rows)) {
                                return;
                            }
                            Gikam.getComp('emc-run-result-grid').setData({
                                index: index,
                                originalResult: rows[0].result,
                                reportResult: rows[0].result,
                            });
                        }
                    });
                }
            },
            onChange: function (field, value, index) {
                var data = this.getData()[index];
                if ('number' === data.resultCategory) {
                    var forbidden = false;
                    if (Gikam.isNotEmpty(value)) {
                        var regF = /^(-?\d+)(\.\d+)?$/;
                        if (value.indexOf('0') == 0 && value.length > 1 && value.indexOf('0.') == -1) {
                            forbidden = true;
                        }
                        if ((value.indexOf('-0') == 0 && value.length == 2) || (value.indexOf('-0.') == 0 && value.length == 3)) {
                            forbidden = true;
                        }
                        if (!regF.test(value)) {
                            forbidden = true;
                        }

                        if (forbidden) {
                            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.FORMAT_ERROR');
                            this.setData({
                                index: index,
                                originalResult: ''
                            });
                        }

                    }

                }
            }
        }, {
            field: 'reportResult',
            title: 'T_EMC_ORDER_TASK_RESULT.REPORTRESULT',
            sort: false,
            formatter: function (i, v, r) {
                return Gikam.scientificNotation(v);
            }
        }, {
            field: 'convertFactor',
            title: 'T_EMC_ORDER_TASK_RESULT.CONVERTFACTOR',
            sort: false,
            // editor : true
        }, {
            field: 'resultRule',
            title: 'T_EMC_ORDER_TASK_RESULT.RESULTRULE',
            // editor : true,
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'resultRule'
            },
        }, {
            field: 'lowLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT',
            // editor : true,
            validators: ['number'],
            sort: false
        }, {
            field: 'revisionRule',
            title: 'T_EMC_ORDER_TASK_RESULT.REVISIONRULE',
            // editor : true,
            type: 'choose',
            category: 'rounding-rule',
            single: true,
            onAfterChoose: function (back, index) {
                if (back) {
                    this.setData({
                        index: index,
                        'revisionRule': back
                    });
                }
            },
            sort: false
        }, {
            field: 'unitName',
            title: 'T_EMC_ORDER_TASK_RESULT.UNITNAME',
            // editor : true,
            type: 'choose',
            category: 'unit',
            single: true,
            onAfterChoose: function (rows, index) {
                if (rows) {
                    this.setData({
                        index: index,
                        unitName: rows[0].unitName
                    })
                }
            },
            sort: false
        }, {
            field: 'highLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.HIGHLIMIT',
            // editor : true,
            validators: ['number'],
            sort: false
        }, {
            field: 'indexLowLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.INDEXLOWLIMIT',
            sort: false
        }, {
            field: 'indexHighLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.INDEXHIGHLIMIT',
            sort: false
        }, {
            field: 'resultStatus',
            title: 'T_EMC_ORDER_TASK_RESULT.RESULTSTATUS',
            sort: false,
            styleFormatter: function (row) {
                if (row.resultStatus === 'OOS') {
                    return {
                        backgroundColor: 'red'
                    }
                }
            }
        }, {
            field: 'ext$.methodname',
            title: 'T_EMC_ORDER_TASK_RESULT.METHODNAME',
            sort: false
        }, {
            field: 'ext$.testmethodbasis',
            title: 'T_EMC_ORDER_TASK_RESULT.TESTMETHODBASIS',
            sort: false
        }]
    },

    getEditItemListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.testname',
            title: 'T_EMC_ORDER_TASK_RESULT.TESTNAME',
        }, {
            field: 'itemName',
            title: 'T_EMC_ORDER_TASK_RESULT.ITEMNAME',
        }, {
            field: 'itemAliasName',
            title: 'T_EMC_ORDER_TASK_RESULT.ITEMALIASNAME',
        }, {
            field: 'unitName',
            title: 'T_EMC_ORDER_TASK_RESULT.UNITNAME',
            type: 'choose',
            category: 'unit',
        }, {
            field: 'highLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.HIGHLIMIT',
        }, {
            field: 'lowLimit',
            title: 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT',
        }];
    },

    getQcIndexTestMethodListColumns: function () {
        return [{
            checkbox: true
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_ORDER_TASK_RESULT.TESTNAME',
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_ORDER_TASK_RESULT.METHODNAME',
        },];
    },

    getQcIndexListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.itemname',
            title : '分析项名称',
        }, {
            field : 'ext$.qcname',
            title : 'T_EMC_ITEM_QCINDEX.QCCATEGORYID',
        }, {
            field : 'resultLowLimit',
            title : 'T_EMC_ITEM_QCINDEX.RESULTLOWLIMIT',
        }, {
            field : 'resultHighLimit',
            title : 'T_EMC_ITEM_QCINDEX.RESULTHIGHLIMIT',
        }, {
            field : 'decisionCategory',
            title : 'T_EMC_ITEM_QCINDEX.DECISIONCATEGORY',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'decisionCategory',
            },
        }, {
            field : 'stdLowLimit',
            title : 'T_EMC_ITEM_QCINDEX.STDLOWLIMIT',
        }, {
            field : 'stdHighLimit',
            title : 'T_EMC_ITEM_QCINDEX.STDHIGHLIMIT',
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ITEM_QCINDEX.ORDERCATEGORY',
        } ];
    },

});
