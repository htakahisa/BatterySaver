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
public class BtService extends IntentService {


    public BtService() {
        super("BtService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {

            Log.d("call", "RECIVE wakeup, wakeup:" + Env.wakeupTime + ", sleep:" + Env.sleepTime + ", idle:" + Env.idleTime);



            //
            if (Env.isPlugged) {
                Log.d("plug", "always on, because charging");
                WifiHandler.isConnect(BtService.this, true);
                isConnectMobile(true);

                return;
            }

            //画面がONの時は常に wakeup
            if (Env.isScreenOn && !Env.isDebug) {
                Log.d("screen", "always wake up because screen on.");
                WifiHandler.isConnect(BtService.this, true);
                isConnectMobile(true);
                return;
            }


            //まずは設定 ON (すでに ON なら何もしない)
            Log.d("d", "wakeup");
            WifiHandler.isConnect(BtService.this, true);
            isConnectMobile(true);


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
