var exec = require('cordova/exec');

var MultiFileTransfers = function () {
};

MultiFileTransfers.prototype.uploadmultiple = function (base64Files, target, successCallback, errorCallback, params) {
    var args = [];
    args.push(base64Files);
    args.push(target);
    args.push(params);

    console.log("Calling upload on native....");
    exec(successCallback, errorCallback, "MultiFileTransfers", "upload", args);
};

MultiFileTransfers.prototype.testPlugin = function(success, error) {
    exec(success, error, "MultiFileTransfers", "testPlugin", []);
}

module.exports = MultiFileTransfers;

