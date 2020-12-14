package com.example.shaoyangyang.myplayer.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.shaoyangyang.myplayer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OnlineFragment extends Fragment {

    private WebView webview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        webview = (WebView) view.findViewById(R.id.online_music_webView);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);


        //支持缩放
        settings.setUseWideViewPort(true);//设定支持viewport
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);//设定支持缩放

        //打开的网址
        webview.loadUrl("https://freemusicarchive.org/genre/Classical/");
    }
}
