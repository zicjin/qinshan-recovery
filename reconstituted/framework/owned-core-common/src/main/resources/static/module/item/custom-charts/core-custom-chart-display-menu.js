coreCustomChart.displayPage = {
  createDetailPage: function () {
    Gikam.create("layout", {
      renderTo: workspace.window.$dom,
      center: {
        items: [
          {
            type: "chartEdit",
            requestId: this.param.id,
            edit: false,
          },
        ],
      },
    });
  },
  createPage: function () {
    Gikam.create("layout", {
      renderTo: workspace.window.$dom,
      center: {
        items: [
          {
            type: "chartEdit",
            requestId: this.param.id,
            edit: false,
            share: this.param.share == 1,
          },
        ],
      },
    });
  },

  init: function (param) {
    this.param = param;
    if (this.param.detailMenu) {
      this.createDetailPage();
    } else {
      this.createPage();
    }
  },
};
