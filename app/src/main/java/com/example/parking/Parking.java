package com.example.parking;

import com.google.android.gms.maps.model.LatLng;

public class Parking {
    private String name;
    private LatLng location;
    private LatLng[] polygonPoints;

    public Parking(String name, LatLng location, LatLng[] polygonPoints) {
        this.name = name;
        this.location = location;
        this.polygonPoints = polygonPoints;
    }

    public String getName() {
        return name;
    }

    public LatLng getLocation() {
        return location;
    }

    public LatLng[] getPolygonPoints() {
        return polygonPoints;
    }
}
