package knowledge.prime.batterysaver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;
import java.util.List;

/**
 * Created by takahisa007 on 11/30/16.
 */
public class BtReciver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

//        Env.isPlugged = false;//ここで初期化しておく

        String action = intent.getAction();
        if (action != null) {
            if (Intent.ACTION_TIME_CHANGED.equals(action) || Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {
//                Log.d("recive", "time change ;" + action);
                // but it seems to need to do nothing.
                return;
             } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                // 画面ON時
                EventLog.d(this.getClass(), "screen", "SCREEN_ON");
                Env.isScreenOn = true;

            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                // 画面OFF時
                EventLog.d(this.getClass(), "screen", "SCREEN_OFF");
                Env.isScreenOn = false;
                Env.intervalType = 0;
                Env.sleepCount = 0;
                resetInterval(context);
                return;

            } else if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())) {
                //バッテリー接続時は ON のまま
                BatteryHandler.isCharging();

            } else if (Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction())) {
                //ここにはプラグを抜いたときの処理
                EventLog.d(this.getClass(), "plug", "plug is unplugged");
                Env.isPlugged = false;
                return;

            } else if(action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                //起動時は再設定
                Intent intentActivity = new Intent(context, MainActivity.class);
                intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentActivity);
                return;
            } else if(action.equals("WAKEUP")) {
                EventLog.d(this.getClass(), "intent", "WAKEUP intent. count:" + Env.sleepCount + " and then +1");
                Env.sleepCount++;
            } else if (action.equals("android.net.conn.TETHER_STATE_CHANGED")) {

                List<String> tetheringNameList = intent.getStringArrayListExtra("activeArray");

                if (tetheringNameList == null || tetheringNameList.size() == 0) {
                    EventLog.d(this.getClass(), "intent", "tethering OFF");
                    Env.isTetheringOn = false;
                } else {
                    EventLog.d(this.getClass(), "intent", "tethering ON");
                    Env.isTetheringOn = true;
                }
                return;
            } else {
                EventLog.d(this.getClass(), "intent", "BtReceiver:" + action);
                return;
            }
        }


        // 再設定が必要なら設定する
        if (needToChangeNextType()) {
            resetInterval(context);
            return;
        }
        // サービス起動
        Intent serviceIntent = new Intent(context,BtService.class);
        serviceIntent.setAction(intent.getAction());
        startWakefulService(context,serviceIntent);

    }



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
            if (SpecifiedTimeHandler.isSpecifiedTime()) {
                return false; //時間内は何もしない
            } else {
                Env.intervalType = 4;
                Env.sleepCount = 0;
                return true;
            }
        }

        if (Env.sleepCount > maxCount) {
            EventLog.d(this.getClass(), "count", "sleepCount:" + Env.sleepCount + ", maxCount:" + maxCount);
            if (Env.intervalType < 4) {
                Env.intervalType = Env.intervalType + 1;
                Env.sleepCount = 0;
                return true;
            }
            if (Env.intervalType == 4) {// type4 の時は時間帯によって type 5に変更
                if (SpecifiedTimeHandler.isSpecifiedTime()) {
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
                sleepTime = Env.sleepTime;
                wakeupTime = Env.idleTime;
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
