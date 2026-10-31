let interval = null;

function Deferred(callback) {
    this.thenCallback = null;
    var _this = this;
    this.resolve = function(data) {
        _this.thenCallback && _this.thenCallback.call(_this, data);
    };
    this.reject = function() {};
    this.then = function(thenCallback) {
        this.thenCallback = thenCallback;
    };
    return callback.call(this, this.resolve, this.reject);
}

function get(url) {
    return new Deferred(function(resolve, reject) {
        let xhr = new XMLHttpRequest();
        xhr.open('get', url);
        xhr.send();
        xhr.onreadystatechange = function() {
            if (this.readyState === 4) {
                if (this.status === 200) {
                    resolve(this.responseText);
                } else {
                    reject(this.responseText);
                }
            }
        };
    });
}

function initResponseTime(event) {
    if (event.data === 'end') {
        self.close();
        self.clearInterval(interval);
    }
    var startTime = new Date().getTime();

    get(event.data.IFM_CONTEXT + '/core/module/sys/statuses/speed').then(function(data) {
        if (data === '1') {
            postMessage((new Date().getTime() - startTime) / 1000);
        } else {
            postMessage(10);
        }
    });
}

onmessage = function(event) {
    var speedTestPeriod = (event.data.speedTestPeriod || 30) * 1000;
    initResponseTime(event);
    interval = setInterval(function() {
        initResponseTime(event);
    }, speedTestPeriod);
};
