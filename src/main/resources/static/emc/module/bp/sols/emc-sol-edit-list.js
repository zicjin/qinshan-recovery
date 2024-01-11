emcSol.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sol-edit-list-grid',
            url : emcSol.baseUrl + '/queries/raw',
            service : 'emcSolServiceImpl',
            columns : emcSol.getEditListColumns(),
            page : true,
            requestData : {
                methodId_SEQ : _this.param.methodId
            },
            onCellClick : function(field, row) {
                field === 'solNo' && _this.loadDetailPage(row.id);
            },
            deleteFormatter : function(row) {
                return row.solNo || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.BP.SOLS.MODAL.ADD_SOL',
                        fields : emcSol.getPreInsertFields(),
                        url : emcSol.baseUrl
                    }).done(function() {
                        Gikam.getComp('emc-sol-edit-list-grid').refresh();
                    })
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    _this.deleteSol();
                }
            }
            // , {
            //     type : 'button',
            //     text : 'EMC.MODULE.BP.SOLS.BUTTON.SELECT_SOL2',
            //     icon : 'select',
            //     onClick : function() {
            //         _this.selectSol('sol2Id');
            //     }
            // }
            , {
                type : 'button',
                text : 'EMC.MODULE.BP.SOLS.BUTTON.SELECT_SOL1',
                icon : 'select',
                onClick : function() {
                    _this.selectSol('sol1Id');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEW',
                icon : 'browse',
                onClick : function() {
                    Gikam.viewReport('stdSol', Gikam.getComp('emc-sol-edit-list-grid').getSelections());
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.COPY',
                icon : 'edit',
                onClick : function() {
                    _this.copySol();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.SOLS.BUTTON.PUBLISHED',
                icon : 'default',
                onClick : function() {
                    _this.publishSol();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.SOLS.BUTTON.ABANDONED',
                icon : 'default',
                onClick : function() {
                    _this.abandonedSol();
                }
            }, {
                type : 'form',
                autoSave : false,
                fields : emcSol.getShowAllFields()
            } ],
            onLoadSuccess : function(data) {
                if (Gikam.isNotEmpty(data)) {
                    data.map(function(item, index) {
                        if (item.status === '1') {
                            Gikam.getComp('emc-sol-edit-list-grid').toFieldsReadonly(index, [ 'calibrateDate', 'toCalibrateSolName', 'partakeUserName' ]);
                        }
                    });
                }
            }
        }
    },
    selectSol : function(solNo) {
        var selections = Gikam.getComp('emc-sol-edit-list-grid').getSelections();
        if (selections.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        var data = selections[0];
        data[solNo] = data.id;
        Gikam.getLastModal().close(data);
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-sol-edit-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.SOLS.MODAL.SOL_DETAIL',
            url : emcSol.baseUrl + '/page/detail' + Gikam.param({
                id : id
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-sol-edit-list-grid').refresh();
            },
            height: '95%'
        })
    },

    init : function(param) {
        this.param = param;
        this.create();
    },

    copySol : function() {
        var selections = Gikam.getComp('emc-sol-edit-list-grid').getSelections();
        if (selections.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.getLastModal().window.showMask(true);
        Gikam.post(emcSol.baseUrl + '/action/copy', Gikam.getJsonWrapper(null, [ 'emcSolServiceImpl', selections ])).always(function() {
            Gikam.getComp('emc-sol-edit-list-grid').refresh();
        }).always(function() {
            Gikam.getLastModal().window.closeMask();
        });

    },

    publishSol : function() {
        var selections = Gikam.getComp('emc-sol-edit-list-grid').getSelections();
        if (selections.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.getLastModal().window.showMask(true);
        Gikam.put(emcSol.baseUrl + '/action/publish', Gikam.getJsonWrapper(null, [ 'emcSolServiceImpl', selections ])).always(function() {
            Gikam.getComp('emc-sol-edit-list-grid').refresh();
        }).always(function() {
            Gikam.getLastModal().window.closeMask();
        });
    },

    
    abandonedSol : function() {
        var selections = Gikam.getComp('emc-sol-edit-list-grid').getSelections();
        if (selections.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.getLastModal().window.showMask(true);
        Gikam.put(emcSol.baseUrl + '/action/abandoned', Gikam.getJsonWrapper(null, [ 'emcSolServiceImpl', selections ])).always(function() {
            Gikam.getComp('emc-sol-edit-list-grid').refresh();
        }).always(function() {
            Gikam.getLastModal().window.closeMask();
        });
    },
    
    deleteSol : function() {
        var selections = Gikam.getComp('emc-sol-edit-list-grid').getSelections();
        if (selections.length > 0) {
            var publishArr = [];
            selections.map(function(item) {
                if (item.status === '1') {
                    publishArr.push(item);
                }
            });
            if (Gikam.isNotEmpty(publishArr)) {
                Gikam.alert('EMC.MODULE.BP.SOLS.TIP.CAN_NOT_DELETE');
                return;
            } else {
                Gikam.getComp('emc-sol-edit-list-grid').deleteRows(emcSol.baseUrl);
            }
        }
    }
};
