package knowledge.prime.batterysaver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by takahisa007 on 12/1/16.
 */
public class BtSleepReciver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action != null) {
            if (action.equals("SLEEP")) {
                //wifi を on にする指定場所ならフラグ変更
                changeWIfiRestrictedAreaFlag();

            } else {
                return;
            }
        }

        Intent serviceIntent = new Intent(context,BtSleepService.class);
        serviceIntent.setAction(intent.getAction());
        // サービス起動
        startWakefulService(context,serviceIntent);

    }

    /**
     * WIFI 指定場所ならフラグを設定する
     */
    private void changeWIfiRestrictedAreaFlag() {
        //次回に wifi on にするか判定

        //モバイル接続を確認する
        if (!NetworkInfoHandler.isConnectMobileNetwork(Env.context)) {
            Log.d("wifi", "mobile is not connected now. connecting...");
            Env.isWifiRestrictedArea = false;
            return;//モバイル未接続なら接続しない
        }
        //指定されている場所かどうか
        Boolean isRestrictedArea = NetworkInfoHandler.isRestrictedArea(Env.context);
        if (isRestrictedArea == null) {
            //LTEがまだなので不明(wifi も接続しない)
            Env.isWifiRestrictedArea = false;
            return;
        }
        if (isRestrictedArea.booleanValue()) {//指定されてる場所
            Log.d("wifi", "Restricted Area.");
            Env.isWifiRestrictedArea = true;
        } else {
            Log.d("wifi", "NOT Restricted Area or try connecting now.");
            Env.isWifiRestrictedArea = false;
        }
    }

}
