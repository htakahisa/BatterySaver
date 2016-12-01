package knowledge.prime.batterysaver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by takahisa007 on 11/30/16.
 */
public class BtReciver extends WakefulBroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context,BtService.class);
        serviceIntent.setAction(intent.getAction());
        // サービス起動
        startWakefulService(context,serviceIntent);
    }
}
