window.WorkspaceIndexPage = function(options) {
    window.$frontendAlone = true;
    var request = new XMLHttpRequest();
    var url = '/module/index/workspaces/resource-page';
    if (window.IFM_CONTEXT) {
        url = window.IFM_CONTEXT + url;
    }
    request.open('GET', url, true);
    request.onload = function(result) {
        const styleList = document.querySelectorAll('style');
        document.open();
        document.write(result.target.response);
        Array.from(styleList).forEach(function(style) {
            document.head.appendChild(style);
        });
        document.close();
        if (!result.target.response) {
            var homeUrl = '/home/index.html';
            if (window.IFM_CONTEXT) {
                url = window.IFM_CONTEXT + homeUrl;
            }
            location.href = homeUrl;
            return;
        }
        window.addEventListener('DOMContentLoaded', function() {
            window.workspace = new window.IndexPage(options);
            if (options.onLoad) {
                options.onLoad.call();
            }
        });
    };
    request.send();
};
