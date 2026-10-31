emcEmp.editPage = {
    // 右边多个tab页
    trainGrid : null,
    empGrid : null,
    // 左右布局存在一个问题: 当Grid触发loadSuccess事件时, 如果右侧form未渲染完成，那么form将加载不出数据
    // 解决方案：使用ready做为标志，由grid的loadSuccess事件和form的rendered事件修改其值。当任何一方发现ready值为true时，再触发grid的activeRow事件以完成form数据加载。
    ready : false,
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-emp-edit-list-grid',
            service : 'emcEmpServiceImpl',
            url : emcEmp.baseUrl + '/queries/raw',
            requestData : {
            	userOrg_filter : '1',
                orgid_SEQ : workspace.user.ext$.parentorgid
            },
            page : true,
            columns : emcEmp.getEditListColumns(),
            toolbar : _this.getBtnToolbarParam(),
            filterOpen : true,
            onLoadSuccess : function(data) {
                if (!_this.ready) {
                    _this.ready = true;
                    return;
                }
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }
            },
            deleteFormatter : function(row) {
                return row.username || row.id;
            },
            onRendered : function() {
                _this.empGrid = this;
            },
            onRowActive : function(index, row) {
                if (row) {
                    Gikam.getComp('emc-emp-detail-base-info-form').refresh({
                        url : _this.getCompUrl('baseInfoForm', row.id)
                    });
                }
            },
        };
    },

    getBtnToolbarParam : function() {
        var _this = this;
        return [ {
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
                _this.empGrid.deleteRows(emcEmp.baseUrl);
                if (Gikam.isEmpty(_this.empGrid.getData())) {
                    Gikam.getComp('emc-emp-detail-base-info-form')
                }
            }
        }, {
            type : 'button',
            text : 'GIKAM.BUTTON.PREVIEW',
            icon : 'print',
            onClick : function() {
                Gikam.viewReport('EMP_REPORT', Gikam.getComp('emc-emp-edit-list-grid').getSelections());
            }
        }, {
            type : 'button',
            text : 'GIKAM.BUTTON.EXCEL_EXPORT',
            icon : 'edit',
            onClick : function() {
                Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}', {
                    templateCode : 'emcEmpExportTemplate',
                    service : 'emcEmpExportTemplate'
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
                    url : IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcEmpImportFileFunction',
                    dbTable : 'T_CORE_EMP',
                    onImportSuccess : function(files) {
                        Gikam.getComp('emc-emp-edit-list-grid').refresh();
                    }
                });
            }
        },{
            type : 'form',
            autoSave : false,
            fields : _this.getShowAllField()
        } ]
    },

    getShowAllField : function() {
        return [ {
            title : '证书超期',
            field : 'showFlag',
            type : 'simpleCheckbox',
            refreshGrid : false,
            styleFormatter : function() {
                return {
                    paddingLeft : '0'
                }
            },
            onChange : function(field, value, index) {
                var grid = Gikam.getComp('emc-emp-edit-list-grid');

                var showFlags = value;
                if (showFlags == '1') {
                    grid.refresh({
                        requestData : {
                            overdueFilter : '1',
                        }
                    }).done(function() {
                    });
                } else {
                    grid.refresh({
                        requestData : {
                            overdueFilter : '',
                        }
                    }).done(function() {
                    });
                }
            },
        } ]
    },

    add : function() {
        var _this = this;
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('user-simple').title,
            url : Gikam.choose.getConfig('user-simple').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_EMP',
                targetUserIdColumn : 'ID'
            }),
            width : Gikam.choose.getConfig('user-simple').width,
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var emps = rows.map(function(row) {
                    return {
                        id : row.id,
                        userName : row.userName
                    };
                });
                Gikam.getComp('emc-emp-edit-list-grid').insert(Gikam.printf(emcEmp.baseUrl), emps)
            }
        });
    },

    // 右边多个tab页
    getInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            onActived : function(tab, title) {
                if (tab.index === 3) {
                    Gikam.getComp('other-grid-layout').setSize({
                        west : {
                            width : '60%'
                        }
                    });
                    Gikam.getComp('methodGrid').show();
                } else {
                    Gikam.getComp('other-grid-layout').setSize({
                        west : {
                            width : '100%'
                        }
                    });
                    Gikam.getComp('methodGrid').hide();
                }
            },
            panels : [ {
                title : 'EMC.MODULE.MDM.EMP.EMPS.TAB.BASE_INFO',
                items : [ _this.getBaseinfo() ]
            }, {
                // 人员培训
                title : 'EMC.MODULE.MDM.EMP.EMPS.TAB.TRAIN',
                items : [ {
                    type : 'grid',
                    id : 'emc-emp-detail-train-list-grid',
                    columns : emcEmp.getTrainListColumns(),
                    service : 'emcEmpTrainServiceImpl',
                    deleteFormatter : function(row) {
                        return row.trainContent || row.id;
                    },
                    onRendered : function() {
                        _this.trainGrid = this;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var formData = Gikam.getComp('emc-emp-detail-base-info-form').getData();
                            var url = Gikam.printf(emcEmp.baseUrl + '/{id}/trains', {
                                id : formData.id
                            });
                            Gikam.getComp('emc-emp-detail-train-list-grid').insert(url, [ {
                                empId : formData.id
                            } ]);
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            var formData = Gikam.getComp('emc-emp-detail-base-info-form').getData();
                            var url = Gikam.printf(emcEmp.baseUrl + '/{id}/trains', {
                                id : formData.id
                            });
                            Gikam.getComp('emc-emp-detail-train-list-grid').deleteRows(url);
                        }
                    },{
                        type : 'button',
                        text : 'EMC.MODULE.MDM.EMP.EMPS.BUTTON.COPY',
                        icon : 'copy',
                        onClick : function() {
                            _this.copeTrain();
                        }
                    } ]
                } ]
            }, {
                // 资格证书
                title : 'EMC.MODULE.MDM.EMP.EMPS.TAB.QUAL',
                items : [ {
                    type : 'grid',
                    id : 'emc-emp-detail-qual-list-grid',
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
                // 分析员上岗证
                title : '上岗证',
                items : [ {
                    type : 'grid',
                    id : 'emc-emp-detail-credentials-list-grid',
                    onRendered : function() {
                        _this.credentialsGrid = this;
                    },
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        } else {
                            _this.methodGrid.cleanData();
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.onEmpCertActive(row);
                    },
                    columns : emcEmp.getCredentialsListColumns(),
                } ]
            }, {
                // 可见测试
                title : 'EMC.MODULE.MDM.EMP.EMPS.TAB.TEST',
                items : [ {
                    type : 'grid',
                    id : 'emc-emp-detail-test-list-grid',
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
                // 附件
                title : 'EMC.MODULE.MDM.EMP.EMPS.TAB.FILE',
                items : [ {
                    type : 'uploader',
                    id : 'emc-emp-detail-uploader',
                    dbTable : 'T_EMC_EMP_FILE',
                } ]
            } ]
        }
    },

    getBaseinfo : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-emp-detail-base-info-form',
            service : 'emcEmpServiceImpl',

            columns : 3,
            fields : emcEmp.getBaseInfoFormFields(),
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
                Gikam.getComp('emc-emp-detail-uploader').setOptions({
                    bizId : data.id
                });
                Gikam.getComp('emc-emp-detail-qual-list-grid').refresh({
                    url : _this.getCompUrl('qualGrid', data.id)
                });
                Gikam.getComp('emc-emp-detail-qual-list-grid').refresh({
                    url : _this.getCompUrl('qualGrid', data.id)
                });
                Gikam.getComp('emc-emp-detail-credentials-list-grid').refresh({
                    url : _this.getCompUrl('credentialsGrid', data.id)
                });
                Gikam.getComp('emc-emp-detail-test-list-grid').refresh({
                    url : _this.getCompUrl('testGrid', data.id)
                });
                Gikam.getComp('emc-emp-detail-train-list-grid').refresh({
                    url : _this.getCompUrl('trainGrid', data.id)
                });
            },
            onUpdated : function(data) {
                _this.empGrid.setData(data);
            }
        };
    },

    // 方法证书
    getMethodGridParam : function() {
        var _this = this;
        return {
            id : 'methodGrid',
            type : 'tab',
            fill : true,
            width : Gikam.width.XL,
            panels : [ {
                title : 'EMC.MODULE.MDM.EMP.EMPS.TAB.METHOD',
                items : [ {
                    type : 'grid',
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
        }
    },

    qualGrid : null,
    testGrid : null,
    addTest : function() {
        var _this = this;
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('testMethod').title,
            url : Gikam.choose.getConfig('testMethod').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_EMP_TEST',
                targetTestMethodIdColumn : 'TESTID',
                targetFilterColumn : 'EMPID',
                targetFilterValue : _this.getGridActiveRow().id,
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
                    id : _this.getGridActiveRow().id
                }), chooseData);
            },
        });
    },

    deleteTest : function() {
        var _this = this;
        Gikam.getComp('emc-emp-detail-test-list-grid').deleteRows(Gikam.printf(emcEmp.baseUrl + '/{id}/tests', {
            id : _this.getGridActiveRow().id
        }));
    },

    copeTrain : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-emp-detail-train-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        } else {
            Gikam.create('modal', {
                title : Gikam.choose.getConfig('emp').title,
                url : Gikam.choose.getConfig('emp').url,
                onAfterClose : function(choices) {
                    if (Gikam.isEmpty(choices)) {
                        return;
                    }
                    choices.forEach(function(r){
                        Gikam.getComp('emc-emp-detail-train-list-grid').insert(Gikam.printf(emcEmp.baseUrl + '/{id}/trains', {
                            id : r.id
                        }), selections);
                    });
                    Gikam.alert("复制成功")
                },
            });
        }
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
        var _this = this;
        var activedRow = Gikam.getComp('emc-emp-detail-credentials-list-grid').getActivedRow();
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('testMethod').title,
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
                    Gikam.getComp('emc-emp-detail-method-list-grid').refresh({
                        url : _this.getCertTestMethodUrl(activedRow.id),
                    });
                }).always(function() {
                    workspace.window.closeMask();
                });

            },
        });
    },

    getGridActiveRow : function() {
        var _this = this;
        return Gikam.getComp('emc-emp-edit-list-grid').getActivedRow();
    },

    urlManager : {
        baseInfoForm : emcEmp.baseUrl + '/{id}',
        credentialsGrid : emcEmp.baseUrl + '/{id}/credentials/queries',
        qualGrid : emcEmp.baseUrl + '/{id}/quals/queries',
        testGrid : emcEmp.baseUrl + '/{id}/tests/queries',
        trainGrid : emcEmp.baseUrl + '/{id}/trains/queries',
    },

    getCompUrl : function(name, id) {
        if (Gikam.isEmpty(id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : id
        })
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            west : {
                width : '400',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    center : {
                        items : [ {
                            id : 'other-grid-layout',
                            type : 'layout',
                            west : {
                                width : '100%',
                                items : [ this.getInfoTab() ]
                            },
                            center : {
                                items : [ this.getMethodGridParam() ]
                            }
                        } ]
                    }

                } ]
            },
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }

};
