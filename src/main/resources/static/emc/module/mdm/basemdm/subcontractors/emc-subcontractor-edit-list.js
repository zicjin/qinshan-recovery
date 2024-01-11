emcSubcontractor.editPage = {

    getSubcontractorGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-subcontractor-edit-list-grid',
            url : emcSubcontractor.baseUrl + '/queries/raw',
            columns : emcSubcontractor.getEditListColumns(),
            service : 'emcSubcontractorServiceImpl',
	        requestData : {
	            orgId_SB : workspace.user.ext$.parentorgid
	        },
            deleteFormatter : function(row) {
                return row.subcontractorName || row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-subcontractor-edit-list-uploader').grid.cleanData();
                    Gikam.getComp('emc-subcontractor-edit-list-order-task-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                _this.onSubcontractorActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    var url = Gikam.printf(emcSubcontractor.baseUrl);
                    Gikam.getComp('emc-subcontractor-edit-list-grid').insert(url, [ {} ]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-subcontractor-edit-list-grid').deleteRows(emcSubcontractor.baseUrl);
                }
            } ]
        }
    },

    getOrderTaskGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-subcontractor-edit-list-order-task-grid',
            columns : emcSubcontractor.getOrderTaskListColumns(),
            url : _this.getOrderTaskUrl(),
            deleteFormatter : function(row) {
                return row.ext$.testname || row.id;
            },
        }
    },

    getAttachmentParam : function() {
        return {
            type : 'uploader',
            id : 'emc-subcontractor-edit-list-uploader',
            filter : false,
            dbTable : 'T_EMC_SUBCONTRACTOR',
        }
    },

    onSubcontractorActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-subcontractor-edit-list-order-task-grid').refresh({
                url : _this.getOrderTaskUrl(row.id),
            });
            Gikam.getComp('emc-subcontractor-edit-list-uploader').setOptions({
                bizId : row.id
            })
        }
    },

    getOrderTaskUrl : function(subcontractorId) {
        if (subcontractorId) {
            return Gikam.printf(emcSubcontractor.baseUrl + '/{id}/records/queries', {
                id : subcontractorId
            })
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            north : {
                height : 300,
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.MDM.BASEMDM.SUBCONTRACTORS.TAB.BASE_INFO',
                        items : [ _this.getSubcontractorGridParam() ]
                    } ]
                } ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.MDM.BASEMDM.SUBCONTRACTORS.TAB.RECORD_INFO',
                        items : [ _this.getOrderTaskGridParam() ]
                    }, {
                        title : 'GIKAM.FILE.ATTACHMENT',
                        items : [ _this.getAttachmentParam() ]
                    } ]
                } ]
            },
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(emcSubcontractor.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function() {
        this.create();
    }
};
