package knowledge.prime.batterysaver;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by takahisa007 on 12/8/16.
 */
public class PropertyUtils {

    public static void setProperty(Context context, String key, long value) {
        SharedPreferences data = context.getSharedPreferences("DataSave", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = data.edit();
        editor.putLong(key, value);
        editor.apply();
        editor.commit();
    }

    public static void setProperty(Context context, String key, String value) {
        SharedPreferences data = context.getSharedPreferences("DataSave", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = data.edit();
        editor.putString(key, value);
        editor.apply();
        editor.commit();
    }

    public static Long getProperty(Context context, String key, long defaultValue) {
        SharedPreferences data = context.getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        Long l =  data.getLong(key, defaultValue);

        return l;
    }

    public static String getProperty(Context context, String key, String defaultValue) {
        SharedPreferences data = context.getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        String l =  data.getString(key, defaultValue);

        return l;
    }


    /**
     * 初期化
     */
    public static void initOnMemorySetting(Context context) {
        Env.sleepTime   = PropertyUtils.getProperty(context, "sleepTime", 60000);
        Env.sleepTime2  = PropertyUtils.getProperty(context, "sleepTime2", 300000);
        Env.sleepTime3  = PropertyUtils.getProperty(context, "sleepTime3", 600000);
        Env.sleepTime4  = PropertyUtils.getProperty(context, "sleepTime4", 900000);
        Env.sleepTime5  = PropertyUtils.getProperty(context, "sleepTime5", 1800000);

        Env.fromH = PropertyUtils.getProperty(context, "fromH", 23);
        Env.toH = PropertyUtils.getProperty(context, "toH", 7);

        Env.wakeupTime = PropertyUtils.getProperty(context, "wakeupTime", 60000);
        Env.idleTime   = PropertyUtils.getProperty(context, "idleTime", 60000);

        Env.count  = PropertyUtils.getProperty(context, "count", 1);
        Env.count2 = PropertyUtils.getProperty(context, "count2", 3);
        Env.count3 = PropertyUtils.getProperty(context, "count3", 3);
        Env.count4 = PropertyUtils.getProperty(context, "count4", 3);

        Env.intervalType = PropertyUtils.getProperty(context, "intervalType", 0).intValue();

        String cellIds = PropertyUtils.getProperty(context, "cellIds", "");
        Set<String> cellIdSet = new HashSet<>();
        for (String s : cellIds.split(",")) {
            cellIdSet.add(s);
        }
        Env.wifiCellIdSet = cellIdSet;
    }
}
