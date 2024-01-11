var emcEquiptCalibration = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + "/secure/emc/module/mdm/equipt/equipt-calibrations",

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNAME',
        }, {
            field : 'ext$.equiptspecification',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTSPECIFICATION',
        }, {
            field : 'ext$.equiptno',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNO',
        }, {
            field : 'calibrationNo',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONNO',
        }, {
            field : 'calibrationType',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONTYPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationType',
            },
        }, {
            field : 'ext$.calibrationequiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONEQUIPTNAME',
        }, {
            field : 'ext$.calibrationequiptspec',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONEQUIPTSPEC',
        }, {
            field : 'ext$.calibrationequiptno',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONEQUIPTNO',
        }, {
            field : 'calibrationBasis',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONBASIS',
            editor : true,
            multiple : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationBasis'
            },
        }, {
            field : 'errorScope',
            title : 'T_EMC_EQUIPT_CALIBRATION.ERRORSCOPE',
            editor : true
        }, {
            field : 'structure',
            title : 'T_EMC_EQUIPT_CALIBRATION.STRUCTURE',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'screen',
            title : 'T_EMC_EQUIPT_CALIBRATION.SCREEN',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'logo',
            title : 'T_EMC_EQUIPT_CALIBRATION.LOGO',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'power',
            title : 'T_EMC_EQUIPT_CALIBRATION.POWER',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_CALIBRATION.REMARK',
            editor : true,
        }, {
            field : 'createdByName',
            title : 'T_EMC_EQUIPT_CALIBRATION.CREATEDBYNAME',
        }, {
            field : 'recorder',
            title : 'T_EMC_EQUIPT_CALIBRATION.RECORDER',
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-equipt-calibration-edit-list-grid').getActivedRow().id,
                        recorder : rows[0].userName,
                        recorderId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-equipt-calibration-edit-list-grid').getActivedRow().id,
                    recorder : '',
                    recorderId : ''
                });
            }
        }, {
            field : 'reviewer',
            title : 'T_EMC_EQUIPT_CALIBRATION.REVIEWER',
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-equipt-calibration-edit-list-grid').getActivedRow().id,
                        reviewer : rows[0].userName,
                        reviewerId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-equipt-calibration-edit-list-grid').getActivedRow().id,
                    reviewer : '',
                    reviewerId : ''
                });
            }
        }, {
            field : 'calibrationDate',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONDATE',
            type : 'date',
            editor:true,
        }, {
            field : 'gasFlow',
            title : 'T_EMC_EQUIPT_CALIBRATION.GASFLOW',
            editor : true,
        }, {
            field : 'envTemperature',
            title : 'T_EMC_EQUIPT_CALIBRATION.ENVTEMPERATURE',
            editor : true,
        }, {
            field : 'envPressure',
            title : 'T_EMC_EQUIPT_CALIBRATION.ENVPRESSURE',
            editor : true,
        }, {
            field : 'envHumidity',
            title : 'T_EMC_EQUIPT_CALIBRATION.ENVHUMIDITY',
            editor : true,
        }, {
            field : 'standardGasUnit',
            title : 'T_EMC_EQUIPT_CALIBRATION.STANDARDGASUNIT',
            editor : true,
        }, {
            field : 'standardGasExpireDate',
            title : 'T_EMC_EQUIPT_CALIBRATION.STANDARDGASEXPIREDATE',
            type : 'date',
            editor : true,
        }, {
            field : 'status',
            title : 'T_EMC_EQUIPT_CALIBRATION.STATUS',
        } ];
    },

    /** 二噁英校准 */
    getDioxinEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNAME',
        }, {
            field : 'ext$.equiptspecification',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTSPECIFICATION',
        }, {
            field : 'ext$.equiptno',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNO',
        }, {
            field : 'calibrationType',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONTYPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationType',
            },
        }, {
            field : 'calibrationCategory',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationCategory',
            },
        }, {
            field : 'ext$.calibrationequiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.DIOXINCALIBRATIONEQUIPTNAME',
            editor : true
        }, {
            field : 'ext$.calibrationequiptspec',
            title : 'T_EMC_EQUIPT_CALIBRATION.DIOXINCALIBRATIONEQUIPTSPEC',
            editor : true
        }, {
            field : 'ext$.calibrationequiptno',
            title : 'T_EMC_EQUIPT_CALIBRATION.DIOXINCALIBRATIONEQUIPTNO',
            editor : true
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_CALIBRATION.REMARK',
            editor : true,
        }, {
            field : 'createdByName',
            title : 'T_EMC_EQUIPT_CALIBRATION.CREATEDBYNAME',
        }, {
            field : 'recorder',
            title : 'T_EMC_EQUIPT_CALIBRATION.RECORDER',
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-equipt-calibration-sampling-edit-list-grid').getActivedRow().id,
                        recorder : rows[0].userName,
                        recorderId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-equipt-calibration-sampling-edit-list-grid').getActivedRow().id,
                    recorder : '',
                    recorderId : ''
                });
            }
        }, {
            field : 'reviewer',
            title : 'T_EMC_EQUIPT_CALIBRATION.REVIEWER',
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-equipt-calibration-sampling-edit-list-grid').getActivedRow().id,
                        reviewer : rows[0].userName,
                        reviewerId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-equipt-calibration-sampling-edit-list-grid').getActivedRow().id,
                    reviewer : '',
                    reviewerId : ''
                });
            }
        }, {
            field : 'calibrationDate',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONDATE',
            type : 'date',
            editor:true,
        } ];
    },

    /** 标气 */
    getStdGasEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNAME',
        }, {
            field : 'ext$.equiptspecification',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTSPECIFICATION',
        }, {
            field : 'ext$.equiptno',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNO',
        }, {
            field : 'calibrationType',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONTYPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationType',
            },
        }, {
            field : 'ext$.calibrationequiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONEQUIPTNAME1',
        }, {
            field : 'ext$.calibrationequiptspec',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONEQUIPTSPEC1',
        }, {
            field : 'ext$.calibrationequiptno',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONEQUIPTNO1',
        }, {
            field : 'gasFlow',
            title : 'T_EMC_EQUIPT_CALIBRATION.GASFLOW',
            editor : true,
        }, {
            field : 'envTemperature',
            title : 'T_EMC_EQUIPT_CALIBRATION.ENVTEMPERATURE',
            editor : true,
        }, {
            field : 'envPressure',
            title : 'T_EMC_EQUIPT_CALIBRATION.ENVPRESSURE',
            editor : true,
        }, {
            field : 'envHumidity',
            title : 'T_EMC_EQUIPT_CALIBRATION.ENVHUMIDITY',
            editor : true,
        }, {
            field : 'standardGasUnit',
            title : 'T_EMC_EQUIPT_CALIBRATION.STANDARDGASUNIT',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'standardGasUnit',
            },
        }, {
            field : 'standardGasExpireDate',
            title : 'T_EMC_EQUIPT_CALIBRATION.STANDARDGASEXPIREDATE',
            type : 'date',
            editor : true,
        }, {
            field : 'ext$.dayornight',
            title : 'T_EMC_EQUIPT_CALIBRATION.DAYORNIGHT',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'dayornight'
            },
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_CALIBRATION.REMARK',
            editor : true,
        }, {
            field : 'createdByName',
            title : 'T_EMC_EQUIPT_CALIBRATION.CREATEDBYNAME',
        }, {
            field : 'recorder',
            title : 'T_EMC_EQUIPT_CALIBRATION.RECORDER',
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-equipt-calibration-capability-edit-list-grid').getActivedRow().id,
                        recorder : rows[0].userName,
                        recorderId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-equipt-calibration-capability-edit-list-grid').getActivedRow().id,
                    recorder : '',
                    recorderId : ''
                });
            }
        }, {
            field : 'reviewer',
            title : 'T_EMC_EQUIPT_CALIBRATION.REVIEWER',
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-equipt-calibration-capability-edit-list-grid').getActivedRow().id,
                        reviewer : rows[0].userName,
                        reviewerId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-equipt-calibration-capability-edit-list-grid').getActivedRow().id,
                    reviewer : '',
                    reviewerId : ''
                });
            }
        }, {
            field : 'calibrationDate',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONDATE',
            type : 'date',
            editor:true,
        } ];
    },

    /** 流量校准 */
    getFlowEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNAME',
        }, {
            field : 'ext$.equiptspecification',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTSPECIFICATION',
        }, {
            field : 'ext$.equiptno',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNO',
        }, {
            field : 'calibrationType',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONTYPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationType',
            },
        }, {
            field : 'ext$.calibrationequiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONEQUIPTNAME',
        }, {
            field : 'ext$.calibrationequiptspec',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONEQUIPTSPEC',
        }, {
            field : 'ext$.calibrationequiptno',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONEQUIPTNO',
        }, {
            field : 'calibrationBasis',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONBASIS',
            editor : true,
            multiple : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationBasis'
            },
        }, {
            field : 'errorScope',
            title : 'T_EMC_EQUIPT_CALIBRATION.ERRORSCOPE',
            editor : true
        }, {
            field : 'envTemperature',
            title : 'T_EMC_EQUIPT_CALIBRATION.ENVTEMPERATURE',
            editor : true,
        }, {
            field : 'envHumidity',
            title : 'T_EMC_EQUIPT_CALIBRATION.ENVHUMIDITY',
            editor : true,
        }, {
            field : 'envPressure',
            title : 'T_EMC_EQUIPT_CALIBRATION.ENVPRESSURE',
            editor : true,
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_CALIBRATION.REMARK',
            editor : true,
        }, {
            field : 'createdByName',
            title : 'T_EMC_EQUIPT_CALIBRATION.CREATEDBYNAME',
        }, {
            field : 'recorder',
            title : 'T_EMC_EQUIPT_CALIBRATION.RECORDER',
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-equipt-calibration-general-edit-list-grid').getActivedRow().id,
                        recorder : rows[0].userName,
                        recorderId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-equipt-calibration-general-edit-list-grid').getActivedRow().id,
                    recorder : '',
                    recorderId : ''
                });
            }
        }, {
            field : 'reviewer',
            title : 'T_EMC_EQUIPT_CALIBRATION.REVIEWER',
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-equipt-calibration-general-edit-list-grid').getActivedRow().id,
                        reviewer : rows[0].userName,
                        reviewerId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-equipt-calibration-general-edit-list-grid').getActivedRow().id,
                    reviewer : '',
                    reviewerId : ''
                });
            }
        }, {
            field : 'calibrationDate',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONDATE',
            type : 'date',
        } ];
    },

    /** 水质校准 */
    getWaterCalibrationEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNAME',
        }, {
            field : 'ext$.equiptspecification',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTSPECIFICATION',
        }, {
            field : 'ext$.equiptno',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNO',
        }, {
            field : 'calibrationType',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONTYPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationType',
            },
        }, {
            field : 'lowLimit',
            title : 'T_EMC_EQUIPT_CALIBRATION.LOWLIMIT',
            editor : true
        }, {
            field : 'calibrationBasis',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONBASIS',
            editor : true,
            multiple : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationBasis'
            },
        }, {
            field : 'errorScope',
            title : 'T_EMC_EQUIPT_CALIBRATION.ERRORSCOPE',
            editor : true
        }, {
            field : 'structure',
            title : 'T_EMC_EQUIPT_CALIBRATION.STRUCTURE',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'screen',
            title : 'T_EMC_EQUIPT_CALIBRATION.SCREEN',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'logo',
            title : 'T_EMC_EQUIPT_CALIBRATION.LOGO',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'power',
            title : 'T_EMC_EQUIPT_CALIBRATION.POWER',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'ext$.dayornight',
            title : 'T_EMC_EQUIPT_CALIBRATION.DAYORNIGHT',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'dayornight'
            },
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_CALIBRATION.REMARK',
            editor : true,
        }, {
            field : 'createdByName',
            title : 'T_EMC_EQUIPT_CALIBRATION.CREATEDBYNAME',
        }, {
            field : 'recorder',
            title : 'T_EMC_EQUIPT_CALIBRATION.RECORDER',
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-equipt-calibration-general-edit-list-grid').getActivedRow().id,
                        recorder : rows[0].userName,
                        recorderId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-equipt-calibration-general-edit-list-grid').getActivedRow().id,
                    recorder : '',
                    recorderId : ''
                });
            }
        }, {
            field : 'reviewer',
            title : 'T_EMC_EQUIPT_CALIBRATION.REVIEWER',
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-equipt-calibration-general-edit-list-grid').getActivedRow().id,
                        reviewer : rows[0].userName,
                        reviewerId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-equipt-calibration-general-edit-list-grid').getActivedRow().id,
                    reviewer : '',
                    reviewerId : ''
                });
            }
        }, {
            field : 'calibrationDate',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONDATE',
            type : 'date',
            editor:true,
        } ];
    },

    /** 选择采样校准 */
    getCalibrationChooseListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNAME',
        }, {
            field : 'ext$.equiptspecification',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTSPECIFICATION',
        }, {
            field : 'ext$.equiptno',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNO',
        }, {
            field : 'calibrationType',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONTYPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationType',
            },
        }, {
            field : 'ext$.calibrationequiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONEQUIPTNAME2',
        }, {
            field : 'ext$.calibrationequiptspec',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONEQUIPTSPEC2',
        }, {
            field : 'ext$.calibrationequiptno',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONEQUIPTNO2',
        }, {
            field : 'createdByName',
            title : 'T_EMC_EQUIPT_CALIBRATION.CREATEDBYNAME',
        }, {
            field : 'calibrationDate',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONDATE',
            type : 'date',
            editor:true,
        } ];
    },

    getSoundEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.equiptname',
            title : '声级校准器名称',
        }, {
            field : 'ext$.equiptspecification',
            title : '声级校准器型号',
        }, {
            field : 'ext$.equiptno',
            title : '声级校准器编号',
        }, {
            field : 'calibrationType',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONTYPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationType',
            },
        }, {
            field : 'stdSoundLevel',
            title : '标准声压级',
            editor : true,
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_CALIBRATION.REMARK',
            editor : true,
        }, {
            field : 'createdByName',
            title : 'T_EMC_EQUIPT_CALIBRATION.CREATEDBYNAME',
        }, {
            field : 'recorder',
            title : 'T_EMC_EQUIPT_CALIBRATION.RECORDER',
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-equipt-calibration-sampling-edit-list-grid').getActivedRow().id,
                        recorder : rows[0].userName,
                        recorderId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-equipt-calibration-sampling-edit-list-grid').getActivedRow().id,
                    recorder : '',
                    recorderId : ''
                });
            }
        }, {
            field : 'reviewer',
            title : 'T_EMC_EQUIPT_CALIBRATION.REVIEWER',
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-equipt-calibration-sampling-edit-list-grid').getActivedRow().id,
                        reviewer : rows[0].userName,
                        reviewerId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-equipt-calibration-sampling-edit-list-grid').getActivedRow().id,
                    reviewer : '',
                    reviewerId : ''
                });
            }
        }, {
            field : 'calibrationDate',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONDATE',
            type : 'date',
            editor:true,
        } ];
    },

    getDioxinSmokeLineEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'standResult',
            title : '标气浓度',
            editor : true
        }, {
            field : 'rn1',
            title : '实测浓度',
            editor : true,
        }];
    },

    getDioxinFlowLineEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'lowFlow',
            title : '低流量',
            editor : true
        }, {
            field : 'rn1',
            title : '实测浓度',
            editor : true,
        }, {
            field : 'middleFlow',
            title : '中流量',
            editor : true
        }, {
            field : 'rn2',
            title : '实测浓度',
            editor : true,
        }, {
            field : 'highFlow',
            title : '高流量',
            editor : true
        }, {
            field : 'rn3',
            title : '实测浓度',
            editor : true,
        }];
    },

    getSoundLineEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'rn1',
            title : '测量前校准声压级示值',
            editor : true,
        }, {
            field : 'rn2',
            title : '测量后校准声压级示值',
            editor : true,
        }, {
            field : 'calibrationResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONRESULT',
            editor : true,
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationResult',
            },
        } ];
    },

    getWaterTempLineEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'rn1',
            title : '仪器情况',
            editor : true,
        }, {
            field : 'resultError',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RESULTERROR',
            editor : true
        }, {
            field : 'allowError',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.ALLOWERROR',
            editor : true
        }, {
            field : 'calibrationResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONRESULT',
            editor : true,
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationResult',
            },
        } ];
    },

    getWaterPhLineEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'gasName',
            title : '校准校正溶液名称',
            editor : true
        }, {
            field : 'standResult',
            title : '标准值',
            editor : true
        }, {
            field : 'rn1',
            title : '测量值',
            editor : true,
        }, {
            field : 'resultError',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RESULTERROR',
            editor : true
        }, {
            field : 'allowError',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.ALLOWERROR',
            editor : true
        }, {
            field : 'calibrationResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONRESULT',
            editor : true,
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationResult',
            },
        } ];
    },

    getWaterCondLineEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'temperature',
            title : '电导率标液温度',
            editor : true
        }, {
            field : 'standResult',
            title : '标准值',
            editor : true
        }, {
            field : 'rn1',
            title : '测量值',
            editor : true,
        }, {
            field : 'resultError',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RESULTERROR',
            editor : true
        }, {
            field : 'allowError',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.ALLOWERROR',
            editor : true
        }, {
            field : 'calibrationResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONRESULT',
            editor : true,
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationResult',
            },
        } ];
    },

    getWaterTurbLineEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'temperature',
            title : '标准溶液温度',
            editor : true
        }, {
            field : 'standResult',
            title : '标准值',
            editor : true
        }, {
            field : 'rn1',
            title : '测量值',
            editor : true,
        }, {
            field : 'resultError',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RESULTERROR',
            editor : true
        }, {
            field : 'allowError',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.ALLOWERROR',
            editor : true
        }, {
            field : 'calibrationResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONRESULT',
            editor : true,
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationResult',
            },
        } ];
    },

    getWaterOxygenLineEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'gasName',
            title : '水饱和空气溶解氧',
            editor : true
        }, {
            field : 'standResult',
            title : '标准值',
            editor : true
        }, {
            field : 'rn1',
            title : '测量值',
            editor : true,
        }, {
            field : 'resultError',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RESULTERROR',
            editor : true
        }, {
            field : 'allowError',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.ALLOWERROR',
            editor : true
        }, {
            field : 'calibrationResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONRESULT',
            editor : true,
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationResult',
            },
        } ];
    },

    getFlowLineEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'calibrationPeriod',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONPERIOD',
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationPeriod',
            },
        }, {
            field : 'standResult',
            title : '设定值',
            editor : true
        }, {
            field : 'flowUnit',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.FLOWUNIT',
            editor : true,
            type : 'choose',
            category : 'unit',
            single : true,
            onBeforeChoose : function() {
                return {
                    calibrationUnit_SEQ : '1',
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    var grid = this;
                    grid.setData({
                        index : index,
                        flowUnit : rows[0].unitName
                    });
                }
            },
        }, {
            field : 'rn1',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RN111',
            editor : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var row = Gikam.getComp('emc-equipt-calibration-line-edit-list-grid').getData()[rowIndex];
                if (!Gikam.isEmpty(row.rn1) && !Gikam.isEmpty(row.rn2) && !Gikam.isEmpty(row.rn3)) {
                    var avgResult = (parseFloat(row.rn1) + parseFloat(row.rn2) + parseFloat(row.rn3)) / 3;
                    avgResult = avgResult.toFixed(1);
                    var errorValue = '';
                    if (!Gikam.isEmpty(row.standResult)) {
                        var error = (parseFloat(row.standResult) - avgResult) / avgResult * 100;
                        errorValue = error.toFixed(1);
                    }
                    this.setData({
                        index : rowIndex,
                        id : row.id,
                        rn1 : value,
                        avgResult : avgResult,
                        resultError : errorValue
                    })
                }
            }
        }, {
            field : 'rn2',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RN222',
            editor : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var row = Gikam.getComp('emc-equipt-calibration-line-edit-list-grid').getData()[rowIndex];
                if (!Gikam.isEmpty(row.rn1) && !Gikam.isEmpty(row.rn2) && !Gikam.isEmpty(row.rn3)) {
                    var avgResult = (parseFloat(row.rn1) + parseFloat(row.rn2) + parseFloat(row.rn3)) / 3;
                    avgResult = avgResult.toFixed(1);
                    var errorValue = '';
                    if (!Gikam.isEmpty(row.standResult)) {
                        var error = (parseFloat(row.standResult) - avgResult) / avgResult * 100;
                        errorValue = error.toFixed(1);
                    }
                    this.setData({
                        index : rowIndex,
                        id : row.id,
                        rn2 : value,
                        avgResult : avgResult,
                        resultError : errorValue
                    })
                }
            }
        }, {
            field : 'rn3',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RN333',
            editor : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var row = Gikam.getComp('emc-equipt-calibration-line-edit-list-grid').getData()[rowIndex];
                if (!Gikam.isEmpty(row.rn1) && !Gikam.isEmpty(row.rn2) && !Gikam.isEmpty(row.rn3)) {
                    var avgResult = (parseFloat(row.rn1) + parseFloat(row.rn2) + parseFloat(row.rn3)) / 3;
                    avgResult = avgResult.toFixed(1);
                    var errorValue = '';
                    if (!Gikam.isEmpty(row.standResult)) {
                        var error = (parseFloat(row.standResult) - avgResult) / avgResult * 100;
                        errorValue = error.toFixed(1);
                    }
                    this.setData({
                        index : rowIndex,
                        id : row.id,
                        rn3 : value,
                        avgResult : avgResult,
                        resultError : errorValue
                    })
                }
            }
        }, {
            field : 'avgResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.AVGRESULT11',
            editor : true
        }, {
            field : 'resultError',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RESULTERROR',
            editor : true
        }, {
            field : 'calibrationResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONRESULT',
            editor : true,
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationResult',
            },
        } ];
    },

    getStdGasLineEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'calibrationPeriod',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONPERIOD',
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationPeriod',
            },
        }, {
            field : 'gasName',
            title : '标准物质',
        }, {
            field : 'standResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.STANDRESULT1',
            editor : true
        }, {
            field : 'flowUnit',
            title : '标气单位',
            editor : true,
            type : 'choose',
            category : 'unit',
            single : true,
            onBeforeChoose : function() {
                return {
                    calibrationUnit_SEQ : '1',
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    var grid = this;
                    grid.setData({
                        index : index,
                        flowUnit : rows[0].unitName
                    });
                }
            },
        }, {
            field : 'rn1',
            title : '设备示值',
            editor : true,
        }, {
            field : 'resultError',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RESULTERROR',
            editor : true
        }, {
            field : 'calibrationResult',
            title : '判定',
            editor : true,
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationResult',
            },
        }, {
            field : 'rn2',
            title : '系统示值',
            editor : true,
        }, {
            field : 'allowError',
            title : '系统偏差',
            editor : true,
        }, {
            field : 'avgResult',
            title : '判定',
            editor : true,
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationResult',
            },
        } ];
    },

    /** 大气采样校准记录 */
    getSamplingLineEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'calibrationPeriod',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONPERIOD',
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationPeriod',
            },
        }, {
            field : 'ext$.calibrationvalue',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONVALUE',
            editor : true
        }, {
            field : 'temperature',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.TEMPERATURE',
            editor : true
        }, {
            field : 'humidity',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.HUMIDITY',
            editor : true
        }, {
            field : 'pressure',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.PRESSURE',
            editor : true
        }, {
            field : 'standResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.STANDRESULT',
            editor : true
        }, {
            field : 'flowUnit',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.FLOWUNIT',
            editor : true,
            type : 'choose',
            category : 'unit',
            single : true,
            onBeforeChoose : function() {
                return {
                    calibrationUnit_SEQ : '1',
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    var grid = this;
                    grid.setData({
                        index : index,
                        flowUnit : rows[0].unitName
                    });
                }
            },
        }, {
            field : 'rn1',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RN1',
            editor : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var row = Gikam.getComp('emc-equipt-calibration-line-edit-list-grid').getData()[rowIndex];
                if (!Gikam.isEmpty(row.rn1) && !Gikam.isEmpty(row.rn2) && !Gikam.isEmpty(row.rn3)) {
                    var avgResult = (parseFloat(row.rn1) + parseFloat(row.rn2) + parseFloat(row.rn3)) / 3;
                    avgResult = avgResult.toFixed(1);
                    var errorValue = '';
                    if (!Gikam.isEmpty(row.standResult)) {
                        var error = (parseFloat(row.standResult) - avgResult) / avgResult * 100;
                        errorValue = error.toFixed(1);
                    }
                    this.setData({
                        index : rowIndex,
                        id : row.id,
                        rn1 : value,
                        avgResult : avgResult,
                        resultError : errorValue
                    })
                }
            }
        }, {
            field : 'rn2',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RN2',
            editor : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var row = Gikam.getComp('emc-equipt-calibration-line-edit-list-grid').getData()[rowIndex];
                if (!Gikam.isEmpty(row.rn1) && !Gikam.isEmpty(row.rn2) && !Gikam.isEmpty(row.rn3)) {
                    var avgResult = (parseFloat(row.rn1) + parseFloat(row.rn2) + parseFloat(row.rn3)) / 3;
                    avgResult = avgResult.toFixed(1);
                    var errorValue = '';
                    if (!Gikam.isEmpty(row.standResult)) {
                        var error = (parseFloat(row.standResult) - avgResult) / avgResult * 100;
                        errorValue = error.toFixed(1);
                    }
                    this.setData({
                        index : rowIndex,
                        id : row.id,
                        rn2 : value,
                        avgResult : avgResult,
                        resultError : errorValue
                    })
                }
            }
        }, {
            field : 'rn3',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RN3',
            editor : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var row = Gikam.getComp('emc-equipt-calibration-line-edit-list-grid').getData()[rowIndex];
                if (!Gikam.isEmpty(row.rn1) && !Gikam.isEmpty(row.rn2) && !Gikam.isEmpty(row.rn3)) {
                    var avgResult = (parseFloat(row.rn1) + parseFloat(row.rn2) + parseFloat(row.rn3)) / 3;
                    avgResult = avgResult.toFixed(1);
                    var errorValue = '';
                    if (!Gikam.isEmpty(row.standResult)) {
                        var error = (parseFloat(row.standResult) - avgResult) / avgResult * 100;
                        errorValue = error.toFixed(1);
                    }
                    this.setData({
                        index : rowIndex,
                        id : row.id,
                        rn3 : value,
                        avgResult : avgResult,
                        resultError : errorValue
                    })
                }
            }
        }, {
            field : 'avgResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.AVGRESULT',
            editor : true
        }, {
            field : 'resultError',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RESULTERROR',
            editor : true
        }, {
            field : 'calibrationResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONRESULT',
            editor : true,
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationResult',
            },
        } ];
    },

    /** 仪器性能校准记录 */
    getCapabilityLineEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'calibrationPeriod',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONPERIOD',
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationPeriod',
            },
        }, {
            field : 'ext$.calibrationvalue',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONVALUE',
            editor : true
        }, {
            field : 'gasName',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.GASNAME1',
        }, {
            field : 'standResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.STANDRESULT1',
            editor : true
        }, {
            field : 'flowUnit',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.FLOWUNIT',
            editor : true,
            type : 'choose',
            category : 'unit',
            single : true,
            onBeforeChoose : function() {
                return {
                    calibrationUnit_SEQ : '1',
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    var grid = this;
                    grid.setData({
                        index : index,
                        flowUnit : rows[0].unitName
                    });
                }
            },
        }, {
            field : 'rn1',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RN11',
            editor : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var row = Gikam.getComp('emc-equipt-calibration-line-edit-list-grid').getData()[rowIndex];
                if (!Gikam.isEmpty(row.rn1) && !Gikam.isEmpty(row.rn2) && !Gikam.isEmpty(row.rn3)) {
                    var avgResult = (parseFloat(row.rn1) + parseFloat(row.rn2) + parseFloat(row.rn3)) / 3;
                    avgResult = avgResult.toFixed(1);
                    var errorValue = '';
                    if (!Gikam.isEmpty(row.standResult)) {
                        // 仪器性能类：示值误差=(平均值-标准值)/标准值
                        var error = (avgResult - parseFloat(row.standResult)) / parseFloat(row.standResult) * 100;
                        errorValue = error.toFixed(1);
                    }
                    this.setData({
                        index : rowIndex,
                        id : row.id,
                        rn1 : value,
                        avgResult : avgResult,
                        resultError : errorValue
                    })
                }
            }
        }, {
            field : 'rn2',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RN22',
            editor : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var row = Gikam.getComp('emc-equipt-calibration-line-edit-list-grid').getData()[rowIndex];
                if (!Gikam.isEmpty(row.rn1) && !Gikam.isEmpty(row.rn2) && !Gikam.isEmpty(row.rn3)) {
                    var avgResult = (parseFloat(row.rn1) + parseFloat(row.rn2) + parseFloat(row.rn3)) / 3;
                    avgResult = avgResult.toFixed(1);
                    var errorValue = '';
                    if (!Gikam.isEmpty(row.standResult)) {
                        // 仪器性能类：示值误差=(平均值-标准值)/标准值
                        var error = (avgResult - parseFloat(row.standResult)) / parseFloat(row.standResult) * 100;
                        errorValue = error.toFixed(1);
                    }
                    this.setData({
                        index : rowIndex,
                        id : row.id,
                        rn2 : value,
                        avgResult : avgResult,
                        resultError : errorValue
                    })
                }
            }
        }, {
            field : 'rn3',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RN33',
            editor : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var row = Gikam.getComp('emc-equipt-calibration-line-edit-list-grid').getData()[rowIndex];
                if (!Gikam.isEmpty(row.rn1) && !Gikam.isEmpty(row.rn2) && !Gikam.isEmpty(row.rn3)) {
                    var avgResult = (parseFloat(row.rn1) + parseFloat(row.rn2) + parseFloat(row.rn3)) / 3;
                    avgResult = avgResult.toFixed(1);
                    var errorValue = '';
                    if (!Gikam.isEmpty(row.standResult)) {
                        // 仪器性能类：示值误差=(平均值-标准值)/标准值
                        var error = (avgResult - parseFloat(row.standResult)) / parseFloat(row.standResult) * 100;
                        errorValue = error.toFixed(1);
                    }
                    this.setData({
                        index : rowIndex,
                        id : row.id,
                        rn3 : value,
                        avgResult : avgResult,
                        resultError : errorValue
                    })
                }
            }
        }, {
            field : 'avgResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.AVGRESULT1',
            editor : true
        }, {
            field : 'resultError',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RESULTERROR',
            editor : true
        }, {
            field : 'calibrationResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONRESULT11',
            editor : true,
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationResult',
            },
        } ];
    },

    /** 一般仪器校准 */
    getGeneralCalibrationLineEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'calibrationPeriod',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONPERIOD',
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationPeriod',
            },
        }, {
            field : 'temperature',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.TEMPERATURE',
            editor : true
        }, {
            field : 'ext$.calibrationvalue',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONVALUE',
            editor : true
        }, {
            field : 'humidity',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.HUMIDITY',
            editor : true
        }, {
            field : 'standResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.STANDRESULT11',
            editor : true
        }, {
            field : 'flowUnit',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.FLOWUNIT',
            editor : true,
            type : 'choose',
            category : 'unit',
            single : true,
            onBeforeChoose : function() {
                return {
                    calibrationUnit_SEQ : '1',
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    var grid = this;
                    grid.setData({
                        index : index,
                        flowUnit : rows[0].unitName
                    });
                }
            },
        }, {
            field : 'rn1',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RN111',
            editor : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var row = Gikam.getComp('emc-equipt-calibration-line-edit-list-grid').getData()[rowIndex];
                if (!Gikam.isEmpty(row.rn1) && !Gikam.isEmpty(row.rn2) && !Gikam.isEmpty(row.rn3)) {
                    var avgResult = (parseFloat(row.rn1) + parseFloat(row.rn2) + parseFloat(row.rn3)) / 3;
                    avgResult = avgResult.toFixed(1);
                    var errorValue = '';
                    if (!Gikam.isEmpty(row.standResult)) {
                        var error = (parseFloat(row.standResult) - avgResult) / avgResult * 100;
                        errorValue = error.toFixed(1);
                    }
                    this.setData({
                        index : rowIndex,
                        id : row.id,
                        rn1 : value,
                        avgResult : avgResult,
                        resultError : errorValue
                    })
                }
            }
        }, {
            field : 'rn2',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RN222',
            editor : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var row = Gikam.getComp('emc-equipt-calibration-line-edit-list-grid').getData()[rowIndex];
                if (!Gikam.isEmpty(row.rn1) && !Gikam.isEmpty(row.rn2) && !Gikam.isEmpty(row.rn3)) {
                    var avgResult = (parseFloat(row.rn1) + parseFloat(row.rn2) + parseFloat(row.rn3)) / 3;
                    avgResult = avgResult.toFixed(1);
                    var errorValue = '';
                    if (!Gikam.isEmpty(row.standResult)) {
                        var error = (parseFloat(row.standResult) - avgResult) / avgResult * 100;
                        errorValue = error.toFixed(1);
                    }
                    this.setData({
                        index : rowIndex,
                        id : row.id,
                        rn2 : value,
                        avgResult : avgResult,
                        resultError : errorValue
                    })
                }
            }
        }, {
            field : 'rn3',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RN333',
            editor : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var row = Gikam.getComp('emc-equipt-calibration-line-edit-list-grid').getData()[rowIndex];
                if (!Gikam.isEmpty(row.rn1) && !Gikam.isEmpty(row.rn2) && !Gikam.isEmpty(row.rn3)) {
                    var avgResult = (parseFloat(row.rn1) + parseFloat(row.rn2) + parseFloat(row.rn3)) / 3;
                    avgResult = avgResult.toFixed(1);
                    var errorValue = '';
                    if (!Gikam.isEmpty(row.standResult)) {
                        var error = (parseFloat(row.standResult) - avgResult) / avgResult * 100;
                        errorValue = error.toFixed(1);
                    }
                    this.setData({
                        index : rowIndex,
                        id : row.id,
                        rn3 : value,
                        avgResult : avgResult,
                        resultError : errorValue
                    })
                }
            }
        }, {
            field : 'avgResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.AVGRESULT11',
            editor : true
        }, {
            field : 'resultError',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.RESULTERROR',
            editor : true
        }, {
            field : 'calibrationResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONRESULT',
            editor : true,
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationResult',
            },
        } ];
    },

    getPreInsertFields : function() {
        return [ {
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNAME',
            validators : [ 'notEmpty' ],
            preInsert : true,
            single : true,
            type : 'choose',
            category : 'equipt',
            onBeforeChoose : function() {
                return {
                    equiptcategoryname_SEQ : '现场',
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        equiptId : rows[0].id,
                        ext$ : {
                            equiptname : rows[0].equiptName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    equiptId : '',
                    ext$ : {
                        equiptname : ''
                    }
                });
            }
        }, {
            field : 'ext$.calibrationequiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONEQUIPTNAME',
            validators : [ 'notEmpty' ],
            preInsert : true,
            single : true,
            type : 'choose',
            category : 'equipt',
            onBeforeChoose : function() {
                return {
                    equiptcategoryname_SEQ : '现场',
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        calibrationEquiptId : rows[0].id,
                        ext$ : {
                            calibrationequiptname : rows[0].equiptName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    calibrationEquiptId : '',
                    ext$ : {
                        calibrationequiptname : ''
                    }
                });
            }
        }, {
            field : 'calibrationType',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONTYPE',
            validators : [ 'notEmpty' ],
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationType',
            },
            preInsert : true,
        } ];
    },

    getDioxinPreInsertFields : function() {
        return [ {
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNAME',
            validators : [ 'notEmpty' ],
            preInsert : true,
            single : false,
            type : 'choose',
            category : 'equipt',
            onBeforeChoose : function() {
                return {
                    targetType : 'calibration',
                    calibrationType  : 'dioxinCalibration',
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var multiEquiptIds = [];
                    var multiEquiptNames = [];
                    Gikam.each(rows, function(index, item) {
                        multiEquiptNames.push(item.equiptName);
                        multiEquiptIds.push(item.id);
                    });
                    this.setData({
                        equiptId : rows[0].id,
                        ext$ : {
                            multiequiptid : multiEquiptIds.join(','),
                            equiptname : multiEquiptNames.join(',')
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    equiptId : '',
                    ext$ : {
                        multiequiptid : '',
                        equiptname : ''
                    }
                });
            }
        }, {
            field : 'calibrationCategory',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONCATEGORY',
            validators : [ 'notEmpty' ],
            preInsert : true,
            single : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationCategory',
            },
        }, {
            field : 'calibrationType',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONTYPE',
            validators : [ 'notEmpty' ],
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationType',
            },
            preInsert : true,
            value : 'dioxinCalibration'
        } ];
    },

    getStdGasPreInsertFields : function() {
        return [ {
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNAME',
            validators : [ 'notEmpty' ],
            preInsert : true,
            single : false,
            type : 'choose',
            category : 'equipt',
            onBeforeChoose : function() {
                return {
                    targetType : 'calibration',
                    calibrationType  : 'stdGasCalibration',
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var multiEquiptIds = [];
                    var multiEquiptNames = [];
                    Gikam.each(rows, function(index, item) {
                        multiEquiptNames.push(item.equiptName);
                        multiEquiptIds.push(item.id);
                    });
                    this.setData({
                        equiptId : rows[0].id,
                        ext$ : {
                            multiequiptid : multiEquiptIds.join(','),
                            equiptname : multiEquiptNames.join(',')
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    equiptId : '',
                    ext$ : {
                        multiequiptid : '',
                        equiptname : ''
                    }
                });
            }
        }, {
            field : 'calibrationType',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONTYPE',
            validators : [ 'notEmpty' ],
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationType',
            },
            preInsert : true,
            value : 'stdGasCalibration'
        } ];
    },

    getFlowPreInsertFields : function() {
        return [ {
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNAME',
            validators : [ 'notEmpty' ],
            preInsert : true,
            single : false,
            type : 'choose',
            category : 'equipt',
            onBeforeChoose : function() {
                return {
                    targetType : 'calibration',
                    calibrationType  : 'flowCalibration',
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var multiEquiptIds = [];
                    var multiEquiptNames = [];
                    Gikam.each(rows, function(index, item) {
                        multiEquiptNames.push(item.equiptName);
                        multiEquiptIds.push(item.id);
                    });
                    this.setData({
                        equiptId : rows[0].id,
                        ext$ : {
                            multiequiptid : multiEquiptIds.join(','),
                            equiptname : multiEquiptNames.join(',')
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    equiptId : '',
                    ext$ : {
                        multiequiptid : '',
                        equiptname : ''
                    }
                });
            }
        }, {
            field : 'calibrationType',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONTYPE',
            validators : [ 'notEmpty' ],
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationType',
            },
            preInsert : true,
            value : 'flowCalibration'
        } ];
    },

    getWaterPreInsertFields : function() {
        return [ {
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNAME',
            validators : [ 'notEmpty' ],
            preInsert : true,
            single : false,
            type : 'choose',
            category : 'equipt',
            onBeforeChoose : function() {
                return {
                    targetType : 'calibration',
                    calibrationType  : 'waterTempCalibration,phCalibration,turbidityCalibration,conductivityCalibration,oxygenCalibration',
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var multiEquiptIds = [];
                    var multiEquiptNames = [];
                    Gikam.each(rows, function(index, item) {
                        multiEquiptNames.push(item.equiptName);
                        multiEquiptIds.push(item.id);
                    });
                    this.setData({
                        equiptId : rows[0].id,
                        ext$ : {
                            multiequiptid : multiEquiptIds.join(','),
                            equiptname : multiEquiptNames.join(',')
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    equiptId : '',
                    ext$ : {
                        multiequiptid : '',
                        equiptname : ''
                    }
                });
            }
        }, {
            field : 'calibrationType',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONTYPE',
            validators : [ 'notEmpty' ],
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationType',
            },
            preInsert : true,
        } ];
    },

    getSoundPreInsertFields : function() {
        return [ {
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNAME',
            validators : [ 'notEmpty' ],
            preInsert : true,
            single : false,
            type : 'choose',
            category : 'equipt',
            onBeforeChoose : function() {
                return {
                    targetType : 'calibration',
                    calibrationType : 'soundCalibration',
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var multiEquiptIds = [];
                    var multiEquiptNames = [];
                    Gikam.each(rows, function(index, item) {
                        multiEquiptNames.push(item.equiptName);
                        multiEquiptIds.push(item.id);
                    });
                    this.setData({
                        equiptId : rows[0].id,
                        ext$ : {
                            multiequiptid : multiEquiptIds.join(','),
                            equiptname : multiEquiptNames.join(',')
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    equiptId : '',
                    ext$ : {
                        multiequiptid : '',
                        equiptname : ''
                    }
                });
            }
        }, {
            field : 'calibrationType',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONTYPE',
            validators : [ 'notEmpty' ],
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationType',
            },
            preInsert : true,
            value : 'soundCalibration'
        } ];
    },

    getPreInsertFields2 : function() {
        return [ {
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNAME',
            validators : [ 'notEmpty' ],
            preInsert : true,
            single : true,
            type : 'choose',
            category : 'equipt',
            onBeforeChoose : function() {
                return {
                    equiptcategoryname_IN : '现场,现场校准标准气',
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        equiptId : rows[0].id,
                        ext$ : {
                            equiptname : rows[0].equiptName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    equiptId : '',
                    ext$ : {
                        equiptname : ''
                    }
                });
            }
        }, {
            field : 'ext$.calibrationequiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONEQUIPTNAME1',
            validators : [ 'notEmpty' ],
            preInsert : true,
            single : true,
            type : 'choose',
            category : 'equipt',
            onBeforeChoose : function() {
                return {
                    equiptcategoryname_IN : '现场,现场校准标准气',
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        calibrationEquiptId : rows[0].id,
                        ext$ : {
                            calibrationequiptname : rows[0].equiptName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    calibrationEquiptId : '',
                    ext$ : {
                        calibrationequiptname : ''
                    }
                });
            }
        }, {
            field : 'calibrationType',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONTYPE',
            validators : [ 'notEmpty' ],
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationType',
            },
            preInsert : true,
            value : 'stdGasCalibration'
        } ];
    },

    getPreInsertFields3 : function() {
        return [ {
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.EQUIPTNAME',
            validators : [ 'notEmpty' ],
            preInsert : true,
            single : true,
            type : 'choose',
            category : 'equipt',
            onBeforeChoose : function() {
                return {
                    equiptcategoryname_IN : '现场,现场校准标准气',
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        equiptId : rows[0].id,
                        ext$ : {
                            equiptname : rows[0].equiptName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    equiptId : '',
                    ext$ : {
                        equiptname : ''
                    }
                });
            }
        }, {
            field : 'ext$.calibrationequiptname',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONEQUIPTNAME',
            validators : [ 'notEmpty' ],
            preInsert : true,
            single : true,
            type : 'choose',
            category : 'equipt',
            onBeforeChoose : function() {
                return {
                    equiptcategoryname_IN : '现场,现场校准标准气',
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        calibrationEquiptId : rows[0].id,
                        ext$ : {
                            calibrationequiptname : rows[0].equiptName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    calibrationEquiptId : '',
                    ext$ : {
                        calibrationequiptname : ''
                    }
                });
            }
        }, {
            field : 'calibrationType',
            title : 'T_EMC_EQUIPT_CALIBRATION.CALIBRATIONTYPE',
            validators : [ 'notEmpty' ],
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationType',
            },
            preInsert : true,
            value : 'flowCalibration'
        } ];
    },

    getBatchInputFields : function () {
        return [ {
            field : 'temperature',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.TEMPERATURE',
            preInsert : true
        }, {
            field : 'humidity',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.HUMIDITY',
            preInsert : true
        }, {
            field : 'pressure',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.PRESSURE',
            preInsert : true
        }, {
            field : 'calibrationResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONRESULT',
            preInsert : true,
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationResult',
            },
        } ];
    },

    getBatchInputFields1 : function () {
        return [ {
            field : 'temperature',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.TEMPERATURE',
            preInsert : true
        }, {
            field : 'humidity',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.HUMIDITY',
            preInsert : true
        }, {
            field : 'calibrationResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONRESULT',
            preInsert : true,
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationResult',
            },
        } ];
    },

    getBatchInputFields2 : function () {
        return [ {
            field : 'calibrationResult',
            title : 'T_EMC_EQUIPT_CALIBRATION_LINE.CALIBRATIONRESULT',
            preInsert : true,
            type : 'select',
            firstBlank : false,
            category : 'sys',
            param : {
                codeCategoryId : 'calibrationResult',
            },
        } ];
    },

    getCapabilityBatchInputFields : function () {
        return [ {
            field : 'envTemperature',
            title : 'T_EMC_EQUIPT_CALIBRATION.ENVTEMPERATURE',
            preInsert : true,
        }, {
            field : 'envPressure',
            title : 'T_EMC_EQUIPT_CALIBRATION.ENVPRESSURE',
            preInsert : true,
        }, {
            field : 'envHumidity',
            title : 'T_EMC_EQUIPT_CALIBRATION.ENVHUMIDITY',
            preInsert : true,
        } ];
    },

})
