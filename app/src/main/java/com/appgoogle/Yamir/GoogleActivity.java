package com.appgoogle.Yamir;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.appgoogle.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class GoogleActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap myMap;
    private SearchView mapSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);

        mapSearchView = findViewById(R.id.mapSearch);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (myMap != null) {
                    String location = mapSearchView.getQuery().toString();
                    List<Address> addressList = null;
                    if (location != null && !location.isEmpty()) {
                        Geocoder geocoder = new Geocoder(GoogleActivity.this);
                        try {
                            addressList = geocoder.getFromLocationName(location, 1);
                            if (addressList != null && !addressList.isEmpty()) {
                                Address address = addressList.get(0);
                                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                                myMap.addMarker(new MarkerOptions().position(latLng).title(location));
                                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        LatLng sydney = new LatLng(-31, 150);
        myMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
