package knowledge.prime.batterysaver;

import android.content.Context;
import android.net.ConnectivityManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by takahisa007 on 12/8/16.
 */
public class MobileDataConnectionHandler {


    public static void toConnectMobile(Context context, boolean isConnect) {
        try {
            Env.isMobileWakeTime = isConnect;
            final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class<?> conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, isConnect);

            EventLog.d(MobileDataConnectionHandler.class, "connect", "mobile data network is " + isConnect);
        } catch (Exception e) {

        }
    }
}
