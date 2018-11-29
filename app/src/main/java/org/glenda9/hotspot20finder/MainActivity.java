package org.glenda9.hotspot20finder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOGNAME="hotspot20finder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void doScan(View view) {
        String ssid_filter;
        List<ScanResult> apList = scanAP();
        List<Hotspot20Info> hs20Infos;

        if (apList == null) {
            clearCountAndList();
            return;
        }

        /* get text input: to filter SSID */
        EditText et = findViewById(R.id.ssid_filter);
        ssid_filter = et.getText().toString();
        Log.i(LOGNAME, "ssidfilter => " + ssid_filter);

        if (apList.isEmpty()) {
            showToast("No AP found");
            clearCountAndList();
            return;
        }

        /* parse and convert List<ScanResult> to List<Hotspot20Info> */
        hs20Infos = parseScanResultsToHotspot20Infos(apList, ssid_filter);
        Log.i(LOGNAME, "filtered Hotspot2.0 AP COunt => " + String.valueOf(hs20Infos.size()));

        if (hs20Infos.isEmpty()) {
            showToast("No Hotspot 2.0 AP found in Total " + String.valueOf(apList.size()) + " APs");
            clearCountAndList();
            return;
        }

        /* update ap count */
        TextView tv = findViewById(R.id.ap_count);
        tv.setText(String.valueOf(hs20Infos.size()) + " ");

        ListView lv = findViewById(R.id.scan_listview);
        Hotspot20InfoAdapter adapter = new Hotspot20InfoAdapter(this, R.layout.scanlist_item, hs20Infos);
        lv.setAdapter(adapter);

    }

    private List<ScanResult> scanAP() {
        Log.i(LOGNAME, "start scanning");

        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1001);
        }

        WifiManager manager = (WifiManager)getSystemService(WIFI_SERVICE);
        if (manager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            showToast("Wi-Fi is not enabled");
            return null;
        }

        manager.startScan();
        return manager.getScanResults();
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    private void clearCountAndList() {
        List<Hotspot20Info> emptyDisplayList = new ArrayList<>();

        /* update ap count */
        TextView tv = findViewById(R.id.ap_count);
        tv.setText(String.valueOf(0) + " ");

        /* update list */
        ListView lv = findViewById(R.id.scan_listview);
        Hotspot20InfoAdapter adapter = new Hotspot20InfoAdapter(this, R.layout.scanlist_item, emptyDisplayList);
        lv.setAdapter(adapter);

    }

    private List<Hotspot20Info> parseScanResultsToHotspot20Infos(List<ScanResult> scanResults, String ssid_filter) {
        List<Hotspot20Info> hs20Infos = new ArrayList<>();

        for (int i = 0; i < scanResults.size(); i++) {
            ScanResult scanResult = scanResults.get(i);
            Hotspot20Info hs20info = new Hotspot20Info(scanResult);
            Boolean isHotspot20 = false;

            if (ssid_filter != null && !ssid_filter.equals("") && !scanResult.SSID.contains(ssid_filter)) {
                continue;
            }

            try {
                isHotspot20 = hs20info.checkHotspot20();
            } catch (NoSuchFieldException e) {
                Log.i(LOGNAME, "NoSuchFieldException, failed to parse IE");

            } catch (IllegalAccessException e) {
                Log.i(LOGNAME, "IllegalAccessException, failed to parse IE");
            }

            if (!isHotspot20) {
                continue;
            }

            hs20Infos.add(hs20info);
        }

        return hs20Infos;
    }


}
