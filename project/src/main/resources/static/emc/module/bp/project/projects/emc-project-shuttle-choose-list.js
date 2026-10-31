emcProject.shuttleChoosePage = {

    getShuttleFrame : function() {
        let _this = this, columns = emcProject.getChooseListColumns();
        // 如果设置了rememberChoose，并且传递了id，打开页面后自动加载之前选择的数据
        let rightGridUrl = _this.param.id && _this.param.rememberChoose ? emcProject.baseUrl + '/queries/choosable' : '';
        columns.unshift({
            checkbox : true
        });
        return {
            type : 'shuttleFrame',
            id : 'emc-project-choose-shuttle-frame',
            columns : columns,
            leftRequestData : _this.param,
            leftGridUrl : emcProject.baseUrl + '/queries/choosable',
            rightGridUrl : rightGridUrl,
            rightRequestData : {
                id_IN : _this.param.id
            },
            rightPage : false,
            leftPage : false,
        }
    },

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    let selections = Gikam.getComp('emc-project-choose-shuttle-frame').getSelectList();
                    Gikam.getLastModal().close(selections);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ _this.getBtnToolbar(), this.getShuttleFrame() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
}
