package com.diploma.likharev.android.mymap;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Lirik on 27.04.2017.
 */

public class Route {

    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}
