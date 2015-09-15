package com.rockraft7.plugin.multifiletransfers;

import android.widget.Toast;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;

public class MultiFileTransfers extends CordovaPlugin {
    public static final String TAG = MultiFileTransfers.class.getName();

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("upload".equals(action)) {
            String files = args.getString(0);
            String params = args.getString(1);

            LOG.d(TAG, "Files=" + files + ", Params=" + params);
            Toast.makeText(cordova.getActivity(), "Hello!!!", Toast.LENGTH_LONG).show();
        }

        return false;
    }
}