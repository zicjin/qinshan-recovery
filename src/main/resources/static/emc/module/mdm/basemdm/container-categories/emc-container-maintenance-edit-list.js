//@ sourceURL=emc-container-maintenance-edit-list.js
emcContainerCategory.maintenanceListPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-container-maintenance-edit-list-grid',
            service : 'emcContainerCategoryServiceImpl',
            url : '/secure/emc/module/mdm/bizmdm/tests/container-maintenance/queries/raw',
	        requestData : {
	            org_filter : '1',
	        },
            columns : emcContainerCategory.getMaintenanceEditListColumns(),
            page : true,
            pageSize : 100,
            group: {
                fields: ['ext$.monitortmplname'],
                groupCheckbox : false,
                expand : false,
                formatter: function (row) {
                    return '样品类型:' + row.ext$.monitortmplname;
                }
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    return this.activeRowByIndex(0);
                }
                Gikam.getComp('emc-container-maintenance-testmethod-list-grid').cleanData();
            },
            deleteFormatter : function(row) {
                return row.fixative || row.ext$.containercategoryname;
            },
            onRowActive : function(index, row) {
                if (row) {
                    var tmids = row.ext$.tmids;
                    tmids = tmids.replace(/,/g,"','");
                    Gikam.getAsyncComp('emc-container-maintenance-testmethod-list-grid').done(function(grid) {
                        grid.refresh({
                            url : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests/queries',
                            requestData : {
                                tmids : "'" + tmids + "'"
                            }
                        });
                    });
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.add();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    var selections = Gikam.getComp('emc-container-maintenance-edit-list-grid').getSelections();
                    var ids = "";
                    if (selections.length > 0) {
                        for (var j = 0; j < selections.length; j++) {
                            ids = ids + selections[j].ext$.ids + ",";
                        }
                    }
                    Gikam.getComp('emc-container-maintenance-edit-list-grid').deleteRows(Gikam.printf(emcContainerCategory.baseUrl + '/{ids}/dele-order-categories', {
                        ids : ids
                    })).done(function() {
                        Gikam.getComp('emc-container-maintenance-testmethod-list-grid').refresh();
                    });
                }
            } , {
                type : 'button',
                text : 'GIKAM.BUTTON.COPY',
                icon : 'copy',
                onClick : function() {
                    _this.copyCM();
                }
            }, {
                type : 'dropDownMenu',
                text : 'GIKAM.BUTTON.REEDIT',
                icon : 'edit',
                items : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CONTAINERGROUP',
                    icon : 'edit',
                    onClick : function() {
                        _this.modify("containerGroup");
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.RQLX',
                    icon : 'edit',
                    onClick : function() {
                        _this.modify("rqlx");
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.STORECONDITION',
                    icon : 'edit',
                    onClick : function() {
                        _this.modify("storeCondition");
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.FIXATIVE',
                    icon : 'edit',
                    onClick : function() {
                        _this.modify("zcfs");
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CYGS',
                    icon : 'edit',
                    onClick : function() {
                        _this.modify("cygs");
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.SAMPLEWEIGHT',
                    icon : 'edit',
                    onClick : function() {
                        _this.modify("sampleWeight");
                    }
                } ]
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ADDTESTMETHOD',
                icon : 'add',
                onClick : function() {
                    _this.addTM()
                }
            } ]
        }
    },

    modify : function(type){
        var selections = Gikam.getComp('emc-container-maintenance-edit-list-grid').getSelections();
        if (selections.length === 0) {
            Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
            return;
        }
        if ("containerGroup" === type && selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var ids = "";
        if (selections.length > 0) {
            for (var j = 0; j < selections.length; j++) {
                ids = ids + selections[j].ext$.ids + ",";
            }
        }
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BASEMDM.CONTAINERCATEGORIES.MODAL.MODIFY',
            width:'360',
            height:'160',
            url : '/secure/emc/module/mdm/basemdm/container-categories/page/cm-modify' + Gikam.param({
                type : type,
                containerGroup : selections[0].containerGroup,
                containercategoryid : selections[0].containerCategoryId,
                containercategoryname : selections[0].ext$.containercategoryname,
                sampleqty : selections[0].sampleqty,
                fixative : encodeURI(selections[0].fixative),
                sampleWeight : selections[0].sampleWeight,
                storeCondition : selections[0].storeCondition,
            }),
            onAfterClose : function(items) {
                if (!Gikam.isEmpty(items)) {
                    var chooseData = [];
                    var data = {};
                    data.containerGroup = items.containerGroup;
                    data.containerCategoryId = items.containercategoryid;
                    data.sampleqty = items.sampleqty;
                    data.fixative = items.fixative;
                    data.storeCondition = items.storeCondition;
                    data.sampleWeight = items.sampleWeight;
                    chooseData.push(data);
                    Gikam.postText(emcContainerCategory.baseUrl +'/modify' + '/'+ type, Gikam.getJsonWrapper({},['EmcTestMethodOrderCategoryServiceImpl', selections], ['emcContainerCategoryServiceImpl', chooseData])).done(function() {
                        Gikam.getComp('emc-container-maintenance-edit-list-grid').refresh();
                    });
                }
            }
        });
    },

    addTM : function() {
        var selections = Gikam.getComp('emc-container-maintenance-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
            return;
        }

        var tmidsnotin = selections[0].ext$.tmids;
        let testMethodIds = tmidsnotin.split(",");
        tmidsnotin = tmidsnotin.replace(/,/g,"','");
        tmidsnotin = "'" + tmidsnotin + "'";

        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BASEMDM.CONTAINERCATEGORIES.MODAL.ADD',
            width:'800',
            height:'600',
            url : '/secure/emc/module/mdm/basemdm/container-categories/page/test-method-choose' + Gikam.param({
                tmidsnotin : tmidsnotin
            }),
            onAfterClose : function(item) {
                if (!Gikam.isEmpty(item)) {
                    var testmethodids = item.testmethodids;
                    testmethodids.split(',').forEach(id => {
                        if(!Gikam.isEmpty(id)) {
                            testMethodIds.push(id);
                        }
                    });

                    testMethodIds = testMethodIds.filter((s) => {
                        return s && s.trim();
                    });

                    var chooseData = selections.map(function(selection) {
                        return {
                            monitorTmplId : selection.monitorTmplId,
                            monitorTmplName : selection.monitorTmplName,
                            containerCategoryId : selection.containerCategoryId,
                            containerGroup : selection.containerGroup,
                            sampleqty : selection.sampleqty,
                            fixative : selection.fixative,
                            sampleWeight : selection.sampleWeight,
                            storeCondition : selection.storeCondition
                        }
                    });

                    Gikam.postText(emcContainerCategory.baseUrl +'/add' + '/'+ testmethodids , Gikam.getJsonWrapper({}, ['emcContainerCategoryServiceImpl', chooseData])).done(function() {
                        Gikam.getComp('emc-container-maintenance-edit-list-grid').refresh();
                        Gikam.getAsyncComp('emc-container-maintenance-testmethod-list-grid').done(function(grid) {
                            grid.refresh({
                                url : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests/queries',
                                requestData : {
                                    tmids : testMethodIds.join()
                                }
                            });
                        });
                    });

                }
            }
        });
    },

    copyCM : function() {
        var selections = Gikam.getComp('emc-container-maintenance-edit-list-grid').getSelections();
        if (selections.length !== 1) {
            Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
            return;
        }
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BASEMDM.CONTAINERCATEGORIES.MODAL.COPY',
            width:'360',
            height:'160',
            url : '/secure/emc/module/mdm/basemdm/container-categories/page/cm-copy' + Gikam.param({
                tmids : '',
            }),
            onAfterClose : function(item) {
                if (!Gikam.isEmpty(item)) {
                    var containerGroup = [];
                    containerGroup.push({containerGroup : item.containerGroup})
                    Gikam.getComp('emc-container-maintenance-edit-list-grid').insert(Gikam.printf(emcContainerCategory.baseUrl + '/{ids}/{monitortmplid}/copy', {
                        ids : selections[0].ext$.ids,
                        monitortmplid : item.monitortmplid
                    }), containerGroup );

                }
            }
        });
    },

    add : function() {
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BASEMDM.CONTAINERCATEGORIES.MODAL.ADD',
            width:'800',
            height:'600',
            url : '/secure/emc/module/mdm/basemdm/container-categories/page/tm-list' + Gikam.param({
                tmids : '',
            }),
            onAfterClose : function(item) {
                if (!Gikam.isEmpty(item)) {
                    Gikam.getComp('emc-container-maintenance-edit-list-grid').insert(Gikam.printf(emcContainerCategory.baseUrl + '/{ids}/add', {
                        ids : item.id
                    }), [item]);
                }
            }
        });
    },

    getEastGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-container-maintenance-testmethod-list-grid',
            service : 'emcContainerCategoryServiceImpl',
            columns : emcContainerCategory.getMaintenanceTMListColumns(),
            deleteFormatter : function(row) {
                return row.ext$.methodname || row.ext$.testname;
            },
            readonly : true,
            page : true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.MOVE',
                icon : 'move',
                onClick : function() {
                    _this.move();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    var activeRow = Gikam.getComp('emc-container-maintenance-edit-list-grid').getActivedRow();
                    var selections = Gikam.getComp('emc-container-maintenance-testmethod-list-grid').getSelections();
                    var ids = activeRow.ext$.ids;
                    var testmethodids = "";
                    if (selections.length > 0) {
                        for (var j = 0; j < selections.length; j++) {
                            testmethodids = testmethodids + selections[j].id + ",";
                        }
                    }
                    Gikam.getComp('emc-container-maintenance-edit-list-grid').deleteRows(Gikam.printf(emcContainerCategory.baseUrl + '/{ids}/{testmethodids}/dele-order-categories', {
                        ids : ids,
                        testmethodids : testmethodids
                    })).done(function() {
                        Gikam.getComp('emc-container-maintenance-edit-list-grid').refresh();
                        Gikam.getComp('emc-container-maintenance-testmethod-list-grid').refresh();
                    });
                }
            } ]
        }
    },

    move : function() {
        var selections = Gikam.getComp('emc-container-maintenance-testmethod-list-grid').getSelections();
        if (selections.length == 0) {
            Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
            return;
        }
        var moveIds = "";
        for (var m = 0; m < selections.length; m++) {
            moveIds = moveIds + selections[m].id + ",";
        }
        var activeRow = Gikam.getComp('emc-container-maintenance-edit-list-grid').getActivedRow();
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BASEMDM.CONTAINERCATEGORIES.MODAL.MOVE',
            width:'800',
            height:'600',
            url : '/secure/emc/module/mdm/basemdm/container-categories/page/container-choose' + Gikam.param({
                monitorTmplId_EQ : activeRow.monitorTmplId,
                containerGroup_DIFFER : activeRow.containerGroup,
            }),
            onAfterClose : function(item) {
                if (!Gikam.isEmpty(item)) {
                    var chooseData = [];
                    var dataOne = {};
                    dataOne.monitorTmplId = item.monitorTmplId;
                    dataOne.monitorTmplName = item.monitorTmplName;
                    dataOne.containerCategoryId = item.containerCategoryId;
                    dataOne.sampleqty = item.sampleqty;
                    dataOne.sampleWeight = item.sampleWeight;
                    dataOne.fixative = item.fixative;
                    dataOne.containerGroup = item.containerGroup;
                    dataOne.ids = item.ext$.ids;
                    chooseData.push(dataOne);
                    var oldIds = activeRow.ext$.ids;
                    Gikam.postText(emcContainerCategory.baseUrl +'/move' + '/'+ oldIds + '/' + moveIds , Gikam.getJsonWrapper({}, ['emcContainerCategoryServiceImpl', chooseData])).done(function() {
                         Gikam.getComp('emc-container-maintenance-edit-list-grid').refresh();
                     });
                }
            }
        });
    },
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-container-maintenance-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '50%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getEastGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
