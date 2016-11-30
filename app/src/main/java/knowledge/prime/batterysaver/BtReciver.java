package knowledge.prime.batterysaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

/**
 * Created by takahisa007 on 11/30/16.
 */
public class BtReciver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("d", "recive");


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

                //遅延実行でOFF
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Env.isStop) {
                            Log.d("s", "stoped.");
                            return;
                        }
                        if (Env.isScreenOn) {
                            Log.d("s", "stop skip. 'case screen on");
                            return;
                        }
                        Log.d("s", "called stop by screen off");
                        WifiHandler.isConnect(false);
                        MobileDataHandler.isConnect(false);

                        Env.isScreenOffIdle = true;

                    }
                }, Env.idleTime);

            return;
            } else {
                Log.d("intent", action);
            }

        }

        //画面がONの時は常に wakeup
        if (Env.isScreenOn) {
            Log.d("screen", "always wake up because screen on.");
            WifiHandler.isConnect(true);
            MobileDataHandler.isConnect(true);
            return;
        }

        //まずは設定 ON (すでに ON なら何もしない)
        Log.d("d", "wakeup");
        WifiHandler.isConnect(true);
        MobileDataHandler.isConnect(true);

        //遅延実行でOFF
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Env.isStop) {
                    Log.d("s", "stoped.");
                    return;
                }
                if (Env.isScreenOn) {
                    Log.d("s", "stop skip. 'case screen on");
                    return;
                }
                if (Env.isScreenOffIdle) {
                    Log.d("s", "skip stop. because screen off idle");
                    Env.isScreenOffIdle = false;
                    return;
                }
                Log.d("s", "called stop");
                WifiHandler.isConnect(false);
                MobileDataHandler.isConnect(false);

            }
        }, Env.wakeupTime);
    }
}
