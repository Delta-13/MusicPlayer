package com.example.shaoyangyang.myplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;

//import androidx.core.app.NotificationCompat;

public class DownloadService extends Service {
    private DownloadTask downloadTask;
    private String downloadUrl;

    ///DownloadListener匿名类，显示下载进度的通知
    private DownloadListener listener=new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            //getNotification（）显示下载进度的通知，notify（）触发这个通知，使得在下拉状态栏中实时看到当前下载的进度
            getNotificationManager().notify(1,getNotification("Downloading...",progress));
        }

        @Override
        public void onSuccess() {
            downloadTask=null;
            //下载成功时将前台服务通知关闭，并创建一个下载成功的通知
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Download Success",-1));//notify（）用于显示通知
            Toast.makeText(DownloadService.this,"Download Success",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            downloadTask=null;
            //下载失败时将前台拂去通知关闭，并创建一个下载失败的通知
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Download Failed",-1));//notify（）用于显示通知
            Toast.makeText(DownloadService.this,"Download Failed",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downloadTask=null;
            Toast.makeText(DownloadService.this,"Paused",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadTask=null;
            Toast.makeText(DownloadService.this,"Canceled",Toast.LENGTH_SHORT).show();
        }
    };

    //用于服务与活动间进行通信
    private DownloadBinder mBinder=new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent){
        return mBinder;
    }

    //包括开始下载、暂停下载、和取消下载的函数
    class DownloadBinder extends Binder {
        public void startDownload(String url){
            if (downloadTask==null){
                downloadUrl=url;
                downloadTask=new DownloadTask(listener);
                downloadTask.execute(downloadUrl);//开启下载
                startForeground(1,getNotification("Downloading...",0));//让这个下载服务成为了一个前台服务
                Toast.makeText(DownloadService.this,"Downloading...",Toast.LENGTH_SHORT).show();
            }
        }
        public void pauseDownload(){
            if (downloadTask!=null){
                downloadTask.pauseDownload();
            }
        }
        public  void cancelDownload(){
            if (downloadTask!=null){
                downloadTask.cancelDownload();
            }else {
                if (downloadUrl!=null){
                    //取消下载时需将文件删除，并将通知关闭
                    String fileName=downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file=new File(directory+fileName);
                    if (file.exists()){
                        file.delete();
                    }
                    getNotificationManager().cancel(1);//关闭通知
                    stopForeground(true);
                    Toast.makeText(DownloadService.this,"Canceled",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private NotificationManager getNotificationManager(){
        return (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    //显示进度条
    private Notification getNotification(String title, int progress){

        String CHANNEL_ONE_ID = "com.primedu.cn";//
        String CHANNEL_ONE_NAME = "Channel One";//
        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH); notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); manager.createNotificationChannel(notificationChannel);
        }

        //pendinginttent为延迟执行的intent，可以用于启动活动/服务/发送广播
        Intent intent=new Intent(this,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,intent,0);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,CHANNEL_ONE_ID);//创建notification对象
        builder.setSmallIcon(R.mipmap.ic_launcher);//设置通知的小图标
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));//设置通知的大图标
        builder.setContentIntent(pi);//接受pendingintent，实现点击通知就可以跳转到对应的活动
        builder.setContentTitle(title);//指定通知的标题内容，下拉状态栏就可以看到这部分内容，
        if (progress>=0){
            //当progress大于或者等于0时才需要显示下载进度
            builder.setContentText(progress+"%");//指定通知的正文内容
            builder.setProgress(100,progress,false);//设置完setProgress函数后，通知上就会有进度条显示出来了
        }
        return builder.build();
    }
}
