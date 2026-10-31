var emcAdvisoryReport = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + "/secure/emc/module/bp/report/advisory-reports",

    getPreInsertFields : function() {
            return [
                {
                  field : 'bizClassId',
                  title : '业务类别',
                  type : 'select',
                  search : true,
                  firstBlank : false,
                  category : 'bizClass',
                  onChange : function(field, value, rowIndex, oldValue, text) {
                      var _form = this;
                      var url = IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/biz-categories/queries/selectable';
                      if (!Gikam.isEmpty(value)) {
                          url += Gikam.param({
                              bizClassId_EQ : value,
                          });
                          Gikam.getJson(url).done(function(items) {
                              _form.setSelectOptions('bizCategoryId', items.map(function(item) {
                                  return {
                                      text : item.bizCategoryName,
                                      value : item.id,
                                  };
                              }), true);
                              if (items.length > 0) {
                                  _form.setData({
                                      bizCategoryId : value,
                                  });
                              } else {
                                  _form.setData({
                                      bizCategoryId : '',
                                  });
                              }
                          });
                      } else {
                          _form.setSelectOptions('bizCategoryId', []);
                      }
                  },
                  editable : true,
                  preInsert : true
                }  , {
                  field : 'bizCategoryId',
                  title : 'T_EMC_PROJECT.BIZCATEGORY',
                  type : 'select',
                  onChange : function(field, value, rowIndex, oldValue, text) {
                      var url = IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/biz-categories/' + value;
                      var _form = this;
                      Gikam.getJson(url).done(function(row) {
                          _form.setData({
                              sample : row.sample,
                          });
                          if (row.showSample == '1') {
                              _form.showFields([ 'sample' ]);
                          } else {
                              _form.hideFields([ 'sample' ]);
                          }
                      });
                      if (!Gikam.isEmpty(value)) {
                          var url = IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/biz-categories/projects/queries/selectable';
                          url += Gikam.param({
                              bizCategoryId : value,
                              activatedFlag : '1',
                          });
                          Gikam.getJson(url).done(function(items) {
                              _form.setSelectOptions('projName', items.map(function(item) {
                                  return {
                                      text : item.projName,
                                      value : item.id
                                  };
                              }), true);
                          });
                      }
                  },
                  editable : true,
                  preInsert : true
              },]
        },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcAdvisoryReport.workflow.dbTable), {
            field : 'reportNo',
            title : '报告编号',
        }, {
          field : 'projNo',
          title : '检测任务编号',
          type : 'choose',
          editor : true,
          category : 'project',
          single : true,
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
            field : 'ext$.projname',
            title : 'T_EMC_ADVISORY_REPORT.PROJNAME',
        }, {
            field : 'ext$.projusername',
            title : 'T_EMC_ADVISORY_REPORT.PROJUSERID'
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_ADVISORY_REPORT.TESTORGNAME'
        }, {
            field : 'ext$.entrustorgname',
            title : 'T_EMC_ADVISORY_REPORT.ENTRUSTORGNAME'
        }, {
            field : 'ext$.bizcategoryname',
            title : 'T_EMC_ADVISORY_REPORT.BIZCATEGORY'
        }, {
            field : 'remark',
            title : 'T_EMC_ADVISORY_REPORT.REMARK',
            editor : true
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcAdvisoryReport.workflow.dbTable), {
            field : 'ext$.projno',
            title : 'T_EMC_ADVISORY_REPORT.PROJNO',
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_ADVISORY_REPORT.PROJNAME',
        }, {
            field : 'ext$.projusername',
            title : 'T_EMC_ADVISORY_REPORT.PROJUSERID'
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_ADVISORY_REPORT.TESTORGNAME'
        }, {
            field : 'ext$.entrustorgname',
            title : 'T_EMC_ADVISORY_REPORT.ENTRUSTORGNAME'
        }, {
            field : 'ext$.bizcategoryname',
            title : 'T_EMC_ADVISORY_REPORT.BIZCATEGORY'
        }, {
            field : 'remark',
            title : 'T_EMC_ADVISORY_REPORT.REMARK'
        } ];
    },

    getIssueListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcAdvisoryReport.workflow.dbTable), {
            field : 'ext$.projno',
            title : 'T_EMC_ADVISORY_REPORT.PROJNO',
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_ADVISORY_REPORT.PROJNAME',
        }, {
            field : 'discuss',
            title : 'T_EMC_ADVISORY_REPORT.DISCUSS',
            validators : [ 'notEmpty' ],
            type : 'select',
            editor : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'revise',
            title : 'T_EMC_ADVISORY_REPORT.REVISE',
            validators : [ 'notEmpty' ],
            type : 'select',
            editor : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'ext$.projusername',
            title : 'T_EMC_ADVISORY_REPORT.PROJUSERID'
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_ADVISORY_REPORT.TESTORGNAME'
        }, {
            field : 'ext$.entrustorgname',
            title : 'T_EMC_ADVISORY_REPORT.ENTRUSTORGNAME'
        }, {
            field : 'ext$.bizcategoryname',
            title : 'T_EMC_ADVISORY_REPORT.BIZCATEGORY'
        }, {
            field : 'remark',
            title : 'T_EMC_ADVISORY_REPORT.REMARK'
        } ];
    },

    getArchivingListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.projno',
            title : 'T_EMC_ADVISORY_REPORT.PROJNO',
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_ADVISORY_REPORT.PROJNAME',
        }, {
            field : 'ext$.projusername',
            title : 'T_EMC_ADVISORY_REPORT.PROJUSERID'
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_ADVISORY_REPORT.TESTORGNAME'
        }, {
            field : 'ext$.entrustorgname',
            title : 'T_EMC_ADVISORY_REPORT.ENTRUSTORGNAME'
        }, {
            field : 'ext$.bizcategoryname',
            title : 'T_EMC_ADVISORY_REPORT.BIZCATEGORY'
        }, {
            field : 'remark',
            title : 'T_EMC_ADVISORY_REPORT.REMARK'
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcAdvisoryReport.workflow.dbTable), {
            field : 'ext$.projno',
            title : 'T_EMC_ADVISORY_REPORT.PROJNO',
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_ADVISORY_REPORT.PROJNAME',
        }, {
            field : 'ext$.projusername',
            title : 'T_EMC_ADVISORY_REPORT.PROJUSERID'
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_ADVISORY_REPORT.TESTORGNAME'
        }, {
            field : 'ext$.entrustorgname',
            title : 'T_EMC_ADVISORY_REPORT.ENTRUSTORGNAME'
        }, {
            field : 'ext$.bizcategoryname',
            title : 'T_EMC_ADVISORY_REPORT.BIZCATEGORY'
        }, {
            field : 'remark',
            title : 'T_EMC_ADVISORY_REPORT.REMARK'
        } ];
    },

    workflow : {
        dbTable : 'T_EMC_ADVISORY_REPORT',
        columns : [ {
            field : 'id',
            title : 'T_EMC_ADVISORY_REPORT.ID'
        } ]
    },

})
