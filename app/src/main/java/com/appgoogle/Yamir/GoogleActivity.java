package com.appgoogle.Yamir;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SearchView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.appgoogle.Marcelo.GuardadosFragment;
import com.appgoogle.R;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GoogleActivity extends AppCompatActivity {

    private SearchView mapSearch;
    private View mapView;
    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);

        mapSearch = findViewById(R.id.mapSearch);
        mapView = findViewById(R.id.map);
        fragmentContainer = findViewById(R.id.fragment_container);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_explore) {
                showMapView();
                return true;
            } else if (itemId == R.id.navigation_saved) {
                showGuardadosView();
                return true;
            } else if (itemId == R.id.navigation_contribute) {
                showCustomExitDialog();
                return true;
            }

            return false;
        });

        // Mostrar la vista del mapa por defecto
        showMapView();
    }

    // Método para mostrar el diálogo de confirmación personalizado
    private void showCustomExitDialog() {
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


    private void showMapView() {
        mapView.setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.GONE);
        mapSearch.setVisibility(View.VISIBLE);
    }

    private void showGuardadosView() {
        mapView.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
        mapSearch.setVisibility(View.GONE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new GuardadosFragment())
                .commit();
    }
}
