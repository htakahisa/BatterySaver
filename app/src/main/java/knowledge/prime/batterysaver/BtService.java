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

            //まずは設定 ON (すでに ON なら何もしない)
            Log.d("d", "wakeup");
            MobileDataConnectionHandler.toConnectMobile(Env.context, true);
//            this.wakeUpWifiRestrictedArea();


    } finally {
            // Wakelockの解除処理が必ず呼ばれるようにしておく
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

//    /**
//     * モバイル通信ができるか確認してからWIFI接続します。
//     */
//    private void wakeUpWifiRestrictedArea() {
//
//        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = manager.getActiveNetworkInfo();
//        if (info == null || !info.isConnected()) {
//            Log.d("wifi", "mobile network is not connected. wifi-on skip.");
//            return;
//        }
//
//        for (String cellId : CellInfoHandler.getCellId(BtService.this)) {
//            //指定場所なら WI-FI ON
//            if (Env.wifiCellIdSet.contains(cellId)) {
//                WifiHandler.isConnect(BtService.this, true);
//                Log.d("cellId", "wifi on. Restricted area.");
//            }
//        }
//        Env.isNetworkChanged = false;
//
//    }



}
