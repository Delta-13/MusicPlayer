package com.example.shaoyangyang.myplayer;


public interface DownloadListener {
    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onPaused();

    void onCanceled();

}

