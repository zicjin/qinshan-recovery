var emcArea = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/areas',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getActiveColumn(), {
            field : 'areaId',
            title : 'T_EMC_AREA.AREAID',
            editor : true,
            validators : [ 'strLength[0,10]' ]
        }, {
            field : 'areaName',
            title : 'T_EMC_AREA.AREANAME',
            validators : [ 'strLength[0,21]' ],
            editor : true,
            validators : [ 'strLength[0,21]' ],
            onChange : function(field, value, index) {
                var nodeId = Gikam.getComp('emc-area-edit-list-grid').getData()[index].id;
                var tree = Gikam.getComp('emc-area-edit-list-area-tree');
                var treeNode = tree.getNodeById(tree.options.data, nodeId);
                if(!Gikam.isEmpty(treeNode)){
                    tree.setNodeText(treeNode.text, value);
                }
            }
        }, {
            field : 'areaLevel',
            title : 'T_EMC_AREA.AREALEVEL',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'areaLevel',
            },
            editor : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_AREA.ORDERNO',
            validators : [ 'integer' ],
            type : 'number',
            hasArrow : true,
            editor : true
        }, {
            field : 'remark',
            title : 'T_EMC_AREA.REMARK',
            validators : [ 'strLength[0,170]' ],
            editor : true
        } ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'areaId',
            title : 'T_EMC_AREA.AREAID'
        }, {
            field : 'areaName',
            title : 'T_EMC_AREA.AREANAME'
        }, {
            field : 'areaLevel',
            title : 'T_EMC_AREA.AREALEVEL',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'areaLevel',
            }
        }, {
            field : 'orderNo',
            title : 'T_EMC_AREA.ORDERNO'
        }, {
            field : 'remark',
            title : 'T_EMC_AREA.REMARK'
        }, ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'areaId',
            title : 'T_EMC_AREA.AREAID',
            validators : [ 'strLength[0,10]' ]
        }, {
            field : 'areaName',
            title : 'T_EMC_AREA.AREANAME',
            validators : [ 'strLength[0,21]' ],
        }, {
            field : 'areaLevel',
            title : 'T_EMC_AREA.AREALEVEL',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'areaLevel',
            },
        }, {
            field : 'orderNo',
            title : 'T_EMC_AREA.ORDERNO',
            validators : [ 'integer' ]
        }, {
            field : 'remark',
            title : 'T_EMC_AREA.REMARK',
            validators : [ 'strLength[0,170]' ],
            colspan : 2,
            type : 'textarea',
        } ];
    },

    getPreInsertFormFields : function() {
        return [ {
            field : 'areaId',
            title : 'T_EMC_AREA.AREAID',
            preInsert : true,
            validators : [ 'remote[' + emcArea.baseUrl + '/action/validate-unique]', 'strLength[0,10]' ]
        }, {
            field : 'areaName',
            title : 'T_EMC_AREA.AREANAME',
            validators : [ 'strLength[0,21]' ],
            preInsert : true,
        }, {
            field : 'areaLevel',
            title : 'T_EMC_AREA.AREALEVEL',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'areaLevel',
            },
            preInsert : true
        } ];
    }
});
