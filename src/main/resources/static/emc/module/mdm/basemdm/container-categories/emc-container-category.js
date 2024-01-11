var emcContainerCategory = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/container-categories',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'containerCategoryId',
            title : 'T_EMC_CONTAINER_CATEGORY.CONTAINERCATEGORYID',
            validators : [ 'remote[' + emcContainerCategory.baseUrl + '/action/validate-unique]', 'notEmpty', 'strLength[0,16]' ],
            editor : true,
            width : 100
        }, {
            field : 'containerCategoryName',
            title : 'T_EMC_CONTAINER_CATEGORY.CONTAINERCATEGORYNAME',
            validators : [ 'strLength[0,64]' ],
            editor : true,
        }, {
            field : 'capacity',
            title : 'T_EMC_CONTAINER_CATEGORY.CAPACITY',
            validators : [ 'strLength[0,16]' ],
            editor : true,
            width : 100
        }, {
            field : 'color',
            title : 'T_EMC_CONTAINER_CATEGORY.COLOR',
            validators : [ 'strLength[0,16]' ],
            editor : true,
            width : 100
        }, {
            field : 'material',
            title : 'T_EMC_CONTAINER_CATEGORY.MATERIAL',
            validators : [ 'strLength[0,64]' ],
            editor : true,
            width : 100
        }, {
            field : 'sampleBottleCaliber',
            title : 'T_EMC_CONTAINER_CATEGORY.SAMPLEBOTTLECALIBER',
            validators : [ 'strLength[0,32]' ],
            editor : true
        }, {
            field : 'bottleneckProcess',
            title : 'T_EMC_CONTAINER_CATEGORY.BOTTLENECKPROCESS',
            validators : [ 'strLength[0,16]' ],
            editor : true
        }, {
            field : 'remark',
            title : 'T_EMC_CONTAINER_CATEGORY.REMARK',
            validators : [ 'strLength[0,512]' ],
            editor : true
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'containerCategoryId',
            preInsert : true,
            title : 'T_EMC_CONTAINER_CATEGORY.CONTAINERCATEGORYID',
            validators : [ 'remote[' + emcContainerCategory.baseUrl + '/action/validate-unique]', 'notEmpty', 'strLength[0,16]' ],
        }, {
            field : 'containerCategoryName',
            preInsert : true,
            title : 'T_EMC_CONTAINER_CATEGORY.CONTAINERCATEGORYNAME',
            validators : [ 'notEmpty', 'strLength[0,21]' ]
        }, {

        }, {
            field : 'capacity',
            title : 'T_EMC_CONTAINER_CATEGORY.CAPACITY',
            validators : [ 'strLength[0,16]' ]
        }, {
            field : 'color',
            title : 'T_EMC_CONTAINER_CATEGORY.COLOR',
            validators : [ 'strLength[0,5]' ]
        }, {
            field : 'material',
            title : 'T_EMC_CONTAINER_CATEGORY.MATERIAL',
            validators : [ 'strLength[0,21]' ]
        }, {
            field : 'sampleBottleCaliber',
            title : 'T_EMC_CONTAINER_CATEGORY.SAMPLEBOTTLECALIBER',
            validators : [ 'strLength[0,10]' ]
        }, {
            field : 'bottleneckProcess',
            title : 'T_EMC_CONTAINER_CATEGORY.BOTTLENECKPROCESS',
            validators : [ 'strLength[0,10]' ]
        }, {

        }, {
            field : 'remark',
            title : 'T_EMC_CONTAINER_CATEGORY.REMARK',
            validators : [ 'strLength[0,170]' ],
            type : 'textarea',
            colspan : 3
        } ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'containerCategoryId',
            title : 'T_EMC_CONTAINER_CATEGORY.CONTAINERCATEGORYID',
        }, {
            field : 'containerCategoryName',
            title : 'T_EMC_CONTAINER_CATEGORY.CONTAINERCATEGORYNAME',
        }, {
            field : 'capacity',
            title : 'T_EMC_CONTAINER_CATEGORY.CAPACITY',
        }, {
            field : 'color',
            title : 'T_EMC_CONTAINER_CATEGORY.COLOR',
        }, {
            field : 'material',
            title : 'T_EMC_CONTAINER_CATEGORY.MATERIAL',
        }, {
            field : 'sampleBottleCaliber',
            title : 'T_EMC_CONTAINER_CATEGORY.SAMPLEBOTTLECALIBER',
        }, {
            field : 'bottleneckProcess',
            title : 'T_EMC_CONTAINER_CATEGORY.BOTTLENECKPROCESS',
        }, {
            field : 'remark',
            title : 'T_EMC_CONTAINER_CATEGORY.REMARK',
        } ];
    },

    getMaintenanceEditListColumns : function(){
        return [ {
            checkbox : true
        }, {
            field : 'containerGroup',
            title : 'T_EMC_TEST_ORDER_CATEGORY.CONTAINERGROUP',
        }, {
            field : 'ext$.monitortmplname',
            title : 'T_EMC_TEST_ORDER_CATEGORY.MONITORTMPLNAME',
        }, {
            field : 'ext$.containercategoryname',
            title : 'T_EMC_TEST_ORDER_CATEGORY.CONTAINERCATEGORYNAME',
        }, {
            field : 'sampleqty',
            title : 'T_EMC_TEST_ORDER_CATEGORY.SAMPLEQTY',
        }, {
            field : 'sampleWeight',
            title : 'T_EMC_TEST_ORDER_CATEGORY.SAMPLEWEIGHT'
        }, {
            field : 'fixative',
            title : 'T_EMC_TEST_ORDER_CATEGORY.FIXATIVE',
        }, {
            field : 'ext$.testmethodqty',
            title : 'T_EMC_TEST_ORDER_CATEGORY.TESTMETHODQTY',
        }, {
            field : 'storeCondition',
            title : 'T_EMC_TEST_ORDER_CATEGORY.STORECONDITION',
        } ];
    },

    getMaintenanceTMListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getActiveColumn(), {
            field : 'testCategoryId',
            title : 'T_EMC_TEST_METHOD.TESTCATEGORY',
            type : 'select',
            search : true,
            category : 'testCategory',
        }, {
            field : 'testId',
            title : 'T_EMC_TEST_METHOD.TESTID',
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_TEST_METHOD.TESTNAME',
            type : 'choose',
            category : 'test',
            single : true,
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        id : Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow().id,
                        testId : rows[0].id,
                        ext$ : {
                            testname : rows[0].testName,
                        }
                    });
                    var methodId = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow().id;
                    var gridDatas = Gikam.getComp('emc-test-item-group-list-grid').getData();
                    var datas = [];
                    Gikam.each(gridDatas, function(index, row) {
                        var data = {};
                        data.id = row.id;
                        data.testId = rows[0].id;
                        data.testMethodId = methodId;
                        datas.push(data);
                    });
                    Gikam.put(Gikam.printf(emcTestMethod.baseUrl + '/{id}', {
                        id : methodId
                    }), Gikam.getJsonWrapper(null, [ 'emcItemGroupServiceImpl', datas ]));
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow().id,
                    testId : '',
                    ext$ : {
                        testname : '',
                    }
                });
                var methodId = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow().id;
                var gridDatas = Gikam.getComp('emc-test-item-group-list-grid').getData();
                var datas = [];
                Gikam.each(gridDatas, function(index, row) {
                    var data = {};
                    data.id = row.id;
                    data.testId = '';
                    data.testMethodId = methodId;
                    datas.push(data);
                });
                Gikam.put(Gikam.printf(emcTestMethod.baseUrl + '/{id}', {
                    id : methodId
                }), Gikam.getJsonWrapper(null, [ 'emcItemGroupServiceImpl', datas ]));
            },
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_TEST_METHOD.METHODNAME',
            type : 'choose',
            width : 250,
            category : 'method',
            single : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        methodId : rows[0].id,
                        ext$ : {
                            testmethodbasis : rows[0].testMethodBasis,
                            methodname : rows[0].methodName
                        }
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    methodId : '',
                    ext$ : {
                        testmethodbasis : '',
                        methodname : ''
                    }
                })
            },
        } ];
    },


    getChooseTMListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getActiveColumn(), {
            field : 'testCategoryId',
            title : 'T_EMC_TEST_METHOD.TESTCATEGORY',
            type : 'select',
            search : true,
            category : 'testCategory',
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_TEST_METHOD.TESTNAME'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_TEST_METHOD.METHODNAME'
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_TEST_METHOD.TESTMETHODBASIS',
            readonly : true
        }, {
            field : 'testId',
            title : 'T_EMC_TEST_METHOD.TESTID'
        } ];
    },
});
