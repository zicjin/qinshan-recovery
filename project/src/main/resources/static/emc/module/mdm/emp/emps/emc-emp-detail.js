emcEmp.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'EMC.MODULE.MDM.EMP.EMPS.BUTTON.UPLOAD_ELECTRONICSIGNATURE',
                icon : 'add',
                onClick : function() {
                    _this.uploadElectronicSignature();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.MDM.EMP.EMPS.BUTTON.UPLOAD_PIC',
                icon : 'add',
                onClick : function() {
                    _this.uploadEmpPicture();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_EMC_EMP', _this.param.id);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            panels : [ {
                title : 'EMC.MODULE.MDM.EMP.EMPS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'emc-emp-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'emcEmpServiceImpl',
                    columns : 3,
                    fields : emcEmp.getBaseInfoFormFields()
                } ]
            } ]
        }
    },

    qualGrid : null,
    testGrid : null,
    getOtherGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.XL,
            panels : [ {
                title : 'EMC.MODULE.MDM.EMP.EMPS.TAB.QUAL',
                items : [ {
                    type : 'simpleGrid',
                    id : 'emc-emp-detail-qual-list-grid',
                    url : _this.getCompUrl('qualGrid'),
                    columns : emcEmp.getQualListColumns(),
                    service : 'emcEmpQualServiceImpl',
                    deleteFormatter : function(row) {
                        return row.qualName || row.id;
                    },
                    onRendered : function() {
                        _this.qualGrid = this;
                    },
                    onCellClick : function(field, row) {
                        field === 'ext$.filename' && Gikam.openFile(row.ext$.fileid, true);
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertQualRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteQualRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.FILE.BUTTON.UPLOAD_FILE',
                        icon : 'upload',
                        onClick : function() {
                            _this.uploadQualFile();
                        }
                    } ]
                } ]
            }, {
                title : 'EMC.MODULE.MDM.EMP.EMPS.TAB.WORKLICENSE',
                items : [ {
                    type : 'simpleGrid',
                    id : 'emc-emp-detail-credentials-list-grid',
                    url : _this.getCompUrl('credentialsGrid'),
                    onRendered : function() {
                        _this.credentialsGrid = this;
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.onEmpCertActive(row);
                    },
                    columns : emcEmp.getCredentialsListColumns(),
                }, {
                    type : 'tab',
                    width : Gikam.width.XL,
                    panels : [ {
                        title : 'EMC.MODULE.MDM.EMP.EMPS.TAB.METHOD',
                        items : [ {
                            type : 'simpleGrid',
                            id : 'emc-emp-detail-method-list-grid',
                            service : 'emcCertTestMethodServiceImpl',
                            url : _this.getCertTestMethodUrl(),
                            deleteFormatter : function(row) {
                                return row.ext$.testname || row.id;
                            },
                            onRendered : function() {
                                _this.methodGrid = this;
                            },
                            columns : emcEmp.getMethodListColumns(),
                            toolbar : [ {
                                type : 'button',
                                text : 'GIKAM.BUTTON.INSERT',
                                icon : 'add',
                                onClick : function() {
                                    _this.insertCertTestMethod();
                                }
                            }, {
                                type : 'button',
                                text : 'GIKAM.BUTTON.DELETE',
                                icon : 'remove-row',
                                onClick : function() {
                                    var url = Gikam.printf(emcEmp.baseUrl + '/cert-test-methods');
                                    Gikam.getComp('emc-emp-detail-method-list-grid').deleteRows(url);
                                }
                            } ],
                        } ]
                    } ]
                } ]
            }, {
                title : 'EMC.MODULE.MDM.EMP.EMPS.TAB.TEST',
                items : [ {
                    type : 'simpleGrid',
                    id : 'emc-emp-detail-test-list-grid',
                    url : _this.getCompUrl('testGrid'),
                    checkOnActive : true,
                    deleteFormatter : function(row) {
                        return row.ext$.testname;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.addTest();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteTest();
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.MDM.EMP.EMPS.BUTTON.COPY',
                        icon : 'copy',
                        onClick : function() {
                            _this.copeTest();
                        }
                    } ],
                    columns : emcEmp.getTestListColumns(),
                } ]
            }, {
                title : 'EMC.MODULE.MDM.EMP.EMPS.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-emp-detail-uploader',
                    dbTable : 'T_EMC_EMP_FILE',
                    bizId : _this.param.id,
                } ]
            } ]
        }
    },

    addTest : function() {
        var _this = this;
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('testMethod').title,
            url : Gikam.choose.getConfig('testMethod').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_EMP_TEST',
                targetTestMethodIdColumn : 'TESTID',
                targetFilterColumn : 'EMPID',
                targetFilterValue : _this.param.id,
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        testId : item.id,
                    }
                });
                Gikam.getComp('emc-emp-detail-test-list-grid').insert(Gikam.printf(emcEmp.baseUrl + '/{id}/tests', {
                    id : _this.param.id
                }), chooseData);
            },
        });
    },
    deleteTest : function() {
        var _this = this;
        Gikam.getComp('emc-emp-detail-test-list-grid').deleteRows(Gikam.printf(emcEmp.baseUrl + '/{id}/tests', {
            id : _this.param.id
        }));
    },
    copeTest : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-emp-detail-test-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        } else {
            Gikam.create('modal', {
                title : Gikam.choose.getConfig('emp').title,
                url : Gikam.choose.getConfig('emp').url + Gikam.param({
                    single : true,
                }),
                onAfterClose : function(choices) {
                    if (Gikam.isEmpty(choices)) {
                        return;
                    }
                    workspace.window.showMask(true);
                    Gikam.post(Gikam.printf(emcEmp.baseUrl + '/{id}/tests/queries', {
                        id : choices[0].id
                    })).done(function(t) {
                        selections = selections.filter(function(selection) {
                            var flag = true;
                            for (var i = 0; i < t.rows.length; i++) {
                                if (t.rows[i].testId == selection.testId) {
                                    flag = false;
                                    break;
                                }
                            }
                            return flag;
                        });
                        Gikam.getComp('emc-emp-detail-test-list-grid').insert(Gikam.printf(emcEmp.baseUrl + '/{id}/tests', {
                            id : choices[0].id
                        }), selections);
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                },
            });
        }
    },
    insertQualRow : function() {
        var formData = Gikam.getComp('emc-emp-detail-base-info-form').getData();
        var url = Gikam.printf(emcEmp.baseUrl + '/{id}/quals', {
            id : formData.id
        });
        Gikam.getComp('emc-emp-detail-qual-list-grid').insert(url, [ {
            empId : formData.id
        } ]);
    },

    deleteQualRow : function() {
        var formData = Gikam.getComp('emc-emp-detail-base-info-form').getData();
        var url = Gikam.printf(emcEmp.baseUrl + '/{id}/quals', {
            id : formData.id
        });
        Gikam.getComp('emc-emp-detail-qual-list-grid').deleteRows(url);
    },

    uploadQualFile : function() {
        var formData = Gikam.getComp('emc-emp-detail-base-info-form').getData();
        var rows = Gikam.getComp('emc-emp-detail-qual-list-grid').getSelections();
        if (rows && rows.length == 1) {
            var bizId = rows[0].id;
            Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
                f : {
                    targetId : 'T_EMC_EMP_QUAL$' + bizId
                },
                n : -1,
                s : 50
            })).done(function(r) {
                if (r.total > 0) {
                    Gikam.confirm('EMC.MODULE.MDM.EMP.EMPS.TIP.FILE_OVERRIDE', 'EMC.MODULE.MDM.EMP.EMPS.TIP.FILE_OVERRIDE', function() {
                        Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', r.rows ])).done(function(r) {
                            Gikam.create('simpleUploader', {
                                id : 'emc-emp-detail-qual-uploader',
                                dbTable : 'T_EMC_EMP_QUAL',
                                bizId : bizId,
                                multiple : false,
                                onAfterClose : function() {
                                    Gikam.getComp('emc-emp-detail-qual-list-grid').refresh()
                                }
                            });
                        });
                    })
                } else {
                    Gikam.create('simpleUploader', {
                        id : 'emc-emp-detail-qual-uploader',
                        dbTable : 'T_EMC_EMP_QUAL',
                        bizId : bizId,
                        multiple : false,
                        onAfterClose : function() {
                            Gikam.getComp('emc-emp-detail-qual-list-grid').refresh()
                        }
                    });
                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
        }
    },

    uploadElectronicSignature : function() {
        var empForm = Gikam.getComp('emc-emp-detail-base-info-form');
        var bizId = empForm.getData().id;
        Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
            f : {
                targetId : 'T_CORE_USER$' + bizId,
            },
            n : -1,
            s : 50
        })).done(function(r) {
            if (r.total > 0) {
                Gikam.confirm('EMC.MODULE.MDM.EMP.EMPS.TIP.ELECTRONICSIGNATURE_OVERRIDE', 'EMC.MODULE.MDM.EMP.EMPS.TIP.ELECTRONICSIGNATURE_OVERRIDE', function() {
                    Gikam.create('simpleUploader', {
                        id : 'emc-emp-detail-electronic-signature-uploader',
                        dbTable : 'T_CORE_USER',
                        bizId : bizId,
                        accept : 'image',
                        multiple : false,
                        onAfterClose : function(fileList) {
                            if (fileList.length > 0) {
                                Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', r.rows ]));
                                Gikam.getJson(Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}', {
                                    id : fileList[0].id
                                })).done(function(r) {
                                    empForm.setData({
                                        electronicSignature : IFM_CONTEXT + r.downloadUrl
                                    })
                                });
                            }
                        }
                    });
                })
            } else {
                Gikam.create('simpleUploader', {
                    id : 'emc-emp-detail-electronic-signature-uploader',
                    dbTable : 'T_CORE_USER',
                    bizId : bizId,
                    accept : 'image',
                    multiple : false,
                    onAfterClose : function(fileList) {
                        if (fileList.length === 0) {
                            return;
                        }
                        Gikam.getJson(Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}', {
                            id : fileList[0].id
                        })).done(function(r) {
                            empForm.setData({
                                electronicSignature : IFM_CONTEXT + r.downloadUrl
                            })
                        });
                    }
                });
            }
        });
    },

    uploadEmpPicture : function() {
        var empForm = Gikam.getComp('emc-emp-detail-base-info-form');
        var bizId = empForm.getData().id;
        Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
            f : {
                targetId : 'T_EMC_EMP$' + bizId,
                bizCategory_SEQ : 'PIC'
            },
            n : -1,
            s : 50
        })).done(function(r) {
            if (r.total > 0) {
                Gikam.confirm('EMC.MODULE.MDM.EMP.EMPS.TIP.PIC_OVERRIDE', 'EMC.MODULE.MDM.EMP.EMPS.TIP.PIC_OVERRIDE', function() {
                    Gikam.create('simpleUploader', {
                        id : 'emc-emp-detail-pic-uploader',
                        dbTable : 'T_EMC_EMP',
                        bizId : bizId,
                        bizCategory : 'PIC',
                        accept : 'image',
                        multiple : false,
                        onAfterClose : function(fileList) {
                            if (fileList.length > 0) {
                                Gikam.del(IFM_CONTEXT + '/core/module/item/files', Gikam.getJsonWrapper(null, [ '', r.rows ]));
                                Gikam.getJson(Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}', {
                                    id : fileList[0].id
                                })).done(function(r) {
                                    empForm.setData({
                                        pic : IFM_CONTEXT + r.downloadUrl
                                    })
                                });
                            }
                        }
                    });
                })
            } else {
                Gikam.create('simpleUploader', {
                    id : 'emc-emp-detail-pic-uploader',
                    dbTable : 'T_EMC_EMP',
                    bizId : bizId,
                    bizCategory : 'PIC',
                    accept : 'image',
                    multiple : false,
                    onAfterClose : function(fileList) {
                        if (fileList.length === 0) {
                            return;
                        }
                        Gikam.getJson(Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}', {
                            id : fileList[0].id
                        })).done(function(r) {
                            empForm.setData({
                                pic : IFM_CONTEXT + r.downloadUrl
                            })
                        });
                    }
                });
            }
        });
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab(), this.getOtherGridParam() ]
            },
            renderTo : workspace.window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    },
    
    getCertTestMethodUrl : function(id) {
        if (id) {
            return Gikam.printf(emcEmp.baseUrl + '/{id}/methods/queries', {
                id : id
            })
        }
    },
    
    onEmpCertActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-emp-detail-method-list-grid').refresh({
                url : _this.getCertTestMethodUrl(row.id),
            });
        }
    },
    
    insertCertTestMethod : function() {
        var _this = emcEmp.detailPage;

        var activedRow = Gikam.getComp('emc-emp-detail-credentials-list-grid').getActivedRow();
        
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BIZMDM.MONITORING.POINTS.TAB.BASE_INFO',
            url : Gikam.choose.getConfig('testMethod').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_CERT_TEST_METHOD',
                targetTestMethodIdColumn : 'TESTID',
                targetFilterColumn1 : 'CERTID',
                targetFilterValue1 : activedRow.id,
            }),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var url = Gikam.printf(emcEmp.baseUrl + '/{id}/cert-test-methods', {
                    id : activedRow.id,
                });
                workspace.window.showMask(true);
                Gikam.put(url, Gikam.getJsonWrapper({}, [ null, rows ])).done(function(data) {
                    
                }).always(function() {
                    workspace.window.closeMask();
                });
                Gikam.getComp('emc-emp-detail-method-list-grid').refresh({
                    url : _this.getCertTestMethodUrl(activedRow.id),
                });
            },
        });
    },

    urlManager : {
        baseInfoForm : emcEmp.baseUrl + '/{id}',
        credentialsGrid : emcEmp.baseUrl + '/{id}/credentials/queries',
        qualGrid : emcEmp.baseUrl + '/{id}/quals/queries',
        testGrid : emcEmp.baseUrl + '/{id}/tests/queries',
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'emc-emp-detail-base-info-form' : this.getCompUrl('baseInfoForm')
        })
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        })
    }
}