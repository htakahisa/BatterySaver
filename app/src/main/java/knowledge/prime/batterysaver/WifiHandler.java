package knowledge.prime.batterysaver;

import android.net.wifi.WifiManager;

/**
 * Created by takahisa007 on 11/28/16.
 */
public class WifiHandler {

    public static void wakeup(WifiManager wifi) {
        wifi.setWifiEnabled(true);
    }


    public static void sleep(WifiManager wifi) {
        wifi.setWifiEnabled(false);
    }


}
