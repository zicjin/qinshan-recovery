var coreEquipt = Gikam.getPageObject({
    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'equiptNo',
            title : 'T_CORE_EQUIPT.EQUIPTNO',
            type : 'link',
            width : 100
        }, {
            field : 'equiptName',
            title : 'T_CORE_EQUIPT.EQUIPTNAME',
            width : 200
        }, {
            field : 'equiptCategory',
            title : 'T_CORE_EQUIPT.EQUIPTCATEGORY',
            width : 200
        }, {
            field : 'keyEquipt',
            title : 'T_CORE_EQUIPT.KEYEQUIPT',
            type : 'select',
            category : 'yesorno',
            width : 150
        }, {
            field : 'location',
            title : 'T_CORE_EQUIPT.LOCATION',
            width : 300
        } ];
    },

    getBaseInfoFormFields : function() {
        var _this = this;
        return [ {
            field : 'equiptNo',
            title : 'T_CORE_EQUIPT.EQUIPTNO',
            validators : [ 'notEmpty'],
            preInsert : true
        }, {
            field : 'equiptName',
            title : 'T_CORE_EQUIPT.EQUIPTNAME',
            validators : [ 'notEmpty'],
            preInsert : true,
        }, {
            field : 'equiptCategory',
            title : 'T_CORE_EQUIPT.EQUIPTCATEGORY',
            validators : [ 'notEmpty'],
            preInsert : true,
        }, {
            field : 'keyEquipt',
            title : 'T_CORE_EQUIPT.KEYEQUIPT',
            type : 'select',
            category : 'yesorno'
        }, {
            field : 'location',
            validators : [ 'strLength[0, 90]'],
            title : 'T_CORE_EQUIPT.LOCATION',
        }, {
            field : 'equiptManagerName',
            title : 'T_CORE_EQUIPT.EQUIPTMANAGERNAME',
            type : 'choose',
            category : 'user',
            single : true,
            targetFields : [ {
                equiptManagerId : 'id'
            }, {
                equiptManagerName : 'userName'
            } ]
        }, {
            field : 'monitorCode',
            validators : [ 'strLength[0, 12]'],
            title : 'T_CORE_EQUIPT.MONITORCODE',
        }, {
            field : 'ip',
            validators : [ 'strLength[0, 12]'],
            title : 'T_CORE_EQUIPT.IP',
        }, {
            field : 'port',
            validators : ['integer' ],
            title : 'T_CORE_EQUIPT.PORT',
        }, {
            field : 'threshold',
            validators : ['number' ],
            title : 'T_CORE_EQUIPT.THRESHOLD',
        }, {
            field : 'planUseTime',
            validators : ['number' ],
            title : 'T_CORE_EQUIPT.PLANUSETIME',
        }, {
            field : 'cost',
            validators : ['number' ],
            title : 'T_CORE_EQUIPT.COST',
        }, {
            field : 'equiptIcon',
            title : 'T_CORE_EQUIPT.EQUIPTICON',
            type : 'image',
            height : 90,
            align : 'center',
            colspan : 2,
            enlarged : false,
            onClick : function() {
                _this.getShowImgPanel()
            }
        } ];
    },

    equiptIconsList : [],

    getShowImgPanel : function() {
        var _this = this;
        if (this.equiptIconsList.length == 0) {
            Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
                f : {
                    targetId_SB : 'T_CORE_EQUIPT$equiptIcon'
                },
                n : -1,
                s : 50
            })).done(function(r) {
                var data = r.rows;
                if (data.length == 0) {
                    Gikam.alert('CORE.MODULE.EQUIPT.EQUIPTS.TIP.ADD_EQUIPT_ICON');
                    return;
                } else {
                    data.forEach(function(item) {
                        item.id = item.name;
                        item.src = IFM_CONTEXT + item.downloadUrl;
                    });
                    _this.equiptIconsList = data;
                    _this.imgPanelHandle();
                }
            })
        } else {
            _this.imgPanelHandle();
        }
    },

    imgPanelHandle : function() {
        var iconPanel = Gikam.create('iconPanel', {
            renderTo : workspace.window.$dom,
            id : 'core-equipt-edit-list-icon-panel',
            title : 'CORE.MODULE.EQUIPT.EQUIPTS.TITLE.ICON',
            data : this.equiptIconsList,
            noRequest : false,
            onConfirm : function(data) {
                var form = Gikam.getComp('core-equipt-detail-base-info-form');
                form.setData({
                    icon : data.name,
                    equiptIcon : data.downloadUrl
                });
                iconPanel.cancel();
            }
        });
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/equipt/equipts'
});
