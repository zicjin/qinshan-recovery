var emcPurchManage = Gikam.getPageObject({});
emcPurchManage.auditPage = {

    getAuditColumns : function(purchObject) {
        return purchObject.getAuditListColumns();
    },

    getAuditGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.ITEM.SCM.EQUIPTPURCHS.TAB.EQUIPT_PURCH',
                items : [ {
                    type : 'grid',
                    id : 'emc-equipt-purch-audit-list-grid',
                    url : emcEquiptPurch.baseUrl + '/queries/tasks',
                    requestData : _this.param,
                    columns : _this.getAuditColumns(emcEquiptPurch),
                    onCellClick : function(field, row) {
                        field === 'equiptName' && _this.loadDetailPage(row.id, emcEquiptPurch);
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                        icon : 'pass',
                        onClick : function() {
                            _this.pass(emcEquiptPurch);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                        icon : 'reject',
                        onClick : function() {
                            _this.reject(emcEquiptPurch);
                        }
                    } ]
                } ]
            }, {
                title : 'EMC.MODULE.ITEM.SCM.CONVENMTLS.TAB.CONVEN_MTL',
                items : [ {
                    type : 'grid',
                    id : 'emc-conven-mtl-audit-list-grid',
                    url : emcConvenMtl.baseUrl + '/queries/tasks',
                    requestData : _this.param,
                    columns : _this.getAuditColumns(emcConvenMtl),
                    onCellClick : function(field, row) {
                        field === 'mtlName' && _this.loadDetailPage(row.id, emcConvenMtl);
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                        icon : 'pass',
                        onClick : function() {
                            _this.pass(emcConvenMtl);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                        icon : 'reject',
                        onClick : function() {
                            _this.reject(emcConvenMtl);
                        }
                    } ]
                } ]
            }, {
                title : 'EMC.MODULE.ITEM.SCM.OFFICEMTLS.TAB.OFFICE_MTL',
                items : [ {
                    type : 'grid',
                    id : 'emc-office-mtl-audit-list-grid',
                    url : emcOfficeMtl.baseUrl + '/queries/tasks',
                    requestData : _this.param,
                    columns : _this.getAuditColumns(emcOfficeMtl),
                    onCellClick : function(field, row) {
                        field === 'mtlName' && _this.loadDetailPage(row.id, emcOfficeMtl);
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                        icon : 'pass',
                        onClick : function() {
                            _this.pass(emcOfficeMtl);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                        icon : 'reject',
                        onClick : function() {
                            _this.reject(emcOfficeMtl);
                        }
                    } ]
                } ]
            }, {
                title : 'EMC.MODULE.ITEM.SCM.STANDARDMTLS.TAB.STANDARD_MTL',
                items : [ {
                    type : 'grid',
                    id : 'emc-standard-mtl-audit-list-grid',
                    url : emcStandardMtl.baseUrl + '/queries/tasks',
                    requestData : _this.param,
                    columns : _this.getAuditColumns(emcStandardMtl),
                    onCellClick : function(field, row) {
                        field === 'mtlName' && _this.loadDetailPage(row.id, emcStandardMtl);
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                        icon : 'pass',
                        onClick : function() {
                            _this.pass(emcStandardMtl);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                        icon : 'reject',
                        onClick : function() {
                            _this.reject(emcStandardMtl);
                        }
                    } ]
                } ]
            } ]
        }
    },

    loadDetailPage : function(id, purchObject) {
        Gikam.create('modal', {
            title : 'EMC.MODULE.ITEM.SCM.' + purchObject.itemFolder + '.MODAL.' + purchObject.itemName,
            width : Gikam.width.XL,
            height : purchObject.auditModalHeight,
            url : purchObject.baseUrl + '/page/detail' + Gikam.param({
                id : id
            }),
            onAfterClose : function() {
                Gikam.getComp(purchObject.auditGridId).refresh();
            }
        });
    },

    pass : function(purchObject) {
        var grid = Gikam.getComp(purchObject.auditGridId);
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : purchObject
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function(purchObject) {
        var grid = Gikam.getComp(purchObject.auditGridId);
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : purchObject
        }).done(function() {
            grid.refresh();
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-purch-manage-audit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getAuditGridParam() ]
            }
        });
    }

}