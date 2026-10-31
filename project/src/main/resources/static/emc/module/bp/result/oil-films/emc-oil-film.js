var emcOilFilm = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'oilFilmNo',
            title : 'T_EMC_OIL_FILM.OILFILMNO',
            editor :true,
        },{
            field : 'weighingUserName',
            title : 'T_EMC_OIL_FILM.WEIGHINGUSERNAME',
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onBeforeChoose : function() {
                var selections = Gikam.getComp('emc-oil-film-edit-list-grid').getSelections();
                return {
                    departmentid_SEQ : selections[0].createdByOrgId
                }
            },
            onAfterChoose : function(rows) {
                var id = Gikam.getComp('emc-oil-film-edit-list-grid').getActivedRow().id;

                if (!Gikam.isEmpty(rows)) {
                    id : Gikam.getComp('emc-oil-film-edit-list-grid').getActivedRow().id,
                    this.setData({
                        id : Gikam.getComp('emc-oil-film-edit-list-grid').getActivedRow().id,	
                    	weighingUserName : rows[0].userName,
                        	weighingUserId : rows[0].id

                    });
                }
            },
            onClean : function() {
                var id = Gikam.getComp('emc-oil-film-edit-list-grid').getActivedRow().id;
                this.setData({
                        id : Gikam.getComp('emc-oil-film-edit-list-grid').getActivedRow().id,
                		weighingUserId : '',
                		weighingUserName : ''
                });
        	}
        }, {
            field : 'projId',
            title : 'T_EMC_OIL_FILM.PROJID',
        }, {
            field : 'projNo',
            title : 'T_EMC_OIL_FILM.PROJNO',
            type : 'choose',
            category : 'project',
            single : true,
            editor :true,
            onBeforeChoose : function() {
                return {
        	            org_filter : '1',
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        projId :  rows[0].id,
                        projNo : rows[0].projNo,
                        projName : rows[0].projName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    projNo : '',
                    projName : '',
                });
            },
        }, {
            field : 'weighingDate',
            title : 'T_EMC_OIL_FILM.WEIGHINGDATE',
            type : 'date',
            editor :true,
        }, {
            field : 'emptyFilterBarrelMOneOne',
            title : 'T_EMC_OIL_FILM.EMPTYFILTERBARRELMONEONE',
            editor :true,
        }, {
            field : 'emptyFilterBarrelMOneTwo',
            title : 'T_EMC_OIL_FILM.EMPTYFILTERBARRELMONETWO',
            editor :true,
        }, {
            field : 'emptyFilterBarrelMOneThree',
            title : 'T_EMC_OIL_FILM.EMPTYFILTERBARRELMONETHREE',
            editor :true,
        }, {
            field : 'emptyFilterBarrelMOneFour',
            title : 'T_EMC_OIL_FILM.EMPTYFILTERBARRELMONEFOUR',
            editor :true,
        }, {
            field : 'emptyFilterBarrelMOneFive',
            title : 'T_EMC_OIL_FILM.EMPTYFILTERBARRELMONEFIVE',
            editor :true,
        }, {
            field : 'emptyFilterBarrelMOneSix',
            title : 'T_EMC_OIL_FILM.EMPTYFILTERBARRELMONESIX',
            editor :true,
        } ];
    },

    getBatchInputColumns : function() {
        return [ {
            field : 'weighingUserName',
            title : 'T_EMC_OIL_FILM.WEIGHINGUSERNAME',
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onBeforeChoose : function() {
                var selections = Gikam.getComp('emc-oil-film-edit-list-grid').getSelections();
                return {
                    departmentid_SEQ : selections[0].createdByOrgId
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        	weighingUserName : rows[0].userName,
                        	weighingUserId : rows[0].id

                    });
                }
            },
            onClean : function() {
                this.setData({
                		weighingUserId : '',
                		weighingUserName : '',
                });
        	}
        }, {
            field : 'projNo',
            title : 'T_EMC_OIL_FILM.PROJNO',
            type : 'choose',
            category : 'project',
            single : true,
            editor :true,
            onBeforeChoose : function() {
                return {
        	            orgId_filter : '1',
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        projId :  rows[0].id,
                        projNo : rows[0].projNo,
                        projName : rows[0].projName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    projId :  '',
                    projNo : '',
                    projName : '',
                });
            },
        }, {
            field : 'weighingDate',
            title : 'T_EMC_OIL_FILM.WEIGHINGDATE',
            type : 'date',
            editor :true,
        } ];
    },
    
    
    getBatchAddColumns : function() {
        return [ {
            field : 'addnum',
            title : '数量',
            validators : [ 'integer' ]
        } ];
    },
    
    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/result/oil-films'
});
