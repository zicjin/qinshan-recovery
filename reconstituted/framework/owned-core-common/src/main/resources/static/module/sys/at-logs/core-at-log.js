var coreAtLog = Gikam.getPageObject({
  baseUrl: IFM_CONTEXT + "/secure/core/module/sys/at-records",
  getCoreAtConfigChooseListColumns: function () {
    return [
      {
        radio: true,
      },
      {
        title: "T_CORE_AT_CONFIG.ID",
        field: "id",
        search: true,
        width: 150,
      },
      {
        title: "T_CORE_AT_CONFIG.ID",
        field: "ext$.tabledesc",
        formatter: function (idx, value) {
          return Gikam.propI18N(value);
        },
      },
      {
        title: "T_CORE_AT_CONFIG.AUDITTRAIL",
        field: "auditTrail",
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
        title: "T_CORE_AT_RECORD.DELETED",
        field: "deleted",
        width: 100,
      },
      {
        title: "T_CORE_AT_RECORD.TARGETID",
        field: "bizId",
        width: 250,
      },
      {
        title: "T_CORE_AT_RECORD.TARGETNAME",
        field: "targetName",
      },
    ];
  },

  getAtRecordSearchListColumns: function () {
    return [
      {
        title: "T_CORE_AT_RECORD_LINE.COLUMN",
        field: "columnName",
        width: 250,
      },
      {
        title: "T_CORE_AT_RECORD_LINE.OLDVALUE",
        field: "ext$.oldvalue",
        width: 250,
      },
      {
        title: "T_CORE_AT_RECORD_LINE.NEWVALUE",
        field: "ext$.newvalue",
        width: 250,
      },
    ];
  },
});
