package com.appgoogle.Yamir;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.appgoogle.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.IOException;
import java.util.List;

public class GoogleActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap myMap;
    private SearchView mapSearchView;
    private Marker currentMarker;
    private Button currentLocationButton; // Añadido

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);

        // Configuración del SearchView
        mapSearchView = findViewById(R.id.mapSearch);
        currentLocationButton = findViewById(R.id.currentLocationButton); // Inicializado
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Configurar el fragmento del mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mapSearchView.setIconifiedByDefault(false);
        mapSearchView.setQueryHint("Buscar lugar...");

        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Realiza la búsqueda de la ubicación
                if (myMap != null) {
                    String location = mapSearchView.getQuery().toString();
                    List<Address> addressList = null;
                    if (location != null && !location.isEmpty()) {
                        Geocoder geocoder = new Geocoder(GoogleActivity.this);
                        try {
                            // Buscar la ubicación usando el Geocoder
                            addressList = geocoder.getFromLocationName(location, 1);
                            if (addressList != null && !addressList.isEmpty()) {
                                Address address = addressList.get(0);
                                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                                // Elimina el marcador anterior si existe
                                if (currentMarker != null) {
                                    currentMarker.remove();
                                }

                                // Añade el nuevo marcador en la ubicación buscada
                                currentMarker = myMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(location));

                                // Mueve la cámara del mapa a la ubicación buscada
                                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // Limpiar el campo de búsqueda después de enviar la consulta
                mapSearchView.setQuery("", false);
                mapSearchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Configurar el botón para centrar en la ubicación actual
        currentLocationButton.setOnClickListener(v -> {
            if (myMap != null) {
                getCurrentLocation();
            }
        });
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        // Verificar permisos de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Habilitar la capa de ubicación en tiempo real (bolita azul)
            myMap.setMyLocationEnabled(true);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    myMap.setMyLocationEnabled(true);
                }
            }
        }
    }
    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            // Obtiene la latitud y longitud actuales
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                            // Centra la cámara en la ubicación actual
                            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));  // Ajusta el nivel de zoom

                            // No añadir ningún marcador manual
                            if (currentMarker != null) {
                                currentMarker.remove(); // Elimina cualquier marcador anterior si existía
                            }
                        }
                    });
        }
    }


}