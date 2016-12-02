package knowledge.prime.batterysaver;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Created by takahisa007 on 11/28/16.
 */
public class WifiHandler {

    private static WifiManager wifi;

    public static void init(WifiManager w) {
        wifi = w;
    }

    public static void isConnect(Context context, boolean is) {

        if (wifi == null) {
            wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        }


        if (wifi.isWifiEnabled() == is) {
            return;
        }

        wifi.setWifiEnabled(is);
    }


}
