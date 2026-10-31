emcEquiptRequisition.editPage = function (pageParam) {
    var pageType = '-' + pageParam.type;
    return {
        getGridParam: function () {
            var _this = this;
            var requestData = {};
            if (pageType === '-rent') {
                requestData.isBack_SEQ = '0';
                requestData.rentUserId_SEQ = workspace.user.id;
            }
            if (pageType === '-back') {
                // requestData.isBack_SEQ = '0';
                requestData.rentUserId_SEQ = workspace.user.id;
                requestData.backUser_IN = '1,';
            }
            if (pageType === '-search') {
                requestData.isBack_SEQ = '0';
            }
            return {
                type: 'grid',
                name : 'QSLS-ZL09-26-2025仪器设备出入库及使用过程性能核查记录表（LIMS专用）',
                id: 'emc-equipt-requisition-edit-list-grid' + pageType,
                url: emcEquiptRequisition.baseUrl + '/queries/raw',
                service: 'emcEquiptRequisitionServiceImpl',
                columns: emcEquiptRequisition.getEditListColumns(),
                page: true,
                requestData: requestData,
                onRowActive: function (index, row) {
                    Gikam.getAsyncComp('emc-equipt-requisition-edit-list-grid' + pageType).done(function (grid) {
                        Gikam.getComp('emc-equipt-requisition-edit-list-uploader').setOptions({
                            bizId: row.equiptId,
                        });
                    });
                },
                onLoadSuccess: function (data) {
                    if (!Gikam.isEmpty(data)) {
                        Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).activeRowByIndex(0);
                    } else if (Gikam.getComp('emc-equipt-requisition-edit-list-uploader')) {
                        Gikam.getComp('emc-equipt-requisition-edit-list-uploader').grid.cleanData();
                    }
                },
                deleteFormatter: function (row) {
                    return row.ext$.equiptcode;
                },
                toolbar: _this.getGridButton(),
            }
        },

        getGridButton: function () {
            var _this = this;
            if (pageParam.type === 'rent') {
                return [{
                    type: 'button',
                    text: 'GIKAM.BUTTON.INSERT',
                    icon: 'add',
                    onClick: function () {
                        Gikam.preInsert({
                            modalTitle: 'EMC.MODULE.MDM.BASEMDM.EQUIPTREQUISITIONS.MODAL.ADD',
                            fields: emcEquiptRequisition.getRentColumns(),
                            url: emcEquiptRequisition.baseUrl,
                            displayReadonly: false,
                            onFormRendered: function (form) {
                                this.removeFieldValidator('remark', ['notEmpty'])
                            }
                        }).done(function (id) {
                            Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).refresh();
                        });
                    }
                }, {
                    type: 'button',
                    text: 'GIKAM.BUTTON.DELETE',
                    icon: 'remove',
                    onClick: function () {
                        Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).deleteRows(emcEquiptRequisition.baseUrl);
                    }
                }, {
                    type: 'button',
                    text: 'EMC.MODULE.MDM.BASEMDM.EQUIPTREQUISITIONS.BUTTON.BATCHRENTSTATUS',
                    icon: 'edit',
                    onClick: function () {
                        _this.batchChangeRentStatus();
                    }
                }, {
                    type: 'button',
                    text: 'GIKAM.BUTTON.PREVIEW',
                    icon: 'print',
                    onClick: function () {
                        Gikam.viewReport('EQREQUISITION', Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).getSelections());
                    }
                }]
            } else if (pageParam.type === 'back') {
                return [{
                    //确认归还
                    type: 'button',
                    text: '确认结束',
                    icon: 'pass',
                    onClick: function () {
                        _this.confirmBack();
                    }
                }, {
                    type: 'button',
                    text: 'GIKAM.BUTTON.PREVIEW',
                    icon: 'print',
                    onClick: function () {
                        Gikam.viewReport('EQREQUISITION', Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).getSelections());
                    }
                }, {
                    type: 'form',
                    id: 'emc-run-edit-list-form',
                    fields: emcEquiptRequisition.getBackShowAllField(),
                    autoSave: false,
                }/*, {
                    type : 'button',
                    text : 'EMC.MODULE.MDM.BASEMDM.EQUIPTREQUISITIONS.BUTTON.BATCHBACKSTATUS',
                    icon : 'edit',
                    onClick : function () {
                        _this.batchChangeBackStatus();
                    }
                }*/]
            } else {
                return [{
                    type: 'button',
                    text: 'GIKAM.BUTTON.PREVIEW',
                    icon: 'print',
                    onClick: function () {
                        Gikam.viewReport('EQREQUISITION', Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).getSelections());
                    }
                }, {
                    type: 'form',
                    id: 'emc-run-edit-list-form',
                    fields: emcEquiptRequisition.getShowAllField(),
                    autoSave: false,
                }, {
                    type: 'button',
                    text: '确认',
                    icon: 'pass',
                    onClick: function () {
                        _this.confirm();
                    }
                }]
            }
        },

        confirm: function () {
            var selections = Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).getSelections();
            if (selections.length < 1) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                return;
            }
            var updateData = selections.map(item => {
                return {
                    id: item.id,
                    confirmUserId: workspace.user.id,
                    confirmUserName: workspace.user.userName
                }
            });

            workspace.window.showMask(true);

            Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, ['emcEquiptRequisitionServiceImpl', updateData])).done(function () {
                Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).refresh();
            }).always(function () {
                workspace.window.closeMask();
                modal.close();
            });
        },

        batchChangeRentStatus: function () {
            var selections = Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).getSelections();
            if (selections.length < 1) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                return;
            }
            var ids = selections.map(row => row.id).join();
            Gikam.preInsert({
                modalTitle: 'EMC.MODULE.MDM.BASEMDM.EQUIPTREQUISITIONS.MODAL.BATCHCHANGESTATUS',
                fields: emcEquiptRequisition.getBatchChangeRentStatusColumns(),
                url: Gikam.printf(emcEquiptRequisition.baseUrl + '/{ids}/rentStatus', {
                    ids: ids
                })
            }).done(function (id) {
                Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).refresh();
            });
        },

        batchChangeBackStatus: function () {
            var selections = Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).getSelections();
            if (selections.length < 1) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                return;
            }
            var ids = selections.map(row => row.id).join();
            Gikam.preInsert({
                modalTitle: 'EMC.MODULE.MDM.BASEMDM.EQUIPTREQUISITIONS.MODAL.BATCHCHANGESTATUS',
                fields: emcEquiptRequisition.getBatchChangeBackStatusColumns(),
                url: Gikam.printf(emcEquiptRequisition.baseUrl + '/{ids}/backStatus', {
                    ids: ids
                })
            }).done(function (id) {
                Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).refresh();
            });
        },

        confirmBack: function () {
            var selections = Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).getSelections();
            if (selections.length < 1) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                return;
            }
            var flag = true;
            selections.forEach(function (item) {
                if (item.isBack === '1') {
                    flag = false;
                }
            });
            var ids = selections.map(row => row.id).join();
            if (!flag) {
                Gikam.alert('T_EMC_EQUIPT_REQUISITION.EQUIPT_HASBACK');
                return;
            }

            // var flag2 = true;
            // selections.forEach(function (item) {
            //     if (Gikam.isEmpty(item.rentDate) || Gikam.isEmpty(item.backDate) || Gikam.isEmpty(item.rentTime)|| Gikam.isEmpty(item.backTime)) {
            //         flag2 = false;
            //     }
            // });
            // if (!flag2) {
            //     Gikam.alert('开始或结束日期时间都不能为空');
            //     return;
            // }

            // var flag3 = true;
            // selections.forEach(function (item) {
            //     // 1. 读取控件值
            //     const sd = item.rentDate; // yyyy-mm-dd
            //     const st = item.rentTime; // hh:mm
            //     const ed = item.backDate;
            //     const et = item.backTime;
            //     // 2. 拼接成 ISO 格式字符串，直接 new Date
            //     const start = new Date(`${sd}T${st}:00`);
            //     const end   = new Date(`${ed}T${et}:00`);
            //     if (start >= end) {
            //         flag3 = false;
            //     }
            // });
            // if (!flag3) {
            //     Gikam.alert('开始使用日期时间必须早于结束使用日期时间，请重新填写！');
            //     return;
            // }

            var modal = Gikam.create('modal', {
                title: "仪器结束使用",
                width : 400,
                height : 350,
            });
            var layout = Gikam.create('layout', {
                renderTo: modal.window.$dom,
                center: {
                    items: [ {
                        type: 'btnToolbar',
                        items: [ {
                            type : 'button',
                            text : 'GIKAM.BUTTON.CONFIRM',
                            class : 'blue',
                            onClick : function() {
                                if (form.validate()) {
                                    let formData = form.options.data;
                                    const ed = formData.backDate;
                                    const et = formData.backTime;
                                    const end   = new Date(`${ed}T${et}:00`);

                                    var flag3 = true;
                                    selections.forEach(function (item) {
                                        // 1. 读取控件值
                                        const sd = item.rentDate; // yyyy-mm-dd
                                        const st = item.rentTime; // hh:mm
                                        // 2. 拼接成 ISO 格式字符串，直接 new Date
                                        const start = new Date(`${sd}T${st}:00`);
                                        if (start >= end) {
                                            flag3 = false;
                                        }
                                    });
                                    if (!flag3) {
                                        Gikam.alert('开始使用日期时间必须早于结束使用日期时间，请重新填写！');
                                        return;
                                    }
                                    Gikam.getLastModal().window.showMask(true);
                                    Gikam.post(Gikam.printf(emcEquiptRequisition.baseUrl + '/{ids}/confirmBack', {
                                        ids: ids
                                    }), Gikam.getJsonWrapper({}, [ '', [formData] ])).done(function() {
                                        Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).refresh();
                                    }).always(function() {
                                        Gikam.getLastModal().window.closeMask();
                                        modal.close();
                                    });
                                }
                            }
                        }, {
                            type : 'button',
                            text : 'GIKAM.BUTTON.CANCEL',
                            onClick : function () {
                                modal.close();
                            }
                        } ]
                    }]
                }
            });

            var form = Gikam.create('form', {
                renderTo: layout.options.center.$dom,
                columns: 1,
                titleWidth: 80,
                fields: emcEquiptRequisition.getBackColumns(),
                autoSave: false
            });
            // Gikam.preInsert({
            //     modalTitle: '仪器结束使用',
            //     fields: emcEquiptRequisition.getBackColumns(),
            //     url: Gikam.printf(emcEquiptRequisition.baseUrl + '/{ids}/confirmBack', {
            //         ids: ids
            //     })
            // }).done(function (id) {
            //     Gikam.getComp('emc-equipt-requisition-edit-list-grid' + pageType).refresh();
            // });
        },

        getAttachmentGridParam: function () {
            var _this = this;
            return {
                type: 'uploader',
                id: 'emc-equipt-requisition-edit-list-uploader',
                dbTable: 'T_EMC_EQUIPT',
                readonly: true,
            }
        },

        create: function () {
            var _this = this;
            Gikam.create('layout', {
                id: 'emc-equipt-requisition-edit-list-layout' + pageType,
                renderTo: workspace.window.$dom,
                center: {
                    items: [this.getGridParam()]
                },
                south: {
                    height: 300,
                    items: [{
                        type: 'tab',
                        fill: true,
                        panels: [{
                            title: 'EMC.MODULE.MDM.BASEMDM.EQUIPTREQUISITIONS.TAB.ATTACHMENT',
                            items: [_this.getAttachmentGridParam()]
                        }]
                    }]
                }
            });
        },

        init: function () {
            this.create();
        }
    }
};
