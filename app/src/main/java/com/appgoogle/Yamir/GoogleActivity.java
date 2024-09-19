package com.appgoogle.Yamir;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.location.Location;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import java.util.List;

public class GoogleActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap myMap;
    private SearchView mapSearchView;
    private Marker currentMarker;
    private Button currentLocationButton;
    private LatLng currentLatLng;  // Para guardar la ubicación actual
    private Polyline currentPolyline; // Para dibujar la línea del trayecto

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);

        // Configuración del SearchView
        mapSearchView = findViewById(R.id.mapSearch);
        currentLocationButton = findViewById(R.id.currentLocationButton);
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
                // Mostrar el diálogo de confirmación personalizado
                showCustomExitDialog();
                return true;
            }

            return false;
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

        // Configuración inicial
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            myMap.setMyLocationEnabled(true);
            getCurrentLocation();
        }

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
    }

    private void calculateRoute(LatLng origin, LatLng destination) {
        String apiKey = "AIzaSyB6PIhBBBdd6c7vItL6ANljT3TavnDIG74"; // Reemplaza con tu API Key
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&key=" + apiKey;

        // Usar Volley para hacer la solicitud HTTP
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray routes = jsonObject.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject legs = route.getJSONArray("legs").getJSONObject(0);
                            String distance = legs.getJSONObject("distance").getString("text");
                            String duration = legs.getJSONObject("duration").getString("text");

                            // Mostrar la distancia y el tiempo estimado
                            Toast.makeText(this, "Distancia: " + distance + ", Tiempo: " + duration, Toast.LENGTH_LONG).show();

                            // Eliminar la polilínea anterior si existe
                            if (currentPolyline != null) {
                                currentPolyline.remove();
                            }

                            // Extraer las coordenadas de la ruta y dibujarla
                            List<LatLng> path = decodePolyline(route.getJSONObject("overview_polyline").getString("points"));
                            currentPolyline = myMap.addPolyline(new PolylineOptions()
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

    // Método para mostrar el diálogo de confirmación personalizado
    private void showCustomExitDialog() {
        // Inflar el diseño del diálogo personalizado
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_exit_dialog, null);

        // Crear el AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Configurar los botones del diálogo
        Button cancelButton = dialogView.findViewById(R.id.button_cancel);
        Button exitButton = dialogView.findViewById(R.id.button_exit);

        cancelButton.setOnClickListener(v -> alertDialog.dismiss()); // Cierra el diálogo
        exitButton.setOnClickListener(v -> finish()); // Cierra la aplicación

        // Mostrar el diálogo
        alertDialog.show();
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
                            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                            // Centra la cámara en la ubicación actual
                            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));  // Ajusta el nivel de zoom

                            // No añadir ningún marcador manual
                            if (currentMarker != null) {
                                currentMarker.remove(); // Elimina cualquier marcador anterior si existía
                            }

                            // Añadir un marcador en la ubicación actual
                            currentMarker = myMap.addMarker(new MarkerOptions()
                                    .position(currentLatLng)
                                    .title("Ubicación actual"));
                        }
                    });
        }
    }
}
