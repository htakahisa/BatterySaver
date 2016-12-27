package knowledge.prime.batterysaver;

import android.app.IntentService;
import android.content.Intent;
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

            Log.d("call", "RECIVE wakeup, type:"+Env.intervalType+", wakeup:" + Env.wakeupTime + ", idleTime:" + Env.idleTime);

            //充電中は常にON
            if (Env.isPlugged && !Env.isDebug) {
                Log.d("plug", "always on, because charging");
                MobileDataConnectionHandler.toConnectMobile(Env.context, true);
                WifiHandler.isConnect(BtService.this, true);

                return;
            }

            //画面がONの時は常に wakeup
            if (Env.isScreenOn && !Env.isDebug) {
                Log.d("screen", "always wake up because screen on.");
            }

            //テザリング中も常に wakeup
            if (Env.isTetheringOn) {
                Log.d("tether", "always wake up because Tethering ON");
            }

            //まずは設定 ON (すでに ON なら何もしない)
            Log.d("d", "wakeup");
            this.wakeUpWifiRestrictedArea();
            MobileDataConnectionHandler.toConnectMobile(Env.context, true);


    } finally {
            // Wakelockの解除処理が必ず呼ばれるようにしておく
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    /**
     * モバイル通信ができるか確認してからWIFI接続します。
     */
    private void wakeUpWifiRestrictedArea() {
        if (Env.isWifiRestrictedArea) {//指定場所
            //指定場所でも、画面がOFFで、夜の場合は on にしない
            if (!Env.isScreenOn && SpecifiedTimeHandler.isSpecifiedTime()) {
                return;
            }

            WifiHandler.isConnect(Env.context, true);
        }
    }


}
