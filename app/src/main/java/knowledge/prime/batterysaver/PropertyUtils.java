package knowledge.prime.batterysaver;

import android.content.Context;
import android.content.SharedPreferences;

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
}
