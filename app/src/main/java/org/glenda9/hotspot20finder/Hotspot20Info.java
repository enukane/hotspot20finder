package org.glenda9.hotspot20finder;

import android.net.wifi.ScanResult;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.Arrays;

public class Hotspot20Info {
    public static final String LOGNAME="hotspot20finder-hs20info";
    public static final String SR_MEMBER_IES="informationElements";
    public static final String SR_IE_MEMBER_ID="id";
    public static final String SR_IE_MEMBER_BYTES="bytes";
    public static final int IE_ID_INTERNETWORKING=107;
    public static final int IE_ID_VENDORSPEC=221;
    public static final int IE_LEN_HOTSPOT20=5;
    public static final int IE_LEN_HOTSPOT20_WITH_OPT=7;
    public static final String OUI_WIFI_ALL = "50-6f-9a";
    public static final int TYPE_VENDOR_SPECIFIC_OUI_HS20 = 16;

    private ScanResult sr;

    public String bssid = "00:00:00:00:00:00";
    public String ssid = "SSID";

    public Boolean isInternetworkingIE = false;
    public Boolean isHotspot20 = false;
    public int accessNetworkType = 0;
    public int internetConnectivity = 0;
    public int asra = 0;
    public int venue_group = 0;
    public int venue_type = 0;
    public String hessid = "00:00:00:00:00:00";

    public Boolean isHotspot20IE = false;
    public int hotspot20ReleaseNumber = 0;
    public Boolean isDGAFDisabled = false;
    public Boolean isPPSMOIDPresent = false;
    public Boolean isANQPDomainIDPresent = false;
    public int ppsMOID = 0;
    public int anqpDomainID = 0;

    public Hotspot20Info(ScanResult sr) {
        this.sr = sr;
    }

    public Boolean checkHotspot20() throws NoSuchFieldException, IllegalAccessException {
        Field field;
        Object[] ieArray;

        this.ssid = sr.SSID;
        this.bssid = sr.BSSID;

        Log.i(LOGNAME, "checking Hotspot for SSID:" + this.ssid + ", BSSID:" + this.bssid);

        field = sr.getClass().getDeclaredField(SR_MEMBER_IES);
        field.setAccessible(true);
        ieArray = (Object[])field.get(this.sr);

        for (int i = 0; i < ieArray.length; i++) {
            Object obj = ieArray[i];
            int id;
            byte[] bytes;

            /* acquire IE id */
            field = obj.getClass().getDeclaredField(SR_IE_MEMBER_ID);
            field.setAccessible(true);
            id = (int)field.get(obj);

            /* acquire IE bytes */
            field = obj.getClass().getDeclaredField(SR_IE_MEMBER_BYTES);
            bytes = (byte[])field.get(obj);

            switch (id) {
                case IE_ID_INTERNETWORKING:
                    parseInternetworkingIE(bytes);
                    break;
                case IE_ID_VENDORSPEC:
                    parseHotspot20IndicationIE(bytes);
                    break;
                default:
                    /* just ignore */
                    break;
            }

        }

        /* check only Internetworking IE; Hotspot 2.0 Indication is optional */
        this.isHotspot20 = this.isInternetworkingIE;
        return this.isInternetworkingIE;
    }

