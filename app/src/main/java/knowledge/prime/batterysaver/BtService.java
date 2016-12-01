package knowledge.prime.batterysaver;

import android.app.IntentService;
import android.content.Intent;
import android.os.BatteryManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by takahisa007 on 12/1/16.
 */
public class BtService extends IntentService {


    public BtService() {
        super("BtService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {

            Log.d("call", "RECIVE wakeup, wakeup:" + Env.wakeupTime + ", sleep:" + Env.sleepTime + ", idle:" + Env.idleTime);


            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Intent.ACTION_SCREEN_ON)) {
                    // 画面ON時
                    Log.d("screen", "SCREEN_ON");
                    Env.isScreenOn = true;
                    return;
                } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                    // 画面OFF時
                    Log.d("screen", "SCREEN_OFF");
                    Env.isScreenOn = false;
                    return;
//            } else if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
//                //バッテリー接続時は ON のまま
//                int plugged = intent.getIntExtra("plugged", 0);
//                if (plugged == BatteryManager.BATTERY_PLUGGED_AC
//                        || plugged == BatteryManager.BATTERY_PLUGGED_USB
//                        ) {
//                    Log.d("plug", "always on. because plugged(ac=1, usd=2):" + plugged);
//                    WifiHandler.isConnect(true);
//                    MobileDataHandler.isConnect(true);
//                }
//                return;
                } else {
                    Log.d("intent", action);
                }
            }

            //
            if (Env.batteryStatus != null && BatteryManager.BATTERY_STATUS_CHARGING == Env.batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
                Log.d("plug", "always on, charging");
                WifiHandler.isConnect(true);
                MobileDataHandler.isConnect(true);

                return;
            }

            //画面がONの時は常に wakeup
            if (Env.isScreenOn && !Env.isDebug) {
                Log.d("screen", "always wake up because screen on.");
                WifiHandler.isConnect(true);
                MobileDataHandler.isConnect(true);
                return;
            }

            //まずは設定 ON (すでに ON なら何もしない)
            Log.d("d", "wakeup");
            WifiHandler.isConnect(true);
            MobileDataHandler.isConnect(true);


    } finally {
            // Wakelockの解除処理が必ず呼ばれるようにしておく
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }
}
