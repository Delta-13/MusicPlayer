package com.example.shaoyangyang.myplayer;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<String,Integer,Integer> {
    //以下四个整型常量用于表示下载的状态
    public static final int TYPE_SUCCESS=0;
    public static final int TYPE_FAILED=1;
    public static final int TYPE_PAUSED=2;
    public static final int TYPE_CANCELED=3;

    private DownloadListener listener;//用于传给构造函数，将下载的状态通过这个参数进行回调
    private boolean isCanceled=false;
    private boolean isPaused=false;
    private int lastProgress;

    //回调函数
    public DownloadTask(DownloadListener listener){
        this.listener=listener;
    }

    //从参数中获取下载的url地址
    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile=null;
        File file=null;
        try{
            long downloadedLength=0;//记录已下载的文件长度
            String downloadUrl=params[0];
            String fileName=downloadUrl.substring(downloadUrl.lastIndexOf("/"));//解析出文件名

            //将文件下载到Environment.DIRECTORY_DOWNLOADS目录下（SD卡的download目录）
            String directory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            //判断目录中是否已存在要下载的文件
            file=new File(directory+fileName);
            if (file.exists()){//文件已存在
                downloadedLength=file.length();//启用断点续传的功能
            }
            long contentLength=getContentLength(downloadUrl);//获取待下载文件的总长度
            if (contentLength==0){///文件有问题
                return TYPE_FAILED;
            } else if (contentLength == downloadedLength) {//文件已下载成功
                return TYPE_SUCCESS;
            }
            OkHttpClient client=new OkHttpClient();
            //断点下载，指定从那个字节开始下载
            Request request=new Request.Builder().addHeader("RANGE","bytes= "+downloadedLength+"-").url(downloadUrl).build();
            Response response=client.newCall(request).execute();//读取服务器响应的数据
            if (response!=null){
                is=response.body().byteStream();
                savedFile=new RandomAccessFile(file,"rw");
                savedFile.seek(downloadedLength);//跳过已下载的字节
                byte[] b=new byte[1024];
                int total=0;
                int len;
                while ((len=is.read(b))!=-1){//判断用户有没有除法暂停或者取消的操作
                    if (isCanceled){
                        return TYPE_CANCELED;
                    }else if (isPaused){
                        return TYPE_PAUSED;
                    }else {
                        total+=len;
                        savedFile.write(b,0,len);//写入本地
                        //计算已下载的百分比
                        int progress=(int)((total+downloadedLength)*100/contentLength);
                        publishProgress(progress);//调用publishProgress（）函数进行通知
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if (is!=null){
                    is.close();
                }
                if (savedFile!=null){
                    savedFile.close();
                }
                if (isCanceled&&file!=null){
                    file.delete();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;

    }

    //在界面上更新当前的下载进度
    @Override
    protected void onProgressUpdate(Integer... values){
        int progress=values[0];
        if (progress>lastProgress){
            listener.onProgress(progress);
            lastProgress=progress;
        }
    }

    //通知最终的下载结果
    @Override
    protected void onPostExecute(Integer status){
        switch (status){
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
            default:
                break;
        }
    }

    public void pauseDownload(){
        isPaused=true;
    }
    public void cancelDownload(){
        isCanceled=true;
    }

    //获取待下载文件的总长度
    private long getContentLength(String downloadUrl)throws IOException {
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(downloadUrl).build();
        Response response=client.newCall(request).execute();
        if (response!=null&&response.isSuccessful()){
            long contentLength=response.body().contentLength();
            response.body().close();
            return contentLength;
        }
        return 0;
    }
}
