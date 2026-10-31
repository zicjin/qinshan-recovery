emcProjScheme.selectPointPage = {

    getBaseInfoForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-select-monitoring-point-form',
            fields : emcProjScheme.getMonitoringPointFormFields(),
            titleWidth : 77,
            onRendered : function() {
                var form = Gikam.getComp('emc-select-monitoring-point-form');
                if (Gikam.isNotEmpty(_this.param.monitorPointType)) {
                    form.setData({
                        monitorPointType : _this.param.monitorPointType
                    });
                }

                if (Gikam.isNotEmpty(_this.param.sampleOrgId)) {
                    form.setData({
                        sampleDepartmentId : _this.param.sampleOrgId,
                    });

                } else if (Gikam.isNotEmpty(_this.param.defaultOrgId)) {
                    form.setData({
                        sampleDepartmentId : _this.param.defaultOrgId
                    });
                } else {
                    form.setData({
                        sampleDepartmentId : form.getField('sampleDepartmentId').items[1].value
                    });
                }

            }
        };
    },

    getConfirmBtnItem : function() {
        var _this = this;
        return {
            type: 'button',
            text: 'GIKAM.BUTTON.CONFIRM',
            icon : 'select',
            margin : '13px 0 0 15px',
            onClick : function () {
                _this.confirm();
            }
        }
    },

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-select-monitoring-point-list-grid',
            columns : emcProjScheme.getMonitoringPointListColumns(),
            filter : false,
            onLoadSuccess : function(rows) {
                if (Gikam.isNotEmpty(_this.param.selectIds)) {
                    var _selectIds = _this.param.selectIds.split(',');
                    Gikam.each(_selectIds, function(index, id) {
                        Gikam.getComp('emc-select-monitoring-point-list-grid').selectRowById(Number(id));
                    })
                }

            }
        };
    },

    confirm : function() {
        var rows = Gikam.getComp('emc-select-monitoring-point-list-grid').getSelections();
        if (rows.length > 0) {
            var _formData = Gikam.getComp('emc-select-monitoring-point-form').getData();
            var sampleDepartmentId = _formData.sampleDepartmentId, monitorPointType = _formData.monitorPointType, sampleDepartment = _formData.sampleDepartment;
            var chooseData = rows.map(function(item) {
                var ext$ = {};
                ext$.templatename = item.ext$.templatename;
                return {
                    monitorPointCategoryId : item.monitorPointCategoryId,
                    ext$ : ext$,
                    id : item.id,
                    monitorPointNo : item.monitorPointNo,
                    monitorPointName : item.monitorPointName,
                    sampleDepartmentId : sampleDepartmentId,
                    monitorPointType : monitorPointType,
                    sampleDepartment : sampleDepartment
                }
            });
            Gikam.getLastModal().close(chooseData);
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            north : {
                height : "52px",
                items : [ {
                    type : 'layout',
                    east : {
                        width : 150,
                        items : [ this.getConfirmBtnItem() ]
                    },
                    center : {
                        items : [ this.getBaseInfoForm() ]
                    }
                } ]
            },
            center : {
                items : [ this.getGridParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
