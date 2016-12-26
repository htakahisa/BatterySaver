package knowledge.prime.batterysaver;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by takahisa007 on 12/26/16.
 */
public class SpecifiedTimeHandler {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("H");

    public static boolean isSpecifiedTime() {

        Date now = new Date();
        long nowHour = Long.valueOf(sdf.format(now.getTime()));

        if (Env.fromH > Env.toH) {
            //23時から 7時などの場合はこちらで判定
            if (Env.fromH <= nowHour || nowHour < Env.toH) {
                return true;
            }
        } else {
            if (Env.fromH <= nowHour && nowHour < Env.toH) {
                return true;//時間内
            }
        }
        return false;
    }
}
