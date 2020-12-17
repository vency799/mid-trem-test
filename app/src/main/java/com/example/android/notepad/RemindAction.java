package com.example.android.notepad;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

public class RemindAction extends BroadcastReceiver {

    public static int id = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(String.valueOf(RemindAction.this),"yunxingtongzhi");
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);

        Notification.Builder mBuilder = new Notification.Builder(context);
        //标题
        mBuilder.setContentTitle(intent.getStringExtra("title"));
        //内容
        mBuilder.setContentText(intent.getStringExtra("note"));
        //小图标
        mBuilder.setSmallIcon(R.drawable.app_notes);
        //大图标
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_notes));

        mBuilder.setContentIntent(pi);
        //点击自动消失
        mBuilder.setAutoCancel(true);

        //创建通知
        Notification notification = mBuilder.build();
        notificationManager.notify(id++, notification);
    }
}
