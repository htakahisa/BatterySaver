package knowledge.prime.batterysaver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by takahisa007 on 12/8/16.
 */
public class AlarmManagerHandler {

    public static void cancelSchedule(Context context) {
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


    public static void setSchedule(Context context, Calendar triggerTime, long sleepTime, long wakeupTime) {
        // alermManager設定

        //設定した日時で発行するIntentを生成
        Intent wakeupIntent = new Intent(context, BtReciver.class);
        wakeupIntent.setAction("WAKEUP");
        PendingIntent wakeupSender = PendingIntent.getBroadcast(context, 0, wakeupIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //日時と発行するIntentをAlarmManagerにセットします
        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis(), sleepTime + wakeupTime, wakeupSender);


        //設定した日時で発行するIntentを生成
        Intent sleepIntent = new Intent(context, BtSleepReciver.class);
        sleepIntent.setAction("SLEEP");
        PendingIntent sleepSender = PendingIntent.getBroadcast(context, 0, sleepIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //日時と発行するIntentをAlarmManagerにセットします
        manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis() + wakeupTime, sleepTime + wakeupTime, sleepSender);


    }
}
