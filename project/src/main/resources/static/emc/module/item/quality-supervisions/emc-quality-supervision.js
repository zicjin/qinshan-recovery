var emcQualitySupervision = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/quality-supervisions',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcQualitySupervision.workflow.dbTable), /*{
            field : 'supervisionCode',
            title : 'T_EMC_QUALITY_SUPERVISION.SUPERVISIONCODE',
            width : 120
        }, {
            field : 'createdByName',
            title : 'T_EMC_CORRECTION.CREATEDBYNAME',
            width : 100
        } */
            // 监督内容、监督对象、计划时间（比如每周一次的，一个月就是四次，计划分四行）、监督员（（人员可以选择并提前一周提醒））和备注
        {
            field : 'ext$.content',
            title : '监督内容',
            type: 'richText',
            editor: true,
        },{
            field : 'ext$.targetobj',
            title : '监督对象',
            editor: true,
        },{
            field : 'ext$.plantime',
            title : '计划时间',
            editor: true,
        },{
            field : 'ext$.supervisorname',
            title : '监督员',
            editor: true,
            type : 'choose',
            category : 'user-simple',
            editable : true,
            single : false,
            onAfterChoose : function(rows,index) {
                if (!Gikam.isEmpty(rows)) {
                    var ids = "";
                    var names = "";
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            ids += rows[index].id;
                            names += rows[index].userName;
                            break;
                        }
                        ids += rows[index].id + ",";
                        names += rows[index].userName + ",";
                    }
                    this.setData({
                        index : index,
                        ext$:{
                            supervisorid: ids,
                            supervisorname: names
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    ext$:{
                        supervisorid: '',
                        supervisorname: ''
                    }
                });
            }
        },{
            field : 'ext$.remark',
            title : '备注',
            type: 'richText',
            editor: true,
        }];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcQualitySupervision.workflow.dbTable), /*{
            field : 'supervisionCode',
            title : 'T_EMC_QUALITY_SUPERVISION.SUPERVISIONCODE',
            width : 120
        }, {
            field : 'createdByName',
            title : 'T_EMC_CORRECTION.CREATEDBYNAME',
            width : 100
        } */ {
            field : 'ext$.content',
            title : '监督内容',
            type: 'richText',
        },{
            field : 'ext$.targetobj',
            title : '监督对象',
        },{
            field : 'ext$.plantime',
            title : '计划时间',
        },{
            field : 'ext$.supervisorname',
            title : '监督员',
            type : 'choose',
            category : 'user-simple',
            editable : true,
            single : false,
            onAfterChoose : function(rows,index) {
                if (!Gikam.isEmpty(rows)) {
                    var ids = "";
                    var names = "";
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            ids += rows[index].id;
                            names += rows[index].userName;
                            break;
                        }
                        ids += rows[index].id + ",";
                        names += rows[index].userName + ",";
                    }
                    this.setData({
                        index : index,
                        ext$:{
                            supervisorid: ids,
                            supervisorname: names
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    ext$:{
                        supervisorid: '',
                        supervisorname: ''
                    }
                });
            }
        },{
            field : 'ext$.remark',
            title : '备注',
            type: 'richText',
        }];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcQualitySupervision.workflow.dbTable), /*{
            field : 'supervisionCode',
            title : 'T_EMC_QUALITY_SUPERVISION.SUPERVISIONCODE',
            width : 120
        }, {
            field : 'createdByName',
            title : 'T_EMC_CORRECTION.CREATEDBYNAME',
            width : 100
        } */ {
            field : 'ext$.content',
            title : '监督内容',
            type: 'richText',
        },{
            field : 'ext$.targetobj',
            title : '监督对象',
        },{
            field : 'ext$.plantime',
            title : '计划时间',
        },{
            field : 'ext$.supervisorname',
            title : '监督员',
            type : 'choose',
            category : 'user-simple',
            editable : true,
            single : false,
            onAfterChoose : function(rows,index) {
                if (!Gikam.isEmpty(rows)) {
                    var ids = "";
                    var names = "";
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            ids += rows[index].id;
                            names += rows[index].userName;
                            break;
                        }
                        ids += rows[index].id + ",";
                        names += rows[index].userName + ",";
                    }
                    this.setData({
                        index : index,
                        ext$:{
                            supervisorid: ids,
                            supervisorname: names
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    ext$:{
                        supervisorid: '',
                        supervisorname: ''
                    }
                });
            }
        },{
            field : 'ext$.remark',
            title : '备注',
            type: 'richText',
        }];
    },

    getPlanBaseInfoFormFields : function() {
        return [ {
            field : 'planTime',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.PLANTIME',
            preInsert : true,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'planProject',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.PLANPROJECT',
            preInsert : true,
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'supervisorName',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.SUPERVISORNAME',
            preInsert : true,
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'supervisedOrgName',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.SUPERVISEDORGNAME',
            preInsert : true,
            validators : [ 'strLength[0,270]' ]
        }, {
            field : 'supervisedUserName',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.SUPERVISEDUSERNAME',
            preInsert : true,
            validators : [ 'strLength[0,60]' ]
        }  ];
    },

    getPlanEditListColumns : function() {
        return [ {
            checkbox : true
        },{
            index : true
        }, {
            field : 'planTime',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.PLANTIME',
            editor : true,
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'planProject',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.PLANPROJECT',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'supervisorName',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.SUPERVISORNAME',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'supervisedOrgName',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.SUPERVISEDORGNAME',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,270]' ]
        }, {
            field : 'supervisedUserName',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.SUPERVISEDUSERNAME',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'ext$.year',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.YEAR',
            editor : true,
        }, {
            field : 'ext$.supervisionmode',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.SUPERVISIONMODE',
            editor : true,
        }, {
            field : 'ext$.inspectioncycle',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.INSPECTIONCYCLE',
            editor : true,
        }, {
            field : 'ext$.monitorresults',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.MONITORRESULTS',
            editor : true,
        }, {
            field : 'createdTime',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.CREATEDTIME',
            type : 'date',
            editor : true,
        }, {
            field : 'createdByName',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.CREATEDBYNAME',
            editor : true,
        }, {
            field : 'ext$.approve',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.APPROVER',
            editor : true,
        }, {
            field : 'ext$.approvaltime',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.APPROVALTIME',
            type : 'date',
            editor : true,
        } ];
    },

    getPlanAuditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'planTime',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.PLANTIME'
        }, {
            field : 'planProject',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.PLANPROJECT',
        }, {
            field : 'supervisorName',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.SUPERVISORNAME'
        }, {
            field : 'supervisedOrgName',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.SUPERVISEDORGNAME'
        }, {
            field : 'supervisedUserName',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.SUPERVISEDUSERNAME'
        } ];
    },

    getPlanSearchListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'planTime',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.PLANTIME'
        }, {
            field : 'planProject',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.PLANPROJECT'
        }, {
            field : 'supervisorName',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.SUPERVISORNAME'
        }, {
            field : 'supervisedOrgName',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.SUPERVISEDORGNAME'
        }, {
            field : 'supervisedUserName',
            title : 'T_EMC_QUALITY_SUPERVISION_PLAN.SUPERVISEDUSERNAME'
        } ];
    },

    getContBaseInfoFormFields : function() {
        return [ {
            field : 'contProject',
            title : 'T_EMC_QUALITY_SUPERVISION_CONT.CONTPROJECT',
            preInsert : true,
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'contResult',
            title : 'T_EMC_QUALITY_SUPERVISION_CONT.CONTRESULT',
            preInsert : true,
            validators : [ 'strLength[0,540]' ]
        } ];
    },

    getContEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'contProject',
            title : 'T_EMC_QUALITY_SUPERVISION_CONT.CONTPROJECT',
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'contResult',
            title : 'T_EMC_QUALITY_SUPERVISION_CONT.CONTRESULT',
            editor : true,
            type : 'richText',
            validators : [ 'notEmpty', 'strLength[0,540]' ]
        }, {
            field : 'conNote',
            title : 'T_EMC_QUALITY_SUPERVISION_CONT.CONNOTE',
            editor : true,
            type : 'richText',
            validators : [ 'strLength[0,300]' ]
        } ];
    },

    getContAuditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'contProject',
            title : 'T_EMC_QUALITY_SUPERVISION_CONT.CONTPROJECT',
        }, {
            field : 'contResult',
            title : 'T_EMC_QUALITY_SUPERVISION_CONT.CONTRESULT',
        }, {
            field : 'conNote',
            title : 'T_EMC_QUALITY_SUPERVISION_CONT.CONNOTE',
        } ];
    },

    getContSearchListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'contProject',
            title : 'T_EMC_QUALITY_SUPERVISION_CONT.CONTPROJECT',
        }, {
            field : 'contResult',
            title : 'T_EMC_QUALITY_SUPERVISION_CONT.CONTRESULT',
        }, {
            field : 'conNote',
            title : 'T_EMC_QUALITY_SUPERVISION_CONT.CONNOTE',
        } ];
    },

    workflow : {
        dbTable : 'T_EMC_QUALITY_SUPERVISION',
        columns : [ {
            field : 'id',
            title : 'T_EMC_QUALITY_SUPERVISION.ID'
        } ]
    }
});
