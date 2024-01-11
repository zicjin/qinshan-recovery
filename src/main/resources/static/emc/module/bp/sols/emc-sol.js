var emcSol = Gikam.getPageObject({

    getEditListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'calibrateDate',
            title: 'T_EMC_SOL.CALIBRATEDATE',
            type: 'date',
            width: 160,
            editor: true
        }, {
            field: 'solNo',
            title: 'T_EMC_SOL.SOLNO',
            width: 160,
            type: 'link',
        }, {
            field: 'ext$.tocalsolname',
            title: 'T_EMC_SOL.TOCALIBRATESOLNAME',
            type: 'comboBox',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'toCalibrateSol',
            },
            width: 200,
            editor: false,
            editable: true,
        }, {
            field: 'ext$.volsolname',
            width: 200,
            title: 'T_EMC_SOL.SOLNAME',
        }, {
            field: 'methodName',
            title: 'T_EMC_SOL.METHODNAME',
            editor: true,
            type: 'choose',
            category: 'method',
            single: true,
            width: 200,
            onAfterChoose: function (rows, index) {
                if (rows) {
                    this.setData({
                        index: index,
                        methodId: rows[0].id,
                        methodName: rows[0].methodName,
                    })
                }
            },
            onClean: function () {
                this.setData({
                    id: this.getActivedRow().id,
                    methodId: '',
                    methodName: ''
                })
            },
        }, {
            field: 'status',
            title: 'T_EMC_SOL.STATUS',
            type: 'select',
            category: 'sys',
            width: 160,
            param: {
                codeCategoryId: 'yesorno',
            },
        }];
    },

    getDetailListColumns: function () {
        return [{
            checkbox: true
        },
            {
                field: 'solName',
                title: 'T_EMC_SOL_LINE.SOLNAME',
                validators: ['strLength[0,60]'],
                editor: true
            },
            {
                field: 'prepareConcent',
                title: 'T_EMC_SOL_LINE.PREPARECONCENT',
                validators: ['strLength[0,60]'],
                editor: true
            },
            {
                field: 'prepareConcentUnit',
                title: 'T_EMC_SOL_LINE.PREPARECONCENTUNIT',
                validators: ['strLength[0,60]'],
                editor: true,
                type: 'choose',
                category: 'unit',
                single: true,
                onAfterChoose: function (rows) {
                    if (!Gikam.isEmpty(rows)) {
                        var grid = Gikam.getComp('emc-sol-detail-grid');
                        grid.setData({
                            id: grid.getActivedRow().id,
                            prepareConcentUnit: rows[0].unitName

                        });
                    }
                },
                onClean: function () {
                    var activedRow = Gikam.getComp('emc-sol-detail-grid').getActivedRow();
                    this.setData({
                        id: activedRow.id,
                        prepareConcentUnit: ''
                    });
                }
            },
            {
                field: 'toCalibrateSolName',
                title: 'T_EMC_SOL_LINE.TOCALIBRATESOLNAME',
                validators: ['strLength[0,60]'],
                editor: true,
                type: 'comboBox',
                search: true,
                category: 'sys',
                param: {
                    codeCategoryId: 'toCalibrateSol',
                },
                editor: true,
                editable: true,
            },
            {
                field: 'prepareDate',
                title: 'T_EMC_SOL_LINE.CALIBRATEDATE',
                type: 'date',
                editor: true
            },
            {
                field: 'prepareUserName',
                title: 'T_EMC_SOL_LINE.PREPAREUSERNAME',
                type: 'choose',
                category: 'user-simple',
                single: true,
                editor: true,
                onAfterChoose: function (rows) {
                    if (!Gikam.isEmpty(rows)) {
                        var grid = Gikam.getComp('emc-sol-detail-grid');
                        grid.setData({
                            id: grid.getActivedRow().id,
                            prepareUserId: rows[0].id,
                            prepareUserName: rows[0].userName
                        });
                    }
                },
                onClean: function () {
                    var activedRow = Gikam.getComp('emc-sol-detail-grid').getActivedRow();
                    this.setData({
                        id: activedRow.id,
                        prepareUserId: '',
                        prepareUserName: '',
                    });
                }
            },
            {
                field: 'partakeUserName',
                title: 'T_EMC_SOL_LINE.PARTAKEUSERNAME',
                type: 'choose',
                category: 'user-simple',
                editor: true,
                onAfterChoose: function (rows) {
                    if (!Gikam.isEmpty(rows)) {
                        var completeUser = '';
                        var completeUserName = '';
                        Gikam.each(rows, function (index, data) {
                            completeUser += ',' + data.id;
                            completeUserName += ',' + data.userName;
                        })
                        var grid = Gikam.getComp('emc-sol-detail-grid');
                        grid.setData({
                            id: grid.getActivedRow().id,
                            partakeUserId: completeUser.substring(1),
                            partakeUserName: completeUserName.substring(1)
                        });
                    }
                },
                onClean: function () {
                    var activedRow = Gikam.getComp('emc-sol-detail-grid').getActivedRow();
                    this.setData({
                        id: activedRow.id,
                        partakeUserId: '',
                        partakeUserName: '',
                    });
                }
            }, {
                field: 'ext$.stdsolvolume',
                title: 'T_EMC_SOL_LINE.STDSOLVOLUME',
                editor: true
            }, {
                field: 'ext$.stdsolvolumeunit',
                title: 'T_EMC_SOL_LINE.STDSOLVOLUMEUNIT',
                editor: true
            }

        ];
    },

    getSolFormFields: function () {
        return [{
            field: 'ext$.tocalibsoltpt',
            title: 'T_EMC_SOL.TOCALIBSOLTPT',
            type: 'textAddon',
            addon: '℃'
        }, {
            field: 'ext$.tocalibsolprocesstime',
            title: 'T_EMC_SOL.TOCALIBSOLPROCESSTIME',
            type: 'textAddon',
            addon: 'h'
        }, {
            field: 'toCalibrateSolVolume',
            title: 'T_EMC_SOL.TOCALIBRATESOLVOLUME',
            validators: ['number', 'numRange(0,)'],
        }, {
            field: 'ext$.tocalibsolweightunit',
            title: 'T_EMC_SOL.TOCALIBSOLWEIGHTUNIT',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'calibsolweightunit',
            }
        }, {
            field: 'ext$.tocalibsolconstantvolume',
            title: 'T_EMC_SOL.TOCALIBSOLCONSTANTVOLUME',
            type: 'textAddon',
            addon: 'ml'
        }, {
            field: 'revisionRule',
            title: 'T_EMC_SOL.REVISIONRULE',
            type: 'choose',
            category: 'rounding-rule',
            single: true,
            onAfterChoose: function (back, index) {
                if (back) {
                    this.setData({
                        index: index,
                        'revisionRule': back
                    });
                }
            },
        }, {
            field: 'script',
            title: 'T_EMC_SOL_LINE.CALIBRATESOLSCRIPT',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'calibrateSolScript',
            },
            colspan: 2
        }, {
            field: 'ext$.calibsoltpt',
            title: 'T_EMC_SOL.CALIBSOLTPT',
            type: 'textAddon',
            addon: '℃'
        }, {
            field: 'ext$.calibsolprocesstime',
            title: 'T_EMC_SOL.CALIBSOLPROCESSTIME',
            type: 'textAddon',
        }, {
            field: 'ext$.coefficient',
            title: '系数',
            type: 'textAddon',
        }, {
            field: 'takeWeightOrVolume',
            title: 'T_EMC_SOL.TAKEWEIGHTORVOLUME',
            validators: ['number', 'numRange(0,)'],

        }, {
            field: 'ext$.calibsolweightunit',
            title: 'T_EMC_SOL.CALIBSOLWEIGHTUNIT',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'calibsolweightunit',
            },
        }, {
            field: 'calibSolConstantVolume',
            title: 'T_EMC_SOL.CALIBSOLCONSTANTVOLUME',
            type: 'textAddon',
            addon: 'ml',
        }
        ]
    },

    getResultFormFields: function () {
        return [{
            field: 'calibratedConcent',
            title: 'T_EMC_SOL.CALIBRATEDCONCENT',
            validators: ['number'],
            editor: true,
        }, {
            field: 'script',
            title: 'T_EMC_SOL.SCRIPT',
            validators: ['strLength [0,40]'],
            type: 'richText',
            editor: true,
        }]
    },

    getRecordListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'titrationNum',
            title: 'T_EMC_SOL_RECORD.TITRATIONNUM',
            editor: true,
            onBeforeEditorRender: function (row) {
                if (Gikam.isEmpty(row.titrationNum)) {
                    return true
                }
                return false;
            }

        }, {
            field: 'samplingVolume',
            title: 'T_EMC_SOL_RECORD.SAMPLINGVOLUME',
            validators: ['notEmpty', 'number'],
            width: 160,
            editor: true,
        }, {
            field: 'blankStartValue',
            title: 'T_EMC_SOL_RECORD.BLANKSTARTVALUE',
            validators: ['notEmpty', 'number'],
            width: 160,
            editor: true,
        }, {
            field: 'blankFinalValue',
            title: 'T_EMC_SOL_RECORD.BLANKFINALVALUE',
            validators: ['notEmpty', 'number'],
            width: 160,
            editor: true,
        }, {
            field: 'standardStartValue',
            title: 'T_EMC_SOL_RECORD.STANDARDSTARTVALUE',
            validators: ['notEmpty', 'number'],
            width: 160,
            editor: true,
        }, {
            field: 'standardFinalValue',
            title: 'T_EMC_SOL_RECORD.STANDARDFINALVALUE',
            validators: ['notEmpty', 'number'],
            width: 160,
            editor: true,
        }, {
            field: 'calibrationConcentration',
            title: 'T_EMC_SOL_RECORD.CALIBRATIONCONCENTRATION',
            validators: ['notEmpty', 'number'],
            width: 160,
        }, {
            field: 'averageConcentration',
            width: 160,
            title: 'T_EMC_SOL_RECORD.AVERAGECONCENTRATION'
        }, {
            field: 'fourRelativeDeviation',
            width: 170,
            title: 'T_EMC_SOL_RECORD.FOURRELATIVEDEVIATION',
        }, {
            field: 'eightRelativeDeviation',
            width: 170,
            title: 'T_EMC_SOL_RECORD.EIGHTRELATIVEDEVIATION',
        }, {
            field: 'remark',
            title: 'T_EMC_SOL_RECORD.REMARK',
            validators: ['number'],
            editor: true,
        }
        ];
    },
    getPreInsertRecordFields: function () {
        return [{
            field: 'samplingVolume',
            title: 'T_EMC_SOL_RECORD.SAMPLINGVOLUME',
            type: 'number',
        }, {
            field: 'blankStartValue',
            title: 'T_EMC_SOL_RECORD.BLANKSTARTVALUE',
            type: 'number',
        }, {
            field: 'blankFinalValue',
            title: 'T_EMC_SOL_RECORD.BLANKFINALVALUE',
            type: 'number',
        }, {
            field: 'standardStartValue',
            title: 'T_EMC_SOL_RECORD.STANDARDSTARTVALUE',
            type: 'number',
        }, {
            field: 'standardFinalValue',
            title: 'T_EMC_SOL_RECORD.STANDARDFINALVALUE',
            type: 'number',
        }
        ];
    },

    getInvalidResultColumns: function () {
        return [{
            field: 'reason',
            title: 'T_EMC_ORDER_TASK_RESULT.REASON',
            type: 'comboBox',
            category: 'sys',
            param: {
                codeCategoryId: 'reason'
            },
            colspan: 2,
            validators: ['strLength[0,11]'],
        },];
    },

    baseUrl: IFM_CONTEXT + '/secure/emc/module/bp/sols',

    getPreInsertFields: function () {
        return [{
            field: 'methodName',
            title: 'T_EMC_SOL.METHODNAME',
            preInsert: true,
            type: 'choose',
            category: 'method',
            single: true,
            onAfterChoose: function (rows, index) {
                if (rows) {
                    this.setData({
                        index: index,
                        methodId: rows[0].id,
                        methodName: rows[0].methodName,
                    })
                }
            },
            onClean: function () {
                this.setData({
                    id: this.getActivedRow().id,
                    methodId: '',
                    methodName: ''
                })
            },
        }]
    },

    getShowAllFields: function () {
        return [{
            title: 'GIKAM.BUTTON.SHOWALL',
            field: 'showFlag',
            type: 'simpleCheckbox',
            refreshGrid: false,
            styleFormatter: function () {
                return {
                    paddingLeft: '0'
                }
            },
            onChange: function (field, value, index) {
                workspace.window.showMask(true);
                if (value === '1') {
                    Gikam.getComp('emc-sol-edit-list-grid').refresh({
                        requestData: {
                            methodId_SEQ: ''
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    })
                } else {
                    var activedRow = Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow();
                    Gikam.getComp('emc-sol-edit-list-grid').refresh({
                        requestData: {
                            methodId_SEQ: activedRow.methodId
                        }
                    }).done(function () {
                        workspace.window.closeMask();
                    })
                }
            },
        }]
    },


});
