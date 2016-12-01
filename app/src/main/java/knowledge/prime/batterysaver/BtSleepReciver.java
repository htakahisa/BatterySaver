package knowledge.prime.batterysaver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by takahisa007 on 12/1/16.
 */
public class BtSleepReciver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
            Intent serviceIntent = new Intent(context,BtSleepService.class);
            serviceIntent.setAction(intent.getAction());
            // サービス起動
            startWakefulService(context,serviceIntent);

    }
}
