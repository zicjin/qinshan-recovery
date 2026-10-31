var emcPrecaution = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/precautions',
    
    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcPrecaution.workflow.dbTable), {
            field : 'precauteCode',
            title : 'T_EMC_PRECAUTION.PRECAUTECODE',
            width : 120
        }, {
            field : 'createdByName',
            title : 'T_EMC_PRECAUTION.CREATEDBYNAME',
            width : 100
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcPrecaution.workflow.dbTable), {
            field : 'precauteCode',
            title : 'T_EMC_PRECAUTION.PRECAUTECODE',
            width : 120
        }, {
            field : 'createdByName',
            title : 'T_EMC_PRECAUTION.CREATEDBYNAME',
            width : 100
        } ];
    },


    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcPrecaution.workflow.dbTable), {
            field : 'precauteCode',
            title : 'T_EMC_PRECAUTION.PRECAUTECODE',
            width : 120
        }, {
            field : 'createdByName',
            title : 'T_EMC_PRECAUTION.CREATEDBYNAME',
            width : 100
        } ];
    },
    
    getDetailFormColumns : function(operType) {
        var isReadonly = operType === 'edit' ? false : true;
        return [ {
            field : 'source',
            title : 'T_EMC_PRECAUTION.SOURCE',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'precautionSource',
                codeClassId : 'biz'
            },
            validators : [ 'strLength[0,36]' ],
            readonly : isReadonly
        }, {
            field : 'sourceNo',
            title : 'T_EMC_PRECAUTION.SOURCENO',
            editor : true,
            validators : [ 'strLength[0,36]' ],
            readonly : isReadonly
        }, {
            field : 'implementOrgName',
            title : 'T_EMC_PRECAUTION.IMPLEMENTORGNAME',
            editor : true,
            validators : [ 'strLength[0,270]' ],
            readonly : isReadonly
        }, {
            field : 'implementLeader',
            title : 'T_EMC_PRECAUTION.IMPLEMENTLEADER',
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
                        id : Gikam.getComp('emc-precaution-edit-list-grid').getSelections().id,
                        ids : ids,
                        implementLeader : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-precaution-edit-list-grid').getSelections().id,
                    ids : '',
                    implementLeader : '',
                });
            }
        }, {
            field : 'partakeUser',
            title : 'T_EMC_MANAGE_REVIEW.PARTAKEUSER',
            validators : [ 'strLength [0,120]' ],
            type : 'choose',
            category : 'user-simple',
            editable : true,
            single : false,
            readonly : isReadonly,
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
                        id : Gikam.getComp('emc-precaution-edit-list-grid').getSelections().id,
                        ids : ids,
                    	partakeUser : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-precaution-edit-list-grid').getSelections().id,
                    ids : '',
                    partakeUser : '',
                });
            }
        }, {
            field : 'reasion',
            title : 'T_EMC_PRECAUTION.REASION',
            editor : true,
            type : 'textarea',
            colspan: 2,
            height : 110,
            validators : [ 'strLength[0,540]' ],
            readonly : isReadonly
        }, {
            field : 'precaution',
            title : 'T_EMC_PRECAUTION.PRECAUTION',
            editor : true,
            type : 'textarea',
            colspan: 2,
            height : 110,
            validators : [ 'strLength[0,540]' ],
            readonly : isReadonly
        }, {
            field : 'verifyMethod',
            title : 'T_EMC_PRECAUTION.VERIFYMETHOD',
            editor : true,
            type : 'textarea',
            colspan: 2,
            height : 110,
            validators : [ 'strLength[0,540]' ],
            readonly : isReadonly
        }, {
            field : 'completeDate',
            title : 'T_EMC_PRECAUTION.COMPLETEDATE',
            editor : true,
            type : 'date',
            readonly : isReadonly
        }, {
            field : 'verifyUser',
            title : 'T_EMC_PRECAUTION.VERIFYUSER',
            editor : true,
            validators : [ 'strLength[0,60]' ],
            type : 'choose',
            category : 'user-simple',
            editable : true,
            single : false,
            readonly : isReadonly,
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
                        id : Gikam.getComp('emc-precaution-edit-list-grid').getSelections().id,
                        ids : ids,
                        verifyUser : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-precaution-edit-list-grid').getSelections().id,
                    ids : '',
                    verifyUser : '',
                });
            }
        } ];
    },
    
    workflow : {
        dbTable : 'T_EMC_PRECAUTION',
        columns : [ {
            field : 'id',
            title : 'T_EMC_PRECAUTION.ID'
        } ]
    }
});
