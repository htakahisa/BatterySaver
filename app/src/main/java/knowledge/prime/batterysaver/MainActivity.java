package knowledge.prime.batterysaver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        SharedPreferences data = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = data.edit();
//        editor.clear();
//        editor.commit();

        Env.sleepTime   = PropertyUtils.getProperty(MainActivity.this, "sleepTime", 60000);
        Env.sleepTime2  = PropertyUtils.getProperty(MainActivity.this, "sleepTime2", 300000);
        Env.sleepTime3  = PropertyUtils.getProperty(MainActivity.this, "sleepTime3", 600000);
        Env.sleepTime4  = PropertyUtils.getProperty(MainActivity.this, "sleepTime4", 900000);

        Env.wakeupTime = PropertyUtils.getProperty(MainActivity.this, "wakeupTime", 60000);
        Env.idleTime   = PropertyUtils.getProperty(MainActivity.this, "idleTime", 60000);

        Env.count  = PropertyUtils.getProperty(MainActivity.this, "count", 1);
        Env.count2 = PropertyUtils.getProperty(MainActivity.this, "count2", 3);
        Env.count3 = PropertyUtils.getProperty(MainActivity.this, "count3", 3);

        Env.intervalType = 1;

        //初期値のセット
        EditText sleepText = (EditText)findViewById(R.id.sleepTimeInput);
        sleepText.setText(String.valueOf(Env.sleepTime));
        EditText sleepText2 = (EditText)findViewById(R.id.sleepTimeInput2);
        sleepText2.setText(String.valueOf(Env.sleepTime2));
        EditText sleepText3 = (EditText)findViewById(R.id.sleepTimeInput3);
        sleepText3.setText(String.valueOf(Env.sleepTime3));
        EditText sleepText4 = (EditText)findViewById(R.id.sleepTimeInput4);
        sleepText4.setText(String.valueOf(Env.sleepTime4));

        EditText sCountText = (EditText)findViewById(R.id.count1);
        sCountText.setText(String.valueOf(Env.count));
        EditText sCountText2 = (EditText)findViewById(R.id.count2);
        sCountText2.setText(String.valueOf(Env.count2));
        EditText sCountText3 = (EditText)findViewById(R.id.count3);
        sCountText3.setText(String.valueOf(Env.count3));


        EditText wakeupText = (EditText)findViewById(R.id.wakeupTimeInput);
        wakeupText.setText(String.valueOf(Env.wakeupTime));
        EditText idleText = (EditText)findViewById(R.id.idleTimeInput);
        idleText.setText(String.valueOf(Env.idleTime));

        //alermmanager setting
        AlarmManagerHandler.setSchedule(MainActivity.this, Calendar.getInstance(), Env.sleepTime, Env.wakeupTime);

        //スクリーンの状態 on/off
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        Env.isScreenOn= pm.isScreenOn();

        //ON/OFF ボタンイベントの設定
        setOnOffButtonEvent();

        //Logcat イベントの設定
        setLogcatEvent();

        //初期化
        WifiHandler.init((WifiManager)getSystemService(WIFI_SERVICE));


        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(new BtReciver(), filter);

        //通知アイコン設定
        this.setNotification();

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
                    AlarmManagerHandler.setSchedule(MainActivity.this, triggerTime, Env.sleepTime, Env.wakeupTime);
                    setNotification();
                    Log.d("call", "start called");
                } else {
                    AlarmManagerHandler.cancelSchedule(MainActivity.this);
                    Env.isStop = true;
                    deleteNotification();
                    Log.d("call", "stop called");
                }
            }
        });
    }


    private void setNotification() {
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0,
                new Intent(this, MainActivity.class), 0);

        Notification notif= new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
//                .setContentText("")
                .setSmallIcon(R.drawable.status_bar_notification)
                .setContentIntent(contentIntent)
                .build();
        //常駐させる
        notif.flags = Notification.FLAG_ONGOING_EVENT;
        nm.notify(1, notif);

    }

    private void deleteNotification() {
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(1);
    }

    @Override
    protected void onStop() {


        //終了前に値の保存
        PropertyUtils.setProperty(MainActivity.this, "sleepTime", Env.sleepTime);
        PropertyUtils.setProperty(MainActivity.this, "sleepTime2", Env.sleepTime2);
        PropertyUtils.setProperty(MainActivity.this, "sleepTime3", Env.sleepTime3);
        PropertyUtils.setProperty(MainActivity.this, "sleepTime4", Env.sleepTime4);

        PropertyUtils.setProperty(MainActivity.this, "wakeupTime", Env.wakeupTime);
        PropertyUtils.setProperty(MainActivity.this, "idleTime", Env.idleTime);

        PropertyUtils.setProperty(MainActivity.this, "count", Env.count);
        PropertyUtils.setProperty(MainActivity.this, "count2", Env.count2);
        PropertyUtils.setProperty(MainActivity.this, "count3", Env.count3);

        Log.d("end", "onStop called.");
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        //終了前に値の保存
        PropertyUtils.setProperty(MainActivity.this, "sleepTime", Env.sleepTime);
        PropertyUtils.setProperty(MainActivity.this, "sleepTime2", Env.sleepTime2);
        PropertyUtils.setProperty(MainActivity.this, "sleepTime3", Env.sleepTime3);
        PropertyUtils.setProperty(MainActivity.this, "sleepTime4", Env.sleepTime4);

        PropertyUtils.setProperty(MainActivity.this, "wakeupTime", Env.wakeupTime);
        PropertyUtils.setProperty(MainActivity.this, "idleTime", Env.idleTime);

        PropertyUtils.setProperty(MainActivity.this, "count", Env.count);
        PropertyUtils.setProperty(MainActivity.this, "count2", Env.count2);
        PropertyUtils.setProperty(MainActivity.this, "count3", Env.count3);

        deleteNotification();

        Log.d("end", "onDestroy called.");
        super.onDestroy();
    }


}
