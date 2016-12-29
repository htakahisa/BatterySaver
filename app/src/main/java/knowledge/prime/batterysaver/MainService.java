package knowledge.prime.batterysaver;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Calendar;

/**
 * Created by takahisa007 on 12/18/16.
 */
public class MainService extends Service {

    private BtReciver btReciver;

    @Override
    public void onCreate() {

        EventLog.d(this.getClass(), "create", "onCreate called.");
        super.onCreate();

        Env.context = MainService.this;

        //レシーバー登録
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
//        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
//        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        btReciver = new BtReciver();
        registerReceiver(btReciver, filter);

        //充電状態を初期化
        BatteryHandler.isCharging();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        EventLog.d(this.getClass(), "service", "onStartCommand called.");

        //battery saving start
        Calendar triggerTime = Calendar.getInstance();
        AlarmManagerHandler.setSchedule(MainService.this, triggerTime, Env.sleepTime, Env.wakeupTime);

        // ステータス ON
        Env.isStop = false;

        //通知アイコン設定
        NotificationHandler.setNotification(MainService.this);
//        startForeground(Env.NOTIFICATION_ID, NotificationHandler.getNotification(MainService.this));


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        EventLog.d(this.getClass(), "service", "onDestroy called.");
        super.onDestroy();

        //battery saving stop
        AlarmManagerHandler.cancelSchedule(MainService.this);
        //ステータスを 停止に
        Env.isStop = true;

        //通知削除
        NotificationHandler.deleteNotification(MainService.this);

        //レシーバーの解除
        unregisterReceiver(btReciver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
