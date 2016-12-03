package knowledge.prime.batterysaver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Env.sleepTime  = 300000;
        Env.wakeupTime = 60000;

        Env.idleTime   = 60000;

        //初期値のセット
        EditText sleepText = (EditText)findViewById(R.id.sleepTimeInput);
        sleepText.setText(String.valueOf(Env.sleepTime));

        EditText wakeupText = (EditText)findViewById(R.id.wakeupTimeInput);
        wakeupText.setText(String.valueOf(Env.wakeupTime));

        EditText idleText = (EditText)findViewById(R.id.idleTimeInput);
        idleText.setText(String.valueOf(Env.idleTime));

        //alermmanager setting
        this.setManager(Calendar.getInstance());

        //スクリーンの状態 on/off
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        Env.isScreenOn= pm.isScreenOn();

        //ON/OFF ボタンイベントの設定
        setOnOffButtonEvent();

        //Logcat イベントの設定
        setLogcatEvent();

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
            setMobileDataEnabledMethod.invoke(iConnectivityManager, true);

        } catch (Exception e) {

        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(new BtReciver(), filter);

        Log.d("time", "wakeup:" + Env.wakeupTime + ", sleep:" + Env.sleepTime + ", idle:" + Env.idleTime);
    }

    /**
     * Logcat のログを出力します。
     */
    private void setLogcatEvent() {
        TextView view = (TextView)findViewById(R.id.logcat);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int maxLine = 8;
                StringBuilder commandLine = new StringBuilder();
                commandLine.append("logcat ");
                commandLine.append("-d ");
                commandLine.append("-v ");
                commandLine.append("time ");
//                commandLine.append("-s ");

                try{
                    //clear

                    Process process = Runtime.getRuntime().exec(commandLine.toString());
                    BufferedReader br = new BufferedReader( new InputStreamReader(process.getInputStream()), 1024);
                    String thisLine;
                    List<String> logList = new ArrayList<String>();
                    StringBuilder sb = new StringBuilder();
                    while ((thisLine = br.readLine()) != null) {
//                        if (i++ >= maxLine) {
//                            break;
//                        }
                        logList.add(thisLine);
                    }
                    int startLine = logList.size() - maxLine;
                    if (startLine < 0) {
                        startLine = 0;
                    }
                    for (int i = logList.size() - 1; i >= startLine ; i--) {
                        sb.append(logList.get(i)).append(System.getProperty("line.separator"));
                    }

                    ((TextView)view).setText(sb.toString());

                }catch(Exception e){
                    e.printStackTrace();
                }


            }
        });
    }

    /**
     * ON/OFF のボタンのイベント。節電をスタートしたり、ストップする。
     */
    private void setOnOffButtonEvent() {
        //on/off ボタンのイベントセット
        Switch onOff = (Switch)findViewById(R.id.onoff);
        onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {
                if (isOn) {
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
                    setManager(triggerTime);
                    Log.d("call", "start called");
                } else {
                    cancelManager();
                    Env.isStop = true;
                    Log.d("call", "stop called");
                }
            }
        });
    }

    public void cancelManager() {
        Intent wakeupIntent = new Intent(MainActivity.this, BtReciver.class);
        PendingIntent wakeupSender = PendingIntent.getBroadcast(MainActivity.this, 0, wakeupIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent sleepIntent = new Intent(MainActivity.this, BtSleepReciver.class);
        PendingIntent sleepSender = PendingIntent.getBroadcast(MainActivity.this, 0, sleepIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        manager.cancel(wakeupSender);
        manager.cancel(sleepSender);
    }

    public void setManager(Calendar triggerTime) {
        // alermManager設定

        //設定した日時で発行するIntentを生成
        Intent wakeupIntent = new Intent(MainActivity.this, BtReciver.class);
        PendingIntent wakeupSender = PendingIntent.getBroadcast(MainActivity.this, 0, wakeupIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //日時と発行するIntentをAlarmManagerにセットします
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis(), Env.sleepTime + Env.wakeupTime, wakeupSender);


        //設定した日時で発行するIntentを生成
        Intent sleepIntent = new Intent(MainActivity.this, BtSleepReciver.class);
        PendingIntent sleepSender = PendingIntent.getBroadcast(MainActivity.this, 0, sleepIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //日時と発行するIntentをAlarmManagerにセットします
        manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis() + Env.wakeupTime, Env.wakeupTime + Env.sleepTime, sleepSender);


    }
}
