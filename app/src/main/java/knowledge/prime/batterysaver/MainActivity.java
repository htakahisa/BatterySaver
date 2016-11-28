package knowledge.prime.batterysaver;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private long sleepTime = 5000;
    private long wakeupTime = 10000;
    private long idleTime = 1000;

    private WifiManager wifi;

    private boolean isStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifi = (WifiManager)getSystemService(WIFI_SERVICE);

        //done ボタンのイベントセット
        findViewById(R.id.doneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isStop = false;

                //sleeptime
                EditText sleepEditText = (EditText) findViewById(R.id.sleepTimeInput);
                if (sleepEditText.getText() != null) {
                    sleepTime = Long.valueOf(sleepEditText.getText().toString()).longValue();
                }
                //wakeuptime
                EditText wakeupEditText = (EditText) findViewById(R.id.wakeupTimeInput);
                if (wakeupEditText.getText() != null) {
                    wakeupTime = Long.valueOf(wakeupEditText.getText().toString()).longValue();
                }
                //idletime
                EditText idleEditText = (EditText) findViewById(R.id.idleTimeInput);
                if (idleEditText.getText() != null) {
                    idleTime = Long.valueOf(idleEditText.getText().toString()).longValue();
                }
            }
        });

        findViewById(R.id.stopButton).setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     isStop = true;
                 }
             }
        );

        switchWakeupOrSleep();
    }



    private boolean isSleep;

    private void switchWakeupOrSleep() {
        if (isStop) {
            return;
        }

        Log.d("time", "wakeup:" + wakeupTime + ", sleep:" + sleepTime + ", idle:" + idleTime);

        if (isSleep) {
            //go wakeup
            Log.d("d", "go wakeup");
            wakeUp();
            isSleep = false;
        } else {
            //go sleep
            Log.d("d", "go sleep");
            sleep();
            isSleep = true;
        }
    }

    private void wakeUp() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("s", "called wakeUp");
                WifiHandler.wakeup(wifi);
                mobiledataenable(true);
                switchWakeupOrSleep();
            }
        }, sleepTime);//遅延実行のため sleepTime後に wakeup が実行される
    }

    private void sleep() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("s", "called sleep");
                WifiHandler.sleep(wifi);
                mobiledataenable(false);
                switchWakeupOrSleep();

            }
        }, wakeupTime);
    }




    public void mobiledataenable(boolean enabled) {

        try {
            final ConnectivityManager conman = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class<?> conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
