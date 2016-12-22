package knowledge.prime.batterysaver;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

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
            Log.d("call", "RECIVE sleep, type:" + Env.intervalType + ", wakeup:" + Env.wakeupTime + ", sleep:" + Env.getSleepTime());

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
            if (Env.isPlugged && !Env.isDebug) {
                Log.d("plug", "skip stop, because plugged.");
                return;
            }
            Log.d("s", "called stop");
            WifiHandler.isConnect(BtSleepService.this, false);
            MobileDataConnectionHandler.toConnectMobile(Env.context, false);

        } finally {
            // Wakelockの解除処理が必ず呼ばれるようにしておく
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }


}
