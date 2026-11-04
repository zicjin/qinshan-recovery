coreAuditLog.traceListPage = {
  getAuditConfigGridParam: function () {
    var _this = this;
    return {
      type: "grid",
      single: true,
      page: true,
      order: false,
      checkOnActive: true,
      id: "core-audit-config-choose-list-grid",
      url: IFM_CONTEXT + "/secure/core/module/sys/audit/audit-configs/queries",
      requestData: {
        id_IN: "T_CORE_USER,T_CORE_ROLE",
      },
      columns: coreAuditLog.getCoreAuditConfigChooseListColumns(),
      toolbar: [],
      onLoadSuccess: function (data) {
        if (!Gikam.isEmpty(data)) {
          this.activeRowByIndex(0);
        } else {
          Gikam.cleanCompData([
            "core-audit-log-trace-overview-list-grid",
            "core-audit-log-search-list-grid",
          ]);
        }
      },
      onRowActive: function (index, row) {
        if (row) {
          _this.param.id = row.id;
        }

        _this.onConfigActive(row);
      },
    };
  },

  getAuditLogOverviews: function () {
    var _this = this;
    return {
      type: "grid",
      single: true,
      page: true,
      order: false,
      filter: false,
      checkOnActive: true,
      id: "core-audit-log-trace-overview-list-grid",
      columns: coreAuditLog.getOverviewListColumns(),
      toolbar: [],
      onLoadSuccess: function (data) {
        if (!Gikam.isEmpty(data)) {
          this.activeRowByIndex(0);
        } else {
          Gikam.cleanCompData(["core-audit-log-search-list-grid"]);
        }
      },
      onBeforeLoad: function (data) {
        data.forEach(function (item) {
          var splitArr = item.targetId.split("$");
          item.tableName = Gikam.propI18N(splitArr[0] + ".SERVICE_NAME");
          item.deleted = !!item.deleted
            ? Gikam.propI18N("GIKAM.SELECT.YES")
            : Gikam.propI18N("GIKAM.SELECT.NO");
        });
      },
      onRowActive: function (index, row) {
        _this.onOverviewActive(row);
      },
    };
  },

  getAuditLogs: function () {
    var _this = this;

    return {
      type: "tab",
      id: "core-audit-log-search-list-grid-tab",
      width: Gikam.width.N,
      panels: [
        {
          title: "CORE.MODULE.AT.ATRECORDS.TAB.RECORD_LINE_GRID",
          items: [
            {
              type: "grid",
              id: "core-audit-log-search-list-grid",
              service: "coreAuditLogColumnServiceImpl",
              columns: coreAuditLog.getAuditLogSearchListColumns(),
              page: false,
              order: false,
              filter: false,
              toolbar: [],
              group: {
                groupCheckbox: false,
                fields: ["activityId"],
                formatter: function (row) {
                  return (
                    Gikam.propI18N(row.activity) +
                    "  操作人：" +
                    row.userId +
                    "  操作时间：" +
                    row.createdTime
                  );
                },
              },
              onBeforeLoad: function (data) {
                data.forEach(function (item) {
                  item.columnName = Gikam.propI18N(
                    item.targetTable + "." + item.column
                  );
                  if (!item.ext$) {
                    item.ext$ = {};
                  }
                  if (!item.ext$.oldvalue) {
                    item.ext$.oldvalue = item.oldValue;
                  }
                  if (!item.ext$.newvalue) {
                    item.ext$.newvalue = item.newValue;
                  }
                });
              },
            },
          ],
        },
      ],
    };
  },

  onConfigActive: function (row) {
    var _this = this;
    if (row) {
      var grid = Gikam.getComp("core-audit-log-trace-overview-list-grid");
      grid.cleanData();
      grid.refresh({
        url: coreAuditLog.baseUrl + "/queries/trace-overview",
        requestData: {
          tableName: row.id,
        },
      });
    }
  },

  onOverviewActive: function (row) {
    var _this = this;
    if (row) {
      var targetId = row.targetId;
      var grid = Gikam.getComp("core-audit-log-search-list-grid");
      grid.cleanData();
      var url = Gikam.printf(
        coreAuditLog.baseUrl +
          "/queries/columns-with-log/{tableName}/{targetId}",
        {
          tableName: _this.param.id,
          targetId: targetId,
        }
      );
      Gikam.post(url).done(function (data) {
        grid.loadData(data);
      });
    }
  },

  create: function () {
    var _this = this;
    Gikam.create("layout", {
      west: {
        width: 500,
        items: [_this.getAuditConfigGridParam()],
      },

      center: {
        items: [
          {
            type: "layout",

            north: {
              height: "300",
              items: [_this.getAuditLogOverviews()],
            },

            center: {
              items: [_this.getAuditLogs()],
            },
          },
        ],
      },

      renderTo: workspace.window.$dom,
    });
  },

  init: function () {
    this.param = {};
    this.create();
  },
};
