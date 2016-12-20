package knowledge.prime.batterysaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //設定値をプロパティから取得
        initSetting();
        //レイアウトのオブジェクトの値を更新
        setInitLayoutValue();

        Button reset = (Button)findViewById(R.id.resetButton);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //プロバティの値をクリア
                SharedPreferences data = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = data.edit();
                editor.clear();
                editor.commit();
                //メモリ上を初期値で初期化
                initSetting();
                //レイアウトも更新
                setInitLayoutValue();
            }
        });

        //スクリーンの状態 on/off
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        Env.isScreenOn= pm.isScreenOn();

        //ON/OFF ボタンイベントの設定
        setOnOffButtonEvent();

        //Logcat イベントの設定
        setLogcatEvent();

        //cellId 追加ボタンイベントの設定
        setCellIdEvent();


        //初期化
        WifiHandler.init((WifiManager)getSystemService(WIFI_SERVICE));

        /** ONがチェックされていたら サービス開始 */
        Switch onOff = (Switch)findViewById(R.id.onoff);
        if (onOff.isChecked()) {
            Intent intent = new Intent(getApplication(), MainService.class);
            startService(intent);
        }

    }

    /**
     * CellId ボタンを押した時に既存の CellId をプロパティにセットします。
     */
    private void setCellIdEvent() {

        Button cellIdBtn = (Button)findViewById(R.id.cellIdButton);
        cellIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //既存のデータ取得
                String cellIdStr = PropertyUtils.getProperty(MainActivity.this, "cellIds", "");
                Set<String> cellIdSet = new HashSet<>();
                for(String s : cellIdStr.split(",")){
                    cellIdSet.add(s);
                }

                //現在のCellId取得
                cellIdSet.addAll(CellInfoHandler.getCellId(MainActivity.this));

                //プロパティに登録
                StringBuilder sb = new StringBuilder();
                for (String s : cellIdSet) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(s);
                }
                PropertyUtils.setProperty(MainActivity.this, "cellIds", sb.toString());

                //メモリも更新
                Env.wifiCellIdSet = cellIdSet;

                Log.d("cellId", "cellId:" + sb.toString());
            }
        });

    }





    private void setInitLayoutValue() {
        //初期値のセット
        EditText sleepText = (EditText)findViewById(R.id.sleepTimeInput);
        sleepText.setText(String.valueOf(Env.sleepTime));
        EditText sleepText2 = (EditText)findViewById(R.id.sleepTimeInput2);
        sleepText2.setText(String.valueOf(Env.sleepTime2));
        EditText sleepText3 = (EditText)findViewById(R.id.sleepTimeInput3);
        sleepText3.setText(String.valueOf(Env.sleepTime3));
        EditText sleepText4 = (EditText)findViewById(R.id.sleepTimeInput4);
        sleepText4.setText(String.valueOf(Env.sleepTime4));
        EditText sleepText5 = (EditText)findViewById(R.id.sleepTimeInput5);
        sleepText5.setText(String.valueOf(Env.sleepTime5));

        EditText sCountText = (EditText)findViewById(R.id.count1);
        sCountText.setText(String.valueOf(Env.count));
        EditText sCountText2 = (EditText)findViewById(R.id.count2);
        sCountText2.setText(String.valueOf(Env.count2));
        EditText sCountText3 = (EditText)findViewById(R.id.count3);
        sCountText3.setText(String.valueOf(Env.count3));
        EditText sCountText4 = (EditText)findViewById(R.id.count4);
        sCountText4.setText(String.valueOf(Env.count4));

        EditText wakeupText = (EditText)findViewById(R.id.wakeupTimeInput);
        wakeupText.setText(String.valueOf(Env.wakeupTime));
        EditText idleText = (EditText)findViewById(R.id.idleTimeInput);
        idleText.setText(String.valueOf(Env.idleTime));

        EditText fromH = (EditText)findViewById(R.id.fromH);
        fromH.setText(String.valueOf(Env.fromH));
        EditText toH = (EditText)findViewById(R.id.toH);
        toH.setText(String.valueOf(Env.toH));
    }

    /**
     * 初期化
     */
    private void initSetting() {
        Env.sleepTime   = PropertyUtils.getProperty(MainActivity.this, "sleepTime", 60000);
        Env.sleepTime2  = PropertyUtils.getProperty(MainActivity.this, "sleepTime2", 300000);
        Env.sleepTime3  = PropertyUtils.getProperty(MainActivity.this, "sleepTime3", 600000);
        Env.sleepTime4  = PropertyUtils.getProperty(MainActivity.this, "sleepTime4", 900000);
        Env.sleepTime5  = PropertyUtils.getProperty(MainActivity.this, "sleepTime5", 1800000);

        Env.fromH = PropertyUtils.getProperty(MainActivity.this, "fromH", 0);
        Env.toH = PropertyUtils.getProperty(MainActivity.this, "toH", 7);

        Env.wakeupTime = PropertyUtils.getProperty(MainActivity.this, "wakeupTime", 60000);
        Env.idleTime   = PropertyUtils.getProperty(MainActivity.this, "idleTime", 60000);

        Env.count  = PropertyUtils.getProperty(MainActivity.this, "count", 1);
        Env.count2 = PropertyUtils.getProperty(MainActivity.this, "count2", 3);
        Env.count3 = PropertyUtils.getProperty(MainActivity.this, "count3", 3);
        Env.count4 = PropertyUtils.getProperty(MainActivity.this, "count4", 3);

        String cellIds = PropertyUtils.getProperty(MainActivity.this, "cellIds", "");
        Set<String> cellIdSet = new HashSet<>();
        for (String s : cellIds.split(",")) {
            cellIdSet.add(s);
        }
        Env.wifiCellIdSet = cellIdSet;

        Env.intervalType = 0;
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

                    //sleeptime
                    EditText sleepEditText = (EditText) findViewById(R.id.sleepTimeInput);
                    if (sleepEditText.getText() != null) {
                        Env.sleepTime = Long.valueOf(sleepEditText.getText().toString()).longValue();
                    }
                    EditText sleepEditText2 = (EditText) findViewById(R.id.sleepTimeInput2);
                    if (sleepEditText2.getText() != null) {
                        Env.sleepTime2 = Long.valueOf(sleepEditText2.getText().toString()).longValue();
                    }
                    EditText sleepEditText3 = (EditText) findViewById(R.id.sleepTimeInput3);
                    if (sleepEditText3.getText() != null) {
                        Env.sleepTime3 = Long.valueOf(sleepEditText3.getText().toString()).longValue();
                    }
                    EditText sleepEditText4 = (EditText) findViewById(R.id.sleepTimeInput4);
                    if (sleepEditText4.getText() != null) {
                        Env.sleepTime4 = Long.valueOf(sleepEditText4.getText().toString()).longValue();
                    }
                    EditText sleepEditText5 = (EditText) findViewById(R.id.sleepTimeInput5);
                    if (sleepEditText5.getText() != null) {
                        Env.sleepTime5 = Long.valueOf(sleepEditText5.getText().toString()).longValue();
                    }

                    //count
                    EditText count1 = (EditText) findViewById(R.id.count1);
                    if (count1.getText() != null) {
                        Env.count = Long.valueOf(count1.getText().toString()).longValue();
                    }
                    EditText count2 = (EditText) findViewById(R.id.count2);
                    if (count2.getText() != null) {
                        Env.count2 = Long.valueOf(count2.getText().toString()).longValue();
                    }
                    EditText count3 = (EditText) findViewById(R.id.count3);
                    if (count3.getText() != null) {
                        Env.count3 = Long.valueOf(count3.getText().toString()).longValue();
                    }
                    EditText count4 = (EditText) findViewById(R.id.count4);
                    if (count4.getText() != null) {
                        Env.count4 = Long.valueOf(count4.getText().toString()).longValue();
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

                    // from , to Hour
                    EditText fromHText = (EditText) findViewById(R.id.fromH);
                    if (fromHText.getText() != null) {
                        Env.fromH = Long.valueOf(fromHText.getText().toString()).longValue();
                    }
                    EditText toHText = (EditText) findViewById(R.id.toH);
                    if (toHText.getText() != null) {
                        Env.toH = Long.valueOf(toHText.getText().toString()).longValue();
                    }


                    Log.d("call", "start called");

                    Intent intent = new Intent(getApplication(), MainService.class);
                    startService(intent);
                } else {


                    Log.d("call", "stop called");

                    Intent intent = new Intent(getApplication(), MainService.class);
                    stopService(intent);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("end", "onStop called.");

        //終了前に値の保存
        PropertyUtils.setProperty(MainActivity.this, "sleepTime", Env.sleepTime);
        PropertyUtils.setProperty(MainActivity.this, "sleepTime2", Env.sleepTime2);
        PropertyUtils.setProperty(MainActivity.this, "sleepTime3", Env.sleepTime3);
        PropertyUtils.setProperty(MainActivity.this, "sleepTime4", Env.sleepTime4);
        PropertyUtils.setProperty(MainActivity.this, "sleepTime5", Env.sleepTime5);

        PropertyUtils.setProperty(MainActivity.this, "wakeupTime", Env.wakeupTime);
        PropertyUtils.setProperty(MainActivity.this, "idleTime", Env.idleTime);

        PropertyUtils.setProperty(MainActivity.this, "count", Env.count);
        PropertyUtils.setProperty(MainActivity.this, "count2", Env.count2);
        PropertyUtils.setProperty(MainActivity.this, "count3", Env.count3);
        PropertyUtils.setProperty(MainActivity.this, "count4", Env.count4);

        PropertyUtils.setProperty(MainActivity.this, "fromH", Env.fromH);
        PropertyUtils.setProperty(MainActivity.this, "toH", Env.toH);
        PropertyUtils.setProperty(MainActivity.this, "intervalType", Env.intervalType);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("end", "onDestroy called.");

    }


}
