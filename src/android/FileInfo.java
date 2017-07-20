package com.rockraft7.plugin.multifiletransfers;

/**
 * Created by 608761587 on 16/09/2015.
 */
public class FileInfo {
    private String fileKey;
    private String fileName;
    private String mimeType;
    private String encodedData;

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getEncodedData() {
        return encodedData;
    }

    public void setEncodedData(String encodedData) {
        this.encodedData = encodedData;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileKey='" + fileKey + '\'' +
                ", fileName='" + fileName + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", encodedData='" + encodedData + '\'' +
                '}';
    }
}
