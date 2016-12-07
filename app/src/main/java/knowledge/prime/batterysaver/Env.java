package knowledge.prime.batterysaver;

/**
 * Created by takahisa007 on 11/30/16.
 */
public class Env {

    //type 1
    public static long sleepTime;
    public static long wakeupTime;
    public static long idleTime;
    public static long count;

    //type 2
    public static long sleepTime2;
    public static long count2;

    //type3
    public static long sleepTime3;
    public static long count3;

    //type 4
    public static long sleepTime4;

    public static boolean isStop;


    public static boolean isScreenOn;

    public static boolean isScreenOffIdle;

    public static boolean isPlugged;

    public static boolean isDebug = false;

    public static long sleepCount = 0;
    // 1, 2, 3, 4
    public static int intervalType = 1;

    public static long getSleepTime() {
        switch (intervalType) {
            case 1: return sleepTime;
            case 2: return sleepTime2;
            case 3: return sleepTime3;
            case 4: return sleepTime4;
        }
        return 0;
    }
}

