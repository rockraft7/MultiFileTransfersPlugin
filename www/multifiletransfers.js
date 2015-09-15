var exec = require('cordova/exec');

exports.uploadmultiple = function (files, params, success, error) {
    exec(success, error, "MultipleFileTransfer", "upload", [files, params]);
};