emcTrainCourse.editPage = {

    getCourseGridParam : function() {
        var _this = this;
        return {
            type : 'Grid',
            id : 'emc-train-course-edit-list-grid',
            url : emcTrainCourse.baseUrl + '/queries/raw',
            service : 'emcTrainCourseServiceImpl',
	        requestData : {
	            orgId_SB : workspace.user.ext$.parentorgid
	        }, 
            columns : emcTrainCourse.getEditListColumns(),
            checkOnActive : false,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "emc-train-course-edit-list-detail-form", "emc-train-course-edit-list-test-method-grid" ]);
                }
            },
            onRowActive : function(index, row) {
                _this.onCourseActive(row);
            },
            deleteFormatter : function(row) {
                return row.courseName || row.courseCode || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        fields : emcTrainCourse.getEditListColumns(),
                        url : emcTrainCourse.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-train-course-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-train-course-edit-list-grid').deleteRows(emcTrainCourse.baseUrl).done(function() {
                        Gikam.getComp('emc-train-course-edit-list-grid').refresh();
                    });
                }
            } ]
        }
    },

    onCourseActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-train-course-edit-list-detail-form').refresh({
                url : _this.getDetailUrl(row.id)
            });

            Gikam.getComp('emc-train-course-edit-list-test-method-grid').refresh({
                url : _this.getTestMethodUrl(row.id),
            });
        }
    },

    getDetailUrl : function(courseId) {
        if (courseId) {
            return Gikam.printf(emcTrainCourse.baseUrl + '/{id}', {
                id : courseId
            })
        }
    },
    getTestMethodUrl : function(courseId) {
        if (courseId) {
            return Gikam.printf(emcTrainCourse.baseUrl + '/{id}/test-methods/queries/raw', {
                id : courseId
            })
        }
    },

    getDetailFormFields : function() {
        return {
            id : 'emc-train-course-edit-list-detail-form',
            type : 'form',
            service : 'emcTrainCourseServiceImpl',
            fields : emcTrainCourse.getBaseInfoFormFields(),
            columns : 1
        }
    },

    getTestMethodGridParam : function() {
        var _this = this;
        return {
            id : 'emc-train-course-edit-list-test-method-grid-tab',
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.TRAINCOURSES.TAB.TEST_METHOD',
                items : [ {
                    type : 'grid',
                    id : 'emc-train-course-edit-list-test-method-grid',
                    service : 'emcTcTestMethodServiceImpl',
                    deleteFormatter : function(row) {
                        return row.ext$.testname || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertTestMethod()
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            _this.deleteTestMethod()
                        }
                    }, ],
                    columns : emcTrainCourse.getTestMethodListColumns()
                } ]

            } ]
        }
    },

    insertTestMethod : function() {
        var activedRow = Gikam.getComp('emc-train-course-edit-list-grid').getActivedRow();
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BASEMDM.TRAINCOURSES.TAB.TEST_METHOD_ADD',
            url : '/secure/emc/module/mdm/bizmdm/tests/page/tc-test-method-choose-list' + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_TC_TEST_METHOD',
                targetTestMethodIdColumn : 'TESTMETHODID',
                targetFilterColumn : 'courseId',
                targetFilterValue : activedRow.id
            }),
            onAfterClose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var url = Gikam.printf(emcTrainCourse.baseUrl + '/{id}/test-methods', {
                    id : activedRow.id,
                });
                var chooseData = [];
                Gikam.each(rows, function(index, item) {
                    chooseData.push({
                        testMethodId : item.id,
                        methodId : item.methodId,
                        testId : item.testId,
                    })
                })
                Gikam.getComp('emc-train-course-edit-list-test-method-grid').insert(url, chooseData);
            }
        });
    },

    deleteTestMethod : function() {
        var activedRow = Gikam.getComp('emc-train-course-edit-list-grid').getActivedRow();
        var url = Gikam.printf(emcTrainCourse.baseUrl + '/{id}/test-methods', {
            id : activedRow.id,
        });
        Gikam.getComp('emc-train-course-edit-list-test-method-grid').deleteRows(url);
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-train-course-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 900,
                items : [ _this.getCourseGridParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : '50%',
                        items : [ _this.getDetailFormFields() ]
                    },
                    center : {
                        items : [ _this.getTestMethodGridParam() ]
                    }
                } ]
            },

        });
    },

    init : function() {
        this.create();
    }
};
