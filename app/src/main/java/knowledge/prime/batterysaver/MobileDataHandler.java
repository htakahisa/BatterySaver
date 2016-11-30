package knowledge.prime.batterysaver;

import java.lang.reflect.Method;

/**
 * Created by takahisa007 on 11/30/16.
 */
public class MobileDataHandler {

    public static void init(Object o, Method m) {
        setMobileDataEnabledMethod = m;
        iConnectivityManager = o;
    }

    private static Method setMobileDataEnabledMethod;
    private static Object iConnectivityManager;

    public static void isConnect(boolean is) {



        try {
            setMobileDataEnabledMethod.invoke(iConnectivityManager, is);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
