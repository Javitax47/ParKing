package com.example.parking;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Map;

public class Tab1 extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private GoogleMap mapa;
    private SearchView searchView;
    private ListView suggestionListView;
    private Map<String, Parking> parkingMap;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab1, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        this.rootView = view;
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapa);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        FloatingActionButton fab = view.findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mapa.setMyLocationEnabled(true);
                    LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    String provider = locationManager.getBestProvider(criteria, false);
                    Location location = locationManager.getLastKnownLocation(provider);

                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));
                    }
                }
            }
        });

        Map<String, Parking> parkingMap = createParkingMap();

        configureSearchView(parkingMap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapa.getUiSettings().setZoomControlsEnabled(false);
        mapa.getUiSettings().setCompassEnabled(false);

        int fillColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary);

        for (Parking parking : parkingMap.values()) {
            PolygonOptions polygonOptions = new PolygonOptions()
                    .add(parking.getPolygonPoints())
                    .strokeColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                    .fillColor(Color.argb(128, Color.red(fillColor), Color.green(fillColor), Color.blue(fillColor)));
            googleMap.addPolygon(polygonOptions);

            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_round);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 65, 65, false);
            googleMap.addMarker(new MarkerOptions()
                    .position(parking.getLocation())
                    .title(parking.getName())
                    .snippet("Este es un parking placeholder hasta que se tome la información de FireBase.")
                    .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))
                    .anchor(0.5f, 0.5f));
        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Find the Parking object associated with the clicked marker
                Parking clickedParking = parkingMap.get(marker.getTitle());

                if (clickedParking != null) {
                    // Show parking information in bottom sheet
                    ParkingInfoBottomSheetFragment bottomSheetFragment =
                            ParkingInfoBottomSheetFragment.newInstance(clickedParking.getName(), marker.getSnippet());
                    bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());

                    return true;
                }
                return false;
            }
        });
    }
    private Map<String, Parking> createParkingMap() {

        Map<String, Parking> parkingMap = new HashMap<>();

        // Add information for ParKing1
        Parking parking1 = new Parking("ParKing1", new LatLng(38.9956483, -0.1662051),
                new LatLng[]{
                        new LatLng(38.9951483, -0.1652051),
                        new LatLng(38.9951483, -0.1672051),
                        new LatLng(38.9961483, -0.1672051),
                        new LatLng(38.9961483, -0.1652051)
                });
        parkingMap.put(parking1.getName(), parking1);

        // Add information for ParKing2
        Parking parking2 = new Parking("ParKing2", new LatLng(39.0, -0.17),
                new LatLng[]{
                        new LatLng(39.0, -0.169),
                        new LatLng(39.0, -0.171),
                        new LatLng(39.001, -0.171),
                        new LatLng(39.001, -0.169)
                });
        parkingMap.put(parking2.getName(), parking2);

        // Add information for ParKing3
        Parking parking3 = new Parking("ParKing3", new LatLng(39.005, -0.16),
                new LatLng[]{
                        new LatLng(39.005, -0.159),
                        new LatLng(39.005, -0.161),
                        new LatLng(39.006, -0.161),
                        new LatLng(39.006, -0.159)
                });
        parkingMap.put(parking3.getName(), parking3);

        return parkingMap;
    }
    private void configureSearchView(Map<String, Parking> parkingMap) {
        this.parkingMap = parkingMap;
        searchView = rootView.findViewById(R.id.buscador);
        suggestionListView = rootView.findViewById(R.id.suggestionListView);

        final String[] parkingNames = parkingMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, parkingNames);
        suggestionListView.setAdapter(adapter);

        suggestionListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedParkingName = ((TextView) view).getText().toString();
            Parking selectedParking = parkingMap.get(selectedParkingName);

            if (selectedParking != null) {
                mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedParking.getLocation(), 18));

                searchView.onActionViewCollapsed();
                suggestionListView.setVisibility(View.GONE);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Parking selectedParking = parkingMap.get(query);

                if (selectedParking != null) {
                    mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedParking.getLocation(), 18));

                    searchView.onActionViewCollapsed();
                    suggestionListView.setVisibility(View.GONE);

                    ParkingInfoBottomSheetFragment bottomSheetFragment =
                            ParkingInfoBottomSheetFragment.newInstance(selectedParking.getName(), "Additional info");
                    bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());

                    return true;
                } else {
                    // Handle the case when the entered query does not match any parking
                    Toast.makeText(requireContext(), "Parking no encontrado", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Show/hide suggestion list based on query text
                if (newText.isEmpty()) {
                    suggestionListView.setVisibility(View.GONE);
                } else {
                    suggestionListView.setVisibility(View.VISIBLE);
                    adapter.getFilter().filter(newText);
                }
                return true;
            }
        });

    }
    @Override public void onMapClick(LatLng puntoPulsado) {
        //mapa.addMarker(new MarkerOptions().position(puntoPulsado).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
    }
}