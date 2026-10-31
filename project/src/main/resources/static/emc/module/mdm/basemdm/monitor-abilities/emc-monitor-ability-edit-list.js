emcMonitorAbility.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-monitor-ability-edit-list-grid',
            url : emcMonitorAbility.baseUrl + '/queries/raw',
            columns : emcMonitorAbility.getEditListColumns(),
            service : 'emcMonitorAbilityServiceImpl',
            checkOnActive : true,
            deleteFormatter : function(row) {
                return row.monitorName || row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }
            },
            onRowActive : function(index, row) {
                _this.onGridActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BASEMDM.MONITORABILITIES.TAB.ADD',
                        fields : emcMonitorAbility.getBaseInfoFormFields(),
                        url : emcMonitorAbility.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-monitor-ability-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-monitor-ability-edit-list-grid').deleteRows(emcMonitorAbility.baseUrl);
                }
            } ]
        }
    },

    getItemGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.MONITORABILITIES.TAB.TEST_INFO',
                items : [ {
                    type : 'grid',
                    id : 'emc-monitor-ability-edit-list-test-grid',
                    url : '',
                    columns : emcMonitorAbility.getTestItemColumns(),
                    service : 'EmcMonitorAbilityTestServiceImpl',
                    deleteFormatter : function(row) {
                        return row.ext$.testname || row.id;
                    },
                    onCellClick : function(field, row) {
                        field === 'ext$.methodname' && _this.loadMethodVersionPage(row.methodId);
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.addTestItem();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            var url = emcMonitorAbility.baseUrl + '/tests';
                            Gikam.getComp('emc-monitor-ability-edit-list-test-grid').deleteRows(url);
                        }
                    } ]
                } ]
            }, {
                title : 'EMC.MODULE.MDM.BASEMDM.MONITORABILITIES.TAB.EQUIPT_INFO',
                items : [ {
                    type : 'grid',
                    id : 'emc-monitor-ability-edit-list-equipt-grid',
                    url : '',
                    columns : emcMonitorAbility.getEquipItemColumns(),
                    service : 'EmcMonitorAbilityEquiptServiceImpl',
                    deleteFormatter : function(row) {
                        return row.ext$.equiptname || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.addEquiptItem();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            var url = emcMonitorAbility.baseUrl + '/equipts';
                            Gikam.getComp('emc-monitor-ability-edit-list-equipt-grid').deleteRows(url);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.SYNCEQUIPT',
                        icon : 'refresh',
                        onClick : function() {
                            _this.syncEquipt()
                        }
                    } ]
                } ]
            }, {
                title : 'EMC.MODULE.MDM.BASEMDM.MONITORABILITIES.TAB.EMP_INFO',
                items : [ {
                    type : 'grid',
                    id : 'emc-monitor-ability-edit-list-emp-grid',
                    url : '',
                    columns : emcMonitorAbility.getEmpItemColumns(),
                    deleteFormatter : function(row) {
                        return row.ext$.username || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.addEmpItem();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            var url = emcMonitorAbility.baseUrl + '/emps';
                            Gikam.getComp('emc-monitor-ability-edit-list-emp-grid').deleteRows(url);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.SYNCEMP',
                        icon : 'refresh',
                        onClick : function() {
                            _this.syncEmp();
                        }
                    } ]
                } ]
            } ]
        }
    },

    loadDetailPage : function(itemId) {
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BASEMDM.MONITORABILITIES.TAB.BASE_INFO',
            url : emcMonitorAbility.baseUrl + '/page/detail' + Gikam.param({
                id : itemId,
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-monitor-ability-edit-list-grid').refresh();
            }
        });
    },

    loadMethodVersionPage : function(methodId) {
        var _this = this;
        Gikam.create('modal', {
            url : emcMonitorAbility.baseUrl + '/page/method-detail' + Gikam.param({
                id : methodId
            }),
        });
    },

    onGridActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-monitor-ability-edit-list-test-grid').refresh({
                url : Gikam.printf(emcMonitorAbility.baseUrl + '/{id}/tests/queries', {
                    id : row.id
                }),
            });
            Gikam.getComp('emc-monitor-ability-edit-list-equipt-grid').refresh({
                url : Gikam.printf(emcMonitorAbility.baseUrl + '/{id}/equipts/queries', {
                    id : row.id
                }),
            });
            Gikam.getComp('emc-monitor-ability-edit-list-emp-grid').refresh({
                url : Gikam.printf(emcMonitorAbility.baseUrl + '/{id}/emps/queries', {
                    id : row.id
                }),
            });

        }
    },

    addTestItem : function() {
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BASEMDM.MONITORABILITIES.TAB.TEST_ADD',
            url : Gikam.choose.getConfig('testMethod').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_MONITOR_ABILITY_TEST',
                targetTestMethodIdColumn : 'TESTMETHODID',
                targetFilterColumn : 'MONITORABILITYID',
                targetFilterValue : Gikam.getComp('emc-monitor-ability-edit-list-grid').getActivedRow().id

            }),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var gridId = Gikam.getComp('emc-monitor-ability-edit-list-grid').getActivedRow().id;
                var url = _this.getTestItemUrl(gridId);
                var chooseData = [];
                Gikam.each(rows, function(index, item) {
                    chooseData.push({
                        monitorAbilityId : gridId,
                        testMethodId : item.id,
                        methodId : item.methodId
                    })
                })
                Gikam.getComp('emc-monitor-ability-edit-list-test-grid').insert(url, chooseData);
            }
        });
    },

    addEquiptItem : function() {
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BASEMDM.MONITORABILITIES.TAB.EQUIPT_ADD',
            url : Gikam.choose.getConfig('equipt').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_MONITOR_ABILITY_EQUIPT',
                targetEquiptColumn : 'EQUIPTID',
                targetFilterColumn : 'MONITORABILITYID',
                targetFilterValue : Gikam.getComp('emc-monitor-ability-edit-list-grid').getActivedRow().id

            }),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var gridId = Gikam.getComp('emc-monitor-ability-edit-list-grid').getActivedRow().id;
                var url = _this.getEquiptItemUrl(gridId);
                var chooseData = [];
                Gikam.each(rows, function(index, item) {
                    chooseData.push({
                        monitorAbilityId : gridId,
                        equiptId : item.id,
                    })
                })
                Gikam.getComp('emc-monitor-ability-edit-list-equipt-grid').insert(url, chooseData);
            }
        });
    },

    syncEquipt : function() {
        var gridData = Gikam.getComp('emc-monitor-ability-edit-list-equipt-grid').getSelections();

        if (gridData.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var url = emcMonitorAbility.baseUrl + '/sync-equipt';

        workspace.window.showMask(true);
        Gikam.post(url, Gikam.getJsonWrapper(null, [ '', gridData ])).always(function() {
            workspace.window.closeMask();
        });
    },

    addEmpItem : function() {
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BASEMDM.MONITORABILITIES.TAB.EMP_ADD',
            url : Gikam.choose.getConfig('emp').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_MONITOR_ABILITY_EMP',
                targetEmpColumn : 'EMPID',

            }),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var gridId = Gikam.getComp('emc-monitor-ability-edit-list-grid').getActivedRow().id;
                var url = _this.getEmpItemUrl(gridId);
                var chooseData = [];
                Gikam.each(rows, function(index, item) {
                    chooseData.push({
                        monitorAbilityId : gridId,
                        empId : item.id,
                    })
                })
                Gikam.getComp('emc-monitor-ability-edit-list-emp-grid').insert(url, chooseData);
            }
        });
    },

    syncEmp : function() {
        var gridData = Gikam.getComp('emc-monitor-ability-edit-list-emp-grid').getSelections();

        if (gridData.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var url = emcMonitorAbility.baseUrl + '/sync-emp';

        workspace.window.showMask(true);
        Gikam.post(url, Gikam.getJsonWrapper(null, [ '', gridData ])).always(function() {
            workspace.window.closeMask();
        });
    },

    getTestItemUrl : function(id) {
        if (id) {
            return Gikam.printf(emcMonitorAbility.baseUrl + '/{id}/tests/insert', {
                id : id
            });
        }
    },

    getEquiptItemUrl : function(id) {
        if (id) {
            return Gikam.printf(emcMonitorAbility.baseUrl + '/{id}/equipts/insert', {
                id : id
            });
        }
    },

    getEmpItemUrl : function(id) {
        if (id) {
            return Gikam.printf(emcMonitorAbility.baseUrl + '/{id}/emps/insert', {
                id : id
            });
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-monitor-ability-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : "50%",
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getItemGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
