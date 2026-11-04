var coreAuditLog = Gikam.getPageObject({
  baseUrl: IFM_CONTEXT + "/secure/core/module/sys/audit/audit-logs",
  getCoreAuditConfigChooseListColumns: function () {
    return [
      {
        radio: true,
      },
      {
        title: "T_CORE_AUDIT_CONFIG.ID",
        field: "id",
        search: true,
        width: 150,
      },
      {
        title: "T_CORE_AUDIT_CONFIG.NAME",
        field: "name",
        formatter: function (idx, value) {
          return Gikam.propI18N(value);
        },
      },
      {
        title: "T_CORE_AUDIT_CONFIG.AUDITABLE",
        field: "auditable",
        type: "select",
        category: "sys",
        param: {
          codeCategoryId: "confirmFlag",
        },
        readonly: true,
      },
    ];
  },

  getOverviewListColumns: function () {
    return [
      {
        radio: true,
      },
      {
        title: "T_CORE_AUDIT_LOG.DELETED",
        field: "deleted",
        width: 100,
      },
      {
        title: "T_CORE_AUDIT_LOG.TARGETID",
        field: "targetId",
        width: 250,
      },
      {
        title: "T_CORE_AUDIT_LOG.TARGETNAME",
        field: "targetName",
      },
    ];
  },

  getAuditLogSearchListColumns: function () {
    return [
      {
        title: "T_CORE_AUDIT_LOG_COLUMN.COLUMN",
        field: "columnName",
        width: 250,
      },
      {
        title: "T_CORE_AUDIT_LOG_COLUMN.OLEVALUE",
        field: "ext$.oldvalue",
        width: 250,
      },
      {
        title: "T_CORE_AUDIT_LOG_COLUMN.NEWVALUE",
        field: "ext$.newvalue",
        width: 250,
      },
    ];
  },

  getBaseActivityInfoFormFields : function() {
    var startTime = new Date();
    return [ {
      title : 'T_CORE_AUDIT_CONFIG.NAME',
      field : 'targetTable',
      type : 'select',
      category : 'audit-config',
      search : true
    }, {
      title : 'GIKAM.FROM',
      field : 'startDate',
      type : 'dateTime',
      value : Gikam.DateUtils.formatter(startTime, 'yyyy-MM-dd hh:mm:ss')
    }, {
      title : 'GIKAM.TO',
      field : 'endDate',
      type : 'dateTime',
      value : Gikam.DateUtils.formatter(startTime, 'yyyy-MM-dd hh:mm:ss')
    }, {
      title : 'T_CORE_AUDIT_LOG_ACTIVITY.ACTIVITY',
      field : 'activity',
      type : 'select',
      category : 'audit-activity-category'
    }, {
      title : 'T_CORE_AUDIT_LOG.TARGETID',
      field : 'targetId',
    }, {
      title : 'T_CORE_AUDIT_LOG_ACTIVITY.USERNAME',
      field : 'ext$.username'
    } ]
  },

  getActivitySearchListColumns : function() {
    return [ {
      checkbox : true
    }, {
      index : true
    }, {
      title : 'T_CORE_AUDIT_CONFIG.ID',
      field : 'ext$.targettable',
      width : 160
    }, {
      title : 'T_CORE_AUDIT_CONFIG.NAME',
      field : 'ext$.targettablename',
      width : 160
    }, {
      title : 'T_CORE_AUDIT_LOG.TARGETID',
      field : 'ext$.targetid',
      width : 140
    }, {
      title : 'T_CORE_AUDIT_LOG.ITEMDESC',
      field : 'ext$.itemdesc'
    }, {
      title : 'T_CORE_AUDIT_LOG_ACTIVITY.CREATEDTIME',
      field : 'createdTime',
      width : 150
    }, {
      title : 'T_CORE_AUDIT_LOG_ACTIVITY.ACTIVITY',
      field : 'activity',
      type : 'select',
      category : 'audit-activity-category',
      width : 90
    }, {
      title : 'T_CORE_AUDIT_LOG_ACTIVITY.USERNAME',
      field : 'ext$.username'
    } ];
  },
});
