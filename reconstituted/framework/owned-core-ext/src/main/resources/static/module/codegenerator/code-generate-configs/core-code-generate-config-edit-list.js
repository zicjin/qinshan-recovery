coreCodeGenerateConfig.editPage = {
  getGridParam: function () {
    var _this = this;
    return {
      type: "grid",
      id: "core-code-generate-config-edit-list-grid",
      url: coreCodeGenerateConfig.baseUrl + "/queries/raw",
      columns: coreCodeGenerateConfig.getEditListColumns(),
      onCellClick: function (field, row) {
        field === "id" && _this.loadDetailPage(row.id);
      },
      deleteFormatter: function (row) {
        return row.packageName || row.id;
      },
      requestData: {
        createdById_SEQ: workspace.user.id,
        authority_org: 1,
      },
      toolbar: [
        {
          type: "button",
          text: "GIKAM.BUTTON.INSERT",
          icon: "add",
          onClick: function () {
            _this.loadDetailPage();
          },
        },
        {
          type: "button",
          text: "CORE.MODULE.CODEGENERATOR.CODEGENERATECONFIGS.BUTTON.COPY",
          icon: "copy",
          onClick: function () {
            var selectedRows = Gikam.getComp(
              "core-code-generate-config-edit-list-grid"
            ).getSelections();
            if (selectedRows.length !== 1) {
              Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
              return;
            }
            var id = selectedRows[0].id;
            var url = Gikam.printf(
              coreCodeGenerateConfig.baseUrl + "/{id}/action/copy",
              {
                id: id,
              }
            );
            Gikam.post(url).done(function (data) {
                _this.loadDetailPage(data);
            });
          },
        },
        {
          type: "button",
          text: "GIKAM.BUTTON.DELETE",
          icon: "remove",
          onClick: function () {
            Gikam.getComp(
              "core-code-generate-config-edit-list-grid"
            ).deleteRows(coreCodeGenerateConfig.baseUrl);
          },
        },
      ],
    };
  },

  create: function () {
    var _this = this;
    Gikam.create("layout", {
      renderTo: workspace.window.$dom,
      center: {
        items: [this.getGridParam()],
      },
    });
  },

  loadDetailPage: function (id) {
    workspace.window.load(
      coreCodeGenerateConfig.baseUrl +
        "/page/detail" +
        Gikam.param({
          id: id,
        })
    );
  },

  init: function () {
    this.create();
  },
};
