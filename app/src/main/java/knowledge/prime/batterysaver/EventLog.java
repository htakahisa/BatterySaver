package knowledge.prime.batterysaver;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by takahisa007 on 12/30/16.
 */
public class EventLog {

    private static final int MAX_SIZE = 300;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d H:m:s.SSS");

    public static void d(Class<?> cls, String tag, String log) {
        if (Env.eventLog.size() == MAX_SIZE) {
            Env.eventLog.removeLast();
        }
        Env.eventLog.addFirst(cls.getSimpleName() + " " + sdf.format(new Date())+ " " + tag + ":" + log);
        Log.d(tag, log);
    }
}
