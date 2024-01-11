emcEquiptRequisition.editPage = function(pageParam){
    var pageType = '-' + pageParam.type;
    return {
        getGridParam : function() {
            var _this = this;
            var requestData = {};
            if (pageType === '-rent'){
                requestData.isBack_SEQ = '0';
                requestData.rentUser_SEQ = workspace.user.userName;
            }
            if (pageType === '-back'){
                requestData.rentUser_SEQ = workspace.user.userName;
            }
            return {
                type : 'grid',
                id : 'emc-equipt-requisition-edit-list-grid' + pageType,
                url : emcEquiptRequisition.baseUrl + '/queries/raw',
                service : 'emcEquiptRequisitionServiceImpl',
                columns : emcEquiptRequisition.getEditListColumns(),
                page : true,
                requestData : requestData,
                onRowActive : function(index, row) {
                    Gikam.getAsyncComp('emc-equipt-requisition-edit-list-grid' + pageType).done(function(grid) {
                        Gikam.getComp('emc-equipt-requisition-edit-list-uploader').setOptions({
                            bizId : row.id,
                        });
                    });
                },
                onLoadSuccess : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).activeRowByIndex(0);
                    } else if(Gikam.getComp('emc-equipt-requisition-edit-list-uploader')) {
                        Gikam.getComp('emc-equipt-requisition-edit-list-uploader').grid.cleanData();
                    }
                },
                deleteFormatter : function(row) {
                    return row.ext$.equiptcode;
                },
                toolbar : _this.getGridButton(),
            }
        },

        getGridButton : function() {
            var _this = this;
            if (pageParam.type === 'rent') {
                return [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add',
                    onClick : function() {
                        Gikam.preInsert({
                            modalTitle : 'EMC.MODULE.MDM.BASEMDM.EQUIPTREQUISITIONS.MODAL.ADD',
                            fields : emcEquiptRequisition.getRentColumns(),
                            url : emcEquiptRequisition.baseUrl,
                            onFormRendered : function(form) {
                                this.removeFieldValidator( 'remark' , ['notEmpty'] )
                            }
                        }).done(function(id) {
                            Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).refresh();
                        });
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove',
                    onClick : function() {
                        Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).deleteRows(emcEquiptRequisition.baseUrl);
                    }
                }, {
                    type : 'button',
                    text : 'EMC.MODULE.MDM.BASEMDM.EQUIPTREQUISITIONS.BUTTON.BATCHRENTSTATUS',
                    icon : 'edit',
                    onClick : function () {
                        _this.batchChangeRentStatus();
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.PREVIEW',
                    icon : 'print',
                    onClick : function() {
                        Gikam.viewReport('EQREQUISITION', Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).getSelections());
                    }
                } ]
            } else if (pageParam.type === 'back') {
                return [ {
                    //确认归还
                    type : 'button',
                    text : 'EMC.MODULE.MDM.BASEMDM.EQUIPTREQUISITIONS.BUTTON.CONFIRMBACK',
                    icon : 'pass',
                    onClick : function () {
                        _this.confirmBack();
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.PREVIEW',
                    icon : 'print',
                    onClick : function() {
                        Gikam.viewReport('EQREQUISITION', Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).getSelections());
                    }
                }/*, {
                    type : 'button',
                    text : 'EMC.MODULE.MDM.BASEMDM.EQUIPTREQUISITIONS.BUTTON.BATCHBACKSTATUS',
                    icon : 'edit',
                    onClick : function () {
                        _this.batchChangeBackStatus();
                    }
                }*/]
            } else {
                return [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.PREVIEW',
                    icon : 'print',
                    onClick : function() {
                        Gikam.viewReport('EQREQUISITION', Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).getSelections());
                    }
                } ]
            }
        },

        batchChangeRentStatus : function() {
            var selections = Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).getSelections();
            if (selections.length < 1) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                return;
            }
            var ids = selections.map(row => row.id).join();
            Gikam.preInsert({
                modalTitle : 'EMC.MODULE.MDM.BASEMDM.EQUIPTREQUISITIONS.MODAL.BATCHCHANGESTATUS',
                fields : emcEquiptRequisition.getBatchChangeRentStatusColumns(),
                url : Gikam.printf(emcEquiptRequisition.baseUrl + '/{ids}/rentStatus', {
                    ids : ids
                })
            }).done(function(id) {
                Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).refresh();
            });
        },

        batchChangeBackStatus : function() {
            var selections = Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).getSelections();
            if (selections.length < 1) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                return;
            }
            var ids = selections.map(row => row.id).join();
            Gikam.preInsert({
                modalTitle : 'EMC.MODULE.MDM.BASEMDM.EQUIPTREQUISITIONS.MODAL.BATCHCHANGESTATUS',
                fields : emcEquiptRequisition.getBatchChangeBackStatusColumns(),
                url : Gikam.printf(emcEquiptRequisition.baseUrl + '/{ids}/backStatus', {
                    ids : ids
                })
            }).done(function(id) {
                Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).refresh();
            });
        },

        confirmBack : function() {
            var selections = Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).getSelections();
            if (selections.length < 1) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                return;
            }
            var flag = true;
            selections.forEach(function (item) {
                if (item.isBack === '1'){
                    flag = false;
                }
            });
            var ids = selections.map(row => row.id).join();
            if (flag){
                Gikam.preInsert({
                    modalTitle : 'EMC.MODULE.MDM.BASEMDM.EQUIPTREQUISITIONS.MODAL.CONFIRMBACK',
                    fields : emcEquiptRequisition.getBackColumns(),
                    url : Gikam.printf(emcEquiptRequisition.baseUrl + '/{ids}/confirmBack',{
                        ids : ids
                    })
                }).done(function(id) {
                    Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).refresh();
                });
            }else {
                Gikam.alert('T_EMC_EQUIPT_REQUISITION.EQUIPT_HASBACK');
            }

        },

        getAttachmentGridParam : function() {
            var _this = this;
            return {
                type : 'uploader',
                id : 'emc-equipt-requisition-edit-list-uploader',
                dbTable : 'T_EMC_EQUIPT_REQUISITION',
            }
        },

        create : function() {
            var _this = this;
            Gikam.create('layout', {
                id : 'emc-equipt-requisition-edit-list-layout' + pageType,
                renderTo : workspace.window.$dom,
                center : {
                    items : [ this.getGridParam() ]
                },
                south : {
                    height : 300,
                    items : [ {
                        type : 'tab',
                        fill : true,
                        panels : [ {
                            title : 'EMC.MODULE.MDM.BASEMDM.EQUIPTREQUISITIONS.TAB.ATTACHMENT',
                            items : [ _this.getAttachmentGridParam() ]
                        } ]
                    } ]
                }
            });
        },

        init : function() {
            this.create();
        }
    }
};
