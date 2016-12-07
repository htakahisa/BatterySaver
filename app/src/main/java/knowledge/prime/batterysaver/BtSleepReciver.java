package knowledge.prime.batterysaver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;

/**
 * Created by takahisa007 on 12/1/16.
 */
public class BtSleepReciver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action != null) {
            if (action.equals("SLEEP")) {
                //ok
            } else {
                return;
            }
        }



        Intent serviceIntent = new Intent(context,BtSleepService.class);
        serviceIntent.setAction(intent.getAction());
        // サービス起動
        startWakefulService(context,serviceIntent);

    }


    public void cancelManager(Context context) {
        Intent wakeupIntent = new Intent(context, BtReciver.class);
        wakeupIntent.setAction("WAKEUP");
        PendingIntent wakeupSender = PendingIntent.getBroadcast(context, 0, wakeupIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent sleepIntent = new Intent(context, BtSleepReciver.class);
        sleepIntent.setAction("SLEEP");
        PendingIntent sleepSender = PendingIntent.getBroadcast(context, 0, sleepIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(wakeupSender);
        manager.cancel(sleepSender);
    }

    public void setManager(Context context, Calendar triggerTime) {
        // alermManager設定

        //設定した日時で発行するIntentを生成
        Intent wakeupIntent = new Intent(context, BtReciver.class);
        wakeupIntent.setAction("WAKEUP");
        PendingIntent wakeupSender = PendingIntent.getBroadcast(context, 0, wakeupIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //日時と発行するIntentをAlarmManagerにセットします
        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis(), Env.sleepTime + Env.wakeupTime, wakeupSender);


        //設定した日時で発行するIntentを生成
        Intent sleepIntent = new Intent(context, BtSleepReciver.class);
        sleepIntent.setAction("SLEEP");
        PendingIntent sleepSender = PendingIntent.getBroadcast(context, 0, sleepIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //日時と発行するIntentをAlarmManagerにセットします
        manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis() + Env.wakeupTime, Env.wakeupTime + Env.sleepTime, sleepSender);


    }
}