    private void parseInternetworkingIE(byte[] bytes) {
        int idx = 0;
        int access_network_opt_idx = -1;
        int venue_info_group_idx = -1;
        int venue_info_type_idx = -1;
        int hessid_idx = -1;

        Log.i(LOGNAME, "found Internetworking IE");

        switch(bytes.length) {
            case 1: /* without venue / hessid */
                access_network_opt_idx = 0;
                break;
            case 3: /* with venue */
                access_network_opt_idx = idx++;
                venue_info_group_idx = idx++;
                venue_info_type_idx = idx++;
                break;
            case 7: /* with hessid */
                access_network_opt_idx = idx++;
                hessid_idx = idx++;
                break;
            case 9: /* with venue + hessid */
                access_network_opt_idx = idx++;
                venue_info_group_idx = idx++;
                venue_info_type_idx = idx++;
                hessid_idx = idx++;
                break;
            default:
                Log.i(LOGNAME, "Internetworking IE length is wrong: " + String.valueOf(bytes.length));
                return;
        }

        this.isInternetworkingIE = true;

        if (access_network_opt_idx >= 0) {
            this.accessNetworkType = (bytes[access_network_opt_idx] & 0x0f);
            this.internetConnectivity = (bytes[access_network_opt_idx] & 0x10) >> 4;
            this.asra = (bytes[access_network_opt_idx] & 0x20) >> 5;
        }

        if (venue_info_group_idx >= 0) {
            this.venue_group = (int)bytes[venue_info_group_idx];
        }

        if (venue_info_type_idx >= 0) {
            this.venue_type = (int)bytes[venue_info_type_idx];
        }

        if (hessid_idx >= 0) {
            byte[] hessid_bytes = Arrays.copyOfRange(bytes, hessid_idx, hessid_idx + 6);
            this.hessid = bytesToMACAddress(hessid_bytes);
        }

        return;

    }

    private void parseHotspot20IndicationIE(byte[] bytes) {
        int oui0_idx = 0;
        int oui1_idx = 1;
        int oui2_idx = 2;
        int type_idx = 3;
        int hsconf_idx = 4;
        int opt_idx = -1;
        String oui;

        switch(bytes.length) {
            case IE_LEN_HOTSPOT20:
                opt_idx = -1;
                break;
            case IE_LEN_HOTSPOT20_WITH_OPT:
                opt_idx = hsconf_idx + 1;
                break;
            default:
                /* wrong length */
                return;
        }

        oui = String.format("%02x", bytes[oui0_idx]) + "-" + String.format("%02x", bytes[oui1_idx]) + "-" + String.format("%02x", bytes[oui2_idx]);

        if (!OUI_WIFI_ALL.equalsIgnoreCase(oui)) {
            //Log.i(LOGNAME, "OUI for Vendor specific IE is not for Wi-Fi Alliance");
            return;
        }

        if ((int)bytes[type_idx] != TYPE_VENDOR_SPECIFIC_OUI_HS20) {
            Log.i(LOGNAME, "Vendor specific OUI Type is not Hotspot2.0");
            return;
        }

        Log.i(LOGNAME, "found Hotspot 2.0 Indication IE (length=" + String.valueOf(bytes.length) + ")");

        this.isDGAFDisabled = (bytes[hsconf_idx] & 0x1) != 0;
        this.isPPSMOIDPresent = (bytes[hsconf_idx] & 0x2) != 0;
        this.isANQPDomainIDPresent = (bytes[hsconf_idx] & 0x4) != 0;
        this.hotspot20ReleaseNumber = ((int)bytes[hsconf_idx] & 0xf0) >> 4;

        Log.i(LOGNAME, "Hotspot2.0 release number = " + String.valueOf(hotspot20ReleaseNumber));

        if (opt_idx >= 0) {
            int opt_val = bytes[opt_idx] + (bytes[opt_idx+1] << 8);
            if (this.isPPSMOIDPresent) {
                this.ppsMOID = opt_val;
            } else if (this.isANQPDomainIDPresent) {
                this.anqpDomainID = opt_val;
            }
        }

        this.isHotspot20IE = true;

        return;
    }

    private String bytesToMACAddress(byte[] bytes) {
        if (bytes.length != 6) {
            return "00:00:00:00:00:00";
        }

        return String.format("%02x", bytes[0]) + ":" +
                String.format("%02x", bytes[1]) + ":" +
                String.format("%02x", bytes[2]) + ":" +
                String.format("%02x", bytes[3]) + ":" +
                String.format("%02x", bytes[4]) + ":" +
                String.format("%02x", bytes[5]);
    }

}
