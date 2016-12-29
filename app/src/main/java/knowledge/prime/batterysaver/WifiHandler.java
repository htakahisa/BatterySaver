package knowledge.prime.batterysaver;

import android.content.Context;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;

/**
 * Created by takahisa007 on 11/28/16.
 */
public class WifiHandler {

    private static WifiManager wifi;

    public static void init(WifiManager w) {
        wifi = w;
    }

    public static void isConnect(Context context, boolean is) {
        EventLog.d(WifiHandler.class, "connect", "wifi is " + is);
        Env.isWifiWakeTime = is;

        if (wifi == null) {
            wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        }


        if (wifi.isWifiEnabled() == is) {
            return;
        }

        wifi.setWifiEnabled(is);

    }


    private boolean isTetheringOn(){

        if (wifi == null) {
            wifi = (WifiManager)Env.context.getSystemService(Context.WIFI_SERVICE);
        }

        String status = "UNSUPPORTED";
        try {
            Method method = wifi.getClass().getMethod("isWifiApEnabled");
            Boolean isWifiApEnabled = (Boolean)method.invoke(wifi);
            return isWifiApEnabled.booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
