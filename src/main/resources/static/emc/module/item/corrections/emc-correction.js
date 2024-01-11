var emcCorrection = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/corrections',
    
    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcCorrection.workflow.dbTable), {
            field : 'correctionCode',
            title : 'T_EMC_CORRECTION.CORRECTIONCODE',
            width : 120
        }, {
            field : 'createdByName',
            title : 'T_EMC_CORRECTION.CREATEDBYNAME',
            width : 100
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcCorrection.workflow.dbTable), {
            field : 'correctionCode',
            title : 'T_EMC_CORRECTION.CORRECTIONCODE',
            width : 120
        }, {
            field : 'createdByName',
            title : 'T_EMC_CORRECTION.CREATEDBYNAME',
            width : 100
        } ];
    },


    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcCorrection.workflow.dbTable), {
            field : 'correctionCode',
            title : 'T_EMC_CORRECTION.CORRECTIONCODE',
            width : 120
        }, {
            field : 'createdByName',
            title : 'T_EMC_CORRECTION.CREATEDBYNAME',
            width : 100
        } ];
    },
    
    getDetailFormColumns : function(operType) {
        var isReadonly = operType === 'edit' ? false : true;
        return [ {
            field : 'source',
            title : 'T_EMC_CORRECTION.SOURCE',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'correctionSource',
                codeClassId : 'biz'
            },
            validators : [ 'strLength[0,36]' ],
            readonly : isReadonly
        }, {
            field : 'sourceNo',
            title : 'T_EMC_CORRECTION.SOURCENO',
            editor : true,
            validators : [ 'strLength[0,36]' ],
            readonly : isReadonly
        }, {
            field : 'factDescription',
            title : 'T_EMC_CORRECTION.FACTDESCRIPTION',
            editor : true,
            type : 'textarea',
            colspan: 2,
            height : 110,
            validators : [ 'strLength[0,540]' ],
            readonly : isReadonly
        }, {
            field : 'basis',
            title : 'T_EMC_CORRECTION.BASIS',
            editor : true,
            type : 'textarea',
            colspan: 2,
            height : 110,
            validators : [ 'strLength[0,540]' ],
            readonly : isReadonly
        }, {
            field : 'responsibleOrgName',
            title : 'T_EMC_CORRECTION.RESPONSIBLEORGNAME',
            editor : true,
            validators : [ 'strLength[0,270]' ],
            readonly : isReadonly
        }, {
            field : 'responsibleLeader',
            title : 'T_EMC_CORRECTION.RESPONSIBLELEADER',
            editor : true,
            validators : [ 'strLength[0,60]' ],
            readonly : isReadonly,
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
                        id : Gikam.getComp('emc-correction-edit-list-grid').getSelections().id,
                        ids : ids,
                        responsibleLeader : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-correction-edit-list-grid').getSelections().id,
                    ids : '',
                    responsibleLeader : '',
                });
            }
        }, {
            field : 'discoverDate',
            title : 'T_EMC_CORRECTION.DISCOVERDATE',
            editor : true,
            type : 'date',
            readonly : isReadonly
        }, {
            field : 'discoverUser',
            title : 'T_EMC_CORRECTION.DISCOVERUSER',
            editor : true,
            validators : [ 'strLength[0,300]' ],
            readonly : isReadonly,
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
                        id : Gikam.getComp('emc-correction-edit-list-grid').getSelections().id,
                        ids : ids,
                        discoverUser : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-correction-edit-list-grid').getSelections().id,
                    ids : '',
                    discoverUser : '',
                });
            }
        }, {
            field : 'correction',
            title : 'T_EMC_CORRECTION.CORRECTION',
            editor : true,
            type : 'textarea',
            colspan: 2,
            height : 110,
            validators : [ 'strLength[0,540]' ],
            readonly : isReadonly
        }, {
            field : 'completeSituation',
            title : 'T_EMC_CORRECTION.COMPLETESITUATION',
            editor : true,
            type : 'textarea',
            colspan: 2,
            height : 110,
            validators : [ 'strLength[0,540]' ],
            readonly : isReadonly
        }, {
            field : 'verifyResult',
            title : 'T_EMC_CORRECTION.VERIFYRESULT',
            editor : true,
            type : 'textarea',
            colspan: 2,
            height : 110,
            validators : [ 'strLength[0,540]' ],
            readonly : isReadonly
        }, {
            field : 'completeDate',
            title : 'T_EMC_CORRECTION.COMPLETEDATE',
            editor : true,
            type : 'date',
            readonly : isReadonly
        }, {
            field : 'verifyUser',
            title : 'T_EMC_CORRECTION.VERIFYUSER',
            editor : true,
            validators : [ 'strLength[0,60]' ],
            readonly : isReadonly,
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
                        id : Gikam.getComp('emc-correction-edit-list-grid').getSelections().id,
                        ids : ids,
                        verifyUser : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-correction-edit-list-grid').getSelections().id,
                    ids : '',
                    verifyUser : '',
                });
            }
        } ];
    },
    
    workflow : {
        dbTable : 'T_EMC_CORRECTION',
        columns : [ {
            field : 'id',
            title : 'T_EMC_CORRECTION.ID'
        } ]
    }
});
