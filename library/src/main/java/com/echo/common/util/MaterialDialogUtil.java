package com.echo.common.util;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import cn.pocdoc.majiaxian.R;
import cn.pocdoc.majiaxian.fragment.h5.WebViewFragment;
import cn.pocdoc.majiaxian.model.PocdocProtocolInfo;

/**
 * Created by pengwei on 15/3/20.
 */
public class MaterialDialogUtil {

    public static MaterialDialog showProgressDialog(final Context context, String title) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title(title)
                .progress(true, 0)
                .cancelable(false)
                .build();
        materialDialog.show();
        return materialDialog;
    }

    public static void showAutoDismissProgressDialog(final Context context, long millisToDismiss, final String titleText, final int color, DialogInterface.OnDismissListener listener) {
        final MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title(titleText)
                .progress(true, 0)
                .cancelable(false)
                .build();
        materialDialog.setOnDismissListener(listener);
        AsyncTask<Long, Void, Void> task = new AsyncTask<Long, Void, Void>() {
            /**
             * Runs on the UI thread before {@link #doInBackground}.
             *
             * @see #onPostExecute
             * @see #doInBackground
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                materialDialog.show();
            }

            /**
             * <p>Runs on the UI thread after {@link #doInBackground}. The
             * specified result is the value returned by {@link #doInBackground}.</p>
             * <p/>
             * <p>This method won't be invoked if the task was cancelled.</p>
             *
             * @param aVoid The result of the operation computed by {@link #doInBackground}.
             * @see #onPreExecute
             * @see #doInBackground
             * @see #onCancelled(Object)
             */
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    materialDialog.dismiss();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected Void doInBackground(Long... params) {
                try {
                    Thread.sleep(params[0]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute(millisToDismiss);
    }

    public static void showAlertDialog(Context context, String title, String content) {
        showDialog(context, title, content, null);
    }

    public static void showAlertDialog(Context context, String content) {
        showAlertDialog(context, null, content);
    }

    public static void showDialog(Context context, String title, String content, MaterialDialog.ButtonCallback callback) {
        new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .callback(callback)
                .negativeText(context.getString(R.string.cancel))
                .positiveText(context.getString(R.string.confirm))
                .show();

    }

    public static void showWebViewDialog(FragmentActivity context, String title, String htmlContent) {
        View view = LayoutInflater.from(context).inflate(R.layout.webview_dialog_layout, null);
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        WebViewFragment webViewFragment = (WebViewFragment) fragmentManager.findFragmentById(R.id.webViewFragment);
        webViewFragment.setProgressBarVisibility(View.GONE);
        webViewFragment.loadHtmlContent(htmlContent);


        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .customView(view, false)
                .dismissListener((dialog) -> {
                    fragmentManager.beginTransaction().remove(webViewFragment).commit();
                    dialog.dismiss();
                });
        if (!TextUtils.isEmpty(title)) {
            builder.title(title);
        }

        MaterialDialog materialDialog = builder.build();

        webViewFragment.setOnShouldOverrideUrlLoadingListener((webView, pocdocProtocolInfo) -> {
            if (PocdocProtocolInfo.TYPE_FRAME.equals(pocdocProtocolInfo.getType())){
                return false;
            }
            materialDialog.dismiss();
            return false;
        });

        WebView webView = (WebView) view.findViewById(R.id.webView);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) webView.getLayoutParams();
        params.setMargins(3, 3, 3, 3);
        webView.setLayoutParams(params);

        view.findViewById(R.id.closeImageButton).setOnClickListener((closeImageButton) -> materialDialog.dismiss());

//        WindowManager.LayoutParams windowLayoutParams = materialDialog.getWindow().getAttributes();
//        windowLayoutParams.width = Utils.px2dip(context, 306);
//        materialDialog.getWindow().setAttributes(windowLayoutParams);
        materialDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        materialDialog.show();
    }
}
