emcSol.detailPage = {
    getGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-sol-detail-grid',
            url: Gikam.printf(emcSol.baseUrl + '/{id}/lines/queries', {
                id: _this.param.id
            }),
            columns: emcSol.getDetailListColumns(),
            service: 'emcSolLineServiceImpl',
            checkOnActive: true,
            checkOneOnActive: true,
            deleteFormatter: function (row) {
                return row.solName || row.id;
            },
            onLoadSuccess: function (data) {
                if (data.length > 0) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData(['emc-sol-detail-record-grid']);
                }
            },
            onUpdated: function (data, fields, index) {
                if (fields.indexOf('prepareConcent') > -1) {
                    emcSol.detailPage.calcSolResult();
                }
            },
            onRowActive: function (index, row) {
                Gikam.getComp('emc-sol-detail-record-layout').setSize({
                    east: {
                        width: '0%'
                    }
                });
                Gikam.getComp('emc-sol-detail-result-tab').hide();
            },
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.INSERT',
                icon: 'add',
                onClick: function () {
                    Gikam.getComp('emc-sol-detail-grid').insert(emcSol.baseUrl + '/lines', [{solId: _this.param.id}]);
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.DELETE',
                icon: 'remove',
                onClick: function () {
                    Gikam.getComp('emc-sol-detail-grid').deleteRows(emcSol.baseUrl + '/lines');
                }
            }, /*{
                type : 'button',
                text : 'EMC.MODULE.BP.SOLS.BUTTON.CALC',
                icon : 'material-to-check',
                hidden : true,
                onClick : function() {
                    // 计算标准溶液配制后浓度
/!*                    var grid = Gikam.getComp('emc-sol-detail-grid');
                    var selections = grid.getSelections();
                    if (selections.length < 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    if (grid.validate()) {
                        Gikam.getLastModal().window.showMask(true);
                        Gikam.put(emcSol.baseUrl + '/action/calucate-prepare', Gikam.getJsonWrapper(null, [ '', selections ])).done(function() {
                            Gikam.getComp('emc-sol-detail-grid').refresh();
                        }).always(function() {
                            Gikam.getLastModal().window.closeMask();
                        });
                    }*!/
                }
            } */]
        }
    },

    getSolFormGridParam: function () {
        var _this = this;
        return {
            id: 'emc-sol-detail-form',
            type: 'form',
            columns: 5,
            titleWidth: 130,
            service: 'emcSolServiceImpl',
            url: Gikam.printf(emcSol.baseUrl + '/{id}', {
                id: _this.param.id
            }),
            autoSave: true,

            fields: emcSol.getSolFormFields(),
            onLoadSuccess: function () {
                this.setData({
                    formula: Gikam.propI18N('EMC.MODULE.BP.SOLS.TIP.DEFAULT_FORMULA')
                }, false);
            },
            onUpdated: function (data) {
                if (Gikam.isNotEmpty(data.revisionRule)
                    || Gikam.isNotEmpty(data.script)
                    || Gikam.isNotEmpty(data.takeWeightOrVolume)
                    || Gikam.isNotEmpty(data.calibSolConstantVolume)) {
                    emcSol.detailPage.calcSolResult();
                }
            }
        }
    },

    getSolRecordGridParam: function () {
        var _this = this;
        return {
            type: 'grid',
            id: 'emc-sol-detail-record-grid',
            columns: emcSol.getRecordListColumns(),
            service: 'emcSolRecordServiceImpl',
            url: Gikam.printf(emcSol.baseUrl + '/{id}/records/queries', {
                id: _this.param.id
            }),
            order: {
                'titrationNum': 'asc'
            },
            toolbar: [{
                type: 'button',
                text: 'GIKAM.BUTTON.INSERT',
                icon: 'add',
                onClick: function () {
                    const dataLen = Gikam.getComp('emc-sol-detail-record-grid').getData().length;
                    if (dataLen < 8) {
                        for (let index = 0; index < 8 - dataLen; index++) {
                            Gikam.preInsert({
                                fields: emcSol.getRecordListColumns(),
                                url: Gikam.printf(emcSol.baseUrl + '/{id}/records', {
                                    id: _this.param.id
                                }),
                                param: {
                                    titrationNum: dataLen + index + 1
                                }
                            }).done(function () {
                                Gikam.getComp('emc-sol-detail-record-grid').refresh();
                            })
                        }
                    } else {
                        Gikam.preInsert({
                            fields: emcSol.getRecordListColumns(),
                            url: Gikam.printf(emcSol.baseUrl + '/{id}/records', {
                                id: _this.param.id
                            }),
                            param: {
                                titrationNum: dataLen + 1
                            }
                        }).done(function () {
                            Gikam.getComp('emc-sol-detail-record-grid').refresh();
                        })
                    }

                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.DELETE',
                icon: 'remove',
                onClick: function () {
                    Gikam.getComp('emc-sol-detail-record-grid').deleteRows(emcSol.baseUrl + '/records');
                    emcSol.detailPage.calcSolResult();
                }
            }, {
                type: 'button',
                text: 'EMC.MODULE.BP.SOL.RECORD.BUTTON.BATCH_INPUT',
                icon: 'add',
                onClick: function () {
                    _this.batchInput();
                }
            }, {
                type: 'dropDownMenu',
                text: 'EMC.MODULE.BP.SOL.RECORD.BUTTON.QUICK_INPUT',
                items: [{
                    type: 'button',
                    text: 'GIKAM.BUTTON.EXPORT',
                    icon: 'move-up',
                    onClick: function () {
                        _this.quickExport();
                    }
                }, {
                    type: 'button',
                    text: 'GIKAM.BUTTON.IMPORT',
                    icon: 'move-down',
                    onClick: function () {
                        _this.quickImport();
                    }
                }]
            },
                {
                    type: 'button',
                    text: 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.INVALID_DATA',
                    icon: 'edit',
                    onClick: function () {
                        _this.invalidData();
                    }
                },
                /*{
                type: 'button',
                text: 'EMC.MODULE.BP.SOLS.BUTTON.CALC',
                icon: 'material-to-check',
                onClick: function () {
                    var grid = Gikam.getComp('emc-sol-detail-grid');
                    var recordGrid = Gikam.getComp('emc-sol-detail-record-grid');

                    var selections = grid.getData();
                    if (selections.length < 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    if (Gikam.isEmpty(recordGrid.getData())) {
                        Gikam.alert('EMC.MODULE.BP.SOLS.TIP.NOT_NULL_RECORD');
                        return;
                    }
                    var flag = '';
                    var form = Gikam.getComp('emc-sol-detail-form');
                    if (Gikam.isEmpty(form.getField('toCalibrateSolVolume'))) {
                        flag = 'EMC.MODULE.BP.SOLS.TIP.NO_TOCALIBRATESOLVOLUME';
                    }

                    var hasRequiredValue = selections.some(function (item) {
                        return !Gikam.isEmpty(item.calibrateSolConcent) && !Gikam.isEmpty(item.calibrateSolVolume);
                    });
                    if (!hasRequiredValue) {
                        Gikam.alert('EMC.MODULE.BP.SOLS.TIP.NO_CALIBRATESOLCONCENT');
                        return;
                    }

                    if (Gikam.isEmpty(flag) && Gikam.getComp('emc-sol-detail-record-grid').validate()) {
                        Gikam.getLastModal().window.showMask(true);
                        var activedRow = Gikam.getComp('emc-sol-detail-grid').getActivedRow();
                        Gikam.put(emcSol.baseUrl + '/action/calucate-calibrate', Gikam.getJsonWrapper(null, ['', [activedRow]])).done(function () {
                            //Gikam.getComp('emc-sol-detail-grid').refresh();
                            Gikam.getComp('emc-sol-detail-form').refresh();
                        }).always(function () {
                            Gikam.getLastModal().window.closeMask();
                        });
                    } else {
                        Gikam.alert(flag);
                        return;
                    }
                }
            },*/ {
                    type: 'button',
                    text: 'EMC.MODULE.BP.SOLS.BUTTON.INPUT',
                    icon: 'add-row',
                    hidden: true,
                    onClick: function () {
                        Gikam.getComp('emc-sol-detail-record-layout').setSize({
                            east: {
                                width: '26%'
                            }
                        });
                        Gikam.getComp('emc-sol-detail-result-tab').show();
                        Gikam.getComp('emc-sol-detail-result-form').refresh({
                            url: Gikam.printf(emcSol.baseUrl + '/{id}', {
                                id: Gikam.getComp('emc-sol-detail-grid').getActivedRow().id
                            })
                        });
                    }
                }],
            onUpdated: function (data, fields, index) {
                if (fields.indexOf('samplingVolume') > -1 || fields.indexOf('blankStartValue') > -1 || fields.indexOf('blankFinalValue') > -1
                    || fields.indexOf('standardStartValue') > -1 || fields.indexOf('standardFinalValue') > -1) {
                    emcSol.detailPage.calcSolResult();
                }
            }
            // onBeforeLoad: function (rows) {
            //     const len = rows.length;
            //     var sum = 0;
            //     var solForm = Gikam.getComp('emc-sol-detail-form');
            //     if (Gikam.isEmpty(solForm) || Gikam.isEmpty(solForm.getData().id)) {
            //         return ;
            //     }
            //     const revisionRule = solForm.getData().revisionRule;
            //     let invalidDataNum = 0;
            //     let invalidDataIndex = [];
            //     for (let index = 0; index < len;) {
            //         let flag = emcSol.detailPage.checkRecordGirdRow(rows[index])
            //         // 依次计算每四行数据的总浓度数值
            //         let calibrationconcentration = rows[index].ext$.calibrationconcentration
            //         if (!flag) {
            //             calibrationconcentration = 0;
            //         }
            //         if ( emcSol.detailPage.checkInvalidRecordGirdRow(rows[index]) || calibrationconcentration == '/' ) {
            //             invalidDataNum = invalidDataNum + 1;
            //             invalidDataIndex.push(index);
            //             sum = sum + 0;
            //         } else if (Gikam.isNotEmpty(calibrationconcentration)) {
            //             sum = calibrationconcentration * 10e8 + sum;
            //         } else {
            //             // 如果浓度为空，则需要重新计算一下浓度, 导入数据时，浓度为空
            //             calibrationconcentration = _this.calculatedconcentration(rows[index])?.ext$?.calibrationconcentration;
            //             if (Gikam.isNotEmpty(calibrationconcentration) || calibrationconcentration != undefined) {
            //                 sum = calibrationconcentration * 10e8 + sum;
            //                 rows[index].ext$.calibrationconcentration = calibrationconcentration;
            //             } else {
            //                 sum = sum + 0;
            //             }
            //
            //         }
            //         index++;
            //         if (index > 0 && index % 4 === 0 && sum != 0 && sum != NaN && sum != undefined) {
            //             // 计算平均值，并依次赋值给前4行数据的平均浓度值
            //             let avg = (sum / (4 - invalidDataNum)) / 10e8;
            //             if (Gikam.isNotEmpty(revisionRule)) {
            //                 // 对标准液浓度 进行修约
            //                 Gikam.getJson(Gikam.printf(emcSol.baseUrl + '/cala/concentration/{calibrationconcentration}/{revisionRule}', {
            //                     calibrationconcentration: avg,
            //                     revisionRule: revisionRule
            //                 })).done(function (data) {
            //                     avg = data.concentration;
            //                     emcSol.detailPage.setGroupAverageConcentration(avg, rows, index, 4, invalidDataIndex);
            //                     /*rows[index - 1].ext$.averageconcentration = avg;
            //                     rows[index - 2].ext$.averageconcentration = avg;
            //                     rows[index - 3].ext$.averageconcentration = avg;
            //                     rows[index - 4].ext$.averageconcentration = avg;*/
            //                 })
            //             } else {
            //                 emcSol.detailPage.setGroupAverageConcentration(avg, rows, index, 4, invalidDataIndex);
            //                 /*rows[index - 4].ext$.averageconcentration = avg;
            //                 rows[index - 3].ext$.averageconcentration = avg;
            //                 rows[index - 2].ext$.averageconcentration = avg;
            //                 rows[index - 1].ext$.averageconcentration = avg;*/
            //             }
            //
            //             sum = 0;
            //         }
            //     }
            //     // 检查计算剩余不足4行的平均浓度值
            //     if (sum != 0 && sum != NaN && sum != undefined) {
            //         let num = len % 4;
            //         let avg = (sum / (num - invalidDataNum)) / 10e8;
            //         // 判断是否修约
            //         if (Gikam.isNotEmpty(revisionRule)) {
            //             // 对标准液浓度 进行修约
            //             Gikam.getJson(Gikam.printf(emcSol.baseUrl + '/cala/concentration/{calibrationconcentration}/{revisionRule}', {
            //                 calibrationconcentration: avg,
            //                 revisionRule: revisionRule
            //             })).done(function (data) {
            //                 avg = data.concentration+'';
            //                 for (let index = 1; index <= num; index++) {
            //                     if (invalidDataIndex.includes(len - index)){
            //                         continue;
            //                     }
            //                     rows[len - index].ext$.averageconcentration = avg
            //                 }
            //             })
            //         } else {
            //             for (let index = 1; index <= num; index++) {
            //                 if (invalidDataIndex.includes(len - index)) {
            //                     continue;
            //                 }
            //                 rows[len - index].ext$.averageconcentration = avg
            //             }
            //         }
            //
            //     }
            // },
            // onBeforeUpdate(row, keys) {
            //     const calibrationconcentration = row.ext$.calibrationconcentration;
            //     if (Gikam.isNotEmpty(row.samplingVolume)
            //         || Gikam.isNotEmpty(row.blankStartValue)
            //         || Gikam.isNotEmpty(row.blankFinalValue)
            //         || Gikam.isNotEmpty(row.standardStartValue)
            //         || Gikam.isNotEmpty(row.standardFinalValue)) {
            //         _this.calculatedconcentration(row.row)
            //     }
            //     // 判断修改的字段是否是标准浓度字段 标准浓度修改后 自动计算平均浓度 （平均浓度曲4行浓度的平均）
            //     if (Gikam.isEmpty(calibrationconcentration)) {
            //         return;
            //     }
            //     const gridIndex = row.row.gridIndex - 1;
            //     const group = Math.floor(gridIndex / 4);
            //     let sum = 0;
            //     const data = Gikam.getComp('emc-sol-detail-record-grid').getData();
            //     const form = Gikam.getComp('emc-sol-detail-form').getData();
            //     const revisionRule = form.revisionRule;
            //     var invalidDataNum = 0;
            //     var invalidDataIndex = [];
            //     for (let i = 0; i < 4; i++) {
            //         const index = group * 4 + i
            //         // 不足4行按照剩余数量求平均
            //         if (index >= data.length) {
            //             let avg = sum / (i - invalidDataNum) / 10e8
            //             emcSol.detailPage.setAverageConcentrationRevisionRule(group, i, avg, revisionRule, invalidDataIndex);
            //             return;
            //         }
            //         const cali = data[index].ext$.calibrationconcentration;
            //         if (Gikam.isEmpty(cali)) {
            //             sum = sum + 0;
            //         } else if (cali == '/'){
            //             sum = sum + 0;
            //             invalidDataNum = invalidDataNum + 1;
            //             invalidDataIndex.push(index);
            //         } else {
            //             sum = cali * 10e8 + sum;
            //         }
            //     }
            //     let avg = sum / (4 - invalidDataNum ) / 10e8
            //     emcSol.detailPage.setAverageConcentrationRevisionRule(group, 4, avg, revisionRule, invalidDataIndex);
            // }
        }
    },
    calcSolResult: function () {
        var _this = this, grid = Gikam.getComp('emc-sol-detail-record-grid');
        var solUrl = IFM_CONTEXT + '/secure/emc/module/bp/sols';
        Gikam.put(solUrl + '/action/calc-result', Gikam.getJsonWrapper({
            f: {
                solId: _this.param.id
            }
        }, ['', []])).done(function () {
            Gikam.post(Gikam.printf(emcSol.baseUrl + '/{id}/records/queries', {
                id: _this.param.id
            }), Gikam.getJsonWrapper({
                n: -1,
                o: [{
                    'titrationNum': 'asc'
                }],
            })).done(function (data) {
                Gikam.each(data.rows, function (index, row) {
                    grid.setData(row, false, true);
                });
            });
        })
    },
    setGroupAverageConcentration(avg, rows, index, length, invalidDataIndex) {
        for (let i = 1; i <= length; i++) {
            if (invalidDataIndex.includes[index - i]) {
                continue;
            }
            rows[index - i].ext$.averageconcentration = avg;
        }
    },

    // 设置平均浓度修约
    setAverageConcentrationRevisionRule(group, len, avg, revisionRule, invalidDataIndex) {
        if (Gikam.isNotEmpty(revisionRule)) {
            // 对标准液浓度 进行修约
            Gikam.getJson(Gikam.printf(emcSol.baseUrl + '/cala/concentration/{calibrationconcentration}/{revisionRule}', {
                calibrationconcentration: avg,
                revisionRule: revisionRule
            })).done(function (data) {
                avg = data.concentration;
                emcSol.detailPage.setAverageConcentration(group, len, avg, invalidDataIndex)
            })
        } else {
            emcSol.detailPage.setAverageConcentration(group, len, avg, invalidDataIndex)
        }
    },
    /** 批录入 */
    batchInput: function () {
        var _this = this;
        let flag1 = emcSol.detailPage.checkDetailFormCondition();
        if (!flag1) {
            return false;
        }
        let flag2 = emcSol.detailPage.checkDetailGridCondition();
        if (!flag2) {
            return false;
        }
        var selections = Gikam.getComp('emc-sol-detail-record-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            width: 600,
            height: 313,
            title: 'EMC.MODULE.BP.SOL.RECORD.MODAL.BATCH_INPUT',
            url: emcSol.baseUrl + '/page/sol-record-batch',
            onAfterClose: function (data) {
                if (!Gikam.isEmpty(data)) {
                    workspace.window.showMask(true);
                    selections.map(function (item) {
                        item.samplingVolume = data.samplingVolume;
                        item.blankStartValue = data.blankStartValue;
                        item.blankFinalValue = data.blankFinalValue;
                        item.standardStartValue = data.standardStartValue;
                        item.standardFinalValue = data.standardFinalValue;
                        Gikam.getComp('emc-sol-detail-record-grid').setData({
                            id: item.id,
                            samplingVolume: data.samplingVolume,
                            blankStartValue: data.blankStartValue,
                            blankFinalValue: data.blankFinalValue,
                            standardStartValue: data.standardStartValue,
                            standardFinalValue: data.standardFinalValue,
                            row: item
                        })
                    });
                    workspace.window.closeMask();
                }
            }
        });
    },

    // 导出目标目标
    quickExport: function () {

        var rightData = Gikam.getComp('emc-sol-detail-record-grid').getData();
        if (Gikam.isEmpty(rightData)) {
            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CAN_NOT_EXPORT');
            return;
        }

        Gikam.getLastModal().window.showMask(true);
        Gikam.getText(Gikam.printf('/core/module/item/files/{templateCode}/action/download-template/{service}', {
            templateCode: 'emcSolRecordExportTemplate',
            service: 'emcSolRecordExportTemplate'
        })).done(function (fileUri) {
            var fileUrl = IFM_CONTEXT + fileUri;
            Gikam.emcDownloadFile(fileUrl);
        }).always(function () {
            Gikam.getLastModal().window.closeMask();
        });

    },

    quickImport: function () {
        var id = this.param.id;
        Gikam.create('simpleUploader', {
            dbTable: 'T_EMC_SOL_RECORD',
            id: id,
            multiple: false,
            bizCategory: 'solRecord',
            bizId: id,
            accept: ['xls'],
            onAfterClose: function (files) {
                if (Gikam.isEmpty(files)) {
                    return;
                }
                let flag1 = emcSol.detailPage.checkDetailFormCondition();
                if (!flag1) {
                    return;
                }
                let flag2 = emcSol.detailPage.checkDetailGridCondition();
                if (!flag2) {
                    return;
                }
                Gikam.getLastModal().window.showMask(true);
                Gikam.post(
                    Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}/action/import/' + 'emcSolRecordImportFileFunction', {
                        id: files[0].id
                    })
                ).done(function (result) {
                    if (result.url) {
                        Gikam.emcDownloadFile(result.url);
                    } else if (result.message) {
                        Gikam.alert(result.message);
                    }
                    Gikam.getComp('emc-sol-detail-record-grid').refresh();
                }).always(function () {
                    Gikam.getLastModal().window.closeMask();
                });
            }
        });
    },

    // 修改平均浓度数据
    setAverageConcentration(group, len, avg, invalidDataIndex) {
        for (let index = 0; index < len; index++) {
            //无效数据，不用设置平均浓度
            if (invalidDataIndex.includes(group * 4 + index)) {
                continue;
            }
            Gikam.getComp('emc-sol-detail-record-grid').setData({
                index: group * 4 + index,
                ext$: {
                    averageconcentration: avg
                }
            })
        }
    },

    refreshRecordGridData() {

        // let recordData = Gikam.getComp('emc-sol-detail-record-grid').getData();
        // for (let index = 0; index < recordData.length; index++) {
        //     this.calculatedconcentration(recordData[index]);
        // }
    },

    // 计算标准溶液浓度
    calculatedconcentration(row) {
        // 检查 输入条件
        if (!emcSol.detailPage.checkCondition(row)) {
            return false;
        }
        // 摩尔质量
        let molarMass;
        const form = Gikam.getComp('emc-sol-detail-form').getData();
        const detailGird = Gikam.getComp('emc-sol-detail-grid').getData();

        // V1 标准溶液消耗体积 标准溶液终读 - 标准溶液初读
        let V1 = row.standardFinalValue - row.standardStartValue;
        // V2 空白中消耗的标准溶液消耗体积 空白溶液终读 - 空白溶液初读
        let V2 = row.blankFinalValue - row.blankStartValue;
        // m 取样重量或质量
        let m = form.takeWeightOrVolume;
        // CV Constant volume 定容体积
        let CV = form.calibSolConstantVolume;
        // SV samplingvolume 取样体积
        let SV = row.samplingVolume;
        // C2 标准液浓度
        let C2 = detailGird[0].prepareConcent;
        //标准溶液终读
        let FV = row.standardFinalValue;
        //系数
        let CF = form.ext$.coefficient;
        //标准溶液终读 - 空白溶液终读
        let V3 = row.standardFinalValue - row.blankFinalValue;
        // c 计算的标准液浓度
        let c = 0;

        let script = form.script
        // 根据不同的公式，设置不同的计算规则
        if (script === 'C1') {
            c = (6 * SV * 1.66) / (V1 - V2)
        } else if (script === 'C2') {
            c = (C2 * SV) / (V1 - V2)
        } else if (script === 'C3') {
            c = SV / ((V1 - V2) * 0.053)
        } else if (script === 'C4') {
            c = (C2 * SV) / (V1 - V2)
        } else if (script === 'C5') {

        } else if (script === 'C6') {

        } else if (script === 'C7') {
            molarMass = 0.08138
            c = (SV * m) / (CV * (V1 - V2) * molarMass);
        } else if (script === 'C8') {
            molarMass = 204.22
            c = (m * 1000) / ((V1 - V2) * molarMass)
        } else if (script === 'C9') {
            c = (SV * C2) / (V1 - V2)
        } else if (script === 'C10') {
            c = (SV * C2) / (V1 - V2)
        } else if (script === 'C11') {
            c = (SV * C2) / (V1 - V2)
        } else if (script === 'C12') {
            c = (SV * C2) / (V1 - V2)
        } else if (script === 'C13') {
            c = (SV * C2) / (V1 - V2)
        } else if (script === 'C14') {
            c = (C2 * FV) / (SV)
        } else if (script === 'C15') {
            //系数/(标液用量终读-空白用量终读)
            c = CF / V3
        } else {

        }
        // 标准溶液修约规则
        let revisionRule = form.revisionRule
        if (Gikam.isNotEmpty(revisionRule)) {
            // 对标准液浓度 进行修约
            Gikam.getJsonSync(Gikam.printf(emcSol.baseUrl + '/cala/concentration/{calibrationconcentration}/{revisionRule}', {
                calibrationconcentration: c,
                revisionRule: revisionRule
            })).done(function (data) {
                c = data.concentration;
                row.ext$.calibrationconcentration = c
                Gikam.getComp('emc-sol-detail-record-grid').setData({
                    id: row.id,
                    ext$: {
                        calibrationconcentration: c
                    },
                    row: row
                })
                return row;
            })
        } else {
            row.ext$.calibrationconcentration = c
            Gikam.getComp('emc-sol-detail-record-grid').setData({
                id: row.id,
                ext$: {
                    calibrationconcentration: c
                },
                row: row
            })
            return row;
        }
        return row;
    },

    checkRecordGirdRow(row) {
        if (Gikam.isEmpty(row.samplingVolume)) {
            return false;
        }
        if (Gikam.isEmpty(row.blankStartValue)) {
            return false;
        }
        if (Gikam.isEmpty(row.blankFinalValue)) {
            return false;
        }
        if (Gikam.isEmpty(row.standardStartValue)) {
            return false;
        }
        if (Gikam.isEmpty(row.standardFinalValue)) {
            return false;
        }
        return true;
    },
    checkInvalidRecordGirdRow(row) {
        if (row.samplingVolume == '/') {
            return false;
        }
        if (row.blankStartValue == '/') {
            return false;
        }
        if (row.blankFinalValue == '/') {
            return false;
        }
        if (row.standardStartValue == '/') {
            return false;
        }
        if (row.standardFinalValue == '/') {
            return false;
        }
        return true;
    },

    checkDetailFormCondition() {
        const form = Gikam.getComp('emc-sol-detail-form').getData();
        if (Gikam.isEmpty(form.script)) {
            Gikam.alert('请输入' + Gikam.propI18N('T_EMC_SOL_LINE.CALIBRATESOLSCRIPT'));
            return false;
        }
        if (Gikam.isEmpty(form.takeWeightOrVolume)) {
            Gikam.alert('请输入' + Gikam.propI18N('T_EMC_SOL.TAKEWEIGHTORVOLUME'));
            return false;
        }
        if (Gikam.isEmpty(form.ext$.calibsolweightunit)) {
            Gikam.alert('请输入' + Gikam.propI18N('T_EMC_SOL.CALIBSOLWEIGHTUNIT'));
            return false;
        }
        if (Gikam.isEmpty(form.calibSolConstantVolume)) {
            Gikam.alert('请输入' + Gikam.propI18N('T_EMC_SOL.CALIBSOLCONSTANTVOLUME'));
            return false;
        }
        return true;
    },
    checkDetailGridCondition() {
        const detailGird = Gikam.getComp('emc-sol-detail-grid').getData();
        if (Gikam.isEmpty(detailGird[0].prepareConcent)) {
            Gikam.alert('请输入' + Gikam.propI18N('T_EMC_SOL_LINE.PREPARECONCENT'));
            return false;
        }
        return true;

    },
    checkCondition(row) {
        let flag = emcSol.detailPage.checkRecordGirdRow(row);
        if (!flag) {
            return false;
        }
        let flag0 = emcSol.detailPage.checkInvalidRecordGirdRow(row);
        if (!flag0) {
            return false;
        }
        let flag1 = emcSol.detailPage.checkDetailFormCondition();
        if (!flag1) {
            return false;
        }
        let flag2 = emcSol.detailPage.checkDetailGridCondition();
        if (!flag2) {
            return false;
        }
        return true;
    },

    getDownLayout: function () {
        var _this = this;
        return {
            type: 'layout',
            id: 'emc-sol-detail-center-layout',
            north: {
                height: 150,
                items: [_this.getSolFormGridParam()]
            },
            center: {
                items: [_this.getSolRecordLayout()]
            }
        }
    },

    getSolRecordLayout: function () {
        var _this = this;
        return {
            type: 'layout',
            id: 'emc-sol-detail-record-layout',
            east: {
                items: [_this.getCalcResultTab()]
            },
            center: {
                width: '100%',
                items: [_this.getSolRecordTab()]
            }
        }
    },

    getSolRecordTab: function () {
        var _this = this;
        return {
            type: 'tab',
            fill: true,
            panels: [{
                title: 'EMC.MODULE.BP.SOLS.TAB.RECORD',
                items: [_this.getSolRecordGridParam()]
            }]
        }
    },

    invalidData: function () {
        Gikam.create('modal', {
            width: '20%',
            height: '30%',
            title: 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.INVALID_DATA',
            url: '/secure/emc/module/bp/sols/page/invalid-results',
            onAfterClose: function (data) {
                if (!Gikam.isEmpty(data)) {
                    var resultSelections = Gikam.getComp('emc-sol-detail-record-grid').getSelections();
                    if (Gikam.isEmpty(resultSelections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }

                    resultSelections.forEach(function (item) {
                        item.remark = data.reason;
                    });

                    // 在代码请求前打开遮罩，避免遮罩没有关闭
                    Gikam.getLastModal().window.showMask(true);
                    Gikam.put('/secure/emc/module/bp/sols/action/set-invalid', Gikam.getJsonWrapper({}, [null, resultSelections])).done(function () {
                        Gikam.getComp('emc-sol-detail-record-grid').refresh();
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.UPDATE_SUCCESS');
                    }).always(function () {
                        Gikam.getLastModal().window.closeMask();
                    });
                }

            }
        });
    },

    getCalcResultTab: function () {
        return {
            type: 'tab',
            id: 'emc-sol-detail-result-tab',
            fill: true,
            show: false,
            panels: [{
                title: 'EMC.MODULE.BP.SOLS.TAB.INPUT_RESULT',
                items: [{
                    id: 'emc-sol-detail-result-form',
                    type: 'form',
                    service: 'emcSolServiceImpl',
                    columns: 1,
                    fields: emcSol.getResultFormFields()
                }]
            }]
        }
    },

    createPage: function () {
        Gikam.create('layout', {
            north: {
                height: '30%',
                items: [this.getGridParam()]
            },
            center: {
                items: [this.getDownLayout()]
            },
            renderTo: Gikam.getLastModal().window.$dom,
        });
        Gikam.getAsyncComp('emc-sol-detail-grid').done(function (grid) {
            grid.options.checkOneOnActive = true;
        });
        return this;
    },

    init: function (param) {
        this.param = param;
        this.createPage();
    }
}
