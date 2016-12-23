package knowledge.prime.batterysaver;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.List;

/**
 * Created by takahisa007 on 12/22/16.
 */
public class NetworkInfoHandler {


    public static boolean isConnectMobileNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo nInfo = cm.getActiveNetworkInfo();

        if (nInfo == null) {
            return false;
        }
        return nInfo.isConnected();
    }

    public static Boolean isRestrictedArea(Context context) {

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfoList = tm.getAllCellInfo();

        if (cellInfoList.size() == 0) {
            return null;
        }
        boolean hasCellInfoLte = false;
        for (CellInfo cellInfo : cellInfoList) {
            if (cellInfo instanceof CellInfoLte) {
                hasCellInfoLte = true;
                int cellId = ((CellInfoLte) cellInfo).getCellIdentity().getCi();
                Log.d("cellId", "cell id is " + cellId);
                Log.d("cellId", "designation cell id is " + Env.wifiCellIdSet);
                if (Env.wifiCellIdSet.contains(String.valueOf(cellId))) {
                    return true;
                }
            }
        }
        //LTEに接続前ならnull
        if (!hasCellInfoLte) {
            return null;
        }

        return false;
    }
}
