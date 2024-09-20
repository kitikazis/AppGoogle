package com.appgoogle.Yamir;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appgoogle.Marcelo.GuardadosFragment;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap myMap;
    private SearchView mapSearchView;
    private Marker currentMarker;
    private Button currentLocationButton;
    private LatLng currentLatLng;
    private Polyline currentPolyline;
    private Map<String, List<LatLng>> districtRoutes;
    private String currentDistrict;
    private Spinner districtSpinner;

    private final LatLng[] touristLocations = {
            new LatLng(-12.0464, -77.0428), // Centro de Lima
            new LatLng(-12.1177, -77.0282), // Parque Kennedy
            new LatLng(-12.1708, -77.0272), // Larcomar
            new LatLng(-12.0474, -77.0307)  // Plaza Mayor
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);

        mapSearchView = findViewById(R.id.mapSearch);
        currentLocationButton = findViewById(R.id.currentLocationButton);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        districtSpinner = findViewById(R.id.district_spinner);
        setupDistrictRoutes();
        setupDistrictSpinner();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }




        mapSearchView.setIconifiedByDefault(false);
        mapSearchView.setQueryHint("Buscar lugar...");

        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (myMap != null) {
                    searchLocation(query);
                }
                mapSearchView.setQuery("", false);
                mapSearchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_explore) {
                getSupportFragmentManager().popBackStack(null, getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
                return true;
            } else if (itemId == R.id.navigation_saved) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new GuardadosFragment())
                        .addToBackStack(null)
                        .commit();
                return true;
            } else if (itemId == R.id.navigation_contribute) {
                showCustomExitDialog();
                return true;
            }
            return false;
        });

        currentLocationButton.setOnClickListener(v -> {
            if (myMap != null) {
                getCurrentLocation();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            myMap.setMyLocationEnabled(true);
            getCurrentLocation();
        }

        // Centrar el mapa en Lima, Perú
        LatLng limaLocation = new LatLng(-12.046374, -77.042793); // Coordenadas de Lima
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(limaLocation, 10)); // Ajusta el nivel de zoom

        // Dibujar línea desde la ubicación actual hasta Lima
        myMap.setOnMyLocationChangeListener(location -> {
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            // Elimina la polilínea anterior si existe
            if (currentPolyline != null) {
                currentPolyline.remove();
            }

            // Dibuja la línea hacia Lima
            currentPolyline = myMap.addPolyline(new PolylineOptions()
                    .add(currentLatLng, limaLocation)
                    .width(5)
                    .color(Color.BLUE));
        });

        // Detectar clics en el mapa
        myMap.setOnMapClickListener(latLng -> {
            if (currentLatLng != null) {
                // Elimina el trayecto anterior si existe
                if (currentPolyline != null) {
                    currentPolyline.remove();
                }

                // Dibuja el trayecto
                currentPolyline = myMap.addPolyline(new PolylineOptions()
                        .add(currentLatLng, latLng)
                        .width(5)
                        .color(Color.BLUE));

                // Calcular la ruta y obtener la distancia y el tiempo
                calculateRoute(currentLatLng, latLng);
            }
        });
        if (currentDistrict != null) {
            addDistrictMarkers();
            drawDistrictRoute();
        }

        addTouristMarkers();
    }
    private void addDistrictMarkers() {
        List<LatLng> locations = districtRoutes.get(currentDistrict);
        if (locations != null) {
            for (int i = 0; i < locations.size(); i++) {
                LatLng location = locations.get(i);
                myMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title("Punto " + (i + 1))
                );
            }
            // Center the map on the first location of the route
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locations.get(0), 14));
        }
    }

    private void drawDistrictRoute() {
        List<LatLng> locations = districtRoutes.get(currentDistrict);
        if (locations != null) {
            for (int i = 0; i < locations.size() - 1; i++) {
                calculateRoute(locations.get(i), locations.get(i + 1));
            }
        }
    }
    private void searchLocation(String location) {
        Geocoder geocoder = new Geocoder(GoogleActivity.this);
        try {
            List<Address> addressList = geocoder.getFromLocationName(location, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                if (currentMarker != null) {
                    currentMarker.remove();
                }
                currentMarker = myMap.addMarker(new MarkerOptions().position(latLng).title(location));
                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addTouristMarkers() {
        String[] locationsNames = {"Centro de Lima", "Parque Kennedy", "Larcomar", "Plaza Mayor"};

        for (int i = 0; i < touristLocations.length; i++) {
            LatLng location = touristLocations[i];
            myMap.addMarker(new MarkerOptions().position(location).title(locationsNames[i]));
        }

        // Draw route between tourist locations
        drawTouristRoute();
    }

    private void drawTouristRoute() {
        for (int i = 0; i < touristLocations.length - 1; i++) {
            calculateRoute(touristLocations[i], touristLocations[i + 1]);
        }
    }




    private void calculateRoute(LatLng origin, LatLng destination) {
        String apiKey = "YOUR_ACTUAL_API_KEY"; // Replace with your actual API key
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&key=" + apiKey;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray routes = jsonObject.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            List<LatLng> path = decodePolyline(route.getJSONObject("overview_polyline").getString("points"));
                            myMap.addPolyline(new PolylineOptions()
                                    .addAll(path)
                                    .width(5)
                                    .color(Color.BLUE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error al obtener la ruta", Toast.LENGTH_SHORT).show());

        queue.add(stringRequest);
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result >> 1) ^ -(result & 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result >> 1) ^ -(result & 1));
            lng += dlng;

            LatLng p = new LatLng(((double) lat / 1E5), ((double) lng / 1E5));
            poly.add(p);
        }
        return poly;
    }

    private void showCustomExitDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_exit_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        dialogView.findViewById(R.id.button_exit).setOnClickListener(v -> finish());
        dialogView.findViewById(R.id.button_cancel).setOnClickListener(v -> dialog.dismiss());
    }




    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        if (currentMarker != null) {
                            currentMarker.remove();
                        }
                        currentMarker = myMap.addMarker(new MarkerOptions().position(currentLatLng).title("Ubicación Actual"));
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                    }
                });
    }
















    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    myMap.setMyLocationEnabled(true);
                    getCurrentLocation();
                }
            } else {
                Toast.makeText(this, "Se necesita permiso de ubicación", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupDistrictRoutes() {
        districtRoutes = new HashMap<>();

        // Miraflores route
        districtRoutes.put("Miraflores", Arrays.asList(
                new LatLng(-12.1111, -77.0316), // Parque Kennedy
                new LatLng(-12.1317, -77.0299), // Parque del Amor
                new LatLng(-12.1328, -77.0279), // Larcomar
                new LatLng(-12.1250, -77.0305)  // Huaca Pucllana
        ));

        // Barranco route
        districtRoutes.put("Barranco", Arrays.asList(
                new LatLng(-12.1492, -77.0220), // Puente de los Suspiros
                new LatLng(-12.1503, -77.0223), // Plaza de Barranco
                new LatLng(-12.1478, -77.0217), // MATE Museo
                new LatLng(-12.1470, -77.0185)  // Playa Barranco
        ));

        // Centro de Lima route
        districtRoutes.put("Centro de Lima", Arrays.asList(
                new LatLng(-12.0464, -77.0428), // Plaza Mayor
                new LatLng(-12.0458, -77.0314), // Palacio de Gobierno
                new LatLng(-12.0452, -77.0305), // Catedral de Lima
                new LatLng(-12.0548, -77.0351)  // Convento de San Francisco
        ));

        // Add more districts and their routes as needed
    }

    private void setupDistrictSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>(districtRoutes.keySet()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(adapter);

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentDistrict = (String) parent.getItemAtPosition(position);
                if (myMap != null) {
                    myMap.clear(); // Clear previous markers and routes
                    addDistrictMarkers();
                    drawDistrictRoute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

}
