package com.echo.jsbridge;

import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * Created by jiangecho on 16/6/19.
 * just for test
 */
public class TestJsModule implements JsModule {
    public static void showToast(WebView webView, JSONObject param, final JsCallback callback) {
        String message = param.optString("msg");
        Toast.makeText(webView.getContext(), message, Toast.LENGTH_SHORT).show();
        if (null != callback) {
            try {
                JSONObject object = new JSONObject();
                object.put("key", "value");
                object.put("key1", "value1");
                callback.apply(JsBridge.createResponse(0, "ok", object));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
