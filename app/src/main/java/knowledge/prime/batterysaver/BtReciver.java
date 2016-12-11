package knowledge.prime.batterysaver;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
                Env.intervalType = 0;
                Env.sleepCount = 0;
                resetInterval(context);
                return;


            } else if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                //バッテリー接続時は ON のまま
                int plugged = intent.getIntExtra("plugged", 0);
                if (plugged == BatteryManager.BATTERY_PLUGGED_AC
                        || plugged == BatteryManager.BATTERY_PLUGGED_USB
                        ) {
                    Log.d("plug", "always on. because plugged(ac=1, usd=2):" + plugged);
                    Env.isPlugged = true;
                    Env.intervalType = 0;
                    Env.sleepCount = 0;
                } else {
                    //バッテリーを外した時
                    Log.d("plug", "plug is unplugged");
                    Env.isPlugged = false;

                }
//                Env.isPlugged = false;
                return;
            } else if(action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                //起動時は再設定
                Intent intentActivity = new Intent(context, MainActivity.class);
                intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentActivity);
                return;
            } else if(action.equals("WAKEUP")) {
                Log.d("intent", "WAKEUP intent. count:" + Env.sleepCount +" and then +1");
                Env.sleepCount++;
            } else {
                Log.d("intent", action);
                return;
            }
        }

        Intent serviceIntent = new Intent(context,BtService.class);
        serviceIntent.setAction(intent.getAction());

        // 再設定が必要なら設定する
        if (needToChangeNextType()) {
            resetInterval(context);
            return;
        }
        // サービス起動
        startWakefulService(context,serviceIntent);

    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("H");

    private boolean needToChangeNextType() {

        //画面がON の時は設定の変更不要
        if (Env.isScreenOn) {
            return false;
        }

        long maxCount = 0;
        if (Env.intervalType == 0) {
            maxCount = 1;
        }
        if (Env.intervalType == 1) {
            maxCount = Env.count;
        }
        if (Env.intervalType == 2) {
            maxCount = Env.count2;
        }
        if (Env.intervalType == 3) {
            maxCount = Env.count3;
        }
        if (Env.intervalType == 4) {
            maxCount = Env.count4;
        }
        if (Env.intervalType == 5) {
            //指定時間外の場合は直ちに type4 に戻す
            Date now = new Date();
            long nowHour = Long.valueOf(sdf.format(now.getTime()));
            if (Env.fromH <= nowHour && nowHour < Env.toH) {
                return false; //時間内は何もしない
            } else {
                Env.intervalType = 4;
                Env.sleepCount = 0;
                return true;
            }
        }

        if (Env.sleepCount > maxCount) {
            Log.d("count", "sleepCount:" + Env.sleepCount + ", maxCount:" + maxCount);
            if (Env.intervalType < 4) {
                Env.intervalType = Env.intervalType + 1;
                Env.sleepCount = 0;
                return true;
            }
            if (Env.intervalType == 4) {// type4 の時は時間帯によって type 5に変更
                Date now = new Date();
                long nowHour = Long.valueOf(sdf.format(now.getTime()));
                if (Env.fromH <= nowHour && nowHour < Env.toH) {
                    Env.intervalType = 5;
                    Env.sleepCount = 0;
                    return true;
                }
            }
        } else {
            return false;
        }

        return false;
    }


    private void resetInterval(Context context) {
        long sleepTime = 300000; //初期値は安全のため 5分
        long wakeupTime = Env.wakeupTime;

        switch (Env.intervalType) {
            case 0:
                wakeupTime = Env.idleTime;
                sleepTime = Env.sleepTime;
                break;
            case 1:
                sleepTime = Env.sleepTime;
                break;
            case 2:
                sleepTime = Env.sleepTime2;
                break;
            case 3:
                sleepTime = Env.sleepTime3;
                break;
            case 4:
                sleepTime = Env.sleepTime4;
                break;
            case 5:
                sleepTime = Env.sleepTime5;
                break;
        }

        AlarmManagerHandler.cancelSchedule(context);
        AlarmManagerHandler.setSchedule(context, Calendar.getInstance(), sleepTime, wakeupTime);

    }

}
