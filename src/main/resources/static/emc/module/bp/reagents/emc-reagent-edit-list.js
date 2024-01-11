emcReagent.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-reagent-edit-list-grid',
            url : emcReagent.baseUrl + '/queries/raw',
            service : 'emcReagentServiceImpl',
            columns : emcReagent.getEditListColumns(),
            page : true,
            requestData : {
                methodId_SEQ : _this.param.methodId
            },
            deleteFormatter : function(row) {
                return row.reagentName || row.id;
            },
            onRowActive : function(index, row) {
                _this.onReagentRowActive(row);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-reagent-composition-edit-list-grid' ])
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.BP.REAGENTS.MODAL.ADD_REAGENT',
                        fields : emcReagent.getPreInsertFields(),
                        url : emcReagent.baseUrl
                    }).done(function() {
                        Gikam.getComp('emc-reagent-edit-list-grid').refresh();
                    })
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.COPY',
                icon : 'copy',
                onClick : function() {
                    _this.copyReagent();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-reagent-edit-list-grid').deleteRows(emcReagent.baseUrl);
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.REAGENTS.BUTTON.SELECT_REAGENT',
                icon : 'select',
                onClick : function() {
                    var selections = Gikam.getComp('emc-reagent-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    Gikam.getLastModal().close(selections);
                }
            }, {
                type : 'form',
                autoSave : false,
                fields : emcReagent.getShowAllFields()
            }  ]
        }
    },

    getReagentCompositionGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-reagent-composition-edit-list-grid',
            url : '',
            service : 'emcReagentCompositionServiceImpl',
            columns : emcReagent.getCompositionListColumns(),
            checkOnActive : false,
            onRowActive  : function(index) {
                this.selectRowByIndex(index);
            },
            deleteFormatter : function(row) {
                return row.reagentName || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.insertComposition();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    _this.deleteComposition();
                }
            } ]
        }
    },

    onReagentRowActive : function(row) {
        Gikam.getComp('emc-reagent-composition-edit-list-grid').refresh({
            url : Gikam.printf(emcReagent.baseUrl + '/{id}/compositions/queries', {
                id : row.id
            })
        })
    },

    insertComposition : function() {
        var _this = this;
        var activeRow = Gikam.getComp('emc-reagent-edit-list-grid').getActivedRow();
        if (!activeRow) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.getComp('emc-reagent-composition-edit-list-grid').insert(Gikam.printf(emcReagent.baseUrl + '/{id}/compositions', {
            id : activeRow.id,
        }), [ {} ]);
    },

    deleteComposition : function() {
        var activeRow = Gikam.getComp('emc-reagent-edit-list-grid').getActivedRow();
        if (!activeRow) {
            return;
        }
        var url = Gikam.printf(emcReagent.baseUrl + '/{id}/compositions', {
            id : activeRow.id
        });
        Gikam.getComp('emc-reagent-composition-edit-list-grid').deleteRows(url);
    },

    copyReagent : function() {
        var selections = Gikam.getComp('emc-reagent-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_DATA');
            return;
        }

        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.MDM.EQUIPT.USER.RECORD.CONFRIM_COPY', function() {
            var url = Gikam.printf(emcReagent.baseUrl + '/{id}/copyReagent', {
                id : selections[0].id
            });
            Gikam.post(url, Gikam.getJsonWrapper({},['',selections])).done(function () {
                Gikam.getComp('emc-reagent-edit-list-grid').refresh();
                Gikam.getComp('emc-reagent-composition-edit-list-grid').refresh();
            });
        })
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-reagent-edit-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            north : {
                height : '50%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getReagentCompositionGrid() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
