emcTestMethod.testEditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-test-edit-list-grid',
            url : emcTestMethod.baseUrl + '/tests/queries/raw',
            columns : emcTestMethod.getTestEditListColumns(),
            service : 'emcTestServiceImpl',
            page : true,
            deleteFormatter : function(row) {
                return row.testName || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BIZMDM.TESTS.TAB.ADD_TEST',
                        fields : emcTestMethod.getTestEditListColumns(),
                        url : emcTestMethod.baseUrl + '/tests'
                    }).done(function(id) {
                        Gikam.getComp('emc-test-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    var selections = Gikam.getComp('emc-test-edit-list-grid').getSelections();
                    if (selections.length > 0) {
                        var url = emcTestMethod.baseUrl + '/tests/action/check-used'
                        Gikam.post(url, Gikam.getJsonWrapper({}, [ '', selections ])).done(function(data) {
                            if (!Gikam.isEmpty(data)) {
                                Gikam.confirm('GIKAM.TIP.CONFIRM_TO_DELETE', 'EMC.MODULE.MDM.BIZMDM.TESTS.TIP.TEST_CHECK_USED', function() {
                                    Gikam.getComp('emc-test-edit-list-grid').deleteRows(emcTestMethod.baseUrl + '/tests');
                                })
                            } else {
                                Gikam.getComp('emc-test-edit-list-grid').deleteRows(emcTestMethod.baseUrl + '/tests');
                            }
                        });
                    } else {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                    }
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.EXCEL_EXPORT',
                icon : 'edit',
                onClick : function() {
                    Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}', {
                        templateCode : 'emcTestExportTemplate',
                        service : 'emcTestExportTemplate'
                    })).done(function(fileUri) {
                        var fileUrl = IFM_CONTEXT + fileUri;
                        Gikam.emcDownloadFile(fileUrl);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.EXCEL_IMPORT',
                icon : 'edit',
                onClick : function() {
                    Gikam.importExcel({
                        url : IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcTestImportFileFunction',
                        dbTable : 'T_EMC_TEST',
                        onImportSuccess : function(files) {
                            Gikam.getComp('emc-test-edit-list-grid').refresh();
                        }
                    });
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-test-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
