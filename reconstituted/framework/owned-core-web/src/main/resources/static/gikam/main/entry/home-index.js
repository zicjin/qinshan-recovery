window.HomeIndexPage = function(options) {
    window.$frontendAlone = true;
    var request = new XMLHttpRequest();
    var url = '/open/home-resource-page';
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
        window.addEventListener('DOMContentLoaded', function() {
            sessionStorage.setItem('home-index-path', location.pathname);
            new window.IndexPage(options);
        });
    };
    request.send();
};
