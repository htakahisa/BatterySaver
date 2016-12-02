package knowledge.prime.batterysaver;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by takahisa007 on 12/1/16.
 */
public class BtSleepService extends IntentService {

    public BtSleepService() {
        super("BtSleepService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.d("call", "RECIVE sleep, wakeup:" + Env.wakeupTime + ", sleep:" + Env.sleepTime + ", idle:" + Env.idleTime);

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



            if (Env.isStop) {
                Log.d("s", "stoped.");
                return;
            }
            if (Env.isScreenOn) {
                Log.d("s", "skip stop. because screen on");
                return;
            }
            if (Env.isScreenOffIdle) {
                Log.d("s", "skip stop. because screen off idle");
                Env.isScreenOffIdle = false;
                return;
            }
            if (Env.isPlugged) {
                Log.d("plug", "skip stop, because plugged.");
                return;
            }
            Log.d("s", "called stop");
            WifiHandler.isConnect(BtSleepService.this, false);
            isConnectMobile(false);

        } finally {
            // Wakelockの解除処理が必ず呼ばれるようにしておく
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }


    private void isConnectMobile(boolean isConnect) {
        try {
            final ConnectivityManager conman = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class<?> conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, isConnect);

        } catch (Exception e) {

        }
    }
}
