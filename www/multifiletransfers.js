cordova.define("cordova-plugin-multifiletransfer.MultiFileTransfers", function (require, exports, module) {
    var exec = require('cordova/exec');

    exports.uploadmultiple = function (files, params, success, error) {
        exec(success, error, "MultiFileTransfers", "upload", [files, params]);
    };
});
