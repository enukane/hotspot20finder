package org.glenda9.hotspot20finder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class Hotspot20InfoAdapter extends ArrayAdapter<Hotspot20Info> {
    private List<Hotspot20Info> items;
    private LayoutInflater inflater;
    private int resource;

    public Hotspot20InfoAdapter(Context context, int resource, List<Hotspot20Info> items) {
        super(context, resource, items);

        this.resource = resource;
        this.items = items;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        TextView tv;

        if (convertView != null) {
            view = convertView;
        } else {
            view = inflater.inflate(resource, null);
        }

        Hotspot20Info hs20info = items.get(position);

        setTextView(view, R.id.ap_bssid, hs20info.bssid);
        setTextView(view, R.id.ap_ssid, hs20info.ssid);
        setTextView(view, R.id.internetworking_ie_access_net, convertAccessNetworkType(hs20info.accessNetworkType));
        setTextView(view, R.id.internetworking_ie_internet, convertInternetConnectivity(hs20info.internetConnectivity));
        setTextView(view, R.id.internetworking_ie_venue_group, convertVenueGroup(hs20info.venue_group));
        setTextView(view, R.id.internetworking_ie_venue_type, convertVenueType(hs20info.venue_group, hs20info.venue_type));
        setTextView(view, R.id.internetworking_ie_hessid, hs20info.hessid);
        setTextView(view, R.id.hotspot20_ie_active, hs20info.isHotspot20IE ? "Yes" : "No");

        return view;
    }

    private void setTextView(View view, int id, String text) {
        TextView tv = (TextView)view.findViewById(id);
        tv.setText(text);
    }

    private String convertAccessNetworkType(int networkType) {
        switch(networkType) {
            case 0:
                return "Private network";
            case 1:
                return "Private network with Guest access";
            case 2:
                return "Chargeable public network";
            case 3:
                return "Free public network";
            case 4:
                return "Personal device network";
            case 5:
                return "Emergency services only network";
            case 14:
                return "Test or experimental";
            case 15:
                return "Wildcard";
            default:
                return "Unknown network";
        }
    }

    private String convertInternetConnectivity(int internetConnectivity) {
        switch(internetConnectivity) {
            case 1:
                return "Yes";
            default:
                return "No";
        }
    }

    private String convertVenueGroup(int venueGroup) {
        switch(venueGroup) {
            case 1:
                return "Assembly";
            case 2:
                return "Business";
            case 3:
                return "Educational";
            case 4:
                return "Factory and Industrial";
            case 5:
                return "Institutional";
            case 6:
                return "Mercantile";
            case 7:
                return "Residential";
            case 8:
                return "Storage";
            case 9:
                return "Utility and Miscellaneous";
            case 10:
                return "Vehicular";
            case 11:
                return "Outdoor";
            default: /* Reserved */
                return "Unknown";
        }
    }

    private String convertVenueType(int venueGroup, int venueType) {
        switch(venueGroup) {
            case 0: /* Unspecified */
                return "Unspecified";
            case 1: /* Assembly */
                switch(venueType) {
                    case 0: return "Unspecified Assembly";
                    case 1: return "Arena";
                    case 2: return "Stadium";
                    case 3: return "Passenger Terminal";
                    case 4: return "Amphitheater";
                    case 5: return "Amusement Park";
                    case 6: return "Place of Worship";
                    case 7: return "Convention Center";
                    case 8: return "Library";
                    case 9: return "Museum";
                    case 10: return "Restaurant";
                    case 11: return "Theater";
                    case 12: return "Bar";
                    case 13: return "Coffee Shop";
                    case 14: return "Zoo or Aquarium";
                    case 15: return "Emergency Coordination Center";
                    default: return "Unknown";
                }
            case 2: /* Business */
                switch (venueType) {
                    case 0: return "Unspecified Business";
                    case 1: return "Doctor or Dentist office";
                    case 2: return "Bank";
                    case 3: return "Fire Station";
                    case 4: return "Police Station";
                    case 6: return "Post Office";
                    case 7: return "Professional Office";
                    case 8: return "Research and Development Facility";
                    case 9: return "Attomey Office";
                    default: return "Unknown";
                }
            case 3: /* Educational */
                switch (venueType) {
                    case 0: return "Unspecified";
                    case 1: return "Primary School";
                    case 2: return "Secondary School";
                    case 3: return "University or College";
                    default: return "Unknown";
                }
            case 4: /* Factory and Industrial */
                switch (venueType) {
                    case 0: return "Unspecified";
                    case 1: return "Factory";
                    default: return "Unknown";
                }
            case 5: /* Institutional */
                switch (venueType) {
                    case 0: return "Unspecified";
                    case 1: return "Hospital";
                    case 2: return "Long-Term Care Facility";
                    case 3: return "Alcohol and Drug Rehabilitation Center";
                    case 4: return "Group Home";
                    case 5: return "Prison or Jail";
                    default: return "Unknown";
                }
            case 6: /* Mercantile */
                switch (venueType) {
                    case 0: return "Unspecified";
                    case 1: return "Retail Store";
                    case 2: return "Grocery Market";
                    case 3: return "Automotive Service Station";
                    case 4: return "Shopping Mall";
                    case 5: return "Gas Station";
                    default: return "Unknown";
                }
            case 7: /* Residential */
                switch (venueType) {
                    case 0: return "Unspecified";
                    case 1: return "Private Residence";
                    case 2: return "Hotel or Motel";
                    case 3: return "Dormitory";
                    case 4: return "Boarding House";
                    default: return "Unknown";
                }
            case 8: /* Storage */
                switch (venueType) {
                    case 0: return "Unspecified";
                    default: return "Unknown";
                }
            case 9: /* Utility and miscelaneous */
                switch (venueType) {
                    case 0: return "Unspecified";
                    default: return "Unknown";
                }
            case 10: /* Vehicular */
                switch (venueType) {
                    case 0: return "Unspecified";
                    case 1: return "Automobile or Truck";
                    case 2: return "Airplane";
                    case 3: return "Bus";
                    case 4: return "Ferry";
                    case 5: return "Shop or Boat";
                    case 6: return "Train";
                    case 7: return "Motor Bike";
                    default: return "Unknown";
                }
            case 11: /* Outdoor */
                switch (venueType) {
                    case 0: return "Unspecified";
                    case 1: return "Muni-mesh Network";
                    case 2: return "City Park";
                    case 3: return "Rest Area";
                    case 4: return "Traffic Control";
                    case 5: return "Bus Stop";
                    case 6: return "Kiosk";
                    default: return "Unknown";
                }
            default: /* Reserved */
                return "Unknown";
        }
    }
}
