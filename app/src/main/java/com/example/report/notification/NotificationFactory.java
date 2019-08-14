package com.example.report.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.text.format.Time;

import androidx.core.app.NotificationCompat;

import com.example.report.MainActivity;
import com.example.report.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

// TODO Find optimal Service for running

public class NotificationFactory {

    public static final String NOTIFICATION_JOB_CHANNEL_ID = "notificationJobChannel";
    public static final String NOTIFICATION_REPORT_CHANNEL_ID = "notificationReportChannel";
    public static final int PENDING_INTENT_ID = 1;

    public enum NotificationType {
        JOB_FINISHED,
        REPORT_CREATED
    }

    private static NotificationChannel getNotificationChannelAndReturnId(Context context, String channelId) {
        NotificationChannel channel;

        switch (channelId) {
            case NOTIFICATION_JOB_CHANNEL_ID: {
                channel = new NotificationChannel(NOTIFICATION_JOB_CHANNEL_ID,
                        context.getString(R.string.channel_job_name),
                        NotificationManager.IMPORTANCE_HIGH);
                return channel;
            }

            case NOTIFICATION_REPORT_CHANNEL_ID: {
                channel = new NotificationChannel(NOTIFICATION_REPORT_CHANNEL_ID,
                        context.getString(R.string.channel_report_name),
                        NotificationManager.IMPORTANCE_HIGH);
                return channel;
            }

            default:
                throw new IllegalArgumentException();
        }




    }

    public static void runNotification(Context context, NotificationType notificationType) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId;
        switch (notificationType) {
            case JOB_FINISHED:
                channelId = NOTIFICATION_JOB_CHANNEL_ID;
                break;
            case REPORT_CREATED:
                channelId = NOTIFICATION_REPORT_CHANNEL_ID;
                break;
            default:
                throw new IllegalArgumentException();
        }


        notificationManager.createNotificationChannel(getNotificationChannelAndReturnId(context, channelId));


        String title = context.getString(R.string.notification_title_job);

        Date currentTime = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String time = format.format(currentTime);
        String description = context.getString(R.string.notification_description_prefix_job) +
                " " + time + ". " + context.getString(R.string.notification_description_job);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(getContent(context));
        if (Build.VERSION.SDK_INT < 26) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        //  TODO Create an ID
        notificationManager.notify(1, notificationBuilder.build());
    }

    private static PendingIntent getContent(Context context) {
        Intent launchAppIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context,
                PENDING_INTENT_ID,
                launchAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
