package knowledge.prime.batterysaver;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by takahisa007 on 12/30/16.
 */
public class EventLog {

    private static final int MAX_SIZE = 70;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d H:m:s.SSS");

    public static void d(Class<?> cls, String tag, String log) {
        while (Env.eventLog.size() > MAX_SIZE) {
            Env.eventLog.removeLast();
        }
        Env.eventLog.addFirst(sdf.format(new Date()) + " " + tag + ":" + log + "[" + cls.getSimpleName() +"]");
        Log.d(tag, log);
    }
}
