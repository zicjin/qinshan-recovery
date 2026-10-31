var emcQualityControl = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/quality-controls',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcQualityControl.workflow.dbTable), /*{
            field : 'qualityControlCode',
            title : 'T_EMC_QUALITY_CONTROL.QUALITYCONTROLCODE',
        }, {
            field : 'createdByName',
            title : 'T_EMC_QUALITY_CONTROL.CREATEDBYNAME'
        }*/ {
            field : 'ext$.bigcategory',
            title : '大类',
            editor: true
        },{
            field : 'ext$.samllcategory',
            title : '小类',
            editor: true
        },{
            field : 'ext$.projname',
            title : '项目名称',
            editor: true
        },{
            field : 'ext$.testmethod',
            title : '检测方法',
            editor: true
        },{
            field : 'ext$.plandate',
            title : '计划实施时间',
            editor: true
        },{
            field : 'ext$.qcexamways',
            title : '考核方式',//可以选择仪器比对、标准样品、加标回收、人员比对、留样复测、方法比对
            type: 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'qcExamWays',
            },
            editor: true
        },{
            field : 'ext$.examuser',
            title : '考核人员',
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
                            examuserid: ids,
                            examuser: names
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    ext$:{
                        examuserid: '',
                        examuser: ''
                    }
                });
            }
        },{
            field : 'ext$.qxrequire',
            title : '质量控制要求',
            editor: true
        }];
     },

    getQualityControlFormFields : function() {
        return [ {
            field : 'qualityControlScope',
            title : 'T_EMC_QUALITY_CONTROL.QUALITYCONTROLSCOPE',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'qcScope',
            },
        }, {
            field : 'qualityControlCategory',
            title : 'T_EMC_QUALITY_CONTROL.QUALITYCONTROLCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'qcCategory',
            },
        }, {
            field : 'qualityControlObject',
            title : 'T_EMC_QUALITY_CONTROL.QUALITYCONTROLOBJECT',
            type : 'textarea',
            colspan : 2,
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'qualityControlTarget',
            title : 'T_EMC_QUALITY_CONTROL.QUALITYCONTROLTARGET',
            type : 'textarea',
            colspan : 2,
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'qualityControlOrgName',
            title : 'T_EMC_QUALITY_CONTROL.QUALITYCONTROLORGNAME',
            colspan : 2,
            validators : [ 'strLength [0,90]' ]
        }, {
            field : 'request',
            title : 'T_EMC_QUALITY_CONTROL.REQUEST',
            type : 'textarea',
            colspan : 2,
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'time',
            title : 'T_EMC_QUALITY_CONTROL.TIME',
            type : 'date',
            colspan : 1,
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'ext$.qcfrequency',
            title : 'T_EMC_QUALITY_CONTROL.QCFREQUENCY',
        }, {
            field : 'remark',
            title : 'T_EMC_QUALITY_CONTROL.REMARK',
            type : 'textarea',
            colspan : 2,
            validators : [ 'strLength [0,180]' ]
        } ];
    },

    getImplementationFormFields : function() {
        return [ {
            field : 'standardMtl',
            title : 'T_EMC_QUALITY_CONTROL.STANDARDMTL',
            validators : [ 'strLength [0,90]' ]
        }, {
            field : 'testBasis',
            title : 'T_EMC_QUALITY_CONTROL.TESTBASIS',
            type : 'textarea',
            colspan : 2,
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'partakeUser',
            title : 'T_EMC_QUALITY_CONTROL.PARTAKEUSER',
            validators : [ 'strLength [0,90]' ],
            type : 'choose',
            category : 'user-simple',
            editable : true,
            single : false,
            onAfterChoose : function(rows) {
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
                        id : Gikam.getComp('emc-quality-control-edit-list-grid').getSelections().id,
                        ids : ids,
                    	partakeUser : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-quality-control-edit-list-grid').getSelections().id,
                    ids : '',
                    partakeUser : '',
                });
            }
        }, {
            field : 'technicalRequest',
            title : 'T_EMC_QUALITY_CONTROL.TECHNICALREQUEST',
            type : 'textarea',
            colspan : 1,
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'envCondition',
            title : 'T_EMC_QUALITY_CONTROL.ENVCONDITION',
            validators : [ 'strLength [0,90]' ]
        }, {
            field : 'resourceConfig',
            title : 'T_EMC_QUALITY_CONTROL.RESOURCECONFIG',
            validators : [ 'strLength [0,90]' ]
        }, {
            field : 'testOrder',
            title : 'T_EMC_QUALITY_CONTROL.TESTORDER',
            validators : [ 'strLength [0,90]' ]
        }, {
            field : 'feasibility',
            title : 'T_EMC_QUALITY_CONTROL.FEASIBILITY',
            type : 'textarea',
            colspan : 2,
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'reviewUser',
            title : 'T_EMC_QUALITY_CONTROL.REVIEWUSER',
            validators : [ 'strLength [0,120]' ],
            type : 'choose',
            category : 'user-simple',
            editable : true,
            single : false,
            onAfterChoose : function(rows) {
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
                        id : Gikam.getComp('emc-quality-control-edit-list-grid').getSelections().id,
                        ids : ids,
                        reviewUser : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-quality-control-edit-list-grid').getSelections().id,
                    ids : '',
                    reviewUser : '',
                });
            }
        
        } , {
            field : 'evaluateUser',
            title : 'T_EMC_QUALITY_CONTROL.REVIEWUSER',
            validators : [ 'strLength [0,120]' ],
            type : 'choose',
            category : 'user-simple',
            editable : true,
            single : false,
            onAfterChoose : function(rows) {
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
                        id : Gikam.getComp('emc-quality-control-edit-list-grid').getSelections().id,
                        ids : ids,
                        evaluateUser : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-quality-control-edit-list-grid').getSelections().id,
                    ids : '',
                    evaluateUser : '',
                });
            }
        
        }  ];
    },

    getResultFormFields : function() {
        return [ {
            field : 'evaluateDate',
            title : 'T_EMC_QUALITY_CONTROL.EVALUATEDATE',
            type : 'date'
        }, {
            field : 'originalRecord',
            title : 'T_EMC_QUALITY_CONTROL.ORIGINALRECORD',
            type : 'textarea',
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'controlResult',
            title : 'T_EMC_QUALITY_CONTROL.CONTROLRESULT',
            type : 'textarea',
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'influence',
            title : 'T_EMC_QUALITY_CONTROL.INFLUENCE',
            type : 'textarea',
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'potentialFactor',
            title : 'T_EMC_QUALITY_CONTROL.POTENTIALFACTOR',
            type : 'textarea',
            validators : [ 'strLength [0,180]' ]
        }, {
            field : 'precaution',
            title : 'T_EMC_QUALITY_CONTROL.PRECAUTION',
            type : 'textarea',
            validators : [ 'strLength [0,180]' ]
        } ];
    },

    workflow : {
        dbTable : 'T_EMC_QUALITY_CONTROL',
        columns : [ {
            field : 'ID',
            title : 'T_EMC_QUALITY_CONTROL.ID'
        } ]
    }
});
