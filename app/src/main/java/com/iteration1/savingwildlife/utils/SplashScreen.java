package com.iteration1.savingwildlife.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.iteration1.savingwildlife.R;

public class SplashScreen {

    private Dialog splashDialog;
    private Activity activity;

    public SplashScreen(Activity activity) {
        this.activity = activity;
    }


    public void show(final int millis) {
//public void show() {
        Runnable runnable = () -> {
            DisplayMetrics metrics = new DisplayMetrics();

            final ImageView root = new ImageView(activity);
            root.setMinimumHeight(metrics.heightPixels);
            root.setMinimumWidth(metrics.widthPixels);
            root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
            root.setScaleType(ImageView.ScaleType.FIT_XY);
            root.setImageResource(R.drawable.splash_screen);

            splashDialog = new Dialog(activity, android.R.style.Theme_NoTitleBar_Fullscreen);


            splashDialog.setContentView(root);
            splashDialog.setCancelable(false);
            splashDialog.show();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    removeSplash();
                }
            }, millis);
        };
        activity.runOnUiThread(runnable);
    }

    public void hide() {
        removeSplash();
    }

    private void removeSplash() {
        if (splashDialog != null && splashDialog.isShowing()) {
            splashDialog.dismiss();
            splashDialog = null;
        }
    }
}
