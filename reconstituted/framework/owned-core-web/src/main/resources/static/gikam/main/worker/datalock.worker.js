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

function post(url) {
    return new Deferred(function(resolve, reject) {
        let xhr = new XMLHttpRequest();
        xhr.open('post', url);
        xhr.send();
        xhr.onreadystatechange = function() {
            if (this.readyState === 4) {
                if (this.status === 200) {
                    resolve(this.responseText);
                } else {
                    reject(this.responseText);
                }
                if (JSON.parse(this.responseText).message === 'core.security.login.unauthorized') {
                    self.close();
                    self.clearInterval(interval);
                }
            }
        };
    });
}

function mark(event) {
    if (event.data === 'end') {
        self.close();
        self.clearInterval(interval);
    }
    post(event.data.IFM_CONTEXT + `/secure/core/module/gikam/repositories/mark-editable/${event.data.markFlag}`);
}

onmessage = function(event) {
    interval = setInterval(function() {
        mark(event);
    }, 7 * 1000);
};
