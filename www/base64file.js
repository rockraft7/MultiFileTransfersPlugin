cordova.define("com.rockraft7.plugin.multifiletransfers.Base64File", function(require, exports, module) {
    var exec = require('cordova/exec');
    var Base64File = function (fileKey, fileName, mimeType, encodedData) {
        this.fileKey = fileKey || null;
        this.fileName = fileName || null;
        this.mimeType = mimeType || null;
        this.encodedData = encodedData || null;
    };

    module.exports = Base64File;
});