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
            EventLog.d(this.getClass(), "call", "RECIVE sleep, type:" + Env.intervalType + ", wakeup:" + Env.wakeupTime + ", sleep:" + Env.getSleepTime());

            if (Env.isStop) {
                EventLog.d(this.getClass(), "s", "stoped.");
                return;
            }
            if (Env.isScreenOn) {
                EventLog.d(this.getClass(), "s", "skip stop. because screen on");
                return;
            }
            if (Env.isScreenOffIdle) {
                EventLog.d(this.getClass(), "s", "skip stop. because screen off idle");
                Env.isScreenOffIdle = false;
                return;
            }
            if (Env.isPlugged && !Env.isDebug) {
                EventLog.d(this.getClass(), "plug", "skip stop, because plugged.");
                return;
            }
            if (Env.isTetheringOn) {
                EventLog.d(this.getClass(), "tethering", "skip stop, because tethering ON");
                return;
            }

            EventLog.d(this.getClass(), "s", "called stop");
            WifiHandler.isConnect(BtSleepService.this, false);
            MobileDataConnectionHandler.toConnectMobile(Env.context, false);

        } finally {
            // Wakelockの解除処理が必ず呼ばれるようにしておく
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }


}
