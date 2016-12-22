package knowledge.prime.batterysaver;

import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoLte;
import android.telephony.TelephonyManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by takahisa007 on 12/20/16.
 */
public class CellInfoHandler {

    /**
     * 現在の cellId を取得します。
     * @param context
     * @return
     */
    public static Set<String> getCellId(Context context) {
        Set<String> cellIdSet = new HashSet<>();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfoList = tm.getAllCellInfo();
        for (CellInfo cellInfo : cellInfoList) {

            if (cellInfo instanceof CellInfoLte) {
                int cellId = ((CellInfoLte) cellInfo).getCellIdentity().getCi();
                cellIdSet.add(String.valueOf(cellId));
                continue;
            }
            if (cellInfo instanceof CellInfoCdma) {
                int cellId = ((CellInfoCdma) cellInfo).getCellIdentity().getBasestationId();
                cellIdSet.add(String.valueOf(cellId));
                continue;
            }
//            if (cellInfo instanceof CellInfoWcdma) {
//                int cellId = ((CellInfoWcdma) cellInfo).getCellIdentity().getCid();
//                cellIdSet.add(String.valueOf(cellId));
//                continue;
//            }
        }
        return cellIdSet;
    }
}
