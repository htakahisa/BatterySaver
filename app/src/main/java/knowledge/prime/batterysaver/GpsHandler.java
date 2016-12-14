package knowledge.prime.batterysaver;

import android.content.Context;
import android.content.Intent;

/**
 * Created by takahisa007 on 12/11/16.
 */
public class GpsHandler {

    //cannot work on android 4.2.2
    public static void isConnect(Context context, boolean isOn) {
        if (isOn) {
            turnGPSOff(context);
        } else {
            turnGPSOn(context);
        }
    }

    private static void turnGPSOn(Context context){
        Intent intent=new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        context.sendBroadcast(intent);
    }

    private static void turnGPSOff(Context context){
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", false);
        context.sendBroadcast(intent);
    }
}
