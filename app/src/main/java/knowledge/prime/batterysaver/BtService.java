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

            Log.d("call", "RECIVE wakeup, type:"+Env.intervalType+", wakeup:" + Env.wakeupTime + ", idleTime:" + Env.idleTime);



            //充電中は常にON
            if (Env.isPlugged && !Env.isDebug) {
                Log.d("plug", "always on, because charging");
                WifiHandler.isConnect(BtService.this, true);
                isConnectMobile(true);

                return;
            }

            //画面がONの時は常に wakeup
            if (Env.isScreenOn && !Env.isDebug) {
                Log.d("screen", "always wake up because screen on.");
                this.wakeUpWifiRestrictedArea();
                isConnectMobile(true);
                return;
            }


            //まずは設定 ON (すでに ON なら何もしない)
            Log.d("d", "wakeup");
            this.wakeUpWifiRestrictedArea();
            isConnectMobile(true);


    } finally {
            // Wakelockの解除処理が必ず呼ばれるようにしておく
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    /**
     * 指定場所では WIFI ON
     */
    private void wakeUpWifiRestrictedArea() {
        for (String cellId : CellInfoHandler.getCellId(BtService.this)) {
            //指定場所なら WI-FI ON
            if (Env.wifiCellIdSet.contains(cellId)) {
                WifiHandler.isConnect(BtService.this, true);
                Log.d("cellId", "wifi on. Restricted area.");
            }
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
