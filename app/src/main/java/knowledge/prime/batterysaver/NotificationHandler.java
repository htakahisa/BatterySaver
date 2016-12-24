package knowledge.prime.batterysaver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by takahisa007 on 12/9/16.
 */
public class NotificationHandler {


    public static void setNotification(Context context) {
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notif= getNotification(context);
        //常駐させる
        notif.flags = Notification.FLAG_ONGOING_EVENT;
        nm.notify(Env.NOTIFICATION_ID, notif);

    }

    public static void deleteNotification(Context context) {
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(1);
    }

    public static Notification getNotification(Context context) {
        PendingIntent contentIntent = PendingIntent.getActivity(
                context, 0,
                new Intent(context, MainActivity.class), 0);

        Notification notif= new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
//                .setContentText("")
                .setSmallIcon(R.drawable.status_bar_notif2)
                .setContentIntent(contentIntent)
                .build();
        return notif;
    }
}
