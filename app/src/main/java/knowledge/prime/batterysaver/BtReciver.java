package knowledge.prime.batterysaver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by takahisa007 on 11/30/16.
 */
public class BtReciver extends WakefulBroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Env.isPlugged = false;//ここで初期化しておく

        String action = intent.getAction();
        if (action != null) {
            if (Intent.ACTION_TIME_CHANGED.equals(action) || Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {
                Log.d("recive", "time change ;" + action);
                // but it seems to need to do nothing.
                return;
             } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                // 画面ON時
                Log.d("screen", "SCREEN_ON");
                Env.isScreenOn = true;

            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                // 画面OFF時
                Log.d("screen", "SCREEN_OFF");
                Env.isScreenOn = false;
                cancelManager(context);
                setManager(context, Calendar.getInstance());
                return;


            } else if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                //バッテリー接続時は ON のまま
                int plugged = intent.getIntExtra("plugged", 0);
                if (plugged == BatteryManager.BATTERY_PLUGGED_AC
                        || plugged == BatteryManager.BATTERY_PLUGGED_USB
                        ) {
                    Log.d("plug", "always on. because plugged(ac=1, usd=2):" + plugged);
                    Env.isPlugged = true;
                } else {
                    Log.d("plug", "plug is unplugged");
                    Env.isPlugged = false;

                }
                return;
            } else if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
                //起動時は再設定
                setManager(context, Calendar.getInstance());
                setNotification(context);
                return;
            } else {
                Log.d("intent", action);
                return;
            }
        }

        Intent serviceIntent = new Intent(context,BtService.class);
        serviceIntent.setAction(intent.getAction());

        // サービス起動
        startWakefulService(context,serviceIntent);
    }

    public void cancelManager(Context context) {
        Intent wakeupIntent = new Intent(context, BtReciver.class);
        PendingIntent wakeupSender = PendingIntent.getBroadcast(context, 0, wakeupIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent sleepIntent = new Intent(context, BtSleepReciver.class);
        PendingIntent sleepSender = PendingIntent.getBroadcast(context, 0, sleepIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(wakeupSender);
        manager.cancel(sleepSender);
    }

    public void setManager(Context context, Calendar triggerTime) {
        // alermManager設定

        //設定した日時で発行するIntentを生成
        Intent wakeupIntent = new Intent(context, BtReciver.class);
        PendingIntent wakeupSender = PendingIntent.getBroadcast(context, 0, wakeupIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //日時と発行するIntentをAlarmManagerにセットします
        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis(), Env.sleepTime + Env.wakeupTime, wakeupSender);


        //設定した日時で発行するIntentを生成
        Intent sleepIntent = new Intent(context, BtSleepReciver.class);
        PendingIntent sleepSender = PendingIntent.getBroadcast(context, 0, sleepIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //日時と発行するIntentをAlarmManagerにセットします
        manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis() + Env.wakeupTime, Env.wakeupTime + Env.sleepTime, sleepSender);


    }


    private void setNotification(Context context) {
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context, 0,
                new Intent(context, MainActivity.class), 0);

        Notification notif= new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
//                .setContentText("")
                .setSmallIcon(R.drawable.status_bar_notification)
                .setContentIntent(contentIntent)
                .build();
        //常駐させる
        notif.flags = Notification.FLAG_ONGOING_EVENT;
        nm.notify(1, notif);

    }
}
