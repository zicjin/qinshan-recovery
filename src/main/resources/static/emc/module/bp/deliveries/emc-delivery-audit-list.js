emcDelivery.auditSearchListPage = {

    getProjectGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-delivery-project-audit-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/project/projects/queries',
            columns : emcDelivery.getProjectListColumns(),
            filterOpen : true,
            page : true,
            requestData : {
                targetType: 'deliveryAudit',
                processStatus_SEQ : 'done',
                orgId_SB : workspace.user.ext$.parentorgid,
                sample_DIFFER : '1',
            },
            onCellClick : function(field, row) {
                if (field === 'projNo') {
                    _this.loadProjDetailPage(row.projNo);
                }
            },
            onRowActive : function(index, row) {
                if (row) {
                    Gikam.getComp('emc-delivery-audit-list-grid').refresh({
                        requestData : {
                            status_DIFFER : 'draft',
                            orgId : workspace.user.ext$.parentorgid,
                            projId_SEQ : row.id,
                        },
                        url : emcDelivery.sampleUrl + '/queries',
                    });
                }
            },
            onLoadSuccess : function(data) {
                if (Gikam.isEmpty(data)) {
                    Gikam.cleanCompData([ 'emc-delivery-audit-list-grid' ]);
                } else {
                    this.activeRowByIndex(0);
                }
            },
            toolbar : [ {
                type : 'dropDownMenu',
                text : '客服联系文件',
                items : [{
                    type : 'button',
                    text : '导出文件',
                    icon : 'default',
                    onClick : function() {
                        _this.exportSampleFile();
                    },
                },{
                    type : 'button',
                    text : '查看文件',
                    icon : 'upload',
                    onClick : function() {
                        _this.viewExportSampleFile();
                    }
                }]
            } ]
        }
    },

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-delivery-audit-list-grid',
            columns : emcDelivery.getEditListColumns(),
            filterOpen : false,
            page : true,
            checkOnActive : true,
            deleteFormatter : function(row) {
                return row.sampleNo || row.id;
            },
            onRowActive : function(index, row) {
                if (row && !isNaN(row.id)) {
                    Gikam.getComp('emc-delivery-audit-list-order-grid').refresh({
                        requestData : {
                            sampleId_SEQ : row.id,
                        },
                        url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries/raw',
                    });
                }
            },
            onLoadSuccess : function(data) {
                if (Gikam.isEmpty(data)) {
                    Gikam.cleanCompData([ 'emc-delivery-audit-list-order-grid' ]);
                } else {
                    this.activeRowByIndex(0);
                }
            },
            toolbar : [  {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('deliveringLabel', Gikam.getComp('emc-delivery-audit-list-grid').getSelections());
                }
            },{
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.ANNEX',
                icon : 'upload',
                onClick : function() {
                    _this.uploadSampleFile();
                }
            } ],
        }
    },
    uploadSampleFile : function() {
        var _this = this
        var rows = Gikam.getComp('emc-delivery-audit-list-grid').getSelections();
        if (rows && rows.length === 1) {
            Gikam.create('modal', {
                width : 800,
                height : 500,
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ANNEX',
                url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/page/sample-annex-list' + Gikam.param({
                    id : rows[0].id,
                    readonly : true
                })
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },
    /** 加载任务详情页面 */
    loadProjDetailPage : function(projNo) {
        Gikam.create('modal', {
            id : 'projModal',
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.MODAL.PROJ_DETAIL',
            width : 980,
            height : 500,
            showCloseBtn : true,
            url : IFM_CONTEXT + '/secure/emc/module/bp/project/projects/page/project-search-detail' + Gikam.param({
                projNo : projNo
            }),
        });
    },

    /** 样品信息 */
    getOrderGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-delivery-audit-list-order-grid',
            columns : emcDelivery.getOrderSearchColumns(),
            service : 'pfOrderServiceImpl',
            readonly : true,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-delivery-audit-list-test-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                if (row && !isNaN(row.id)) {
                    Gikam.getComp('emc-delivery-audit-list-test-grid').refresh({
                        url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/{id}/item-groups/queries', {
                            id : row.id
                        }),
                        requestData : {
                            showOrderContainer : '1'
                        }
                    });
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('deliveringLabel', Gikam.getComp('emc-delivery-audit-list-order-grid').getSelections());
                }
            }]
        }
    },

    /** 测试信息 */
    getResultGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.CONTAINER_INFO',
                items : [{
                    type : 'layout',
                    center :{
                        items : [ {
                            type : 'grid',
                            id : 'emc-delivery-audit-list-test-grid',
                            service : 'emcOrderTaskServiceImpl',
                            columns : emcDelivery.getOrderItemGroupColumns(),
                            readonly : true,
                        } ]
                    }
                }]
            } ]
        }
    },

    exportSampleFile : function() {
        var _this = this
        var rows = Gikam.getComp('emc-delivery-project-audit-grid').getSelections();
        if (rows && rows.length === 1) {
            Gikam.create('modal', {
                width : 800,
                height : 500,
                url : Gikam.choose.getConfig('reportLine').url+ Gikam.param({
                    REPORTCONFIGID_IN : 'deliveringLabel',
                    activatedFlag_SEQ :'1'
                }),
                title : '报表',
                onAfterClose : function(data) {
                    var url1 = Gikam.printf(emcDelivery.projectUrl+'/action/sample-contract-file');
                    var reportConfigId = data.reportConfigId;
                    Gikam.postText(url1, Gikam.getJsonWrapper(null,['coreReportConfigLineService',data] ,[ 'pfProjectService', rows ])).done(function(data){
                        console.log(data);
                        data   =   data.replace(/\s+/g,"");
                        var fileIds = [];
                        var selections = data.split(",");
                        var ro = selections.filter(function (s) {
                            return s && s.trim();
                        });
                        console.log(selections);
                        console.log(ro);
                        if (ro.length > 0) {
                            ro.forEach(function (item) {
                                fileIds.push({id : item})
                            })
                            console.log(fileIds);
                            if(fileIds.length>1){
                                Gikam.postText('/core/module/item/files/action/pack',Gikam.getJsonWrapper({}, [ '', fileIds])
                                ).done(function (fileUrl) {
                                    if (fileUrl != '' && fileUrl != undefined) {
                                        Gikam.download(IFM_CONTEXT + fileUrl);
                                    }
                                    Gikam.alert('导出完成！');
                                })
                            }else if (fileIds.length == 1){
                                Gikam.getJson(IFM_CONTEXT + '/core/module/item/files/' + selections[0]).done(function(coreFile) {
                                    Gikam.download(IFM_CONTEXT + coreFile.downloadUrl);
                                });
                                Gikam.alert('导出完成！');
                            }

                        }else{
                            Gikam.alert('导出失败！');
                        }
                        Gikam.getComp('emc-delivery-project-audit-grid').refresh();
                    });
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }

    },

    viewExportSampleFile : function() {
        var _this = this
        var rows = Gikam.getComp('emc-delivery-project-audit-grid').getSelections();
        if (rows && rows.length === 1) {
            Gikam.create('modal', {
                width : 800,
                height : 500,
                title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.ANNEX',
                url : emcDelivery.projectUrl + '/page/deliver-annex-list?id=' + rows[0].id
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },


    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-delivery-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 300,
                items : [ _this.getProjectGridParam() ]
            },
            center : {
                items : [ _this.getGridParam() ]
            },
            east : {
                width : '50%',
                title : Gikam.propI18N('EMC.MODULE.BP.SAMPLE.SAMPLINGPLANS.TITLE.ORDER_INFO'),
                collapse : false,
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ this.getOrderGridParam() ]
                    },
                    center : {
                        items : [ this.getResultGridParam() ]
                    }
                } ]
            }
        });
        Gikam.getAsyncComp('emc-delivery-audit-list-test-grid').done(function(grid) {
            grid.options.checkOneOnActive = true;
        });
    },

    init : function() {
        this.create();
    }
};
