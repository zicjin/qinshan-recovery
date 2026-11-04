limsReportTemplate.choosePage = {

    getGridParam : function() {
        var _this = this, columns = limsReportTemplate.getChooseListColumns();
        if( _this.param.showLink == "0"){
            if (columns.length >= 4) {
                delete columns[3].type;
            }
        }

        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        // NOTE 标准报告模板那里 要过滤掉已经被选择的数据
        if (_this.param.specId) {
            _this.param.targetFilter= {
                targetTable: 'T_LIMS_SPEC_REPORT_TMPL',
                targetMatchColumn: 'TMPLID',
                thisMatchColumn: 'ID',
                targetFilter: {
                    SPECID: _this.param.specId,
                }
            }
        }
        var url = limsReportTemplate.baseUrl + '/queries/choosable';
        return {
            type : 'grid',
            id : _this.idManager.limsReportTemplateChooseGrid,
            url : url,
            columns : columns,
            requestData : _this.param,
            service : 'limsReportTemplateService',
            onCellClick : function(field, row) {
                if( _this.param.showLink != "0"){
                    field === 'templateFileName' && Gikam.openFile(row.templateFileId, false);
                }
            },
            onLoadSuccess : function(data) {
                if (data.length == 0 && _this.fristSearch && _this.param.cailiaoflag) {
                    _this.fristSearch = false;
                    _this.param.cailiaoflag = null;
                    this.refresh({
                        requestData : _this.param
                    })
                }
                _this.fristSearch = false;
            },
            toolbar : [{
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var data = Gikam.getComp(_this.idManager.limsReportTemplateChooseGrid).getSelections();
                    if (Gikam.isEmpty(data)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.getLastModal().close(data);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            }, {
                type : 'form',
                fields: [
                    {
                        title: '关联项目代码',
                        field: 'testCodeIdFlag',
                        type: 'simpleCheckbox'
                    }
                ],
                onRendered: function (){
                    if (!_this.param.testCodeId) {
                        this.setHidden(true)
                    }
                },
            }]
        }
    },

    idManager : {
        limsReportTemplateChooseLayout : 'lims-report-template-choose-list-layout',
        limsReportTemplateChooseGrid : 'lims-report-template-choose-list-grid',
    },
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : _this.idManager.limsReportTemplateChooseLayout,
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [this.getGridParam()]
            }
        });
    },

    init : function(param) {
        var _this = this;
        this.fristSearch = true;
        _this.param = param;
        this.create();
    }
};
