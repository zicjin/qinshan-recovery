coreAtLog.traceListPage = {
  getAtConfigGridParam: function () {
    var _this = this;
    return {
      type: "grid",
      single: true,
      page: true,
      order: false,
      checkOnActive: true,
      id: "core-at-config-choose-list-grid",
      url: IFM_CONTEXT + "/secure/core/module/sys/at-configs/queries",
      requestData: {
        id_IN: "T_CORE_USER,T_CORE_ROLE",
      },
      columns: coreAtLog.getCoreAtConfigChooseListColumns(),
      toolbar: [],
      onLoadSuccess: function (data) {
        if (!Gikam.isEmpty(data)) {
          this.activeRowByIndex(0);
        } else {
          Gikam.cleanCompData([
            "core-at-record-trace-overview-list-grid",
            "core-at-record-search-list-grid",
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

  getAtRecordOverviews: function () {
    var _this = this;
    return {
      type: "grid",
      single: true,
      page: true,
      order: false,
      filter: false,
      checkOnActive: true,
      id: "core-at-record-trace-overview-list-grid",
      columns: coreAtLog.getOverviewListColumns(),
      toolbar: [],
      onLoadSuccess: function (data) {
        if (!Gikam.isEmpty(data)) {
          this.activeRowByIndex(0);
        } else {
          Gikam.cleanCompData(["core-at-record-search-list-grid"]);
        }
      },
      onBeforeLoad: function (data) {
        data.forEach(function (item) {
          var splitArr = item.targetId.split("$");
          item.tableName = Gikam.propI18N(splitArr[0] + ".SERVICE_NAME");
          item.bizId = splitArr[1];
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

  getAtRecords: function () {
    var _this = this;

    return {
      type: "tab",
      id: "core-at-record-search-list-grid-tab",
      width: Gikam.width.N,
      panels: [
        {
          title: "CORE.MODULE.AT.ATRECORDS.TAB.RECORD_LINE_GRID",
          items: [
            {
              type: "grid",
              id: "core-at-record-search-list-grid",
              service: "coreAtLogColumnServiceImpl",
              columns: coreAtLog.getAtRecordSearchListColumns(),
              page: false,
              order: false,
              filter: false,
              toolbar: [],
              group: {
                groupCheckbox: false,
                fields: ["recordId"],
                formatter: function (row) {
                  return (
                    Gikam.propI18N(row.auditTrailName) +
                    "  操作人：" +
                    row.createdByName +
                    "  操作时间：" +
                    row.createdTime
                  );
                },
              },
              onBeforeLoad: function (data) {
                data.forEach(function (item) {
                  item.columnName = Gikam.propI18N(
                    item.tableName + "." + item.column
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
      var grid = Gikam.getComp("core-at-record-trace-overview-list-grid");
      grid.cleanData();
      grid.refresh({
        url:
          IFM_CONTEXT +
          "/secure/core/module/sys/at-records/queries/trace-overview",
        requestData: {
          tableName: row.id,
        },
      });
    }
  },

  onOverviewActive: function (row) {
    var _this = this;
    if (row) {
      var params = row.targetId.split("$");
      var grid = Gikam.getComp("core-at-record-search-list-grid");
      grid.cleanData();
      var url = Gikam.printf(
        IFM_CONTEXT +
          "/secure/core/module/sys/at-records/queries/record-lines-with-record/{tableName}/{targetId}",
        {
          tableName: params[0],
          targetId: params[1],
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
        items: [_this.getAtConfigGridParam()],
      },

      center: {
        items: [
          {
            type: "layout",

            north: {
              height: "300",
              items: [_this.getAtRecordOverviews()],
            },

            center: {
              items: [_this.getAtRecords()],
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
