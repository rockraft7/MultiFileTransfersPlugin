package com.rockraft7.plugin.multifiletransfers;

import android.os.AsyncTask;
import android.content.Context;
import android.util.Base64;
import android.widget.Toast;
import com.squareup.okhttp.*;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * Created by 608761587 on 16/09/2015.
 */
public class MultiFileTransfers extends CordovaPlugin {
    public static final String TAG = "MultiFileTransfer";

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if ("testPlugin".equals(action)) {
            Toast.makeText(cordova.getActivity(), "Testing plugin", Toast.LENGTH_LONG).show();
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "Nice!!!"));
        }

        if ("upload".equals(action)) {
            LOG.d(TAG, "Upload with args: " + args.toString());
            try {
                JSONArray files = args.getJSONArray(0);

                final List<FileInfo> fileInfos = new ArrayList<>();
                for (int i=0; i<files.length(); i++) {
                    JSONObject file = files.getJSONObject(i);
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setFileKey(file.getString("fileKey"));
                    fileInfo.setFileName(file.getString("fileName"));
                    fileInfo.setMimeType(file.getString("mimeType"));
                    fileInfo.setEncodedData(file.getString("encodedData"));
                    fileInfos.add(fileInfo);
                }
                final String url = args.getString(1);
                JSONObject params = args.getJSONObject(2);
                Iterator<String> iterator = params.keys();
                final Map<String, String> paramMap = new HashMap<>();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = params.getString(key);
                    paramMap.put(key, value);
                }

                AsyncTask<Void,Void,String> task = new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String result = "";
                        try {
                            result = uploadFile(url, fileInfos, paramMap);
                        } catch (Exception e) {
                            LOG.d(TAG, "Upload failed.");
                        }
                        return result;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        LOG.d(TAG, "Result: " + result);
                        if (null == result)
                            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "{\"status\":400,\"message\":{\"subject\":\"Bad Request\",\"content\":\"G-Aset ID is required.\"},\"result\":{}}"));
                        else
                            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, result));
                    }
                };
                task.execute();
                PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
                result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
            } catch (JSONException e) {
                LOG.d(TAG, "JSONException here...");
                StringWriter writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                LOG.d(TAG, "Exception: " + writer.toString());
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, writer.toString()));
            }
        }

        return true;
    }

    private String uploadFile(String url, List<FileInfo> fileInfos, Map<String, String> params) throws Exception {
        StringBuffer buffer = new StringBuffer();
        for (String key : params.keySet())
            buffer.append(key + "=" + params.get(key) + ", ");

        LOG.d(TAG, "Uploading " + fileInfos.size() + " files to " + url + " with params: " + buffer.toString());
        OkHttpClient client = new OkHttpClient();
        MultipartBuilder body = new MultipartBuilder();
        body.type(MultipartBuilder.FORM);
        List<File> toBeDeleted = new ArrayList<>();
        for (FileInfo fileInfo : fileInfos) {
            File file = writeAsFile(fileInfo);
            toBeDeleted.add(file);
            body.addFormDataPart(fileInfo.getFileKey(), fileInfo.getFileName(), RequestBody.create(MediaType.parse(fileInfo.getMimeType()), file));
        }
        Iterator<String> iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = params.get(key);
            body.addFormDataPart(key, value);
        }
        Request request = new Request.Builder()
                .url(url)
                .post(body.build())
                .build();
        Response response = client.newCall(request).execute();

        try {
            for (File file : toBeDeleted)
                file.delete();
        } catch (Exception e) {
            LOG.e(TAG, "Files can't be deleted.");
        }
        if (!response.isSuccessful())
            return null;

        return response.body().string();
    }

    private File writeAsFile(FileInfo fileInfo) throws Exception {
        String filePrefix = fileInfo.getFileName().substring(0, fileInfo.getFileName().lastIndexOf("."));
        String extension = fileInfo.getFileName().substring(fileInfo.getFileName().lastIndexOf("."));
        final Context context = this.cordova.getActivity().getApplicationContext();
        File file = File.createTempFile(filePrefix, extension, context.getFilesDir());
        FileOutputStream outputStream = new FileOutputStream(file, true);
        outputStream.write(Base64.decode((fileInfo.getEncodedData()).getBytes(), Base64.DEFAULT));
        outputStream.close();

        return file;
    }
}

