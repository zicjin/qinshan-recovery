var emcClient = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/clients',

    getClientListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'clientNo',
            title : 'T_EMC_CLIENT.CLIENTNO',
            type : 'link',
            width : 100,
        }, {
            field : 'clientName',
            title : 'T_EMC_CLIENT.CLIENTNAME',
            validators : [ 'strLength[0,86]' ],
            width : 140,
        }, {
            field : 'clientCategory',
            title : 'T_EMC_CLIENT.CLIENTCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            width : 100,
            param : {
                codeCategoryId : 'customerCategory'
            }
        }, {
            field : 'ext$.areaname',
            title : 'T_EMC_CLIENT.AREA',
            type : 'choose',
            category : 'area',
            single : true,
            width : 100,
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        areaId : rows[0].id,
                        ext$ : {
                            areaname : rows[0].areaName,
                        }
                    });
                }
            },
        }, ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'clientNo',
            title : 'T_EMC_CLIENT.CLIENTNO',
            readonly : true
        }, {
            field : 'clientName',
            title : 'T_EMC_CLIENT.CLIENTNAME',
            validators : [ 'strLength[0,86]' ],
        }, {
            field : 'clientCategory',
            title : 'T_EMC_CLIENT.CLIENTCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'customerCategory'
            }
        }, {
            field : 'clientSCategory',
            title : 'T_EMC_CLIENT.CLIENTSCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'customerSubclass'
            }
        }, {
            field : 'ext$.areaname',
            title : 'T_EMC_CLIENT.AREA',
            type : 'choose',
            category : 'area',
            single : true,
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        areaId : rows[0].id,
                        ext$ : {
                            areaname : rows[0].areaName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    areaId : '',
                    ext$ : {
                        areaname : '',
                    }
                });
            },
        }, {
            field : 'phone',
            title : 'T_EMC_CLIENT.PHONE',
            validators : [ 'phone' ]
        }, {
            field : 'address',
            title : 'T_EMC_CLIENT.ADDRESS',
            validators : [ 'strLength[0,171]' ],
        }, {
            field : 'ext$.openingbank',
            title : 'T_EMC_CLIENT.OPENINGBANK',
            validators : [ 'strLength[0,120]' ],
        }, {
            field : 'ext$.bankaccount',
            title : 'T_EMC_CLIENT.BANKACCOUNT',
            validators : [ 'strLength[0,66]' ],
        }, {
            field : 'website',
            title : 'T_EMC_CLIENT.WEBSITE',
            validators : [ 'strLength[0,43]' ],
        }, {
            field : 'mail',
            title : 'T_EMC_CLIENT.MAIL',
            validators : [ 'strLength[0,33]' ],
        }, {
            field : 'scale',
            title : 'T_EMC_CLIENT.SCALE',
            validators : [ 'strLength[0,33]' ],
        }, {
            field : 'creditCode',
            title : 'T_EMC_CLIENT.CREDITCODE',
            validators : [ 'strLength[0,30]' ],
        }, {
            field : 'createdTime',
            title : 'T_EMC_CLIENT.CREATEDTIME',
            type : 'date'
        }, {
            field : 'longitude',
            title : 'T_EMC_CLIENT.LONGITUDE',
            validators : [ 'strLength[0,11]' ],
        }, {
            field : 'latitude',
            title : 'T_EMC_CLIENT.LATITUDE',
            validators : [ 'strLength[0,11]' ],
        }, {
            field : 'ext$.clienttag',
            title : 'T_EMC_CLIENT.CLIENTTAG',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'clientTag'
            }
        }, {
            field : 'remark',
            title : 'T_EMC_CLIENT.REMARK',
            type : 'textarea',
            colspan : 4,
            height : 100,
            validators : [ 'strLength[0,171]' ],
        } ];
    },

    getClientContactColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'id',
            title : 'T_EMC_CLIENT_CONTACT.ID',
            width : 100,
        }, {
            field : 'lxrName',
            title : 'T_EMC_CLIENT_CONTACT.LXRNAME',
            editor : true,
            width : 100,
            validators : [ 'notEmpty', 'strLength[0,30]' ],
        }, {
            field : 'lxrPhone',
            title : 'T_EMC_CLIENT_CONTACT.LXRPHONE',
            editor : true,
            width : 100,
            validators : [ 'notEmpty', 'phone' ]
        }, {
            field : 'lxrEmail',
            title : 'T_EMC_CLIENT_CONTACT.LXREMAIL',
            editor : true,
            width : 100,
            validators : [ 'strLength[0,30]' ],
        }, {
            field : 'fax',
            title : 'T_EMC_CLIENT_CONTACT.FAX',
            editor : true,
            width : 60,
            validators : [ 'strLength[0,30]' ],
        }, {
            field : 'postcode',
            title : 'T_EMC_CLIENT_CONTACT.POSTCODE',
            editor : true,
            width : 60,
            validators : [ 'strLength[0,30]' ],
        }, {
            field : 'lxrAddress',
            title : 'T_EMC_CLIENT_CONTACT.LXRADDRESS',
            editor : true,
            width : 100,
            validators : [ 'strLength[0,171]' ],
        }, {
            field : 'job',
            title : 'T_EMC_CLIENT_CONTACT.JOB',
            editor : true,
            width : 100,
            validators : [ 'strLength[0,30]' ],
        }, {
            field : 'province',
            title : 'T_EMC_CLIENT_CONTACT.PROVINCE',
            editor : true,
            width : 60,
            validators : [ 'strLength[0,30]' ],
        }, {
            field : 'city',
            title : 'T_EMC_CLIENT_CONTACT.CITY',
            editor : true,
            width : 60,
            validators : [ 'strLength[0,30]' ],
        }, {
            field : 'county',
            title : 'T_EMC_CLIENT_CONTACT.COUNTY',
            editor : true,
            width : 60,
            validators : [ 'strLength[0,30]' ],
        }, {
            field : 'remark',
            title : 'T_EMC_CLIENT_CONTACT.REMARK',
            editor : true,
            validators : [ 'strLength[0,171]' ],
        }, ];
    },

    getMonitorPointFormFields : function() {
        return [ {
            field : 'ext$.templatename',
            title : 'T_EMC_MONITOR_POINT.MONITORPOINTCATEGORY',
            type : 'choose',
            single : true,
            preInsert : true,
            category : 'monitorPointCategory',
            onBeforeChoose : function() {
                return {
                    orgId_SEQ: workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        monitorPointCategoryId : rows[0].id,
                        ext$ : {
                            templatename : rows[0].ext$.templatename,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    monitorPointCategoryId : '',
                    ext$ : {
                        templatename : '',
                    }
                });
            },
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_MONITOR_POINT.MONITORPOINTNAME',
            editor : true,
            preInsert : true,
            validators : [ 'strLength[0,32]' ],
        }, {
            field : 'defaultCycle',
            title : 'T_EMC_MONITOR_POINT.DEFAULTCYCLE',
            range : '[1,24]',
            value : 1,
            isInteger : true,
            type : 'number',
            hasArrow : true,
            editor : true,
            preInsert : true
        }, {
            field : 'defaultFrequency',
            title : 'T_EMC_MONITOR_POINT.DEFAULTFREQUENCY',
            range : '[1,24]',
            value : 1,
            isInteger : true,
            type : 'number',
            hasArrow : true,
            editor : true,
            preInsert : true
        } ];
    },

    // 监测点管理
    getMonitorPointColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_MONITOR_POINT.ORDERNO',
            validators : [ 'integer' ],
            type : 'number',
            hasArrow : true,
            preInsert : true,
            value : 1,
            editor : true,
            width : 100
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_MONITOR_POINT.MONITORPOINTNO',
            editor : true,
            width : 100,
            validators : [ 'strLength[0,15]' ],
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_MONITOR_POINT.MONITORPOINTNAME',
            editor : true,
            width : 120,
            validators : [ 'strLength[0,32]' ],
        }, {
            field : 'defaultCycle',
            title : 'T_EMC_MONITOR_POINT.DEFAULTCYCLE',
            range : '[1,24]',
            value : 1,
            isInteger : true,
            type : 'number',
            hasArrow : true,
            editor : true
        }, {
            field : 'defaultFrequency',
            title : 'T_EMC_MONITOR_POINT.DEFAULTFREQUENCY',
            range : '[1,24]',
            value : 1,
            isInteger : true,
            type : 'number',
            hasArrow : true,
            editor : true
        }, {
            field : 'ext$.templatename',
            title : 'T_EMC_MONITOR_POINT.MONITORPOINTCATEGORY',
            type : 'choose',
            single : true,
            category : 'monitorPointCategory',
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        monitorPointCategoryId : rows[0].id,
                        ext$ : {
                            templatename : rows[0].ext$.templatename,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    monitorPointCategoryId : '',
                    ext$ : {
                        templatename : '',
                    }
                });
            },
        }, {
            field : 'monitorPointType',
            title : 'T_EMC_MONITOR_POINT.MONITORPOINTTYPE',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'monitoringPointCategory'
            },
            width : 100,
        }, {
            field : 'monitorPlace',
            title : 'T_EMC_MONITOR_POINT.MONITORPLACE',
            editor : true,
            width : 100,
            validators : [ 'strLength[0,128]' ],
        }, {
            field : 'ext$.indexname',
            title : 'T_EMC_MONITOR_POINT.INDEX',
            type : 'link',
            width : 100,
        }, {
            field : 'longitude',
            title : 'T_EMC_MONITOR_POINT.LONGITUDE',
            editor : true,
            width : 90,
            validators : [ 'strLength[0,20]' ],
        }, {
            field : 'latitude',
            title : 'T_EMC_MONITOR_POINT.LATITUDE',
            editor : true,
            width : 90,
            validators : [ 'strLength[0,20]' ],
        }, {
            field : 'river',
            title : 'T_EMC_MONITOR_POINT.RIVER',
            editor : true,
            validators : [ 'strLength[0,120]' ],
        }, {
            field : 'tributary',
            title : 'T_EMC_MONITOR_POINT.TRIBUTARY',
            editor : true,
            validators : [ 'strLength[0,120]' ],
        }, {
            field : 'creditCode',
            title : 'T_EMC_MONITOR_POINT.CREDITCODE',
            editor : true,
            validators : [ 'strLength[0,60]' ],
        }, {
            field : 'industry',
            title : 'T_EMC_MONITOR_POINT.INDUSTRY',
            editor : true,
            validators : [ 'strLength[0,60]' ],
        }, {
            field : 'remark',
            title : 'T_EMC_MONITOR_POINT.REMARK',
            editor : true,
            width : 90,
            validators : [ 'strLength[0,171]' ],
        }, ];
    },

    getMonitorPointSchemeColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'ext$.monitortmplgroup',
            title : 'T_EMC_MONITOR_POINT_SCHEME.MONITORTMPLGROUP',
            width : 100,
        }, {
            field : 'ext$.monitortmplname',
            title : 'T_EMC_MONITOR_POINT_SCHEME.MONITORTMPL',
            width : 100,
        }, {
            field : 'schemeName',
            title : 'T_EMC_MONITOR_POINT_SCHEME.SCHEMENAME',
            width : 100,
        }, {
            field : 'sampleNum',
            title : 'T_EMC_MONITOR_POINT_SCHEME.SAMPLENUM',
            range : '[1,24]',
            value : 1,
            isInteger : true,
            width : 100,
            type : 'number',
            hasArrow : true,
            editor : true
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_MONITOR_POINT_SCHEME.TESTNAME',
            width : 100,
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_MONITOR_SCHEME.METHODNAME',
            width : 100,
        }, {
            field : 'itemGroup',
            title : 'T_EMC_MONITOR_POINT_SCHEME.ITEMGROUP',
            width : 100,
        }, ];
    },

    getMonitorPointChooseListColumns : function() {
        return [ {
            index : true
        }, {
            field : 'ext$.clientname',
            title : 'T_EMC_MONITOR_POINT.CLIENTNAME',
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_MONITOR_POINT.MONITORPOINTNO',
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_MONITOR_POINT.MONITORPOINTNAME',
        }, {
            field : 'ext$.templatename',
            title : 'T_EMC_MONITOR_POINT.MONITORPOINTCATEGORY',
        }, {
            field : 'monitorPointType',
            title : 'T_EMC_MONITOR_POINT.MONITORPOINTTYPE',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'monitoringPointCategory'
            },
        }, {
            field : 'monitorPlace',
            title : 'T_EMC_MONITOR_POINT.MONITORPLACE',
        }, {
            field : 'ext$.indexname',
            title : 'T_EMC_MONITOR_POINT.INDEX',
        }, {
            field : 'export',
            title : 'T_EMC_MONITOR_POINT.EXPORT',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'monitorPointLevel',
            title : 'T_EMC_MONITOR_POINT.MONITORPOINTLEVEL',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'monitoringPointLevel'
            },
        }, ];
    },

    getMonitorPointSimpleChooseListColumns : function() {
        return [ {
            index : true
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_MONITOR_POINT.MONITORPOINTNO',
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_MONITOR_POINT.MONITORPOINTNAME',
        } ];
    },

    getPreInsertFields : function() {
        return [ {
            field : 'ext$.templatename',
            title : 'T_EMC_MONITOR_POINT.MONITORPOINTCATEGORY',
            editor : true,
            type : 'choose',
            single : true,
            category : 'monitorPointCategory',
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        monitorPointCategoryId : rows[0].id,
                        ext$ : {
                            templatename : rows[0].ext$.templatename,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    monitorPointCategoryId : '',
                    ext$ : {
                        templatename : '',
                    }
                });
            },
        } ];
    },
    
    /** 预新增 */
    getPreInsertFormFields : function() {
        return [ {
            field : 'clientName',
            title : 'T_EMC_CLIENT.CLIENTNAME',
            validators : [ 'unique[' + emcClient.baseUrl + '/action/validate-unique]', 'strLength[0,86]' ],
            preInsert : true,
        }, {
            field : 'clientCategory',
            title : 'T_EMC_CLIENT.CLIENTCATEGORY',
            type : 'select',
            search : true,
            preInsert : true,
            category : 'sys',
            param : {
                codeCategoryId : 'customerCategory'
            }
        }, {
            field : 'ext$.areaname',
            title : 'T_EMC_CLIENT.AREA',
            type : 'choose',
            category : 'area',
            preInsert : true,
            single : true,
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        areaId : rows[0].id,
                        ext$ : {
                            areaname : rows[0].areaName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    areaId : '',
                    ext$ : {
                        areaname : '',
                    }
                });
            },
        }, {
            field : 'ext$.clienttag',
            title : 'T_EMC_CLIENT.CLIENTTAG',
            type : 'select',
            preInsert : true,
            selectedIndex : 1,
            category : 'sys',
            param : {
                codeCategoryId : 'clientTag'
            }
        }, {
            field : 'lxrName',
            title : 'T_EMC_CLIENT_CONTACT.LXRNAME',
            preInsert : true,
            validators : [ 'notEmpty', 'strLength[0,30]' ],
        }, {
            field : 'lxrPhone',
            title : 'T_EMC_CLIENT_CONTACT.LXRPHONE',
            preInsert : true,
            validators : [ 'notEmpty', 'phone' ]
        } ];
    },

    getClientChooseListColumns : function() {
        return [ {
            field : 'clientNo',
            title : 'T_EMC_CLIENT.CLIENTNO',
            type : 'link',
        }, {
            field : 'clientName',
            title : 'T_EMC_CLIENT.CLIENTNAME',
            width : 300,
        }, {
            field : 'clientCategory',
            title : 'T_EMC_CLIENT.CLIENTCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'customerCategory'
            }
        }, {
            field : 'ext$.clienttag',
            title : 'T_EMC_CLIENT.CLIENTTAG',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'clientTag'
            }
        } ];
    },
    
    getClientSubChooseListColumns : function() {
        return [ {
            field : 'clientNo',
            title : 'T_EMC_CLIENT.CLIENTNO',
            type : 'link',
        }, {
            field : 'clientName',
            title : 'T_EMC_CLIENT.CLIENTNAME',
            width : 300,
        }, {
            field : 'ext$.monitorplace',
            title : 'T_EMC_MONITOR_POINT.MONITORPLACE',
        }, {
            field : 'clientCategory',
            title : 'T_EMC_CLIENT.CLIENTCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'customerCategory'
            }
        }, {
            field : 'ext$.clienttag',
            title : 'T_EMC_CLIENT.CLIENTTAG',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'clientTag'
            }
        } ];
    },
    
    getClientSubFormFields : function(requestData) {
        var flag = true;
        return [ {
            title : 'EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.SHOW_ALL',
            field : 'showFlag',
            type : 'simpleCheckbox',
            refreshGrid : false,
            styleFormatter : function() {
                return {
                    paddingLeft : '0'
                }
            },
            onChange : function(field, value, index) {
                workspace.window.showMask(true);
                var clientGrid = Gikam.getComp('emc-client-sub-choose-list-grid');

                if (value === '1') {
                    clientGrid.refresh({
                        requestData : {
                            id_SEQ : '',
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                } else {
                    clientGrid.refresh({
                        requestData : requestData
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                }
            },
        } ]
    },

    getBatchUpdateSampleNumFields : function() {
        return [ {
            field : 'sampleNum',
            title : 'T_EMC_MONITOR_POINT_SCHEME.SAMPLENUM',
            range : '[1,24]',
            value : 1,
            isInteger : true,
            type : 'number',
            hasArrow : true,
            editor : true
        } ]
    },

    getContactChooseListColumns : function() {
        return [ {
            field : 'lxrName',
            title : 'T_EMC_CLIENT_CONTACT.LXRNAME',
            width : 100,
        }, {
            field : 'lxrPhone',
            title : 'T_EMC_CLIENT_CONTACT.LXRPHONE',
            width : 100,
        }, {
            field : 'lxrEmail',
            title : 'T_EMC_CLIENT_CONTACT.LXREMAIL',
            width : 100,
        }, {
            field : 'job',
            title : 'T_EMC_CLIENT_CONTACT.JOB',
            width : 100,
        }, {
            field : 'fax',
            title : 'T_EMC_CLIENT_CONTACT.FAX',
            width : 60,
        }, {
            field : 'postcode',
            title : 'T_EMC_CLIENT_CONTACT.POSTCODE',
            width : 60,
        }, {
            field : 'lxrAddress',
            title : 'T_EMC_CLIENT_CONTACT.LXRADDRESS',
            width : 100,
        }, {
            field : 'province',
            title : 'T_EMC_CLIENT_CONTACT.PROVINCE',
            width : 60,
        }, {
            field : 'city',
            title : 'T_EMC_CLIENT_CONTACT.CITY',
            width : 60,
        }, {
            field : 'county',
            title : 'T_EMC_CLIENT_CONTACT.COUNTY',
            width : 60,
        }, ];
    },
});
