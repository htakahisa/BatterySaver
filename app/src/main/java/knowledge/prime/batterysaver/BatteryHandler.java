package knowledge.prime.batterysaver;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Created by takahisa007 on 12/30/16.
 */
public class BatteryHandler {

    public static boolean isCharging() {
        IntentFilter batteryFiler = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = Env.context.registerReceiver(null, batteryFiler);

        return isCharging(batteryStatus);
    }

    public static boolean isCharging(Intent batteryStatus) {
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        if(status == BatteryManager.BATTERY_PLUGGED_AC
                || status == BatteryManager.BATTERY_PLUGGED_USB
                || status == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
            //充電中
            EventLog.d(BatteryHandler.class, "plug", "always on. because plugged(ac=1, usd=2):" + status);
            Env.isPlugged = true;
            if (Env.intervalType != 6) {
                Env.intervalType = 0;
            }
            Env.sleepCount = 0;
            return true;
        } else {
            //プラグを外す
            EventLog.d(BatteryHandler.class, "plug", "unplugged. status:" + status);
            return false;
        }
    }
}
