package com.iteration1.savingwildlife;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class OurInfo extends Fragment {

    private View fView;
    private WebView webView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        fView = inflater.inflate(R.layout.fragment_about, container, false);
        webView = fView.findViewById(R.id.abouttxt);
        String s = "<html><body style='text-align:justify;' bgcolor=\"#F3F7F7\">" + getString(R.string.about_me) +
                "</body></html>";
        webView.loadData(s, "text/html", "UTF-8");
        webView.getSettings().setTextZoom(100);

        return fView;
    }
}
