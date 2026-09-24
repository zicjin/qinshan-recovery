crmOpportunityContract.searchPage = {

    getContractGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-contract-search-list-grid',
            url : crmOpportunityContract.baseUrl + '/queries',
            columns : crmOpportunityContract.getOpportunityContractGridColumns(),
            requestData : {
                corp_filter : '1',
            },
            order : {
                'contractCommencementDate' :'desc',
                'ext$.implementationtimes' : 'desc',
                'contractCommencementDate' : 'desc'
            },
            onBeforeRefresh : function (requestData) {
                var orgName = workspace.user.orgName;
                if(orgName.indexOf("质量部") >-1){
                    requestData['REQSUBTYP_SE'] = 'Z';
                    requestData['REQSUBTYP_SB'] = 'Z';
                    requestData['REQSUBTYP_SC'] = 'Z';
                    requestData['REQSUBTYP_SEQ'] = 'Z';
                }
            },
            service : 'crmOpportunityContractServiceImpl',
            readonly : true,
            page : true,
            filterOpen : true,
            onCellDbClick : function(field, row) {
                _this.viewContractDetail(row);
            },
            rowStyleFormatter: function (row) {
                if (row.financialFlag === '1') {
                    return {
                        backgroundColor: 'yellow'
                    }
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.MDM.CRMS.BUTTON.VIEW_CONTRACT_DETAIL',
                icon : 'browse',
                onClick : function() {
                    _this.viewContractDetail();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.MDM.CRMS.BUTTON.CREATE_PROJECT',
                icon : 'add',
                onClick : function() {
                    _this.createProject();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.MDM.CRMS.BUTTON.SYNC_CONTRACT_FILE',
                icon : 'refresh',
                onClick : function() {
                    _this.syncContractFile();
                }
            } ]
        }
    },

    /** 查看合同明细 */
    viewContractDetail : function(row) {
        var selectedRow = row;
        if (!selectedRow) {
            var selections = Gikam.getComp('emc-contract-search-list-grid').getSelections();
            if (selections.length !== 1) {
                Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                return;
            }
            selectedRow = selections[0];
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.CRMS.TITLE.CONTRACT_DETAIL',
            width : '100%',
            height : '100%',
            url : crmOpportunityContract.baseUrl + '/page/detail'+ Gikam.param({
                opportunityId : selectedRow.opportunityId,
                id : selectedRow.id,
                opportunityNo: selectedRow.ext$.opportunityno
            }),
        });
    },

    /** 创建任务 */
    createProject : function() {
        var selections = Gikam.getComp('emc-contract-search-list-grid').getSelections();
        console.log(selections[0]);
        if (selections.length !== 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.CRMS.TITLE.DETECT_ORDER',
            width : '90%',
            height : '90%',
            url : crmOpportunityContract.baseUrl + '/page/order-choose-list' + Gikam.param({
                opportunityNo_SEQ : selections[0].ext$.opportunityno,
                bizCategoryCode : selections[0].ext$.reqsubtyp,
                sampleSource : selections[0].ext$.samplesource,
                CRMMAINID_SEQ : selections[0].opportunityId
            }),
            onAfterClose : function(data, index) {
                if (Gikam.isEmpty(data)) {
                    return;
                }

                workspace.window.showMask(true);
                Gikam.postText(crmOpportunityContract.baseUrl + '/action/create-project', Gikam.getJsonWrapper({
                    bizCategoryId : data.bizCategoryId,
                    sampleSource : data.sampleSource,
                    opportunityId : selections[0].opportunityId
                }, [ 'crmOpportunityOrdersServiceImpl', data.orders ], [ 'crmOpportunityOrdtaskServiceImpl', data.orderTasks])).done(function () {
                    // 跳转项目登记菜单
                    window.workspace.activeNodeById(150001);
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        });
    },

    syncContractFile : function() {
        var selections = Gikam.getComp('emc-contract-search-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.confirm('GIKAM.TIP.CONFIRM', 'EMC.MODULE.MDM.CRMS.TIP.CONFIRM_SYNC_CONTRACT_FILE', function() {
            workspace.window.showMask(true);
            Gikam.postText(crmOpportunityContract.baseUrl + '/action/sync-contract-file', Gikam.getJsonWrapper(null, [ '', selections ])).done(function () {
                Gikam.alert('EMC.MODULE.MDM.CRMS.TIP.SYNC_CONTRACT_FILE_SUCCESS');
            }).always(function () {
                workspace.window.closeMask();
            });
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-opportunity-contract-search-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getContractGridParam() ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
