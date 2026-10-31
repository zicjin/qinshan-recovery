var emcStaffAbalityCheck = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, /*{
            field : 'name',
            title : 'T_EMC_STAFF_ABALITY_CHECK.NAME'
        }, {
            field : 'dept',
            title : 'T_EMC_STAFF_ABALITY_CHECK.DEPT'
        }, {
            field : 'job',
            title : 'T_EMC_STAFF_ABALITY_CHECK.JOB'
        } */{
            field : 'ext$.bigcategory',
            title : '大类',
            editor: true
        },{
            field : 'ext$.samllcategory',
            title : '小类',
            editor: true
        },{
            field : 'ext$.projname',
            title : '项目名称',
            editor: true
        },{
            field : 'ext$.testmethod',
            title : '检测方法',
            editor: true
        },{
            field : 'ext$.plandate',
            title : '计划实施时间',
            editor: true
        },{
            field : 'ext$.examuser',
            title : '考核人员',
            editor: true,
            type : 'choose',
            category : 'user-simple',
            editable : true,
            single : false,
            onAfterChoose : function(rows,index) {
                if (!Gikam.isEmpty(rows)) {
                    var ids = "";
                    var names = "";
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            ids += rows[index].id;
                            names += rows[index].userName;
                            break;
                        }
                        ids += rows[index].id + ",";
                        names += rows[index].userName + ",";
                    }
                    this.setData({
                        index : index,
                        ext$:{
                            examuserid: ids,
                            examuser: names
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    ext$:{
                        examuserid: '',
                        examuser: ''
                    }
                });
            }
        },{
            field : 'ext$.examres',
            title : '考核结果',
            editor: true,
        }];
    },

    getBaseInfoFormFields : function() {
        return [ /*{
            field : 'name',
            title : 'T_EMC_STAFF_ABALITY_CHECK.NAME',
            validators : [ 'strLength[0,6]' ]
        }, {
            field : 'dept',
            title : 'T_EMC_STAFF_ABALITY_CHECK.DEPT',
            validators : [ 'strLength[0,33]' ]
        }, {
            field : 'job',
            title : 'T_EMC_STAFF_ABALITY_CHECK.JOB',
            validators : [ 'strLength[0,33]' ]
        },*/ {
            field : 'ext$.bigcategory',
            title : '大类',
        },{
            field : 'ext$.samllcategory',
            title : '小类',
        },{
            field : 'ext$.projname',
            title : '项目名称',
        },{
            field : 'ext$.testmethod',
            title : '检测方法',
        },{
            field : 'ext$.plandate',
            title : '计划实施时间',
        },{
            field : 'ext$.examuser',
            title : '考核人员',
            type : 'choose',
            category : 'user-simple',
            editable : true,
            single : false,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var ids = "";
                    var names = "";
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            ids += rows[index].id;
                            names += rows[index].userName;
                            break;
                        }
                        ids += rows[index].id + ",";
                        names += rows[index].userName + ",";
                    }
                    this.setData({
                        ext$:{
                            examuserid: ids,
                            examuser: names
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    ext$:{
                        examuserid: '',
                        examuser: ''
                    }
                });
            }
        },{
            field : 'ext$.examres',
            title : '考核结果',
        }];
    },



    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/staff-abality-checks'
});
