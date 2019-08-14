package com.example.report.notification;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;


public class NotifyJobService extends Service {

    private Context mContext;

    public NotifyJobService(Context context) {
        mContext = context;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationFactory.runNotification(mContext, NotificationFactory.NotificationType.JOB_FINISHED);
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
