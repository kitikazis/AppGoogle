package com.appgoogle.Yamir;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.appgoogle.Marcelo.GuardadosFragment;
import com.appgoogle.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GoogleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_explore) {
                getSupportFragmentManager().popBackStack(null, getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
                return true;
            } else if (itemId == R.id.navigation_saved) {
                selectedFragment = new GuardadosFragment();
            } else if (itemId == R.id.navigation_contribute) {
                // Mostrar el diálogo de confirmación para salir de la aplicación
                showExitConfirmationDialog();
                return true;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
            return false;
        });
    }

    // Método para mostrar el diálogo de confirmación de salida
    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Salir de la aplicación")
                .setMessage("¿Estás seguro de que deseas salir?")
                .setPositiveButton("Sí", (dialog, which) -> finish()) // Cierra la aplicación
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // Cierra el diálogo y continúa la ejecución
                .show();
    }
}
