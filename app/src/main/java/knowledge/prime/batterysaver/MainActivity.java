package knowledge.prime.batterysaver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Env.sleepTime  = 60000;
        Env.wakeupTime = 60000;

        Env.idleTime   = 60000;


        // alermManager設定
        //呼び出す日時を設定する
        Calendar triggerTime = Calendar.getInstance();

        //設定した日時で発行するIntentを生成
        Intent wakeupIntent = new Intent(MainActivity.this, BtReciver.class);
        final PendingIntent wakeupSender = PendingIntent.getBroadcast(MainActivity.this, 0, wakeupIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //日時と発行するIntentをAlarmManagerにセットします
        final AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis(), Env.sleepTime + Env.wakeupTime, wakeupSender);


        //設定した日時で発行するIntentを生成
        Intent sleepIntent = new Intent(MainActivity.this, BtSleepReciver.class);
        final PendingIntent sleepSender = PendingIntent.getBroadcast(MainActivity.this, 0, sleepIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //日時と発行するIntentをAlarmManagerにセットします
        final AlarmManager sleepManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        sleepManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis() + Env.wakeupTime, Env.wakeupTime + Env.sleepTime, sleepSender);




        //スクリーンの状態 on/off
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        Env.isScreenOn= pm.isScreenOn();


        //done ボタンのイベントセット
        findViewById(R.id.doneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Env.isStop = false;

                //sleeptime
                EditText sleepEditText = (EditText) findViewById(R.id.sleepTimeInput);
                if (sleepEditText.getText() != null) {
                    Env.sleepTime = Long.valueOf(sleepEditText.getText().toString()).longValue();
                }
                //wakeuptime
                EditText wakeupEditText = (EditText) findViewById(R.id.wakeupTimeInput);
                if (wakeupEditText.getText() != null) {
                    Env.wakeupTime = Long.valueOf(wakeupEditText.getText().toString()).longValue();
                }
                //idletime
                EditText idleEditText = (EditText) findViewById(R.id.idleTimeInput);
                if (idleEditText.getText() != null) {
                    Env.idleTime = Long.valueOf(idleEditText.getText().toString()).longValue();
                }

                Calendar triggerTime = Calendar.getInstance();
                manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis(), Env.sleepTime + Env.wakeupTime, wakeupSender);
                sleepManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis() + Env.wakeupTime, Env.wakeupTime + Env.sleepTime, sleepSender);

            }
        });

        findViewById(R.id.stopButton).setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     manager.cancel(wakeupSender);
                     sleepManager.cancel(sleepSender);
                     Env.isStop = true;
                    Log.d("call", "stop called");
                 }
             }
        );


        //初期化
        WifiHandler.init((WifiManager)getSystemService(WIFI_SERVICE));

        try {
            final ConnectivityManager conman = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class<?> conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            MobileDataHandler.init(iConnectivityManager, setMobileDataEnabledMethod);
        } catch (Exception e) {

        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(new BtReciver(), filter);

//        IntentFilter ifilter = new IntentFilter();
//        filter.addAction(Intent.ACTION_BATTERY_CHANGED);


//        Intent batteryStatus = this.registerReceiver(null, ifilter);


        Log.d("time", "wakeup:" + Env.wakeupTime + ", sleep:" + Env.sleepTime + ", idle:" + Env.idleTime);
    }



    private boolean isSleep;

    private void switchWakeupOrSleep() {
        Log.d("time", "wakeup:" + Env.wakeupTime + ", sleep:" + Env.sleepTime + ", idle:" + Env.idleTime);

        if (isSleep) {
            //go wakeup
            Log.d("d", "ready wakeup");
            wakeUp();
            isSleep = false;
        } else {
            //go sleep
            Log.d("d", "ready sleep");
            sleep();
            isSleep = true;
        }
    }

    private void wakeUp() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Env.isStop) {
                    Log.d("s", "stoped.");
                    return;
                }
                Log.d("s", "called wakeUp");
                switchWakeupOrSleep();

            }
        }, Env.sleepTime);//遅延実行のため sleepTime後に wakeup が実行される

    }

    private void sleep() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Env.isStop) {
                    Log.d("s", "stoped.");
                    return;
                }

                Log.d("s", "called sleep");
                switchWakeupOrSleep();
            }
        }, Env.wakeupTime);
    }


    private void setIntentFilter() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        this.getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("screen", "screen on");
            }
        }, filter);

        filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        this.getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("onReceive 3");
            }
        }, filter);
    }





}
