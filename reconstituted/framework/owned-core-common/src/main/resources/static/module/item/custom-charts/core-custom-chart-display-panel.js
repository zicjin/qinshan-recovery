coreCustomChart.displayPage = {
  createPage: function () {
    Gikam.create("layout", {
      renderTo: document.body,
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
  init: function (param) {
    this.param = param;
    this.createPage();
  },
};
