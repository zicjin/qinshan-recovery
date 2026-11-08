emcCert.editPage = {

    empGrid : null,
    ready : false,
    certActivationVersion : 0,
    getEmpGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-cert-edit-list-emp-grid',
            url : emcCert.empBaseUrl + '/queries/raw',
            columns : emcCert.getEmpEditListColumns(),
            page : true,
            onRendered : function() {
                _this.empGrid = this;
            },
            onLoadSuccess : function(data) {
                if (!_this.ready) {
                    _this.ready = true;
                    return;
                }
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }
            },
            onRowActive : function(index, row) {
                _this.onEmpActive(row);
            },
	        requestData : {
	            org_filter : '1',
	        },            
        };
    },

    getGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.CERTS.TAB.BASE_INFO',
                items : [ {
                    type : 'grid',
                    id : 'emc-cert-edit-list-grid',
                    service : 'emcCertServiceImpl',
                    url : '',
                    columns : emcCert.getEditListColumns(),
                    onRendered : function() {
                        if (!_this.ready) {
                            _this.ready = true;
                            return;
                        }
                        if (!Gikam.isEmpty(_this.empGrid.getData())) {
                            _this.empGrid.activeRowByIndex(0);
                        }
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        }
                    },
                    deleteFormatter : function(row) {
                        return row.certNo || row.id;
                    },
                    onRowActive : function(index, row) {
                        _this.onEmpCertActive(row);
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var activedRow = Gikam.getComp('emc-cert-edit-list-emp-grid').getActivedRow();
                            var url = Gikam.printf(emcCert.baseUrl + '/{id}', {
                                id : activedRow.id
                            });
                            Gikam.getComp('emc-cert-edit-list-grid').insert(url, [ {} ]);
                        },
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            var selections = Gikam.getComp('emc-cert-edit-list-grid').getSelections();
                            if (Gikam.isEmpty(selections)) {
                                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                                return;
                            }
                            var url = Gikam.printf(emcCert.baseUrl + '/certs');
                            Gikam.getComp('emc-cert-edit-list-grid').deleteRows(url);
                        }
                    // }, {
                    //     type : 'button',
                    //     text : 'GIKAM.BUTTON.AUTH',
                    //     icon : 'add',
                    //     onClick : function() {
                    //         var selections = Gikam.getComp('emc-cert-edit-list-grid').getSelections();
                    //         if (Gikam.isEmpty(selections) || selections.length > 1) {
                    //             Gikam.alert('EMC.MODULE.MDM.BASEMDM.CERTS.AUTH_CHOOSE_ONLY_ONE');
                    //             return;
                    //         }
                    //         var status = selections[0].status;
                    //         if (status === 'auth') {
                    //             Gikam.alert('EMC.MODULE.MDM.BASEMDM.CERTS.AUTH_ONLY_ONE');
                    //             return;
                    //         }
                    //         if (Gikam.isEmpty(selections[0].certNo) || Gikam.isEmpty(selections[0].certName) || Gikam.isEmpty(selections[0].authDate) || Gikam.isEmpty(selections[0].expireDate) || Gikam.isEmpty(selections[0].warningDays)) {
                    //             Gikam.alert('EMC.MODULE.MDM.BASEMDM.CERTS.AUTH_NOT_EMPTY');
                    //             return;
                    //         }
                    //         var url = Gikam.printf(emcCert.baseUrl + '/{id}/cert-method-auth', {
                    //             id : selections[0].id
                    //         });
                    //         Gikam.getComp('emc-cert-edit-list-grid').insert(url, [ {} ]);
                    //     },
                    } ]
                } ]
            } ]
        }
    },

    getOtherGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.CRETTESTMETHODS.TAB.BASE_INFO',
                items : [ {
                    type : 'grid',
                    id : 'emc-cert-method-edit-list-grid',
                    service : 'emcCertTestMethodServiceImpl',
                    url : _this.getCertTestMethodUrl(),
                    columns : emcCert.getCertTestMethodColumns(),
                    deleteFormatter : function(row) {
                        return row.ext$.testname || row.id;
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        }
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertCertTestMethod();
                        },
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            var url = Gikam.printf(emcCert.baseUrl + '/cert-test-methods');
                            Gikam.getComp('emc-cert-method-edit-list-grid').deleteRows(url);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.COPYTO',
                        icon : 'copy',
                        onClick : function() {
                            _this.copyCertTestMethod();
                        }
                    } ]
                }, ]
            }, {
            //     title : 'EMC.MODULE.MDM.BASEMDM.CERTMONITORABILITIES.TAB.BASE_INFO',
            //     items : [ {
            //         type : 'grid',
            //         id : 'emc-cert-monitor-ability-edit-list-grid',
            //         url : _this.getCertMonitorAbilityUrl(),
            //         columns : emcCert.getCertMonitorAbilityColumns(),
            //         service : 'emcCertMonitorAbilityServiceImpl',
            //         deleteFormatter : function(row) {
            //             return row.ext$.monitorobjname || row.id;
            //         },
            //         onLoadSuccess : function(data) {
            //             if (!Gikam.isEmpty(data)) {
            //                 this.activeRowByIndex(0);
            //             }
            //         },
            //         toolbar : [ {
            //             type : 'button',
            //             text : 'GIKAM.BUTTON.INSERT',
            //             icon : 'add',
            //             onClick : function() {
            //                 _this.insertCertMonitorAbility();
            //             }
            //         }, {
            //             type : 'button',
            //             text : 'GIKAM.BUTTON.DELETE',
            //             icon : 'remove-row',
            //             onClick : function() {
            //                 var url = Gikam.printf(emcCert.baseUrl + '/cert-monitor-abilities');
            //                 Gikam.getComp('emc-cert-monitor-ability-edit-list-grid').deleteRows(url);
            //             }
            //         } ]
            //     }, ]
            // }, {
            //     title : 'EMC.MODULE.MDM.BASEMDM.CERTLOGS.TAB.BASE_INFO',
            //     items : [ {
            //         type : 'grid',
            //         id : 'emc-cert-log-list-grid',
            //         service : 'emcCertLogServiceImpl',
            //         url : _this.getCertLogUrl(),
            //         columns : emcCert.getCertLogColumns(),
            //     } ]
            // }, {
                title : 'EMC.MODULE.MDM.BASEMDM.CERTFILE.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-cert-detail-uploader',
                    filter : false,
                    dbTable : 'T_EMC_CERT',
                    onBeforeUpload : function() {
                        this.setUploadData({})
                    }
                } ]
            }, ]
        }
    },

    onEmpActive : function(row) {
        var _this = this;
        _this.certActivationVersion++;
        if (row) {
            Gikam.getComp('emc-cert-edit-list-grid').refresh({
                url : _this.getEmpCertUrl(row.id),
            });
            if (Gikam.getComp('emc-cert-method-edit-list-grid')) {
                Gikam.getComp('emc-cert-method-edit-list-grid').cleanData();
            }
            if (Gikam.getComp('emc-cert-monitor-ability-edit-list-grid')) {
                Gikam.getComp('emc-cert-monitor-ability-edit-list-grid').cleanData();
            }
            if (Gikam.getComp('emc-cert-log-list-grid')) {
                Gikam.getComp('emc-cert-log-list-grid').cleanData();
            }
        }
    },

    onEmpCertActive : function(row) {
        var _this = this;
        var activationVersion = ++_this.certActivationVersion;
        if (row) {
            Gikam.getAsyncComp('emc-cert-method-edit-list-grid').done(function(grid) {
                if (activationVersion !== _this.certActivationVersion) {
                    return;
                }
                grid.refresh({
                    url : _this.getCertTestMethodUrl(row.id),
                });
            });
            Gikam.getAsyncComp('emc-cert-detail-uploader').done(function(uploader) {
                if (activationVersion !== _this.certActivationVersion) {
                    return;
                }
                uploader.setOptions({
                    bizId : row.id
                });
            });
        }
    },

    getEmpCertUrl : function(empId) {
        if (empId) {
            return Gikam.printf(emcCert.baseUrl + '/{id}/queries', {
                id : empId
            })
        }
    },

    getCertTestMethodUrl : function(certId) {
        if (certId) {
            return Gikam.printf(emcCert.baseUrl + '/{id}/cert-test-methods/queries', {
                id : certId
            })
        }
    },

    getCertMonitorAbilityUrl : function(certId) {
        if (certId) {
            return Gikam.printf(emcCert.baseUrl + '/{id}/cert-monitor-abilities/queries', {
                id : certId
            })
        }
    },

    getCertLogUrl : function(certId) {
        if (certId) {
            return Gikam.printf(emcCert.baseUrl + '/{id}/cert-logs/queries', {
                id : certId
            })
        }
    },

    insertCertTestMethod : function() {
        var _this = emcCert.editPage;

        var activedRow = Gikam.getComp('emc-cert-edit-list-grid').getActivedRow();
        if (!activedRow) {
            return;
        }

        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BASEMDM.CRETTESTMETHODS.TAB.BASE_INFO',
            width : 550,
            url : Gikam.choose.getConfig('testCapacity').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_CERT_TEST_METHOD',
                targetTestMethodIdColumn : 'TESTMETHODID',
                targetFilterColumn1 : 'CERTID',
                targetFilterValue1 : activedRow.id,
            }),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var url = Gikam.printf(emcCert.baseUrl + '/{id}/cert-test-methods', {
                    id : activedRow.id,
                });
                workspace.window.showMask(true);
                Gikam.put(url, Gikam.getJsonWrapper({}, [ null, rows ])).done(function(data) {
                    Gikam.getComp('emc-cert-method-edit-list-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            },
        });
    },

    copyCertTestMethod : function() {
        var _this = emcCert.editPage;

        // 方法内容
        var selections = Gikam.getComp('emc-cert-method-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var activedRow = Gikam.getComp('emc-cert-edit-list-emp-grid').getActivedRow();
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BASEMDM.CERTS.MODAL.CHOOSECERT',
            width : 790,
            url : Gikam.choose.getConfig('certTestMethod').url + Gikam.param({
                empId_DIFFER : activedRow.id
            }),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                workspace.window.showMask(true);
                Gikam.postText(emcCert.baseUrl + '/test-methods/action/copy', Gikam.getJsonWrapper({}, [ 'emcCertServiceImpl', rows ], [ 'emcCertTestMethodServiceImpl', selections ])).done(function(str) {
                    Gikam.alert('EMC.MODULE.MDM.BASEMDM.CERTS.TIP.COPY_SUCCESS');
                }).always(function() {
                    workspace.window.closeMask();
                });
            },
        });
    },

    insertCertMonitorAbility : function() {
        var _this = emcCert.editPage;
        var activedRow = Gikam.getComp('emc-cert-edit-list-grid').getActivedRow();

        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BASEMDM.CERTMONITORABILITIES.BASE_INFO',
            url : Gikam.choose.getConfig('monitorAbility').url + Gikam.param({
                targetCert_unassigned : '1',
                targetTable : 'T_EMC_CERT_MONITOR_ABILITY',
                targetCertMonitorAbilityColumn : 'MONOTORABILITYID',
                targetFilterColumn2 : 'CERTID',
                targetFilterValue2 : activedRow.id,
            }),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var url = Gikam.printf(emcCert.baseUrl + '/{certId}/cert-monitor-abilities', {
                    certId : activedRow.id,
                });
                workspace.window.showMask(true);
                Gikam.put(url, Gikam.getJsonWrapper({}, [ null, rows ])).done(function(data) {
                    Gikam.getComp('emc-cert-monitor-ability-edit-list-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            },
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-cert-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '30%',
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.MDM.BASEMDM.EMPS.TAB.BASE_INFO',
                        items : [ _this.getEmpGridParam() ]
                    } ]
                } ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ this.getGridParam() ]
                    },
                    center : {
                        items : [ this.getOtherGridParam() ]
                    }
                } ]
            },
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(emcCert.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
